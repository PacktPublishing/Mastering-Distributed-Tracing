package main

import (
	"context"
	"log"
	"net/http"

	opentracing "github.com/opentracing/opentracing-go"

	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/lib/tracing"
)

func main() {
	tracer, closer := tracing.Init("go-4-formatter")
	defer closer.Close()
	opentracing.SetGlobalTracer(tracer)

	http.HandleFunc("/formatGreeting", handleFormatGreeting)

	log.Print("Listening on http://localhost:8082/")
	log.Fatal(http.ListenAndServe(":8082", nil))
}

func handleFormatGreeting(w http.ResponseWriter, r *http.Request) {
	span := opentracing.GlobalTracer().StartSpan("/formatGreeting")
	defer span.Finish()
	ctx := opentracing.ContextWithSpan(r.Context(), span)

	name := r.FormValue("name")
	title := r.FormValue("title")
	descr := r.FormValue("description")

	greeting := FormatGreeting(ctx, name, title, descr)
	w.Write([]byte(greeting))
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
