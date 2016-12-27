package com.code.aon.webservice.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class ToJSON {
	
	public ToJSON() {
	
	}
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	public static JSONObject generalToJSON(String direction, String commercial, String segmentation, String observation, JSONArray rmedia, String status){
		JSONObject json = new JSONObject();
		json.put("direction", direction);
		json.put("commercial", commercial);
		json.put("segmentation", segmentation);
		json.put("observation", observation);
		json.put("rmedia", rmedia);
		json.put("status", status);
		return json;
	}
	
	public static JSONObject rmediaToJSON(RegistryMedia rmedia) {
		JSONObject json = new JSONObject();
		json.put("id", rmedia.getId());
		json.put("domain", rmedia.getDomain());
		json.put("registry", rmedia.getRegistry().getId());
		json.put("media", rmedia.getMedia());
		json.put("value", rmedia.getValue());
		json.put("comment", rmedia.getComment());
		json.put("administrative", rmedia.getAdministrative() == 1);
		json.put("commercial", rmedia.getCommercial() == 1);
		json.put("technical", rmedia.getTechnical() == 1);
		json.put("raddress", rmedia.getRaddress());
		json.put("icon", Icon.rmediaIcon(rmedia.getMedia()));
		return json;
	}
	
	public static JSONObject rnoteToJSON(RegistryNote rnote) {
		JSONObject json = new JSONObject();
		json.put("id", rnote.getId());
		json.put("domain", rnote.getDomain());
		json.put("registry", rnote.getRegistry());
		json.put("description", rnote.getDescription());
		json.put("note_date", dateFormat.format(rnote.getNoteDate()));
		json.put("comments", rnote.getComments());
		json.put("note_type", rnote.getNoteType());
		json.put("confidential", rnote.getSecurityLevel() == 1);
		return json;
	}
	
	public static JSONObject invoiceToJSON(Invoice invoice) {
		JSONObject json = new JSONObject();
		json.put("id", invoice.getId());
		json.put("domain", invoice.getDomain());
		json.put("registry", invoice.getRegistry());
		json.put("reference_code", invoice.getReferenceCode());
		return json;
	}
	
	public static JSONObject feeToJSON(Fee fee) {
		JSONObject json = new JSONObject();
		json.put("id", fee.getId());
		json.put("domain", fee.getDomain());
		json.put("customer", fee.getCustomer());
		json.put("description", fee.getDescription());
		json.put("start_date", fee.getStartDate() != null ?
				dateFormat.format(fee.getStartDate()) : "");
		json.put("end_date", fee.getEndDate() != null ?
				dateFormat.format(fee.getEndDate()) : "");
		json.put("billing_month", getMonth(fee.getBillingDate()));
		json.put("billing_year", AonDateUtils.getYear(fee.getBillingDate()));
		json.put("period", getPeriod(Period.values()[fee.getPeriod()]));
		return json;
	}

	public static JSONObject boughtProductToJSON(InvoiceDetail id) {
		JSONObject json = new JSONObject();
		json.put("id", id.getId());
		json.put("domain", id.getDomain());
		json.put("description", id.getDescription());
		json.put("name", id.getItem().getName());
		json.put("quantity", id.getQuantity());
		json.put("price", id.getPrice());
		json.put("discount", Double.parseDouble(id.getDiscountExpression()));
		json.put("date", dateFormat.format(id.getInvoice().getIssueDate()));
		json.put("code", id.getItem().getCode());
		json.put("total", AonMathUtils.round(id.getQuantity()*id.getPrice() * ((Double.parseDouble(id.getDiscountExpression())/100) + 1)));
		json.put("reference_code", id.getInvoice().getReferenceCode());
		return json;
	}

	public static JSONObject projectCommercialToJSON(ProjectCommercial project, Registry registry, Registry seller) {
		JSONObject json = new JSONObject();
		json.put("id", project.getId());
		json.put("domain", project.getDomain());
		json.put("name", project.getName());
		json.put("alias", project.getAlias());
		json.put("registry", registryToJSON(registry));
		json.put("seller", registryToJSON(seller));
		json.put("date", dateFormat.format(project.getDate()));
		json.put("project_type", project.getProjectTypeId());
		json.put("comment", project.getComments());
		return json;
	}
	
	public static JSONObject commercialTrackingToJSON(CommercialTracking ct, Registry seller) {
		JSONObject json = new JSONObject();
		json.put("id", ct.getId());
		json.put("domain", ct.getDomain());
		json.put("date", dateFormat.format(ct.getDate()));
		json.put("seller", registryToJSON(seller));
		json.put("comment", ct.getComments());
		return json;
	}

	public static JSONObject registryToJSON(Registry registry) {
		JSONObject json = new JSONObject();
		json.put("id", registry.getId());
		json.put("name", registry.getName());
		return json;
	}
	
	public static String getPeriod(Period period){
		if(period.equals(Period.YEAR))
			return "Anual";
		else if(period.equals(Period.T1) || period.equals(Period.T2) || period.equals(Period.T3) || period.equals(Period.T4))
			return "Trimestral";
		else return "Mensual";
	}
	
	public static String getMonth(Date date){
		switch (AonDateUtils.getMonth(date)){
		case 0 : return "Enero";
		case 1 : return "Febrero";
		case 2 : return "Marzo";
		case 3 : return "Abril";
		case 4 : return "Mayo";
		case 5 : return "Junio";
		case 6 : return "Julio";
		case 7 : return "Agosto";
		case 8 : return "Septiembre";
		case 9 : return "Octubre";
		case 10 : return "Noviembre";
		default : return "Diciembre";
		}
	}
}
