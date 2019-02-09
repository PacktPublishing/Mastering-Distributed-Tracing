package xhttp

import (
	"fmt"
	"io/ioutil"
	"net/http"
)

// Get executes an HTTP GET request and returns the response body.
// Any errors or non-200 status code result in an error.
func Get(url string) ([]byte, error) {
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, err
	}
	return Do(req)
}

// Do executes an HTTP request and returns the response body.
// Any errors or non-200 status code result in an error.
func Do(req *http.Request) ([]byte, error) {
	return DoWithClient(req, http.DefaultClient)
}

// DoWithClient executes an HTTP request and returns the response body.
// Any errors or non-200 status code result in an error.
func DoWithClient(req *http.Request, client *http.Client) ([]byte, error) {
	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	if resp.StatusCode != 200 {
		return nil, fmt.Errorf("StatusCode: %d, Body: %s", resp.StatusCode, body)
	}

	return body, nil
}
