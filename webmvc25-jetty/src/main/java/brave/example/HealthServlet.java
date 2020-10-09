package brave.example;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Dummy /health endpoint that allows us to ensure our HEALTHCHECK doesn't start traces. */
public final class HealthServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    PrintWriter out = res.getWriter();
    out.println("ok");
    out.close();
  }
}
