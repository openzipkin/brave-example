package brave.example;

import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import io.dropwizard.setup.Environment;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

/** Example conventions include a self-contained main class. */
public class Backend extends ExampleApplication<BackendConfiguration> {

  @Path("/")
  public static class Resource {
    @GET
    @Path("/api")
    public String printDate(@HeaderParam("user_name") String username) {
      if (username != null) {
        return new Date().toString() + " " + username;
      }
      return new Date().toString();
    }
  }

  Backend() {
    super("backend", 9000);
  }

  @Override public void run(BackendConfiguration configuration, Environment environment) {
    super.run(configuration, environment);
    environment.jersey().register(new Resource());
  }

  @Override ZipkinFactory zipkinFactory(BackendConfiguration configuration) {
    return configuration.getZipkin();
  }

  public static void main(String[] args) throws Exception {
    new Backend().run();
  }
}

