# Chapter 7: Tracing with Service Mesh

This chapter illustreates how an application running on Kubernetes can be traced with the help of a service mesh Istio.
It compares the traces obtained from an application that only propagates tracing headers (a minimum requirement for
tracing via service mesh to work) with traces from an application that is explicitly instrumented with OpenTracing.
It also shows how _baggage_ (a form of distributed context propagation) can be used to inform routing decisions in Istio.
