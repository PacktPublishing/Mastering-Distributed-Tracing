from flask import Flask
import json
from .database import Person
from lib.tracing import init_tracer
import opentracing


app = Flask('py-4-bigbrother')
init_tracer('py-4-bigbrother')


@app.route("/getPerson/<name>")
def get_person_http(name):
    with opentracing.tracer.start_active_span('/getPerson') as scope:
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
