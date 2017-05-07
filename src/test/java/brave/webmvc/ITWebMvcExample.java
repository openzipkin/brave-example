package brave.webmvc;

import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.handlers.DefaultServlet;
import java.util.Collections;
import javax.servlet.ServletException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Integration test that shows the usage of {@link RestTemplate} which calls a web mvc controller.
 * Both of which are configured by {@link ExampleController}.
 *
 * <p>The first resource will call the 2nd resource (a -> b).
 *
 * <p>{@link TracingConfiguration} sets up tracing which times network calls. By default,
 * timing information is printed to the console.
 *
 * @author kristof
 */
public class ITWebMvcExample {

  Undertow server;

  @Before
  public void setup() throws ServletException {
    DeploymentInfo servletBuilder = Servlets.deployment()
        .setClassLoader(getClass().getClassLoader())
        .setContextPath("/")
        .setDeploymentName("test.war")
        .addServletContainerInitalizer(
            new ServletContainerInitializerInfo(SpringServletContainerInitializer.class,
                Collections.singleton(ExampleInitializer.class)))
        .addServlet(Servlets.servlet("default", DefaultServlet.class));

    DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
    manager.deploy();
    server = Undertow.builder()
        .addHttpListener(8081, "127.0.0.1")
        .setHandler(manager.start()).build();

    server.start();
  }

  @After
  public void tearDown() throws Exception {
    // Wait for Brave's collector queue to flush
    Thread.sleep(1000);
    server.stop();
  }

  @Test
  public void test() {
    String result = new RestTemplate().getForObject("http://localhost:8081/a", String.class);
    assertEquals("b", result);
  }
}
