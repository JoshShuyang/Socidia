from scd import app
from scd import config
from flask import render_template


def runserver():
    app.run(debug=True, threaded=True, host=config.HOST, port=config.PORT)


# routing for basic pages (pass routing onto the Angular app)
@app.route('/')
def main_page():
    return render_template("index.html")


if __name__ == '__main__':
    runserver()
