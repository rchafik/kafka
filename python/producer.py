import time
from confluent_kafka import Producer  
import json
import os
import logging

def start_kafka_producer():

  username=os.environ['SASL_USERNAME']
  password=os.environ['SASL_PASSWORD']
  endpoint_stream="cell-1.streaming.sa-saopaulo-1.oci.oraclecloud.com:9092"
  
         
  conf = {
    'bootstrap.servers': os.environ.get('BOOTSTRAP_SERVERS', endpoint_stream),
    'security.protocol': os.environ.get('SECURITY_PROTOCOL', 'SASL_SSL'),
    'sasl.mechanism': os.environ.get('SASL_MECHANISM', 'PLAIN'),
    'linger.ms': int(os.environ.get('LINGER_MS', 10)),
    'batch.size': int(os.environ.get('BATCH_SIZE', (15 * (1024 * 1024) ))),
    'acks':-1,
    "queue.buffering.max.messages": 10000000, 
    'compression.type': "lz4",
    'max.in.flight.requests.per.connection': 1,
    'sasl.username': username,
    'sasl.password': password
  }
  

  # Create Producer instance  
  producer = Producer(**conf)  
  return producer

def send_to_kafka(producer, topic, message):
    myKey='1000'
    producer.produce(topic, key=myKey, value=message)  


message_template_small = {
    "EventName": "EquitiesExecutionReport",
    "MsgSeqNum": 0
}

gg_template = {"table":"MYAPP1.T1","op_type":"I","op_ts":"2024-08-13 15:54:17.020636","current_ts":"2024-08-13 15:54:19.407000","pos":"00000000160000003101","after":{"A":6,"B":"Teste 6"}}


logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

logging.info('Producing...')
producer = start_kafka_producer()
topic = 'aTeamTopic'

number_of_messages = 100
start_time = time.time() 
for count in range(number_of_messages):
    message_template_small['MsgSeqNum'] = count+1
    json_data = json.dumps(gg_template)
    send_to_kafka(producer, topic, json_data)

producer.flush()
elapsed_time = time.time() - start_time

logging.info(f"Total elapsed time: {elapsed_time:.2f} seconds for "+str(count+1)+" messages!")