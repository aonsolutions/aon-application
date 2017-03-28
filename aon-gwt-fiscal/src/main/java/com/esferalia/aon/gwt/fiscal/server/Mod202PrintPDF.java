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

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.mod202.Model202ScriptProvider;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod202 PDF Print", urlPatterns = { "/aon_gwt_fiscal/Model202PrintPDF" })
public class Mod202PrintPDF extends HttpServlet {

	private static final int DEFAULT_OFFICE_PORT = 2002;

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
		OfficeManager officeManager = null;
		try {
			int id = Integer.parseInt(req.getParameter("mod202"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = AonServletUtils.getLoggedUser();
			Mod202 mod202 = FISCAL.getMod202(domainName, domainId, user, id);
			
			// ------------------------------------------ OBTAIN EXCEL REPORT
			Mod202ExcelAction action = new Mod202ExcelAction(mod202);
			action.initialize(mod202.getModel().getName(mod202.getAdministration(), mod202.getPeriod()));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			for (IModelScript<Mod202Key> ms : Model202ScriptProvider.obtainScript(mod202)) {
				action.accept(ms);
			}
			action.beforeFinalize();
			action.finalize(output);
			// --------------------------------------------------------------
			
			String fileName = AonFiscalFileUtils.getFileName(mod202);
			
			inputFile = File.createTempFile("tmp", fileName + "." + MimeType.MS_EXCEL.getExtension());
			FileOutputStream inputFileOs = new FileOutputStream(inputFile);
			AonIOUtils.write(output.toByteArray(), inputFileOs);
			inputFileOs.flush();
			inputFileOs.close();
			
			outputFile = File.createTempFile("tmp", fileName + "." + MimeType.PDF.getExtension());
			DocumentFormatRegistry formatRegistry = new DefaultDocumentFormatRegistry();
			officeManager = new DefaultOfficeManagerConfiguration()
					.setPortNumber(DEFAULT_OFFICE_PORT)
					.buildOfficeManager();
			officeManager.start();
			OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager, formatRegistry);
			converter.convert(inputFile, outputFile);

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
			if (officeManager != null) officeManager.stop();
		}

	}

}
