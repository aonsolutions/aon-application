package com.esferalia.aon.gwt.template.server.imports;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.template.jooq.DBInvoice;
import com.esferalia.aon.gwt.template.server.exports.InvoiceExcelExport;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "DownloadInvoiceExcelTemplate", urlPatterns = { "/ms/api/downloadInvoiceExcel"
														,"/aon_gwt_template/downloadInvoiceExcel/*"
														,"/aon_gwt_aio/downloadInvoiceExcel/*"})
public class DownloadInvoiceTemplateServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638560042733639057L;
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	private static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {        
		JSONObject json = new JSONObject(decode(req.getParameter(IJsonNames.JSON).getBytes()));
		Integer domainId = JsonUtils.getInteger(json, IJsonNames.DOMAIN_ID);
		String domainName = JsonUtils.getString(json, IJsonNames.DOMAIN_NAME);
		String login = JsonUtils.getString(json, IJsonNames.DOMAIN_LOGIN);
		Domain domain = AON.getDomain(domainName, domainId, login);
		User user = AON.getUser(domainName, domainId, login);

		List<Invoice> invoices = DBInvoice.getInvoices(domain, user, f -> invoiceFilter(f, domainId, json))
				.stream().sorted(Comparator.comparing(Invoice::getIssueDate).thenComparing(Invoice::getId)).toList();
		ApplicationParameter param = AON.getApplicationParameter(domainName, domainId, login, AppParam.FS_DEFAULT_ADMINISTRATION);
		Administration administration = Administration.safeValueOf(AonNumberUtils.toInteger(param.getValue()));
		try {
			resp.setContentType("application/msexcel");
			resp.addHeader("Content-Disposition","attachment; filename=\"" + "Facturas.xls" +"\"");
			InvoiceExcelExport.getInstance().create(resp.getOutputStream(), invoices, 
				administration != null && administration.isCanarias() ? "IGIC" : "IVA");
			resp.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public static Filter invoiceFilter(InvoiceProperties f, Integer domainId, JSONObject json ) {
    	Filter filter =  f.getDomainProperty().eq(domainId);
    
		String description = JsonUtils.getString(json, IJsonNames.DESCRIPTION);
    	if(!AonStringUtils.isBlank(description)) {
    		filter = filter.and(
    			f.getReferenceCodeProperty().like("%" + description + "%")
    			.or(f.getRegistryNameProperty().like("%" + description + "%")));
    	}
    	String[] types = json.opt("type") != null ? json.optString("type").split(","): null;
    	if(types != null && types.length > 0) {
    		Filter filter2 = f.getTypeProperty().eq(InvoiceType.safeValueOf(types[0]).value())
    				.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(types[0].toUpperCase()).value()));
    		for(Integer i = 1; i < types.length; i++) {
    			filter2 = filter2.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(types[i]).value()))
   					.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(types[i].toUpperCase()).value()));
    		}
    		filter = filter.and(filter2); 
    	}
    	
    	Date from = JsonUtils.getDate(json, IJsonNames.FROM);
    	if(from != null) {
    		filter = filter.and(f.getStartIssueDateProperty().ge(from));
    	}
    	
    	Date to = JsonUtils.getDate(json, IJsonNames.TO);
    	if(to != null) {
    		filter = filter.and(f.getEndIssueDateProperty().le(to));
    	}
	
		List<Integer> ids = toList(json.optJSONArray("ids"));
		Integer[] idsArray = ids.toArray(new Integer[ids.size()]);
		if(idsArray.length > 0) {
			filter = filter.and(f.getIdProperty().in(idsArray));
		}
		
		return filter;
    }
	
	
	public static List<Integer> toList(JSONArray array) {
	    if(array==null || array.isEmpty())
	        return new LinkedList<>();
	    LinkedList<Integer> list = new LinkedList<>();
	    for(int i=0; i<array.length(); i++) {
	    	list.add(array.optInt(i));
	    }
	    return list;
	}
	
	public JSONObject getRequestJSON(HttpServletRequest req){
		String line = "";
		StringBuilder bld = new StringBuilder();
		try {
			while((line = req.getReader().readLine()) != null){
				bld.append(" " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String s = bld.toString();
		if(s == null || s.isBlank()){
			s = "{}";
		}
		return new JSONObject(s);
	}
	
	public String decode(byte[] value){
		String decode = "";
		try{
			decode = new String(Base64.getDecoder().decode(value), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decode;
	}
}
