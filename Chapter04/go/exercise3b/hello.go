package main

import (
	"context"
	"log"
	"net/http"
	"strings"

	opentracing "github.com/opentracing/opentracing-go"
	otlog "github.com/opentracing/opentracing-go/log"

	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/exercise3b/people"
	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/lib/tracing"
)

var repo *people.Repository

func main() {
	tracer, closer := tracing.Init("go-3-hello")
	defer closer.Close()
	opentracing.SetGlobalTracer(tracer)

	repo = people.NewRepository()
	defer repo.Close()

	http.HandleFunc("/sayHello/", handleSayHello)

	log.Print("Listening on http://localhost:8080/")
	log.Fatal(http.ListenAndServe(":8080", nil))
}

func handleSayHello(w http.ResponseWriter, r *http.Request) {
	span := opentracing.GlobalTracer().StartSpan("say-hello")
	defer span.Finish()
	ctx := opentracing.ContextWithSpan(r.Context(), span)

	name := strings.TrimPrefix(r.URL.Path, "/sayHello/")
	greeting, err := SayHello(ctx, name)
	if err != nil {
		span.SetTag("error", true)
		span.LogFields(otlog.Error(err))
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	span.SetTag("response", greeting)
	w.Write([]byte(greeting))
}

// SayHello creates a greeting for the named person.
func SayHello(ctx context.Context, name string) (string, error) {
	person, err := repo.GetPerson(ctx, name)
	if err != nil {
		return "", err
	}

	opentracing.SpanFromContext(ctx).LogKV(
		"name", person.Name,
		"title", person.Title,
		"description", person.Description,
	)

	return FormatGreeting(
		ctx,
		person.Name,
		person.Title,
		person.Description,
	), nil
}

// FormatGreeting combines information about a person into a greeting string.
func FormatGreeting(
	ctx context.Context,
	name, title, description string,
) string {
	span, ctx := opentracing.StartSpanFromContext(
		ctx,
		"format-greeting",
	)
	defer span.Finish()

	response := "Hello, "
	if title != "" {
		response += title + " "
	}
	response += name + "!"
	if description != "" {
		response += " " + description
	}
	return response
}
