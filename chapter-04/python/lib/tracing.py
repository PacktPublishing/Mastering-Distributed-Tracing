import logging
import opentracing
from jaeger_client import Config


def init_tracer(service):
    logging.getLogger('').handlers = []
    logging.basicConfig(format='%(message)s', level=logging.DEBUG)

    config = Config(
        config={
            'sampler': {
                'type': 'const',
                'param': 1,
            },
            'logging': True,
            'reporter_batch_size': 1,
        },
        service_name=service,
    )

    # this call sets global variable opentracing.tracer
    config.initialize_tracer()


def flask_to_scope(flask_tracer, request):
    return opentracing.tracer.scope_manager.activate(
        flask_tracer.get_span(request),
        False,
    )
