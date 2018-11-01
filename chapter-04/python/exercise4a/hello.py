import json
import requests
import opentracing
from flask import Flask
from lib.tracing import init_tracer


app = Flask('py-4-hello')
init_tracer('py-4-hello')


@app.route("/sayHello/<name>")
def say_hello(name):
    with opentracing.tracer.start_active_span('say-hello') as scope:
        person = get_person(name)
        resp = format_greeting(person)
        scope.span.set_tag('response', resp)
        return resp


def get_person(name):
    with opentracing.tracer.start_active_span(
        'get-person',
    ) as scope:
        url = 'http://localhost:8081/getPerson/%s' % name
        r = requests.get(url)
        assert r.status_code == 200
        person = json.loads(r.text)
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
        r = requests.get(url, params=person)
        assert r.status_code == 200
        return r.text


if __name__ == "__main__":
    app.run(port=8080)
