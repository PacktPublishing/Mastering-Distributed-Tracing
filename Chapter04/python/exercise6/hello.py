import sys
import json
import requests
from flask import Flask
from flask import request
from lib.tracing import init_tracer, flask_to_scope
import opentracing
from opentracing.ext import tags
from opentracing_instrumentation.client_hooks import install_all_patches
from flask_opentracing import FlaskTracer


app = Flask('py-6-hello')
init_tracer('py-6-hello')
install_all_patches()
flask_tracer = FlaskTracer(opentracing.tracer, True, app)


@app.route("/sayHello/<name>")
def say_hello(name):
    with flask_to_scope(flask_tracer, request) as scope:
        person = get_person(name)
        resp = format_greeting(person)
        opentracing.tracer.active_span.set_tag('response', resp)
        return resp


def get_person(name):
    with opentracing.tracer.start_active_span(
        'get-person',
    ) as scope:
        url = 'http://localhost:8081/getPerson/%s' % name
        res = _get(url)
        person = json.loads(res)
        scope.span.log_kv({
            'name': person['name'],
            'title': person['title'],
            'description': person['description'],
        })
        return person


def format_greeting(person):
    with opentracing.tracer.start_active_span(
        'format-greeting',
    ):
        url = 'http://localhost:8082/formatGreeting'
        return _get(url, params=person)


def _get(url, params=None):
    r = requests.get(url, params=params)
    assert r.status_code == 200
    return r.text


if __name__ == "__main__":
    app.run(port=8080)
