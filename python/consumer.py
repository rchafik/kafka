from confluent_kafka import Consumer, KafkaException
import logging
import os

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

def create_consumer():
    conf = load_properties("streaming-python-consumer.properties")
    consumer = Consumer(conf)
    return consumer

def consume_messages(consumer, topic):
    consumer.subscribe([topic])

    try:
        while True:
            msg = consumer.poll(1.0)
            print("Listening topic "+topic)
            if msg is None:
                continue
            if msg.error():
                if msg.error().code() == KafkaException._PARTITION_EOF:
                    continue
                else:
                    print(msg.error())
                    break

            print('Received message: {}'.format(msg.value().decode('utf-8')))
    except KeyboardInterrupt:
        pass
    finally:
        consumer.close()

if __name__ == '__main__':

    logging.basicConfig(level=logging.DEBUG)
    
    topic = 'aTeamTopic'

    print ("Starting...")
    consumer = create_consumer()

    try:
        consume_messages(consumer, topic)
    except Exception as e:
        print('Error: {}'.format(str(e)))
    finally:
        consumer.close()