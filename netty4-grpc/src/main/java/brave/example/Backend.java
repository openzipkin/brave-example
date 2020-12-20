package brave.example;

import brave.example.generated.BackendGrpc.BackendImplBase;
import brave.example.generated.BackendProto.Empty;
import brave.example.generated.BackendProto.Reply;
import io.grpc.Context;
import io.grpc.Context.Key;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.stub.StreamObserver;
import java.util.Date;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public final class Backend extends BackendImplBase {
  /** This key is set when the header of the same name is via {@link CopyHeaderToGrpcContext}. */
  static final Key<String> USER_NAME_KEY = Context.key("user_name");

  @Override public void printDate(Empty request, StreamObserver<Reply> responseObserver) {
    String response = new Date().toString();
    String username = USER_NAME_KEY.get(); // set if a header "user_name" exists
    if (username != null) response += " " + username;
    Reply reply = Reply.newBuilder().setMessage(response + "\n").build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }

  public static void main(String[] args) throws Exception {
    // Configure JUL because that's the logging API used by the Google gRPC library
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    TracingConfiguration tracing = TracingConfiguration.create("backend");

    Server server = ServerBuilder.forPort(9000)
        .intercept(tracing.serverInterceptor())
        .intercept(new CopyHeaderToGrpcContext(USER_NAME_KEY))
        .addService(new Backend())
        .build();

    server.start().awaitTermination();
  }

  /**
   * Google's gRPC impl disallows reading headers inside a server handler. This copies a header into
   * the context so we can see if it was propagated or not.
   */
  static final class CopyHeaderToGrpcContext implements ServerInterceptor {
    final Key<String> contextKey;
    final Metadata.Key<String> headerKey;

    CopyHeaderToGrpcContext(Key<String> contextKey) {
      this.contextKey = contextKey;
      this.headerKey = Metadata.Key.of(contextKey.toString(), ASCII_STRING_MARSHALLER);
    }

    @Override public <I, O> ServerCall.Listener<I> interceptCall(
        ServerCall<I, O> call, Metadata headers, ServerCallHandler<I, O> next) {
      String userName = headers.get(headerKey);
      if (userName == null) return next.startCall(call, headers);
      Context newContext = Context.current().withValue(contextKey, userName);
      return Contexts.interceptCall(newContext, call, headers, next);
    }
  }
}
