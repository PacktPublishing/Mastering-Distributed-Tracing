from flask import Flask
from flask import request
from lib.tracing import init_tracer, flask_to_scope
import opentracing
from opentracing.ext import tags
from flask_opentracing import FlaskTracer


app = Flask('py-6-formatter')
init_tracer('py-6-formatter')
flask_tracer = FlaskTracer(opentracing.tracer, True, app)


@app.route("/formatGreeting")
def handle_format_greeting():
    with flask_to_scope(flask_tracer, request) as scope:
        name = request.args.get('name')
        title = request.args.get('title')
        descr = request.args.get('description')
        return format_greeting(
            name=name,
            title=title,
            description=descr,
        )


def format_greeting(name, title, description):
    with opentracing.tracer.start_active_span(
        'format-greeting',
    ) as scope:
        greeting = scope.span.get_baggage_item('greeting') or 'Hello'
        greeting += ', '
        if title:
            greeting += title + ' '
        greeting += name + '!'
        if description:
            greeting += ' ' + description
        return greeting


if __name__ == "__main__":
    app.run(port=8082)
