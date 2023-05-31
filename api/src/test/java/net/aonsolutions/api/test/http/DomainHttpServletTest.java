package net.aonsolutions.api.test.http;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.api.http.APIHttpServlet;
import net.aonsolutions.occam.api.AonNames;

class DomainHttpServletTest {

	protected static String DOMAIN_NAME = System.getProperty("domainName", "occam.aonsolutions.test");
	protected static String USER 		= System.getProperty("domainUser", "admin");
	
	@Test
	void testServlet() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		when(request.getParameter(AonNames.DOMAIN_NAME))
			.thenReturn(DOMAIN_NAME);
		when(request.getParameter(AonNames.USER)).thenReturn(USER);

		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(writer);

		new APIHttpServlet().doPost(request, response);

		verify(request, atLeast(1)).getParameter(AonNames.DOMAIN_NAME);
		verify(request, atLeast(1)).getParameter(AonNames.USER);
		
		writer.flush(); 
		
		JSONArray domainJSON = new JSONArray(stringWriter.toString());
		assertNotNull(domainJSON);
		System.out.println(domainJSON.toString(1));
		// Domain domain = DomainJSON.from( domainJSON );
		// assertNotNull(domain);
	}
}