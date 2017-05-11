package brave.webmvc;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Integration test that shows the usage of {@link RestTemplate} which calls a web mvc controller.
 * Both of which are configured by {@link ExampleController}.
 *
 * <p>The first resource will call the 2nd resource (a -> b).
 *
 * <p>xml sets up tracing which times network calls. By default,
 * timing information is printed to the console.
 *
 * @author kristof
 */
public class ITWebMvcExample {

  private Server server;

  @Before
  public void setup() {
    SocketConnector connector = new SocketConnector();
    connector.setMaxIdleTime(1000 * 60 * 60);
    connector.setPort(8081);
    server = new Server();
    server.setConnectors(new Connector[] {connector});

    WebAppContext context = new WebAppContext();
    context.setContextPath("/");
    context.setWar("src/main/webapp");
    server.setHandler(context);

    try {
      server.start();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to start server.", e);
    }
  }

  @After
  public void tearDown() throws Exception {
    // Wait for Brave's collector queue to flush
    Thread.sleep(1000);
    server.stop();
    server.join();
  }

  @Test
  public void test() {
    String result = new RestTemplate().getForObject("http://localhost:8081/a", String.class);
    assertEquals("b", result);
  }
}
