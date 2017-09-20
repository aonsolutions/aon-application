package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.mod131.Model131ScriptProvider;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod131 PDF Print", urlPatterns = { "/aon_gwt_fiscal/Model131PrintPDF" })
public class Mod131PrintPDF extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		File inputFile = null;
		File outputFile = null;
		try {
			int id = Integer.parseInt(req.getParameter("mod131"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = AonServletUtils.getLoggedUser();
			Mod131 mod131 = FISCAL.getMod131(domainName, domainId, user, id);

			// ------------------------------------------ OBTAIN EXCEL REPORT
			Mod131ExcelAction action = new Mod131ExcelAction(mod131);
			action.initialize(mod131.getModel().getName(mod131.getAdministration(), mod131.getPeriod()));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			for (IModelScript<Mod131Key> ms : Model131ScriptProvider.obtainScript(mod131)) {
				action.accept(ms);
			}
			action.beforeFinalize();
			action.finalize(output);
			// --------------------------------------------------------------

			String fileName = AonFiscalFileUtils.getFileName(mod131);

			inputFile = File.createTempFile("tmp", fileName + "." + MimeType.MS_EXCEL.getExtension());
			FileOutputStream inputFileOs = new FileOutputStream(inputFile);
			AonIOUtils.write(output.toByteArray(), inputFileOs);
			inputFileOs.flush();
			inputFileOs.close();

			outputFile = File.createTempFile("tmp", fileName + "." + MimeType.PDF.getExtension());

			resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
			AonIOUtils.copy(new FileInputStream(outputFile), resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		} finally {
			// TODO REMOVE AND CLOSE EVERYTHING
			if (inputFile != null && inputFile.canWrite())
				inputFile.delete();
			if (outputFile != null && outputFile.canWrite())
				outputFile.delete();
		}

	}

}
