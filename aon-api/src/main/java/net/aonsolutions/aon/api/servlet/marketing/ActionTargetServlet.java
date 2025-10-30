package net.aonsolutions.aon.api.servlet.marketing;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Advertising;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.CommercialActivity;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingSellerDistribution;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.registry.TargetFull;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.RegistrySellerStatus;
import com.esferalia.aon.occam.api.model.type.RegistrySellerType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.TargetStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryRelationshipDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistrySellerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TargetDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;
import net.aonsolutions.aon.api.servlet.Utils;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiActionTargetServlet", urlPatterns = { "/ms/api/action-target/*" })
public class ActionTargetServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(ActionTargetServlet.class.getName());

	public static final String ACTION_TAGET = "/";
	public static final String CREATE_ENTERPRISE = "/create-enterprise";

	// Mail
	private static String urlMail;
	private static String userMail;
	private static String passwordMail;

	private static boolean isLocal = false;
	
	private static Domain createdDomain = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());

		try {

			AonApiData api = initialize(req);
			Object object = new AonRouting(api).addRoute(CREATE_ENTERPRISE, ActionTargetServlet::createEnterprise)
					.apply();

			responseHtml(req, resp, object);

		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());

		try {

			AonApiData api = initialize(req);
			Object object = new AonRouting(api).addRoute(ACTION_TAGET, ActionTargetServlet::saveActionTarget)
					.apply();

			response(req, resp, object);

		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	// ---------------------------------------------------------------------------------------------
	// SAVE ACTION TARGET
	// ---------------------------------------------------------------------------------------------

	public static JSONObject saveActionTarget(AonApiData api) {
		// ActionTarget

		ActionTarget actionTarget = new ActionTarget(api.getData());

		// Marketing Action

		MarketingAction ma = AON.getMarketingAction(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), actionTarget.getMarketingAction().getId());
		actionTarget.setMarketingAction(ma);

		// Ceck Exist Domain
		boolean existDomain = checkExistingDomain(api, actionTarget.getTarget().getDocument(), actionTarget.getTarget().getEmail());
		if(existDomain) {
			Domain parentDomain = null == api.getDomain().getParentId() ? api.getDomain() : AON.getDomain(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));
			Domain domain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getNameProperty().like("%" + actionTarget.getTarget().getDocument() + "%")
							.and(f.getParentProperty().eq(parentDomain.getId())));
			
			throw new IllegalArgumentException("El dominio " + domain.getName() + " ya existe para este despacho");
		}
		
		// Get / Save Target
		Optional<Target> targetOpt = AON.getTarget(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), f -> f.getDocumentProperty().eq(actionTarget.getTarget().getDocument())
						.and(f.getDomainProperty().eq(api.getDomain().getId())));

		Target target = null;
		if (targetOpt.isEmpty()) {
			target = new Target()
					.copy(new Registry().setDomain(api.getDomain()).setName(actionTarget.getTarget().getName())
							.setDocumentType(DocumentType.identify(actionTarget.getTarget().getDocument()))
							.setDocumentCountry(Country.safeValueOf(actionTarget.getTarget().getDocumentCountry()))
							.setDocument(actionTarget.getTarget().getDocument())
							.setNationality(Country.safeValueOf(actionTarget.getTarget().getDocumentCountry()))

					).setScope(actionTarget.getMarketingAction().getMarketingCampaign().getScope());

			target = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), target);

			// Save Registry Note (Observacion)

			if (AonStringUtils.isNotBlank(actionTarget.getTarget().getComments())) {
				RegistryNote note = new RegistryNote().setDomain(target.getDomain().getId()).setRegistry(target.getId())
						.setDescription("Observaci\u00f3n").setNoteDate(new Date())
						.setComments(actionTarget.getTarget().getComments()).setNoteType(NoteType.OBSERVATION)
						.setSecurityLevel(SecurityLevel.OFFICIAL);

				AON.saveRegistryNote(api.getDomain(), api.getUser().getLogin(), note);
			}

			// Save Registry Address

			Integer raddressId = null;

			if (AonStringUtils.isNotBlank(actionTarget.getTarget().getAddress())) {
				Optional<GeoZone> geozoneOpt = AON
						.geozoneStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
								f -> f.getDomainProperty().eq(api.getDomain().getId())
										.and(f.getCodeProperty().eq(actionTarget.getTarget().getGeozoneCode())))
						.findFirst();

				RegistryAddress registryAddress = new RegistryAddress().setDomain(target.getDomain().getId())
						.setRegistry(target.getId()).setMain(true)
						.setStreetType(StreetType.getForAeatCode(actionTarget.getTarget().getStreetType(),
								AonLanguage.SPANISH))
						.setAddress(actionTarget.getTarget().getAddress())
						.setNumber(actionTarget.getTarget().getNumber()).setZip(actionTarget.getTarget().getZip())
						.setCity(actionTarget.getTarget().getCity())
						.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null)
						.setGeozoneCode(actionTarget.getTarget().getGeozoneCode())
						.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null);

				registryAddress = AON.save(api.getDomain(), api.getUser().getLogin(), registryAddress);
				raddressId = registryAddress.getId();
			}

			// Save Registry Media

			saveMedia(api, target.getDomain().getId(), target.getId(), MediaType.CELLULAR,
					actionTarget.getTarget().getPhone(), raddressId);
			saveMedia(api, target.getDomain().getId(), target.getId(), MediaType.EMAIL,
					actionTarget.getTarget().getEmail(), raddressId);

		} else
			target = targetOpt.get();

		// Save Marketing Action Target

		MarketingActionTarget mkActionTarget = null;

		try {

			mkActionTarget = new MarketingActionTarget().copy(target).setActionTargetDomain(target.getDomain().getId())
					.setMarketingAction(new MarketingAction().setId(actionTarget.getMarketingAction().getId()))
					.setActionTargetStatus((byte) 0).setComments(actionTarget.getTarget().getComments());

			// Si no existia el target se crea el marketingActionTarget
			if (targetOpt.isEmpty()) {
				AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(),
						api.getUser().getLogin(), mkActionTarget);

				// Si existia el target se busca o crea el marketingActionTarget
			} else {
				MarketingActionTargetParams params = new MarketingActionTargetParams();
				params.setDomainName(api.getDomain().getName());
				params.setDomain(api.getDomain().getId());
				params.setUser(api.getUser().getLogin());
				params.setMarketingAction(new MarketingAction().setId(actionTarget.getMarketingAction().getId()));
				params.setOffset(0);
				params.setLimit(Integer.MAX_VALUE);

				List<MarketingActionTarget> marketingActionTargets = AON.getMarketingActionTargets(params);

				Optional<MarketingActionTarget> marketingActionTargetOpt = marketingActionTargets.stream()
						.filter(targetIt -> targetIt.getId().equals(targetOpt.get().getId())).findAny();

				if (marketingActionTargetOpt.isEmpty())
					AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(),
							api.getUser().getLogin(), mkActionTarget);
				else
					mkActionTarget = marketingActionTargetOpt.get();
			}

		} catch (Exception e) {
			System.out.println("Error creacion MarketingActionTarget : " + e.getMessage());
		}

		// Create Operacion Comercial

		Integer seller = createCommercialOperation(api, actionTarget, target, mkActionTarget);

		// Return data

		JSONObject result = new JSONObject();
		result.put("type", "success");

		// Send mail

		boolean trial = JsonUtils.getboolean(api.getData().getJSONObject("actionTarget"), "trial");
		if (trial) {
			sendCreationEnterpriseMail(api, actionTarget, target.getId(), seller);
			result.put("message",
					"Se ha creado el lead correctamente y se ha enviado un mail para la creaci\u00f3n de la empresa.");
		} else
			result.put("message", "Se ha creado el lead correctamente.");

		// Return data

		return result;
	}

	private static void sendCreationEnterpriseMail(AonApiData api, ActionTarget actionTarget, Integer targetId, Integer seller) {
		Domain parent = api.getDomain().isParent() ? api.getDomain()
				: AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
						f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		if (null != parent) {

			// Construir la URL completa para la llamada GET en GWT
			String postUrl = (isLocal ? "http" : "https") + "://" + parent.getName() + (isLocal ? ":8080" : "")
					+ "/ms/api/action-target/create-enterprise";
			try {
				postUrl += "?name=" + URLEncoder.encode(actionTarget.getTarget().getName(), "UTF-8") + "&document="
						+ actionTarget.getTarget().getDocument() + "&streetType="
						+ actionTarget.getTarget().getStreetType() + "&address="
						+ URLEncoder.encode(actionTarget.getTarget().getAddress(), "UTF-8") + "&number="
						+ actionTarget.getTarget().getNumber() + "&zip=" + actionTarget.getTarget().getZip()
						+ "&geozoneCode=" + actionTarget.getTarget().getGeozoneCode() + "&city="
						+ URLEncoder.encode(actionTarget.getTarget().getCity(), "UTF-8") + "&phone="
						+ actionTarget.getTarget().getPhone() + "&email=" + actionTarget.getTarget().getEmail()
						+ "&target=" + targetId.toString() + "&domain_name=" + parent.getName() + "&domain_id="
						+ parent.getId().toString() + "&domain_login=" + api.getUser().getLogin() + "&session_id="
						+ api.getToken() + "&marketingAction=" + actionTarget.getMarketingAction().getId().toString()
						;
				
				if(null != seller)
					postUrl += "&seller=" + seller.toString();
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			String from = getFromMessage(api);

			String logoUrl = getLogoUrl(parent, api.getUser());

			Date expirationDate = new Date();
			expirationDate = AonDateUtils.addDays(expirationDate, 1);
			String shortUrl = AON.getShortURL("laburr", postUrl, expirationDate);

			VelocityEngine engine = new VelocityEngine();
			engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			engine.init();

			VelocityContext context = new VelocityContext();
			context.put("postUrl", shortUrl);
			context.put("logo", logoUrl);
			context.put("parentName", parent.getDescription());
			context.put("name", actionTarget.getTarget().getName());
			context.put("document", actionTarget.getTarget().getDocument());
			context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

			Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial.vm");

			StringWriter writer = new StringWriter();
			template.merge(context, writer);

			sendTrailEnterpriseMail(api, actionTarget, writer.toString());

		}

	}

	private static void sendTrailEnterpriseMail(AonApiData api, ActionTarget actionTarget, String body) {
		String from = getFromMessage(api);
		String bcc = "booking@aonsolutions.es";

		SESMessage msg = new SESMessage()
				.setAlias(actionTarget.getTarget().getName())
				.setFrom(from)
				.setReplyTo(from)
				.setTo(actionTarget.getTarget().getEmail())
				.setBcc(bcc)
				.setSubject(actionTarget.getTarget().getName() + " (TRIAL)").setBody(body);

		SES.sendEmail(msg);
	}

	// ---------------------------------------------------------------------------------------------
	// CREATE ENTERPRISE
	// ---------------------------------------------------------------------------------------------

	public static String createEnterprise(AonApiData api) {

//		JSONObject data = api.getData();

		// Enterprise Data
//		String name = JsonUtils.getString(data, "name");
//		String document = JsonUtils.getString(data, "document");
//
//		String streetType = JsonUtils.getString(data, "streetType");
//		String address = JsonUtils.getString(data, "address");
//		String number = JsonUtils.getString(data, "number");
//		String zip = JsonUtils.getString(data, "zip");
//		String geozoneCode = JsonUtils.getString(data, "geozoneCode");
//		String city = JsonUtils.getString(data, "city");
//
//		String phone = JsonUtils.getString(data, "phone");
//		String email = JsonUtils.getString(data, "email");
//
//		Integer target = JsonUtils.getInteger(data, "target");
//		Integer marketingActionId = JsonUtils.getInteger(data, "marketingAction");
//		
//		Integer sellerId = JsonUtils.getInteger(data, "seller");

		Domain parent = api.getDomain().isParent() ? api.getDomain()
				: AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
						f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		// Create Enterprise

		System.out.println("----------------------- Create Enterprise -----------------------");

		try (CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())) {
			ctx.transaction(t -> {
							
				System.out.println("----------------------- [Start] Create Enterprise -----------------------");

 				System.out.println("----------------------- Check Customer");
				
 				checkCustomer(api, ctx);
 				
 				// System.out.println("----------------------- Create Scope");
				
				// Scope newScope = createScope(api, ctx);
				
				System.out.println("----------------------- Create Domain");
				
 				// Domain newDomain = createDomain(api, ctx, newScope);
 				Domain newDomain = createDomain(api, ctx, new Scope());
 				urlMail = newDomain.getName();
				
				System.out.println("----------------------- Create Auth / User");
				
				createDefaultUser(api, ctx, newDomain);
				
				// TODO: esto hay que hacerlo cuando nos aseguremos que los sellers que se muestran en la accion comercial tenga un usuario del padre asociado
				
				// System.out.println("----------------------- Create User Scope (supportSeller / api.getUser)");
				
				// createUserScope(api, ctx, newDomain, newScope);
				
				System.out.println("----------------------- Domain Apps / Config");
				
				insertDomainConfiguration(ctx, newDomain);
				
				System.out.println("----------------------- Account Period");
				
				insertAccountPeriod(ctx, newDomain);
				
				System.out.println("----------------------- Project Commercial");
				
				updateProjectCommercial(api, ctx);

				// Send mail
				sendEnterpriseCreatedMail(api, ctx, newDomain);
				
				createdDomain = newDomain;
				
				System.out.println("----------------------- [End] Create Enterprise -----------------------");
				
			});
		}

		System.out.println("----------------------- Create Enterprise (END) -----------------------");

//		// Send mail
//		String logoUrl = getLogoUrl(parent, api.getUser());
//
//		sendTrailEnterpriseCreatedMail(api, email, logoUrl, parent);

		return getFinishCreationHtml(api, parent, createdDomain);
	}
	
	// ---------------------------------------------------------------------------------------------
	// AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------

	private static Customer checkCustomer(AonApiData api, CloseableAONContext ctx) {
		JSONObject data = api.getData();
		
		Integer registry = JsonUtils.getInteger(data, "target");
		
		Customer customer = CustomerDAO.get(ctx, f -> f.getRegistryProperty().eq(registry));
		Optional<Target> targetOpt = TargetDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst();
		
		// Existe target pero no customer
		if(null == customer || null == customer.getId()){
			TargetFull target = TargetDAO.getFull(ctx, registry);
			Customer newCustomer = new Customer()
					.setTariff(null != target.getRegistry().getTariff() ? target.getRegistry().getTariff().getId() : null)
					.setSurcharge(target.getRegistry().getSurcharge())
					.setWithholding(target.getRegistry().getWithholding())
					.setTransaction(target.getRegistry().getTransaction())
					.setStatus(RegistryStatus.ACTIVE)
					.setScope(target.getRegistry().getScope())
					.setCreationUser(api.getUser().getName())
					.setCreationDate(new Date())
					;
			newCustomer.setDomain(new Domain().setId(target.getDomain()));
			newCustomer.copy(target.getRegistry());
			
			customer = CustomerDAO.save(ctx, newCustomer);	
			System.out.println("Registry : " + registry + ", New Customer : " + customer.getId());
		
		// Existe customer pero no target
		} else if(targetOpt.isEmpty()) {
			if(targetOpt.isEmpty()) {
				Target newTarget = new Target()
						.setTariff(new Tariff().setId(customer.getTariff()))
						.setAdvertising(Advertising.ALLOWED)
						.setSurcharge(customer.isSurcharge())
						.setWithholding(customer.isWithholding())
						.setTransaction(customer.getTransaction())
						.setStatus(TargetStatus.ACTIVE)
						.setScope(customer.getScope())
						.setCreationUser(api.getUser().getLogin())
						.setCreationDate(new Date())
						;
				
				newTarget.setDomain(customer.getDomain());
				newTarget.copy(customer);
				
				TargetDAO.save(ctx, newTarget);
			}
		}
		
		return customer;
		
	}

	private static Scope createScope(AonApiData api, CloseableAONContext ctx) throws Exception {
		
		JSONObject data = api.getData();
		
		String document = JsonUtils.getString(data, "document");
		
		Domain parentDomain = null == api.getDomain().getParentId() 
				? api.getDomain() 
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));
		
		Stream<Scope> scopes = SecurityDAO.getScopeStream(ctx, f -> f.getDescriptionProperty().eq(document).and(f.getDomainProperty().eq(parentDomain.getId())));
		
		if(scopes.count() != 0)
			throw new AonApiException("Ya existe un ambito en el entorno cuya descripci\u00f3n es " + document);
		else {
			Scope newScope = new Scope()
					.setDomain(parentDomain.getId())
					.setDescription(document);
			
			newScope = SecurityDAO.insertScope(ctx, newScope);
			return newScope;
		}
		
	}
	
	private static Domain createDomain(AonApiData api, CloseableAONContext ctx, Scope newScope) throws Exception {
		JSONObject data = api.getData();
		
		String name = JsonUtils.getString(data, "name");
		String document = JsonUtils.getString(data, "document");
		Integer sellerSupport = JsonUtils.getInteger(data, "seller");

		Company company = new Company();
		company.setName(name);
		company.setDocument(document);
		company.setLegalPerson(AonDocumentUtil.isValidCIF(document));

		checkCompany(company);
		
		Domain parentDomain = null == api.getDomain().getParentId() 
				? api.getDomain() 
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));
		
		Domain findDomain = checkExistingDomain(api, ctx);
		if(null != findDomain && null != findDomain.getId()) {
			createEnterpriseDuplicateBody(api, findDomain, parentDomain);
			throw new AonApiException("La empresa con identificador " + document + " para el cliente " + name + " ya existe");
		}
		
		if (null == parentDomain || null == parentDomain.getId())
			throw new AonApiException("No existe empresa padre desde la que colgar esta empresa");

		// Get owner email
		
		Stream<RegistryMedia> sellerSupportMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(sellerSupport));
		Optional<RegistryMedia> sellerSupportEmailOpt = sellerSupportMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
		
		if(sellerSupportEmailOpt.isEmpty())
			throw new AonApiException("No existe email para el agente de soporte seleccionado");
		
		String domainNewName = company.getDocument() + "-" + (AonStringUtils.isBlank(parentDomain.getSubDomainSuffix()) ? parentDomain.getName() : parentDomain.getSubDomainSuffix());
		
		Domain newDomain = new Domain()
				.setName(domainNewName.toLowerCase())
				.setDescription(company.getName())
				.setOwner(sellerSupportEmailOpt.get().getValue()) // Alguno mas
				.setParentId(parentDomain.getId())
				.setActive(true)
				.setDomainType(DomainType.ENTERPRISE)
				.setEnableHeredity(true)
				.setDomainManagement(false)
				.setScope(newScope.getId());
		
		newDomain = DomainDAO.insertDomainWithoutEnterprise(ctx, newDomain, company);
		
		Domain createdDomain = newDomain;
		
		Company newCompany = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(createdDomain.getId())).findFirst().get();
		
		Scope enterpriseScope = SecurityDAO.insertScope(ctx, new Scope().setDomain(newDomain.getId()).setDescription("GENERAL"));
		
		ctx.getDslContext()
			.insertInto(ENTERPRISE)
			.set(ENTERPRISE.REGISTRY, newCompany.getId())
			.set(ENTERPRISE.DOMAIN, newDomain.getId())
			.set(ENTERPRISE.SCOPE, enterpriseScope.getId())
			.execute();
		
		createCompanyMedia(api, ctx, newDomain);
		
		return newDomain;
		
	}

	private static void checkCompany(Company company) throws AonApiException {
		if (AonStringUtils.isBlank(company.getDocument())) {
			throw new AonApiException("El documento de la empresa esta vacio.");
		}

		if (!AonDocumentUtil.isValid(company.getDocument().toUpperCase())) {
			throw new AonApiException("El documento de la empresa no es valido");
		}

		if (AonStringUtils.isBlank(company.getName())) {
			throw new AonApiException("El nombre de la empresa esta vacio");
		}
	}
	
	private static Domain checkExistingDomain(AonApiData api, CloseableAONContext ctx) {
		JSONObject data = api.getData();
		
		String document = JsonUtils.getString(data, "document");
		
		Domain parentDomain = null == api.getDomain().getParentId() 
				? api.getDomain() 
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));
		
		if (null != parentDomain && null != parentDomain.getId()) {
			
			Domain domain = DomainDAO.getDomain(ctx, 
					f -> f.getNameProperty().like("%" + document + "%")
						.and(f.getParentProperty().eq(parentDomain.getId()))
			);
			
			return domain;
			
		}
		
		return null;
	}
	
	private static void createCompanyMedia(AonApiData api, CloseableAONContext ctx, Domain newDomain) {
		JSONObject data = api.getData();
		
		String streetType = JsonUtils.getString(data, "streetType");
		String address = JsonUtils.getString(data, "address");
		String number = JsonUtils.getString(data, "number");
		String zip = JsonUtils.getString(data, "zip");
		String geozoneCode = JsonUtils.getString(data, "geozoneCode");
		String city = JsonUtils.getString(data, "city");
		
		String phone = JsonUtils.getString(data, "phone");
		String email = JsonUtils.getString(data, "email");
		
		Integer registry = JsonUtils.getInteger(data, "target");
		
		Company newCompany = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(newDomain.getId())).findFirst().get();

		Integer comapnyRaddressId = null;
		
		if (AonStringUtils.isNotBlank(address)) {
			
			// Conseguir del padre
			Optional<GeoZone> geozoneOpt = GeoZoneDAO.getStream(ctx, 
				f -> f.getDomainProperty().eq(newDomain.getParentId())
					.and(f.getCodeProperty().eq(geozoneCode))
				).findFirst();

			RegistryAddress registryAddress = new RegistryAddress()
					.setDomain(newDomain.getId())
					.setRegistry(newCompany.getId())
					.setMain(true)
					.setStreetType(StreetType.getForAeatCode(streetType, AonLanguage.SPANISH))
					.setAddress(address)
					.setNumber(number)
					.setZip(zip)
					.setCity(city)
					.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null).setGeozoneCode(geozoneCode)
					.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null);

			registryAddress = RegistryAddressDAO.save(ctx, registryAddress);
			comapnyRaddressId = registryAddress.getId();
		} else {
			RegistryAddress registryAddress = new RegistryAddress()
					.setDomain(newDomain.getId())
					.setRegistry(newCompany.getId())
					.setMain(true)
					.setStreetType(StreetType.CALLE)
					.setAddress(null)
					.setNumber(null)
					.setZip(null)
					.setCity(null)
					.setGeozone(null)
					.setGeozoneCode(null)
					.setGeozoneName(null);

			registryAddress = RegistryAddressDAO.save(ctx, registryAddress);
			comapnyRaddressId = registryAddress.getId();
		}

		saveMedia(api, ctx, newDomain.getId(), newCompany.getId(), MediaType.FIXED_PHONE, phone, comapnyRaddressId);
		saveMedia(api, ctx, newDomain.getId(), newCompany.getId(), MediaType.EMAIL, email, comapnyRaddressId);

		if (null != comapnyRaddressId) {
			
			Customer customer = CustomerDAO.get(ctx, f -> f.getRegistryProperty().eq(registry));
			createWorkplace(ctx, newDomain, newCompany.getId(), comapnyRaddressId, customer.getId(), geozoneCode);
		
		}
		
		createRRelationShip(api, ctx, newCompany);
		
	}

	private static void saveMedia(AonApiData api, CloseableAONContext ctx, Integer domain, Integer registry, MediaType mediaType, String value,
			Integer raddress) {
		if (AonStringUtils.isNotBlank(value)) {
			RegistryMedia registryMedia = new RegistryMedia()
					.setDomain(domain)
					.setRegistry(registry)
					.setMedia(mediaType)
					.setValue(value)
					.setCommercial(true)
					.setRaddress(raddress);
			
			RegistryMediaDAO.save(ctx, registryMedia);
		}
	}
	
	private static void createWorkplace(CloseableAONContext ctx, Domain newDomain, Integer companyId, Integer raddressId, Integer customerId, String geozoneCode) {
		Workplace workplace = new Workplace()
				.setActive(true)
				.setDescription("PRINCIPAL")
				.setDomain(newDomain.getId())
				.setEnterprise(companyId)
				.setAddress(raddressId)
				.setCustomer(customerId)
				.setEconomicAgreement(getEconomicAgreement(geozoneCode))
				;

		WorkplaceDAO.save(ctx, workplace);
	}
	
	private static Administration getEconomicAgreement(String geozoneCode) {
		if(AonStringUtils.isBlank(geozoneCode)) return Administration.COMMON_TERRITORY;
		
		switch (geozoneCode) {
			case "01":
				return Administration.ALAVA;
			case "20":
				return Administration.GIPUZKOA;
			case "48":
				return Administration.BIZKAIA;
			case "31":
				return Administration.NAVARRA;
			case "35":
				return Administration.CANARIAS;
			default:
				return Administration.COMMON_TERRITORY;
		}
	}
	
	private static void createRRelationShip(AonApiData api, CloseableAONContext ctx, Company newCompany) {
		JSONObject data = api.getData();

		Integer registry = JsonUtils.getInteger(data, "target"); // target.id
		Integer sellerSupport = JsonUtils.getInteger(data, "seller");
		Integer sellerCommercial = JsonUtils.getInteger(data, "seller");
		
		Customer customer = CustomerDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst().get();
		
		if(null == customer)
			throw new AonApiException("No ha sido posible encontrar al cliente seleccionado");
		
		// Create RSeller
		Stream<RegistrySeller> customerRSellers = RegistrySellerDAO.getStream(ctx, f -> f.getRegistryProperty().eq(registry));

		Optional<RegistrySeller> commercialRSeller = customerRSellers.filter(rseller -> rseller.getType().equals(RegistrySellerType.COMERCIAL)).findFirst();
		
		if(null != sellerCommercial) {
			if(commercialRSeller.isEmpty()) {
				Date startDate = new Date();
				
				RegistrySeller rseller = new RegistrySeller()
						.setDomain(api.getDomain())
						.setRegistry(registry)
						.setSeller(new Seller().setId(sellerCommercial))
						.setType(RegistrySellerType.COMERCIAL)
						.setStatus(RegistrySellerStatus.ACTIVE)
						.setStartDate(startDate);
				
				RegistrySellerDAO.save(ctx, rseller);
			} else {
				Date startDate = new Date();
				Date endDate = AonDateUtils.addDays(startDate, -1);
				
				RegistrySeller rseller = commercialRSeller.get();
				rseller.setEndDate(endDate);
				RegistrySellerDAO.save(ctx, rseller);
				
				RegistrySeller newRseller = new RegistrySeller()
						.setDomain(api.getDomain())
						.setRegistry(registry)
						.setSeller(new Seller().setId(sellerCommercial))
						.setType(RegistrySellerType.COMERCIAL)
						.setStatus(RegistrySellerStatus.ACTIVE)
						.setStartDate(startDate);
				
				RegistrySellerDAO.save(ctx, newRseller);
			}
		}
		
		customerRSellers = RegistrySellerDAO.getStream(ctx, f -> f.getRegistryProperty().eq(registry));
		
		Optional<RegistrySeller> supportRSeller = customerRSellers.filter(rseller -> rseller.getType().equals(RegistrySellerType.SOPORTE)).findFirst();
		if(supportRSeller.isEmpty()) {
			Date startDate = new Date();
			
			RegistrySeller rseller = new RegistrySeller()
					.setDomain(api.getDomain())
					.setRegistry(registry)
					.setSeller(new Seller().setId(sellerSupport))
					.setType(RegistrySellerType.SOPORTE)
					.setStatus(RegistrySellerStatus.ACTIVE)
					.setStartDate(startDate);
			
			RegistrySellerDAO.save(ctx, rseller);
		} else {
			Date startDate = new Date();
			Date endDate = AonDateUtils.addDays(startDate, -1);
			
			RegistrySeller rseller = supportRSeller.get();
			rseller.setEndDate(endDate);
			RegistrySellerDAO.save(ctx, rseller);
			
			RegistrySeller newRseller = new RegistrySeller()
					.setDomain(api.getDomain())
					.setRegistry(registry)
					.setSeller(new Seller().setId(sellerSupport))
					.setType(RegistrySellerType.SOPORTE)
					.setStatus(RegistrySellerStatus.ACTIVE)
					.setStartDate(startDate);
			
			RegistrySellerDAO.save(ctx, newRseller);
		}

		// Create Registry Relationship
		RegistryRelationship rrelationship = new RegistryRelationship();
		rrelationship.setDomain(customer.getDomain());
		rrelationship.setRegistry(customer.getId());
		rrelationship.setRelatedRegistry(newCompany.getId());
		rrelationship.setComments(newCompany.getDomain().getName());

		RegistryRelationshipDAO.save(ctx, rrelationship);
	}
	
	private static User createDefaultUser(AonApiData api, CloseableAONContext ctx,  Domain newDomain) {
		JSONObject data = api.getData();
		
		Integer registry = JsonUtils.getInteger(data, "target"); // target.id
		String email = JsonUtils.getString(data, "email");
		String phone = JsonUtils.getString(data, "phone");
		
		Optional<Target> targetOpt = TargetDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst();
		
		if (Utils.isEmail(email)) {
			
			String login = ramdonLogin();
			String pass = null;
			
			Auth auth = AON_SOLUTIONS.getAuth(email);
			
			if (pass == null) pass = Utils.createPasswordHash(email, login);
			passwordMail = login;
			
			if (auth.getUuid() == null) {
				auth = new Auth()
						.setEmail(email)
						.setPassword(pass)
						.setName(targetOpt.get().getName())
						.setSurname(null)
						.setDocument(targetOpt.get().getDocument())
						.setPhone(phone);

				auth = SecurityDAO.insertAuth(ctx, auth);
			} else {
				auth.setPassword(pass);
				auth = SecurityDAO.updateAuth(ctx, auth);
			}

			User user = null;
			if (auth.getAuth() != null) {
				user = createUser(api, ctx, newDomain, auth, login, targetOpt.get().getName());
				setUserAppRole(ctx, newDomain, user);

				if (newDomain.isChild() || newDomain.isStandalone()) {
					createTaskHolder(ctx, newDomain, auth, user);
				}
			}
			
			userMail = email;
			
			return user;

		} else {
			throw new AonApiException("El email (" + email + ") no tiene un formato correcto.");
		}
		
	}
	
	private static String ramdonLogin() {
		Random rnd = new Random();
		Integer i = rnd.nextInt(100000000 - 10000000 + 1) + 10000000;
		return i.toString();
	}

	private static User createUser(AonApiData api, CloseableAONContext ctx, Domain newDomain, Auth auth, String login, String name) {
		Company newCompany = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(newDomain.getId())).findFirst().get();

		User newUser = new User()
				.setAuth(auth)
				.setActive(true)
				.setDomain(newDomain.getId())
				.setLogin(newCompany.getDocument())
				.setName(AonStringUtils.isNotBlank(name) ? name : newCompany.getDocument())
				.setShared(false)
				.setEnterprise(newCompany.getId())
				.setToolbar(UserToolbar.GOOGLE);

		
		newUser = SecurityDAO.save(ctx, newUser);

		SecurityDAO.updateUserPassword(ctx, newUser.getId(), auth.getPassword());

		Scope scope = getScope(ctx, newDomain, newUser);

		if (scope != null) {
			SecurityDAO.insertUserScope(
					ctx, 
					new UserScope()
					.setDomain(newDomain.getId())
					.setScope(scope.getId())
					.setUserId(newUser.getId())
			);
		}

		ApplicationParameter appParam = AppParamDAO.fetchOne(ctx, AppParam.AON_PORTAL);
		
		if (appParam == null || appParam.getId() == null) {
			
			appParam = new ApplicationParameter()
					.setDomain(newDomain.getId())
					.setValue("288")
					.setName(AppParam.AON_PORTAL.getValue());
			
			AppParamDAO.insertApplicationParameter(ctx, appParam);
		}

		return newUser;
	}

	private static Scope getScope(CloseableAONContext ctx, Domain newDomain, User newUser) {

		Scope s = SecurityDAO.getScopeStream(
				ctx, 
				f -> f.getDomainProperty().eq(newDomain.getId())
					.and(f.getDescriptionProperty().eq("GENERAL"))
				)
				.findFirst().orElse(null);

		if (s == null && newDomain.getParentId() != null) {
			s = SecurityDAO.getScopeStream(
					ctx, 
					f -> f.getDomainProperty().eq(newDomain.getParentId())
						.and(f.getDescriptionProperty().eq("GENERAL"))
					)
					.findFirst().orElse(null);
		}

		if (s == null) {
			s = SecurityDAO.getScopeStream(
					ctx, 
					f -> f.getDomainProperty().eq(newDomain.getId())
					)
					.findFirst().orElse(null);
		}

		if (s == null && newDomain.getParentId() != null) {
			s = SecurityDAO.getScopeStream(
					ctx, 
					f -> f.getDomainProperty().eq(newDomain.getParentId())
					)
					.findFirst().orElse(null);
		}

		return s;
	}
	

	private static void setUserAppRole(CloseableAONContext ctx, Domain newDomain, User user) {
		Integer userId = user.getId();

		LinkedList<AonRole> aRoles = SecurityDAO.getUserAppRoleStream(ctx, f -> f.getUserIdProperty().eq(userId))
				.map(r -> r.getRole())
				.collect(Collectors.toCollection(LinkedList::new));

		LinkedList<AonRole> tRoles = new LinkedList<AonRole>();
		tRoles.add(AonRole.ENTERPRISE);
		tRoles.add(AonRole.INVOICE_PORTAL);
		tRoles.add(AonRole.INVOICE);

		AonRole.stream().forEach(role -> {
			if (aRoles.contains(role) && !tRoles.contains(role)) {
				SecurityDAO.deleteUserAppRole(ctx, 
						f -> f.getDomainProperty().eq(newDomain.getId())
							.and(f.getUserIdProperty().eq(userId))
							.and(f.getRoleProperty().eq(role.value()))
				);
			}
			
			if (!aRoles.contains(role) && tRoles.contains(role)) {
				SecurityDAO.insertUserAppRole(ctx, 
						new UserAppRole()
						.setApp(null)
						.setDomain(newDomain.getId())
						.setRole(role)
						.setUser(userId)
				);
			}
		});
	}
	
	private static void createTaskHolder(CloseableAONContext ctx, Domain newDomain, Auth auth, User newUser) {

		String alias = AonStringUtils.isBlank(auth.getName())
				? newUser.getName()
				: auth.getName().length() > 32 ? auth.getName().substring(0, 31) : auth.getName();
		
		Registry newRegistry = RegistryDAO.save(ctx, 
				new Registry()
				.setDocument(auth.getDocument())
				.setName(
						AonStringUtils.isBlank(auth.getName())
						? newUser.getName()
						: auth.getName() + (AonStringUtils.isBlank(auth.getSurname()) ? "" : (" " + auth.getSurname())) )
				.setAlias(alias)
				.setDomain(newDomain)
		);
		
		newUser.setRegistry(newRegistry);
		newUser = UserDAO.save(ctx, newUser);
		
		TaskHolder taskHolder = new TaskHolder()
				.copy(newRegistry)
				.setActive(true)
				.setUserId(newUser.getId());
		
		taskHolder = TaskHolderDAO.save(ctx, taskHolder);

	}

	private static void createUserScope(AonApiData api, CloseableAONContext ctx, Domain newDomain, Scope newScope) {
		JSONObject data = api.getData();
		
		Integer sellerSupport = JsonUtils.getInteger(data, "seller");
		
		// Actualizar "user_scope": Asignar el scope al agente asignado
		Seller seller = SellerDAO.get(ctx, f -> f.getRegistryProperty().eq(sellerSupport));
		TaskHolder taskHolder = TaskHolderDAO.get(ctx, f -> f.getIdProperty().eq(seller.getTaskHolder().getId()), new Options().setFull(true));
		if(null != taskHolder && null != taskHolder.getUserId() && null != newScope.getId()) {
			
			User user = UserDAO.get(ctx, f -> f.getIdProperty().eq(taskHolder.getUserId()), new Options().setFull(true));
			
			SecurityDAO.insertUserScope(
					ctx, 
					new UserScope()
					.setDomain(user.getDomain().getId())
					.setScope(newScope.getId())
					.setUserId(user.getId())
			);
			
		}
		
		// Insertar el ambito al usuario que ha creado la empresa
		if(null != api.getUser() && null != api.getUser().getId() && null != newScope.getId())
			SecurityDAO.insertUserScope(
					ctx, 
					new UserScope()
					.setDomain(api.getUser().getDomain().getId())
					.setScope(newScope.getId())
					.setUserId(api.getUser().getId())
			);
	}
	
	private static void insertDomainConfiguration(CloseableAONContext ctx, Domain newDomain) {
		SecurityDAO.saveDomainMaxDefinedUser(ctx, newDomain.getId(), 1);
		
		DomainApp domainApp = new DomainApp()
				.setDomain(newDomain.getId())
				.setApp(AonApp.INVOICE)
				.setActive(true);
		
		SecurityDAO.saveDomainApp(ctx, domainApp);
		
		domainApp = new DomainApp()
				.setDomain(newDomain.getId())
				.setApp(AonApp.DOCUMENTAL)
				.setActive(true);
		
		SecurityDAO.saveDomainApp(ctx, domainApp);
		
		domainApp = new DomainApp()
				.setDomain(newDomain.getId())
				.setApp(AonApp.MESSENGER)
				.setActive(true);
		
		SecurityDAO.saveDomainApp(ctx, domainApp);
		
		ApplicationParameter trailParam = new ApplicationParameter()
				.setDomain(newDomain.getId())
				.setName(AppParam.TRIAL)
				.setValue("25");
		
		AppParamDAO.insertApplicationParameter(ctx, trailParam);
	}
	
	private static void insertAccountPeriod(CloseableAONContext ctx, Domain newDomain) {
		AccountPeriod accPeriod = new AccountPeriod()
				.setDomain(newDomain.getId())
				.setName("EC")
				.setInitiationDate(AonDateUtils.getYearFirstDay(new Date()))
				.setDeadline(AonDateUtils.getYearLastDay(new Date()))
				.setStatus(AccountPeriodStatus.ACTIVE);
		
		AccountPeriodDAO.save(ctx, accPeriod);
	}
	
	private static void updateProjectCommercial(AonApiData api, CloseableAONContext ctx) {
		JSONObject data = api.getData();
		
		Integer target = JsonUtils.getInteger(data, "target");
		Integer marketingActionId = JsonUtils.getInteger(data, "marketingAction");
		
		MarketingAction mkAction = AON.getMarketingAction(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), marketingActionId);
		Domain officeDomain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), f -> f.getIdProperty().eq(mkAction.getDomain()));

		// Update autoregistro status
		CommercialActivity commercialActivityAutoRegister = AON.getCommercialActivity(api.getDomain().getName(),
				api.getDomain().getId(), api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(officeDomain.getId()).and(f.getNameProperty().like("%AUTOREGISTRO%")));
		if (null == commercialActivityAutoRegister) {
			CommercialActivity ca = new CommercialActivity().setDomain(api.getDomain().getId()).setName("AUTOREGISTRO");

			commercialActivityAutoRegister = AON.save(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), ca);
		}

		ProjectCommercial projectCommercial = AON.getProjectCommercial(officeDomain.getName(), officeDomain.getId(),
				api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(officeDomain.getId()).and(f.getTargetProperty().eq(target)));

		Integer commercialActivityAutoRegisterId = commercialActivityAutoRegister.getId();

		if(projectCommercial  != null && null != projectCommercial.getId()) {
			// Cerrar antiguo autoregistro tracking
			LinkedList<CommercialTracking> verificationCommercialTrackingList = AON.getCommercialTrackingList(
					api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getProjectCommercialProperty().eq(projectCommercial.getId())
							.and(f.getActivityProperty().eq(commercialActivityAutoRegisterId)));
			
			verificationCommercialTrackingList.forEach(verificationCommercialTracking -> {
				verificationCommercialTracking.setStatus((byte) 1);
				verificationCommercialTracking.setDate(new Date());
				AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
						verificationCommercialTracking);

			});

			projectCommercial.setStatus((byte) 3);
			projectCommercial.setStatusDate(new Date());
			
			AON.saveProjectCommercial(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					projectCommercial);
		}

		Optional<MarketingActionTarget> mkActionTarget = mkAction.getTargets().stream()
				.filter(mkTarget -> mkTarget.getId().equals(target)).findFirst();
		if (mkActionTarget.isPresent())
			AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					mkActionTarget.get().setActionTargetStatus((byte) 5)); // Finalizado
	}
	
	// ---------------------------------------------------------------------------------------------
	// SEND CREATED ENTERPRISE EMAIL
	// ---------------------------------------------------------------------------------------------
	
	private static void sendEnterpriseCreatedMail(AonApiData api, CloseableAONContext ctx, Domain newDomain) {
		JSONObject data = api.getData();
		Integer sellerSupport = JsonUtils.getInteger(data, "seller");
		String name = JsonUtils.getString(data, "name");
		Integer registry = JsonUtils.getInteger(data, "target");
		
		Domain parent = api.getDomain().isParent() 
				? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId())
			);
		
		String logoUrl = getLogoUrl(api, ctx);

		String from = getFromMessage(api, ctx, parent);
		
		if(AonStringUtils.isBlank(from))
			throw new AonApiException("No existe email definido en el entorno para la creaci\u00f3n de empresas");

		Stream<RegistryMedia> sellerSupportMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(sellerSupport));
		Optional<RegistryMedia> sellerSupportEmailOpt = sellerSupportMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
		
		List<String> bcc = new ArrayList<String>();
		
		//throw new AonApiException("No existe email para el agente de soporte seleccionado");
		if(!sellerSupportEmailOpt.isEmpty() && !AonStringUtils.isBlank(sellerSupportEmailOpt.get().getValue()))
			bcc = List.of(sellerSupportEmailOpt.get().getValue(), from);
		
		Optional<Target> targetOpt = TargetDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst();
		Stream<RegistryMedia> targetMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(targetOpt.get().getId()));
		Optional<RegistryMedia> targetEmailOpt = targetMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
		
		if(targetEmailOpt.isEmpty())
			throw new AonApiException("No existe email para el cliente potencial seleccionado");
			
		SESMessage msg = new SESMessage()
				.setFrom(from)
				.setTo(targetEmailOpt.get().getValue())
				.setBcc(bcc)
				.setReplyTo(from)
//				.setAlias(name)
				.setSubject("Empresa " + name)
				.setBody(createEnterpriseCreatedBody(logoUrl, newDomain, from, name, parent.getDescription()));

		SES.sendEmail(msg);
	}

	private static String getLogoUrl(AonApiData api, CloseableAONContext ctx) {
		
		Domain parent = api.getDomain().isParent() 
				? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId())
			);
		
		Company company = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(parent.getId())).findFirst().get();
		
		Attach attach = getLogoAttach(api, ctx, company.getId());

		String str = "domain=" + attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
		String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		String logoUrl = null;
		
		Domain attachDomain = DomainDAO.getDomain(ctx, attach.getDomain().getId());
		 logoUrl = (isLocal ? "http" : "https") + "://" + parent.getName() + (isLocal ? ":8080" : "")
				+ "/ms/download_attachment/" + attachDomain.getName() + "/" + attach.getCreationUser() + "/"
				+ result;
			
		return logoUrl;
	}

	private static Attach getLogoAttach(AonApiData api, CloseableAONContext ctx, Integer enterpriseId) {
		Optional<Attach> attach1 = AttachmentDAO.getRegistryAttachStream(ctx, f -> f.getTypeProperty().eq(SIGNATURE.value()).and(f.getAttachModuleProperty().eq(enterpriseId)), true).findFirst();

		return attach1.isPresent() ? attach1.get() : AttachmentDAO.getRegistryAttachStream(ctx, f -> f.getTypeProperty().eq(LOGO.value()).and(f.getAttachModuleProperty().eq(enterpriseId)), true).findFirst().get();
	}

	private static String getFromMessage(AonApiData api, CloseableAONContext ctx, Domain parent) {
		DomainUserRoles domainUserRoles = SecurityDAO.getDomainUserRoles(ctx, api.getUser().getId()); 
		
		String from = null;

		if (domainUserRoles.hasParentCustomView() || domainUserRoles.hasCustomView()) {
			// Ya es el dominio padre el que hay en api.getDomain()
			if (null == api.getDomain().getParentId() && (null == parent || null == parent.getId())) {
				Enterprise enterprise = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(api.getDomain().getId()));
				RegistryMedia emailMedia = RegistryMediaDAO.get(ctx, f -> f.getRegistryProperty().eq(enterprise.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
				
			// Se busca el dominio padre
			} else if (null != parent && null != parent.getId()) {
				Enterprise enterprise = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(parent.getId()));
				RegistryMedia emailMedia = RegistryMediaDAO.get(ctx, f -> f.getRegistryProperty().eq(enterprise.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
			}
		}

		return from;
	}

	private static String createEnterpriseCreatedBody(String logoUrl, Domain newDomain, String from, String name, String parentDescription) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		String url = (isLocal ? "http" : "https") + "://" + newDomain.getName() + (isLocal ? ":8080" : "")
				+ "/";
		
		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDescription);
		context.put("name", name);
		context.put("url", url);
		context.put("domainName", urlMail);
		context.put("user", userMail);
		context.put("password", passwordMail);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/registry_enterprise_created.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	// TODO: deberia entrar desde el entorno, pero por el momento el usuario no lo reconoce desde el entorno solo desde el hijo
	/*
	private static String createEnterpriseCreatedBody(String logoUrl, Domain parentDomain, String from, String name) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		String url = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
				+ "/";
		
		if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "app.leevy.es"))
			url = "https://leevy.aon.solutions";
		else if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "infoautonomos.aonsolutions.net"))
			url = "https://infoautonomos.aon.solutions";

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", name);
		context.put("url", url);
		context.put("domainName", urlMail);
		context.put("user", userMail);
		context.put("password", passwordMail);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/registry_enterprise_created.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	*/
	
	// ---------------------------------------------------------------------------------------------
	// AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------

	private static void saveMedia(AonApiData api, Integer domain, Integer registry, MediaType mediaType, String value,
			Integer raddress) {
		if (AonStringUtils.isNotBlank(value)) {
			RegistryMedia registryMedia = new RegistryMedia().setDomain(domain).setRegistry(registry)
					.setMedia(mediaType).setValue(value).setCommercial(true).setRaddress(raddress);

			AON.save(api.getDomain(), api.getUser().getLogin(), registryMedia);
		}
	}

	private static Integer createCommercialOperation(AonApiData api, ActionTarget actionTarget, Target target,
			MarketingActionTarget mkActionTarget) {
		CommercialActivity commercialActivityRequest = AON.getCommercialActivity(api.getDomain().getName(),
				api.getDomain().getId(), api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getNameProperty().like("%SOLICITUD%")));
		if (null == commercialActivityRequest) {
			CommercialActivity ca = new CommercialActivity().setDomain(api.getDomain().getId()).setName("SOLICITUD");

			commercialActivityRequest = AON.save(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), ca);
		}

		CommercialActivity commercialActivityCheck = AON.getCommercialActivity(api.getDomain().getName(),
				api.getDomain().getId(), api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getNameProperty().like("%VERIFICACION%")));
		if (null == commercialActivityCheck) {
			CommercialActivity ca = new CommercialActivity().setDomain(api.getDomain().getId()).setName("VERIFICACION");

			commercialActivityCheck = AON.save(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), ca);
		}

		CommercialActivity commercialActivityAutoRegister = AON.getCommercialActivity(api.getDomain().getName(),
				api.getDomain().getId(), api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getNameProperty().like("%AUTOREGISTRO%")));
		if (null == commercialActivityAutoRegister) {
			CommercialActivity ca = new CommercialActivity().setDomain(api.getDomain().getId()).setName("AUTOREGISTRO");

			commercialActivityAutoRegister = AON.save(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), ca);
		}

		MarketingAction mkAction = AON.getMarketingAction(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), mkActionTarget.getMarketingAction().getId());

		ProjectCommercial projectCommercial = null;
		
		// Existe expediente: se cierra la verificacion antigua y se crean los nuevos
		// trackings
		if (null != mkActionTarget.getProject() && null != mkActionTarget.getProject().getId()) {

			projectCommercial = AON.getProjectCommercial(api.getDomain().getName(),
					api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getProjectProperty().eq(null == mkActionTarget.getProject() ? null : mkActionTarget.getProject().getId()));

			Integer commercialActivityAutoRegisterId = commercialActivityAutoRegister.getId();

			// Cerrar antiguo autoregistro tracking
			Integer projectCommercialId = projectCommercial.getId();
			LinkedList<CommercialTracking> verificationCommercialTrackingList = AON.getCommercialTrackingList(
					api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getProjectCommercialProperty().eq(projectCommercialId)
							.and(f.getActivityProperty().eq(commercialActivityAutoRegisterId)));
			verificationCommercialTrackingList.forEach(verificationCommercialTracking -> {
				verificationCommercialTracking.setStatus((byte) 1);
				AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
						verificationCommercialTracking);
			});

			// Crear solicitud tracking
			CommercialTracking commercialTracking = new CommercialTracking().setDomain(target.getDomain().getId())
					.setDate(new Date()).setSeller(projectCommercial.getSeller())
					.setProjectCommercial(projectCommercial.getId()).setActivity(commercialActivityRequest.getId())
					.setStatus((byte) 1).setComments("El lead " + target.getName()
							+ " se ha registrado en la acci\u00f3n " + mkAction.getDescription() + ".");
			AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), commercialTracking);

			// Crear verification tracking
			commercialTracking = new CommercialTracking().setDomain(target.getDomain().getId()).setDate(new Date())
					.setSeller(projectCommercial.getSeller()).setProjectCommercial(projectCommercial.getId())
					.setActivity(commercialActivityCheck.getId()).setStatus((byte) 1).setComments(
							"Se ha enviado al lead " + target.getName() + " un mail para crear una empresa de trial.");
			AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), commercialTracking);

			// Crear autoregistro tracking
			Domain parentDomain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));

			commercialTracking = new CommercialTracking().setDomain(target.getDomain().getId()).setDate(new Date())
					.setSeller(projectCommercial.getSeller()).setProjectCommercial(projectCommercial.getId())
					.setActivity(commercialActivityAutoRegister.getId()).setStatus((byte) 0)
					.setComments("El lead " + target.getName() + " ha creado la empresa " + target.getName() + " ("
							+ target.getDocument() + "-" + parentDomain.getName() + ").");
			AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), commercialTracking);

		} else {

			// Create Operacion Comercial
			projectCommercial = null;

			if (actionTarget.getMarketingAction().getSellerDistribution() == MarketingSellerDistribution.MANUAL
					&& null != actionTarget.getMarketingAction().getSeller()) {
				// Create
				projectCommercial = new ProjectCommercial()
						.copy(new Project().setDomain(target.getDomain()).setRegistry(target.get())
								.setName(actionTarget.getMarketingAction().getDescription()).setDate(new Date())
								.setTas(false).setCommercial(true).setReservation(false).setActive(true))
						.setTarget(target.getId()).setSeller(actionTarget.getMarketingAction().getSeller())
						.setComments(actionTarget.getTarget().getComments()).setSource((byte) 8) // Marketing
						.setStatus((byte) 0).setStatusDate(new Date()).setProbability(0);

				projectCommercial = AON.saveProjectCommercial(api.getDomain().getName(), api.getDomain().getId(),
						api.getUser().getLogin(), projectCommercial);

			} else if (actionTarget.getMarketingAction()
					.getSellerDistribution() == MarketingSellerDistribution.AUTOMATIC
					&& null != actionTarget.getMarketingAction().getWorkgroup().getId()) {
				
				Seller nextSeller = getNextLinealSellerByWorkgroup(api.getDomain().getName(), api.getDomain().getId(),
						api.getUser().getLogin(), actionTarget.getMarketingAction().getWorkgroup().getId());
				
				if (null != nextSeller) {
					projectCommercial = new ProjectCommercial()
							.copy(new Project().setDomain(target.getDomain()).setRegistry(target.get())
									.setName(actionTarget.getMarketingAction().getDescription()).setDate(new Date())
									.setTas(false).setCommercial(true).setReservation(false).setActive(true))
							.setTarget(target.getId()).setSeller(nextSeller.getId())
							.setComments(actionTarget.getTarget().getComments()).setSource((byte) 8) // Marketing
							.setStatus((byte) 0).setStatusDate(new Date()).setProbability(0);

					projectCommercial = AON.saveProjectCommercial(api.getDomain().getName(), api.getDomain().getId(),
							api.getUser().getLogin(), projectCommercial);
					
				}
			}

			if (null != projectCommercial) {
				CommercialTracking commercialTracking = new CommercialTracking().setDomain(target.getDomain().getId())
						.setDate(new Date()).setSeller(projectCommercial.getSeller())
						.setProjectCommercial(projectCommercial.getId()).setActivity(commercialActivityRequest.getId())
						.setStatus((byte) 1).setComments("El lead " + target.getName()
								+ " se ha registrado en la acci\u00f3n " + mkAction.getDescription() + ".");
				AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
						commercialTracking);

				commercialTracking = new CommercialTracking().setDomain(target.getDomain().getId()).setDate(new Date())
						.setSeller(projectCommercial.getSeller()).setProjectCommercial(projectCommercial.getId())
						.setActivity(commercialActivityCheck.getId()).setStatus((byte) 1)
						.setComments("Se ha enviado al lead " + target.getName()
								+ " un mail para crear una empresa de trial.");
				AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
						commercialTracking);

				Domain parentDomain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(),
						api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));

				commercialTracking = new CommercialTracking().setDomain(target.getDomain().getId()).setDate(new Date())
						.setSeller(projectCommercial.getSeller()).setProjectCommercial(projectCommercial.getId())
						.setActivity(commercialActivityAutoRegister.getId()).setStatus((byte) 0)
						.setComments("El lead " + target.getName() + " ha creado la empresa " + target.getName() + " ("
								+ target.getDocument() + "-" + parentDomain.getName() + ").");
				AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
						commercialTracking);
				
				mkActionTarget.setProject(new Project().setId(projectCommercial.getId()));
			}
		}

		mkActionTarget.setActionTargetStatus((byte) 6); // Enviado
		AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				mkActionTarget);
		
		return null == projectCommercial ? null : projectCommercial.getSeller();
	}

	private static Seller getNextLinealSellerByWorkgroup(String domainName, int domain, String user, int workgroup)
			throws AonCoreException {
		List<TaskHolder> taskHolders = AON
				.getTaskHolderWorkgroupStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user),
						f -> f.getDomainProperty().eq(domain), workgroup, 0, Integer.MAX_VALUE)
				.filter(taskHolder -> taskHolder.isActive()).collect(Collectors.toList());
		Integer[] taskHolderIds = new Integer[taskHolders.size()];
		taskHolders.stream().map(taskHolder -> taskHolder.getId()).collect(Collectors.toList()).toArray(taskHolderIds);
		LinkedList<Seller> sellerList = AON.getSellerList(domainName, domain, user,
				f -> f.getStatusProperty().eq((byte) 0).and(f.getTaskHolderProperty().in(taskHolderIds)));
		Map<Seller, Date> sellerProjects = new HashMap<Seller, Date>();

		for (Seller seller : sellerList) {
			LinkedList<ProjectCommercial> projectCommercials = AON.getProjectCommercialList(domainName, domain, user,
					f -> f.getSellerProperty().eq(seller.getId()));

			// Si esta activo y no tiene ninguna operacion comercial se devuelve este
			if (projectCommercials.isEmpty())
				return seller;

			projectCommercials.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
			sellerProjects.put(seller, projectCommercials.get(0).getDate());
		}

		Optional<Date> oldestDate = sellerProjects.values().stream().sorted((d1, d2) -> d1.compareTo(d2)).findFirst();
		if (oldestDate.isEmpty())
			return null;
		else {
			for (Entry<Seller, Date> entry : sellerProjects.entrySet()) {
				if (entry.getValue().equals(oldestDate.get()))
					return entry.getKey();
			}
			return null;
		}
	}

	// ---------------------------------------------------------------------------------------------
	// AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------

	private static boolean checkExistingDomain(AonApiData api, String document, String targetEmail) {
		Domain parentDomain = null == api.getDomain().getParentId() ? api.getDomain() : AON.getDomain(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));
		
		if (null != parentDomain && null != parentDomain.getId()) {
			Domain domain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getNameProperty().like("%" + document + "%")
							.and(f.getParentProperty().eq(parentDomain.getId())));
			if (null != domain && null != domain.getId()) {
				String logoUrl = getLogoUrl(parentDomain, api.getUser());
				sendDomainExistsMail(api, domain, parentDomain, logoUrl, targetEmail);
				return true;
//				throw new IllegalArgumentException("El dominio " + domain.getName() + " ya existe para este despacho");
			}
		}
		
		return false;
	}

	private static void sendDomainExistsMail(AonApiData api, Domain domain, Domain parentDomain,
			String logoUrl, String targetEmail) {
		String from = getFromMessage(api);
		String bcc = "booking@aonsolutions.es";

		SESMessage msg = new SESMessage().setAlias(domain.getDescription()).setFrom(from).setReplyTo(from)
				.setTo(targetEmail).setBcc(bcc).setSubject(domain.getDescription() + " (DUPLICADO)")
				.setBody(createEnterpriseDuplicateBody(domain, parentDomain, logoUrl, from));

		SES.sendEmail(msg);
	}

	private static String createEnterpriseDuplicateBody(Domain domain, Domain parentDomain, String logoUrl,
			String from) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		String url = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
				+ "/app";
		
		if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "app.leevy.es"))
			url = "https://leevy.aon.solutions";
		else if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "infoautonomos.aonsolutions.net"))
			url = "https://infoautonomos.aon.solutions";
		
		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", domain.getDescription());
		context.put("url", url);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_duplicate.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	private static String getLogoUrl(Domain parentDomain, User user) {
		String logoUrl = null;
		try (CloseableAONContext aonContext = AONContext.getAONContext(parentDomain.getName(), user.getLogin())) {
			Company company = AON.getCompany(parentDomain, user, f -> f.getDomainProperty().eq(parentDomain.getId()));

			Attach attach = getLogoAttach(aonContext, company.getId());

			String str = "domain=" + attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
			String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));

			Domain attachDomain = DomainDAO.getDomain(aonContext, attach.getDomain().getId());
			logoUrl = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
					+ "/ms/download_attachment/" + attachDomain.getName() + "/" + attach.getCreationUser() + "/"
					+ result;
		}

		return logoUrl;
	}

	private static Attach getLogoAttach(AONContext aonContext, Integer enterpriseId) {
		Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
				f -> f.getTypeProperty().eq(SIGNATURE.value()).and(f.getAttachModuleProperty().eq(enterpriseId)),
				REGISTRY);

		if (attach1 == null || attach1.getData() == null)
			attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(LOGO.value()).and(f.getAttachModuleProperty().eq(enterpriseId)),
					REGISTRY);

		return attach1;
	}

	private static String getFromMessage(AonApiData api) {
		DomainUserRoles domainUserRoles = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(),
				api.getUser().getId());
		String from = null;

		if (domainUserRoles.hasParentCustomView() || domainUserRoles.hasCustomView()) {
			Domain parentDomain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));
			// Ya es el dominio padre el que hay en api.getDomain()
			if (null == api.getDomain().getParentId() && (null == parentDomain || null == parentDomain.getId())) {
				RegistryMedia emailMedia = AON.getRegistryMedia(api.getDomain(), api.getUser(),
						f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
			// Se busca el dominio padre
			} else if (null != parentDomain && null != parentDomain.getId()) {
				RegistryMedia emailMedia = AON.getRegistryMedia(api.getDomain(), api.getUser(),
						f -> f.getDomainProperty().eq(parentDomain.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
			}
		}

		return from;
	}

	private static String getFinishCreationHtml(AonApiData api, Domain parentDomain, Domain newDomain) {
		JSONObject data = api.getData();

		// Enterprise Data
		String name = JsonUtils.getString(data, "name");
		
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		String url = (isLocal ? "http" : "https") + "://" + newDomain.getName() + (isLocal ? ":8080" : "") + "/";
		
		String logoUrl = getLogoUrl(parentDomain, api.getUser());

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", name);
		context.put("url", url);
		context.put("domainName", urlMail);
		context.put("mail", userMail);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_created_response.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	/*
	private static String getFinishCreationHtml(AonApiData api, Domain parentDomain) {
		JSONObject data = api.getData();

		// Enterprise Data
		String name = JsonUtils.getString(data, "name");
		
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		String url = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
		+ "/app";
		
		if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "app.leevy.es"))
			url = "https://leevy.aon.solutions";
		else if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "infoautonomos.aonsolutions.net"))
			url = "https://infoautonomos.aon.solutions";
		
		String logoUrl = getLogoUrl(parentDomain, api.getUser());

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", name);
		context.put("url", url);
		context.put("domainName", urlMail);
		context.put("mail", userMail);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_created_response.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	*/

	private static String createEnterpriseDuplicateBody(AonApiData api, Domain domain, Domain parentDomain) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		String url = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
				+ "/app";
		
		if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "app.leevy.es"))
			url = "https://leevy.aon.solutions";
		else if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "infoautonomos.aonsolutions.net"))
			url = "https://infoautonomos.aon.solutions";
		
		String logoUrl = getLogoUrl(parentDomain, api.getUser());
		String from = getFromMessage(api);
		
		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", domain.getDescription());
		context.put("url", url);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_duplicate.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
}
