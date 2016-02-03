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
import com.esferalia.aon.gwt.fiscal.client.mod111.Model110Bizkaia;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model110Gipuzkoa;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111AEAT;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Araba;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Araba2016;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Base.IModelScript;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Bizkaia;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111Gipuzkoa;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model715Navarra;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model745Navarra;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;

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
			for (IModelScript ms : obtainScript(mod111)) {
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
		String s = fs.getName();
		StringBuilder sb = new StringBuilder();
		if (!Character.isJavaIdentifierStart(s.charAt(0))) {
			sb.append("_");
		}
		for (char c : s.toCharArray()) {
			if (Character.isJavaIdentifierPart(c)) {
				sb.append(c);
			}
		}
		return  "Mod" + fs.getModel().getName(fs.getAdministration(), fs.getPeriod()) 
				+ "_" + fs.getYear() 
				+ "_" + fs.getPeriod().getName() 
				+ "_" + fs.getAdministration().toString() 
				+ "_" + sb.toString();
	}

	private IModelScript[] obtainScript(Mod111 mod111) {
		IModelScript[] ms = null;
		if (mod111.getAdministration() == Administration.COMMON_TERRITORY) {
			ms = Model111AEAT.ModelScript.values();
		} else if (mod111.getAdministration() == Administration.GIPUZKOA) {
			if (mod111.getPeriod().isQuarterPeriod()) {
				ms = Model110Gipuzkoa.ModelScript.values();
			} else {
				ms = Model111Gipuzkoa.ModelScript.values();
			}
		} else if (mod111.getAdministration() == Administration.BIZKAIA) {
			if (mod111.getPeriod().isQuarterPeriod()) {
				ms = Model110Bizkaia.ModelScript.values();
			} else {
				ms = Model111Bizkaia.ModelScript.values();
			}
		} else if (mod111.getAdministration() == Administration.NAVARRA) {
			if (mod111.getPeriod().isQuarterPeriod()) {
				ms = Model715Navarra.ModelScript.values();
			} else {
				ms = Model745Navarra.ModelScript.values();
			}
		} else if (mod111.getAdministration() == Administration.ALAVA) {
			if (mod111.getYear() > 2015) {
				ms = Model111Araba2016.ModelScript.values();
			} else {
				ms = Model111Araba.ModelScript.values();
			}
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod111.getAdministration().toString() + " "
							+ mod111.getYear() + " " + mod111.getPeriod().getDescription());
		}
		return ms;
	}

}
