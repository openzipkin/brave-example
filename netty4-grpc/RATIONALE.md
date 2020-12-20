## Why Netty HTTP frontend?
While gRPC is HTTP/2, it is difficult to invoke without a compiled client. This
example aims to show gRPC client-server communication, but not distract users
who maybe aren't very familiar with how to compile protos.

While coding a Netty HTTP handler is distracting, due to dealing directly with
keep alives, it is less dependencies to worry about. Also, it serves as an
example of how to use Brave's Netty instrumentation.

## Why check in generated code?

The idiomatic way to generate code for Google's gRPC library is to use native
protoc via org.xolstice.maven.plugins:protobuf-maven-plugin. The native part
is key here. Unlike Square's wire plugin, this plugin requires binaries that
match the host OS and platform architecture.

Our builds use Alpine Linux and protobuf-maven-plugin is not compatible as
described here: https://github.com/xolstice/protobuf-maven-plugin/issues/23

There's a workaround. If you uninstall libc6-compat, you can install
sgerrand/alpine-pkg-glibc to make native protoc work. However, that only works
with Alpine+x86: https://github.com/sgerrand/docker-glibc-builder/pull/7

Our Docker images currently support arm64 also, and will s390x in the future.
The easiest way to deal with the constraints of protobuf-maven-plugin, is to
run it offline. As the example code won't change much this is a better trade-
off until the Google gRPC ecosystem improves its tooling portability.
