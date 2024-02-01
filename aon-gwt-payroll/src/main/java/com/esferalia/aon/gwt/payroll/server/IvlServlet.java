package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.shared.IvlService.Parameter.FILE;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Base64;

import com.esferalia.aon.gwt.payroll.shared.IvlService;
import com.esferalia.aon.in.payroll.tgss.ivl.Ivl2Aon;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "Ivl", urlPatterns = { "/aon_gwt_payroll/ivl" })
public class IvlServlet extends HttpServlet  {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	String user = req.getParameter(IvlService.Parameter.USER.name());
	String domainName = req.getParameter(IvlService.Parameter.DOMAIN.name());
	try ( CloseableAONContext aonContext = AONContext.getAONContext(domainName, user) ) {
	    	req.getParts()
	    	.stream()
	    	.filter(part -> part.getName().equals(FILE.name()))
	    	.forEach(part ->  { 
	    	    try {
	    		String contentTransferEncoding = part.getHeader("Content-Transfer-Encoding");
	    		if ( AonStringUtils.equalsIgnoreCase("base64", contentTransferEncoding) )
	    		    Ivl2Aon.insert(aonContext.getDslContext(), domainName, Base64.getDecoder().decode(part.getInputStream().readAllBytes()));
	    		else 
	    		    Ivl2Aon.insert(aonContext.getDslContext(), domainName, part.getInputStream());
	    		
	    	    } catch ( Exception e ) {
	    		throw new IllegalArgumentException(e);
	    	    }
	    	});
		resp.setStatus(HttpServletResponse.SC_OK);
	    	PrintStream out = new PrintStream(resp.getOutputStream());
	    	out.print("Importados :-)");
	    	out.flush();
	    	out.close();
	} catch ( Exception e ) {
		resp.sendError(HttpServletResponse.SC_OK, e.getMessage());
	}
    }
    
}
