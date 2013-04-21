package com.github.kristofa.brave.resteasyexample;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ClientTracer;
import com.github.kristofa.brave.EndPointSubmitter;
import com.github.kristofa.brave.HeaderConstants;
import com.github.kristofa.brave.SpanId;
import com.github.kristofa.brave.resteasy.BravePostProcessInterceptor;
import com.github.kristofa.brave.resteasy.BravePreProcessInterceptor;

/**
 * Integration test that shows the usage of {@link BravePreProcessInterceptor} and {@link BravePostProcessInterceptor}.
 * <p\>
 * We set up a service at context path RestEasyTest with 2 resources:
 * <ul>
 * <li>RestEasyTest/brave-resteasy-example/a</li>
 * <li>RestEasyTest/brave-resteasy-example/b</li>
 * </ul>
 * The first resource will call the 2nd resource (a -> b). <br\>
 * In our test we set up
 * 
 * @author adriaens
 */
public class ITRestEasyExample {

    private Server server;

    @Before
    public void setup() {
        server = new Server();

        final SocketConnector connector = new SocketConnector();

        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setPort(8080);
        server.setConnectors(new Connector[] {connector});

        final WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath("/RestEasyTest");
        context.setWar("src/main/webapp");

        server.setHandler(context);

        try {
            server.start();
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to start server.", e);
        }
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        server.join();
    }

    @Test
    public void test() throws ClientProtocolException, IOException {
        // We need to set up our endpoint first because we start a client request from
        // in our test so the brave preprocessor did not set up end point yet.
        final EndPointSubmitter endPointSubmitter = Brave.getEndPointSubmitter();
        endPointSubmitter.submit("127.0.0.1", 8080, "RestEasyTest");

        // Start new trace/span using ClientTracer.
        final ClientTracer clientTracer =
            Brave.getClientTracer(Brave.getLoggingSpanCollector(), Brave.getTraceAllTraceFilter());
        final SpanId newSpan = clientTracer.startNewSpan("brave-resteasy-example/a");

        // Create http request and set trace/span headers.
        final HttpGet httpGet = new HttpGet("http://localhost:8080/RestEasyTest/brave-resteasy-example/a");
        httpGet.addHeader(HeaderConstants.TRACE_ID, String.valueOf(newSpan.getTraceId()));
        httpGet.addHeader(HeaderConstants.SPAN_ID, String.valueOf(newSpan.getSpanId()));
        httpGet.addHeader(HeaderConstants.SHOULD_GET_TRACED, "true");

        final DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        try {
            clientTracer.setClientSent();
            final HttpResponse response = defaultHttpClient.execute(httpGet);
            final int returnCode = response.getStatusLine().getStatusCode();
            clientTracer.submitAnnotation("httpcode=" + returnCode);
            clientTracer.setClientReceived();
            assertEquals(200, returnCode);
        } finally {
            defaultHttpClient.getConnectionManager().closeExpiredConnections();
        }
    }

}
