package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.mod111.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.mod111.Model111ScriptProvider;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod111 Print", urlPatterns = { "/aon_gwt_fiscal/Model111Print" })
public class Mod111Print extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod111"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = AonServletUtils.getLoggedUser();
			Mod111 mod111 = AON.getMod111(domainName, domainId, user,id);

			FiscalModelExcelAction action = new FiscalModelExcelAction(mod111);
			action.initialize(mod111.getModel().getName(mod111.getAdministration(), mod111.getPeriod()));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			for (IModelScript ms : Model111ScriptProvider.obtainScript(mod111)) {
				action.accept(ms);
			}
			action.finalize(output);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
			String modName = getPeriodName( mod111 );
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + modName + ".xslx\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

	private static String getPeriodName(FiscalModel fs) {
		String name = AonStringUtils.trimToEmpty( fs.getName() );
		name = AonFiscalFileUtils.changeInvalidCharacters(name);
		name = name.replaceAll("[^a-zA-Z0-9.-]", "_");
		return  "Mod" + fs.getModel().getName(fs.getAdministration(), fs.getPeriod()) 
				+ "_" + fs.getYear() 
				+ "_" + fs.getPeriod().getName() 
				+ "_" + fs.getAdministration().toString() 
				+ AonStringUtils.prependIfMissing(name , "_");
	}
}
