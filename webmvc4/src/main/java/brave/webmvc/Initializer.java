package brave.webmvc;

import brave.spring.webmvc.DelegatingTracingFilter;
import javax.servlet.Filter;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/** Indirectly invoked by {@link SpringServletContainerInitializer} in a Servlet 3+ container */
public class Initializer extends AbstractAnnotationConfigDispatcherServletInitializer {

  @Override protected String[] getServletMappings() {
    return new String[] {"/"};
  }

  @Override protected Class<?>[] getRootConfigClasses() {
    return new Class[] {TracingConfiguration.class, AppConfiguration.class};
  }

  /** Ensures tracing is setup for all HTTP requests. */
  @Override protected Filter[] getServletFilters() {
    return new Filter[] {new DelegatingTracingFilter()};
  }

  @Override protected Class<?>[] getServletConfigClasses() {
    return new Class[] {Frontend.class, Backend.class};
  }
}