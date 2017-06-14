package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

import java.io.ByteArrayInputStream;
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
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryItem;
import com.esferalia.aon.gwt.fiscal.server.JODConverterUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "CCAAPrint", urlPatterns = { "/aon_gwt_deposit/CCAAPrint" })
public class CCAAPrint extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			Integer id = Integer.parseInt(req.getParameter("schemaId"));
			String domainName = req.getParameter("domainName");
			Integer domainId = Integer.parseInt(req.getParameter("domainId"));
			String cif = req.getParameter("cif");
			String razonSocial = req.getParameter("razonSocial");
			Integer year = Integer.parseInt(req.getParameter("year"));
			String type = req.getParameter("type");
			String options = req.getParameter("options");
			String format = req.getParameter("format");
			Boolean isMemory = req.getParameter("isMemory").equalsIgnoreCase("true");
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
			Integer index = 0;
			if(options.substring(index, index+1).equals("T")) action.IDA();index++;
			if(year >= 2016){
				if(options.substring(index, index+1).equals("T")) action.AP3();index++;
			}
			if(options.substring(index, index+1).equals("T")) action.BA();index++;
			if(options.substring(index, index+1).equals("T"))	action.PYG();index++;
			if(year < 2016){
				if(options.substring(index, index+1).equals("T")) action.ECPN();index++;
			}
			if(options.substring(index, index+1).equals("T")) action.DM();index++;
			if(!isMemory){
				for(Integer pos = 0; pos < MemoryItem.getInstance().getApartadosSize(year); pos++){	
					if(options.substring(index, index+1).equals("T")) actionMemory(action, year, pos);index++;
				}
			}
			if(options.substring(index, index+1).equals("T")) action.MA();index++;
			if(options.substring(index, index+1).equals("T")) action.IP();index++;
			if(options.substring(index, index+1).equals("T")) action.CHD();
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			action.finalize(output);
			String fileName = "CCAA";
			
			if("pdf".equals(format)){ // PRINT PDF
				Boolean doget = req.getAttribute("doget") != null;
				File inputFile = null;
				File outputFile = null;
				try {
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
				} finally {
					if (inputFile != null && inputFile.canWrite()) inputFile.delete();
					if (outputFile != null && outputFile.canWrite()) outputFile.delete();
				}
			} else { // PRINT EXCEL
				ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
				resp.setContentType(MimeType.MS_EXCEL.getName());
				resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL.getExtension()+ "\";");
				AonIOUtils.copy(in, resp.getOutputStream());
				resp.flushBuffer();
			}
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}
	
	public void actionMemory(CCAAExcelAction action, Integer year, Integer pos){
		switch (MemoryItem.getInstance().apartadosName.get(year)[pos]) {
		case MemoryItem.ACTIVIDAD_EMPRESA: action.AP1();break;
		case MemoryItem.BASES_PRESENTACION: action.AP2();break;	
		case MemoryItem.APLICACION_RESULTADOS: action.AP3();break;
		case MemoryItem.NORMAS_REGISTRO: action.AP4();break;
		case MemoryItem.INMOVILIZADO: action.AP5();break;
		case MemoryItem.ACTIVOS_FINANCIEROS: action.AP6();break;
		case MemoryItem.PASIVOS_FINANCIEROS: action.AP7();break;
		case MemoryItem.FONDOS_PROPIOS: action.AP8();break;
		case MemoryItem.SITUACION_FISCAL: action.AP9();break;
		case MemoryItem.INGRESOS_GASTOS: action.AP10();break;
		case MemoryItem.SUBVENCIONES: action.AP11();break;
		case MemoryItem.PARTES_VINCULANTES: action.AP12();break;
		case MemoryItem.OTRA_INFORMACION: action.AP13();break;
		case MemoryItem.MEDIOAMBIENTE: action.AP14();break;
		case MemoryItem.APLAZAMIENTOS: action.AP15();break;
		default: break; 
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
