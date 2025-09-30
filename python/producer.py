import time
from confluent_kafka import Producer  
import json
import os
import logging
import sys

def load_properties(file_path):
    props = {}
    with open(file_path, "r") as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith("#"):
                key, value = line.split("=", 1)
                key = key.strip()
                value = value.strip()

                # Converte valores numéricos e booleanos
                if value.lower() in ["true", "false"]:
                    value = value.lower() == "true"
                else:
                    try:
                        value = int(value)
                    except ValueError:
                        pass

                props[key] = value
    return props

def start_kafka_producer(environment):  
    if environment == "kafka":
        conf = load_properties("kafka-python-producer.properties")
    else:
        conf = load_properties("streaming-python-producer.properties")

    # Create Producer instance  
    producer = Producer(**conf)  
    return producer

def send_to_kafka(producer, topic, message):
    myKey='1000'
    producer.produce(topic, key=myKey, value=message)  

ambiente = sys.argv[1]

if ambiente == "kafka":
    print("Utilizando Kafka gerenciado")
elif ambiente == "streaming":
    print("Utilizando Streaming")
else:
    print("Opção inválida. Utilize 'kafka' ou 'streaming'.")
    sys.exit(1)

message_template_small = {
    "EventName": "MessageTest",
    "MsgSeqNum": 0
}

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

logging.info('Producing...')
producer = start_kafka_producer(ambiente)
topic = 'ateam-topic'

number_of_messages = 100
start_time = time.time() 
for count in range(number_of_messages):
    message_template_small['MsgSeqNum'] = count+1
    json_data = json.dumps(message_template_small)
    send_to_kafka(producer, topic, json_data)

producer.flush()
elapsed_time = time.time() - start_time

logging.info(f"Total elapsed time: {elapsed_time:.2f} seconds for "+str(count+1)+" messages!")