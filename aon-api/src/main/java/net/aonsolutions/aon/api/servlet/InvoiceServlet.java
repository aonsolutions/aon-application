package net.aonsolutions.aon.api.servlet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.AccountProperties;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.json.TediInvoiceJSON;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.request.BidoqRequest;
import net.aonsolutions.aon.tedi.AonParser;
import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;

@SuppressWarnings("serial")
@WebServlet(name = "AonInvoiceServlet", urlPatterns = {"/ms/api/invoice/*"})
public class InvoiceServlet extends AonApiHttpServlet{
		
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
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getInvoiceObject(api));
				break;
			case "/accounts":
				response(req, resp, getAccountsObject(api));
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				response(req, resp, setInvoice(api.getDomain(), api.getUser().getLogin(), api.getData()));
				break;
			case "/selfconta":
				response(req, resp, setSelfcontaInvoice(api));
				break;
			case "/selfconta_import":
				response(req, resp, selfconta(api));
				break;
			case "/selfconta_record":
				response(req, resp, selfcontaRecord(api));
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - PUT METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				response(req, resp, setInvoice(api.getDomain(), api.getUser().getLogin(), api.getData()));
				break;
			case "/accept":
				response(req, resp, acceptInvoice(api));
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteInvoiceObject(api));
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private Object getInvoiceObject(AonApiData api) {
		if(api.getParams().opt(IConstants.ID) != null) {
			Integer id = api.getParams().optInt(IConstants.ID);
			return getInvoice(api.getDomain(), api.getUser().getLogin(), id);
		} else {
			InvoiceFilter filter = new InvoiceFilter()
					.setDescription(api.getParams().optString("description"))
					.setStatus(api.getParams().optString(IConstants.STATUS))
					.setTypes(api.getParams().opt(IConstants.TYPE) != null ? api.getParams().optString(IConstants.TYPE).split(","): null)
					.setPage(api.getParams().optInt("page"))
					.setPerPage(api.getParams().optInt("per_page"));
			return getInvoices(api.getDomain(), api.getUser().getLogin(), filter);
		}
	}
	
	private JSONArray getAccountsObject(AonApiData api) {
		JSONArray array = new JSONArray();
		String type = api.getParams().optString(IConstants.TYPE);
		ACCOUNTING.getAccounts(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> accountFilter(f, api.getDomain(), type)).forEach(acc -> {
			if(acc.getCode().length() > 5) {
				JSONObject json = new JSONObject();
				json.put("code", acc.getCode());
				json.put("name", acc.getDescription());
				array.put(json);
			}
		});
		return array;
	}

	private JSONObject deleteInvoiceObject(AonApiData api) {
		LinkedList<Integer> invoiceIds = toList(api.getData().optJSONArray(IConstants.ID));
		if(invoiceIds != null) {
			deleteInvoices(api.getDomain(), api.getUser().getLogin(), invoiceIds);
		}
		return new JSONObject();
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
    		//filter = filter.and(f.getStatusProperty().eq(st.value())); 
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
					JSONObject data = new JSONObject();
					data.put("domain_name", domain.getName());
					data.put("domain_id", domain.getId());
					data.put("id", r.getId());
					data.put("attach_type", AttachType.RAWDOC.getName());
					String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
					String url =  "ms/api/file/" +  result;
										
					JSONObject f = new JSONObject();
				    f.put("url", url);
				    f.put("content_type", r.getMimeType().getName());
				    json.put("file", f);
				}
				JSONArray log = new JSONArray(r.getLog() != null ? r.getLog() : "[]");
				json.put("comments", log);
				String reference = filter.getDescription() != null && json.opt("reference") != null ? json.optString("reference") : "";
				String registryName = RawdocType.OUTPUT.equals(r.getType()) 
						? (filter.getDescription() != null && json.opt("receiver") != null ? json.getJSONObject("receiver").optString("name") : "")
						: (filter.getDescription() != null && json.opt("sender") != null ? json.getJSONObject("sender").optString("name") : "");
						
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
	
	public static JSONObject acceptInvoice(AonApiData api) {
		return AON_SOLUTIONS.acceptInvoice(api.getDomain(), api.getUser(), api.getData());
	}
	
	public static JSONObject setInvoice(Domain domain, String login, JSONObject json) {
		JSONObject file = null;
		
		if(json.opt("file")!= null) { 
			file = json.optJSONObject("file");
			json = json.opt("invoice") != null ? json.optJSONObject("invoice") : json;
			if(json.opt("status") == null) {
				json = initInvoice();
			}
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
	
	private JSONObject setSelfcontaInvoice(AonApiData api) {
		BidoqRequest.selfconta2Aon(api.getDomain(), api.getUser(), api.getData());
		return new JSONObject();
	}
	
	private JSONObject selfconta(AonApiData api) throws JSONException, Exception {
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
		BidoqRequest.selfconta(api.getDomain(), api.getUser(), company.getDocument());
		return new JSONObject();
	}
	
	private JSONObject selfcontaRecord(AonApiData api) throws Exception {
		BidoqRequest.selfcontaRecord(api.getDomain(), api.getUser(), api.getData());
		return new JSONObject();
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
	
	private static JSONObject initInvoice() {
		JSONObject json = new JSONObject();
		json.put("type", "recibida");
		json.put("series", "");
		json.put("number", 0);
		json.put("reference", "");
		json.put("date", new Date());
		json.put("total", 0);
		json.put("sender", initRegistry());
		json.put("receiver", initRegistry());
		json.put("category", "");
		json.put("transaction", "NAC");
		json.put("taxes", new JSONArray());
		json.put("details", new JSONArray());
		json.put("finances", new JSONArray());
		json.put("suplidos", initSuplidos());
		json.put("status", "inbox");
		json.put("comments", new JSONArray());
		return json;
	}
	
	private static JSONObject initRegistry() {
		JSONObject json = new JSONObject();
		json.put("document", "");
		json.put("name", "");
		json.put("address", initAddress());			
		return json;
	}
	
	private static JSONObject initAddress() {
		JSONObject json = new JSONObject();
		json.put("country", "ES");
		json.put("address", "");
		json.put("zip", "");
		json.put("city", "");
		json.put("province", "");
		return json;
	}

	private static JSONObject initSuplidos() {
		JSONObject json = new JSONObject();
		json.put("active", false);
		json.put("description", "");
		json.put("total", 0);
		return json;
	}
}
