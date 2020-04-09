package brave.webmvc;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import brave.http.HttpTracing;
import brave.jaxrs2.TracingClientFilter;
import io.smallrye.config.inject.ConfigProducer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jboss.resteasy.microprofile.client.BuilderResolver;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAlternatives;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

@EnableAutoWeld
@AddBeanClasses({ConfigProducer.class, TracingProducer.class})
@EnableAlternatives(TracingProducerTest.class)
class TracingProducerTest {

    private static final Logger log = Logger.getLogger(TracingProducerTest.class.getName());

    private static final TestSpanReporter REPORTER = new TestSpanReporter();
    private static final MockWebServer SERVER = new MockWebServer();

    public interface TestInterface {
        @GET
        @Path("test")
        @Produces(MediaType.TEXT_PLAIN)
        String test();
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        SERVER.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        SERVER.shutdown();
    }

    @AfterEach
    void beforeEach() {
        REPORTER.clear();
    }

    @Test
    void producesTracing() {
        assertNotNull(TracingProducer.createTracing());
    }

    @Test
    void tracesRequest() {
        SERVER.enqueue(new MockResponse()
                .setResponseCode(HttpServletResponse.SC_OK)
                .addHeader("Content-Type", MediaType.TEXT_PLAIN)
                .setBody("response-text"));

        final String response = new BuilderResolver().newBuilder()
                .baseUri(SERVER.url("").uri())
                .register(TracingClientFilter.create(
                        HttpTracing.create(TracingProducer.createTracing())
                                .clientOf("test-service")))
                .build(TestInterface.class)
                .test();
        assertEquals("response-text", response);
        assertEquals(1, REPORTER.getSpans().size(), "Wrong number of reported spans");
    }

    /**
     * As the tracing filter is not processed no span is reported.
     * this is due to a wrong TracingClientFilter order / priority.
     *
     * @throws InterruptedException if response could not be taken
     */
    @Test
    void noTraceOnErrorResponse() throws InterruptedException {
        SERVER.enqueue(new MockResponse().setResponseCode(SC_INTERNAL_SERVER_ERROR));

        try {
            new BuilderResolver().newBuilder()
                    .baseUri(SERVER.url("").uri())
                    .register(TracingClientFilter.create(
                            HttpTracing.create(TracingProducer.createTracing())
                                    .clientOf("test-service")))
                    .build(TestInterface.class)
                    .test();
        } catch (final WebApplicationException e) {
            log.log(Level.WARNING, "Received expected error response: " + e.getMessage());
        }

        assertNotNull(SERVER.takeRequest(5, TimeUnit.SECONDS), "No recorded request to mock-server");
        // FIXME should be 1 instead of 0!
        assertEquals(0, REPORTER.getSpans().size(), "Wrong number of reported spans");
    }

    @SuppressWarnings({"SameReturnValue"})
    @javax.enterprise.inject.Produces
    @Dependent
    @Alternative
    private Reporter<Span> testSpanReporterProducer() {
        return REPORTER;
    }

    private static class TestSpanReporter implements Reporter<Span> {
        private final List<String> spans;

        TestSpanReporter() {
            spans = new ArrayList<>();
        }

        @Override
        public void report(final Span span) {
            spans.add(span.toString());
            log.log(Level.INFO, span.toString());
        }

        /**
         * @return recorded spans.
         */
        public List<String> getSpans() {
            return new ArrayList<>(spans);
        }

        public synchronized void clear() {
            spans.clear();
        }
    }
}
