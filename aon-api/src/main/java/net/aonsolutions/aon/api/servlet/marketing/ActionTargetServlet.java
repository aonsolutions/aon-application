package net.aonsolutions.aon.api.servlet.marketing;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
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
import com.esferalia.aon.occam.api.model.PayrollWorkplace;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.product.ProductBookingType;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
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
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.TargetStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AuthDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CommercialDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.MarketingCampaignDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryNoteDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryRelationshipDAO;
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
import net.aonsolutions.aon.api.utils.EmailFromToType;
import net.aonsolutions.aon.api.utils.EmailFromToUtils;
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
	private static Domain parent = null;
	
	private static EmailFromToUtils emailFromToUtils;
	private static String bookingEmail = "booking@aonsolutions.es";

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
		JSONObject result = new JSONObject();
		
		try (CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())) {
			
			ctx.transaction(t -> {
				
				// ActionTarget

				ActionTarget actionTarget = new ActionTarget(api.getData());

				// Marketing Action
				
				MarketingAction ma = MarketingCampaignDAO.getAction(ctx, actionTarget.getMarketingAction().getId());				
				actionTarget.setMarketingAction(ma);
				
				// EmailFromToUtils
				
				emailFromToUtils = new EmailFromToUtils(ctx);
				emailFromToUtils.setMarketingAction(ma);

				// Ceck Exist Domain
				
				Domain existDomain = checkExistingDomain(ctx, api, actionTarget.getTarget().getDocument(), actionTarget.getTarget().getEmail());
				if(null != existDomain) {
					throw new IllegalArgumentException("El dominio " + existDomain.getName() + " ya existe para este despacho");
				}
				
				// Get / Save Target
				
				Optional<Target> targetOpt = TargetDAO.getStream(ctx, 
						f -> f.getDocumentProperty().eq(actionTarget.getTarget().getDocument().toUpperCase())
							.and(f.getDomainProperty().eq(api.getDomain().getId()))
						).findFirst(); 

				Target target = targetOpt.isEmpty()
						? createTarget(ctx, api, actionTarget)
						: targetOpt.get();
				
				// Save Marketing Action Target
				
				MarketingActionTarget mkActionTarget = null;
				try {
					mkActionTarget = createMkActionTarget(ctx, api, actionTarget, target, !targetOpt.isEmpty());
				} catch (Exception e) {
					System.out.println("Error creacion MarketingActionTarget : " + e.getMessage());
				}

				// Create Operacion Comercial

				Integer seller = createCommercialOperation(ctx, api, actionTarget, target, mkActionTarget);

				// Return data

				result.put("type", "success");

				// Send mail

				boolean trial = JsonUtils.getboolean(api.getData().getJSONObject("actionTarget"), "trial");
				if (trial) {
					sendCreationEnterpriseMail(ctx, api, actionTarget, target.getId(), seller);
					result.put("message","Se ha creado el lead correctamente y se ha enviado un mail para la creaci\u00f3n de la empresa.");
				} else
					result.put("message", "Se ha creado el lead correctamente.");

			});
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Algo ha salido mal : " + e.getMessage());
		}
		
		return result;

	}

	private static Target createTarget(CloseableAONContext ctx, AonApiData api, ActionTarget actionTarget) {
		Target target = new Target()
				.copy(
					new Registry()
						.setDomain(api.getDomain())
						.setName(actionTarget.getTarget().getName())
						.setDocumentType(DocumentType.identify(actionTarget.getTarget().getDocument()))
						.setDocumentCountry(Country.safeValueOf(actionTarget.getTarget().getDocumentCountry()))
						.setDocument(actionTarget.getTarget().getDocument().toUpperCase())
						.setNationality(Country.safeValueOf(actionTarget.getTarget().getDocumentCountry()))

				).setScope(actionTarget.getMarketingAction().getMarketingCampaign().getScope());

		target = TargetDAO.save(ctx, target);

		// Save Registry Note (Observacion)

		if (AonStringUtils.isNotBlank(actionTarget.getTarget().getComments())) {
			RegistryNote note = new RegistryNote()
					.setDomain(target.getDomain().getId())
					.setRegistry(target.getId())
					.setDescription("Observaci\u00f3n")
					.setNoteDate(new Date())
					.setComments(actionTarget.getTarget().getComments())
					.setNoteType(NoteType.OBSERVATION)
					.setSecurityLevel(SecurityLevel.OFFICIAL);

			RegistryNoteDAO.save(ctx, note);
		}

		// Save Registry Address

		Integer raddressId = null;

		if (AonStringUtils.isNotBlank(actionTarget.getTarget().getAddress())) {
			
			Optional<GeoZone> geozoneOpt = GeoZoneDAO.getStream(ctx, 
					f -> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getCodeProperty().eq(actionTarget.getTarget().getGeozoneCode())))
					.findFirst();

			RegistryAddress registryAddress = new RegistryAddress()
					.setDomain(target.getDomain().getId())
					.setRegistry(target.getId())
					.setMain(true)
					.setStreetType(StreetType.getForAeatCode(actionTarget.getTarget().getStreetType(),AonLanguage.SPANISH))
					.setAddress(actionTarget.getTarget().getAddress())
					.setNumber(actionTarget.getTarget().getNumber())
					.setZip(actionTarget.getTarget().getZip())
					.setCity(actionTarget.getTarget().getCity())
					.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null)
					.setGeozoneCode(actionTarget.getTarget().getGeozoneCode())
					.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null);

			registryAddress = RegistryAddressDAO.save(ctx, registryAddress);
			raddressId = registryAddress.getId();
		}

		// Save Registry Media

		saveMedia(ctx, api, target.getDomain().getId(), target.getId(), MediaType.CELLULAR, actionTarget.getTarget().getPhone(), raddressId);
		saveMedia(ctx, api, target.getDomain().getId(), target.getId(), MediaType.EMAIL, actionTarget.getTarget().getEmail(), raddressId);
		
		return target;
	}

	private static MarketingActionTarget createMkActionTarget(CloseableAONContext ctx, AonApiData api, ActionTarget actionTarget, Target target, boolean hasCreateTarget) {
		MarketingActionTarget mkActionTarget = new MarketingActionTarget()
				.copy(target)
				.setActionTargetDomain(target.getDomain().getId())
				.setMarketingAction(actionTarget.getMarketingAction())
				.setActionTargetStatus((byte) 0)
				.setComments(actionTarget.getTarget().getComments());

		// Si no existia el target se crea el marketingActionTarget
		
		if (hasCreateTarget) {
			MarketingCampaignDAO.saveActionTarget(ctx, mkActionTarget);

		
		// Si existia el target se busca o crea el marketingActionTarget
		} else {
			MarketingActionTargetParams params = new MarketingActionTargetParams();
			params.setDomainName(api.getDomain().getName());
			params.setDomain(api.getDomain().getId());
			params.setUser(api.getUser().getLogin());
			params.setMarketingAction(actionTarget.getMarketingAction());
			params.setOffset(0);
			params.setLimit(Integer.MAX_VALUE);

			List<MarketingActionTarget> marketingActionTargets = MarketingCampaignDAO.getActionTargetList(ctx, params);

			Optional<MarketingActionTarget> marketingActionTargetOpt = marketingActionTargets
					.stream()
					.filter(targetIt -> targetIt.getId().equals(target.getId()))
					.findAny();

			if (marketingActionTargetOpt.isEmpty())
				MarketingCampaignDAO.saveActionTarget(ctx, mkActionTarget);
			else
				mkActionTarget = marketingActionTargetOpt.get();
		}
		
		return mkActionTarget;
	}

	private static void sendCreationEnterpriseMail(CloseableAONContext ctx, AonApiData api, ActionTarget actionTarget, Integer targetId, Integer seller) {
		
		Domain parent = getParentDomain(ctx, api);

		if (null != parent) {

			// Construir la URL
			
			String postUrl = (
					isLocal ? "http" : "https") + 
					"://" + parent.getName() + 
					(isLocal ? ":8080" : "") + 
					"/ms/api/action-target/create-enterprise";
			try {
				postUrl += 
						"?name=" + URLEncoder.encode(actionTarget.getTarget().getName(), "UTF-8") + 
						"&document=" + actionTarget.getTarget().getDocument().toUpperCase() + 
						"&streetType=" + actionTarget.getTarget().getStreetType() + 
						"&address=" + URLEncoder.encode(actionTarget.getTarget().getAddress(), "UTF-8") + 
						"&number=" + actionTarget.getTarget().getNumber() + 
						"&zip=" + actionTarget.getTarget().getZip() + 
						"&geozoneCode=" + actionTarget.getTarget().getGeozoneCode() + 
						"&city=" + URLEncoder.encode(actionTarget.getTarget().getCity(), "UTF-8") + 
						"&phone=" + actionTarget.getTarget().getPhone() + 
						"&email=" + actionTarget.getTarget().getEmail() + 
						"&target=" + targetId.toString() + 
						"&domain_name=" + parent.getName() + 
						"&domain_id=" + parent.getId().toString() + 
						"&domain_login=" + api.getUser().getLogin() + 
						"&session_id=" + api.getToken() + 
						"&marketingAction=" + actionTarget.getMarketingAction().getId().toString()
						;
				
				if(null != seller)
					postUrl += "&seller=" + seller.toString();
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			String from = MarketingEmailTemplates.getFromMessage(ctx, api, parent);

			String logoUrl = MarketingEmailTemplates.getLogoUrl(parent, api.getUser());

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
			context.put("document", actionTarget.getTarget().getDocument().toUpperCase());
			context.put("contact", AonStringUtils.isBlank(from) ? bookingEmail : from);

			Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_ayudat.vm");

			StringWriter writer = new StringWriter();
			template.merge(context, writer);

			sendTrailEnterpriseMail(ctx, parent, actionTarget, from, writer.toString());

		}
	}

	private static void sendTrailEnterpriseMail(CloseableAONContext ctx, Domain parent, ActionTarget actionTarget, String from, String body) {
		String bookingBcc = bookingEmail;
		
		List<String> bccs = emailFromToUtils.getEmails(EmailFromToType.MARKETING_ACTION_EMAILS);
		bccs.add(bookingBcc);

		SESMessage msg = new SESMessage()
				.setAlias(parent.getDescription())
				.setFrom(from)
				.setReplyTo(from)			
				.setTo(actionTarget.getTarget().getEmail())
				.setBcc(bccs)
				.setSubject("Confirma tu registro y empieza a facturar")
				.setBody(body)
				;
		
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

		// Create Enterprise

		System.out.println("----------------------- Create Enterprise -----------------------");

		try (CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())) {
			ctx.transaction(t -> {
				
				parent = getParentDomain(ctx, api);
				
				System.out.println("----------------------- Check Customer");
				
				Customer customer = checkCustomer(api, ctx);
 				
 				// System.out.println("----------------------- Create Scope");
				
				// Scope newScope = createScope(api, ctx);
				
				System.out.println("----------------------- Create Domain");
				
 				// Domain newDomain = createDomain(api, ctx, newScope);
 				Domain newDomain = createDomain(api, ctx, new Scope());
 				urlMail = newDomain.getName();
				
				System.out.println("----------------------- Create Auth / User");
				
				User newUser = createDefaultUser(api, ctx, newDomain);
				
				// TODO: esto hay que hacerlo cuando nos aseguremos que los sellers que se muestran en la accion comercial tenga un usuario del padre asociado
				
				// System.out.println("----------------------- Create User Scope (supportSeller / api.getUser)");
				
				// createUserScope(api, ctx, newDomain, newScope);
				
				System.out.println("----------------------- Domain Apps / Config");
				

				Integer marketingActionId = JsonUtils.getInteger(api.getData(), "marketingAction");
				MarketingAction mkAction = MarketingCampaignDAO.getAction(ctx, marketingActionId);
				Domain officeDomain = DomainDAO.getDomain(ctx, mkAction.getDomain());
				
				LinkedList<ProductBooking> productsBooking = ProductDAO.getBookingList(
						ctx, 
						new ProductParams()
							.setDomainName(officeDomain.getName())
							.setDomain(officeDomain.getId())
							.setUser(api.getUser().getLogin())
							.setOffset(0)
							.setLimit(Integer.MAX_VALUE)
						);
				
				insertDomainConfiguration(ctx, api.getDomain(), newDomain, newUser, customer, productsBooking);
				
				System.out.println("----------------------- Account Period");
				
				insertAccountPeriod(ctx, newDomain);
				
				System.out.println("----------------------- Project Commercial");
				
				updateProjectCommercial(api, ctx);
				
				createTaskHolderScope(api, ctx, newDomain);

				// Send mail
				sendEnterpriseCreatedMail(api, ctx, newDomain);
				
				createdDomain = newDomain;
				
				System.out.println("----------------------- [End] Create Enterprise -----------------------");
				
			});
		}

		System.out.println("----------------------- Create Enterprise (END) -----------------------");

		return MarketingEmailTemplates.getFinishCreationHtml(api, parent, createdDomain, urlMail, userMail);
	}
	
	private static void createTaskHolderScope(AonApiData api, CloseableAONContext ctx, Domain newDomain) {
		Integer marketingActionId = JsonUtils.getInteger(api.getData(), "marketingAction");
		MarketingAction marketingAction = MarketingCampaignDAO.getAction(ctx, marketingActionId);		
		
		TaskHolder mkActionTH = marketingAction.getTaskHolder();
		
		// Si existe responsable se manda una copia del mail
		if(null != mkActionTH && null != mkActionTH.getId() && null != mkActionTH.getUser() && null != mkActionTH.getUser().getId()) {
			
			Integer registryId = JsonUtils.getInteger(api.getData(), "target");
			
			Domain domain = DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(marketingAction.getDomain()));
			Domain parentDomain = DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(domain.getParentId()));

			Registry registry = RegistryDAO.get(ctx, registryId);
			
			Stream<Scope> scopes = SecurityDAO.getScopeStream(ctx, f -> f.getDescriptionProperty().eq(registry.getDocument()).and(f.getDomainProperty().eq(null == parentDomain.getId() ? domain.getId() : parentDomain.getId())));

			Scope scope = null;
			if (scopes.count() == 0) {
				Scope newScope = new Scope().setDomain(null == parentDomain.getId() ? domain.getId() : parentDomain.getId()).setDescription(registry.getDocument());
				scope = SecurityDAO.insertScope(ctx, newScope);
			} else scope = scopes.findFirst().get();
			
			newDomain.setScope(scope.getId());
			DomainDAO.updateDomainScope(ctx, newDomain);
			
			User user = UserDAO.get(ctx, f -> f.getIdProperty().eq(mkActionTH.getUser().getId()),
					new Options().setFull(true));
			
			SecurityDAO.insertUserScope(ctx, new UserScope().setDomain(user.getDomain().getId())
					.setScope(scope.getId()).setUserId(user.getId()));
			
		}
		
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
	
	private static Domain createDomain(AonApiData api, CloseableAONContext ctx, Scope newScope) throws Exception {
		JSONObject data = api.getData();
		
		String name = JsonUtils.getString(data, "name");
		String document = JsonUtils.getString(data, "document");
		Integer sellerSupport = JsonUtils.getInteger(data, "seller");

		Company company = new Company();
		company.setName(name);
		company.setDocument(document.toUpperCase());
		company.setLegalPerson(AonDocumentUtil.isValidCIF(document));

		checkCompany(company);
		
		Domain parentDomain = null == api.getDomain().getParentId() 
				? api.getDomain() 
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));
		
		Domain findDomain = checkExistingDomain(api, ctx);
		if(null != findDomain && null != findDomain.getId()) {
			MarketingEmailTemplates.createEnterpriseDuplicateBody(ctx, api, findDomain, parentDomain);
			throw new AonApiException("La empresa con identificador " + document + " para el cliente " + name + " ya existe");
		}
		
		if (null == parentDomain || null == parentDomain.getId())
			throw new AonApiException("No existe empresa padre desde la que colgar esta empresa");

		// Get owner email
		
		Stream<RegistryMedia> sellerSupportMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(sellerSupport));
		Optional<RegistryMedia> sellerSupportEmailOpt = sellerSupportMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
		
		String owner = null;
		if(sellerSupportEmailOpt.isEmpty() || AonStringUtils.isBlank(sellerSupportEmailOpt.get().getValue())) {
			// Si no existe responsable se asigna como owner al from
			owner = MarketingEmailTemplates.getFromMessage(ctx, api, parent);
		} else 
			owner = sellerSupportEmailOpt.get().getValue();
		
		if(AonStringUtils.isBlank(owner))
			throw new AonApiException("No existe email como owner para la creaci\u00f3n de la empresa");
		
		String domainNewName = company.getDocument().toLowerCase() + "-" + (AonStringUtils.isBlank(parentDomain.getSubDomainSuffix()) ? parentDomain.getName() : parentDomain.getSubDomainSuffix());
		
		Domain newDomain = new Domain()
				.setName(domainNewName.toLowerCase())
				.setDescription(company.getName())
				.setOwner(owner) // Alguno mas
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
		
		createCompanyMedia(api, ctx, newDomain, enterpriseScope);
		
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
	
	private static void createCompanyMedia(AonApiData api, CloseableAONContext ctx, Domain newDomain, Scope enterpriseScope) {
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
			createWorkplace(ctx, newDomain, newCompany.getId(), comapnyRaddressId, customer.getId(), geozoneCode, enterpriseScope.getId());
		
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
	
	private static void createWorkplace(CloseableAONContext ctx, Domain newDomain, Integer companyId, Integer raddressId, Integer customerId, String geozoneCode, Integer enterpriseScope) {
		Workplace workplace = new Workplace()
				.setActive(true)
				.setDescription("PRINCIPAL")
				.setDomain(newDomain.getId())
				.setEnterprise(companyId)
				.setAddress(new RegistryAddress().setId(raddressId))
				.setCustomer(customerId)
				.setEconomicAgreement(getEconomicAgreement(geozoneCode))
				.setScope(enterpriseScope)
				.setPayrollWorkplace(new PayrollWorkplace().setDomain(newDomain.getId()))
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
		
		Customer customer = CustomerDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst().get();
		
		if(null == customer)
			throw new AonApiException("No ha sido posible encontrar al cliente seleccionado");

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

				auth = AuthDAO.insertAuth(ctx, auth);
			} else {
				auth.setPassword(pass);
				auth = AuthDAO.updateAuth(ctx, auth);
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
		
		SecurityDAO.insertUserApplicationAio(ctx, newDomain.getId(), userId);
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
	
	private static void insertDomainConfiguration(CloseableAONContext ctx, Domain officeDomain, Domain newDomain, User newUser, Customer customer, LinkedList<ProductBooking> productsBooking) {
		SecurityDAO.saveDomainMaxDefinedUser(ctx, newDomain.getId(), 0);
		
		Optional<ProductBooking> defaultProduct = productsBooking.stream().filter(pb -> pb.getBookingType().equals(ProductBookingType.DEFAULT)).findFirst();
		
		if(defaultProduct.isEmpty() || defaultProduct.get().getAonApps().isEmpty()) {
			DomainApp domainApp = new DomainApp()
					.setDomain(newDomain.getId())
					.setApp(AonApp.INVOICE)
					.setActive(true);
			 
			SecurityDAO.saveDomainApp(ctx, domainApp, false);
			
			domainApp = new DomainApp()
					.setDomain(newDomain.getId())
					.setApp(AonApp.DOCUMENTAL)
					.setActive(true);
			
			SecurityDAO.saveDomainApp(ctx, domainApp, false);
			
			domainApp = new DomainApp()
					.setDomain(newDomain.getId())
					.setApp(AonApp.MESSENGER)
					.setActive(true);
			
			SecurityDAO.saveDomainApp(ctx, domainApp, false);
		} else {
			defaultProduct.get().getAonApps().forEach(aonApp -> {
				DomainApp domainApp = new DomainApp()
						.setDomain(newUser.getDomain().getId())
						.setActive(true)
						.setApp(aonApp)
						;
				
				SecurityDAO.saveDomainApp(ctx, domainApp, false);
				
				insertUserAppRole(ctx, newUser, aonApp);
			});
			
			if(defaultProduct.get().isWebhook() && null != defaultProduct.get().getWebhookProductId())
				try {
					fireWebhook(ctx, officeDomain, newDomain, newUser.getLogin(), defaultProduct.get(), customer);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		
		ApplicationParameter trailParam = new ApplicationParameter()
				.setDomain(newDomain.getId())
				.setName(AppParam.TRIAL)
				.setValue("50");
		
		AppParamDAO.insertApplicationParameter(ctx, trailParam);
	}
	
	private static void fireWebhook(CloseableAONContext ctx, Domain officeDomain,  Domain newDomain,  String login, ProductBooking product, Customer customer) throws Exception {
		RegistryMedia customerEmail = RegistryMediaDAO.get(ctx, f -> f.getRegistryProperty().eq(customer.getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
		RegistryMedia customerPhone = RegistryMediaDAO.get(ctx, f -> f.getRegistryProperty().eq(customer.getId()).and(f.getMediaProperty().eq(MediaType.CELLULAR.value())));
		
		String targetURL =  "https://" + officeDomain.getName() + "/ms/api/contracted_plans_servlet/callForm";

		// LOCAL
		//String targetURL =  "http://" + officeDomain.getName() + ":8080/ms/api/contracted_plans_servlet/callForm";
		
		 JSONObject companyData = new JSONObject()
				 .put("name", customer.getName()) 
				 .put("document", customer.getDocument()) 
				 ;
		
		 JSONObject formData = new JSONObject()
		            .put("email", null != customerEmail && null != customerEmail.getId() ? customerEmail.getValue() : AonStringUtils.EMPTY)
		            .put("name", customer.getName()) 
		            .put("surname", AonStringUtils.EMPTY) 
		            .put("phone", null != customerPhone && null != customerPhone.getId() ? customerPhone.getValue() : AonStringUtils.EMPTY)
		            .put("chanel", "Solicitud Software")
		            .put("companyData", companyData)
		            .put("domainUrl", newDomain.getName())
		            .put("plan", product.getCode())
		            .put("serviceId", product.getWebhookProductId());
		
		URL url = new URI(targetURL).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		
		conn.setRequestProperty("domain_login", login);
		conn.setRequestProperty("session_id", "AONd95770f269e711eb94390242ac130002");
		
		conn.setDoOutput(true);

		try (OutputStream os = conn.getOutputStream()) {
		    os.write(formData.toString().getBytes("UTF-8"));
		}
		
		conn.getInputStream().close();
	}
	
	private static void insertUserAppRole(CloseableAONContext ctx, User user, AonApp aonApp) {
		List<AonRole> aonRoles = AonApp.getPortalAonRole(aonApp);
		Byte[] arrayAonRoles = aonRoles.stream()
			    .map(ar -> ar.value())
			    .toArray(Byte[]::new);
		
		List<UserAppRole> userAppRole = SecurityDAO.getUserAppRoleStream(ctx, f -> f.getUserIdProperty().eq(user.getId()).and(f.getRoleProperty().in(arrayAonRoles))).collect(Collectors.toList());
		
		if(userAppRole.isEmpty()) {
			aonRoles.forEach(aonRole -> {
				UserAppRole newUserAppRole = new UserAppRole()
						.setDomain(user.getDomain().getId())
						.setApp(null)
						.setUser(user.getId())
						.setRole(aonRole);
				
				SecurityDAO.insertUserAppRole(ctx, newUserAppRole);
			});
			
		} 
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
		
		MarketingAction mkAction = MarketingCampaignDAO.getAction(ctx, marketingActionId);
		Domain officeDomain = DomainDAO.getDomain(ctx, mkAction.getDomain());

		// Update autoregistro status
		CommercialActivity commercialActivityAutoRegister = CommercialDAO.getCommercialActivity(ctx, f -> f.getDomainProperty().eq(officeDomain.getId()).and(f.getNameProperty().like("%AUTOREGISTRO%")));
		if (null == commercialActivityAutoRegister) {
			CommercialActivity ca = new CommercialActivity().setDomain(api.getDomain().getId()).setName("AUTOREGISTRO");

			commercialActivityAutoRegister = CommercialDAO.save(ctx, ca);
		}
		
		ProjectDAO.getProjectCommercialStream(ctx, f -> f.getDomainProperty().eq(officeDomain.getId()).and(f.getTargetProperty().eq(target))).findFirst().orElse(new ProjectCommercial());

		ProjectCommercial projectCommercial = ProjectDAO.getProjectCommercialStream(ctx, f -> f.getDomainProperty().eq(officeDomain.getId()).and(f.getTargetProperty().eq(target))).findFirst().orElse(new ProjectCommercial());

		Integer commercialActivityAutoRegisterId = commercialActivityAutoRegister.getId();

		if(projectCommercial  != null && null != projectCommercial.getId()) {
			// Cerrar antiguo autoregistro tracking
			
			LinkedList<CommercialTracking> verificationCommercialTrackingList = CommercialDAO.getCommercialTrackingStream(ctx, 
					f -> f.getProjectCommercialProperty().eq(projectCommercial.getId())
						.and(f.getActivityProperty().eq(commercialActivityAutoRegisterId)))
					.collect(Collectors.toCollection(LinkedList::new));
			
			verificationCommercialTrackingList.forEach(verificationCommercialTracking -> {
				verificationCommercialTracking.setStatus((byte) 1);
				verificationCommercialTracking.setDate(new Date());
				
				CommercialDAO.save(ctx, verificationCommercialTracking);

			});

			projectCommercial.setStatus((byte) 3);
			projectCommercial.setStatusDate(new Date());
			
			ProjectDAO.saveProjectCommercial(ctx, projectCommercial);
		}

		Optional<MarketingActionTarget> mkActionTarget = mkAction.getTargets().stream()
				.filter(mkTarget -> mkTarget.getId().equals(target)).findFirst();
		if (mkActionTarget.isPresent())
			MarketingCampaignDAO.saveActionTarget(ctx, mkActionTarget.get().setActionTargetStatus((byte) 5)); // Finalizado
	}
	
	// ---------------------------------------------------------------------------------------------
	// SEND CREATED ENTERPRISE EMAIL
	// ---------------------------------------------------------------------------------------------
	
	private static void sendEnterpriseCreatedMail(AonApiData api, CloseableAONContext ctx, Domain newDomain) {
		JSONObject data = api.getData();
		String name = JsonUtils.getString(data, "name");
		Integer registry = JsonUtils.getInteger(data, "target");

		Integer marketingActionId = JsonUtils.getInteger(data, "marketingAction");
		
		MarketingAction ma = MarketingCampaignDAO.getAction(ctx, marketingActionId);
		
		// EmailFromToUtils
		
		emailFromToUtils = new EmailFromToUtils(ctx);
		emailFromToUtils.setMarketingAction(ma);
		
		Domain parent = getParentDomain(ctx, api);
		
		String logoUrl = MarketingEmailTemplates.getLogoUrl(parent, api.getUser());

		String from = MarketingEmailTemplates.getFromMessage(ctx, api, parent);
		
		if(AonStringUtils.isBlank(from))
			throw new AonApiException("No existe email definido en el entorno para la creaci\u00f3n de empresas");

		String bookingBcc = bookingEmail;
		List<String> bccs = emailFromToUtils.getEmails(EmailFromToType.MARKETING_ACTION_EMAILS);
		bccs.add(bookingBcc);
		
		Optional<Target> targetOpt = TargetDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst();
		Stream<RegistryMedia> targetMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(targetOpt.get().getId()));
		Optional<RegistryMedia> targetEmailOpt = targetMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
		
		if(targetEmailOpt.isEmpty())
			throw new AonApiException("No existe email para el cliente potencial seleccionado");
			
		SESMessage msg = new SESMessage()
				.setAlias(parent.getDescription())
				.setFrom(from)
				.setTo(targetEmailOpt.get().getValue())
				.setBcc(bccs)
				.setReplyTo(from)
				.setSubject("Ya puedes empezar a usar tu cuenta de facturaci\u00f3n")
				.setBody(createEnterpriseCreatedBody(logoUrl, newDomain, from, name, parent.getDescription()));

		SES.sendEmail(msg);
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
		context.put("contact", AonStringUtils.isBlank(from) ? bookingEmail : from);

		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/registry_enterprise_created_ayudat.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	// ---------------------------------------------------------------------------------------------
	// AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------

	private static void saveMedia(CloseableAONContext ctx, AonApiData api, Integer domain, Integer registry, MediaType mediaType, String value, Integer raddress) {
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

	private static Integer createCommercialOperation(CloseableAONContext ctx, AonApiData api, ActionTarget actionTarget, Target target, MarketingActionTarget mkActionTarget) {
		
		// Operacion Comercial - SOLICITUD
		
		CommercialActivity commercialActivityRequest =  CommercialDAO.getCommercialActivity(ctx, 
				f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getNameProperty().like("%SOLICITUD%")));
		
		// Si no existe se crea
		if (null == commercialActivityRequest) {
			CommercialActivity ca = new CommercialActivity().setDomain(api.getDomain().getId()).setName("SOLICITUD");
			CommercialDAO.save(ctx, ca);
		}
		
		// Operacion Comercial - VERIFICACION
		
		CommercialActivity commercialActivityCheck =  CommercialDAO.getCommercialActivity(ctx, 
					f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getNameProperty().like("%VERIFICACION%")));

		// Si no existe se crea
		if (null == commercialActivityCheck) {
			CommercialActivity ca = new CommercialActivity().setDomain(api.getDomain().getId()).setName("VERIFICACION");
			CommercialDAO.save(ctx, ca);
		}
		
		// Operacion Comercial - AUTOREGISTRO

		CommercialActivity commercialActivityAutoRegister = CommercialDAO.getCommercialActivity(ctx, 
				f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getNameProperty().like("%AUTOREGISTRO%"))); 
		
		// Si no existe se crea
		if (null == commercialActivityAutoRegister) {
			CommercialActivity ca = new CommercialActivity().setDomain(api.getDomain().getId()).setName("AUTOREGISTRO");
			CommercialDAO.save(ctx, ca);
		}
		
		// Obtenemos la accion comercial
		MarketingAction mkAction = MarketingCampaignDAO.getAction(ctx, actionTarget.getMarketingAction().getId());				

		ProjectCommercial projectCommercial = null;
		
		// Existe expediente: se cierra la verificacion antigua y se crean los nuevos trackings
		
		if (null != mkActionTarget.getProject() && null != mkActionTarget.getProject().getId()) {

			projectCommercial = ProjectDAO.getProjectCommercialStream(ctx, 
					f -> f.getProjectProperty().eq(null == mkActionTarget.getProject() ? null : mkActionTarget.getProject().getId()))
					.findFirst()
					.orElse(new ProjectCommercial());
			
			Integer commercialActivityAutoRegisterId = commercialActivityAutoRegister.getId();

			// Cerrar antiguo autoregistro tracking
			Integer projectCommercialId = projectCommercial.getId();
			
			LinkedList<CommercialTracking> verificationCommercialTrackingList = CommercialDAO.getCommercialTrackingStream(ctx, 
					f -> f.getProjectCommercialProperty().eq(projectCommercialId)
						.and(f.getActivityProperty().eq(commercialActivityAutoRegisterId))
					).collect(Collectors.toCollection(LinkedList::new));
					
			verificationCommercialTrackingList.forEach(verificationCommercialTracking -> {
				verificationCommercialTracking.setStatus((byte) 1);
				CommercialDAO.save(ctx, verificationCommercialTracking);
			});

			// Crear solicitud tracking
			CommercialTracking commercialTracking = new CommercialTracking()
					.setDomain(target.getDomain().getId())
					.setDate(new Date())
					.setSeller(projectCommercial.getSeller())
					.setProjectCommercial(projectCommercial.getId())
					.setActivity(commercialActivityRequest.getId())
					.setStatus((byte) 1)
					.setComments("El lead " + target.getName() + " se ha registrado en la acci\u00f3n " + mkAction.getDescription() + ".");
			
			CommercialDAO.save(ctx, commercialTracking);

			// Crear verification tracking
			commercialTracking = new CommercialTracking()
					.setDomain(target.getDomain().getId())
					.setDate(new Date())
					.setSeller(projectCommercial.getSeller())
					.setProjectCommercial(projectCommercial.getId())
					.setActivity(commercialActivityCheck.getId())
					.setStatus((byte) 1)
					.setComments("Se ha enviado al lead " + target.getName() + " un mail para crear una empresa de trial.");
			
			CommercialDAO.save(ctx, commercialTracking);

			// Crear autoregistro tracking
			Domain parentDomain = getParentDomain(ctx, api);

			commercialTracking = new CommercialTracking()
					.setDomain(target.getDomain().getId())
					.setDate(new Date())
					.setSeller(projectCommercial.getSeller())
					.setProjectCommercial(projectCommercial.getId())
					.setActivity(commercialActivityAutoRegister.getId())
					.setStatus((byte) 0)
					.setComments(
							"El lead " + target.getName() + " ha creado la empresa " + target.getName() + " ("
							+ target.getDocument() + "-" + parentDomain.getName() + ")."
					);
			
			CommercialDAO.save(ctx, commercialTracking);

		// No existe expediente
			
		} else {

			projectCommercial = null;

			if (actionTarget.getMarketingAction().getSellerDistribution() == MarketingSellerDistribution.MANUAL && null != actionTarget.getMarketingAction().getTaskHolder().getId()) {
				
				Seller seller = SellerDAO.get(ctx, f -> f.getDomainProperty().eq(target.getDomain().getId()).and(f.getTaskHolderProperty().eq(actionTarget.getMarketingAction().getTaskHolder().getId())));
				
				// Create
				projectCommercial = new ProjectCommercial()
						.copy(
							new Project()
								.setDomain(target.getDomain())
								.setRegistry(target.get())
								.setName(actionTarget.getMarketingAction().getDescription()).setDate(new Date())
								.setTas(false)
								.setCommercial(true)
								.setReservation(false)
								.setActive(true))
						.setTarget(target.getId())
						.setSeller(seller.getId())
						.setComments(actionTarget.getTarget().getComments())
						.setSource((byte) 8) // Marketing
						.setStatus((byte) 0)
						.setStatusDate(new Date())
						.setProbability(0);

				projectCommercial = ProjectDAO.saveProjectCommercial(ctx, projectCommercial);

			} else if (actionTarget.getMarketingAction().getSellerDistribution() == MarketingSellerDistribution.AUTOMATIC && null != actionTarget.getMarketingAction().getWorkgroup().getId()) {
				
				Seller nextSeller = getNextLinealSellerByWorkgroup(ctx, api.getDomain().getId(), actionTarget.getMarketingAction().getWorkgroup().getId());
				
				if (null != nextSeller) {
					projectCommercial = new ProjectCommercial()
							.copy(
								new Project()
									.setDomain(target.getDomain())
									.setRegistry(target.get())
									.setName(actionTarget.getMarketingAction().getDescription())
									.setDate(new Date())
									.setTas(false)
									.setCommercial(true)
									.setReservation(false)
									.setActive(true))
							.setTarget(target.getId())
							.setSeller(nextSeller.getId())
							.setComments(actionTarget.getTarget().getComments())
							.setSource((byte) 8) // Marketing
							.setStatus((byte) 0)
							.setStatusDate(new Date()).
							setProbability(0);
					
					projectCommercial = ProjectDAO.saveProjectCommercial(ctx, projectCommercial);
					
				}
			}

			if (null != projectCommercial) {
				CommercialTracking commercialTracking = new CommercialTracking()
						.setDomain(target.getDomain().getId())
						.setDate(new Date())
						.setSeller(projectCommercial.getSeller())
						.setProjectCommercial(projectCommercial.getId())
						.setActivity(commercialActivityRequest.getId())
						.setStatus((byte) 1).setComments("El lead " + target.getName() + " se ha registrado en la acci\u00f3n " + mkAction.getDescription() + ".");
				
				CommercialDAO.save(ctx, commercialTracking);

				commercialTracking = new CommercialTracking()
						.setDomain(target.getDomain().getId())
						.setDate(new Date())
						.setSeller(projectCommercial.getSeller())
						.setProjectCommercial(projectCommercial.getId())
						.setActivity(commercialActivityCheck.getId())
						.setStatus((byte) 1)
						.setComments("Se ha enviado al lead " + target.getName() + " un mail para crear una empresa de trial.");
				
				CommercialDAO.save(ctx, commercialTracking);

				Domain parentDomain = getParentDomain(ctx, api);

				commercialTracking = new CommercialTracking()
						.setDomain(target.getDomain().getId())
						.setDate(new Date())
						.setSeller(projectCommercial.getSeller())
						.setProjectCommercial(projectCommercial.getId())
						.setActivity(commercialActivityAutoRegister.getId())
						.setStatus((byte) 0)
						.setComments("El lead " + target.getName() + " ha creado la empresa " + target.getName() + " (" + target.getDocument() + "-" + parentDomain.getName() + ").");
				
				CommercialDAO.save(ctx, commercialTracking);
				
				mkActionTarget.setProject(new Project().setId(projectCommercial.getId()));
			}
		}

		mkActionTarget.setActionTargetStatus((byte) 6); // Enviado
		
		MarketingCampaignDAO.saveActionTarget(ctx, mkActionTarget);
		
		return null == projectCommercial ? null : projectCommercial.getSeller();
	}

	private static Seller getNextLinealSellerByWorkgroup(CloseableAONContext ctx, int domain, int workgroup) throws AonCoreException {
		
		List<TaskHolder> taskHolders = TaskHolderDAO.getTaskHolderWorkgroup(ctx, f -> f.getDomainProperty().eq(domain), workgroup, 0, Integer.MAX_VALUE).collect(Collectors.toList());
		
		Integer[] taskHolderIds = new Integer[taskHolders.size()];
		taskHolders.stream().map(taskHolder -> taskHolder.getId()).collect(Collectors.toList()).toArray(taskHolderIds);
		
		LinkedList<Seller> sellerList = SellerDAO.getStream(ctx, 
				f -> f.getStatusProperty().eq((byte) 0)
					.and(f.getTaskHolderProperty().in(taskHolderIds)))
				.collect(Collectors.toCollection(LinkedList::new));
		
		Map<Seller, Date> sellerProjects = new HashMap<Seller, Date>();

		for (Seller seller : sellerList) {
			LinkedList<ProjectCommercial> projectCommercials = ProjectDAO.getProjectCommercialStream(ctx, 
					f -> f.getSellerProperty().eq(seller.getId()))
					.collect(Collectors.toCollection(LinkedList::new));
			
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

	private static Domain checkExistingDomain(CloseableAONContext ctx, AonApiData api, String document, String targetEmail) {
		Domain parentDomain = null == api.getDomain().getParentId() 
				? api.getDomain() 
				: getParentDomain(ctx, api);
		
		if (null != parentDomain && null != parentDomain.getId()) {
			
			Domain existDomain = DomainDAO.getDomain(ctx, 
					f -> f.getNameProperty().like("%" + document.toLowerCase() + "%").and(f.getParentProperty().eq(parentDomain.getId())));
			
			if (null != existDomain && null != existDomain.getId()) {
				String logoUrl = MarketingEmailTemplates.getLogoUrl(parentDomain, api.getUser());
				sendDomainExistsMail(ctx, api, existDomain, parentDomain, logoUrl, targetEmail);
				return existDomain;
			}
		}
		
		return null;
	}

	private static void sendDomainExistsMail(CloseableAONContext ctx, AonApiData api, Domain domain, Domain parentDomain, String logoUrl, String targetEmail) {
		String from = MarketingEmailTemplates.getFromMessage(ctx, api, parentDomain);
		String bcc = bookingEmail;

		SESMessage msg = new SESMessage()
				.setAlias(parentDomain.getDescription())
				.setFrom(from)
				.setReplyTo(from)
				.setTo(targetEmail)
				.setBcc(bcc)
				.setSubject(domain.getDescription() + " (DUPLICADO)")
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
		context.put("contact", AonStringUtils.isBlank(from) ? bookingEmail : from);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_duplicate.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private static Domain getParentDomain(AONContext ctx, AonApiData api) {
		return api.getDomain().isParent() 
				? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()))
				;
				
	}

	// ------------------------------------------------------------------------------------------------
	//											SCOPE
	// ------------------------------------------------------------------------------------------------
	
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
}
