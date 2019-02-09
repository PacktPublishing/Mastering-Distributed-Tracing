import json
import requests
from flask import Flask
from flask import request
from lib.tracing import init_tracer
import opentracing
from opentracing.ext import tags


app = Flask('py-5-hello')
init_tracer('py-5-hello')


@app.route("/sayHello/<name>")
def say_hello(name):
    span_ctx = opentracing.tracer.extract(
        opentracing.Format.HTTP_HEADERS,
        request.headers,
    )
    with opentracing.tracer.start_active_span(
        'say-hello',
        child_of=span_ctx,
        tags={tags.SPAN_KIND: tags.SPAN_KIND_RPC_SERVER},
    ) as scope:
        person = get_person(name)
        resp = format_greeting(person)
        scope.span.set_tag('response', resp)
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
    span = opentracing.tracer.active_span
    span.set_tag(tags.HTTP_URL, url)
    span.set_tag(tags.HTTP_METHOD, 'GET')
    span.set_tag(tags.SPAN_KIND, tags.SPAN_KIND_RPC_CLIENT)
    headers = {}
    opentracing.tracer.inject(
        span.context, 
        opentracing.Format.HTTP_HEADERS,
        headers,
    )
    r = requests.get(url, params=params, headers=headers)
    assert r.status_code == 200
    return r.text


if __name__ == "__main__":
    app.run(port=8080)
