package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model303ScriptProvider;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod303 Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model303Print" })
public class Mod303Print extends HttpServlet {

	private static final long serialVersionUID = 1105648961423707713L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod303"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod303 mod303 = MODEL303.get(occam,id);

			Mod303ExcelAction action = new Mod303ExcelAction(mod303);
			action.initialize(FiscalModelUtils.getModelName(mod303));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			for (IModelScript<Mod303Key> ms : Model303ScriptProvider.obtainScript(mod303)) {
				action.accept(ms);
			}
			action.beforeFinalize();
			action.finalize(output);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
			String fileName = AonFiscalFileUtils.getFileName(mod303);
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}
