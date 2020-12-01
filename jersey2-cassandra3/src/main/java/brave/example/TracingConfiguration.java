package brave.example;

import brave.Tracing;
import brave.cassandra.driver.CassandraClientTracing;
import brave.cassandra.driver.TracingSession;
import brave.http.HttpTracing;
import brave.jersey.server.TracingApplicationEventListener;
import com.datastax.driver.core.Session;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
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

  Tracing tracing() {
    return context.getBean(Tracing.class);
  }

  Session tracingSession(Session delegate, String remoteServiceName) {
    CassandraClientTracing cassandraTracing = CassandraClientTracing.newBuilder(tracing())
        .propagationEnabled(true).remoteServiceName(remoteServiceName).build();
    return TracingSession.create(cassandraTracing, delegate);
  }

  ApplicationEventListener tracingListener() {
    return TracingApplicationEventListener.create(context.getBean(HttpTracing.class));
  }
}
