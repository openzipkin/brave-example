package brave.webmvc;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class Backend implements MessageListener {
  @Override public void onMessage(Message message) {
    try {
      System.err.println(((ObjectMessage) message).getObject());
    } catch (JMSException e) {
      throw new RuntimeException(e); // Present for the listener container!
    }
  }
}
