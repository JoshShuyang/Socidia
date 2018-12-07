import logging
import os

DEBUG = False
APPLICATION_ROOT = os.getenv('APPLICATION_APPLICATION_ROOT', '/application')
HOST = '0.0.0.0'
PORT = int(os.getenv('APPLICATION_PORT', '3000'))


logging.basicConfig(
    filename=os.getenv('SERVICE_LOG', 'server.log'),
    level=logging.DEBUG,
    format='%(levelname)s: %(asctime)s \
        pid:%(process)s module:%(module)s %(message)s',
    datefmt='%d/%m/%y %H:%M:%S',
)
