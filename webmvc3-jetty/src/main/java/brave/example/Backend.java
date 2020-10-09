package brave.example;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Backend {
  @RequestMapping("/api")
  public ResponseEntity<String> printDate(
      @RequestHeader(value = "user_name", required = false) String username
  ) {
    String result;
    if (username != null) {
      result = new Date().toString() + " " + username;
    } else {
      result = new Date().toString();
    }
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }
}
