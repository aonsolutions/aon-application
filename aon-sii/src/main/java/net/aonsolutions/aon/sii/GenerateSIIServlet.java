package net.aonsolutions.aon.sii;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "GenerateSIIServlet", urlPatterns = { "/generate_sii/*",
												 "/aon_gwt_aio/ms/generate_sii/*"})
public class GenerateSIIServlet extends HttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(GenerateSIIServlet.class.getName());

	public static GenerateSIIServlet getInstance() {
		return new GenerateSIIServlet();
	}
	
	public GenerateSIIServlet() {
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("SII Servlet - GET METHOD");
		
		String[] pathInfo = req.getPathInfo().split("/");
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(pathInfo[pathInfo.length-1]);
		String domainName = parameters.get("domain");
		String login = parameters.get("login");	
		
		String idStr = parameters.get("id");
		Integer[] ids = new Integer[1];
		ids[0] = Integer.parseInt(idStr);
	
		String action = parameters.get("action"); // consulta || suministro || anulacion
		String option = parameters.get("option"); 
		String terceros = parameters.get("terceros"); 

		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));		
		Company company = AON.getCompany(domain.getName(), domain.getId(), login,f -> f.getDomainProperty().eq(domain.getId()));
			
		AccountingReportParams params = new AccountingReportParams();
		params.setDomain(domain.getId());
		params.setInvoices(ids);
		LOGGER.info("GET SII VAT CONTEXT");
		LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(domain.getName(), domain.getId(), login, params, option)
				.collect(Collectors.toCollection(LinkedList::new));
		LOGGER.info("AFTER GET SII VAT CONTEXT");		
		LOGGER.info("SII. TAMAÑO VAT CONTEXT: " + contextList.size());

			
		ApplicationParameter param= AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_DEFAULT_ADMINISTRATION);
		Administration administration = param.getValue() != null ? Administration.values()[Integer.parseInt(param.getValue())] : Administration.COMMON_TERRITORY;
		try{
			SIIManager manager = SIIManager.getInstance(administration);

			byte[] object = null;
				
			if("cp_cobros".equals(option)){
//				LinkedList<Finance> financeList = AON.getSiiFinanceList(domain.getName(), domain.getId(), login,f -> f.getInvoiceProperty().in(ids));	
//				object = manager.suministroFacturasEmitidasCobros(domain, login, company, financeList, ids[0]);
			} else if("cp_pagos".equals(option)){
//				LinkedList<Finance> financeList = AON.getSiiFinanceList(domain.getName(), domain.getId(), login,f -> f.getInvoiceProperty().in(ids));	
//				object = manager.suministroFacturasRecibidasPagos(domain, login, company, financeList, ids[0]);
			} else if("intracomunitarias".equals(option)){
				String tipoOp = parameters.get("tipo_operacion");
				if(isSuministro(action)){
					object = manager.getSuministroOperacionesIntracomunitarias(domain, login, company, ids[0], contextList, tipoOp, terceros);
				}else if(isBaja(action)){
					//object = manager.bajaOperacionesIntracomunitarias(domain, login, company, ids[0], contextList, terceros);
				}
			} else if(option.contains("fe_")){
				if(isSuministro(action)){
					object = manager.getSuministroFacturasEmitidas(domain, login, company, ids[0], contextList, terceros);
				} else if(isBaja(action)){
//					object = manager.bajaFacturasEmitidas(domain, login, company, ids[0], contextList, terceros);
				}
			} else if(option.contains("fr_")){
				if(isSuministro(action)){
					object = manager.getSuministroFacturasRecibidas(domain, login, company, ids[0], contextList, terceros);
				} else if(isBaja(action)){
//					object = manager.bajaFacturasRecibidas(domain, login, company, ids[0], contextList, terceros);
				}
			} else if("bienes".equalsIgnoreCase(option)){
				if(isSuministro(action)){
					object = manager.getSuministroBienesInversion(domain, login, company, ids[0], contextList, terceros);
				} else if(isBaja(action)){
//					object = manager.bajaBienesInversion(domain, login, company, ids[0], contextList, terceros);
				}
			} else if("metalico".equalsIgnoreCase(option)){
//				if(isSuministro(action)){
//					object = manager.suministroCobrosMetalico(domain, login, company, ids[0], contextList, terceros);
//				} else if(isBaja(action)){
//					object = manager.bajaCobrosMetalico(domain, login, company, ids[0], contextList, terceros);
//				}
			} else if("seguros".equalsIgnoreCase(option)){
//				if(isSuministro(action)){
//					object = manager.suministroOperacionesSeguros(domain, login, company, ids[0], contextList, terceros);
//				} else if(isBaja(action)){
//					object = manager.bajaOperacionesSeguros(domain, login, company, ids[0], contextList, terceros);
//				}
			} else if("agencias".equalsIgnoreCase(option)){
//				if(isSuministro(action)){
//					object = manager.suministroAgenciasViajes(domain, login, company, ids[0], contextList, terceros);
//				} else if(isBaja(action)){
//					object = manager.bajaAgenciasViajes(domain, login, company, ids[0], contextList, terceros);
//				}
			}				
			
			if(object != null) {
				addCorsHeader(resp);
				resp.setContentType(MimeType.XML.getName());
				resp.setHeader("Content-disposition", "inline; filename=\"SII_" +(option != null? option.toUpperCase(): "DOWNLOAD") + ".xml\";");
				ByteArrayInputStream fileInpurOs =  new ByteArrayInputStream(object);
				AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
				resp.flushBuffer();
				fileInpurOs.close();
			}
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
			e.printStackTrace();
			JSONArray array = new JSONArray();
			JSONObject json = new JSONObject();
			json.put("id", 1);
			json.put("name", e.getLocalizedMessage());
			array.put(json);
			giveBack(req, resp, array, new JSONObject());
		}
	}
	
	 public static void addCorsHeader(HttpServletResponse response){
	        response.addHeader("Access-Control-Allow-Origin", "*");
	        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
	        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
	        response.addHeader("Access-Control-Max-Age", "1728000");
    }
	
	public static void giveBack(HttpServletRequest req, HttpServletResponse resp, Object object, JSONObject meta) {
		try {
			String js = req.getParameter("callback");
			if(js != null){
				resp.setContentType("application/javascript; charset=utf-8");     
				PrintWriter out = resp.getWriter();
				out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
				out.flush();
			} else {
				resp.setContentType("application/json");     
				PrintWriter out = resp.getWriter();
				out.print(object);
				out.flush();
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("SII Servlet - POST METHOD");
	}	
	
    public static Filter invoiceFilter(Domain domain, Map<String, String[]> filterMap, InvoiceProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
	
		if(filterMap.containsKey("id")){
			Integer id = Integer.parseInt(filterMap.get("id")[0]);
			filter = filter.and(f.getIdProperty().eq(id));
		}
		
		return filter;
    }
    
    private Boolean isSuministro(String value) {
    	return "suministro".equals(value);
	}
    
    private Boolean isBaja(String value) {
    	return "baja".equals(value);
	}
}
