from flask import Flask
import config

app = Flask(__name__)

app.debug = config.DEBUG
