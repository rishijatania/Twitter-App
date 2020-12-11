FROM python:3.8.0
COPY ./middleware ./app 
WORKDIR /app 
RUN pip install -r requirements.txt
# RUN python -m spacy download en
EXPOSE 5000
RUN chmod +x run.sh
CMD ["./run.sh"]