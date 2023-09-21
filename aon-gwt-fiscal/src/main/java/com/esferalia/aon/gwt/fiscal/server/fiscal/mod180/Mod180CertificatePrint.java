package com.esferalia.aon.gwt.fiscal.server.fiscal.mod180;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.fiscal.MODEL180;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.itextpdf.text.DocumentException;

@WebServlet(name = "Mod180 Certificate Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model180CertificatePrint" })
public class Mod180CertificatePrint extends HttpServlet {
	
	private static final long serialVersionUID = 8021949595124772358L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			int id = Integer.parseInt(req.getParameter("mod180"));
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod180 mod180 = MODEL180.get(occam, id);
			resp.setContentType(MimeType.PDF.getName());
			String s = sanitize(mod180.getName());
		    String fileName = "CertificadoRetenciones_" + "_" + mod180.getYear() + "_" + s;
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
			Mod180CertificatePDF print = new Mod180CertificatePDF();
			print.printMod180( resp.getOutputStream(), occam, mod180);
			resp.flushBuffer();
		} catch (DocumentException | IOException e) {
			throw new ServletException(e);
		}
	}
	
	private String sanitize(String seq) {
		if (seq == null) return null;
		StringBuilder sb = new StringBuilder();
	    if(!Character.isJavaIdentifierStart(seq.charAt(0))) {
	        sb.append("_");
	    }
	    for (char c : seq.toCharArray()) {
	        if(Character.isJavaIdentifierPart(c)) {
	            sb.append(c);
	        }
	    }
	    return sb.toString();
	}

}

