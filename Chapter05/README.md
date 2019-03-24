# Chapter 5: Instrumentation of Asynchronous Applications

In this chapter, we attempt to instrument an online chat application, "Tracing Talk", 
which uses asynchronous messaging-based interactions between microservices built on top of Apache Kafka.
We see how metadata context can be passed through messaging systems using the OpenTracing primitives, 
and how causal relationships between spans can be modeled differently than in the plain RPC scenarios.

The chapter also illustrates how in-process context propagation can be achieved automatically even when
using asynchronous programming model based on Futures.
