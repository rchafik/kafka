FROM python:3.12-slim

WORKDIR /app

# Set locale environment variables
ENV LANG=pt_BR.UTF-8 \
    LANGUAGE=pt_BR:pt \
    LC_ALL=pt_BR.UTF-8

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY consumer.py .
COPY streaming-python-consumer.properties .
ENV PYTHONUNBUFFERED=1

EXPOSE 8000

CMD ["python", "consumer.py"]