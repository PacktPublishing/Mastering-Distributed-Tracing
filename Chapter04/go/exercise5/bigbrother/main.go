package main

import (
	"encoding/json"
	"log"
	"net/http"
	"strings"

	opentracing "github.com/opentracing/opentracing-go"
	otlog "github.com/opentracing/opentracing-go/log"

	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/exercise5/people"
	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/lib/tracing"
)

var repo *people.Repository

func main() {
	tracer, closer := tracing.Init("go-5-bigbrother")
	defer closer.Close()
	opentracing.SetGlobalTracer(tracer)

	repo = people.NewRepository()
	defer repo.Close()

	http.HandleFunc("/getPerson/", handleGetPerson)

	log.Print("Listening on http://localhost:8081/")
	log.Fatal(http.ListenAndServe(":8081", nil))
}

func handleGetPerson(w http.ResponseWriter, r *http.Request) {
	spanCtx, _ := opentracing.GlobalTracer().Extract(
		opentracing.HTTPHeaders,
		opentracing.HTTPHeadersCarrier(r.Header),
	)
	span := opentracing.GlobalTracer().StartSpan(
		"/getPerson",
		opentracing.ChildOf(spanCtx),
	)
	defer span.Finish()

	ctx := opentracing.ContextWithSpan(r.Context(), span)

	name := strings.TrimPrefix(r.URL.Path, "/getPerson/")
	person, err := repo.GetPerson(ctx, name)
	if err != nil {
		span.SetTag("error", true)
		span.LogFields(otlog.Error(err))
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	span.LogKV(
		"name", person.Name,
		"title", person.Title,
		"description", person.Description,
	)

	bytes, _ := json.Marshal(person)
	w.Write(bytes)
}
