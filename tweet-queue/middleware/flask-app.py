from flask import Flask, request, jsonify, Response
from flask_cors import CORS, cross_origin
import requests
import os
import json
from rejson import Client, Path
from datetime import datetime
import ErrorResponse
from werkzeug.utils import secure_filename
from werkzeug.datastructures import FileStorage
from slack_webhook import Slack
import jwt
from prometheus_flask_exporter import PrometheusMetrics
# from profanity_check import predict, predict_prob
# import spacy
# from profanity_filter import ProfanityFilter


app = Flask(__name__)
app.config['JSONIFY_PRETTYPRINT_REGULAR'] = False
app.config['CORS_HEADERS'] = 'Content-Type'
CORS(app)
PrometheusMetrics(app)
# nlp = spacy.load('en')
# pf = ProfanityFilter(nlps={'en': nlp})

BACKEND_SERVICE_URL = "http://localhost:8080" if os.getenv(
    'BACKEND_SERVICE_URL') == None else os.getenv('BACKEND_SERVICE_URL')
TWEET_SERVICE_URL = "http://localhost:8081" if os.getenv(
    'TWEET_SERVICE_URL') == None else os.getenv('TWEET_SERVICE_URL')
REDIS_HOST = "localhost" if os.getenv(
    'REDIS_HOST') == None else os.getenv('REDIS_HOST')
PROFANITY_IDX = 0.5 if os.getenv(
    'PROFANITY_IDX') == None else os.getenv('PROFANITY_IDX')
SLACK_WEBHOOK_URI = "" if os.getenv(
    'SLACK_WEBHOOK_URI') == None else os.getenv('SLACK_WEBHOOK_URI')
RAPID_API_URL = "" if os.getenv(
    'RAPID_API_URL') == None else os.getenv('RAPID_API_URL')
RAPID_API_HOST = "" if os.getenv(
    'RAPID_API_HOST') == None else os.getenv('RAPID_API_HOST')
RAPID_API_KEY = "" if os.getenv(
    'RAPID_API_KEY') == None else os.getenv('RAPID_API_KEY')

slack = Slack(url=SLACK_WEBHOOK_URI)

dir_path = os.path.dirname(os.path.realpath(__file__))
UPLOAD_FOLDER = dir_path+"/filestore"
if not os.path.isdir(UPLOAD_FOLDER):
    os.mkdir(UPLOAD_FOLDER)

app.config['MAX_CONTENT_LENGTH'] = 2 * 1024 * 1024 if os.getenv(
    'MAX_CONTENT_LENGTH') == None else os.getenv('MAX_CONTENT_LENGTH')  # 2MB upload
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

rj = Client(host=REDIS_HOST, port=6379, decode_responses=True)

autherrresponse = {
    "statusCode": "401",
    "message": "Unauthorized. Please login and try again"
}

servererrresponse = {
    "statusCode": "500",
    "message": "Server Error. Please try again later"
}

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}
AUTH_REQUIRED_ENPOINTS = {'profile'}


def _proxy_user_service(*args, **kwargs):

    resp = requests.request(
        method=request.method,
        url=request.url.replace(request.host_url, BACKEND_SERVICE_URL+"/"),
        headers={key: value for (key, value)
                 in request.headers if key != 'Host'},
        data=request.get_data(),
        cookies=request.cookies,
        allow_redirects=False)

    excluded_headers = ['content-encoding',
                        'content-length', 'transfer-encoding', 'connection']
    headers = [(name, value) for (name, value) in resp.raw.headers.items()
               if name.lower() not in excluded_headers]

    return Response(resp, resp.status_code, headers)


def _proxy_tweet_service(*args, **kwargs):

    resp = requests.request(
        method=request.method,
        url=request.url.replace(request.host_url, TWEET_SERVICE_URL+"/"),
        headers={key: value for (key, value)
                 in request.headers if key != 'Host'},
        data=request.get_data(),
        cookies=request.cookies,
        allow_redirects=False)

    excluded_headers = ['content-encoding',
                        'content-length', 'transfer-encoding', 'connection']
    headers = [(name, value) for (name, value) in resp.raw.headers.items()
               if name.lower() not in excluded_headers]

    return Response(resp, resp.status_code, headers)


def allowed_file(filename):
    return '.' in filename and \
        filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


@app.route('/api/testHealth')
@cross_origin()
def healthcheck():
    return jsonify(
        statusCode="200",
        message="Python Services are up and running."
    )


@app.route('/api/user/profile', methods=['PUT'])
@cross_origin()
def profile():

    if 'profilePic' in request.files and not allowed_file(request.files['profilePic'].filename):
        respo = ErrorResponse.ErrorResponseMessage(
            'File not of type Image!', str(datetime.now()), 400, "", "/profile")
        return json.dumps(respo.__dict__), 400

    if('userForm' not in request.form):
        respo = ErrorResponse.ErrorResponseMessage(
            'Invalid Request!', str(datetime.now()), 400, "", "/profile")
        return json.dumps(respo.__dict__), 400

    filename = None

    if 'profilePic' in request.files:
        file = request.files['profilePic']

        filename = secure_filename(str(datetime.now())+"-"+file.filename)
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
        print("File Saved")

    apiObj = {
        'requestPath': request.path,
        'headers': {'Authorization': request.headers['Authorization']},
        'method': request.method,
        'form': request.form['userForm'],
        'file': filename if filename != None else ""
    }

    print(apiObj)
    rj.jsonarrappend('user', Path('.profileRequest'), apiObj)
    return jsonify(
        statusCode="200",
        message="Request Accepted"
    )


@app.route("/api/user/followers/<id>", methods=["DELETE"])
@app.route("/api/user/profile", methods=['GET'])
@app.route("/api/user/search", methods=['GET'])
@app.route("/api/user/followers", methods=['POST'])
@app.route("/api/user/followSuggestions", methods=['GET'])
@cross_origin()
def user_service(id=None):
    return _proxy_user_service()


@app.route("/api/tweet/feed", methods=['GET'])
@app.route("/api/tweet/testHealth", methods=['GET'])
@app.route("/api/tweet/search", methods=['GET'])
@app.route("/api/tweet/searchTag", methods=['GET'])
@app.route("/api/tweet/<id>", methods=['GET'])
@app.route("/api/tweet", methods=['GET'])
@app.route("/api/tweet/feed", methods=['GET'])
@app.route("/api/tweet/<id>/like", methods=['POST'])
@cross_origin()
def tweet_service(id=None):
    return _proxy_tweet_service()


def check_profanity(text):
    response = requests.request("GET", RAPID_API_URL, headers={
        'x-rapidapi-host': RAPID_API_HOST,
        'x-rapidapi-key': RAPID_API_KEY
    }, params={'text': text}
    )
    return response.text == 'true'


@app.route('/api/tweet/<id>/comment', methods=['POST'])
@app.route('/api/tweet/<id>/comment/<cid>', methods=['DELETE'])
@cross_origin()
def comment(id=None, cid=None):

    if request.method == 'POST' and not request.json and 'text' not in request.json and len(request.json['text']) == 0:
        respo = ErrorResponse.ErrorResponseMessage(
            'Comment cannot be empty', str(datetime.now()), 400, "", "/comment")
        return json.dumps(respo.__dict__), 400

    # print(predict_prob([request.json['text']]))
    # print(pf.censor(request.json['text']))
    # and predict_prob([request.json['text']]) > PROFANITY_IDX #and pf.is_profanerequest.json['text']:
    if request.method == 'POST' and check_profanity(request.json['text']):
        respo = ErrorResponse.ErrorResponseMessage(
            'Offensive Comments are Not Allowed!', str(datetime.now()), 400, "", "/comments")

        username = jwt.decode(
            request.headers['Authorization'].split(' ')[1], verify=False)
        slack.post(attachments=[
            {
                "fallback": "Plain-text summary of the attachment.",
                "color": "#FF0000",
                "pretext": "Offensive Comment - Error",
                "author_name": "Username - "+username['sub'],
                "title": "Comment Service",
            }
        ])
        return json.dumps(respo.__dict__), 400

    apiObj = {
        'requestPath': request.path,
        'headers': {'Authorization': request.headers['Authorization']},
        'method': request.method,
        'body': request.json,
        'service': 'comment'
    }

    print(apiObj)
    rj.jsonarrappend('tweet', Path('.tweetRequest'), apiObj)
    return jsonify(
        statusCode="200",
        message="Request Accepted"
    )


@app.route('/api/tweet', methods=['POST'])
@app.route('/api/tweet/<id>', methods=['PUT', 'DELETE'])
@cross_origin()
def tweet(id=None, cid=None):

    if request.method == 'POST' and 'file' in request.files and not allowed_file(request.files['file'].filename):
        respo = ErrorResponse.ErrorResponseMessage(
            'File not of type Image!', str(datetime.now()), 400, "", "/tweet")
        return json.dumps(respo.__dict__), 400

    if (request.method == 'POST' or request.method == 'PUT') and ('tweetForm' not in request.form or len(request.form['tweetForm']) == 0):
        respo = ErrorResponse.ErrorResponseMessage(
            'Invalid Request!', str(datetime.now()), 400, "", "/tweet")
        return json.dumps(respo.__dict__), 400

    if (request.method == 'POST' or request.method == 'PUT') and check_profanity(request.form['tweetForm']):
        respo = ErrorResponse.ErrorResponseMessage(
            'Offensive Tweets are Not Allowed!', str(datetime.now()), 400, "", "/tweet")

        username = jwt.decode(
            request.headers['Authorization'].split(' ')[1], verify=False)
        slack.post(attachments=[
            {
                "fallback": "Plain-text summary of the attachment.",
                "color": "#FF0000",
                "pretext": "Offensive Tweet - Error",
                "author_name": "Username - "+username['sub'],
                "title": "Tweet Service",
            }
        ])
        return json.dumps(respo.__dict__), 400

    filename = None

    if request.method == 'POST' and 'file' in request.files:
        file = request.files['file']

        filename = secure_filename(str(datetime.now())+"-"+file.filename)
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
        print("File Saved")

    if request.method == 'POST' or request.method == 'PUT':
        apiObj = {
            'requestPath': request.path,
            'headers': {'Authorization': request.headers['Authorization']},
            'method': request.method,
            'form': request.form['tweetForm'],
            'file': filename if filename != None else "",
            'service': 'tweet'
        }
    else:
        apiObj = {
            'requestPath': request.path,
            'headers': {'Authorization': request.headers['Authorization']},
            'method': request.method,
            'service': 'tweet'
        }

    print(apiObj)
    rj.jsonarrappend('tweet', Path('.tweetRequest'), apiObj)
    return jsonify(
        statusCode="200",
        message="Request Accepted"
    )


@app.errorhandler(413)
def request_entity_too_large(error):
    return jsonify(
        statusCode="413",
        error="Request Entity Too Large",
        message="The data value transmitted exceeds the capacity limit."
    ), 413


@app.before_request
def before_request():
    print(request.endpoint)
    if request.endpoint in AUTH_REQUIRED_ENPOINTS:
        try:
            print(BACKEND_SERVICE_URL)
            response = requests.get(BACKEND_SERVICE_URL+"/api/user/verify",
                                    headers={"Authorization": request.headers['Authorization']})
        except requests.exceptions.RequestException as err:
            print("Exception")
            print(err)
            return jsonify(servererrresponse), 500

        if not (response and response.ok):
            return jsonify(autherrresponse), 401


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
