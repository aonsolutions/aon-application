package net.aonsolutions.aon.api.servlet;
import static net.aonsolutions.invofox.OCRDocumentsParams.normalize;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvofoxConfigurationJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceErrorJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvofoxConfigurationDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.tedi.invofox.OCRInvalidValueException;
import net.aonsolutions.aon.tedi.invofox.OCRInvoiceBuilder;
import net.aonsolutions.aon.tedi.invofox.OCROwnerNotFoundException;
import net.aonsolutions.aon.tedi.invofox.OCRResult;
import net.aonsolutions.aon.tedi.invofox.OCRTooManyOwnersException;
import net.aonsolutions.aon.tedi.invofox.OCRUndefinedTypeException;
import net.aonsolutions.invofox.OCRCompanyParams;
import net.aonsolutions.invofox.OCRDocumentsParams;
import net.aonsolutions.invofox.OCRInvofox;
import net.aonsolutions.invofox.json.OCRDocumentJSON;
import net.aonsolutions.invofox.json.OCRNames;
import net.aonsolutions.invofox.model.OCRAddress;
import net.aonsolutions.invofox.model.OCRApiKey;
import net.aonsolutions.invofox.model.OCRApiKeyResponse;
import net.aonsolutions.invofox.model.OCRCompaniesResponse;
import net.aonsolutions.invofox.model.OCRCompany;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRDocumentResponse;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;
import net.aonsolutions.invofox.model.OCREndpoint;
import net.aonsolutions.invofox.model.OCREnvironment;
import net.aonsolutions.invofox.model.OCREnvironmentResponse;
import net.aonsolutions.invofox.model.OCRError;
import net.aonsolutions.invofox.model.OCREvent;
import net.aonsolutions.invofox.model.OCRField;
import net.aonsolutions.invofox.model.OCRInfoResponse;
import net.aonsolutions.invofox.model.OCRInvoice;
import net.aonsolutions.invofox.model.OCRLine;
import net.aonsolutions.invofox.model.OCRLogin;
import net.aonsolutions.invofox.model.OCRLoginToken;
import net.aonsolutions.invofox.model.OCRPage;
import net.aonsolutions.invofox.model.OCRSecurity;
import net.aonsolutions.invofox.model.OCRSeverity;
import net.aonsolutions.invofox.model.OCRType;
import net.aonsolutions.invofox.model.OCRUser;
import net.aonsolutions.invofox.model.OCRWebhook;
import net.aonsolutions.invofox.model.OCRWord;
import solutions.aon.aws.s3.S3;

@SuppressWarnings("serial")
@WebServlet(name = "InvofoxServlet", urlPatterns = {"/ms/api/invofox/*"})
public class InvofoxServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(InvofoxServlet.class.getName());
	private static final String WEBHOOK_URL = "https://7ocqe3muv7hdurqizaxljwsc4e0xcvgl.lambda-url.eu-west-1.on.aws";

	public static final String DOCUMENTS = "/";
	public static final String DOCUMENT = "/document";
	public static final String COUNT = "/count";
	public static final String TEXT_CONTENT = "/text_content";
	public static final String CONFIGURATION = "/configuration";
	public static final String ACCEPT = "/accept";
	public static final String RAWDOC = "/rawdoc";
	public static final String LOGIN = "/login";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);

			Object object = new AonRouting(api)
					.addRoute(DOCUMENTS, InvofoxServlet::getDocuments)
					.addRoute(DOCUMENT, InvofoxServlet::getDocument)
					.addRoute(TEXT_CONTENT, InvofoxServlet::getTextContent)
					.addRoute(CONFIGURATION, InvofoxServlet::getConfiguration)
					.addRoute(COUNT, InvofoxServlet::getCount)
					.apply();

			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);

			Object object = new AonRouting(api)
					.addRoute(LOGIN, InvofoxServlet::invofoxLogin)
					.addRoute(DOCUMENT, InvofoxServlet::updateDocument)
					.addRoute(CONFIGURATION, InvofoxServlet::saveConfiguration)
					.addRoute(ACCEPT, InvofoxServlet::acceptDocument)
					.addRoute(RAWDOC, InvofoxServlet::rawdocDocument)
					.apply();

			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private static JSONObject getDocument(AonApiData api) {
//		JSONObject params = api.getData();
//		String documentId = params.optString(IJsonNames.ID);
//		return getDocument(api.getDomain(), api.getUser(), documentId);	
		return rawdocDocument(api);
	}

	private static JSONObject rawdocDocument(AonApiData api) {
		JSONObject params = api.getData();
		String documentId = params.optString(IJsonNames.ID);
		JSONObject json = new JSONObject();
		Rawdoc rawdoc = AON.getRawdocStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getJsonProperty().like("%invofoxId%" + documentId + "%"))).findFirst().orElse(null);
		
		if(rawdoc == null) {
			json = getDocument(api.getDomain(), api.getUser(), documentId);

			RawdocType type = json.opt("type") != null && json.optString("type").equalsIgnoreCase("emitida") 
					? RawdocType.OUTPUT : RawdocType.INPUT;
			String ocrStatus = JsonUtils.getString(json, IJsonNames.STATUS);
			RawdocStatus status = getRawdocStatus(ocrStatus); 
			json.put("ocrStatus", ocrStatus);
			json.put(IJsonNames.STATUS, status.getTediName());

			
			rawdoc = new Rawdoc()
				.setDomain(api.getDomain().getId())
				.setNature(RawdocNature.INVOICE)
				.setType(type)
				.setStatus(status)
				.setJson(json.toString())
				.setS3Key(getS3Key(json))
				.setMimeType(getMimeType(json));
			
			rawdoc = AON.rawdocSave(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), rawdoc);
			json.put("id", rawdoc.getId()); 

			exportDocument(api, documentId);
		} else json = new JSONObject(rawdoc.getJson());
		
		return json;
	}
	
	private static RawdocStatus getRawdocStatus(String status) {
		Optional<OCRSeverity> ocrSeverity = OCRSeverity.safeValueOf(status);
		if(ocrSeverity.isPresent() && (
				OCRSeverity.pendingDecission.equals(ocrSeverity.get())
				|| OCRSeverity.rejected.equals(ocrSeverity.get()))) {
			return RawdocStatus.REJECTED;
		} else if(ocrSeverity.isPresent() && OCRSeverity.discarded.equals(ocrSeverity.get())) {
			return RawdocStatus.DRAFT;
		}
		return RawdocStatus.INBOX;
	}
	
	private static String getS3Key(JSONObject json) {
		if(JsonUtils.has(json, IJsonNames.FILE)) {
			JSONObject file = JsonUtils.getJSONObject(json, IJsonNames.FILE);
			return JsonUtils.getString(file, "s3Key");
		} else return null;
	}
	
	private static MimeType getMimeType(JSONObject json) {
		if(JsonUtils.has(json, IJsonNames.FILE)) {
			JSONObject file = JsonUtils.getJSONObject(json, IJsonNames.FILE);
			return MimeType.get(JsonUtils.getString(file, "content_type"));
		} else return null;
	}
	
	private static JSONObject acceptDocument(AonApiData api) {
		JSONObject params = api.getData();
		String documentId = params.optString(IJsonNames.ID);
		try (CloseableAONContext aonContext = AONContext.getAONContext(api.getDomain().getName(), api.getUser().getLogin())) {
			Company company = AON.getCompany( aonContext, f -> f.getDomainProperty().eq( api.getDomain().getId()));
			String companyDocument = company == null? null : company.getDocument(); 

			InvofoxConfiguration invofoxConfiguration = InvofoxConfigurationDAO.get(aonContext);
//			if(invofoxConfiguration.isAutoAccept()) {
				OCRDocumentResponse response = OCRInvofox.getDocument(invofoxConfiguration.getApiKey(),
						invofoxConfiguration.getApiUrl(), documentId);
				
				OCRDocument ocrDocument = response.getDocument().orElseThrow(RuntimeException::new);
				OCRInvoice ocrInvoice = ocrDocument.getData().orElseThrow(RuntimeException::new);
				Invoice inv = response.getDocument().map( doc -> InvofoxServlet.toInvoice(doc,companyDocument))
						.map(invoice -> fillRegistry(aonContext, ocrInvoice, invoice))
						.map(invoice -> OCRInvoiceBuilder.guessItemsOrAccounts(aonContext, invoice))
						.map(invoice -> fillFinances(aonContext, ocrInvoice, invoice))
						.map(invoice -> fillCategory(aonContext, invoice))
						.map(invoice -> fillActivity(aonContext, invoice))
						.orElse(null);

				OCRSeverity publicState = ocrDocument.getPublicState().orElse(null);
				if (inv != null && publicState != null && OCRSeverity.approved.equals(publicState)
						&& inv.getTediCategory() != null) {
					inv = AON.acceptInvoice(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
							inv, null);
					if (inv != null && inv.getId() != null) {
						api.getData().put("file", getFileJSON(ocrDocument));
						InvoiceServlet.processInvoiceFile(api, inv);
						exportDocument(api, documentId);
					}

					if(invofoxConfiguration.isAutoRecord()) {					
						// TODO RECORD INVOICE!
					}
				} else rawdocDocument(api);
//			}
			
			return new JSONObject();
		}
	}
	
	private static void exportDocument(AonApiData api, String documentId) {
		JSONObject data = new JSONObject();
		data.put("_id", documentId);
		data.put("publicState", "exported");
		api.setData(data);
		updateDocument(api);
	}

	public static JSONObject getDocument(Domain domain, User user, String documentId) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domain.getName(), user.getLogin())) {
			Company company = AON.getCompany( aonContext, f -> f.getDomainProperty().eq( domain.getId()));
			String companyDocument = company == null? null : company.getDocument();
			
			InvofoxConfiguration invofoxConfiguration = InvofoxConfigurationDAO.get(aonContext);
			OCRDocumentResponse response = OCRInvofox.getDocument(invofoxConfiguration.getApiKey(),
					invofoxConfiguration.getApiUrl(), documentId);
			OCRDocument ocrDocument = response.getDocument().orElseThrow(RuntimeException::new);
			OCRInvoice ocrInvoice = ocrDocument.getData().orElseThrow(RuntimeException::new);
			String token = OCRInvofox.getLoginToken(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl())
					.getLoginToken().orElse(new OCRLoginToken()).getToken().orElse(null);

			return response.getDocument()
					.map(doc -> InvofoxServlet.toInvoice(doc,companyDocument))
					.map(invoice -> fillRegistry(aonContext, ocrInvoice, invoice))
					.map(invoice -> fillReferenceCode(ocrInvoice, invoice))
					.map(invoice -> OCRInvoiceBuilder.guessItemsOrAccounts(aonContext, invoice))
					.map(invoice -> fillFinances(aonContext, ocrInvoice, invoice))
					.map(invoice -> fillCategory(aonContext, invoice))
					.map(invoice -> fillActivity(aonContext, invoice))
					.map(InvoiceJSON::toJSON)
					.map(invoice -> invoice.put("token", token))
					.map(invoice -> invoice.put("file", getFileJSON(ocrDocument)))
					.map(invoice -> invoice.put("messages", getMessages(ocrDocument, company)))
					.map(invoice -> invoice.put("status", toString(ocrDocument.getPublicState().orElse(OCRSeverity.error))))
					.map(invoice -> invoice.put("insight", new JSONObject().put("invofoxId", ocrDocument.getId().orElse(""))))
					.orElseThrow(() -> new AonApiException("No such document"));
		}
	}

	private static JSONObject getTextContent(AonApiData api) {
		JSONObject params = api.getData();
		String documentId = params.optString(IJsonNames.ID);
		InvofoxConfiguration invofoxConfiguration = AON.getInvofoxConfiguration(api.getDomain(),
				api.getUser().getLogin());
		OCRInfoResponse ocrInfoResponse = OCRInvofox.getOcrInfo(invofoxConfiguration.getApiKey(),
				invofoxConfiguration.getApiUrl(), documentId);

		Stream<OCRPage> ocrPages = Arrays.stream(ocrInfoResponse.getPages().orElse(new OCRPage[0]));

		List<JSONObject> pages = ocrPages
				.map(page -> new JSONObject()
						.put("page", page.getPage().orElse(0))
						.put("angle", page.getAngle().orElse(BigDecimal.ZERO))
						.put("width", page.getWidth().orElse(BigDecimal.ZERO))
						.put("height", page.getHeight().orElse(BigDecimal.ZERO))
						.put("items", getTextItems(page)))
				.toList();
		return new JSONObject().put("pages", pages);

	}

	private static JSONArray getDocuments(AonApiData api) {
		try ( CloseableAONContext aonContext = AONContext.getAONContext(api.getDomain().getName(), api.getUser().getLogin())) {
			JSONArray array = new JSONArray();
			Integer page = JsonUtils.getInteger(api.getData(), IJsonNames.PAGE);
			Integer perPage = JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE);
			JSONArray publicStates = JsonUtils.getJSONArray(api.getData(), IJsonNames.PUBLIC_STATE);
			if (publicStates == null) {
				publicStates = new JSONArray().put(JsonUtils.getString(api.getData(), IJsonNames.PUBLIC_STATE));
			}
			Optional<OCRType> type = OCRType.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.TYPE));
			String companyActsLike = JsonUtils.getString(api.getData(), IJsonNames.COMPANY_ACTS_LIKE);
			InvofoxConfiguration invofoxConfiguration = AON.getInvofoxConfiguration(aonContext);
			String token = OCRInvofox.getLoginToken(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl())
					.getLoginToken().orElse(new OCRLoginToken()).getToken().orElse(null);
			String token2 = OCRInvofox
					.getLogin(invofoxConfiguration.getUser(), invofoxConfiguration.getPass(), invofoxConfiguration.getApiUrl()).getLogin()
					.orElse(new OCRLogin()).getToken().orElse(null);

			Company cp = AON.getCompany(aonContext,f -> f.getDomainProperty().eq(api.getDomain().getId()));
			if (!AonStringUtils.isBlank(cp.getDocument())) {
				OCRCompaniesResponse companiesResponse = OCRInvofox.getCompanies(invofoxConfiguration.getApiKey(),
						invofoxConfiguration.getApiUrl(), OCRCompanyParams.get().withTaxId(cp.getDocument()));
				List<OCRCompany> companies = companiesResponse.getCompanies().orElse(new LinkedList<>());
				if (!companies.isEmpty()) {
					OCRCompany ocrCompany = companies.get(0);
					OCRDocumentsParams ocrDocumentParams = OCRDocumentsParams.get();
					type.ifPresent(ocrDocumentParams::withType);
					ocrDocumentParams.withEnvironment(invofoxConfiguration.getEnvironment());
					ocrDocumentParams.sort(OCRNames.CREATION, OCRDocumentsParams.DESC);
					ocrDocumentParams.withCompany(ocrCompany.getId()).skiping(page * perPage);
					ocrDocumentParams.withCompanyActsLike(companyActsLike);
					publicStates.forEach(publicState -> OCRSeverity.safeValueOf((String) publicState)
							.ifPresent(ocrDocumentParams::withPublicState));
					ocrDocumentParams.limit(perPage);
					OCRDocumentsResponse response = OCRInvofox.getDocumentsWithToken(token2,
							invofoxConfiguration.getApiUrl(), ocrDocumentParams);
					response.getDocuments().orElse(new LinkedList<>()).stream().forEach(r -> {
						OCRResult result = OCRInvoiceBuilder.toInvoice(aonContext, r );
						Invoice invoice = result.getInvoice();
						JSONObject json = new JSONObject();
						json.put("id", r.getId().get());
						json.put("reference", invoice.isSales() ? invoice.getDocumentNumber() : invoice.getReferenceCode());
						json.put("name", invoice.getRegistryName());
						Date issueDate = invoice.getIssueDate();
						if (issueDate != null) {
							SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
							json.put("date", f.format(invoice.getIssueDate()));
						}
						json.put("total",invoice.getTotal());
						json.put("token", token);
						json.put("status", toString(r.getPublicState().orElse(OCRSeverity.error)));
						json.put("invofox", true);
						array.put(json);
					});
				}
			}
			return array;
		}
	}

	public static JSONObject getCount(AonApiData api) {
		JSONObject json = new JSONObject();

		JSONArray publicStates = JsonUtils.getJSONArray(api.getData(), IJsonNames.PUBLIC_STATE);
		if (publicStates == null) {
			publicStates = new JSONArray().put(JsonUtils.getString(api.getData(), IJsonNames.PUBLIC_STATE));
		}

		Optional<OCRType> type = OCRType.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.TYPE));
		String companyActsLike = JsonUtils.getString(api.getData(), IJsonNames.COMPANY_ACTS_LIKE);
		InvofoxConfiguration invofoxConfiguration = AON.getInvofoxConfiguration(api.getDomain(), api.getUser().getLogin());
		String token = OCRInvofox
				.getLogin(invofoxConfiguration.getUser(), invofoxConfiguration.getPass(), invofoxConfiguration.getApiUrl()).getLogin()
				.orElse(new OCRLogin()).getToken().orElse(null);
		Company cp = AON.getCompany(api.getDomain(), api.getUser(),
				f -> f.getDomainProperty().eq(api.getDomain().getId()));
		if (!AonStringUtils.isBlank(cp.getDocument())) {
			OCRCompaniesResponse companiesResponse = OCRInvofox.getCompanies(invofoxConfiguration.getApiKey(),
					invofoxConfiguration.getApiUrl(), OCRCompanyParams.get().withTaxId(cp.getDocument()));
			List<OCRCompany> companies = companiesResponse.getCompanies().orElse(new LinkedList<>());
			if (!companies.isEmpty()) {
				OCRCompany ocrCompany = companies.get(0);
				OCRDocumentsParams ocrDocumentParams = OCRDocumentsParams.get();
				type.ifPresent(ocrDocumentParams::withType);
				ocrDocumentParams.sort(OCRNames.CREATION, OCRDocumentsParams.DESC);
				ocrDocumentParams.withCompany(ocrCompany.getId());
				ocrDocumentParams.withEnvironment(invofoxConfiguration.getEnvironment());
				ocrDocumentParams.withCompanyActsLike(companyActsLike);
				publicStates.forEach(publicState -> OCRSeverity.safeValueOf((String) publicState)
						.ifPresent(ocrDocumentParams::withPublicState));
				ocrDocumentParams.limit(1);
				OCRDocumentsResponse response = OCRInvofox.getDocumentsWithToken(token,
						invofoxConfiguration.getApiUrl(), ocrDocumentParams);

				json.put("count", response.getCount().orElse(0));
			}
		}

		return json;
	}

	public static JSONObject invofoxLogin(AonApiData api) {
		String user = JsonUtils.getString(api.getData(), IJsonNames.USER);
		String password = JsonUtils.getString(api.getData(), IJsonNames.PASSWORD);
		OCRLogin ocrLogin = OCRInvofox
			.getLogin(user, password, InvofoxConfiguration.DEFAULT_API_URL).getLogin()
			.orElse(new OCRLogin());		

		String token = ocrLogin.getToken().orElse(null);
		String account = ocrLogin.getUser().orElse(new OCRUser()).getAccount().orElse(null);
		
		if(token != null && account != null) {
			List<OCREnvironment> environments = OCRInvofox.getEnvironments(token, account).getEnvironments();
			if(!environments.isEmpty()) {
				OCREnvironment environment = environments.getFirst();
				
				OCRApiKey apikey = environment.getApikeys().stream().filter(f -> f.isActive()).findFirst().orElse(new OCRApiKey());
				if(AonStringUtils.isBlank(apikey.getKey())) {
					OCRApiKeyResponse resp = OCRInvofox.createApikey(token, environment.getId());
					apikey = resp.getApikey().orElse(new OCRApiKey());
				}

				OCRWebhook webhook = environment.getWebhooks().stream().filter(f -> WEBHOOK_URL.equals(f.getEndpoint().getUrl())).findFirst().orElse(null);
				if(webhook == null) {
					webhook = new OCRWebhook()
						.setEndpoint(new OCREndpoint()
							.setHeaders(new LinkedList<>())
							.setMethod("POST")
							.setUrl(WEBHOOK_URL))
						.setEvents(OCREvent.getValues())
						.setSecurity(new OCRSecurity()
								.setAlgorithm("sha256")
								.setSecret(""))
						.setActive(true);
						
					OCRInvofox.createWebhook(token, environment.getId(), webhook);
				}
				
				InvofoxConfiguration invofoxConfiguration = new InvofoxConfiguration()
						.setPersonalized(true)
						.setUser(user)
						.setPass(password)
						.setApiKey(apikey.getKey())
						.setEnvironment(environment.getId());
				invofoxConfiguration = AON.saveInvofoxConfiguration(api.getDomain(), api.getUser().getLogin(), invofoxConfiguration);
				return InvofoxConfigurationJSON.toJSON(invofoxConfiguration);	
			} else throw new AonApiException("No se ha encontrado ningún entorno en Invofox");
		} else throw new AonApiException("Error al iniciar sesión en Invofox");
	}
	
	public static JSONObject getConfiguration(AonApiData api) {
		InvofoxConfiguration invofoxConfiguration = AON.getInvofoxConfiguration(api.getDomain(), api.getUser());
		JSONObject invofoxConfigurationJSON = InvofoxConfigurationJSON.toJSON(invofoxConfiguration);
		if(!invofoxConfiguration.isLoginRequired()) {
			OCRLogin ocrLogin = OCRInvofox
					.getLogin(invofoxConfiguration.getUser(), invofoxConfiguration.getPass(), invofoxConfiguration.getApiUrl()).getLogin()
					.orElse(new OCRLogin());
			
			String token = ocrLogin.getToken().orElse(null);
			String account = ocrLogin.getUser().orElse(new OCRUser()).getAccount().orElse(null);
			
			if(token != null && account != null) {
				JSONArray environments = new JSONArray();
				
				OCRInvofox.getEnvironments(token, account).getEnvironments().stream().forEach(env-> {
					OCRApiKey apikey = env.getApikeys().stream().filter(f -> f.isActive()).findFirst().orElse(new OCRApiKey());
					if(AonStringUtils.isBlank(apikey.getKey())) {
						OCRApiKeyResponse resp = OCRInvofox.createApikey(token, env.getId());
						apikey = resp.getApikey().orElse(new OCRApiKey());
					}
					JSONObject envJSON = new JSONObject();
					envJSON.put(IJsonNames.ID, env.getId());
					envJSON.put(IJsonNames.NAME, env.getName());
					envJSON.put(IJsonNames.API_KEY, apikey.getKey());
					environments.put(envJSON);
				});
				invofoxConfigurationJSON.put("environments", environments);
			}
		}

		return invofoxConfigurationJSON;
	}
	
	public static JSONObject saveConfiguration(AonApiData api) {
		InvofoxConfiguration invofoxConfiguration = InvofoxConfigurationJSON.fromJSON(api.getData());
		AON.saveInvofoxConfiguration(api.getDomain(), api.getUser(), invofoxConfiguration);
		
		invofoxConfiguration = AON.getInvofoxConfiguration(api.getDomain(), api.getUser());

		OCRLogin ocrLogin = OCRInvofox
				.getLogin(invofoxConfiguration.getUser(), invofoxConfiguration.getPass(), invofoxConfiguration.getApiUrl()).getLogin()
				.orElse(new OCRLogin());
		
		String token = ocrLogin.getToken().orElse(null);
		
		if(token != null) {
			OCREnvironmentResponse response = OCRInvofox.getEnvironment(token, invofoxConfiguration.getEnvironment());
			response.getEnvironment().ifPresent(r -> {
				OCRApiKey apikey = r.getApikeys().stream().filter(f -> f.isActive()).findFirst().orElse(null);
				if(apikey == null) {
					OCRInvofox.createApikey(token, r.getId());
				}

//	TODO revisar...
//				
//				OCRWebhook webhook = r.getWebhooks().stream().filter(f -> WEBHOOK_URL.equals(f.getEndpoint().getUrl())).findFirst().orElse(null);
//				if(webhook == null) {
//					webhook = new OCRWebhook()
//						.setEndpoint(new OCREndpoint()
//							.setHeaders(new LinkedList<>())
//							.setMethod("POST")
//							.setUrl(WEBHOOK_URL))
//						.setEvents(OCREvent.getValues())
//						.setSecurity(new OCRSecurity()
//								.setAlgorithm("sha256")
//								.setSecret(""))
//						.setActive(true);
//						
//					OCRInvofox.createWebhook(token, r.getId(), webhook);
//				}
			});
		}
		
		return InvofoxConfigurationJSON.toJSON(invofoxConfiguration);
	}
	
	private static JSONObject updateDocument(AonApiData api) {
		OCRDocument document = OCRDocumentJSON.from(api.getData());
		InvofoxConfiguration invofoxConfiguration = AON.getInvofoxConfiguration(api.getDomain(), api.getUser().getLogin());
		OCRDocumentResponse response = OCRInvofox.putDocument(invofoxConfiguration.getApiKey(),
				invofoxConfiguration.getApiUrl(), document);
		return response.getDocument().map(OCRDocumentJSON::to).orElseThrow(RuntimeException::new);
	}

//	INVOICE_DOMAIN
//	.andThen(INVOICE_TRANSACTION)
//	.andThen(INVOICE_ISSUE_DATE)
//	.andThen(INVOICE_TYPE)
//	.andThen(INVOICE_REGISTRY_DOCUMENT)
//	.andThen(INVOICE_REGISTRY)
//	.andThen(INVOICE_REFERENCE_CODE)
//	.andThen(INVOICE_BREAKDOWN)
//	.andThen(INVOICE_WITHOLDING)
//	.andThen(INVOICE_DETAILS)
//	.andThen(INVOICE_TOTAL)
//	.andThen(INVOICE_TAXABLE_BASE)
//	.andThen(INVOICE_VAT_QUOTA)
//	.andThen(INVOICE_RETENTION_QUOTA)
//	.andThen(INVOICE_FINANCES)
	private static Invoice toInvoice(OCRDocument ocrDocument, String companyDocument) {
		Invoice invoice = new Invoice();

		OCRInvoice ocrInvoice = ocrDocument.getData().orElseThrow(RuntimeException::new);

		OCRInvoiceBuilder.fillTransaction(ocrInvoice, invoice);
		try {
			OCRInvoiceBuilder.fillIssueDate(ocrInvoice, invoice);
		} catch (OCRInvalidValueException e) {
		}
		invoice.setType(InvoiceType.EXPENSES);
		OCRInvoiceBuilder.fillInvoiceType(ocrDocument, ocrInvoice, companyDocument, invoice);
		try {
			OCRInvoiceBuilder.fillRegistryDocument(ocrInvoice, invoice);
		} catch (OCRUndefinedTypeException e) {

		}
		
		fillReferenceCode(ocrInvoice, invoice);
		OCRInvoiceBuilder.fillBreakdown(ocrInvoice, invoice);
		OCRInvoiceBuilder.fillWithHolding(ocrInvoice, invoice);
		OCRInvoiceBuilder.fillDetails(ocrInvoice, invoice);

		OCRInvoiceBuilder.fillTotal(ocrInvoice, invoice);
		OCRInvoiceBuilder.fillTaxableBase(ocrInvoice, invoice);
		OCRInvoiceBuilder.fillVatQuota(ocrInvoice, invoice);
		OCRInvoiceBuilder.fillRetentionQuota(ocrInvoice, invoice);

		if (invoice.mustApplyISP()) {
			invoice.setBreakdown(invoice.getBreakdown().stream().map(r -> {
				if (r.getPercentage() == 0.0) {
					r.setPercentage(21.0);
					r.setQuota(AonMathUtils.round(r.getBase() * 0.21));
				}
				return r;
			}).toList());
			invoice.setDetails(invoice.getDetails().stream().map(detail -> {
				if (detail.getInvoiceTaxes().isEmpty()) {
					double amount = detail.getPrice() * detail.getQuantity() * (1 - detail.getDiscount() / 100);
					InvoiceTax invoiceTax = new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(amount)
							.setPercentage(21.0)
							.setQuota(AonMathUtils.round(amount * 0.21))
							.setVatDeductionType(VatDeductionType.WITH_RIGHT).setDeductiblePercent(100)
							.setDeductibleQuota(AonMathUtils.round(amount * 0.21));
					detail.addInvoiceTax(invoiceTax);
				}
				return detail;
			}).collect(Collectors.toCollection(LinkedList::new)));
		}
		return invoice;

	}

	private static Invoice fillReferenceCode(OCRInvoice ocrInvoice, Invoice invoice) {
		Optional<String> optReference = ocrInvoice.getReferenceCode();
		if (optReference.isPresent()) {
			invoice.setReferenceCode(optReference.orElse(null));		
		}
		return invoice;
	}

	private static final JSONObject getFileJSON(OCRDocument ocrDocument) {
		JSONObject jsonObject = new JSONObject();
		ocrDocument.getClientData().ifPresent(clientData -> clientData.getS3Object()
				.ifPresent(s3Object -> s3Object.getBucket().ifPresent(bucketName -> s3Object.getKey().ifPresent(key -> {
					URL url = S3.getURL(bucketName, key);
					jsonObject.put("url", url.toExternalForm());
					jsonObject.put("path", url.toExternalForm());
					String contentType = S3.getContentType(bucketName, key);
					jsonObject.put("content_type", contentType);
					jsonObject.put("s3Bucket", key);
					jsonObject.put("s3Key", key);
				}))));
		return jsonObject;
	}

	private static final Invoice fillFinances(AONContext ctx, OCRInvoice ocrInvoice, Invoice invoice) {
		OCRInvoiceBuilder.fillFinances(ctx, ocrInvoice, invoice);
		return invoice;
	}

	private static final Invoice fillRegistry(AONContext ctx, OCRInvoice ocrInvoice, Invoice invoice) {
		try {
			OCRInvoiceBuilder.fillRegistry(ctx, AON.getConfiguration(ctx), invoice);
		} catch (OCROwnerNotFoundException | OCRTooManyOwnersException e) {
			Registry registry = new Registry().setDocument(invoice.getRegistryDocument())
					.setDocumentType(invoice.getRegistryDocumentType())
					.setDocumentCountry(invoice.getRegistryDocumentCountry());
			invoice.getType().visit(invoice, new IInvoiceTypeVisitor<Void>() {

				@Override
				public Void visitUndeductible(Invoice invoice) {
					return visitPurchase(invoice);
				}

				@Override
				public Void visitSales(Invoice invoice) {
					ocrInvoice.getRecipientName().ifPresent(name -> name.getValue().ifPresent(registry::setName));
					ocrInvoice.getRecipientCountry().ifPresent(country -> country.getValue().map(Country::safeValueOf)
							.ifPresent(registry::setNationality));
					ocrInvoice.getRecipientAddressDetails().ifPresentOrElse(details -> {
						RegistryAddress registryAddress = toRegistryAddress(details);
						if(registryAddress.getDomain() == null) registryAddress.setDomain(invoice.getDomain());
						invoice.setAddress(registryAddress);
					}, () -> {

					});
					return null;
				}

				@Override
				public Void visitPurchase(Invoice invoice) {
					ocrInvoice.getIssuerName().ifPresent(name -> name.getValue().ifPresent(registry::setName));
					ocrInvoice.getIssuerCountry().ifPresent(country -> country.getValue().map(Country::safeValueOf)
							.ifPresent(registry::setNationality));
					ocrInvoice.getIssuerAddressDetails().ifPresentOrElse(details -> {
						RegistryAddress registryAddress = toRegistryAddress(details);
						invoice.setAddress(registryAddress);
					}, () -> {

					});
					return null;
				}

				@Override
				public Void visitExpenses(Invoice invoice) {
					return visitPurchase(invoice);
				}

				private RegistryAddress toRegistryAddress(OCRAddress details) {
					RegistryAddress registryAddress = new RegistryAddress();

					details.getPostalCode().ifPresent(registryAddress::setZip);
					details.getMunicipality().ifPresent(registryAddress::setCity);
					details.getCountry().map(Country::safeValueOf).ifPresent(registryAddress::setCountry);
					details.getStreet().ifPresent(registryAddress::setAddress);
					details.getAddressNumber().ifPresent(registryAddress::setAddress2);
					details.getNeighborhood().ifPresent(registryAddress::setAddress3);
					details.getRegion().ifPresent(registryAddress::setProvince);

					return registryAddress;
				}
			});
			invoice
					// .setRegistry(ar.getId())
					// .setTransaction(ar.getTransaction())
					.setRegistryData(registry);
		}
		return invoice;
	}

	private static Invoice fillCategory(AONContext ctx, Invoice invoice) {
		if (invoice.getRegistry() == null)
			return invoice;
		List<Account> accounts = AccountingInvoiceDAO.getSuggestedAccounts(ctx, invoice.getRegistry(), invoice.getType());
		if (accounts.isEmpty() && InvoiceType.EXPENSES.equals(invoice.getType())) {
			accounts = AccountingInvoiceDAO.getSuggestedAccounts(ctx, invoice.getRegistry(), InvoiceType.PURCHASE);
			if (!accounts.isEmpty())
				invoice.setType(InvoiceType.PURCHASE);
		} else if (accounts.isEmpty() && InvoiceType.PURCHASE.equals(invoice.getType())) {
			accounts = AccountingInvoiceDAO.getSuggestedAccounts(ctx, invoice.getRegistry(), InvoiceType.EXPENSES);
			if (!accounts.isEmpty())
				invoice.setType(InvoiceType.EXPENSES);
		}
		if (!accounts.isEmpty()) {
			Account account = accounts.get(0);
			invoice.setTediCategory(account.getCode());
			invoice.getDetails().stream().forEach(d -> d.setExpAccount(account));
		}
		return invoice;
	}

	private static Invoice fillActivity(AONContext ctx, Invoice invoice) {
		if (invoice.getActivity().isEmpty()) {
			AonConfiguration config = AON.getConfiguration(ctx);
			invoice.setActivity(config.getMainActivity());
		}
		return invoice;
	}

	private static final JSONArray getMessages(OCRDocument ocrDocument, Company company) {
		List<JSONObject> messages = new LinkedList<>();
		ocrDocument.getValidationInfo()
				.ifPresent(validationInfo -> validationInfo.getErrors()
						.ifPresent(errors -> errors.forEach(ocrError -> getMessages(ocrError)
								.forEach(message -> messages.add(InvoiceErrorJSON.toJSON(message))))));

		ocrDocument.getData().ifPresent(ocrInvoice -> getMessages(ocrInvoice, company)
				.forEach(message -> messages.add(InvoiceErrorJSON.toJSON(message))));

		return new JSONArray(messages);
	}

	private static final Collection<InvoiceError> getMessages(OCRInvoice ocrInvoice, Company company) {

		if (AonStringUtils.isBlank(ocrInvoice.getIssuerDocument()))
			return Collections.emptyList();
		if (AonStringUtils.equals(normalize(ocrInvoice.getIssuerDocument()), company.getDocument()))
			return Collections.emptyList();
		if (AonStringUtils.equals(normalize(ocrInvoice.getRecipientDocument()), company.getDocument()))
			return Collections.emptyList();

		System.out.println(company.getDocument() + " = " + ocrInvoice.getIssuerDocument() + " = "
				+ ocrInvoice.getRecipientDocument());

		return Collections
				.singleton(new InvoiceError().setLevel(InvoiceErrorLevel.ERR).setCode("ERROR_ISSUER_RECIPIENT_MISMATCHED")
						.setMessage("Ni el emisor ni el receptor coinciden con la empresa."));

	}

	private static final Collection<InvoiceError> getMessages(OCRError ocrError) {

		Collection<InvoiceError> messages = ocrError.getFields().orElse(Collections.emptyList()).stream().map(ocrField -> {
			InvoiceError invoiceError = new InvoiceError();
			invoiceError.setLevel(getInvoiceErrorLevel(ocrError));
			ocrError.getCode().ifPresent(invoiceError::setCode);
			getInvoiceErrorContext(ocrField).ifPresent(invoiceError::setContext);
			ocrError.getDescription().ifPresent(invoiceError::setMessage);
			return invoiceError;
		}).collect(Collectors.toMap(InvoiceError::getMessage, err -> err, (err1, err2) -> err2)).values();

		if (!messages.isEmpty()) {
			return messages;
		}

		InvoiceError invoiceError = new InvoiceError();
		invoiceError.setLevel(getInvoiceErrorLevel(ocrError));
		ocrError.getCode().ifPresent(invoiceError::setCode);
		ocrError.getDescription().ifPresent(invoiceError::setMessage);
		return Collections.singletonList(invoiceError);

	}

	private static final InvoiceErrorLevel getInvoiceErrorLevel(OCRError ocrError) {
		OCRSeverity severity = ocrError.getSeverity().orElse(OCRSeverity.error);
		switch (severity) {
		case approved:
		case exported:
		case processing:
			return InvoiceErrorLevel.INF;
		case error:
		case rejected:
		case discarded:
			return InvoiceErrorLevel.ERR;
		case pendingDecission:
		case pendingCorrection:
			return InvoiceErrorLevel.WRN;
		default:
			return InvoiceErrorLevel.ERR;
		}
	}

	private static final Optional<InvoiceErrorContext> getInvoiceErrorContext(OCRField ocrField) {
		InvoiceErrorKey invoiceErrorKey = getInvoiceErrorKey(ocrField);
		if (invoiceErrorKey == null) {
			return Optional.empty();
		}

		InvoiceErrorContext invoiceErrorContext = new InvoiceErrorContext();
		invoiceErrorContext.setKey(invoiceErrorKey);

		ocrField.getIndex().ifPresent(invoiceErrorContext::setLine);

		return Optional.of(invoiceErrorContext);
	}

	private static final InvoiceErrorKey getInvoiceErrorKey(OCRField ocrField) {
		String fieldName = ocrField.getName().orElse("");
		switch (fieldName) {
		case "documentNumber":
			return InvoiceErrorKey.REFERENCE_CODE;
		case "issueDate":
			return InvoiceErrorKey.ISSUE_DATE;
		case "issuerName":
			return InvoiceErrorKey.RNAME;
		case "issuerTaxId":
			return InvoiceErrorKey.RDOCUMENT;
		case "issuerCountry":
			return InvoiceErrorKey.RDOCUMENT_COUNTRY;
		case "issuerAddress":
		case "issuerAddressDetails":
			return InvoiceErrorKey.ADDRESS;
		case "invoiceRef":
			return InvoiceErrorKey.REFERENCE_CODE;
		case "seriesCode":
			return InvoiceErrorKey.SERIES;
		case "taxRate":
			return InvoiceErrorKey.TAX_RATE;
		case "taxAmount":
		case "totalTaxAmount":
			return InvoiceErrorKey.TAX_QUOTA;
		case "taxBaseAmount":
		case "totalTaxBaseAmount":
			return InvoiceErrorKey.TAX_BASE;
		case "totalAmount":
			return InvoiceErrorKey.TOTAL;
		case "withholdingTaxAmount":
			return InvoiceErrorKey.IRPF_QUOTA;
		case "withholdingTaxRate":
			return InvoiceErrorKey.IRPF_RATE;
		case "paymentMethod":
			return InvoiceErrorKey.PAY_METHOD;

		case "additionalChargesAmount":
		case "additionalDiscountsAmount":
		case "additionalNotes":
		case "clientCode":
		case "contractRef":
		case "currency":
		case "deliveryNoteRef":
		case "documentType":
		case "IBAN":
		case "incoterms":
		case "isCreditNote":
		case "issuerEmail":
		case "issuerPhoneNumber":
		case "issuerWebsite":
		case "language":
		case "legalNotes":
		case "meterNumber":
		case "numberFormat":
		case "orderRef":
		case "recipientAddress":
		case "recipientAddressDetails":
		case "recipientCountry":
		case "recipientEmail":
		case "recipientName":
		case "recipientPhoneNumber":
		case "recipientTaxId":
		case "recipientWebsite":
		case "reimbursableExpensesAmount":
		case "serviceAddress":
		case "shippingAddress":
		case "supplyNumber":
		case "SWIFT":
		case "taxClass":
		case "totalDiscountAmount":
		case "totalDueAmount":
		case "totalFeesAmount":
		case "totalGrossAmount":
		case "totalUsage":
		case "usageUnitOfMeasurement":
		default:
			return null;
		}
	}

	private static String toString(OCRSeverity ocrSeverity) {
		return "ocr" + AonStringUtils.substring(ocrSeverity.name(), 0, 1).toUpperCase()
				+ AonStringUtils.substring(ocrSeverity.name(), 1);
	}

	private static List<JSONObject> getTextItems(OCRPage page) {
		Stream<OCRLine> lines = Arrays.stream(page.getLines().orElse(new OCRLine[0]));
		Stream<OCRWord> words = lines.map(line -> line.getWords().orElse(new OCRWord[0])).flatMap(Arrays::stream);

		List<JSONObject> items = words.map(word -> {
			JSONObject textItem = new JSONObject();
			word.getText().ifPresent(text -> textItem.put("str", text));
			word.getBoundingBox().ifPresent(box -> {

				BigDecimal leftTopX = box[0];
				BigDecimal leftTopY = box[1];

				BigDecimal rightTopX = box[2];
				BigDecimal rightTopY = box[3];

				BigDecimal rightBottomX = box[4];
				BigDecimal rightBottomY = box[5];

				BigDecimal leftBottomX = box[6];
				BigDecimal leftBottomY = box[7];

				BigDecimal leftHeigth = leftBottomY.subtract(leftTopY);
				BigDecimal rightHeigth = rightBottomY.subtract(rightTopY);
				BigDecimal height = leftHeigth.max(rightHeigth);

				BigDecimal topWidth = rightTopX.subtract(leftTopX);
				BigDecimal bottomWidth = rightBottomX.subtract(leftBottomX);
				BigDecimal width = topWidth.max(bottomWidth);

				textItem.put("width", width);
				textItem.put("height", height);

				BigDecimal top = leftTopY.min(rightTopY);
				textItem.put("top", top);

				BigDecimal left = leftTopX.min(leftBottomX);
				textItem.put("left", left);

			});
			return textItem;
		}).toList();
		return items;
	}
}
