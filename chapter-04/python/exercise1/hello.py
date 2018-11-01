from flask import Flask
from .database import Person


app = Flask('py-1-hello')


@app.route("/sayHello/<name>")
def say_hello(name):
    person = get_person(name)
    resp = format_greeting(
        name=person.name,
        title=person.title,
        description=person.description,
    )
    return resp


def get_person(name):
    person = Person.get(name)
    if person is None:
        person = Person()
        person.name = name
    return person


def format_greeting(name, title, description):
    greeting = 'Hello, '
    if title:
        greeting += title + ' '
    greeting += name + '!'
    if description:
        greeting += ' ' + description
    return greeting


if __name__ == "__main__":
    app.run(port=8080)
