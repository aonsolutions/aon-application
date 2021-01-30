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

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.AccountProperties;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.finance.Invoice;
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
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.json.TediInvoiceJSON;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.tedi.AonParser;
import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;

@SuppressWarnings("serial")
@WebServlet(name = "AonInvoiceServlet", urlPatterns = {"/ms/api/invoice/*"})
public class InvoiceServlet extends HttpServlet{
		
	private class InvoiceFilter {
		String description;
		String status;
		String[] types;
		Integer page;
		Integer perPage;
		 
		public InvoiceFilter() {
			// TODO Auto-generated constructor stub
		}

		public String getDescription() {
			return description;
		}

		public InvoiceFilter setDescription(String description) {
			this.description = description;
			return this;
		}

		public String getStatus() {
			return status;
		}

		public InvoiceFilter setStatus(String status) {
			this.status = status;
			return this;
		}

		public String[] getTypes() {
			return types;
		}

		public InvoiceFilter setTypes(String[] types) {
			this.types = types;
			return this;
		}

		public Integer getPage() {
			return page;
		}

		public InvoiceFilter setPage(Integer page) {
			this.page = page;
			return this;
		}

		public Integer getPerPage() {
			return perPage;
		}

		public InvoiceFilter setPerPage(Integer perPage) {
			this.perPage = perPage;
			return this;
		}
		
		
	}
	
	private static final Logger LOGGER  = Logger.getLogger(InvoiceServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");
		Domain domain = AON.getDomain(domainName, domainId, "");
		Object object = new JSONObject();

		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
		if(pathInfo != null) {
			if("accounts".equalsIgnoreCase(pathInfo[1])) {
				JSONArray array = new JSONArray();
				String type = req.getParameter(IConstants.TYPE);
				ACCOUNTING.getAccounts(domain.getName(), domain.getId(), "", f -> accountFilter(f, domain, type)).forEach(acc -> {
					if(acc.getCode().length() > 5) {
						JSONObject json = new JSONObject();
						json.put("code", acc.getCode());
						json.put("name", acc.getDescription());
						array.put(json);
					}
				});
				object = array;
			}
		} else {
			if(req.getParameter(IConstants.ID) != null) {
				Integer id = AonNumberUtils.toInteger(req.getParameter(IConstants.ID));
				object = getInvoice(domain, "api", id);
			} else {
				InvoiceFilter filter = new InvoiceFilter()
						.setDescription(req.getParameter("description"))
						.setStatus(req.getParameter(IConstants.STATUS))
						.setTypes(req.getParameter(IConstants.TYPE) != null ? req.getParameter(IConstants.TYPE).split(","): null)
						.setPage(AonNumberUtils.toInteger(req.getParameter("page")))
						.setPerPage(AonNumberUtils.toInteger(req.getParameter("per_page")));
				object = getInvoices(domain, "api", filter);
			}
		}

		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, object, new JSONObject());	
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
	
	public static Filter invoiceFilter(InvoiceProperties f, Integer domainId, InvoiceFilter invoiceFilter) {
    	Filter filter =  f.getDomainProperty().eq(domainId);
    	
    	InvoiceStatus st = getInvoiceStatus(invoiceFilter.getStatus());
    	if(st != null) {
    		filter = filter.and(f.getStatusProperty().eq(st.value())); 
    	}
    	if(invoiceFilter.getDescription() != null) {
    		filter = filter.and(
    				f.getReferenceCodeProperty().like("%" + invoiceFilter.getDescription() + "%")
    				.or(f.getRegistryNameProperty().like("%" + invoiceFilter.getDescription() + "%")));
    	}

    	if(invoiceFilter.getTypes() != null && invoiceFilter.getTypes().length > 0) {
    		Filter filter2 = f.getTypeProperty().eq(InvoiceType.safeValueOf(invoiceFilter.getTypes()[0]).value());
    		for(Integer i = 1; i < invoiceFilter.getTypes().length; i++) {
    			filter2 = filter2.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(invoiceFilter.getTypes()[i]).value()));
    		}
    		filter = filter.and(filter2); 
    	}
    	
    	if(invoiceFilter.getPage() != null) {
    		filter.page(invoiceFilter.getPage());
    	} 
    	
    	if(invoiceFilter.getPerPage() != null) {
    		filter.perPage(invoiceFilter.getPerPage());
    	}
    	
		return filter;
    }
	
	public static Filter accountFilter(AccountProperties f, Domain domain, String type) {
		Integer[] domains = { domain.getId(), domain.getParentId() };
		Filter filter =  f.getDomainProperty().in(domains);

    	InvoiceType iType = InvoiceType.safeValueOf(type);
    	if(iType != null && InvoiceType.SALES.equals(iType)) {
    		filter = filter.and(f.getCodeProperty().like("700%").or(f.getCodeProperty().like("705%"))); 
    	}
    	if(iType != null && InvoiceType.PURCHASE.equals(iType)) {
    		filter = filter.and(f.getCodeProperty().like("60%").or(f.getCodeProperty().like("62%"))); 
    	}
    	if(iType != null && (InvoiceType.EXPENSES.equals(iType) || InvoiceType.UNDEDUCTIBLE.equals(iType))) {
    		filter = filter.and(f.getCodeProperty().like("629%")); 
    	}
    	
		return filter;
    }

	private static JSONObject getInvoice(Domain domain, String login, Integer id) {
		AonParser parser = new AonParser();
		TediInvoice invoice = parser.aon2Tedi(domain, login, id);
		return TediInvoiceJSON.toJSON(invoice);
	}
	
	private static JSONArray getInvoices(Domain domain, String login, InvoiceFilter filter) {
		JSONArray jsArray = new JSONArray();
		if(isContabilizada(filter.getStatus())) {
			//Company company = AON.getCompany(domain.getName(), domain.getId(), "api", f -> f.getDomainProperty().eq(domain.getId()));
			AON_SOLUTIONS.getInvoices(domain.getName(), domain.getId(), "api", f -> invoiceFilter(f, domain.getId(), filter))
				.forEach(invoice -> jsArray.put(invoiceList2JSON(invoice)));
		
		} else {
			RawdocStatus rs = getRawdocStatus(filter.getStatus());

			AON.getRawdocStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
					.and(f.getStatusProperty().eq(rs.value())))
			.forEach(r -> { 

				JSONObject json = new JSONObject(r.getJson());
				json.put("id", r.getId());
				json.put("status", getRawdocStatus(r.getStatus()));
				if(r.getMimeType() != null){
					JSONObject f = new JSONObject();
				    String str = "domain="+ domain.getId() + "&id=" + r.getId() + "&attach_type=data";
				    String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
				    String url =  "ms/download_rawdoc/"  + domain.getName() + "/" + login + "/" +  result;
				    f.put("url", url);
				    f.put("type", r.getMimeType().getName());
				    json.put("file", f);
				}
				JSONArray log = new JSONArray(r.getLog() != null ? r.getLog() : "[]");
				json.put("comments", log);

				String reference = filter.getDescription() != null && json.opt("reference") != null ? json.getString("reference") : "";
				String registryName = RawdocType.OUTPUT.equals(r.getType()) 
						? (filter.getDescription() != null && json.opt("receiver") != null ? json.getJSONObject("receiver").getString("name") : "")
						: (filter.getDescription() != null && json.opt("sender") != null ? json.getJSONObject("sender").getString("name") : "");
						
				if(filter.getDescription() == null || (filter.getDescription() != null && 
						(AonStringUtils.containsIgnoreCase(reference, filter.getDescription()) 
								|| AonStringUtils.containsIgnoreCase(registryName, filter.getDescription())))) {
					jsArray.put(json);
				}
			});
		}
		return jsArray;
	}
	
	private static JSONObject invoiceList2JSON(Invoice invoice) {
		JSONObject json = new JSONObject();
		json.put("id", invoice.getId());
		json.put("date", invoice.getIssueDate());
		json.put("reference", invoice.getReferenceCode());
		json.put("name", invoice.getRegistryName());
		json.put("total", invoice.getTotal());
		return json;
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
		    	TediResult r = TEDI.parse(tctx, input, MimeType.get(contentType));
		    	json = TediInvoiceJSON.toJSON(r.getTedi());
			} catch (TediException e) {
				e.printStackTrace();
			}
		}
    	rawdoc.setJson(json.toString());
    	rawdoc.setLog(json.opt("comments") != null? json.optJSONArray("comments").toString(): "[]");
		
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
			if("inbox".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)
					|| "verified".equalsIgnoreCase(status)) {
				st = InvoiceStatus.PENDING;
			} else if("accepted".equalsIgnoreCase(status) || "scored".equalsIgnoreCase(status) || "accounting".equalsIgnoreCase(status)) {
				st = InvoiceStatus.SCORED;
			} else if("refused".equalsIgnoreCase(status) || "rejected".equalsIgnoreCase(status)) {
				st = InvoiceStatus.REFUSED; 
			} else if("trash".equalsIgnoreCase(status) || "draft".equalsIgnoreCase(status)) {
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
	
	
	private static String getRawdocStatus(RawdocStatus status) {
		if(RawdocStatus.REJECTED.equals(status)) {
			return "refused";
		} else if(RawdocStatus.DRAFT.equals(status)) {
			return "trash";
		} else return "inbox";
	}
	
	private static Boolean isContabilizada(String status) {
		return "accounting".equalsIgnoreCase(status);
	}

}
