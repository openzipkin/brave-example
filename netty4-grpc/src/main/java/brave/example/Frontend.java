package brave.example;

import brave.example.generated.BackendGrpc;
import brave.example.generated.BackendGrpc.BackendBlockingStub;
import brave.example.generated.BackendProto.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public final class Frontend extends ChannelInboundHandlerAdapter {
  final BackendBlockingStub backend;
  HttpRequest req;

  Frontend(BackendBlockingStub backend) {
    this.backend = backend;
  }

  @Override public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (!(msg instanceof HttpRequest)) return;
    req = (HttpRequest) msg;

    String path = req.uri();
    HttpResponseStatus status = OK;
    String content = "";
    if (req.method() != HttpMethod.GET) {
      status = METHOD_NOT_ALLOWED;
    } else if (path.equals("/health")) {
      content = "ok\n"; // fake health
    } else if (path.equals("/")) {
      // TODO: It isn't good to block. Feel free to improve, but watch out for complexity!
      content = backend.printDate(Empty.getDefaultInstance()).getMessage();
    } else {
      status = NOT_FOUND;
    }

    writeResponse(ctx, status, content);
  }

  @Override public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  void writeResponse(ChannelHandlerContext ctx, HttpResponseStatus responseStatus, String content) {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
        Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, "text/plain");
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
    response.setStatus(responseStatus);

    if (HttpUtil.isKeepAlive(req)) {
      ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    } else {
      response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      ctx.write(response);
    }
  }

  @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    ctx.fireExceptionCaught(cause);
    writeResponse(ctx, INTERNAL_SERVER_ERROR,
        cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName());
  }

  public static void main(String[] args) throws Exception {
    // Configure JUL because that's the logging API used by the Google gRPC library
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    TracingConfiguration tracing = TracingConfiguration.create("frontend");

    ManagedChannel backendChannel =
        ManagedChannelBuilder.forTarget(System.getProperty("backend.target", "127.0.0.1:9000"))
            .intercept(tracing.clientInterceptor())
            .usePlaintext()
            .build();
    Runtime.getRuntime().addShutdownHook(new Thread(backendChannel::shutdown));

    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    ServerBootstrap b = new ServerBootstrap();
    b.option(ChannelOption.SO_BACKLOG, 1024);
    b.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<Channel>() {
          @Override protected void initChannel(Channel ch) {
            ChannelPipeline p = ch.pipeline();
            p.addLast(new HttpServerCodec());
            p.addLast(tracing.serverHandler());
            p.addLast(new Frontend(BackendGrpc.newBlockingStub(backendChannel)));
          }
        });

    Channel channel = b.bind(8081).sync().channel();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> { // Cleanup Netty on shutdown
      try {
        channel.close().sync();
      } catch (InterruptedException e) {
      }
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }));

    Thread.currentThread().join();
  }
}
