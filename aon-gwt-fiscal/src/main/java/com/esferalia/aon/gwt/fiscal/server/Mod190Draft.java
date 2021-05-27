package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod190 Draft", urlPatterns = { "/aon_gwt_fiscal/ms/Model190Draft" })
public class Mod190Draft extends HttpServlet {

	private static final long serialVersionUID = -2333787421849079563L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod190"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod190 mod190 = FISCAL.getMod190(domainName, domainId, user,id);

			Mod190ExcelAction action = new Mod190ExcelAction(mod190);
			action.initialize(FiscalModelUtils.getModelName(mod190));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			for (Mod190Detail detail : mod190.getDetails()) {
				action.accept(detail);				
			}			
			
			action.finalize(output);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
			String s = mod190.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

			String fileName = "Mod190" + "_" + mod190.getYear() + "_" + sb.toString();			
			
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}
