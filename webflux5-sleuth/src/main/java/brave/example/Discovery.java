package brave.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Discovery {

  /** Manually set properties based on the env {@code EUREKA_SERVICE_URL}. */
  static String[] withDiscoveryArgs(String... args) {
    String eurekaUri = System.getenv("EUREKA_SERVICE_URL");
    List<String> argList = new ArrayList<>(Arrays.asList(args));
    if (eurekaUri != null && !eurekaUri.isEmpty()) {
      // Only the v2 endpoint is used, but the Spring property is before that.
      argList.add("--eureka.client.serviceUrl.defaultZone=" +
          eurekaUri.replace("/v2", ""));
      // Enable discovery of the Zipkin endpoint.
      argList.add("--spring.zipkin.discoveryClientEnabled=true");
      // Set the reported localServiceName to registered Eureka service ID.
      argList.add("--spring.zipkin.locator.discovery.enabled=true");
      // Typically, this would be the application zipkin registered into, but
      // in Spring, it is the *VIP address* zipkin registered.
      // See https://github.com/spring-cloud/spring-cloud-netflix/issues/1788
      argList.add("--spring.zipkin.baseUrl=http://zipkin/");
    } else {
      argList.add("--eureka.client.enabled=false");
    }
    return argList.toArray(new String[0]);
  }
}
