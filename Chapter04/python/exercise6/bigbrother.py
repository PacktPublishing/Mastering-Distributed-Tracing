from flask import Flask
from flask import request
import json
from .database import Person
from lib.tracing import init_tracer, flask_to_scope
import opentracing
from opentracing.ext import tags
from opentracing_instrumentation.client_hooks import install_all_patches
from flask_opentracing import FlaskTracer


app = Flask('py-6-bigbrother')
init_tracer('py-6-bigbrother')
install_all_patches()
flask_tracer = FlaskTracer(opentracing.tracer, True, app)


@app.route("/getPerson/<name>")
def get_person_http(name):
    with flask_to_scope(flask_tracer, request) as scope:
        person = Person.get(name)
        if person is None:
            person = Person()
            person.name = name
        opentracing.tracer.active_span.log_kv({
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
