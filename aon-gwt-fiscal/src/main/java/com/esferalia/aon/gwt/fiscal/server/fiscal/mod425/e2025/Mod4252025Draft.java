package com.esferalia.aon.gwt.fiscal.server.fiscal.mod425.e2025;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL4252025;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod4252025 Draft", urlPatterns = { "/aon_gwt_fiscal/Model4252025Draft" })
public class Mod4252025Draft extends HttpServlet {

	private static final long serialVersionUID = -1626672397185567794L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod390"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Occam occam = new Occam()
					.setDomainName(domainName)
					.setDomain(domainId)
					.setUser(user);			
			Mod4252025 mod425 = MODEL4252025.get(occam,id);

			Mod4252025ExcelAction action = new Mod4252025ExcelAction(mod425);
			action.initialize(FiscalModelUtils.getModelName(mod425));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			action.accept(mod425);				
			
			action.finalize(output);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
			String s = mod425.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

			String fileName = "Mod425" + "_" + mod425.getYear() + "_" + sb.toString();			
			
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}
