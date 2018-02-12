package com.code.aon.webservice.commercial;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.InvoiceDetailCommissionProperties;
import com.esferalia.aon.occam.api.model.Properties.OfferDetailCommissionProperties;
import com.esferalia.aon.occam.api.model.commission.InvoiceDetailCommission;
import com.esferalia.aon.occam.api.model.commission.InvoiceDetailCommissionStatus;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommission;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommissionStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "CommissionServlet", urlPatterns = {"/commission/*",
												   "/aon_gwt_aio/commission/*",
												   "/aon_gwt_commercial/commission/*"})
public class CommissionServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CommissionServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Common Servlet - GET METHOD");
		String domainName = req.getServerName();
		Integer domainId = Integer.parseInt(req.getParameter(MSG.DOMAIN));
		String userName = req.getRemoteUser();
		Domain domain = AON.getDomain(domainName, domainId, userName);
	
		Object object = new Object();
		JSONObject meta = new JSONObject();
		
		switch (req.getPathInfo()) {
		case "/calculated/offer":
			object = getOfferCalculatedCommission(domain, userName, req.getParameterMap());
			break;
		case "/calculated/invoice":
			object = getInvoiceCalculatedCommission(domain, userName, req.getParameterMap());
			break;
		default:
			break;
		}
		Utils.giveBack(req, resp, object, meta);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Common Servlet - POST METHOD");

		JSONObject json = Utils.getRequestJSON(req);

		String domainName = req.getServerName();
		Integer domainId = Integer.parseInt(req.getParameter(MSG.DOMAIN));
		String userName = req.getRemoteUser();
		Domain domain = AON.getDomain(domainName, domainId, userName);
		
		Object object = new Object();
		switch (req.getPathInfo()) {
		case "/calculated/offer":
			object = updateOfferCalculatedCommission(domain, userName, json);
			break;
		case "/calculated/invoice":
			object = updateInvoiceCalculatedCommission(domain, userName, json);
			break;
		default:
			break;
		}
		resp.setContentType("application/json;charset=UTF-8");
		Utils.addCorsHeader(resp);
		PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
		os.println(object.toString());
		os.flush();
		os.close();
	}
	
	private Object getOfferCalculatedCommission(Domain domain, String userName,  Map<String,String[]> filterMap){
		JSONArray array = new JSONArray();
		AON.getOfferDetailCommissionStream(domain.getName(), domain.getId(), userName, 
				f -> offerCalculatedCommissionFilter(domain, filterMap, f))
		.forEach(r -> {
			JSONObject json = new JSONObject();
			json.put("id", r.getId());
			json.put("date", r.getOfferDetail().getOffer().getIssueDate());
			json.put("seller", r.getOfferDetail().getOffer().getSeller().getRegistryName());	
			Double p = r.getOfferDetail().getPrice()* r.getOfferDetail().getQuantity();
			Double discount = Double.parseDouble(r.getOfferDetail().getDiscountExpression());
			json.put("base", p - (p*discount/100));
			json.put("amount", r.getAmount());
			json.put("percentage", r.getCommission());
			json.put("status", r.getStatus().getName());
			json.put("product", r.getOfferDetail().getDescription());
			
			String series = r.getOfferDetail().getOffer().getSeries();
			Integer number = r.getOfferDetail().getOffer().getNumber();
			Integer version = r.getOfferDetail().getOffer().getVersion();
			 
			json.put("description", series + "/" + ceros(number.toString(),6) + "/" + version);
			array.put(json);
		});
		return array;
	}
	
	public Object updateOfferCalculatedCommission(Domain domain, String userName, JSONObject json) {
		Integer id = json.getInt("id");
		OfferDetailCommission odc = AON.getOfferDetailCommission(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(id));
		if(json.opt("amount") != null) {
			odc.setAmount(json.getDouble("amount"));
		}
		
		if(json.opt("percentage") != null) {
			odc.setCommission(json.getDouble("percentage"));
		}
		
		if(json.opt("status") != null) {
			odc.setStatus(OfferDetailCommissionStatus.values()[json.getInt("status")]);
		}

		AON.updateOfferDetailCommission(domain.getName(), domain.getId(), userName, odc);
		
		JSONObject odcJson = new JSONObject();
		odcJson.put("id", odc.getId());
		odcJson.put("date", odc.getOfferDetail().getOffer().getIssueDate());
		odcJson.put("seller", odc.getOfferDetail().getOffer().getSeller().getRegistryName());	
		Double p = odc.getOfferDetail().getPrice()* odc.getOfferDetail().getQuantity();
		Double discount = Double.parseDouble(odc.getOfferDetail().getDiscountExpression());
		odcJson.put("base", p - (p*discount/100));
		odcJson.put("amount", odc.getAmount());
		odcJson.put("percentage", odc.getCommission());
		odcJson.put("status", odc.getStatus().getName());
		odcJson.put("product", odc.getOfferDetail().getDescription());
		
		String series = odc.getOfferDetail().getOffer().getSeries();
		Integer number = odc.getOfferDetail().getOffer().getNumber();
		Integer version = odc.getOfferDetail().getOffer().getVersion();
		 
		odcJson.put("description", series + "/" + ceros(number.toString(),6) + "/" + version);
		
		return odcJson;
	}
	
	public Object updateInvoiceCalculatedCommission(Domain domain, String userName, JSONObject json) {
		Integer id = json.getInt("id");
		InvoiceDetailCommission idc = AON.getInvoiceDetailCommission(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(id));
		if(json.opt("amount") != null) {
			idc.setAmount(json.getDouble("amount"));
		}
		
		if(json.opt("percentage") != null) {
			idc.setCommission(json.getDouble("percentage"));
		}
		
		if(json.opt("status") != null) {
			idc.setStatus(InvoiceDetailCommissionStatus.values()[json.getInt("status")]);
		}

		AON.updateInvoiceDetailCommission(domain.getName(), domain.getId(), userName, idc);
		
		JSONObject idcJson = new JSONObject();
		idcJson.put("id", idc.getId());
		idcJson.put("date", idc.getInvoiceDetail().getInvoice().getIssueDate());
		idcJson.put("seller", idc.getInvoiceDetail().getInvoice().getSellerName());	
		Double p = idc.getInvoiceDetail().getPrice()* idc.getInvoiceDetail().getQuantity();
		Double discount = Double.parseDouble(idc.getInvoiceDetail().getDiscountExpression());
		idcJson.put("base", p - (p*discount/100));
		idcJson.put("amount", idc.getAmount());
		idcJson.put("percentage", idc.getCommission());
		idcJson.put("status", idc.getStatus().getName());
		idcJson.put("product", idc.getInvoiceDetail().getDescription());
		
		String series = idc.getInvoiceDetail().getInvoice().getSeries();
		Integer number = idc.getInvoiceDetail().getInvoice().getNumber();
		 
		idcJson.put("description", series + "/" + ceros(number.toString(),6));
		
		return idcJson;
	}
	
	public static Filter offerCalculatedCommissionFilter(Domain domain, Map<String, String[]> filterMap, OfferDetailCommissionProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
	
		if(filterMap.containsKey("from")){
			String from = filterMap.get(MSG.FROM)[0];
			Date d = new Date(Long.parseLong(from));
			Filter fDate = f.getDateProperty().ge(AonDateUtils.toSql(d));
			filter = filter.and(fDate);
		}
		
		if(filterMap.containsKey("to")){
			String to = filterMap.get(MSG.TO)[0];
			Date d = new Date(Long.parseLong(to));
			Filter fDate = f.getDateProperty().le(AonDateUtils.toSql(d));
			filter = filter.and(fDate);
		}
		
		if(filterMap.containsKey("series")){
			String serie = filterMap.get(MSG.SERIES)[0];			
			Filter fSerie = f.getSerieProperty().eq(serie);
			filter = filter.and(fSerie);
		}
		
		if(filterMap.containsKey("number_from")){
			try { 
				Integer n = Integer.parseInt(filterMap.get("number_from")[0]);
				Filter fnumber = f.getNumberProperty().ge(n);
				filter = filter.and(fnumber);
			} catch (Exception e) {}
		} 
		
		if(filterMap.containsKey("number_to")){
			try {
				Integer n = Integer.parseInt(filterMap.get("number_to")[0]);
				Filter fnumber = f.getNumberProperty().le(n);
				filter = filter.and(fnumber);
			} catch (Exception e) {}
		} 
		
		if(filterMap.containsKey("type")){
			try {
				Integer n = Integer.parseInt(filterMap.get("type")[0]);
				Filter ftype = f.getTypeProperty().eq(n.byteValue());
				filter = filter.and(ftype);
			} catch (Exception e) {}
		} 
		
		if(filterMap.containsKey("seller")){
			try {
				Integer n = Integer.parseInt(filterMap.get("seller")[0]);
				Filter fseller= f.getSellerProperty().eq(n);
				filter = filter.and(fseller);
			}catch (Exception e) {}
		} 
		
		if(filterMap.containsKey("target")){
			try {
				Integer n = Integer.parseInt(filterMap.get("target")[0]);
				Filter ftarget = f.getTargetProperty().eq(n);
				filter = filter.and(ftarget);
			}catch (Exception e) {}
		} 

		if(filterMap.containsKey("supplier")){
			try {
				Integer n = Integer.parseInt(filterMap.get("supplier")[0]);
				Filter fsupplier= f.getSupplierProperty().eq(n);
				filter = filter.and(fsupplier);
			} catch (Exception e) {}
		}

		return filter;
	}

	private Object getInvoiceCalculatedCommission(Domain domain, String userName,  Map<String,String[]> filterMap){
		JSONArray array = new JSONArray();
		AON.getInvoiceDetailCommissionStream(domain.getName(), domain.getId(), userName, 
				f -> invoiceCalculatedCommissionFilter(domain, filterMap, f))
		.forEach(r -> {
			JSONObject json = new JSONObject();
			json.put("id", r.getId());
			json.put("date", r.getInvoiceDetail().getInvoice().getIssueDate());
			json.put("seller", r.getInvoiceDetail().getInvoice().getSellerName());	
			Double p = r.getInvoiceDetail().getPrice()* r.getInvoiceDetail().getQuantity();
			Double discount = Double.parseDouble(r.getInvoiceDetail().getDiscountExpression());
			json.put("base", p - (p*discount/100));
			json.put("amount", r.getAmount());
			json.put("percentage", r.getCommission());
			json.put("status", r.getStatus().getName());
			json.put("product", r.getInvoiceDetail().getDescription());
			
			String series = r.getInvoiceDetail().getInvoice().getSeries();
			Integer number = r.getInvoiceDetail().getInvoice().getNumber();
					 
			json.put("description", series + "/" + ceros(number.toString(),6));
			array.put(json);
		});
		return array;
	}
	
	public static Filter invoiceCalculatedCommissionFilter(Domain domain, Map<String, String[]> filterMap, InvoiceDetailCommissionProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
	
		if(filterMap.containsKey("from")){
			String from = filterMap.get(MSG.FROM)[0];
			Date d = new Date(Long.parseLong(from));
			Filter fDate = f.getDateProperty().ge(AonDateUtils.toSql(d));
			filter = filter.and(fDate);
		}
		
		if(filterMap.containsKey("to")){
			String to = filterMap.get(MSG.TO)[0];
			Date d = new Date(Long.parseLong(to));
			Filter fDate = f.getDateProperty().le(AonDateUtils.toSql(d));
			filter = filter.and(fDate);
		}
		
		if(filterMap.containsKey("series")){
			String serie = filterMap.get(MSG.SERIES)[0];			
			Filter fSerie = f.getSeriesProperty().eq(serie);
			filter = filter.and(fSerie);
		}	
		
		if(filterMap.containsKey("number_from")){
			try { 
				Integer n = Integer.parseInt(filterMap.get("number_from")[0]);
				Filter fnumber = f.getNumberProperty().ge(n);
				filter = filter.and(fnumber);
			} catch (Exception e) {}
		} 
		
		if(filterMap.containsKey("number_to")){
			try {
				Integer n = Integer.parseInt(filterMap.get("number_to")[0]);
				Filter fnumber = f.getNumberProperty().le(n);
				filter = filter.and(fnumber);
			} catch (Exception e) {}
		} 
		
		if(filterMap.containsKey("type")){
			try {
				Integer n = Integer.parseInt(filterMap.get("type")[0]);
				Filter ftype = f.getTypeProperty().eq(n.byteValue());
				filter = filter.and(ftype);
			} catch (Exception e) {}
		} 
		
		if(filterMap.containsKey("seller")){
			try {
				Integer n = Integer.parseInt(filterMap.get("seller")[0]);
				Filter fseller= f.getSellerProperty().eq(n);
				filter = filter.and(fseller);
			}catch (Exception e) {}
		} 
		
		if(filterMap.containsKey("registry")){
			try {
				Integer n = Integer.parseInt(filterMap.get("registry")[0]);
				Filter fregistry= f.getRegistryProperty().eq(n);
				filter = filter.and(fregistry);
			} catch (Exception e) {}
		}

		return filter;
	}
	
	private String ceros(String str, Integer ceros) {
		if(ceros > str.length()) {
			String sc = "";
			Integer size = ceros - str.length(); 
			for(Integer i = 0 ; i < size; i++) {
				sc = sc + "0";
			}
			return sc + str;
		}
		return str;
	}
}
