from flask import Flask
from flask import request
from lib.tracing import init_tracer
import opentracing
from opentracing.ext import tags


app = Flask('py-5-formatter')
init_tracer('py-5-formatter')


@app.route("/formatGreeting")
def handle_format_greeting():
    span_ctx = opentracing.tracer.extract(
        opentracing.Format.HTTP_HEADERS,
        request.headers,
    )
    with opentracing.tracer.start_active_span(
        '/formatGreeting',
        child_of=span_ctx,
        tags={tags.SPAN_KIND: tags.SPAN_KIND_RPC_SERVER},
    ) as scope:
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
