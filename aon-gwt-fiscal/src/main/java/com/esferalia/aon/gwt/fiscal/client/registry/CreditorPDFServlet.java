package com.esferalia.aon.gwt.fiscal.client.registry;

import java.io.IOException;
import java.io.OutputStream;

import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.registry.report.CreditorReportPDF;

@WebServlet(name = "Creditor PDF Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/CreditorPDFServlet" })
public class CreditorPDFServlet extends HttpServlet {

	private static final long serialVersionUID = 6152551748473230508L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			OutputStream out = resp.getOutputStream();
			resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition","attachment; filename=\"ACREEDORES."+MimeType.PDF.getExtension()+"\";");
			Occam occam = new Occam()
					.setDomainName(  req.getParameter(IRequestParamsNames.DOMAIN_NAME) )
					.setDomain( AonNumberUtils.toint(req.getParameter(IRequestParamsNames.DOMAIN_ID)))
					.setUser(  req.getParameter(IRequestParamsNames.USER) )
					;
			RegistryParams params = JsonParser.parseRegistryParams(req.getParameter(IRequestParamsNames.REGISTRY_PARAMS));
			new CreditorReportPDF( occam ).print(out,
				AON.getCreditors(occam,params,0,Integer.MAX_VALUE)
					.map(c -> AON.getCreditorFull(occam,c.getId())))
				;
			resp.flushBuffer();
		} catch (Exception e) {
			
		}
	}
	
}
