package net.aonsolutions.aon.sii;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyStore;
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
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

@SuppressWarnings("serial")
@WebServlet(name = "SIIServlet22", urlPatterns = { "/newsii/*",
												 "/aon_gwt_aio/ms/newsii/*"})
public class SIINewServlet extends HttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(SIINewServlet.class.getName());

	public static SIINewServlet getInstance() {
		return new SIINewServlet();
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
				
		if(ids.length > 0) {
			Invoice invoice = AON.getInvoice(domain.getName(), domain.getId(), login, ids[0]);//AON_SOLUTIONS.getInvoice(domain.getName(), domain.getId(), login, ids[0]);	
			
			LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(domain.getName(), domain.getId(), login, params, option)
					.collect(Collectors.toCollection(LinkedList::new));
			
			Integer cert = Integer.parseInt(parameters.get("cert"));
			String pass = parameters.get("pass");
			
			Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(cert)
					.and(f.getTypeProperty().eq(RegistryAttachmentType.DIGITAL_CERTIFICATE.value())), AttachType.REGISTRY, true);

			if(attach.getData() == null){
				DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), login);
				Drive drive = AonDrive.getInstace().serviceInitialize(g);
				attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
			}
			
			if(AonStringUtils.isBlank(pass)) {
				pass = attach.getDescription().split("HIDE\\(")[1].split("\\)")[0];
			}
			
			SiiConfiguration siiConfiguration = AON.getSiiConfiguration(domain, login);
			siiConfiguration.setCertificate(new Certificate()
					.setData(attach.getData())
					.setPassword(pass)
					.setType(CertificateType.AEAT.name()));
			try{
				SIIManager manager = SIIManager.getInstance(siiConfiguration);

				Object object = new Object();
				
				if("cp_cobros".equals(option)){
					LinkedList<Finance> financeList = AON.getSiiFinanceList(domain.getName(), domain.getId(), login,f -> f.getInvoiceProperty().in(ids));	
					object = manager.suministroFacturasEmitidasCobros(domain, login, company, financeList, ids[0]);
				} else if("cp_pagos".equals(option)){
					LinkedList<Finance> financeList = AON.getSiiFinanceList(domain.getName(), domain.getId(), login,f -> f.getInvoiceProperty().in(ids));	
					object = manager.suministroFacturasRecibidasPagos(domain, login, company, financeList, ids[0]);
				} else if("intracomunitarias".equals(option)) {
					String tipoOp = parameters.get("tipo_operacion");
					if(isSuministro(action)) {
						object = manager.suministroOperacionesIntracomunitarias(domain, login, company, ids[0], contextList, tipoOp, terceros);
					} else if(isBaja(action)){
						object = manager.bajaOperacionesIntracomunitarias(domain, login, company, ids[0], contextList, terceros);
					}
				} else if(option.contains("fe_") || option.contains("fr_")){
					if(isSuministro(action) && invoice.isSales()){
						object = manager.suministroFacturasEmitidas(domain, login, company, ids[0], contextList, terceros);
					} else if(isBaja(action) && invoice.isSales() ){
						object = manager.bajaFacturasEmitidas(domain, login, company, ids[0], contextList, terceros);
					} else if(isSuministro(action) && !invoice.isSales()){
						object = manager.suministroFacturasRecibidas(domain, login, company, ids[0], contextList, terceros, false);
					} else if(isBaja(action) && !invoice.isSales()){
						object = manager.bajaFacturasRecibidas(domain, login, company, ids[0], contextList, terceros);
					}
				} else if("bienes".equalsIgnoreCase(option)){
					if(isSuministro(action)){
						object = manager.suministroBienesInversion(domain, login, company, ids[0], contextList, terceros);
					} else if(isBaja(action)){
						object = manager.bajaBienesInversion(domain, login, company, ids[0], contextList, terceros);
					}
				} else if("metalico".equalsIgnoreCase(option)){
					if(isSuministro(action)){
						object = manager.suministroCobrosMetalico(domain, login, company, ids[0], contextList, terceros);
					} else if(isBaja(action)){
						object = manager.bajaCobrosMetalico(domain, login, company, ids[0], contextList, terceros);
					}
				} else if("seguros".equalsIgnoreCase(option)){
					if(isSuministro(action)){
						object = manager.suministroOperacionesSeguros(domain, login, company, ids[0], contextList, terceros);
					} else if(isBaja(action)){
						object = manager.bajaOperacionesSeguros(domain, login, company, ids[0], contextList, terceros);
					}
				} else if("agencias".equalsIgnoreCase(option)){
					if(isSuministro(action)){
						object = manager.suministroAgenciasViajes(domain, login, company, ids[0], contextList, terceros);
					} else if(isBaja(action)){
						object = manager.bajaAgenciasViajes(domain, login, company, ids[0], contextList, terceros);
					}
				}
				saveCertificate(domain, siiConfiguration.getCertificate(), attach);
				giveBack(req, resp, object, new JSONObject());
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
	}
	
	private void saveCertificate(Domain domain, Certificate certificate, Attach attach) {
		if(!attach.getDescription().contains("HIDE") && checkCert(attach.getData(), certificate.getPassword())) {
			String desc = attach.getDescription() + "HIDE(" + certificate.getPassword() + ")";
			if(desc.length() >= 64) {
				Integer len = desc.length() - 64;
				desc = attach.getDescription().subSequence(0, attach.getDescription().length() - len) + "HIDE(" + certificate.getPassword() + ")";
			}
			attach.setDescription(desc);
		} else if(attach.getDescription().contains("HIDE")){
			String password = attach.getDescription().split("HIDE\\(")[1].split("\\)")[0];
			Integer index = attach.getDescription().indexOf("HIDE");
			if(!certificate.getPassword().equals(password)) {
				if(checkCert(attach.getData(), certificate.getPassword())) {
					attach.setDescription(attach.getDescription().substring(0, index) + "HIDE(" + certificate.getPassword() + ")");
				} else if(!checkCert(attach.getData(), password)) {
					attach.setDescription(attach.getDescription().substring(0, index));
				}
			} else if(!checkCert(attach.getData(), password)) {
				attach.setDescription(attach.getDescription().substring(0, index));
			}
		}

		AON.updateAttach(domain.getName(), domain.getId(), "", attach);
	}

	public static boolean checkCert(byte[] cert, String password) {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(cert);
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(is, password.toCharArray());
			return true;
		} catch (Exception e) {
			return false;
		}
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
