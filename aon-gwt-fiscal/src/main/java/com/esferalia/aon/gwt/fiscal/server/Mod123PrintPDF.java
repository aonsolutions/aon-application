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
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.mod123.Model123ScriptProvider;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod123 PDF Print", urlPatterns = { "/aon_gwt_fiscal/Model123PrintPDF" })
public class Mod123PrintPDF extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("doget", "doget");
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Boolean doget = req.getAttribute("doget") != null;
		File inputFile = null;
		File outputFile = null;
		try {
			int id = Integer.parseInt(req.getParameter("mod123"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = AonServletUtils.getLoggedUser();
			Mod123 mod123 = FISCAL.getMod123(domainName, domainId, user, id);
			
			// ------------------------------------------ OBTAIN EXCEL REPORT
			Mod123ExcelAction action = new Mod123ExcelAction(mod123);
			action.initialize(mod123.getModel().getName(mod123.getAdministration(), mod123.getPeriod()));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			for (IModelScript<Mod123Key> ms : Model123ScriptProvider.obtainScript(mod123)) {
				action.accept(ms);
			}
			action.beforeFinalize();
			action.finalize(output);
			// --------------------------------------------------------------
			
			String fileName = AonFiscalFileUtils.getFileName(mod123);
			
			inputFile = File.createTempFile("tmp", fileName + "." + MimeType.MS_EXCEL.getExtension());
			FileOutputStream inputFileOs = new FileOutputStream(inputFile);
			AonIOUtils.write(output.toByteArray(), inputFileOs);
			inputFileOs.flush();
			inputFileOs.close();
			
			outputFile = File.createTempFile("tmp", fileName + "." + MimeType.PDF.getExtension());
			JODConverterUtils.process(inputFile, outputFile);
			resp.setContentType(MimeType.PDF.getName());
			if(doget) resp.setHeader("Content-disposition", "inline; filename=\"" + fileName + ".pdf\";");
			else resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
			AonIOUtils.copy(new FileInputStream(outputFile), resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		} finally {
			// TODO REMOVE AND CLOSE EVERYTHING
			if (inputFile != null && inputFile.canWrite()) inputFile.delete();
			if (outputFile != null && outputFile.canWrite()) outputFile.delete();
		}

	}

}
