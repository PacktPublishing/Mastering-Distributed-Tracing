package main

import (
	"log"
	"net/http"
	"strings"

	"github.com/PacktPublishing/Mastering-Distributed-Tracing/Chapter04/go/exercise1/people"
)

var repo *people.Repository

func main() {
	repo = people.NewRepository()
	defer repo.Close()

	http.HandleFunc("/sayHello/", handleSayHello)

	log.Print("Listening on http://localhost:8080/")
	log.Fatal(http.ListenAndServe(":8080", nil))
}

func handleSayHello(w http.ResponseWriter, r *http.Request) {
	name := strings.TrimPrefix(r.URL.Path, "/sayHello/")
	greeting, err := SayHello(name)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Write([]byte(greeting))
}

// SayHello creates a greeting for the named person.
func SayHello(name string) (string, error) {
	person, err := repo.GetPerson(name)
	if err != nil {
		return "", err
	}
	return FormatGreeting(
		person.Name,
		person.Title,
		person.Description,
	), nil
}

// FormatGreeting combines information about a person into a greeting string.
func FormatGreeting(name, title, description string) string {
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
