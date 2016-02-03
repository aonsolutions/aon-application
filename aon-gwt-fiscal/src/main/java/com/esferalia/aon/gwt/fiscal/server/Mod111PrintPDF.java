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
@WebServlet(name = "Mod111 PDF Print", urlPatterns = { "/aon_gwt_fiscal/Model111PrintPDF" })
public class Mod111PrintPDF extends HttpServlet {

	private static final int DEFAULT_OFFICE_PORT = 2002;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		File inputFile = null;
		File outputFile = null;
		OfficeManager officeManager = null;
		try {
			int id = Integer.parseInt(req.getParameter("mod111"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = AonServletUtils.getLoggedUser();
			Mod111 mod111 = AON.getMod111(domainName, domainId, user, id);
			
			// ------------------------------------------ OBTAIN EXCEL REPORT
			FiscalModelExcelAction action = new FiscalModelExcelAction(mod111);
			action.initialize(mod111.getModel().getName(mod111.getAdministration(), mod111.getPeriod()));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			for (IModelScript ms : Model111ScriptProvider.obtainScript(mod111)) {
				action.accept(ms);
			}
			action.finalize(output);
			// --------------------------------------------------------------
			
			String modName = getPeriodName( mod111 );
			
			inputFile = File.createTempFile("tmp", modName + "." + MimeType.MS_EXCEL.getExtension());
			FileOutputStream inputFileOs = new FileOutputStream(inputFile);
			AonIOUtils.write(output.toByteArray(), inputFileOs);
			inputFileOs.flush();
			inputFileOs.close();
			
			outputFile = File.createTempFile("tmp", modName + "." + MimeType.PDF.getExtension());
			DocumentFormatRegistry formatRegistry = new DefaultDocumentFormatRegistry();
			officeManager = new DefaultOfficeManagerConfiguration()
					.setPortNumber(DEFAULT_OFFICE_PORT)
					.buildOfficeManager();
			officeManager.start();
			OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager, formatRegistry);
			converter.convert(inputFile, outputFile);

			resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + modName + ".pdf\";");
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
