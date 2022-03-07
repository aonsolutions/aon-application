package com.esferalia.aon.gwt.template.server.imports;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.template.jooq.DBInvoice;
import com.esferalia.aon.gwt.template.server.exports.InvoiceExcelExport;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.security.User;

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
			
		List<Integer> ids = toList(json.optJSONArray("ids"));
		Integer[] idsArray = ids.toArray(new Integer[ids.size()]);
		
		List<Invoice> invoices = DBInvoice.getInvoices(domain, user, f -> f.getIdProperty().in(idsArray));
		try {
			resp.setContentType("application/msexcel");
			resp.addHeader("Content-Disposition","attachment; filename=\"" + "Facturas.xls" +"\"");
			InvoiceExcelExport.getInstance().create(resp.getOutputStream(), invoices);
			resp.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
