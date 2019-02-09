package main

import (
	"context"
	"net/http"

	opentracing "github.com/opentracing/opentracing-go"

	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/exercise6/othttp"
	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/lib/tracing"
)

func main() {
	tracer, closer := tracing.Init("go-6-formatter")
	defer closer.Close()
	opentracing.SetGlobalTracer(tracer)

	http.HandleFunc("/formatGreeting", handleFormatGreeting)
	othttp.ListenAndServe(":8082", "/formatGreeting")
}

func handleFormatGreeting(w http.ResponseWriter, r *http.Request) {
	name := r.FormValue("name")
	title := r.FormValue("title")
	descr := r.FormValue("description")

	greeting := FormatGreeting(r.Context(), name, title, descr)
	w.Write([]byte(greeting))
}

// FormatGreeting combines information about a person into a greeting string.
func FormatGreeting(
	ctx context.Context,
	name, title, description string,
) string {
	span := opentracing.SpanFromContext(ctx)

	greeting := span.BaggageItem("greeting")
	if greeting == "" {
		greeting = "Hello"
	}
	response := greeting + ", "
	if title != "" {
		response += title + " "
	}
	response += name + "!"
	if description != "" {
		response += " " + description
	}
	return response
}
