package com.esferalia.aon.gwt.fiscal.server.fiscal.mod347;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL347;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.type.MimeType;

@WebServlet(name = "Merge Mail Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model347MergeMail" })
public class Mod347MergeMail extends HttpServlet {

	private static final long serialVersionUID = 1812242250894272353L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod347"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			
			Mod347 mod347 = MODEL347.get(occam,id);
			
			String fileName = getFileName( mod347 );
			resp.setContentType(MimeType.CSV.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.CSV.getExtension()+ "\";");
			OutputStreamWriter writer = new OutputStreamWriter(resp.getOutputStream(),StandardCharsets.ISO_8859_1);
			
			MODEL347.writeMailMergeReport(occam,mod347,writer);
			
			writer.flush();
			resp.flushBuffer();

		} catch (Exception e) {
			throw new ServletException(e);
		}

	}
	
    private String getFileName(IFiscalModel fm) {
		StringBuilder sb = new StringBuilder();
		sb.append("Mod");
		sb.append(FiscalModelUtils.getModelName(fm));
		sb.append("_");
		sb.append(fm.getYear());
		if ( fm.isQuarterPeriod() || fm.isMonthPeriod()) {
			sb.append("_");
			sb.append(fm.getPeriod().getName());
		}
		sb.append("_");
		for (char c : fm.getName().toCharArray()) {
			if (Character.isJavaIdentifierPart(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}


