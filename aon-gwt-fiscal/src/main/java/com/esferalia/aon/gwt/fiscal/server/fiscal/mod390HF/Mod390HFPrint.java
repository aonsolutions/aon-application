package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390HF;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model390ScriptProvider;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod390HF Print", urlPatterns = { "/aon_gwt_fiscal/Model390HFPrint" })
public class Mod390HFPrint extends HttpServlet {

	private static final long serialVersionUID = -7836398822794207890L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod390HF"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam()
					.setDomainName(domainName)
					.setDomain(domainId)
					.setUser(user);
			Mod390HF mod390  = MODEL390HF.get(occam,id);

			Mod390HFExcelAction action = new Mod390HFExcelAction(mod390);
			action.initialize(FiscalModelUtils.getModelName(mod390));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			for (IModelScript<Mod390Key> ms : Model390ScriptProvider.obtainScript(mod390)) {
				action.accept(ms);
			}
			action.beforeFinalize();
			action.finalize(output);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
			String fileName = AonFiscalFileUtils.getFileName(mod390);
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}
