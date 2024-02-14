package brave.example;

import java.net.URI;
import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.HttpEndpointSupplier;
import zipkin2.reporter.HttpEndpointSuppliers;

@Configuration(proxyBeanMethods = false)
public class ZipkinDiscoveryConfiguration {
  @Bean HttpEndpointSupplier.Factory loadbalancerEndpoints(LoadBalancerClient loadBalancerClient) {
    LoadBalancerHttpEndpointSupplier.Factory httpEndpointSupplierFactory =
        new LoadBalancerHttpEndpointSupplier.Factory(loadBalancerClient);
    // don't ask more than 30 seconds (just to show)
    return HttpEndpointSuppliers.newRateLimitedFactory(httpEndpointSupplierFactory, 30);
  }

  record LoadBalancerHttpEndpointSupplier(LoadBalancerClient loadBalancerClient, URI virtualURL)
      implements HttpEndpointSupplier {
    record Factory(LoadBalancerClient loadBalancerClient) implements HttpEndpointSupplier.Factory {

      @Override public HttpEndpointSupplier create(String endpoint) {
        return new LoadBalancerHttpEndpointSupplier(loadBalancerClient, URI.create(endpoint));
      }
    }

    @Override public String get() {
      // At least spring-cloud-netflix wants the actual hostname as a lookup value
      ServiceInstance instance = loadBalancerClient.choose(virtualURL.getHost());
      if (instance != null) {
        return instance.getUri() + virtualURL.getPath();
      }
      throw new IllegalArgumentException(
          virtualURL.getHost() + " is not an instance registered in Eureka");
    }

    @Override public void close() {
    }

    @Override public String toString() {
      return "LoadBalancer{" + virtualURL + "}";
    }
  }
}
