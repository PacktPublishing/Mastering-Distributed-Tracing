from flask import Flask
from flask import request
import json
from .database import Person
from lib.tracing import init_tracer
import opentracing
from opentracing.ext import tags


app = Flask('py-4-bigbrother')
init_tracer('py-4-bigbrother')


@app.route("/getPerson/<name>")
def get_person_http(name):
    span_ctx = opentracing.tracer.extract(
        opentracing.Format.HTTP_HEADERS,
        request.headers,
    )
    with opentracing.tracer.start_active_span(
        '/getPerson',
        child_of=span_ctx,
        tags={tags.SPAN_KIND: tags.SPAN_KIND_RPC_SERVER},
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
        return json.dumps({
            'name': person.name,
            'title': person.title,
            'description': person.description,
        })


if __name__ == "__main__":
    app.run(port=8081)
