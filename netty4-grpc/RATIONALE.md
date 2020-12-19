## Why Netty HTTP frontend?
While gRPC is HTTP/2, it is difficult to invoke without a compiled client. This
example aims to show gRPC client-server communication, but not distract users
who maybe aren't very familiar with how to compile protos.

While coding a Netty HTTP handler is distracting, due to dealing directly with
keep alives, it is less dependencies to worry about. Also, it serves as an
example of how to use Brave's Netty instrumentation.

## Why Spring XML?
Jersey requires HK2 or CDI injection. We don't currently have CDI integration
with Brave, else we would use that.

There are some external ways to use integrate Spring with Jersey, but it is
confusing. Instead, we hide Spring inside `TracingConfiguration`. Internally,
this uses XML as it is declarative, but we could have easily used Java Config.
