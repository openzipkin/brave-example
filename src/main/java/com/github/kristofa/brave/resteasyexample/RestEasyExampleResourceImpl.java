package com.github.kristofa.brave.resteasyexample;

import java.io.IOException;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.springframework.stereotype.Repository;

/**
 * Example resource.
 * 
 * @author kristof
 */
@Repository
@Path("/brave-resteasy-example")
public class RestEasyExampleResourceImpl implements RestEasyExampleResource {

    /**
     * A will call B.
     * 
     * @return
     * @throws InterruptedException
     * @throws ClientProtocolException
     * @throws IOException
     */
    @Override
    @Path("/a")
    @GET
    public Response a() throws InterruptedException, ClientProtocolException, IOException {

        final Random random = new Random();
        Thread.sleep(random.nextInt(1000));

        final RestEasyExampleResource client =
            ProxyFactory.create(RestEasyExampleResource.class, "http://localhost:8081/RestEasyTest");
        @SuppressWarnings("unchecked")
        final ClientResponse<Void> response = (ClientResponse<Void>)client.b();
        try {
            return Response.status(response.getStatus()).build();
        } finally {
            response.releaseConnection();
        }
    }

    @Override
    @Path("/b")
    @GET
    public Response b() throws InterruptedException {

        final Random random = new Random();
        Thread.sleep(random.nextInt(1000));

        return Response.ok().build();
    }

}
