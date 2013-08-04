package com.github.kristofa.brave.resteasyexample;

import java.io.IOException;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.kristofa.brave.BraveHttpHeaders;
import com.github.kristofa.brave.ClientTracer;
import com.github.kristofa.brave.SpanId;

/**
 * Example resource.
 * 
 * @author kristof
 */
@Repository
@Path("/brave-resteasy-example")
public class RestEasyExampleResource {

    private final ClientTracer clientTracer;

    @Autowired
    public RestEasyExampleResource(final ClientTracer clientTracer) {
        Validate.notNull(clientTracer);
        this.clientTracer = clientTracer;
    }

    /**
     * A will call B.
     * 
     * @return
     * @throws InterruptedException
     * @throws ClientProtocolException
     * @throws IOException
     */
    @Path("/a")
    @GET
    public Response a() throws InterruptedException, ClientProtocolException, IOException {

        final Random random = new Random();
        Thread.sleep(random.nextInt(1000));

        final HttpGet httpGet = new HttpGet("http://localhost:8080/RestEasyTest/brave-resteasy-example/b");

        final SpanId newSpan = clientTracer.startNewSpan("/brave-resteasy-example/b");

        if (newSpan != null) {
            httpGet.addHeader(BraveHttpHeaders.TraceId.getName(), String.valueOf(newSpan.getTraceId()));
            httpGet.addHeader(BraveHttpHeaders.SpanId.getName(), String.valueOf(newSpan.getSpanId()));
            httpGet.addHeader(BraveHttpHeaders.ParentSpanId.getName(), String.valueOf(newSpan.getParentSpanId()));
            httpGet.addHeader(BraveHttpHeaders.Sampled.getName(), "true");
        } else {
            httpGet.addHeader(BraveHttpHeaders.Sampled.getName(), "false");
        }

        final DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        clientTracer.setClientSent();
        try {
            final HttpResponse response = defaultHttpClient.execute(httpGet);
            final int returnCode = response.getStatusLine().getStatusCode();
            clientTracer.submitBinaryAnnotation("http.responsecode", returnCode);
            return Response.status(returnCode).build();

        } finally {
            clientTracer.setClientReceived();
            defaultHttpClient.getConnectionManager().shutdown();
        }

    }

    @Path("/b")
    @GET
    public Response b() throws InterruptedException {

        final Random random = new Random();
        Thread.sleep(random.nextInt(1000));

        return Response.ok().build();
    }

}
