# Chapter 4: Instrumentation Basics with OpenTracing

This chapter contains a step-by-step walk-through of instrumenting a simple application for tracing while evolving it from a monolith to a microservices-based application. The examples are provided in three programming languages (Go, Java, and Python), to illustrate the language-specific differences of applying the concepts of the OpenTracing APIs.

* Exercise 1: The Hello application
* Exercise 2: The first trace
* Exercise 3: Tracing functions and passing context
  * 3a) tracing individual functions
  * 3b) combining spans into a single trace
  * 3c) propagating context in-process
* Exercise 4: Tracing RPC requests
  * 4a) breaking up the monolith
  * 4b) passing context between processes
  * 4c) applying OpenTracing-recommended tags
* Exercise 5: Using "baggage"
* Exercise 6: Applying open-source auto-instrumentation
