package main

import (
	"context"
	"encoding/json"
	"log"
	"net/http"
	"net/url"
	"strings"

	opentracing "github.com/opentracing/opentracing-go"
	ottag "github.com/opentracing/opentracing-go/ext"
	otlog "github.com/opentracing/opentracing-go/log"

	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/lib/http"
	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/lib/model"
	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/lib/tracing"
)

func main() {
	tracer, closer := tracing.Init("go-4-hello")
	defer closer.Close()
	opentracing.SetGlobalTracer(tracer)

	http.HandleFunc("/sayHello/", handleSayHello)

	log.Print("Listening on http://localhost:8080/")
	log.Fatal(http.ListenAndServe(":8080", nil))
}

func handleSayHello(w http.ResponseWriter, r *http.Request) {
	spanCtx, _ := opentracing.GlobalTracer().Extract(
		opentracing.HTTPHeaders,
		opentracing.HTTPHeadersCarrier(r.Header),
	)
	span := opentracing.GlobalTracer().StartSpan(
		"say-hello",
		ottag.RPCServerOption(spanCtx),
	)
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
	person, err := getPerson(ctx, name)
	if err != nil {
		return "", err
	}

	return formatGreeting(ctx, person)
}

func getPerson(ctx context.Context, name string) (*model.Person, error) {
	url := "http://localhost:8081/getPerson/" + name
	res, err := get(ctx, "getPerson", url)
	if err != nil {
		return nil, err
	}

	var person model.Person
	if err := json.Unmarshal(res, &person); err != nil {
		return nil, err
	}
	return &person, nil
}

func formatGreeting(
	ctx context.Context,
	person *model.Person,
) (string, error) {
	v := url.Values{}
	v.Set("name", person.Name)
	v.Set("title", person.Title)
	v.Set("description", person.Description)
	url := "http://localhost:8082/formatGreeting?" + v.Encode()
	res, err := get(ctx, "formatGreeting", url)
	if err != nil {
		return "", err
	}
	return string(res), nil
}

func get(ctx context.Context, operationName, url string) ([]byte, error) {
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, err
	}

	span, ctx := opentracing.StartSpanFromContext(ctx, operationName)
	defer span.Finish()

	ottag.SpanKindRPCClient.Set(span)
	ottag.HTTPUrl.Set(span, url)
	ottag.HTTPMethod.Set(span, "GET")
	opentracing.GlobalTracer().Inject(
		span.Context(),
		opentracing.HTTPHeaders,
		opentracing.HTTPHeadersCarrier(req.Header),
	)

	return xhttp.Do(req)
}
