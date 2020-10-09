package brave.example;

import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Backend {
  @RequestMapping("/api")
  public void printDate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String result;
    String username = req.getHeader("user_name");
    if (username != null) {
      result = new Date().toString() + " " + username;
    } else {
      result = new Date().toString();
    }
    resp.getWriter().write(result);
  }
}
