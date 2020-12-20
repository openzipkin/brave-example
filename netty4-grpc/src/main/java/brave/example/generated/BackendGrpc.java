package brave.example.generated;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.31.1)",
    comments = "Source: backend.proto")
public final class BackendGrpc {

  private BackendGrpc() {}

  public static final String SERVICE_NAME = "brave.example.Backend";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<brave.example.generated.BackendProto.Empty,
      brave.example.generated.BackendProto.Reply> getPrintDateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PrintDate",
      requestType = brave.example.generated.BackendProto.Empty.class,
      responseType = brave.example.generated.BackendProto.Reply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<brave.example.generated.BackendProto.Empty,
      brave.example.generated.BackendProto.Reply> getPrintDateMethod() {
    io.grpc.MethodDescriptor<brave.example.generated.BackendProto.Empty, brave.example.generated.BackendProto.Reply> getPrintDateMethod;
    if ((getPrintDateMethod = BackendGrpc.getPrintDateMethod) == null) {
      synchronized (BackendGrpc.class) {
        if ((getPrintDateMethod = BackendGrpc.getPrintDateMethod) == null) {
          BackendGrpc.getPrintDateMethod = getPrintDateMethod =
              io.grpc.MethodDescriptor.<brave.example.generated.BackendProto.Empty, brave.example.generated.BackendProto.Reply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PrintDate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  brave.example.generated.BackendProto.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  brave.example.generated.BackendProto.Reply.getDefaultInstance()))
              .setSchemaDescriptor(new BackendMethodDescriptorSupplier("PrintDate"))
              .build();
        }
      }
    }
    return getPrintDateMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BackendStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BackendStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BackendStub>() {
        @java.lang.Override
        public BackendStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BackendStub(channel, callOptions);
        }
      };
    return BackendStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BackendBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BackendBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BackendBlockingStub>() {
        @java.lang.Override
        public BackendBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BackendBlockingStub(channel, callOptions);
        }
      };
    return BackendBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BackendFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BackendFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BackendFutureStub>() {
        @java.lang.Override
        public BackendFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BackendFutureStub(channel, callOptions);
        }
      };
    return BackendFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class BackendImplBase implements io.grpc.BindableService {

    /**
     */
    public void printDate(brave.example.generated.BackendProto.Empty request,
        io.grpc.stub.StreamObserver<brave.example.generated.BackendProto.Reply> responseObserver) {
      asyncUnimplementedUnaryCall(getPrintDateMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPrintDateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                brave.example.generated.BackendProto.Empty,
                brave.example.generated.BackendProto.Reply>(
                  this, METHODID_PRINT_DATE)))
          .build();
    }
  }

  /**
   */
  public static final class BackendStub extends io.grpc.stub.AbstractAsyncStub<BackendStub> {
    private BackendStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BackendStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BackendStub(channel, callOptions);
    }

    /**
     */
    public void printDate(brave.example.generated.BackendProto.Empty request,
        io.grpc.stub.StreamObserver<brave.example.generated.BackendProto.Reply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPrintDateMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class BackendBlockingStub extends io.grpc.stub.AbstractBlockingStub<BackendBlockingStub> {
    private BackendBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BackendBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BackendBlockingStub(channel, callOptions);
    }

    /**
     */
    public brave.example.generated.BackendProto.Reply printDate(brave.example.generated.BackendProto.Empty request) {
      return blockingUnaryCall(
          getChannel(), getPrintDateMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class BackendFutureStub extends io.grpc.stub.AbstractFutureStub<BackendFutureStub> {
    private BackendFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BackendFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BackendFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<brave.example.generated.BackendProto.Reply> printDate(
        brave.example.generated.BackendProto.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getPrintDateMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PRINT_DATE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final BackendImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(BackendImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PRINT_DATE:
          serviceImpl.printDate((brave.example.generated.BackendProto.Empty) request,
              (io.grpc.stub.StreamObserver<brave.example.generated.BackendProto.Reply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class BackendBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BackendBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return brave.example.generated.BackendProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Backend");
    }
  }

  private static final class BackendFileDescriptorSupplier
      extends BackendBaseDescriptorSupplier {
    BackendFileDescriptorSupplier() {}
  }

  private static final class BackendMethodDescriptorSupplier
      extends BackendBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    BackendMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (BackendGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BackendFileDescriptorSupplier())
              .addMethod(getPrintDateMethod())
              .build();
        }
      }
    }
    return result;
  }
}
