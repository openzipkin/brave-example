package com.github.kristofa.brave.resteasyexample;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.kristofa.brave.resteasy.BraveClientExecutionInterceptor;
import com.github.kristofa.brave.resteasy.BravePostProcessInterceptor;
import com.github.kristofa.brave.resteasy.BravePreProcessInterceptor;

/**
 * Integration test that shows the usage of {@link BraveClientExecutionInterceptor} at client side and
 * {@link BravePreProcessInterceptor} / {@link BravePostProcessInterceptor} at server side.
 * <p\>
 * We set up a service at context path RestEasyTest with 2 resources:
 * <ul>
 * <li>RestEasyTest/brave-resteasy-example/a</li>
 * <li>RestEasyTest/brave-resteasy-example/b</li>
 * </ul>
 * The first resource will call the 2nd resource (a -> b). <br\>
 * </p>
 * Brave is set up at client and server side.
 * 
 * @author kristof
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
    public void test() throws ClientProtocolException, IOException, InterruptedException {

        // this initialization only needs to be done once per VM
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

        // Create our client. The client will be configured using BraveClientExecutionInterceptor because
        // we Spring will scan com.github.kristofa.brave package. This is the package containing our client interceptor
        // in module brave-resteasy-spring-module which is on our class path.
        final RestEasyExampleResource client =
            ProxyFactory.create(RestEasyExampleResource.class, "http://localhost:8080/RestEasyTest");

        @SuppressWarnings("unchecked")
        final ClientResponse<Void> response = (ClientResponse<Void>)client.a();
        try {
            assertEquals(200, response.getStatus());
        } finally {
            response.releaseConnection();
        }
    }

}
