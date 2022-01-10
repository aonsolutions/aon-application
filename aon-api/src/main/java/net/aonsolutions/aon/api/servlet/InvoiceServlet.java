package net.aonsolutions.aon.api.servlet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceSeriesJSON;
import com.esferalia.aon.occam.api.json.invoice.PrintInvoiceConfigurationJSON;
import com.esferalia.aon.occam.api.json.invoice.SiiConfigurationJSON;
import com.esferalia.aon.occam.api.json.invoice.TbaiConfigurationJSON;
import com.esferalia.aon.occam.api.model.AccountProperties;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MaritalStatus;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import es.translogia.tedi.json.TediInvoiceJSON;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.request.BidoqRequest;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.tbai.TbaiMain;
import net.aonsolutions.aon.tbai.exceptions.TbaiException;
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
			case "/series":
				response(req, resp, getInvoiceSeries(api));
				break;
			case "/print_configuration":
				response(req, resp, getPrintConfiguration(api));
				break;
			case "/configuration":
				response(req, resp, getConfiguration(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
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
				response(req, resp, setInvoice(api));
				break;
			case "/print_configuration":
				response(req, resp, savePrintConfiguration(api, api.getData()));
				break;
			case "/configuration":
				response(req, resp, saveConfiguration(api));
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
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
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
				response(req, resp, setInvoice(api));
				break;
			case "/accept":
				response(req, resp, acceptInvoice(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
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
				response(req, resp, deleteInvoice(api));
				break;
			case "/rawdoc":
				response(req, resp, deleteInvoiceObject(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
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
	
	private JSONArray getInvoiceSeries(AonApiData api) {
		return InvoiceSeriesJSON.toJSON(AON.getInvoiceSalesSeries(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin()));
	}

	private JSONObject deleteInvoiceObject(AonApiData api) {
		List<Integer> invoiceIds = toList(api.getData().optJSONArray(IConstants.ID));
		if(!invoiceIds.isEmpty()) {
			deleteInvoices(api.getDomain(), api.getUser().getLogin(), invoiceIds);
		}
		return new JSONObject();
	}
	
	private JSONObject deleteInvoice(AonApiData api) {
		Integer invoiceId = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		Invoice invoice = AON_SOLUTIONS.getInvoice(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoiceId);
		
		Attach attach = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
			f.getAttachModuleProperty().eq(invoiceId)
			.and(f.getTypeProperty().eq(InvoiceAttachmentType.INVOICE.value()))
			, AttachType.INVOICE, true);		
		
		Rawdoc rawdoc = new Rawdoc()
				.setData(attach.getData())
				.setDomain(invoice.getDomain())
				.setJson(InvoiceJSON.toJSON(invoice).toString())
				.setMimeType(attach.getMimeType())
				.setNature(RawdocNature.INVOICE)
				.setStatus(RawdocStatus.DRAFT)
				.setType(invoice.isPurchase() ? RawdocType.INPUT : RawdocType.OUTPUT);
		AON.deleteInvoice(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoiceId);
		AON.rawdocSave(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), rawdoc);
		return new JSONObject();
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
    		Filter filter2 = f.getTypeProperty().eq(InvoiceType.safeValueOf(invoiceFilter.getTypes()[0]).value())
    				.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(invoiceFilter.getTypes()[0].toUpperCase()).value()));
    		for(Integer i = 1; i < invoiceFilter.getTypes().length; i++) {
    			filter2 = filter2.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(invoiceFilter.getTypes()[i]).value()))
    					.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(invoiceFilter.getTypes()[i].toUpperCase()).value()));
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
		Attach a = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getAttachModuleProperty().eq(id), AttachType.INVOICE);
		Invoice invoice = AON_SOLUTIONS.getInvoice(domain.getName(), domain.getId(), login, id);
		JSONObject json = InvoiceJSON.toJSON(invoice);

		TbaiConfiguration tbai = AON.getTbaiConfiguration(domain, login);
		if(tbai.isActive()) {	
			String tbaiUrl = TbaiData.getTbaiUrl(domain.getName(), domain.getId(), login, invoice.getId());
			if(!AonStringUtils.isBlank(tbaiUrl)) {
				json.put("tbai", true);
				json.put("tbaiUrl", tbaiUrl);
			}
		}
		if(a != null && a.getId() != null) {
			JSONObject data = new JSONObject();
			data.put("domain_name", domain.getName());
			data.put("domain_id", domain.getId());
			data.put("id", a.getId());
			data.put("attach_type", AttachType.INVOICE.getName());
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			String url =  "ms/api/file/" +  result;
								
			JSONObject f = new JSONObject();
		    f.put("url", url);
		    f.put("content_type", a.getMimeType().getName());
		    json.put("file", f);
		}
		return json;
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
				json.put("remarks", log);
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
		json.put("status", InvoiceStatus.safeValueOf(invoice.getStatus()));
		return json;
	}
	
	private void deleteInvoices(Domain domain, String login, List<Integer> ids) {
		Integer[] idsArray = ids.toArray(new Integer[ids.size()]);
		AON.rawdocDelete(domain.getName(), domain.getId(), login, 
			f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getIdProperty().in(idsArray)));
	}
	
	public static JSONObject acceptInvoice(AonApiData api) throws JAXBException, ParserConfigurationException, SAXException, IOException, TbaiException {
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(api.getDomain(), api.getUser());
		Invoice invoice = InvoiceJSON.fromJSON(api.getData());
		if(invoice.isSales() && tbaiConfiguration.isActive()) {
			invoice.setIssueDate(new Date());
			tbaiConfiguration.setCertificate(checkCertificate(api));
		}
		invoice = AON_SOLUTIONS.acceptInvoice(api.getDomain(), api.getUser(), invoice);
		acceptTbai(tbaiConfiguration, company, invoice);
		JSONObject json = InvoiceJSON.toJSON(invoice);
		if(invoice.isSales() && tbaiConfiguration.isActive()) {	
			String tbaiUrl = TbaiData.getTbaiUrl(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoice.getId());
			if(!AonStringUtils.isBlank(tbaiUrl)) {
				json.put("tbai", true);
				json.put("tbaiUrl", tbaiUrl);
			}
		}
		return json;
	}
	
	public static void acceptTbai(TbaiConfiguration tbaiConfiguration, Company company,  Invoice invoice) throws JAXBException, ParserConfigurationException, SAXException, IOException, TbaiException {
		if(invoice.isSales() && tbaiConfiguration.isActive()) {
			TbaiMain tbai = new TbaiMain();
			tbai.createEmisionTBAI(company, invoice, tbaiConfiguration);
		}
	}
	
	private static Certificate checkCertificate(AonApiData api) {
		Certificate cert = new Certificate();
		try {
			cert =  AON.getCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), CertificateType.AEAT.name());
		} catch (Exception e) {
			throw new AonApiException("Error al obtener el certificado.");
		}
		try {
			if(!checkCert(cert.getCertificate(), cert.getPassword())) {
				throw new AonApiException("El certificado o la contraseña no son correctos.");
			}
		} catch (Exception e) {
			throw new AonApiException("El certificado o la contraseña no son correctos.");			
		}		
		if(cert.isEmpty()) {
			throw new AonApiException("El certificado no existe.");
		}
		return cert;
		
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
	
	public static JSONObject setInvoice(AonApiData api) {
		Domain domain = api.getDomain();
		String login = api.getUser().getLogin();
		JSONObject json = api.getData();
		JSONObject file = null;
		
		if(json.opt("file")!= null && json.opt("invoice") != null) { 
			file = json.optJSONObject("file");
			json.remove("file");
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
		    if(api.getDur().isOcr()) {
		    	InputStream input = new ByteArrayInputStream(fileData);
		    	TediContext tctx = new TediContext()
		    		.setDomainName(domain.getName())
		    		.setDomain(domain.getId())
		    		.setUser(login);
		    	try {
		    		TediResult r = TEDI.parse(tctx, input, MimeType.get(contentType));
		    		json = tediParse(TediInvoiceJSON.toJSON(r.getTedi()), json);
		    	} catch (TediException e) {
		    		e.printStackTrace();
		    	}
		    }
		}
    	rawdoc.setJson(json.toString());
    	rawdoc.setLog(json.opt("remarks") != null? json.optJSONArray("remarks").toString(): "[]");

		rawdoc = AON.rawdocSave(domain.getName(), domain.getId(), login, rawdoc);
		json.put("id", rawdoc.getId()); 
		if(rawdoc.getMimeType() != null){
			JSONObject data = new JSONObject();
			data.put("domain_name", domain.getName());
			data.put("domain_id", domain.getId());
			data.put("id", rawdoc.getId());
			data.put("attach_type", AttachType.RAWDOC.getName());
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			String url =  "ms/api/file/" +  result;
								
			JSONObject f = new JSONObject();
		    f.put("url", url);
		    f.put("content_type", rawdoc.getMimeType().getName());
		    json.put("file", f);
		}
		return json;
	}
	
	private static JSONObject tediParse(JSONObject tedi, JSONObject json) {
		tedi.keySet().stream().forEach(key -> {
			json.put(key, tedi.get(key));
		});
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
	
	private JSONObject getConfiguration(AonApiData api) {
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		JSONObject json = new JSONObject();
		json.put("print", getPrintConfiguration(api));
		json.put("company", CompanyJSON.toJSON(company));
		json.put(IJsonNames.E_INVOICE, company.iseInvoice());
		json.put("tbai", getTbaiConfiguration(api));
		json.put("sii", getSiiConfiguration(api));
		json.put(IJsonNames.ADMINISTRATION, getAdministration(api));
		return json;
	}
	
	private JSONObject saveConfiguration(AonApiData api) {
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		company.seteInvoice(JsonUtils.getboolean(api.getData(), IJsonNames.E_INVOICE));
		JSONObject cJson = api.getData().getJSONObject(IJsonNames.COMPANY);
		if(cJson.opt(IJsonNames.PERSON) != null) {
			JSONObject pJson = cJson.getJSONObject(IJsonNames.PERSON);
			String name = JsonUtils.getString(pJson, IJsonNames.NAME);
			String surname1 = JsonUtils.getString(pJson, IJsonNames.SURNAME + "1");
			String surname2 = JsonUtils.getString(pJson, IJsonNames.SURNAME + "2");
			Person person = AON.getPerson(api.getDomain(), api.getUser().getLogin(), f -> f.getIdProperty().eq(company.getId()));
			person.setFirstName(name);
			person.setFirstSurname(surname1);
			person.setSecondSurname(surname2);
			person.setDomain(company.getDomain());
			person.setGender(Gender.UNKNOWN);
			person.setMaritalStatus(MaritalStatus.UNKNOWN);
			person.setId(company.getId());
			AON.savePerson(api.getDomain(), api.getUser().getLogin(), person);
		}
		AON.saveCompany(api.getDomain(), api.getUser(), company);

		Administration administration = saveAdministration(api, JsonUtils.getString(api.getData(), IJsonNames.ADMINISTRATION));

		JSONObject print = savePrintConfiguration(api, api.getData().getJSONObject("print"));
		JSONObject tbai = saveTbaiConfiguration(api, api.getData().getJSONObject("tbai"));
		JSONObject sii = saveSiiConfiguration(api, api.getData().getJSONObject("sii"));
		return new JSONObject()
			.put("administration", administration.name())	
			.put("print", print)
			.put("tbai", tbai)
			.put("sii", sii)
			.put(IJsonNames.E_INVOICE, company.iseInvoice());
	}
	
	private JSONObject getPrintConfiguration(AonApiData api) {
		PrintInvoiceConfiguration pic = AON_SOLUTIONS.getPrintInvoiceConfiguration(api.getDomain(), api.getUser(), false);
		return PrintInvoiceConfigurationJSON.toJSON(pic);
	}

	private Administration getAdministration(AonApiData api) {
		ApplicationParameter administration = AON.getApplicationParameter(api.getDomain().getName(),
			api.getDomain().getId(), api.getUser().getLogin(), AppParam.FS_DEFAULT_ADMINISTRATION.toString());

		return administration.getValue() != null
			? Administration.safeValueOf(Integer.parseInt(administration.getValue()))
			: Administration.UNKNOWN;
	}
	
	private Administration saveAdministration(AonApiData api, String value) {
		Administration administration = Administration.safeValueOf(value);
		AON.insertApplicationParameter(api.getDomain().getName(),
				api.getDomain().getId(), api.getUser().getLogin(), 
				AppParam.FS_DEFAULT_ADMINISTRATION,
				Integer.toString(administration.value()));
		return administration;
	}
	
	private JSONObject savePrintConfiguration(AonApiData api, JSONObject json) {
		PrintInvoiceConfiguration pic = PrintInvoiceConfigurationJSON.fromJSON(json);
		AON_SOLUTIONS.savePrintInvoiceConfiguration(api.getDomain(), api.getUser(), pic);
		if(api.getData().optBoolean("backgroundRemove")) {
			AON.deleteAttach(api.getDomain().getName(),	api.getDomain().getId(), api.getUser().getLogin(), f ->
				f.getDomainProperty().eq(api.getDomain().getId()).and(f.getSourceTypeProperty().eq(DataAttachSource.INVOICE_PRINT_CONFIGURATION.value())), AttachType.DATA);
		}
		return getPrintConfiguration(api);
	}
	
	private JSONObject getSiiConfiguration(AonApiData api) {
		SiiConfiguration sii = AON.getSiiConfiguration(api.getDomain(), api.getUser());
		return SiiConfigurationJSON.toJSON(sii);
	}
	
	private JSONObject saveSiiConfiguration(AonApiData api, JSONObject json) {
		SiiConfiguration sii = SiiConfigurationJSON.fromJSON(json);
		AON.saveSiiConfiguration(api.getDomain(), api.getUser(), sii);
		return json;
	}
	
	private JSONObject getTbaiConfiguration(AonApiData api) {
		TbaiConfiguration tbai = AON.getTbaiConfiguration(api.getDomain(), api.getUser());
		return TbaiConfigurationJSON.toJSON(tbai);
	}
	
	private JSONObject saveTbaiConfiguration(AonApiData api, JSONObject json) {
		TbaiConfiguration t = TbaiConfigurationJSON.fromJSON(json);
		AON.saveTbaiConfiguration(api.getDomain(), api.getUser(), t);
		return json;
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
		json.put("date", AonDateUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
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
		json.put("comments", "");
		json.put("remarks", new JSONArray());
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
