package net.aonsolutions.aon.api.servlet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvofoxConfigurationJSON;
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
import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;
import com.esferalia.aon.occam.api.model.Properties.RawdocProperties;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceNewPortal;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MaritalStatus;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import es.translogia.tedi.json.TediInvoiceJSON;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.request.BidoqRequest;
import net.aonsolutions.aon.sign.PdfSigner;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.tbai.TbaiMain;
import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import solutions.aon.aws.s3.S3;

@SuppressWarnings("serial")
@WebServlet(name = "AonInvoiceServlet", urlPatterns = {"/ms/api/invoice/*"})
public class InvoiceServlet extends AonApiHttpServlet{
		
	private class InvoiceFilter {
		String description;
		String status;
		String[] types;
		Integer page;
		Integer perPage;
		Byte recorded;
		String contact;
		String total;
		String referenceCode;
		String searchedDate;
		String global = "";

		Date from;
		Date to;
		Integer registry;
		
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
		
		public Date getFrom() {
			return from;
		}
		
		public InvoiceFilter setFrom(Date from) {
			this.from = from;
			return this;
		}
		
		public Date getTo() {
			return to;
		}
		
		public InvoiceFilter setTo(Date to) {
			this.to = to;
			return this;
		}
		
		public Byte getRecorded() {
			return recorded;
		}

		public InvoiceFilter setRecorded(Byte recorded) {
			this.recorded = recorded;
			return this;
		}
		
		public Integer getRegistry() {
			return registry;
		}
		
		public InvoiceFilter setRegistry(Integer registry) {
			this.registry = registry;
			return this;
		}
		
		public String getContact() {
			return contact;
		}
		
		public InvoiceFilter setContact(String contact) {
			this.contact = contact;
			return this;
		}
		
		public String getTotal() {
			return total;
		}
		
		public InvoiceFilter setTotal(String total) {
			this.total = total;
			return this;
		}
		
		public String getReferenceCode() {
			return referenceCode;
		}
		
		public InvoiceFilter setReferenceCode(String referenceCode) {
			this.referenceCode = referenceCode;
			return this;
		}
		
		public String getSearchedDate() {
			return searchedDate;
		}
		
		public InvoiceFilter setSearchedDate(String searchedDate) {
			this.searchedDate = searchedDate;
			return this;
		}
		
		public String getGlobal() {
			return global;
		}
		
		public InvoiceFilter setGlobal(String global) {
			this.global = global;
			return this;
		}
		
		
	}
	
	private class RawdocFilter {
		int nature;
		Integer type;
		String status;
		String json;
		String log;
		int mimeType;
		Integer page;
		Integer perPage;
		String referenceCode;
		String name;
		String total;
		String searchedDate;
		String global;

		
		public int getNature() {
			return nature;
		}
		public RawdocFilter setNature(int nature) {
			this.nature = nature;
			return this;
		}
		public Integer getType() {
			return type;
		}
		public RawdocFilter setType(Integer type) {
			this.type = type;
			return this;

		}
		public String getStatus() {
			return status;
		}
		public RawdocFilter setStatus(String status) {
			this.status = status;
			return this;

		}
		public String getJson() {
			return json;
		}
		public RawdocFilter setJson(String json) {
			this.json = json;
			return this;

		}
		public String getLog() {
			return log;
		}
		public RawdocFilter setLog(String log) {
			this.log = log;
			return this;

		}
		public int getMimeType() {
			return mimeType;
		}
		public RawdocFilter setMimeType(int mimeType) {
			this.mimeType = mimeType;
			return this;

		}
		public Integer getPage() {
			return page;
		}
		public RawdocFilter setPage(Integer page) {
			this.page = page;
			return this;

		}
		public Integer getPerPage() {
			return perPage;
		}
		public RawdocFilter setPerPage(Integer perPage) {
			this.perPage = perPage;
			return this;
		}
		
		public String getReferenceCode() {
			return referenceCode;
		}
		
		public RawdocFilter setReferenceCode(String referenceCode) {
			this.referenceCode = referenceCode;
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public RawdocFilter setName(String name) {
			this.name = name;
			return this;
		}
		
		public String getTotal() {
			return total;
		}
		
		public RawdocFilter setTotal(String total) {
			this.total = total;
			return this;
		}
		
		public String getDate() {
			return searchedDate;
		}
		
		public RawdocFilter setDate(String searchedDate) {
			this.searchedDate = searchedDate;
			return this;
		}
		
		public String getGlobal() {
			return global;
		}
		
		public RawdocFilter setGlobal(String global) {
			this.global = global;
			return this;
		}
	}
	
	private static final Logger LOGGER  = Logger.getLogger(InvoiceServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getInvoiceObject(api));
				break;
			case "/invoice_new_portal":
				response(req, resp, getInvoiceNewPortalObject(api));
				break;
			case "/invoice_new_portal_count":
				response(req, resp, getInvoiceNewPortalCount(api));
				break;
			case "/rawdoc_new_portal":
				response(req, resp, getRawdocNewPortal(api));
				break;
			case "/rawdoc_new_portal_count":
				response(req, resp, getRawdocCount(api));
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
			AonApiData api = initialize(req);
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
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, setInvoice(api));
				break;
			case "/accept":
				response(req, resp, acceptInvoice(api));
				break;
			case "/sign":
				response(req, resp, signInvoice(api));
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
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteInvoice(api));
				break;
			case "/rawdoc":
				response(req, resp, deleteInvoiceObject(api));
				break;
			case "/cancel":
				response(req, resp, deleteInvoiceTBAI(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getInvoiceNewPortalObject(AonApiData api) {
		InvoiceFilter filter = new InvoiceFilter()
				.setDescription(api.getData().optString("description"))
				.setStatus(api.getData().optString(IConstants.STATUS))
				.setTypes(api.getData().opt(IConstants.TYPE) != null 
					? api.getData().optString(IConstants.TYPE).split(","): null)
				.setGlobal(api.getData().optString("global"))
//				.setContact(api.getData().optString("contact"))
//				.setTotal(api.getData().optString("total"))
//				.setSearchedDate(api.getData().optString("searched_date"))
//				.setReferenceCode(api.getData().optString("reference_code"))
				.setFrom(JsonUtils.getDate(api.getData(), IJsonNames.FROM))
				.setTo(JsonUtils.getDate(api.getData(), IJsonNames.TO))
				.setPage(api.getData().optInt("page"))
				.setPerPage(api.getData().optInt("per_page"))
				.setRecorded(!api.getData().optString("recorded").equals("") ? InvoiceStatus.safeValueOf(api.getData().optString("recorded")).value() : null);
		JSONArray jsArray = new JSONArray();
		AON_SOLUTIONS.getInvoiceNewPortal(api.getDomain().getName(), api.getDomain().getId(), "api", 
				f -> invoiceFilter(f, api.getDomain().getId(), filter))
		.forEach(invoice -> {
			jsArray.put(InvoiceNewPortalList2JSON(invoice, api));
		}
		);
		return jsArray;
	}
	
	private JSONArray getRawdocNewPortal(AonApiData api) {
	    JSONArray jsArray = new JSONArray();
	    RawdocFilter filter = new RawdocFilter()
	    		.setType(api.getData().optInt(IConstants.TYPE))
	    		.setStatus(api.getData().optString(IConstants.STATUS))
	    		.setGlobal(api.getData().optString("global"));
	    Integer page = api.getData().optInt("page");
	    Integer perPage = api.getData().optInt("per_page");
	    boolean ticket = api.getData().optBoolean("ticket");
	    AON.getRawdocNewPortal(api.getDomain().getName(), api.getDomain().getId(), "api", 
	            f -> rawdocFilter(f,api.getDomain().getId() , filter), page, perPage, ticket).forEach(rawdoc ->{
	                JSONObject json = rawdoc2json(rawdoc, api);
	                jsArray.put(json);
	            });
	    return jsArray;
	}
	
	private JSONObject rawdoc2json(Rawdoc rawdoc, AonApiData api) {
	    JSONObject json = new JSONObject(rawdoc.getJson());
	    json.put(IJsonNames.ID, rawdoc.getId());
	    json.put(IJsonNames.STATUS, rawdoc.getStatus() != null ? rawdoc.getStatus().getName() : IConstants.INBOX);
	    if(rawdoc.getMimeType() != null){
	        JSONObject data = new JSONObject();
	        data.put("domain_name", api.getDomain().getName());
	        data.put("domain_id", api.getDomain().getId());
	        data.put("id", rawdoc.getId());
	        data.put("attach_type", AttachType.RAWDOC.getName());
	        String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
	        String url =  "ms/api/file/" +  result;
	                            
	        JSONObject f = new JSONObject();
	        f.put("url", url);
	        f.put("path", url);
	        f.put("content_type", rawdoc.getMimeType().getName());
	        json.put("file", f);
	    }
	    return json; 
	}
	
	private long getRawdocCount(AonApiData api) {
		RawdocFilter filter = new RawdocFilter()
	    		.setType(api.getData().optInt(IConstants.TYPE))
	    		.setStatus(api.getData().optString(IConstants.STATUS));
		
		boolean ticket = api.getData().optBoolean("ticket");
		
		return AON.getRawdocCount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f -> rawdocFilter(f,api.getDomain().getId(), filter), ticket);
	}

	
	private long getInvoiceNewPortalCount(AonApiData api) {
		
		InvoiceFilter filter = new InvoiceFilter()
                .setDescription(api.getData().optString("description"))
                .setStatus(api.getData().optString(IConstants.STATUS))
                .setTypes(api.getData().opt(IConstants.TYPE) != null 
                    ? api.getData().optString(IConstants.TYPE).split(","): null)
                .setFrom(JsonUtils.getDate(api.getData(), IJsonNames.FROM))
                .setTo(JsonUtils.getDate(api.getData(), IJsonNames.TO))
                .setPage(api.getData().optInt("page"))
                .setPerPage(api.getData().optInt("per_page"))
                .setRecorded(!api.getData().optString("recorded").equals("") ? InvoiceStatus.safeValueOf(api.getData().optString("recorded")).value() : null);
		
		return AON_SOLUTIONS.getInvoiceNewPortalCount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f -> invoiceFilter(f , api.getDomain().getId(), filter));

	}

	private Object getInvoiceObject(AonApiData api) {
		if(api.getData().opt(IConstants.ID) != null) {
			Integer id = api.getData().optInt(IConstants.ID);
			return getInvoice(api.getDomain(), api.getUser().getLogin(), id);
		} else {
			InvoiceFilter filter = new InvoiceFilter()
				.setDescription(api.getData().optString("description"))
				.setStatus(api.getData().optString(IConstants.STATUS))
				.setRecorded(!api.getData().optString("recorded").equals("") ? InvoiceStatus.safeValueOf(api.getData().optString("recorded")).value() : null)
				.setTypes(api.getData().opt(IConstants.TYPE) != null 
					? api.getData().optString(IConstants.TYPE).split(","): null)
				.setFrom(JsonUtils.getDate(api.getData(), IJsonNames.FROM))
				.setTo(JsonUtils.getDate(api.getData(), IJsonNames.TO))
				.setRegistry(JsonUtils.getInteger(api.getData(), IJsonNames.REGISTRY))
				.setPage(api.getData().optInt("page"))
				.setPerPage(api.getData().optInt("per_page"));
			return getInvoices(api.getDomain(), api.getUser().getLogin(), filter);
		}
	}
	
	private JSONArray getAccountsObject(AonApiData api) {
		JSONArray array = new JSONArray();
		String type = api.getData().optString(IConstants.TYPE);
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
	
	private JSONObject deleteInvoiceTBAI(AonApiData api) {
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(api.getDomain(), api.getUser());
		tbaiConfiguration.setCertificate(checkCertificate(api));
		
		List<Integer> invoiceIds = toList(api.getData().optJSONArray(IConstants.ID));
		invoiceIds.stream().forEach(id -> {
			
			Invoice invoice = AON_SOLUTIONS.getInvoice(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), id);

			try {
				TbaiMain tbai = new TbaiMain();
				tbai.createAnulacionTBAI(company, invoice, tbaiConfiguration);
				AON.deleteInvoice(api.getDomain().getName(), invoice.getDomain(), api.getUser().getLogin(), invoice.getId());
			} catch (Exception e) {
				if(tbaiConfiguration.isTest()) {
					AON.deleteInvoice(api.getDomain().getName(), invoice.getDomain(), api.getUser().getLogin(), invoice.getId());
				} else {
					e.printStackTrace();
				}
			}
			
		});
		return new JSONObject();
	}
	
	private JSONObject deleteInvoice(AonApiData api) {
		Integer invoiceId = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		Invoice invoice = AON_SOLUTIONS.getInvoice(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoiceId);
		
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(api.getDomain(), api.getUser());

		boolean accepted = true;
		if(tbaiConfiguration.isBizkaia() && invoice.isSales()) {
			InvoiceInfo info = AON.getInvoiceInfo(api.getDomain(), api.getUser(), f -> 
				f.getInvoiceProperty().eq(invoiceId)
				.and(f.getTypeProperty().eq(InvoiceCommunicationType.LROE.value())));
			accepted = info.isAccepted() || info.isAcceptedWithErrors();
		}

		if(invoice.isSales() && tbaiConfiguration.isActive() && accepted) {
			tbaiConfiguration.setCertificate(checkCertificate(api));
			TbaiMain tbai = new TbaiMain();
			try {
				tbai.createAnulacionTBAI(company, invoice, tbaiConfiguration);
			} catch (Exception e) {
				e.printStackTrace();
				throw new AonApiException(e.getMessage());
			}
		}
		
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
    	
    	if(!AonStringUtils.isBlank(invoiceFilter.getDescription())) {
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
    	
    	if(invoiceFilter.getFrom() != null) {
    		filter = filter.and(f.getStartIssueDateProperty().ge(invoiceFilter.getFrom()));
    	}
    	
    	if(invoiceFilter.getTo() != null) {
    		filter = filter.and(f.getEndIssueDateProperty().le(invoiceFilter.getTo()));
    	}
    	
    	if(invoiceFilter.getRecorded() != null) {
    		filter = filter.and(f.getStatusProperty().eq(invoiceFilter.getRecorded()));
    	}
    	
    	if(invoiceFilter.getRegistry() != null) {
    		filter = filter.and(f.getRegistryProperty().eq(invoiceFilter.getRegistry()));
    	}
    	
    	if(!AonStringUtils.isBlank(invoiceFilter.getGlobal())) {
    		Filter filter3 = f.getRegistryNameProperty().like("%"+ invoiceFilter.getGlobal() +"%")
    				.or(f.getTotalProperty().like("%" + invoiceFilter.getGlobal() + "%"))
    				.or(f.getReferenceCodeProperty().like("%" + invoiceFilter.getGlobal() + "%"))
    				.or(f.getDateNewPortalProperty().like("%" + invoiceFilter.getGlobal() +"%"));
    		filter = filter.and(filter3);
    	}
    	
    	if(invoiceFilter.getPage() != null) {
    		filter.page(invoiceFilter.getPage());
    	} 
    	
    	if(invoiceFilter.getPerPage() != null) {
    		filter.perPage(invoiceFilter.getPerPage());
    	}
    	
		return filter;
    }
	
	public static Filter rawdocFilter(RawdocProperties f , Integer domainId, RawdocFilter rawdocFilter) {
		Filter filter = f.getDomainProperty().eq(domainId);
		
		if(rawdocFilter.getType() != null ) {
			filter = filter.and(f.getTypeProperty().eq(RawdocType.safeValueOf(rawdocFilter.getType()).value()));
		}
		
		if(AonStringUtils.isBlank(rawdocFilter.getStatus())) {
			filter = filter.and(f.getStatusProperty().eq(RawdocStatus.INBOX.value()));
		}else {
			filter = filter.and(f.getStatusProperty().eq(RawdocStatus.safeValueOf(rawdocFilter.getStatus()).value()));
		}
		
		if(!AonStringUtils.isBlank(rawdocFilter.getGlobal())) {
			filter = filter.and((f.getJsonNameProperty().like("%"+ rawdocFilter.getGlobal() + "%"))
					.or(f.getReferenceCodeProperty().like("%" + rawdocFilter.getGlobal() + "%"))
					.or(f.getJsonTotalProperty().like("%" + rawdocFilter.getGlobal() +"%"))
					.or(f.getJsonDateProperty().like("%" + rawdocFilter.getGlobal() + "%")));
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
		Invoice invoice = AON_SOLUTIONS.getInvoice(domain.getName(), domain.getId(), login, id);
		JSONObject json = InvoiceJSON.toJSON(invoice);

		TbaiConfiguration tbai = AON.getTbaiConfiguration(domain, login);
		if(tbai.isActive()) {	
			String tbaiUrl = TbaiData.getInstance(tbai).getTbaiUrl(domain.getName(), domain.getId(), login, invoice.getId());
			if(!AonStringUtils.isBlank(tbaiUrl)) {
				json.put("tbai", true);
				json.put("tbaiUrl", tbaiUrl);
			}
		}
		json.put(IJsonNames.FILE, buildInvoiceFileJSON(domain, login, invoice));
		return json;
	}
	
	public static JSONObject buildInvoiceFileJSON(Domain domain, String login, Invoice invoice) {
		Attach invoiceAttach = AON.getAttach(domain.getName(), domain.getId(), login,
				f -> f.getAttachModuleProperty().eq(invoice.getId())
				, AttachType.INVOICE);

		JSONObject json = new JSONObject();
		if(invoiceAttach != null && invoiceAttach.getId() != null) {
			JSONObject data = new JSONObject();
			data.put(IConstants.DOMAIN_NAME, domain.getName());
			data.put(IConstants.DOMAIN_ID, domain.getId());
			data.put(IJsonNames.ID, invoiceAttach.getId());
			data.put(IConstants.ATTACH_TYPE, AttachType.INVOICE.getName());
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			String path = "/ms/api/file/" +  result;	
			String url = "https://" + domain.getName() + path; 
		    json.put(IJsonNames.URL, url);
		    json.put(IJsonNames.PATH, path);
		    json.put(IConstants.CONTENT_TYPE, invoiceAttach.getMimeType().getName());
		} else {
			JSONObject data = new JSONObject();
			data.put(IConstants.DOMAIN_NAME, domain.getName());
			data.put(IConstants.DOMAIN_ID, domain.getId());
			data.put(IJsonNames.ID, invoice.getId());
			data.put(IConstants.SOURCE, "invoice");
			data.put(IJsonNames.LOGIN, login);
		
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			String path = "/ms/api/download_invoice_pdf?json=" +  result;
			String url = "https://" + domain.getName() + path;
		    json.put(IJsonNames.URL, url);
		    json.put(IJsonNames.PATH, path);
		    json.put(IConstants.CONTENT_TYPE, MimeType.PDF.getName());
		}
		return json;
	}
	
	private static JSONArray getInvoices(Domain domain, String login, InvoiceFilter filter) {
		JSONArray jsArray = new JSONArray();
		if(!isRawdoc(filter.getStatus())) {
			AON_SOLUTIONS.getInvoices(domain.getName(), domain.getId(), "api", f -> invoiceFilter(f, domain.getId(), filter))
				.forEach(invoice -> jsArray.put(invoiceList2JSON(invoice)));
		} else {
			RawdocStatus rs = getRawdocStatus(filter.getStatus());
			AON.getRawdocStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
					.and(f.getStatusProperty().eq(rs.value())))
			.forEach(r -> { 
				JSONObject json = new JSONObject(r.getJson());
				json.put(IJsonNames.ID, r.getId());
				json.put(IJsonNames.STATUS, r.getStatus() != null ? r.getStatus().getName() : IConstants.INBOX);
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
				    f.put("path", url);
				    f.put("content_type", r.getMimeType().getName());
				    json.put("file", f);
				}
				JSONArray log = new JSONArray(r.getLog() != null ? r.getLog() : "[]");
				json.put("remarks", log);
				String reference = filter.getDescription() != null && json.opt(IJsonNames.REFERENCE) != null ? json.optString(IJsonNames.REFERENCE) : "";
				String registryName = RawdocType.OUTPUT.equals(r.getType()) 
						? (filter.getDescription() != null && json.opt(IJsonNames.RECEIVER) != null ? json.getJSONObject(IJsonNames.RECEIVER).optString(IJsonNames.NAME) : "")
						: (filter.getDescription() != null && json.opt(IJsonNames.SENDER) != null ? json.getJSONObject(IJsonNames.SENDER).optString(IJsonNames.NAME) : "");
						
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
		String referenceAux = "";
		if(!AonStringUtils.isBlank(invoice.getSeries())) {
			referenceAux = referenceAux + invoice.getSeries() + "/";
		}
		referenceAux = referenceAux + "PROFORMA";
		JSONObject json = new JSONObject();
		json.put(IJsonNames.ID, invoice.getId());
		json.put(IJsonNames.DATE, invoice.getIssueDate());
		json.put(IJsonNames.REFERENCE, invoice.getNumber() > 0 
				? invoice.getReferenceCode() : referenceAux);
		json.put(IJsonNames.NAME, invoice.getRegistryName());
		json.put(IJsonNames.TOTAL, invoice.getTotal());
		json.put(IJsonNames.STATUS, invoice.isRecorded() 
				? InvoiceStatus.SCORED.name().toLowerCase() 
				: InvoiceStatus.PENDING.name().toLowerCase());
		json.put(IJsonNames.TYPE, invoice.getType().getTediName());
		return json;
	}
	
	private static JSONObject InvoiceNewPortalList2JSON(InvoiceNewPortal invoice, AonApiData api) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.ID, invoice.getId());
		json.put(IJsonNames.DATE, invoice.getIssueDate());
		json.put(IJsonNames.REFERENCE, invoice.getReferenceCode());
		json.put(IJsonNames.NAME, invoice.getRegistryName());
		json.put(IJsonNames.TOTAL, invoice.getTotal());
		json.put(IJsonNames.TYPE, invoice.getType());
		json.put(IJsonNames.STATUS, invoice.isRecorded() 
				? InvoiceStatus.SCORED.name().toLowerCase() 
				: InvoiceStatus.PENDING.name().toLowerCase());
		json.put(IJsonNames.NUMBER, invoice.getNumber());
		json.put(IJsonNames.SERIE, invoice.getSeries());
		if(invoice.getMimeType() != null) {
			JSONObject data = new JSONObject();
			data.put("domain_name", api.getDomain().getName());
			data.put("domain_id", api.getDomain().getId());
			data.put("id", invoice.getId());
			data.put("attach_type", AttachType.RAWDOC.getName());
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			String url =  "/ms/api/file/" +  result;
			json.put(IJsonNames.FILE, url);
			json.put(IJsonNames.CONTENT_TYPE, invoice.getMimeType().getName());
		} else {
			JSONObject data = new JSONObject();
			data.put("id", invoice.getId());
			data.put("source", "rawdoc");
			data.put("domain_id", api.getDomain().getId());
			data.put("domain_name", api.getDomain().getName());
			data.put("login", api.getUser().getLogin());
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			String url =  "/ms/api/download_invoice_pdf?json=" +  result;
			json.put(IJsonNames.FILE, url);
			json.put(IJsonNames.CONTENT_TYPE, MimeType.PDF.getName());
		}
		return json;
	}
	
	private void deleteInvoices(Domain domain, String login, List<Integer> ids) {
		Integer[] idsArray = ids.toArray(new Integer[ids.size()]);
		AON.rawdocDelete(domain.getName(), domain.getId(), login, 
			f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getIdProperty().in(idsArray)));
	}
	
	public static JSONObject acceptInvoice(AonApiData api) throws Exception {
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(api.getDomain(), api.getUser());
		Invoice invoice = InvoiceJSON.fromJSON(api.getData());
		if(invoice.isSales() && tbaiConfiguration.isActive()) {
			tbaiConfiguration.setCertificate(checkCertificate(api));
			tbaiValidation(invoice);
		}
		invoice = AON_SOLUTIONS.acceptInvoice(api.getDomain(), api.getUser(), invoice);
		processInvoiceFile(api, invoice);
		acceptTbai(tbaiConfiguration, company, invoice);
		JSONObject json = InvoiceJSON.toJSON(invoice);
		if(invoice.isSales() && tbaiConfiguration.isActive()) {	
			String tbaiUrl = TbaiData.getInstance(tbaiConfiguration).getTbaiUrl(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoice.getId());
			if(!AonStringUtils.isBlank(tbaiUrl)) {
				json.put("tbai", true);
				json.put("tbaiUrl", tbaiUrl);
			}
		}		
		json.put(IJsonNames.FILE, buildInvoiceFileJSON(api.getDomain(), api.getUser().getLogin(), invoice));

		return json;
	}
	
	public static void processInvoiceFile(AonApiData api, Invoice invoice) {
		JSONObject fileJSON = JsonUtils.getJSONObject(api.getData(), IJsonNames.FILE);
		if(!fileJSON.isEmpty()) {
			String s3Key = JsonUtils.getString(fileJSON, IJsonNames.S3_KEY);
			String contentType = JsonUtils.getString(fileJSON, "content_type");
			if(s3Key != null) {
				try {
					byte[] data = S3.download("aon-upload-post", s3Key);
					if(data != null) {
						MimeType mimetype = MimeType.safeValueFromContenType(contentType);
						Attach attach = new Attach()
							.setDate(new Date())
							.setDomain(new Domain().setId(invoice.getDomain()))
							.setAttachModule(invoice.getId())
							.setMimeType(mimetype != null ? mimetype : MimeType.PDF)
							.setAttachType(AttachType.INVOICE)
							.setType(InvoiceAttachmentType.INVOICE.value())
							.setData(data);

						AON.insertAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), attach);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void acceptTbai(TbaiConfiguration tbaiConfiguration, Company company,  Invoice invoice) throws Exception {
		if(invoice.isSales() && tbaiConfiguration.isActive()) {
			TbaiMain tbai = new TbaiMain();
			tbai.createEmisionTBAI(company, invoice, tbaiConfiguration);
		}
	}
	
	public static JSONObject signInvoice(AonApiData api) throws Exception {
		Integer invoiceId = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		Invoice invoice = AON_SOLUTIONS.getInvoice(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoiceId);
		PrintInvoiceConfiguration config = AON_SOLUTIONS.getPrintInvoiceConfiguration(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), true);
		CompanyFull company = AON.getCompanyFull(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		
		String qrUrl = "https://" +  api.getDomain().getName() + "/dip?d=" + company.getRegistry().getDocument() 
				+ "&f=" + AonDateUtils.simpleFormat(invoice.getIssueDate())
				+ "&s=" + invoice.getSeries()
				+ "&n=" + invoice.getNumber()
				+ "&t=" + invoice.getTotal();  
		TbaiConfiguration tbai = AON.getTbaiConfiguration(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		String tbaiId = "";
		if(tbai.isActive()) {
			TbaiData tbaiData = TbaiData.getInstance(tbai);
			String tbaiUrl = tbaiData.getTbaiUrl(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoice.getId());
			qrUrl = AonStringUtils.isBlank(tbaiUrl) ? qrUrl : tbaiUrl;
			tbaiId = tbaiData.getTbaiId(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoice.getId());
		}
		Attach logo = new Attach();
		
		if(config.isLogo()) {
			Integer id = company.getRegistry().getId();
			logo = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f-> f.getAttachModuleProperty().eq(id)
				.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PdfMaker.printInvoice(out, company, invoice, config, qrUrl, logo.getData(), tbaiId);
		byte[] data = out.toByteArray();
		PdfSigner signer = new PdfSigner();
		byte[] signedData = signer.sign(checkCertificate(api), data);
		
		Attach attach = new Attach(AttachType.INVOICE)	
			.setDomain(api.getDomain())
			.setDate(new Date())
			.setDescription(invoice.getReferenceCode())
			.setMimeType(MimeType.SIGNED_PDF)
			.setType(InvoiceAttachmentType.INVOICE.value())
			.setAttachModule(invoiceId)
			.setData(signedData);
		AON.insertAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), attach);
		return new JSONObject();
	}
	
	public static JSONObject setInvoice(AonApiData api) {
		Domain domain = api.getDomain();
		String login = api.getUser().getLogin();
		JSONObject json = api.getData();
		JSONObject file = null;
		
		if(json.opt("file")!= null && json.opt(IJsonNames.INVOICE) != null) { 
			file = json.optJSONObject("file");
			json.remove("file");
			json = json.opt(IJsonNames.INVOICE) != null ? json.optJSONObject(IJsonNames.INVOICE) : json;
			if(json.opt(IJsonNames.STATUS) == null) {
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
		    	} catch (Exception e) {
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
		Integer year = null;
		if(api.getData().opt("year") != null) {
			year = api.getData().optInt("year");
		}
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
		BidoqRequest.selfconta(api.getDomain(), api.getUser(), company.getDocument(), year);
		return new JSONObject();
	}
	
	private JSONObject selfcontaRecord(AonApiData api) throws Exception {
		String status = JsonUtils.getString(api.getData(), IJsonNames.STATUS);
		Invoice invoice = InvoiceJSON.fromJSON(api.getData());
		if(invoice.getId() == null || isRawdoc(status)) {
			invoice.getDetails().stream().forEach(d -> d.setSource(InvoiceSource.ACCOUNT));
			invoice = AON_SOLUTIONS.validateInvoice(api.getDomain(), api.getUser(), invoice);
		} else invoice = AON_SOLUTIONS.getInvoice(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoice.getId());

		BidoqRequest.selfcontaRecord(api.getDomain(), api.getUser(), invoice, api.getData());
		
		return InvoiceJSON.toJSON(invoice);
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
		ApplicationParameter defaultWithholdingPercent = AON.getApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), AppParam.ACC_DEFAULT_RETENTION_PERCENT);
		Integer withholdingPercentId = AonNumberUtils.toInteger(defaultWithholdingPercent.getValue());
		Tax withholdingPercent = AON.getTax(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), withholdingPercentId);
		if(withholdingPercent.getWithholdingType() == null) withholdingPercent.setWithholdingType(WithholdingType.PROFESSIONAL);

		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		JSONObject json = new JSONObject();
		json.put("print", getPrintConfiguration(api));
		json.put("company", CompanyJSON.toJSON(company));
		json.put(IJsonNames.E_INVOICE, company.iseInvoice());
		json.put("tbai", getTbaiConfiguration(api));
		json.put("sii", getSiiConfiguration(api));
		json.put(IJsonNames.ADMINISTRATION, getAdministration(api));
		json.put("withholdingPercent", withholdingPercent.getWithholdingType().name());
		json.put("invofox", InvofoxServlet.getConfiguration(api));
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
		
		JSONObject invofox = JsonUtils.has(api.getData(), "invofox") ? 
				InvofoxConfigurationJSON.toJSON(AON.saveInvofoxConfiguration(api.getDomain(), api.getUser(), 
						InvofoxConfigurationJSON.fromJSON(JsonUtils.getJSONObject(api.getData(), "invofox")))) 
				: new JSONObject();
		
		return new JSONObject()
			.put("administration", administration.name())	
			.put("print", print)
			.put("tbai", tbai)
			.put("sii", sii)
			.put("invofox", invofox)
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
	
	private static RawdocStatus getRawdocStatus(String status) {
		return RawdocStatus.safeValueOf(status);
	}
	
	private static boolean isRawdoc(String status) {
		return getRawdocStatus(status) != null;
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
	
	private static  void tbaiValidation(Invoice invoice) throws Exception {
		checkInvoice(invoice);
		checkRegistry(invoice);
	}
	
	private static  void checkInvoice(Invoice invoice) throws Exception {
		if(invoice.isRectifier() && AonStringUtils.isBlank(invoice.getSeries())) {
			throw new Exception("Las Facturas rectificativas tienen que tener serie.");
		}
		
		Date date = AonDateUtils.getDateWithoutTime(invoice.getIssueDate());
		if(date.after(new Date())) {
			throw new Exception("Las Fecha de la factura no puede ser superior a la fecha actual.");
		}
	}
	
	private static void checkRegistry(com.esferalia.aon.occam.api.model.finance.Invoice invoice) throws Exception {
		if(AonStringUtils.isBlank(invoice.getRegistryDocument()) 
				&& !invoice.isSimplified()) {
			throw new Exception("El Documento del cliente está vacio.");
		}
			
		if(Country.ES.equals(invoice.getRegistryDocumentCountry()) 
				&& !AonDocumentUtil.isValid(invoice.getRegistryDocument())
				&& !invoice.isSimplified()) {
			throw new Exception("El Documento del cliente no es válido.");
		}
	}
	
	
	public static void main(String[] args) {
		JSONObject data = new JSONObject();
		data.put(IConstants.DOMAIN_NAME, "innovative-mac.aonsolutions.net");
		data.put(IConstants.DOMAIN_ID, 562);
		data.put(IJsonNames.ID, 1209900); //1177840);
		data.put(IConstants.ATTACH_TYPE, AttachType.DATA.getName());
		String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
		String url = "innovative-mac.aonsolutions.net/ms/api/file/" +  result;	
		System.out.println(url);
	}
}
