package net.aonsolutions.api.test.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import net.aonsolutions.api.rest.RestApplication;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.json.DomainJSON;

public class DomainRESTTestCase {
	
	private static final String DOMAIN_NAME = "occam.aonsolutions.test";
	private static SeBootstrap.Instance INSTANCE;

	@BeforeAll
	public static void startInstance() throws Exception {
		INSTANCE = SeBootstrap
			.start(RestApplication.class)
			.toCompletableFuture()
			.get(10, TimeUnit.SECONDS);
		Assertions.assertNotNull(INSTANCE, "Failed to start instance");
	}

	@AfterAll
	public static void stopInstance() throws Exception {
		if (INSTANCE != null) {
			INSTANCE.stop().toCompletableFuture().get(10, TimeUnit.SECONDS);
		}
	}

	@Test
	void domainTest() {
		try (Client client = ClientBuilder.newClient()) {
			
			final Response response = client
//					.target("http://occam.aonsolutions.test:8081/")
					.target(INSTANCE.configuration().baseUriBuilder().host(DOMAIN_NAME))
					.path("/rest")
					.path("/domains")
	//				.path("/withAudit")
	//				.path("/withBooking")
	//				.path("/withCompany")
	//				.path("/withConfiguration")
	//				.path("/withParentDomain")
	//				.path("/withUsers")
					.queryParam("withId", 85)
					.request()
	//				.header(null, client)
	//				.header(null, client)
				.get();
			assertNotNull(response);
			String resp = response.readEntity(String.class);
			System.out.println( "resp -->" + resp );
			JSONObject domainJson = new JSONObject(resp);
			assertNotNull(domainJson);
			System.out.println( domainJson.toString(1) );
			Domain domain = DomainJSON.from( domainJson );
			assertNotNull(domain);
			assertEquals(85, domain.getId());
		}
	}
	
}