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
import com.esferalia.aon.gwt.fiscal.shared.mod123.Model123ScriptProvider;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod123 Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model123Print" })
public class Mod123Print extends HttpServlet {

	private static final long serialVersionUID = 1179462867550084505L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod123"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Mod123 mod123 = FISCAL.getMod123(domainName, domainId, user,id);

			Mod123ExcelAction action = new Mod123ExcelAction(mod123);
			action.initialize(FiscalModelUtils.getModelName(mod123));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			for (IModelScript<Mod123Key> ms : Model123ScriptProvider.obtainScript(mod123)) {
				action.accept(ms);
			}
			action.beforeFinalize();
			action.finalize(output);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
			String fileName = AonFiscalFileUtils.getFileName(mod123);
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}
