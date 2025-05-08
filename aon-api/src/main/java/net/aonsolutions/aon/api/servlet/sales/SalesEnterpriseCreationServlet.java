package net.aonsolutions.aon.api.servlet.sales;

import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
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
import com.esferalia.aon.occam.api.model.Advertising;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.TargetStatus;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
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
@WebServlet(name = "SalesEnterpriseCreationServlet", urlPatterns = { "/ms/api/sales-creation-enterprise/*" })
public class SalesEnterpriseCreationServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(SalesEnterpriseCreationServlet.class.getName());

	public static final String CREATE_ENTERPRISE = "/";

	// Mail
	private static String enterpriseNameMail;
	private static String urlMail;
	private static String userMail;
	private static String passwordMail;
	private static boolean authExisted = false;

	private static boolean isLocal = false;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());

		try {

			AonApiData api = initialize(req, false);
			Object object = new AonRouting(api)
					.addRoute(CREATE_ENTERPRISE, SalesEnterpriseCreationServlet::createEnterprise)
					.apply();

			response(req, resp, object);

		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	// ---------------------------------------------------------------------------------------------
	// CREATE ENTERPRISE
	// ---------------------------------------------------------------------------------------------

	public static JSONObject createEnterprise(AonApiData api) {
		
		/*
		 * Allowed params 
		 * 
		 * name : registry name
		 * document : registry document
		 * 
		 * streetType
		 * address
		 * number
		 * zip
		 * geozoneCode
		 * city
		 * 
		 * phone
		 * email
		 * 
		 * registry : customer.id / target.id
		 * 
		 * marketingAction : marketing action id
		 * 
		 * sellerSupport : seller.id support type
		 * sellerCommercial : seller.id commercial type
		 * 
		 * */

		JSONObject data = api.getData();

		String email = JsonUtils.getString(data, "email");
			

		// Create Enterprise
		
		try (CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())) {
			ctx.transaction(t -> {
				
				Domain parent = api.getDomain().isParent() 
					? api.getDomain()
					: AON.getDomain(
							api.getDomain().getName(), 
							api.getDomain().getId(), 
							api.getUser().getLogin(),
							f -> f.getIdProperty().eq(api.getDomain().getParentId())
				);
				
				System.out.println("----------------------- [Start] Create Enterprise -----------------------");
				
				Domain newDomain = createDomain(api);
				
				System.out.println("----------------------- Create Auth / User");
				
				createDefaultUser(api, newDomain);
				
				System.out.println("----------------------- Domain Apps / Config");
				
				insertDomainConfiguration(ctx, newDomain);
				
				// Send mail
				String logoUrl = getLogoUrl(parent, api.getUser());

				sendTrailEnterpriseCreatedMail(api, email, logoUrl, parent);
				
				System.out.println("----------------------- [End] Create Enterprise -----------------------");
				
			});
		}
		
		JSONObject result = new JSONObject();
		result.put("message", "Pedido procesado correctamente. Empresa creada correctamente.");
		
		return result;
	}

	// ---------------------------------------------------------------------------------------------
	// AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------

	private static Domain createDomain(AonApiData api) throws Exception {
		JSONObject data = api.getData();
		
		String name = JsonUtils.getString(data, "name");
		String document = JsonUtils.getString(data, "document");

		Company company = new Company();
		company.setName(name);
		company.setDocument(document);
		company.setLegalPerson(AonDocumentUtil.isValidCIF(document));

		checkCompany(company);
		
		if(checkExistingDomain(api))
			throw new AonApiException("La empresa con identificador " + document + " para el cliente " + name + " ya existe");
			
		Domain parentDomain = null == api.getDomain().getParentId() 
				? api.getDomain() 
				: AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));
		
		if (null == parentDomain || null == parentDomain.getId())
			throw new AonApiException("No existe empresa padre desde la que colgar esta empresa");

		String domainNewName = company.getDocument() + "-" + (AonStringUtils.isBlank(parentDomain.getSubDomainSuffix()) ? parentDomain.getName() : parentDomain.getSubDomainSuffix());
		
		Domain newDomain = new Domain()
				.setName(domainNewName.toLowerCase())
				.setDescription(company.getName())
				.setOwner(api.getDomain().getOwner())
				.setParentId(parentDomain.getId())
				.setActive(true)
				.setDomainType(DomainType.ENTERPRISE)
				.setEnableHeredity(true)
				.setDomainManagement(false);

		newDomain = AON_SOLUTIONS.insertDomain(api.getDomain(), api.getUser(), newDomain, company);
		
		createCompanyMedia(api, newDomain);
		
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
	
	private static boolean checkExistingDomain(AonApiData api) {
		JSONObject data = api.getData();
		
		String document = JsonUtils.getString(data, "document");
		
		Domain parentDomain = null == api.getDomain().getParentId() 
				? api.getDomain() 
				: AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));
		
		if (null != parentDomain && null != parentDomain.getId()) {
			
			Domain domain = AON.getDomain(
					api.getDomain().getName(), 
					api.getDomain().getId(), 
					api.getUser().getLogin(),
					f -> f.getNameProperty().like("%" + document + "%")
							.and(f.getParentProperty().eq(parentDomain.getId()))
			);
			
			return null != domain && null != domain.getId();
			
		}
		
		return false;
	}
	
	private static void createCompanyMedia(AonApiData api, Domain newDomain) {
		JSONObject data = api.getData();
		
		String streetType = JsonUtils.getString(data, "streetType");
		String address = JsonUtils.getString(data, "address");
		String number = JsonUtils.getString(data, "number");
		String zip = JsonUtils.getString(data, "zip");
		String geozoneCode = JsonUtils.getString(data, "geozoneCode");
		String city = JsonUtils.getString(data, "city");
		
		String phone = JsonUtils.getString(data, "phone");
		String email = JsonUtils.getString(data, "email");
		
		Company newCompany = AON.getCompany(
				newDomain.getName(), 
				newDomain.getId(), 
				api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(newDomain.getId()));

		
		Integer comapnyRaddressId = null;
		
		if (AonStringUtils.isNotBlank(address)) {
			Optional<GeoZone> geozoneOpt = AON.geozoneStream(
					api.getDomain().getName(), 
					api.getDomain().getId(), 
					api.getUser().getLogin(), 
					f -> f.getDomainProperty().eq(api.getDomain().getId())
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

			registryAddress = AON.save(api.getDomain(), api.getUser().getLogin(), registryAddress);
			comapnyRaddressId = registryAddress.getId();
		}

		saveMedia(api, newDomain.getId(), newCompany.getId(), MediaType.CELLULAR, phone, comapnyRaddressId);
		saveMedia(api, newDomain.getId(), newCompany.getId(), MediaType.EMAIL, email, comapnyRaddressId);

		if (null != comapnyRaddressId)
			createWorkplace(newDomain, newCompany.getId(), comapnyRaddressId);
		
		createRRelationShip(api, newCompany);
		
	}

	private static void saveMedia(AonApiData api, Integer domain, Integer registry, MediaType mediaType, String value,
			Integer raddress) {
		if (AonStringUtils.isNotBlank(value)) {
			RegistryMedia registryMedia = new RegistryMedia().setDomain(domain).setRegistry(registry)
					.setMedia(mediaType).setValue(value).setCommercial(true).setRaddress(raddress);

			AON.save(api.getDomain(), api.getUser().getLogin(), registryMedia);
		}
	}
	
	private static void createWorkplace(Domain newDomain, Integer companyId, Integer raddressId) {
		Workplace workplace = new Workplace()
				.setActive(true)
				.setDescription("PRINCIPAL")
				.setDomain(newDomain.getId())
				.setEnterprise(companyId)
				.setAddress(raddressId);

		AON.saveWorkplace(newDomain, new User(), workplace);
	}
	
	private static void createRRelationShip(AonApiData api, Company newCompany) {
		JSONObject data = api.getData();

		Integer registry = JsonUtils.getInteger(data, "registry"); // target.id / customer.id
		
		Customer customer = AON.getCustomer(
			api.getDomain().getName(), 
			api.getDomain().getId(),
			api.getUser().getLogin(), 
			f -> f.getIdProperty().eq(registry)
		);
		
		if(null == customer)
			throw new AonApiException("No ha sido posible encontrar al cliente seleccionado");
		
		Optional<Target> targetOpt = AON.getTarget(
			api.getDomain().getName(), 
			api.getDomain().getId(),
			api.getUser().getLogin(), 
			f -> f.getIdProperty().eq(registry)
		);
		
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
			newTarget.setId(registry);
			
			AON.save(
				api.getDomain().getName(), 
				api.getDomain().getId(),
				api.getUser().getLogin(), 
				newTarget
			);
		}

		// Create Registry Relationship
		RegistryRelationship rrelationship = new RegistryRelationship();
		rrelationship.setDomain(customer.getDomain());
		rrelationship.setRegistry(customer.getId());
		rrelationship.setRelatedRegistry(newCompany.getId());
		rrelationship.setComments(newCompany.getDomain().getName());

		AON_SOLUTIONS.saveRegistryRelationship(api.getDomain(), api.getUser(), rrelationship);
	}
	
	private static void createDefaultUser(AonApiData api, Domain newDomain) {
		JSONObject data = api.getData();
		
		Integer registry = JsonUtils.getInteger(data, "registry"); // target.id / customer.id
		String email = JsonUtils.getString(data, "email");
		String phone = JsonUtils.getString(data, "phone");
		
		Optional<Target> targetOpt = AON.getTarget(
			api.getDomain().getName(), 
			api.getDomain().getId(),
			api.getUser().getLogin(), 
			f -> f.getIdProperty().eq(registry)
		);
		
		if (Utils.isEmail(email)) {
			
			String login = ramdonLogin();
			String pass = null;
			
			Auth auth = AON_SOLUTIONS.getAuth(email);
			if (auth.getUuid() == null) {
				if (pass == null) pass = Utils.createPasswordHash(email, login);
				
				auth = new Auth()
						.setEmail(email)
						.setPassword(pass)
						.setName(targetOpt.get().getName())
						.setSurname(null)
						.setDocument(targetOpt.get().getDocument())
						.setPhone(phone);

				auth = AON_SOLUTIONS.insertAuth(newDomain.getName(), newDomain.getId(), auth);
			}

			if (auth.getAuth() != null) {
				User user = createUser(api, newDomain, auth, login, targetOpt.get().getName());
				setUserAppRole(newDomain, user);

				if (newDomain.isChild() || newDomain.isStandalone()) {
					createTaskHolder(newDomain, auth, user);
				}
			}

		} else {
			throw new AonApiException("El email (" + email + ") no tiene un formato correcto.");
		}
		
	}
	
	private static String ramdonLogin() {
		Random rnd = new Random();
		Integer i = rnd.nextInt(100000000 - 10000000 + 1) + 10000000;
		return i.toString();
	}

	private static User createUser(AonApiData api, Domain newDomain, Auth auth, String login, String name) {
		Company newCompany = AON.getCompany(
			newDomain.getName(), 
			newDomain.getId(), 
			api.getUser().getLogin(),
			f -> f.getDomainProperty().eq(newDomain.getId())
		);

		User newUser = new User()
				.setAuth(auth)
				.setActive(true)
				.setDomain(newDomain.getId())
				.setLogin(newCompany.getDocument())
				.setName(AonStringUtils.isNotBlank(name) ? name : newCompany.getDocument())
				.setShared(false)
				.setEnterprise(newCompany.getId())
				.setToolbar(UserToolbar.GOOGLE);

		newUser = AON.saveUser(newDomain.getName(), newDomain.getId(), "", newUser);

		AON.updateUserPassword(newDomain.getName(), newDomain.getId(), newUser.getLogin(), newUser.getId(), auth.getPassword());

		Scope scope = getScope(newDomain, newUser);

		if (scope != null) {
			AON.insertUserScope(
					newDomain.getName(), 
					newDomain.getId(), 
					newUser.getLogin(),
					new UserScope()
						.setDomain(newDomain.getId())
						.setScope(scope.getId())
						.setUserId(newUser.getId())
			);
		}

		ApplicationParameter appParam = AON.getApplicationParameter(newDomain.getName(), newDomain.getId(), newUser.getLogin(), AppParam.AON_PORTAL);
		
		if (appParam == null || appParam.getId() == null) {
			
			appParam = new ApplicationParameter()
					.setDomain(newDomain.getId())
					.setValue("288")
					.setName(AppParam.AON_PORTAL.getValue());
			
			AON.insertApplicationParameter(newDomain.getName(), newDomain.getId(), newUser.getLogin(), appParam);
		}

		return newUser;
	}

	private static Scope getScope(Domain newDomain, User newUser) {

		Scope s = AON
				.getScopeStream(newDomain.getName(), newDomain.getId(), newUser.getLogin(),
						f -> f.getDomainProperty().eq(newDomain.getId()).and(f.getDescriptionProperty().eq("GENERAL")))
				.findFirst().orElse(null);

		if (s == null && newDomain.getParentId() != null) {
			s = AON.getScopeStream(newDomain.getName(), newDomain.getId(), newUser.getLogin(),
					f -> f.getDomainProperty().eq(newDomain.getParentId()).and(f.getDescriptionProperty().eq("GENERAL")))
					.findFirst().orElse(null);
		}

		if (s == null) {
			s = AON.getScopeStream(newDomain.getName(), newDomain.getId(), newUser.getLogin(),
					f -> f.getDomainProperty().eq(newDomain.getId())).findFirst().orElse(null);
		}

		if (s == null && newDomain.getParentId() != null) {
			s = AON.getScopeStream(newDomain.getName(), newDomain.getId(), newUser.getLogin(),
					f -> f.getDomainProperty().eq(newDomain.getParentId())).findFirst().orElse(null);
		}

		return s;
	}
	

	private static void setUserAppRole(Domain newDomain, User user) {
		String login = user.getLogin();
		Integer userId = user.getId();

		LinkedList<AonRole> aRoles = AON_SOLUTIONS
				.getUserAppRole(newDomain.getName(), newDomain.getId(), "", f -> f.getUserIdProperty().eq(userId))
				.map(r -> r.getRole()).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<AonRole> tRoles = new LinkedList<AonRole>();
		tRoles.add(AonRole.ENTERPRISE);
		tRoles.add(AonRole.INVOICE_PORTAL);
		tRoles.add(AonRole.INVOICE);

		AonRole.stream().forEach(role -> {
			if (aRoles.contains(role) && !tRoles.contains(role)) {
				AON_SOLUTIONS.deleteUserAppRole(
					newDomain.getName(), 
					newDomain.getId(), 
					login,
					f -> f.getDomainProperty().eq(newDomain.getId())
							.and(f.getUserIdProperty().eq(userId))
							.and(f.getRoleProperty().eq(role.value()))
				);
			}
			if (!aRoles.contains(role) && tRoles.contains(role)) {
				AON_SOLUTIONS.insertUserAppRole(
					newDomain.getName(), 
					newDomain.getId(), 
					"",
					new UserAppRole()
						.setApp(null)
						.setDomain(newDomain.getId())
						.setRole(role)
						.setUser(userId)
				);
			}
		});
	}
	
	private static void createTaskHolder(Domain newDomain, Auth auth, User newUser) {
		
		Registry newRegistry = AON.save(newDomain.getName(), newDomain.getId(), "", 
			new Registry()
			.setDocument(auth.getDocument())
			.setName(auth.getName() + (AonStringUtils.isBlank(auth.getSurname()) ? "" : (" " + auth.getSurname())) )
			.setAlias(auth.getName())
			.setDomain(newDomain)
		);
		
		newUser.setRegistry(newRegistry);
		newUser = AON.saveUser(newDomain.getName(), newDomain.getId(), newUser.getLogin(), newUser);
		
		TaskHolder taskHolder = new TaskHolder()
				.copy(newRegistry)
				.setActive(true)
				.setUserId(newUser.getId());
		
		taskHolder = AON.save(newDomain.getName(), newDomain.getId(), newUser.getLogin(), taskHolder);

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
	}

	// ---------------------------------------------------------------------------------------------
	// AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------

	
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
				+ "/beta";
		
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

	private static void sendTrailEnterpriseCreatedMail(AonApiData api, String targetEmail, String logoUrl, Domain parentDomain) {
		String from = getFromMessage(api);
		String bcc = "booking@aonsolutions.es";

		SESMessage msg = new SESMessage().setAlias(enterpriseNameMail).setFrom(from).setReplyTo(from).setTo(targetEmail)
				.setBcc(bcc).setSubject(enterpriseNameMail + " (TRIAL)")
				.setBody(createEnterpriseCreatedBody(logoUrl, parentDomain, from));

		SES.sendEmail(msg);
	}

	private static String createEnterpriseCreatedBody(String logoUrl, Domain parentDomain, String from) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		String url = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
				+ "/beta";
		
		if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "app.leevy.es"))
			url = "https://leevy.aon.solutions";
		else if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "infoautonomos.aonsolutions.net"))
			url = "https://infoautonomos.aon.solutions";

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", enterpriseNameMail);
		context.put("url", url);
		context.put("domainName", urlMail);
		context.put("user", userMail);
		context.put("password", authExisted ? "La existente para este usuario" : passwordMail);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_created.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	private static String getFinishCreationHtml(String logoUrl, Domain parentDomain) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		String url = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
		+ "/beta";
		
		if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "app.leevy.es"))
			url = "https://leevy.aon.solutions";
		else if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "infoautonomos.aonsolutions.net"))
			url = "https://infoautonomos.aon.solutions";

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", enterpriseNameMail);
		context.put("url", url);
		context.put("domainName", urlMail);
		context.put("mail", userMail);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/booking_trial_created_response.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
}
