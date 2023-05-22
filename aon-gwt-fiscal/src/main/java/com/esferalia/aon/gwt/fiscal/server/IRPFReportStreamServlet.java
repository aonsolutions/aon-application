package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.json.IrpfBreakdownJSON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.type.MimeType;

@WebServlet(name = "IRPF Report Stream", urlPatterns = { "/aon_gwt_fiscal/roms/IRPFReportStream" })
public class IRPFReportStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -2697508555670615321L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			String irpfParams = req.getParameter( IRequestParamsNames.IRPF_PARAMS );
			String domainName = req.getParameter( IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			IRPFParams params = JsonParser.parseIRPFParams(irpfParams);
			resp.setContentType(MimeType.JSON.getName());
			PrintWriter writer = new PrintWriter (resp.getWriter(), true); 
			writer.print( "[" );
			FISCAL.getIrpfBreakdown(occam, params)
				.map( IrpfBreakdownJSON::toJSON )
				.forEach( js -> writer.print( js.toString() ));
			writer.print( "]" );
			resp.flushBuffer();
		} catch (ParseException | java.text.ParseException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}

	}
}
