package com.github.kristofa.brave.resteasyexample;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;

/**
 * Our resource interface. Shared between client and server.
 * 
 * @author kristof
 */
@Path("/brave-resteasy-example")
public interface RestEasyExampleResource {

    @Path("/a")
    @GET
    public Response a() throws InterruptedException, ClientProtocolException, IOException;

    @Path("/b")
    @GET
    public Response b() throws InterruptedException;
    
    @Path("/jersey")
    @GET
    public Response jersey() throws Exception;

}
