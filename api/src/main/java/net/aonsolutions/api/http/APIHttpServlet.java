package net.aonsolutions.api.http;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.occam.api.AON;
import net.aonsolutions.occam.api.AonConstants;
import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.Occam;
import net.aonsolutions.occam.json.DomainJSON;

@WebServlet(name = "APIHttpServlet", urlPatterns = {"/ms/api/aon/*"})
public class APIHttpServlet extends HttpServlet{

	private static final long serialVersionUID = 5807589402121022731L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	    String domainName = req.getParameter(AonNames.DOMAIN_NAME);
	    String user = req.getParameter(AonNames.USER);

        resp.setContentType(AonConstants.APPLICATION_JSON);
        resp.setHeader(AonConstants.CACHE_CONTROL, AonConstants.NO_CACHE);
        Occam occam = new Occam().setDomainName(domainName).setUser(user);
        resp.getWriter().write( DomainJSON.to( 
	        AON.getDomains(occam
	    		, f -> f.withId().gt(0)
	    		, b -> b.withUsers()
	    			.withAudit()
	    			.withBooking()
	    			.withCompany()
	    			.withParentDomain()
	    			.withConfiguration( a -> a.withAccountingConfiguration())
	    			.limit(0, 3)
   		)).toString());
	}
}
