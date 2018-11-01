from flask import Flask
from flask import request
from lib.tracing import init_tracer
import opentracing


app = Flask('py-4-formatter')
init_tracer('py-4-formatter')


@app.route("/formatGreeting")
def handle_format_greeting():
    with opentracing.tracer.start_active_span('/formatGreeting') as scope:
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
    ):
        greeting = 'Hello, '
        if title:
            greeting += title + ' '
        greeting += name + '!'
        if description:
            greeting += ' ' + description
        return greeting


if __name__ == "__main__":
    app.run(port=8082)
