from rejson import Client, Path
import json
import time
from datetime import date, datetime, timedelta
from timeloop import Timeloop
import os
import requests

# start timeloop
tl = Timeloop()

BACKEND_SERVICE_URL = "http://localhost:8080" if os.getenv(
    'BACKEND_SERVICE_URL') == None else os.getenv('BACKEND_SERVICE_URL')
TWEET_SERVICE_URL = "http://localhost:8081" if os.getenv(
    'TWEET_SERVICE_URL') == None else os.getenv('TWEET_SERVICE_URL')
REDIS_HOST = "localhost" if os.getenv(
    'REDIS_HOST') == None else os.getenv('REDIS_HOST')

dir_path = os.path.dirname(os.path.realpath(__file__))
UPLOAD_FOLDER = dir_path+"/filestore"

# connect to queue
rj = Client(host=REDIS_HOST, port=6379, decode_responses=True)
tweetRequest = {'tweetRequest': []}
profileRequest = {'profileRequest': []}


def checkifObjectExist():
    if rj.jsonget('user', Path.rootPath()) == None:
        rj.jsonset('user', Path.rootPath(), profileRequest)

    if rj.jsonget('tweet', Path.rootPath()) == None:
        rj.jsonset('tweet', Path.rootPath(), tweetRequest)


def profileAction(requestPath, method, headers, filename, userform):
    files = []
    if len(filename) > 0 and os.path.exists(UPLOAD_FOLDER+'/'+filename) and os.path.isfile(UPLOAD_FOLDER+'/'+filename):
        file = ('profilePic', open(UPLOAD_FOLDER+'/'+filename, 'rb'))
        files.append(file)
        os.remove(UPLOAD_FOLDER+'/'+filename)
    else:
        files.append(('profilePic', ''))

    payload = {'userForm': userform}
    resp = requests.request(
        method=method,
        url=BACKEND_SERVICE_URL+requestPath,
        headers=headers,
        data=payload,
        files=files,
        allow_redirects=False
    )
    return resp


def tweetAction(requestPath, method, headers, filename, tweetForm):
    files = []

    if method == 'DELETE':
        resp = requests.request(
            method=method,
            url=TWEET_SERVICE_URL+requestPath,
            headers=headers,
            allow_redirects=False
        )
        return resp

    if filename and len(filename) > 0 and os.path.exists(UPLOAD_FOLDER+'/'+filename) and os.path.isfile(UPLOAD_FOLDER+'/'+filename):
        file = ('file', open(UPLOAD_FOLDER+'/'+filename, 'rb'))
        files.append(file)
        os.remove(UPLOAD_FOLDER+'/'+filename)
    else:
        files.append(('file', ''))

    payload = {'tweetForm': tweetForm}
    resp = requests.request(
        method=method,
        url=TWEET_SERVICE_URL+requestPath,
        headers=headers,
        data=payload,
        files=files,
        allow_redirects=False
    )
    return resp


def commentAction(requestPath, method, headers, body):
    resp = requests.request(
        method=method,
        url=TWEET_SERVICE_URL+requestPath,
        headers=headers,
        json=body,
        allow_redirects=False
    )
    return resp

# every 100 seconds, print groups


@tl.job(interval=timedelta(seconds=20))
def profileService():
    # get contents
    checkifObjectExist()
    for obj in rj.jsonget('user', Path('.profileRequest')):
        print(obj)
        rj.jsonarrpop('user', Path('.profileRequest'))
        res = profileAction(obj['requestPath'], obj['method'],
                            obj['headers'], obj['file'], obj['form'])
        print(res.text.encode('utf8'))


@tl.job(interval=timedelta(seconds=40))
def tweetService():
    # get contents
    checkifObjectExist()
    for obj in rj.jsonget('tweet', Path('.tweetRequest')):
        print(obj)
        rj.jsonarrpop('tweet', Path('.tweetRequest'))
        if(obj['service'] == 'comment'):
            res = commentAction(obj['requestPath'],
                                obj['method'], obj['headers'], obj['body'])
        else:
            form = None if 'form' not in obj else obj['form']
            file = None if 'file' not in obj else obj['file']
            res = tweetAction(obj['requestPath'],
                              obj['method'], obj['headers'], file, form)
        print(res.text.encode('utf8'))


if __name__ == "__main__":
    tl.start(block=True)
