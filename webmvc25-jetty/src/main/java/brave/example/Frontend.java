package brave.example;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Frontend {
  final HttpClient client;
  final String backendEndpoint;

  @Autowired Frontend(HttpClient client, String backendEndpoint) {
    this.client = client;
    this.backendEndpoint = backendEndpoint;
  }

  @RequestMapping("/")
  public void callBackend(HttpServletResponse resp) throws IOException {
    HttpGet request = new HttpGet(backendEndpoint);
    HttpResponse response = client.execute(request);
    resp.getWriter().write(EntityUtils.toString(response.getEntity()));
  }
}
