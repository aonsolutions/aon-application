package net.aonsolutions.aon.api.servlet.marketing;

import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import com.esferalia.aon.occam.api.json.UserJSON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.CommercialActivity;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingSellerDistribution;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.attachment.Attach;
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
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
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
	private static String enterpriseNameMail;
	private static String urlMail;
	private static String userMail;
	private static String passwordMail;
	private static boolean authExisted = false;

	private static boolean isLocal = false;

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
		checkExistingDomain(api, actionTarget.getTarget().getDocument(), actionTarget.getTarget().getEmail());

		// Get / Save Target
		Optional<Target> targetOpt = AON.getTarget(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), f -> f.getDocumentProperty().eq(actionTarget.getTarget().getDocument())
						.and(f.getDomainProperty().eq(api.getDomain().getId())));

		Target target = null;
		if (targetOpt.isEmpty()) {
			target = new Target()
					.copy(new Registry().setDomain(api.getDomain()).setName(actionTarget.getTarget().getName())
							.setDocumentType(
									DocumentType.values()[Integer.parseInt(actionTarget.getTarget().getDocumentType())])
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

		createCommercialOperation(api, actionTarget, target, mkActionTarget);

		// Return data

		JSONObject result = new JSONObject();
		result.put("type", "success");

		// Send mail

		boolean trial = JsonUtils.getboolean(api.getData().getJSONObject("actionTarget"), "trial");
		if (trial) {
			sendCreationEnterpriseMail(api, actionTarget, target.getId());
			result.put("message",
					"Se ha creado el lead correctamente y se ha enviado un mail para la creaci\u00f3n de la empresa.");
		} else
			result.put("message", "Se ha creado el lead correctamente.");

		// Return data

		return result;
	}

	private static void sendCreationEnterpriseMail(AonApiData api, ActionTarget actionTarget, Integer targetId) {
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
						+ api.getToken() + "&marketingAction=" + actionTarget.getMarketingAction().getId().toString();
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

		SESMessage msg = new SESMessage().setAlias(actionTarget.getTarget().getName()).setFrom(from).setReplyTo(from)
				.setTo(actionTarget.getTarget().getEmail()).setBcc(bcc)
				.setSubject(actionTarget.getTarget().getName() + " (TRIAL)").setBody(body);

		SES.sendEmail(msg);
	}

	// ---------------------------------------------------------------------------------------------
	// CREATE ENTERPRISE
	// ---------------------------------------------------------------------------------------------

	public static String createEnterprise(AonApiData api) {

		JSONObject data = api.getData();

		// Enterprise Data
		String name = JsonUtils.getString(data, "name");
		String document = JsonUtils.getString(data, "document");

		String streetType = JsonUtils.getString(data, "streetType");
		String address = JsonUtils.getString(data, "address");
		String number = JsonUtils.getString(data, "number");
		String zip = JsonUtils.getString(data, "zip");
		String geozoneCode = JsonUtils.getString(data, "geozoneCode");
		String city = JsonUtils.getString(data, "city");

		String phone = JsonUtils.getString(data, "phone");
		String email = JsonUtils.getString(data, "email");

		Integer target = JsonUtils.getInteger(data, "target");
		Integer marketingActionId = JsonUtils.getInteger(data, "marketingAction");

		Domain parent = api.getDomain().isParent() ? api.getDomain()
				: AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
						f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		// Create Enterprise

		System.out.println("----------------------- Create Enterprise -----------------------");

		Company company = new Company();
		company.setName(name);
		company.setDocument(document);
		company.setLegalPerson(AonDocumentUtil.isValidCIF(document));

		checkCompany(company);

		// Ceck Exist Domain
		checkExistingDomain(api, document, email);

		String domainNewName = company.getDocument() + "-" + parent.getName();
		Domain d = new Domain().setName(domainNewName.toLowerCase()).setDescription(company.getName())
				.setOwner(api.getDomain().getOwner()).setParentId(parent.getId()).setActive(true)
				.setDomainType(DomainType.ENTERPRISE).setEnableHeredity(true).setDomainManagement(false);

		Domain domain = new Domain();
		try {
			domain = AON_SOLUTIONS.insertDomain(api.getDomain(), api.getUser(), d, company);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Domain createdDomain = domain;
		Company c = AON.getCompany(createdDomain.getName(), createdDomain.getId(), api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(createdDomain.getId()));

		enterpriseNameMail = name;
		urlMail = domain.getName();

		Integer comapnyRaddressId = null;
		if (AonStringUtils.isNotBlank(address)) {
			Optional<GeoZone> geozoneOpt = AON
					.geozoneStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f
							.getDomainProperty().eq(api.getDomain().getId()).and(f.getCodeProperty().eq(geozoneCode)))
					.findFirst();

			RegistryAddress registryAddress = new RegistryAddress().setDomain(domain.getId()).setRegistry(c.getId())
					.setMain(true).setStreetType(StreetType.getForAeatCode(streetType, AonLanguage.SPANISH))
					.setAddress(address).setNumber(number).setZip(zip).setCity(city)
					.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null).setGeozoneCode(geozoneCode)
					.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null);

			registryAddress = AON.save(api.getDomain(), api.getUser().getLogin(), registryAddress);
			comapnyRaddressId = registryAddress.getId();
		}

		saveMedia(api, domain.getId(), c.getId(), MediaType.CELLULAR, phone, comapnyRaddressId);
		saveMedia(api, domain.getId(), c.getId(), MediaType.EMAIL, email, comapnyRaddressId);

		RegistryAddress raddress = AON.getRegistryAddress(domain, new User(),
				f -> f.getDomainProperty().eq(createdDomain.getId()).and(f.getTypeProperty().eq((byte) 1)));

		if (raddress != null && raddress.getId() != null) {

			Workplace workplace = new Workplace().setActive(true).setDescription("PRINCIPAL").setDomain(domain.getId())
					.setEnterprise(c.getId()).setAddress(raddress.getId());

			AON.saveWorkplace(domain, new User(), workplace);
		}

		Optional<Target> targetObj = AON.getTarget(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), f -> f.getIdProperty().eq(target));

		// Create Registry Relationship
		RegistryRelationship rrelationship = new RegistryRelationship();
		rrelationship.setDomain(targetObj.get().getDomain());
		rrelationship.setRegistry(target);
		rrelationship.setRelatedRegistry(c.getId());
		rrelationship.setComments(c.getDomain().getName());

		AON_SOLUTIONS.saveRegistryRelationship(api.getDomain(), api.getUser(), rrelationship);

		ApplicationParameter trailParam = new ApplicationParameter().setDomain(domain.getId()).setName(AppParam.TRIAL)
				.setValue("50");

		AON.insertApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				trailParam);

		// Create Default User
		boolean newAuth = true;
		try {
			newAuth = createDefaultUser(api, c, targetObj.get(), email, phone);
		} catch (Exception e) {
			e.printStackTrace();
		}
		authExisted = !newAuth;

		try (CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin())) {
			SecurityDAO.saveDomainMaxDefinedUser(ctx, domain.getId(), 1);
			DomainApp domainApp = new DomainApp().setDomain(domain.getId()).setApp(AonApp.INVOICE).setActive(true);
			SecurityDAO.saveDomainApp(ctx, domainApp);
		}

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

		// Cerrar antiguo autoregistro tracking
		LinkedList<CommercialTracking> verificationCommercialTrackingList = AON.getCommercialTrackingList(
				api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f -> f.getProjectCommercialProperty().eq(projectCommercial.getId())
						.and(f.getActivityProperty().eq(commercialActivityAutoRegisterId)));
		verificationCommercialTrackingList.forEach(verificationCommercialTracking -> {
			verificationCommercialTracking.setStatus((byte) 1);
			AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					verificationCommercialTracking);

		});

		projectCommercial.setStatus((byte) 3);
		AON.saveProjectCommercial(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				projectCommercial);

		Optional<MarketingActionTarget> mkActionTarget = mkAction.getTargets().stream()
				.filter(mkTarget -> mkTarget.getId().equals(target)).findFirst();
		if (mkActionTarget.isPresent())
			AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					mkActionTarget.get().setActionTargetStatus((byte) 5)); // Finalizado

		System.out.println("----------------------- Create Enterprise (END) -----------------------");

		// Send mail
		String logoUrl = getLogoUrl(parent, api.getUser());

		sendTrailEnterpriseCreatedMail(api, email, logoUrl, parent.getDescription());

		return getFinishCreationHtml(logoUrl, parent.getDescription());
	}

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

	private static void createCommercialOperation(AonApiData api, ActionTarget actionTarget, Target target,
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

		// Existe expediente: se cierra la verificacion antigua y se crean los nuevos
		// trackings
		if (null != mkActionTarget.getProjectCommercial()) {

			ProjectCommercial projectCommercial = AON.getProjectCommercial(api.getDomain().getName(),
					api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getProjectProperty().eq(mkActionTarget.getProjectCommercial()));

			Integer commercialActivityAutoRegisterId = commercialActivityAutoRegister.getId();

			// Cerrar antiguo autoregistro tracking
			LinkedList<CommercialTracking> verificationCommercialTrackingList = AON.getCommercialTrackingList(
					api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getProjectCommercialProperty().eq(projectCommercial.getId())
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
			ProjectCommercial projectCommercial = null;

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
			}
		}

		mkActionTarget.setActionTargetStatus((byte) 6); // Enviado
		AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				mkActionTarget);
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

	private static boolean createDefaultUser(AonApiData api, Company company, Target target, String email, String phone)
			throws Exception {
		Domain domain = company.getDomain();
		boolean newAuth = true;

		if (Utils.isEmail(email)) {

			String login = ramdonLogin();
			String pass = null;

			Auth auth = AON_SOLUTIONS.getAuth(email);
			if (auth.getUuid() == null)
				auth = createAuth(domain, target, login, pass, email, phone);
			else
				newAuth = false;

			if (auth.getAuth() != null) {
				User user = createUser(company, auth, login, target.getName());
				userMail = email;
				// Login
				// userLogingMail = user.getLogin();
				passwordMail = login;

				setUserAppRole(domain, user);

				if (domain.isChild() || domain.isStandalone()) {
					saveTaskHolder(domain, user);
				}
			}

		} else {
			throw new Exception("El email (" + email + ") no tiene un formato correcto.");
		}

		return newAuth;
	}

	private static String ramdonLogin() {
		Random rnd = new Random();
		Integer i = rnd.nextInt(100000000 - 10000000 + 1) + 10000000;
		return i.toString();
	}

	private static Auth createAuth(Domain domain, Target target, String login, String pass, String email,
			String phone) {
		if (pass == null) {
			pass = Utils.createPasswordHash(email, login);
		}
		Auth auth = new Auth().setEmail(email).setPassword(pass).setName(target.getName()).setSurname(null)
				.setDocument(target.getDocument()).setPhone(phone);

		auth = AON_SOLUTIONS.insertAuth(domain.getName(), domain.getId(), auth);

		return auth;
	}

	private static User createUser(Company company, Auth auth, String login, String name) {
		Domain domain = company.getDomain();

		User user = new User().setAuth(auth).setActive(true).setDomain(domain.getId()).setLogin(company.getDocument())
				.setName(AonStringUtils.isNotBlank(name) ? name : company.getDocument()).setShared(false)
				.setEnterprise(company.getId()).setToolbar(UserToolbar.GOOGLE);

		if (auth.getDocument() != null) {
			String document = auth.getDocument();

			if (!AonStringUtils.isBlank(document) && AonDocumentUtil.isValid(document)) {
				Integer registryId = null;
				Optional<Person> p = AON.getPerson(domain.getName(), domain.getId(), user.getLogin(),
						f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(document)));

				if (p.isPresent() && p.get().getId() != null)
					registryId = p.get().getId();

				if (registryId == null) {
					Registry r = AON.getRegistry(domain.getName(), domain.getId(), "",
							f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(document)));
					registryId = r.getId();
				}

				user.setRegistry(new Registry().setId(registryId));
			}
		}

		user = AON.saveUser(domain.getName(), domain.getId(), "", user);

		AON.updateUserPassword(domain.getName(), domain.getId(), user.getLogin(), user.getId(), auth.getPassword());

		Scope s = getScope(company.getDomain(), user);

		if (s != null) {
			AON.insertUserScope(domain.getName(), domain.getId(), user.getLogin(),
					new UserScope().setDomain(domain.getId()).setScope(s.getId()).setUserId(user.getId()));
		}
		if (domain.getScope() != null) {
			AON.insertUserScope(domain.getName(), domain.getId(), user.getLogin(),
					new UserScope().setDomain(domain.getId()).setScope(domain.getScope()).setUserId(user.getId()));
		}

		ApplicationParameter a = AON.getApplicationParameter(domain.getName(), domain.getId(), user.getLogin(),
				AppParam.AON_PORTAL);
		ApplicationParameter appParam = new ApplicationParameter().setDomain(domain.getId()).setValue("288")
				.setName(AppParam.AON_PORTAL.getValue());

		if (a == null || a.getId() == null)
			AON.insertApplicationParameter(domain.getName(), domain.getId(), user.getLogin(), appParam);

		// AON_SOLUTIONS.saveUserFinancePortal(domain, user.getLogin(), user.getId());

		return user;
	}

	private static void setUserAppRole(Domain domain, User user) {
		String login = user.getLogin();
		Integer userId = user.getId();

		LinkedList<AonRole> aRoles = AON_SOLUTIONS
				.getUserAppRole(domain.getName(), domain.getId(), "", f -> f.getUserIdProperty().eq(userId))
				.map(r -> r.getRole()).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<AonRole> tRoles = new LinkedList<AonRole>();
		tRoles.add(AonRole.ENTERPRISE);
		tRoles.add(AonRole.INVOICE_PORTAL);
		tRoles.add(AonRole.INVOICE);

		AonRole.stream().forEach(role -> {
			if (aRoles.contains(role) && !tRoles.contains(role)) {
				AON_SOLUTIONS.deleteUserAppRole(domain.getName(), domain.getId(), login,
						f -> f.getDomainProperty().eq(domain.getId()).and(f.getUserIdProperty().eq(userId))
								.and(f.getRoleProperty().eq(role.value())));
			}
			if (!aRoles.contains(role) && tRoles.contains(role)) {
				AON_SOLUTIONS.insertUserAppRole(domain.getName(), domain.getId(), "",
						new UserAppRole().setApp(null).setDomain(domain.getId()).setRole(role).setUser(userId));
			}
		});
	}

	private static JSONObject saveTaskHolder(Domain domain, User user) {
		Integer userId = user.getId();

		TaskHolder th = AON.getTaskHolder(domain.getName(), domain.getId(), "",
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getUserIdProperty().eq(userId)));

		if (th == null || th.getId() == null) {
			User u = AON.getUser(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(userId));
			Auth a = AON_SOLUTIONS.getAuth(domain.getName(), domain.getId(), u.getAuth().getAuth());

			Registry r = null;
			if (!AonStringUtils.isBlank(a.getDocument())) {
				r = AON.getRegistry(domain.getName(), domain.getId(), "",
						f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(a.getDocument())));
			}
			if (r == null || r.getId() == null) {
				r = AON.save(domain.getName(), domain.getId(), "", new Registry().setDocument(a.getDocument())
						.setName(a.getName() + " " + a.getSurname()).setAlias(a.getName()).setDomain(domain));
			}
			Integer registryId = r.getId();
			th = AON.getTaskHolder(domain.getName(), domain.getId(), "",
					f -> f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq(registryId)));
			if (th != null && th.getId() != null) {
				th.setActive(true);
				if (th.getUserId() == null)
					th.setUserId(userId);
			} else {
				th = new TaskHolder().copy(r).setActive(true).setUserId(userId);
			}
		} else if (!th.isActive()) {
			th.setActive(true);
		}

		th = AON.save(domain.getName(), domain.getId(), user.getLogin(), th);

		if (user != null && th != null && th.getId() != null) {
			user.setRegistry(new Registry().setId(th.getId()));
			user = AON.saveUser(domain.getName(), domain.getId(), user.getLogin(), user);
		}

		return UserJSON.toJSON(user);
	}

	private static Scope getScope(Domain domain, User user) {

		Scope s = AON
				.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),
						f -> f.getDomainProperty().eq(domain.getId()).and(f.getDescriptionProperty().eq("GENERAL")))
				.findFirst().orElse(null);

		if (s == null && domain.getParentId() != null) {
			s = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),
					f -> f.getDomainProperty().eq(domain.getParentId()).and(f.getDescriptionProperty().eq("GENERAL")))
					.findFirst().orElse(null);
		}

		if (s == null) {
			s = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),
					f -> f.getDomainProperty().eq(domain.getId())).findFirst().orElse(null);
		}

		if (s == null && domain.getParentId() != null) {
			s = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),
					f -> f.getDomainProperty().eq(domain.getParentId())).findFirst().orElse(null);
		}

		return s;
	}

	// ---------------------------------------------------------------------------------------------
	// AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------

	private static void checkExistingDomain(AonApiData api, String document, String targetEmail) {
		Domain parentDomain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));
		if (null != parentDomain && null != parentDomain.getId()) {
			Domain domain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getNameProperty().like("%" + document + "%")
							.and(f.getParentProperty().eq(parentDomain.getId())));
			if (null != domain && null != domain.getId()) {
				String logoUrl = getLogoUrl(parentDomain, api.getUser());
				sendDomainExistsMail(api, domain, parentDomain.getDescription(), logoUrl, targetEmail);
				throw new IllegalArgumentException("El dominio " + domain.getName() + " ya existe para este despacho");
			}
		}
	}

	private static void sendDomainExistsMail(AonApiData api, Domain domain, String parentDomainDescription,
			String logoUrl, String targetEmail) {
		String from = getFromMessage(api);
		String bcc = "booking@aonsolutions.es";

		SESMessage msg = new SESMessage().setAlias(domain.getDescription()).setFrom(from).setReplyTo(from)
				.setTo(targetEmail).setBcc(bcc).setSubject(domain.getDescription() + " (DUPLICADO)")
				.setBody(createEnterpriseDuplicateBody(domain, parentDomainDescription, logoUrl, from));

		SES.sendEmail(msg);
	}

	private static String createEnterpriseDuplicateBody(Domain domain, String parentDomainDescription, String logoUrl,
			String from) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomainDescription);
		context.put("name", domain.getDescription());
		context.put("url", (isLocal ? "http" : "https") + "://" + domain.getName() + (isLocal ? ":8080" : "")
				+ "/beta?theme=https://aonsolutions.github.io/aon-theme/css/infoautonomos.css");
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

	private static void sendTrailEnterpriseCreatedMail(AonApiData api, String targetEmail, String logoUrl, String parentDomainName) {
		String from = getFromMessage(api);
		String bcc = "booking@aonsolutions.es";

		SESMessage msg = new SESMessage().setAlias(enterpriseNameMail).setFrom(from).setReplyTo(from).setTo(targetEmail)
				.setBcc(bcc).setSubject(enterpriseNameMail + " (TRIAL)")
				.setBody(createEnterpriseCreatedBody(logoUrl, parentDomainName, from));

		SES.sendEmail(msg);
	}

	private static String createEnterpriseCreatedBody(String logoUrl, String parentDomainName, String from) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomainName);
		context.put("name", enterpriseNameMail);
		context.put("url", (isLocal ? "http" : "https") + "://" + urlMail + (isLocal ? ":8080" : "")
				+ "/beta?theme=https://aonsolutions.github.io/aon-theme/css/infoautonomos.css");
		context.put("domainName", urlMail);
		context.put("user", userMail);
		context.put("password", authExisted ? "La existente para este usuario" : passwordMail);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_created.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	private static String getFinishCreationHtml(String logoUrl, String parentDomainDesc) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomainDesc);
		context.put("name", enterpriseNameMail);
		context.put("url", (isLocal ? "http" : "https") + "://" + urlMail + (isLocal ? ":8080" : "")
				+ "/beta?theme=https://aonsolutions.github.io/aon-theme/css/infoautonomos.css");
		context.put("domainName", urlMail);
		context.put("mail", userMail);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_created_response.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
}
