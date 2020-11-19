package net.aonsolutions.aon.api.servlet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import es.translogia.tedi.json.TediInvoiceJSON;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.json.AonInvoiceJSON;
import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;

@SuppressWarnings("serial")
@WebServlet(name = "AonInvoiceServlet", urlPatterns = {"/ms/api/invoice/*"})
public class InvoiceServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(InvoiceServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");
		Domain domain = AON.getDomain(domainName, domainId, "");
		
		String status = req.getParameter(IConstants.STATUS);
		String[] types = req.getParameter(IConstants.TYPE) != null ? req.getParameter(IConstants.TYPE).split(","): null;
		
		System.out.println(status);
		System.out.println(types);
		
		JSONArray jsArray = getInvoices(domain, "api", status, types);
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, jsArray, new JSONObject());	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API INVOICE SERVLET - POST METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");	
		Domain domain = AON.getDomain(domainName, domainId, "");
		User user = AON_SOLUTIONS.getUser(domain, token);
		JSONObject json = Utils.getRequestJSON(req);
//		Company company = AON.getCompany(domainName, domainId, user.getLogin(), f -> f.getDomainProperty().eq(domainId));
		
		json = setInvoice(domain, user.getLogin(), json);
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, json, new JSONObject());	
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API INVOICE SERVLET - DELETE METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");	
		Domain domain = AON.getDomain(domainName, domainId, "");
		JSONObject json = Utils.getRequestJSON(req);
		LinkedList<Integer> invoiceIds = toList(json.optJSONArray(IConstants.ID));
		if(invoiceIds != null) {
			deleteInvoices(domain, "api", invoiceIds);
		}
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, new JSONObject(), new JSONObject());	
	}
	
	public static LinkedList<Integer> toList(JSONArray array) {
	    if(array==null || array.isEmpty())
	        return new LinkedList<>();
	    LinkedList<Integer> list = new LinkedList<>();
	    for(int i=0; i<array.length(); i++) {
	    	list.add(array.optInt(i));
	    }
	    return list;
	}
	
	public static Filter invoiceFilter(InvoiceProperties f, Integer domainId, String status, String[] types) {
    	Filter filter =  f.getDomainProperty().eq(domainId);
    	
    	InvoiceStatus st = getInvoiceStatus(status);
    	if(st != null) {
    		filter = filter.and(f.getStatusProperty().eq(st.value())); 
    	}
    	
    	if(types != null && types.length > 0) {
    		Filter filter2 = f.getTypeProperty().eq(InvoiceType.safeValueOf(types[0]).value());
    		for(Integer i = 1; i < types.length; i++) {
    			filter2 = filter2.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(types[i]).value()));
    		}
    		filter = filter.and(filter2); 
    	}
    	
		return filter;
    }

	private static JSONArray getInvoices(Domain domain, String login, String status, String[] types) {
		JSONArray jsArray = new JSONArray();
		if(isContabilizada(status)) {
			Company company = AON.getCompany(domain.getName(), domain.getId(), "api", f -> f.getDomainProperty().eq(domain.getId()));
			AON_SOLUTIONS.getInvoices(domain.getName(), domain.getId(), "api", f -> invoiceFilter(f, domain.getId(), status, types))
				.forEach(invoice -> jsArray.put(AonInvoiceJSON.toJSON(invoice, company)));
		} else {
			RawdocStatus rs = getRawdocStatus(status);

			AON.getRawdocStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
					.and(f.getStatusProperty().eq(rs.value())))
			.forEach(r -> { 

				JSONObject json = new JSONObject(r.getJson());
				json.put("id", r.getId());
				if(r.getMimeType() != null){
					JSONObject f = new JSONObject();
				    String str = "domain="+ domain.getId() + "&id=" + r.getId() + "&attach_type=data";
				    String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
				    String url =  "ms/download_rawdoc/"  + domain.getName() + "/" + login + "/" +  result;
				    f.put("url", url);
				    f.put("type", r.getMimeType().getName());
				    json.put("file", f);
				    json.put("file", f);	
				}

				jsArray.put(json);
			});
		}
		return jsArray;
	}
	
	private void deleteInvoices(Domain domain, String login, LinkedList<Integer> ids) {
		Integer[] idsArray = ids.toArray(new Integer[ids.size()]);
		AON.rawdocDelete(domain.getName(), domain.getId(), login, 
			f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getIdProperty().in(idsArray)));
	}
	
	private static JSONObject setInvoice(Domain domain, String login, JSONObject json) {
		JSONObject file = null;
		
		if(json.opt("file")!= null) { 
			file = json.opt("invoice") != null ? json.optJSONObject("file") : null;
			json = json.opt("invoice") != null ? json.optJSONObject("invoice"): json;
		}
		Integer id = json.opt("id") !=null ? json.optInt("id") : null;
		RawdocStatus status = getRawdocStatus(json.optString("status")); 
		RawdocType type = json.opt("type") != null && json.optString("type").equalsIgnoreCase("emitida") 
				? RawdocType.OUTPUT : RawdocType.INPUT;

		Rawdoc rawdoc = new Rawdoc()
				.setId(id)
				.setDomain(domain.getId())
				.setNature(RawdocNature.INVOICE)
				.setType(type)
				.setStatus(status);
		
		if(file != null) {
			String base64 = file.optString("content");
			String contentType = file.optString("contentType");
			byte[] fileData = Base64.getDecoder().decode(base64);
			rawdoc.setData(fileData)
				.setMimeType(MimeType.get(contentType));

			// TEDI PARSER!!!		    
		    
		    InputStream input = new ByteArrayInputStream(fileData);
		    TediContext tctx = new TediContext()
		    		.setDomainName(domain.getName())
		    		.setDomain(domain.getId())
		    		.setUser(login);
			try {
		    	TediResult r = TEDI.parse(tctx, input);
		    	json = TediInvoiceJSON.toJSON(r.getTedi());
			} catch (TediException e) {
				e.printStackTrace();
			}
		}
    	rawdoc.setJson(json.toString());
		
		rawdoc = AON.rawdocSave(domain.getName(), domain.getId(), login, rawdoc);
		json.put("id", rawdoc.getId());
		return json;
	}
	
	public static File getFile(Drive drive, String id){
		File file  = new File();
		try {
			file = drive.files().get(id).setFields("webContentLink, webViewLink").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private static InvoiceStatus getInvoiceStatus(String status) {
		InvoiceStatus st = InvoiceStatus.safeValueOf(status);
		if(st == null) {
			if("inbox".equals(status) || "pending".equals(status)
					|| "verified".equals(status)) {
				st = InvoiceStatus.PENDING;
			} else if("accepted".equals(status)) {
				st = InvoiceStatus.SCORED;
			} else if("refused".equals(status)) {
				st = InvoiceStatus.REFUSED; 
			} else if("trash".equals(status)) {
				st = InvoiceStatus.TRASH;
			}
		}
		return st;
	}
	
	private static RawdocStatus getRawdocStatus(String status) {
		RawdocStatus st = RawdocStatus.safeValueOf(status);
		if(st == null) {
			if("refused".equalsIgnoreCase(status)) {
				st = RawdocStatus.REJECTED; 
			} else if("trash".equalsIgnoreCase(status)) {
				st = RawdocStatus.DRAFT;
			} else st = RawdocStatus.INBOX;
		}
		return st;
	}
	
	private static Boolean isContabilizada(String status) {
		return "accounting".equalsIgnoreCase(status);
	}

}
