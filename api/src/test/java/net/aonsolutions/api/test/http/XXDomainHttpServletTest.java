package net.aonsolutions.api.test.http;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.api.http.XXDomainHttpServlet;
import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.json.DomainJSON;

class XXDomainHttpServletTest extends Mockito {

	protected static String DOMAIN_NAME = System.getProperty("domainName", "occam.aonsolutions.test");
	protected static String USER 		= System.getProperty("domainUser", "admin");
	
	@Test
	void testServlet() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		when(request.getParameter(AonNames.DOMAIN_NAME)).thenReturn(DOMAIN_NAME);
		when(request.getParameter(AonNames.USER)).thenReturn(USER);

		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(writer);

		new XXDomainHttpServlet().doPost(request, response);

		verify(request, atLeast(1)).getParameter(AonNames.DOMAIN_NAME);
		verify(request, atLeast(1)).getParameter(AonNames.USER);
		
		writer.flush(); 
		JSONObject domainJSON = new JSONObject(stringWriter.toString());
		assertNotNull(domainJSON);
		System.out.println(domainJSON.toString(1));
		Domain domain = DomainJSON.from( domainJSON );
		assertNotNull(domain);
	}
}