package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryItem;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "CCAAPrint", urlPatterns = { "/aon_gwt_deposit/CCAAPrint",
												"/aon_gwt_aio/CCAAPrint"})
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
			Boolean doget = req.getAttribute("doget") != null;
			String user = AonServletUtils.getLoggedUser();

			Boolean isPdf = "pdf".equals(format);
			Domain domain = new Domain().setName(domainName).setId(domainId);
			D2Deposit d2Deposit = new D2Deposit(domain, cif, razonSocial).setId(id).setYear(year).setType(type);

			AonData aonData = new AonData().setDomain(d2Deposit.getDomain()).setUser(new User().setLogin(user));
		
			d2Deposit.setMap(getSchema(aonData, year));
			
			if(isPdf){ // PRINT PDF
				pdf(d2Deposit, options, isMemory, doget, resp);
			} else { // PRINT EXCEL
				excel(d2Deposit, options, isMemory, resp);
			}
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}
	
	private void pdf(D2Deposit d2Deposit, String options, Boolean isMemory, Boolean doget, HttpServletResponse resp) throws IOException {
		CCAAPdfAction action = new CCAAPdfAction(d2Deposit) {
			@Override protected String getTitle() {
				return "Depósito de cuentas anuales";
			}
		};
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		action.initialize(output, options, isMemory);
		
		String fileName = "CCAA";
		
		ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
		resp.setContentType(MimeType.PDF.getName());
		
		ServletOutputStream a = resp.getOutputStream();
		if(doget)resp.setHeader("Content-disposition", "inline; filename=\"" + fileName + ".pdf\";");
		else resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
		AonIOUtils.copy(in, resp.getOutputStream());
		resp.flushBuffer();
	}

	private void excel(D2Deposit d2Deposit, String options, Boolean isMemory, HttpServletResponse resp) throws IOException {
		CCAAExcelAction action = new CCAAExcelAction(d2Deposit) {
			@Override protected String getTitle() {
				return "Depósito de cuentas anuales";
			}
		};

		action.initialize();
		Integer index = 0;
		if(options.substring(index, index+1).equals("T")) action.IDA();index++;
		if(d2Deposit.getYear() >= 2016){
			if(options.substring(index, index+1).equals("T")) action.AP3();index++;
		}
		if(options.substring(index, index+1).equals("T")) action.BA();index++;
		if(options.substring(index, index+1).equals("T"))	action.PYG();index++;
		if(d2Deposit.getYear() < 2016){
			if(options.substring(index, index+1).equals("T")) action.ECPN();index++;
		}
		if(options.substring(index, index+1).equals("T")) action.DM();index++;
		if(!isMemory){
			for(Integer pos = 0; pos < MemoryItem.getInstance().getApartadosSize(d2Deposit.getYear()); pos++){	
				if(options.substring(index, index+1).equals("T")) actionMemory(action, d2Deposit.getYear(), pos);index++;
			}
		}
		if(options.substring(index, index+1).equals("T")) action.MA();index++;
		if(options.substring(index, index+1).equals("T")) action.IP();index++;
		if(options.substring(index, index+1).equals("T")) action.CHD();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		action.finalize(output);
		String fileName = "CCAA";
		
		ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
		resp.setContentType(MimeType.MS_EXCEL.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL.getExtension()+ "\";");
		AonIOUtils.copy(in, resp.getOutputStream());
		resp.flushBuffer();
	}
	
	public void actionMemory(CCAAExcelAction excel, Integer year, Integer pos){
		switch (MemoryItem.getInstance().getApartadoName2(year, pos)) {
		case MemoryItem.ACTIVIDAD_EMPRESA: excel.AP1();break;
		case MemoryItem.BASES_PRESENTACION: excel.AP2();break;	
		case MemoryItem.APLICACION_RESULTADOS: excel.AP3();break;
		case MemoryItem.NORMAS_REGISTRO: excel.AP4();break;
		case MemoryItem.INMOVILIZADO: excel.AP5();break;
		case MemoryItem.ACTIVOS_FINANCIEROS: excel.AP6();break;
		case MemoryItem.PASIVOS_FINANCIEROS: excel.AP7();break;
		case MemoryItem.FONDOS_PROPIOS: excel.AP8();break;
		case MemoryItem.SITUACION_FISCAL: excel.AP9();break;
		case MemoryItem.INGRESOS_GASTOS: excel.AP10();break;
		case MemoryItem.SUBVENCIONES: excel.AP11();break;
		case MemoryItem.PARTES_VINCULANTES: excel.AP12();break;
		case MemoryItem.OTRA_INFORMACION: excel.AP13();break;
		case MemoryItem.MEDIOAMBIENTE: excel.AP14();break;
		case MemoryItem.APLAZAMIENTOS: excel.AP15();break;
		default: break; 
		}
	}

	public Map<String, String> getSchema(AonData aonData, Integer year) {
		Esquema schema = NormalizedMemoryServlet.getInstance().getSchema(aonData, year);
		Map<String, String> map = new HashMap<String, String>();
		map.put(D2DepositConstants.DEPOSIT_TYPE, schema.getCabecera().getTipoCuestionario());
		schema.getClaves().getClave().stream().forEach(clave ->{
			map.put(clave.getCodigo().toString(), clave.getValor());
		});
		return map;
	}
}
