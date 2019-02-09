from flask import Flask
from .database import Person
from lib.tracing import init_tracer
import opentracing


app = Flask('py-3-hello')
init_tracer('py-3-hello')


@app.route("/sayHello/<name>")
def say_hello(name):
    with opentracing.tracer.start_active_span('say-hello') as scope:
        person = get_person(name)
        resp = format_greeting(
            name=person.name,
            title=person.title,
            description=person.description,
        )
        scope.span.set_tag('response', resp)
        return resp


def get_person(name):
    with opentracing.tracer.start_active_span(
        'get-person',
    ) as scope:
        person = Person.get(name)
        if person is None:
            person = Person()
            person.name = name
        scope.span.log_kv({
            'name': person.name,
            'title': person.title,
            'description': person.description,
        })
        return person


def format_greeting(name, title, description):
    with opentracing.tracer.start_active_span(
        'format-greeting',
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
