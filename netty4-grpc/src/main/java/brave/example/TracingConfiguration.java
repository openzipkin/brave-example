package brave.example;

import brave.grpc.GrpcTracing;
import brave.http.HttpTracing;
import brave.netty.http.NettyHttpTracing;
import brave.rpc.RpcTracing;
import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import io.netty.channel.ChannelHandler;
import org.springframework.context.support.ClassPathXmlApplicationContext;

final class TracingConfiguration {
  ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("tracing.xml");

  static TracingConfiguration create(String defaultServiceName) {
    if (System.getProperty("brave.localServiceName") == null) {
      System.setProperty("brave.localServiceName", defaultServiceName);
    }
    TracingConfiguration result = new TracingConfiguration();
    Runtime.getRuntime().addShutdownHook(new Thread(result.context::close));
    return result;
  }

  public ChannelHandler serverHandler() {
    return NettyHttpTracing.create(context.getBean(HttpTracing.class)).serverHandler();
  }

  public ClientInterceptor clientInterceptor() {
    // TODO: RpcTracing.clientOf("backend") equivalent
    return GrpcTracing.create(context.getBean(RpcTracing.class)).newClientInterceptor();
  }

  public ServerInterceptor serverInterceptor() {
    return GrpcTracing.create(context.getBean(RpcTracing.class)).newServerInterceptor();
  }
}
