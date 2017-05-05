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

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.fiscal.server.JODConverterUtils;
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
		try {
			Integer id = Integer.parseInt(req.getParameter("schemaId"));
			String domainName = req.getParameter("domainName");
			Integer domainId = Integer.parseInt(req.getParameter("domainId"));
			String cif = req.getParameter("cif");
			String razonSocial = req.getParameter("razonSocial");
			Integer year = Integer.parseInt(req.getParameter("year"));
			String type = req.getParameter("type");
			String options = req.getParameter("options");
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
			if(options.substring(0, 1).equals("T")) action.IDA();
			if(options.substring(1, 2).equals("T")) action.BA();
			if(options.substring(2, 3).equals("T"))	action.PYG();
			if(options.substring(3, 4).equals("T")) action.ECPN();
			if(options.substring(4, 5).equals("T")) action.DM();
			if(options.substring(5, 6).equals("T")) action.AP1();
			if(options.substring(6, 7).equals("T")) action.AP2();
			if(options.substring(7, 8).equals("T")) action.AP3();
			if(options.substring(8, 9).equals("T"))	action.AP4();
			if(options.substring(9, 10).equals("T")) action.AP5();
			if(options.substring(10, 11).equals("T")) action.AP6();
			if(options.substring(11, 12).equals("T")) action.AP7();
			if(options.substring(12, 13).equals("T")) action.AP8();
			if(options.substring(13, 14).equals("T")) action.AP9();
			if(options.substring(14, 15).equals("T")) action.AP10();
			if(options.substring(15, 16).equals("T")) action.AP11();
			if(options.substring(16, 17).equals("T")) action.AP12();
			if(options.substring(17, 18).equals("T")) action.AP13();
			if(options.substring(18, 19).equals("T")) action.AP14();
			if(options.substring(19, 20).equals("T")) action.AP15();
			if(options.substring(20, 21).equals("T")) action.MA(); 
			if(options.substring(21, 22).equals("T")) action.IP();
			if(options.substring(22, 23).equals("T")) action.CHD();
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();


			action.finalize(output);
			
			String fileName = "CCAA";
			
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


	public Map<String, String> getSchema(HttpServletRequest req, D2Deposit d2Deposit, String user) {
		Esquema schema = DBConsults.getSchema(d2Deposit, user);
		Map<String, String> map = new HashMap<String, String>();
		map.put(D2DepositConstants.DEPOSIT_TYPE, schema.getCabecera().getTipoCuestionario());
		schema.getClaves().getClave().stream().forEach(clave ->{
			map.put(clave.getCodigo().toString(), clave.getValor());
		});
		return map;
	}
}
