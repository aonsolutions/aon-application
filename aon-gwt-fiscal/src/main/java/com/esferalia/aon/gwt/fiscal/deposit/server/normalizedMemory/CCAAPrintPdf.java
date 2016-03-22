package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "CCAAPrintPdf", urlPatterns = { "/aon_gwt_deposit/CCAAPrintPdf" })
public class CCAAPrintPdf extends HttpServlet {

	private static final String D2_DEPOSIT_SCHEMA = "d2DepositSchema";
	private static final int DEFAULT_OFFICE_PORT = 2002;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Boolean doget = req.getAttribute("doget") != null;
		File inputFile = null;
		File outputFile = null;
		OfficeManager officeManager = null;
		try {
			Integer id = Integer.parseInt(req.getParameter("schemaId"));
			String domainName = req.getParameter("domainName");
			Integer domainId = Integer.parseInt(req.getParameter("domainId"));
			String cif = req.getParameter("cif");
			String razonSocial = req.getParameter("razonSocial");
			Integer year = Integer.parseInt(req.getParameter("year"));
			String type = req.getParameter("type");
			String user = AonServletUtils.getLoggedUser();

			Domain domain = new Domain().setName(domainName).setId(domainId);
			D2Deposit d2Deposit = new D2Deposit(domain, cif, razonSocial).setId(id).setYear(year).setType(type);
			d2Deposit.setMap(getSchema(req, d2Deposit, user));

			CCAAExcelAction action = new CCAAExcelAction(d2Deposit) {
				@Override protected String getTitle() {
					return "Depósito de cuentas anuales";
				}
			};
			action.initialize();
			action.IDA();
			action.BA();
			action.PYG();
			action.ECPN();
			action.DM();
			action.AP1();
			action.AP2();
			action.AP3();
			action.AP4();
			action.AP5();
			action.AP6();
			action.AP7();
			action.AP8();
			action.AP9();
			action.AP10();
			action.AP11();
			action.AP12();
			action.AP13();
			action.AP14();
			action.AP15();
			action.MA();
			action.IP();
			action.CHD();
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();


			action.finalize(output);
			
			String fileName = "CCAA";
			
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


	public Map<String, String> getSchema(HttpServletRequest req, D2Deposit d2Deposit, String user) {
		Esquema schema = (Esquema) req.getSession().getAttribute(D2_DEPOSIT_SCHEMA + d2Deposit.getCif() + d2Deposit.getYear());
		if(schema == null){
			schema = DBConsults.getSchema(d2Deposit, user);
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put(D2DepositConstants.DEPOSIT_TYPE, d2Deposit.getType());
		schema.getClaves().getClave().stream().forEach(clave ->{
			map.put(clave.getCodigo().toString(), clave.getValor());
		});
		return map;
	}
}
