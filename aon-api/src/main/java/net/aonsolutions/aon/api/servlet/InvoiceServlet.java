package net.aonsolutions.aon.api.servlet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.FINANCE;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.json.AccountJSON;
import com.esferalia.aon.occam.api.json.ApiConfigurationJSON;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.PersonJSON;
import com.esferalia.aon.occam.api.json.TaxJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceCommunicationConfigurationJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceSeriesJSON;
import com.esferalia.aon.occam.api.json.invoice.PrintInvoiceConfigurationJSON;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.doc.ExternalStorage;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.finance.ApiConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistoryMapValue;
import com.esferalia.aon.occam.api.model.finance.InvoiceDataName;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceFilter;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectTas;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.occam.api.model.type.MaritalStatus;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceJSONUtils;
import com.esferalia.aon.occam.impl.jooq.dao.api.InvoiceApiDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.codec.AonDigestUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.servlet.registry.RegistryAdditionalInfo;
import net.aonsolutions.aon.api.servlet.registry.RegistryServlet;
import net.aonsolutions.aon.in.pdf.maker.PdfMaker;
import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;
import net.aonsolutions.aon.sign.PdfSigner;
import net.aonsolutions.aon.sii.SIIManager;
import net.aonsolutions.aon.tbai.CRC8;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.tbai.TbaiMain;

@WebServlet(name = "AonInvoiceServlet", urlPatterns = {"/ms/api/invoice/*"})
public class InvoiceServlet extends AonApiHttpServlet{
		
	private static final long serialVersionUID = 7805502763869318228L;
	private static final Logger LOGGER  = Logger.getLogger(InvoiceServlet.class.getName());
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			LOGGER.log(Level.INFO,"AON API INVOICE SERVLET - GET METHOD {0}", api.getPath());			
			switch (api.getPath()) {
			case "/":
				response(req, resp, getInvoiceObject(api));
				break;
			case "/count":
				response(req, resp, getInvoiceCount(api));
				break;
			case "/accounts":
				response(req, resp, getExpAccounts(api));
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
			case "/api_configuration":
				response(req, resp, getApiConfiguration(api));
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
				response(req, resp, RawdocServlet.putRawdoc(api));
				break;
			case "/print_configuration":
				response(req, resp, savePrintConfiguration(api, api.getData()));
				break;
			case "/configuration":
				response(req, resp, saveConfiguration(api));
				break;
			case "/fix":
				response(req, resp, fix(api));
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
				response(req, resp, RawdocServlet.putRawdoc(api));
				break;
			case "/accept":
				response(req, resp, acceptInvoice(api));
				break;
			case "/rectify":
				response(req, resp, rectifyInvoice(api));
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
				response(req, resp, RawdocServlet.deleteRawdocs(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject getInvoiceCount(AonApiData api) {
		String domainName = api.getDomain().getName();
		Integer domainId = api.getDomain().getId();
		String login = api.getUser().getLogin();
		
		Integer count = AON_SOLUTIONS.getInvoicesCount(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId));
		
		return new JSONObject().put("invoiceCount", count);
	}
	
	private Object getInvoiceObject(AonApiData api) {
		Integer id = api.getData().optIntegerObject(IConstants.ID, null);
		if (id != null) {
			return getInvoice(api, id);
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
			return getInvoices(api, filter);
		}
	}
	
	private JSONArray getExpAccounts(AonApiData api) {
		String type = api.getData().optString(IConstants.TYPE);
		return ACCOUNTING.getAccounts(api.getOccam(), f -> AonApiServletUtils.accountFilter(f, api.getDomain(), type))
			.map( AccountJSON::to )
			.filter( Optional<JSONObject>::isPresent )
			.map( Optional<JSONObject>::get )
			.collect(Collector.of(JSONArray::new,JSONArray::put,(left, right) -> left, Collector.Characteristics.UNORDERED));
	}
	
	private JSONObject deleteInvoice(AonApiData api) throws Exception {
		Integer invoiceId = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		Invoice invoice = AON_SOLUTIONS.getInvoice(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoiceId);
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		
		
		// ***********************************
		// If the invoice is not a sales invoice or TBAI is not active, we accept and communicate the invoice
		if (invoice.isSales()) {
			InvoiceCommunicationConfiguration config = AON.getInvoiceCommunicationConfiguration(api.getOccam());
			if (config.isVerifactu()) {
				if (InvoiceCommunicator.mustBeAnnulled(api.getOccam(), invoice, InvoiceCommunicationType.VERIFACTU)) {
					cancelAndCommunicateInvoice(api, config, company, invoice);
					return new JSONObject();
				}
			}
			anularTbai(api, config, company, invoice);
			anularSii(api, config, company, invoice);
		}
		// ***********************************
		
		Attach attach = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
			f.getAttachModuleProperty().eq(invoiceId)
			.and(f.getTypeProperty().eq(InvoiceAttachmentType.INVOICE.value()))
			, AttachType.INVOICE, true);		

		// ids to null
		invoice.setId(null);
		invoice.detailStream()
			.map( d -> d.setId(null))
			.flatMap(d -> d.taxStream())
			.forEach(t -> t.setId(null)); 
		
		invoice.financeStream()
			.map( f -> f.setId(null))
			.forEach(t -> t.setId(null));
		
		// ----------
	
		Rawdoc rawdoc = new Rawdoc()
			.setData(attach.getData())
			.setDomain(invoice.getDomain())
			.setJson(InvoiceJSON.toJSON(invoice).toString())
			.setMimeType(attach.getMimeType())
			.setNature(RawdocNature.INVOICE)
			.setStatus(RawdocStatus.TRASH)
			.setType(invoice.isPurchase() ? RawdocType.INPUT : RawdocType.OUTPUT);
		AON.deleteInvoice(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), invoiceId);
		AON.rawdocSave(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), rawdoc);
		
		return new JSONObject();	
	}

	private static JSONObject getInvoice(AonApiData api, Integer invoiceId) {
		return getInvoice(api, invoiceId, null);
	}
	
	private static JSONObject giveBackInvoice(AONContext ctx, Invoice invoice) {
		JSONObject json = InvoiceJSON.toJSON(invoice);

		Integer invoiceId = invoice.getId();
		if (invoiceId != null) {
			Integer domainId = invoice.getDomain();
			// [START]
			// Cuando se grabe en invoice_info la información de los envios de ARABA y GIPUZKOA, el siguiente código debe borrarse.
			InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx,domainId, false);
			if (!icc.isBizkaia() && icc.isTbai()) {
				Map<InvoiceCommunicationType, InvoiceCommunicationHistoryMapValue> history = InvoiceCommunicator.history(ctx, icc, invoice );
				AonCollectionUtils.stream(history)
				.filter(e -> !invoice.hasInvoiceInfo(e.getKey()))
				.forEach(e -> invoice.putInvoiceInfo(e.getKey(), e.getValue().getInfo()) )
				;
			}
			// [START]
			// Este atributo debe modificarse a lo existente en InvoiceDOC
			json.put(IJsonNames.FILE, InvoiceJSONUtils.buildInvoiceFileJSON(ctx, invoice));
			// [END]
		}
		
		
		// [START]
		// Borrar
		System.out.println( " ****** REQUESTED INVOICE ("+ invoiceId+")");
		System.out.println( json.toString(1));
		System.out.println( " ****** ");
		// [END]
		
		return json;
		
	}
	private static JSONObject getInvoice(AonApiData api, Integer invoiceId, List<InvoiceError> previousErrors) {
		checkApiData(api);
		Domain domain = api.getDomain();
		String login = api.getUser().getLogin();
		Integer domainId = domain.getId();
		Occam occam = new Occam()
			.setDomainName(domain.getName())
			.setDomain(domainId)
			.setUser(login);
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			Invoice invoice = InvoiceDAO.getFullInvoice(ctx, invoiceId);
			if (invoice == null || invoice.getId() == null) {
				throw new AonApiException("Factura no encontrada");
			}
			AonCollectionUtils.stream(previousErrors).forEach(invoice::addMessage);
			return giveBackInvoice(ctx, invoice);
		}
	}
	
	private static JSONArray getInvoices(AonApiData api, InvoiceFilter filter) {
		if(!isRawdoc(filter.getStatus())) {
			Integer domainId = api.getDomain().getId();
			try(CloseableAONContext ctx = AONContext.getAONContext(api.getOccam())) {
				return InvoiceApiDAO.getInvoices(ctx, domainId, f -> AonApiServletUtils.invoiceFilter(f, domainId, filter))
					.map( i ->  invoiceList2JSON(ctx, i))
					.collect(Collector.of(JSONArray::new,JSONArray::put,(left, right) -> left, Collector.Characteristics.UNORDERED));
			}
		} else {
			return RawdocServlet.getRawdocs(api);
		}
	}
	
	private static JSONObject invoiceList2JSON(AONContext ctx, Invoice invoice) {
		
		String referenceAux = "";
		if(!AonStringUtils.isBlank(invoice.getSeries())) {
			referenceAux = referenceAux + invoice.getSeries() + "/";
		}
		referenceAux = referenceAux + "PROFORMA";
		JSONObject json = new JSONObject()
			.put(IJsonNames.ID, invoice.getId())
			.put(IJsonNames.DATE, invoice.getIssueDate())
			.put(IJsonNames.EXP_DATE, invoice.getExpDate())
			.put(IJsonNames.REFERENCE, invoice.getNumber() > 0 ? invoice.getReferenceCode() : referenceAux)
			.put(IJsonNames.NAME, invoice.getRegistryName())
			.put(IJsonNames.TOTAL, invoice.getTotal())
			.put(IJsonNames.STATUS, invoice.isRecorded() ? InvoiceStatus.SCORED.name().toLowerCase() : InvoiceStatus.PENDING.name().toLowerCase())
			.put(IJsonNames.TYPE, invoice.getType().getTediName())
			.put(IJsonNames.SERIES, invoice.getSeries())
			.put(IJsonNames.SERIE, invoice.getSeries())
			.put(IJsonNames.NUMBER, invoice.getNumber())
			.put(IJsonNames.SIGNED, invoice.isSigned())
			.put(IJsonNames.COMMUNICATION_INFO, InvoiceJSON.getCommunicationInfoJSON(invoice.getCommunicationInfo()).orElse(null));
		InvoiceDataDAO.get(ctx, invoice.getDomain(), invoice.getId(), InvoiceDataName.MD5)
			.filter(id -> AonStringUtils.isNotBlank(id.getValue()))
			.ifPresent(id -> {
				String md5 = AonDigestUtils.md5Hex(invoice.flat());
				json.put("altered", !md5.equalsIgnoreCase(id.getValue()));
			});
		return json;
	}

	// [END REMOVE]
	// -----------------------------------
	// -----------------------------------
	// -----------------------------------
	
	public static JSONObject rectifyInvoice(AonApiData api) throws Exception {
		JSONObject invoiceJSON = JsonUtils.getJSONObject( api.getData(), "invoice");
		String series = JsonUtils.getString( api.getData(), "series");
		Date date = JsonUtils.getDate( api.getData(), "date");
		String cause = JsonUtils.getString( api.getData(), "cause");
		
		Invoice invoice = InvoiceJSON.fromJSON(invoiceJSON);
		InvoiceRectificationData ird = new InvoiceRectificationData()
			.setType( invoice.getType())
			.setSeries(series)
			.setIssueDate(date)
			.setCause(cause)
			.setRectificationtype(RectificationType.NORMAL_RECTIFIER )
			.setSettleFinances(false)
		;
		try(CloseableAONContext ctx = AONContext.getAONContext(api.getDomain(), api.getUser())) {
			
			Invoice rectified = InvoiceDAO.rectifyInvoice(ctx, invoice.getId(), ird
				, false		// No se graba la factura rectificativa. Se devuelve para grabar en rawdoc.
			);
			
			return giveBackInvoice(ctx, rectified);
		}
	}
	
	public static JSONObject acceptInvoice(AonApiData api) throws Exception {
		Invoice invoice = InvoiceJSON.fromJSON(api.getData());
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		InvoiceCommunicationConfiguration icc = AON.getInvoiceCommunicationConfiguration(api.getOccam());
		
		if (invoice.isSales()) {
			// ***********************************
			// If the invoice is not a sales invoice or TBAI is not active, we accept and communicate the invoice
			if (icc.hasCommunication() && !icc.isSii() && !icc.isTbai() && !icc.isLroe()) {
				return acceptAndCommunicateInvoice(api, icc, company);	
			} 
			
			
			// ONLY TBAI & THIRD PART INVOICES
			if (AonStringUtils.contains( invoice.getReferenceCode(), "undefined")) {
				invoice.setReferenceCode(null);	
			}
				
			if( icc.isTbai() || icc.isLroe() || icc.isSii()) {
				Certificate certificate = checkCertificate(api);
				icc.setCertificate(certificate);
				invoiceValidation(invoice);
			}
				
			// ***********************************		
		}
		
		Integer rawdocId = invoice.getId();
		invoice.setRawdocId(invoice.getId());
		invoice.setId(null);
		invoice = AON.acceptInvoice(api.getOccam(), invoice, rawdocId);
		processInvoiceFile(api, invoice);
		acceptTbai(icc, company, invoice);
		acceptSii(api, icc, company, invoice);
		return getInvoice(api, invoice.getId(), invoice.messageStream().toList());
	}
	
	public static void processInvoiceFile(AonApiData api, Invoice invoice) {
		JSONObject fileJSON = JsonUtils.getJSONObject(api.getData(), IJsonNames.FILE);
		if(!fileJSON.isEmpty()) {
			String s3Key = JsonUtils.getString(fileJSON, IJsonNames.S3_KEY);
			String contentType = JsonUtils.getString(fileJSON, "content_type");
					
			if(s3Key != null) {
				MimeType mimetype = MimeType.safeValueFromContenType(contentType);
				InvoiceDoc invoiceDoc = new InvoiceDoc()
					.setExternalStorage(ExternalStorage.AWS)
					.setS3Bucket("aon-upload-post")
					.setS3Key(s3Key)
					.setInvoice(invoice.getId())
					.setMimeType(mimetype != null ? mimetype : MimeType.PDF)
					.setType(InvoiceAttachmentType.INVOICE)
					.setDomain(invoice.getDomain());
				AON.saveInvoiceDoc(api.getOccam(), invoiceDoc);
			}
		}
	}
	
	public static void acceptTbai(InvoiceCommunicationConfiguration icc, Company company,  Invoice invoice) throws Exception {
		if(invoice.isSales() && (icc.isTbai() || icc.isLroe())) {
			TbaiMain tbai = new TbaiMain();
			tbai.createEmisionTBAI(company, invoice, icc);
		}
	}
	
	public static void anularTbai(AonApiData api, InvoiceCommunicationConfiguration config, Company company,  Invoice invoice) throws Exception {
		if (config.isTbai() || config.isLroe()) {
			boolean accepted = true;
			if(config.isBizkaia()) {
				try(CloseableAONContext ctx = AONContext.getAONContext(api.getDomain(), api.getUser())) {
					accepted = InvoiceInfoDAO.getMap(ctx, config, invoice)
							.map( ic -> ic.get(InvoiceCommunicationType.LROE) )
							.filter( Objects::nonNull )
							.map(info -> info.isAccepted() || info.isAcceptedWithErrors())
							.orElse( true )
						;
				}
			}
			if (accepted) {
				config.setCertificate(checkCertificate(api));
				TbaiMain tbai = new TbaiMain();
				try {
					tbai.createAnulacionTBAI(company, invoice, config);
				} catch (Exception e) {
					e.printStackTrace();
					throw new AonApiException(e.getMessage());
				}
			}
		}
	}
	
	public static void acceptSii(AonApiData api, InvoiceCommunicationConfiguration icc, Company company,  Invoice invoice) throws Exception {
		if(invoice.isSales() && icc.isSii() && !icc.isTbai()) {
			try {
				SIIManager manager = SIIManager.getInstance(icc);
				
				AccountingReportParams params = new AccountingReportParams();
				params.setDomain(api.getOccam().getDomain());
				params.setInvoices(new Integer[] {invoice.getId()});
				LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(api.getOccam(), params, "")
						.collect(Collectors.toCollection(LinkedList::new));		
				manager.suministroFacturas(api.getDomain(), api.getUser().getLogin(), company, invoice, contextList, null);
			} catch (Exception e) {
				if (e instanceof InvoiceCommunicationException ice) {
					throw ice;
				} else {
					throw new InvoiceCommunicationException( e );
				}
			}
		}
	}
	
	public static void anularSii(AonApiData api, InvoiceCommunicationConfiguration icc, Company company,  Invoice invoice)  throws Exception {
		if(invoice.isSales() && icc.isSii() && !icc.isTbai()) {
			try {
				SIIManager manager = SIIManager.getInstance(icc);
				
				AccountingReportParams params = new AccountingReportParams();
				params.setDomain(api.getOccam().getDomain());
				params.setInvoices(new Integer[] {invoice.getId()});
				LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(api.getOccam(), params, "")
						.collect(Collectors.toCollection(LinkedList::new));		
				manager.bajaFacturas(api.getDomain(), api.getUser().getLogin(), company, invoice, contextList, null);
			} catch (Exception e) {
				if (e instanceof InvoiceCommunicationException ice) {
					throw ice;
				} else {
					throw new InvoiceCommunicationException( e );
				}
			}
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
		InvoiceCommunicationConfiguration icc = AON.getInvoiceCommunicationConfiguration(api.getOccam());
		String tbaiId = "";
		Date expDate = invoice.getExpDate() != null ? invoice.getExpDate() : invoice.getIssueDate();
		if(icc.isTbai(expDate) || icc.isLroe(expDate)) {
			try(CloseableAONContext ctx = AONContext.getAONContext(api.getDomain(), api.getUser())) {
				TbaiData tbaiData = TbaiData.getInstance(ctx, icc);
				String tbaiUrl = tbaiData.getTbaiUrl(api.getDomain().getId(), invoice.getId());
				qrUrl = AonStringUtils.isBlank(tbaiUrl) ? qrUrl : tbaiUrl;
				tbaiId = tbaiData.getTbaiId(api.getDomain().getId(), invoice.getId());				
			}

		} else if(icc.isVerifactu(expDate) || icc.isNoVerifactu(expDate)) {
			if (invoice.getCommunicationInfo() != null) {
				InvoiceInfo info = invoice.getCommunicationInfo().get(
					icc.isVerifactu(expDate) 
						? InvoiceCommunicationType.VERIFACTU 
						: InvoiceCommunicationType.NO_VERIFACTU);
				if (info != null) {
					qrUrl = info.getCheckUrl();
				}
			}
		}
		if(company.getRegistry().getDomain().isGarage()) {	
			invoice.detailStream().forEach(d -> {
				ProjectTas pt = AON.getProjectTas(api.getOccam(), f -> f.getDomainProperty().eq(invoice.getDomain())
						.and(f.getIdProperty().eq(d.getProject()))).orElse(null);
				if(pt != null)	d.setProjectName(d.getProjectName() + " - KMS. " + d.getProject());	
			});
		}
		
		Attach logo = new Attach();
		
		if(config.isLogo()) {
			Integer id = company.getRegistry().getId();
			logo = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f-> f.getAttachModuleProperty().eq(id)
				.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PdfMaker.printInvoice(out, company, icc, invoice, config, qrUrl, logo.getData(), tbaiId);
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
	
	public static File getFile(Drive drive, String id){
		File file  = new File();
		try {
			file = drive.files().get(id).setFields("webContentLink, webViewLink").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private JSONObject fix(AonApiData api) {
		AON.fixInvoice(api.getOccam(), api.getDomain().getId());
		return new JSONObject();
	}
	
	private JSONObject getApiConfiguration(AonApiData api) {
//		Date start1 = new Date();
//		Date end1 = new Date();
		Integer domainId = api.getDomain().getId();
		ApiConfiguration conf = AON.getApiConfiguration(api.getOccam(), domainId );
		return ApiConfigurationJSON.to(conf);
//		JSONObject json = ApiConfigurationJSON.to(conf);
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		System.out.println( "NEW Configuration secs: "  + (end1.getTime() - start1.getTime()) );
//		System.out.println( "[START] NEW Configuration *****" );
//		System.out.println( json.toString(1) );
//		System.out.println( "[END] NEW Configuration *****" );
//		System.out.println();
//		System.out.println();
//		System.out.println();
	}
	
	private JSONObject getConfiguration(AonApiData api) {
		Date start = new Date();
		ApplicationParameter defaultWithholdingPercent = AON.getApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), AppParam.ACC_DEFAULT_RETENTION_PERCENT);
		Integer withholdingPercentId = AonNumberUtils.toInteger(defaultWithholdingPercent.getValue());
		WithholdingType withholdingType = AON.getTax(api.getOccam(), api.getDomain().getId(), withholdingPercentId)
			.filter( t -> t.getWithholdingType() != null)
			.map(Tax::getWithholdingType)
			.orElse(WithholdingType.PROFESSIONAL);
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		List<RegistryAdditionalInfo> rais = new LinkedList<>();
		rais.add(RegistryAdditionalInfo.BANKS);
		JSONObject companyJSON = RegistryServlet.getRegistryAdditionalInfo(CompanyJSON.toJSON(company), api, api.getData(), company.getId(), rais);;
		Person person = AON.getPerson(api.getDomain(), api.getUser().getLogin(), f -> f.getIdProperty().eq(company.getId()));
		JSONObject personJSON = PersonJSON.toJSON(person);
		JSONObject json = new JSONObject();
		json.put(IJsonNames.PRINT, getPrintConfiguration(api));
		json.put(IJsonNames.COMPANY, companyJSON);
		json.put(IJsonNames.PERSON, personJSON);
		json.put(IJsonNames.E_INVOICE, company.iseInvoice());
		json.put(IJsonNames.COMMUNICATION, getInvoiceCommunicationConfiguration(api));
		json.put(IJsonNames.ADMINISTRATION, getAdministration(api));
		json.put("withholdingPercent", withholdingType.name());
		json.put(IJsonNames.INVOFOX, InvofoxServlet.getConfiguration(api));
		LinkedList<Workplace> workplaces = AON.getWorkplaces(api.getOccam(), api.getDomain().getId())
			.collect(Collectors.toCollection(LinkedList::new));
		json.put(IJsonNames.WORKPLACES, workplaces);
		json.put(IJsonNames.VATS, getVats(api));
		return json;
	}

	private JSONObject saveConfiguration(AonApiData api) {
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		company.seteInvoice(JsonUtils.getboolean(api.getData(), IJsonNames.E_INVOICE));
		if(AonStringUtils.isBlank(company.getDocument())) {
			Company c = CompanyJSON.fromJSON(JsonUtils.getJSONObject(api.getData(), IJsonNames.COMPANY));
			company.setDocument(c.getDocument());
		}
		
		if(AonStringUtils.isBlank(company.getName())) {
			Company c = CompanyJSON.fromJSON(JsonUtils.getJSONObject(api.getData(), IJsonNames.COMPANY));
			company.setName(c.getName());
		}
		
		AON.saveCompany(api.getDomain(), api.getUser(), company);
		if(JsonUtils.has(api.getData(), IJsonNames.PERSON)) {
			Person p = PersonJSON.fromJSON(JsonUtils.getJSONObject(api.getData(), IJsonNames.PERSON));
			p.copy(company);
			if(p.getDomain() == null) p.setDomain(company.getDomain());
			if(p.getGender() == null) p.setGender(Gender.UNKNOWN);
			if(p.getMaritalStatus() == null) p.setMaritalStatus(MaritalStatus.UNKNOWN);
			if(p.getId() == null) p.setId(company.getId());
			AON.savePerson(api.getDomain(), api.getUser().getLogin(), p);
		}

		Administration administration = saveAdministration(api, JsonUtils.getString(api.getData(), IJsonNames.ADMINISTRATION));

		JSONObject print = savePrintConfiguration(api, JsonUtils.getJSONObject(api.getData(), IJsonNames.PRINT));
		JSONObject icc = saveInvoiceCommunicationConfiguration(api, JsonUtils.getJSONObject(api.getData(), IJsonNames.COMMUNICATION));
		JSONObject invofox = JsonUtils.has(api.getData(), IJsonNames.INVOFOX) ? 
				InvofoxServlet.saveConfiguration(api.setData(JsonUtils.getJSONObject(api.getData(), IJsonNames.INVOFOX))) 
				: new JSONObject();
		
		return getConfiguration(api);
	}
	
	private JSONArray getVats(AonApiData api) {
		return TaxJSON.toJSON(AON.getTaxStream(api.getOccam(), api.getDomain().getId()));
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
	
	private JSONObject getInvoiceCommunicationConfiguration(AonApiData api) {
		InvoiceCommunicationConfiguration config = AON.getInvoiceCommunicationConfiguration(api.getOccam(), true);
		return InvoiceCommunicationConfigurationJSON.to(config).orElse(null);
	}
	
	private JSONObject saveInvoiceCommunicationConfiguration(AonApiData api, JSONObject json) {
		return InvoiceCommunicationConfigurationJSON.from(json)
			.map(icc -> AON.saveInvoiceCommunicationConfiguration(api.getOccam(), api.getDomain().getId(), icc))
			.flatMap(InvoiceCommunicationConfigurationJSON::to)
			.orElse(null);
	}

	private static RawdocStatus getRawdocStatus(String status) {
		return RawdocStatus.safeValueOf(status);
	}
	
	private static boolean isRawdoc(String status) {
		return getRawdocStatus(status) != null;
	}
	
	private static void invoiceValidation(Invoice invoice) throws Exception {
		checkInvoice(invoice);
		checkRegistry(invoice);
	}
	
	private static void checkTbaiId(Company company, Invoice invoice, String tbaiId) throws Exception {
		if(AonStringUtils.isBlank(tbaiId)) throw new Exception("El Identificador TicketBai está vacío.");
		if(tbaiId.length() != 39) throw new Exception("El Tamaño del Identificador TicketBai no es correcto.");
		String[] arr = tbaiId.split("-");
		if(arr.length != 5) throw new Exception("El Formato del Identificador TicketBai no es correcto."); 
		
		if(!arr[0].equalsIgnoreCase("TBAI")) throw new Exception("El Identificador TicketBai no es correcto.");
		if(!arr[1].equalsIgnoreCase(company.getDocument())) throw new Exception("El Emisor del Identificador TicketBai no coincide con el Documento del Emisor.");		
		
		Integer l = tbaiId.length() - 3;
		String t = tbaiId.substring(0, l);
		String c = tbaiId.substring(l);
		String crc = CRC8.calculate(t);
		if(!crc.equals(c)) {
			throw new Exception("El Identificador TicketBai no es correcto.");
		}
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

	private JSONArray getInvoiceSeries(AonApiData api) {
		return InvoiceSeriesJSON.to(FINANCE.getInvoiceSalesSeries(api.getOccam(), api.getDomain().getId()));	
	}
	
//	private JSONObject fixInvoice(AonApiData api) {
//		AON_SOLUTIONS.fixInvoice(api.getOccam());
//		return new JSONObject();
//	}
	
	// ************************************************************
	// ******************************* [ACCEPT AND COMMUNICATE] ***
	// ************************************************************
	public static JSONObject acceptAndCommunicateInvoice(AonApiData api, InvoiceCommunicationConfiguration config, Company company) throws InvoiceCommunicationException, InvoiceErrorException {
		checkApiData(api);
		Integer certId = JsonUtils.getInteger(api.getData(), IJsonNames.CERT);
		Invoice invoice = InvoiceJSON.from(api.getData())
			.orElseThrow(() -> new AonApiException("Invalid invoice data provided."));
		processInvoiceFile2(api, invoice);
		invoice.setRawdocId(invoice.getId());
		invoice.setId(null);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext communicator = new InvoiceCommunicatorContext(api.getDomain(), api.getUser(), certId, invoices)
			.setConfig(config)
			.setCompany(company);
		
		try {
			InvoiceCommunicator.acceptInvoice(communicator);
		} catch (Exception e) {
			InvoiceCommunicator.throwRightException( e, invoice  );
		}
		return getInvoice(api, invoice.getId(), invoice.messageStream().toList());
	}
	
	private static void processInvoiceFile2(AonApiData api, Invoice invoice) {
		JSONObject fileJSON = JsonUtils.getJSONObject(api.getData(), IJsonNames.FILE);
		if (!fileJSON.isEmpty()) {
			String s3Key = JsonUtils.getString(fileJSON, IJsonNames.S3_KEY);
			String contentType = JsonUtils.getString(fileJSON, "content_type");
			if(s3Key != null) {
				MimeType mimetype = MimeType.safeValueFromContenType(contentType);
				InvoiceDoc invoiceDoc = new InvoiceDoc()
					.setExternalStorage(ExternalStorage.AWS)
					.setS3Bucket("aon-upload-post")
					.setS3Key(s3Key)
					.setInvoice(invoice.getId())
					.setMimeType(mimetype != null ? mimetype : MimeType.PDF)
					.setType(InvoiceAttachmentType.INVOICE)
					.setDomain(invoice.getDomain());
				invoice.setDoc(invoiceDoc);
			}
		}
	}
	
	// ************************************************************
	// ******************************* [CANCEL AND COMMUNICATE] ***
	// ************************************************************
	
	private static void cancelAndCommunicateInvoice(AonApiData api, InvoiceCommunicationConfiguration config,Company company, Invoice invoice) throws InvoiceCommunicationException, InvoiceErrorException {
		checkApiData(api);
		Integer certId = JsonUtils.getInteger(api.getData(), IJsonNames.CERT);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = new InvoiceCommunicatorContext(api.getDomain(), api.getUser(), certId, invoices)
				.setConfig(config)
				.setCompany(company)
				.setPreserveRawdocOnDeletion(true);
		try {
			InvoiceCommunicator.cancelInvoice(icc);
		} catch (Exception e) {
			InvoiceCommunicator.throwRightException( e, invoice  );
//			if (invoice.hasMessages()) {
//				List<InvoiceError> errors = invoice.messageStream()
//					.collect(Collectors.toCollection(LinkedList::new));
//				e.printStackTrace();
//				throw new InvoiceErrorException(errors);
//			}
//
//			// Para evitar recursividad --- 
//			Set<Throwable> visited = new HashSet<>();
//			Throwable current = e;
//			while (current != null && !visited.contains(current)) {
//				visited.add(current);
//				current = current.getCause();
//			}
//			// ------------------------------			
//			throw AonCollectionUtils.stream(visited)
//				.filter( InvoiceCommunicationException.class::isInstance )
//				.map(ex -> (InvoiceCommunicationException) ex)
//				.findFirst()
//				.orElse( new InvoiceCommunicationException(e.getMessage()))
//			;
		}
	}
	
}
