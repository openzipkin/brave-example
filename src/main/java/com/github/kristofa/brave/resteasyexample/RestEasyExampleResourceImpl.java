package com.github.kristofa.brave.resteasyexample;

import java.io.IOException;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.kristofa.brave.BraveTracer;

/**
 * Example resource.
 * 
 * @author kristof
 */
@Repository
@Path("/brave-resteasy-example")
public class RestEasyExampleResourceImpl implements RestEasyExampleResource {

	@Autowired
	BraveTracer braveTracer;

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
	public Response a() throws InterruptedException, ClientProtocolException,
			IOException {
		braveTracer.startClientTracer("Sleep for random time");
		final Random random = new Random();
		Thread.sleep(random.nextInt(1000));
		braveTracer.stopClientTracer();
		
		braveTracer.startClientTracer("Calling remote REST service");
		
		final RestEasyExampleResource client = ProxyFactory.create(
				RestEasyExampleResource.class,
				"http://localhost:8080/RestEasyTest");
		@SuppressWarnings("unchecked")
		final ClientResponse<Void> response = (ClientResponse<Void>) client.b();
		try {
			braveTracer.stopClientTracer();
			return Response.status(response.getStatus()).build();
		} finally {
			response.releaseConnection();
		}
	}

	@Override
	@Path("/b")
	@GET
	public Response b() throws InterruptedException {

		//final Random random = new Random();
		//Thread.sleep(random.nextInt(1000));
		clientCall();
		return Response.ok().build();
	}

	private void clientCall() {
		try {
			braveTracer.startClientTracer("FirstLevelClient");
			braveTracer.submitAnnotation("Marker 1", "begin sleep marker");
			braveTracer.submitBinaryAnnotation(
					"Some Interesting Contaxt Value", "session id is 123");
			Thread.sleep(250);
			braveTracer.submitAnnotation("Marker 2", "end sleep marker");
			Thread.sleep(250);
			secondLevelClientCall();
			braveTracer.stopClientTracer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void secondLevelClientCall() throws Exception {
		braveTracer.startClientTracer("SecondLevelClient");
		Thread.sleep(250);
		thirdLevelClient();
		braveTracer.stopClientTracer();
	}

	private void thirdLevelClient() throws Exception {
		braveTracer.startClientTracer("ThirdLevelClient()");
		Thread.sleep(250);
		braveTracer.stopClientTracer();
	}

}
