from flask import Flask
from .database import Person
from lib.tracing import init_tracer
import opentracing


app = Flask('py-3-hello')
init_tracer('py-3-hello')


@app.route("/sayHello/<name>")
def say_hello(name):
    with opentracing.tracer.start_span('say-hello') as span:
        person = get_person(name, span)
        resp = format_greeting(
            name=person.name,
            title=person.title,
            description=person.description,
            span=span,
        )
        span.set_tag('response', resp)
        return resp


def get_person(name, span):
    with opentracing.tracer.start_span(
        'get-person', child_of=span,
    ) as span:
        person = Person.get(name)
        if person is None:
            person = Person()
            person.name = name
        span.log_kv({
            'name': person.name,
            'title': person.title,
            'description': person.description,
        })
        return person


def format_greeting(name, title, description, span):
    with opentracing.tracer.start_span(
        'format-greeting', child_of=span,
    ):
        greeting = 'Hello, '
        if title:
            greeting += title + ' '
        greeting += name + '!'
        if description:
            greeting += ' ' + description
        return greeting


if __name__ == "__main__":
    app.run(port=8080)
