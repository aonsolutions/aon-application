package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.shared.IvlService.Parameter.FILE;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedList;

import com.esferalia.aon.gwt.payroll.shared.IvlService;
import com.esferalia.aon.in.payroll.tgss.ivl.Ivl2Aon;
import com.esferalia.aon.in.payroll.tgss.ivl.IvlParserListener;
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
	    	resp.setContentType("text/html");
	    	PrintStream out = new PrintStream(resp.getOutputStream());
	    	req.getParts()
	    	.stream()
	    	.filter(part -> part.getName().equals(FILE.name()))
	    	.forEach(part ->  { 
	    	    try {
	    		TraceIvlParserListener traceIvlParserListener = new TraceIvlParserListener();
	    		String contentTransferEncoding = part.getHeader("Content-Transfer-Encoding");
	    		if ( AonStringUtils.equalsIgnoreCase("base64", contentTransferEncoding) )
	    		    Ivl2Aon.insert(aonContext.getDslContext(), domainName, Base64.getDecoder().decode(part.getInputStream().readAllBytes()), traceIvlParserListener);
	    		else 
	    		    Ivl2Aon.insert(aonContext.getDslContext(), domainName, part.getInputStream(), traceIvlParserListener);
	    		
	    		out.printf("Importados <b>%d</b> contratos en la empress <b>%s</b>", traceIvlParserListener.getEmployeeNames().size(), traceIvlParserListener.getEnterpriseName());
	    	    } catch ( Exception e ) {
	    		throw new IllegalArgumentException(e);
	    	    }
	    	});
		resp.setStatus(HttpServletResponse.SC_OK);
	    	out.flush();
	    	out.close();
	} catch ( Exception e ) {
		resp.sendError(HttpServletResponse.SC_OK, e.getMessage());
	}
    }
    
    
    private static class TraceIvlParserListener implements IvlParserListener {
	
	private String enterpriseName;
	private Collection<String> employeeNames;
	
	public String getEnterpriseName() {
	    return enterpriseName;
	}
	
	public Collection<String> getEmployeeNames() {
	    return employeeNames;
	}

	private TraceIvlParserListener() {
	    employeeNames = new LinkedList<>();
	}

	@Override
	public void onEnterprise(String enterpriseName, String cccRegime, String cccProvince, String cccNumber,
		String docType, String docNumber, String enterpriseAddress, String enterpriseCity, String enterpriseCP,
		String enterpriseCNAENumber, String enterpriseCNAEDescription) {
	    this.enterpriseName = enterpriseName;
	}
	
	@Override
	public void onEmployee(String nafProvince, String nafNumber, String docType, String docNumber,
		String employeeName) {
	    employeeNames.add(employeeName);
	}
	
    }
    
}
