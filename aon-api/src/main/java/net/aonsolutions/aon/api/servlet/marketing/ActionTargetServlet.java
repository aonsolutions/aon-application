package net.aonsolutions.aon.api.servlet.marketing;

import java.util.ArrayList;
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

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.UserJSON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingSellerDistribution;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
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
import com.esferalia.aon.watson.error.AonCoreException;
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
@WebServlet(name = "AonApiActionTargetServlet", urlPatterns = {"/ms/api/action-target/*"})
public class ActionTargetServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ActionTargetServlet.class.getName());
	
	public static final String ACTION_TAGET = "/";
	
	// Mail
	private static List<String> errors = new ArrayList<String>();
	private static String enterpriseNameMail;
	private static String urlMail;
	private static String userMail;
	private static String userLogingMail;
	private static String passwordMail;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		
		try {
			
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(ACTION_TAGET, ActionTargetServlet::saveActionTarget)
				.apply();
			
			response(req, resp, object);
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	public static JSONObject saveActionTarget(AonApiData api) {
		// ActionTarget
		ActionTarget actionTarget = new ActionTarget(api.getData());
		
		// Marketing Action 
		MarketingAction ma = AON.getMarketingAction(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), actionTarget.getMarketingAction().getId());
		actionTarget.setMarketingAction(ma);
		
		// Save Target

		Target target = new Target()
				.copy(
					new Registry()
						.setDomain(api.getDomain())
						.setName(actionTarget.getTarget().getName())
						.setDocumentType(DocumentType.values()[Integer.parseInt(actionTarget.getTarget().getDocumentType())])
						.setDocumentCountry(Country.safeValueOf(actionTarget.getTarget().getDocumentCountry()))
						.setDocument(actionTarget.getTarget().getDocument())
						.setNationality(Country.safeValueOf(actionTarget.getTarget().getDocumentCountry()))
						
				)
				.setScope(actionTarget.getMarketingAction().getMarketingCampaign().getScope())
				;
		
		target = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), target);
		
		// Save Registry Note (Observacion)
		
		if(AonStringUtils.isNotBlank(actionTarget.getTarget().getComments())) {
			RegistryNote note = new RegistryNote()
					.setDomain(target.getDomain().getId())
					.setRegistry(target.getId())
					.setDescription("Observaci\u00f3n")
					.setNoteDate(new Date())
					.setComments(actionTarget.getTarget().getComments())
					.setNoteType(NoteType.OBSERVATION)
					.setSecurityLevel(SecurityLevel.OFFICIAL)
					;
			
			AON.saveRegistryNote(api.getDomain(), api.getUser().getLogin(), note);
		}
		
		// Save Registry Address
		
		Integer raddressId = null;
		if(AonStringUtils.isNotBlank(actionTarget.getTarget().getAddress())) {
			Optional<GeoZone> geozoneOpt = AON.geozoneStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getCodeProperty().eq(actionTarget.getTarget().getGeozoneCode()))).findFirst();			
			
			RegistryAddress registryAddress = new RegistryAddress()
					.setDomain(target.getDomain().getId())
					.setRegistry(target.getId())
					.setMain(true)
					.setStreetType(StreetType.getForAeatCode(actionTarget.getTarget().getStreetType(), AonLanguage.SPANISH))
					.setAddress(actionTarget.getTarget().getAddress())
					.setNumber(actionTarget.getTarget().getNumber())
					.setZip(actionTarget.getTarget().getZip())
					.setCity(actionTarget.getTarget().getCity())
					.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null)
					.setGeozoneCode(actionTarget.getTarget().getGeozoneCode())
					.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null)
					;
			
			registryAddress = AON.save(api.getDomain(), api.getUser().getLogin(), registryAddress);
			raddressId = registryAddress.getId();
		}
		
		// Save Registry Media
		
		saveMedia(api, target.getDomain().getId(), target.getId(), MediaType.CELLULAR, actionTarget.getTarget().getPhone(), raddressId);
		saveMedia(api, target.getDomain().getId(), target.getId(), MediaType.EMAIL, actionTarget.getTarget().getEmail(), raddressId);
		
		// Save Marketing Action Target
		
		MarketingActionTarget mkActionTarget = new MarketingActionTarget()
				.copy(target)
				.setActionTargetDomain(target.getDomain().getId())
				.setMarketingAction(new MarketingAction().setId(actionTarget.getMarketingAction().getId()))
				.setActionTargetStatus((byte)0)
				.setComments(actionTarget.getTarget().getComments())
				;
		
		AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), mkActionTarget);
		
		// Create Operacion Comercial
		
		createCommercialOperation(api, actionTarget, target, mkActionTarget);
		
		// Create Enterprise
		try {
			createEnterprise(api, actionTarget, target);
		} catch (Exception e) {
			errors.add(e.getMessage());
		}
		
		// Send mail
		sendTrailEnterpriseMail();
		
		// Return data
		
		return api.getData();
	}
	
	private static void saveMedia(AonApiData api, Integer domain, Integer registry, MediaType mediaType, String value, Integer raddress) {
		if(AonStringUtils.isNotBlank(value)) {
			RegistryMedia registryMedia = new RegistryMedia()
					.setDomain(domain)
					.setRegistry(registry)
					.setMedia(mediaType)
					.setValue(value)
					.setCommercial(true)
					.setRaddress(raddress)
					;
			
			AON.save(api.getDomain(), api.getUser().getLogin(), registryMedia);
		}
	}
	
	private static void createCommercialOperation(AonApiData api, ActionTarget actionTarget, Target target, MarketingActionTarget mkActionTarget) {
		// Create Operacion Comercial
		
		if(actionTarget.getMarketingAction().getSellerDistribution() == MarketingSellerDistribution.MANUAL  && null != actionTarget.getMarketingAction().getSeller()) {
			ProjectCommercial projectCommercial = new ProjectCommercial()
					.copy(new Project()
						.setDomain(target.getDomain())
						.setRegistry(target.get())
						.setName(actionTarget.getMarketingAction().getDescription())
						.setDate(new Date())
						.setTas(false)
						.setCommercial(true)
						.setReservation(false)
						.setActive(true)
					)
					.setTarget(target.getId())
					.setSeller(actionTarget.getMarketingAction().getSeller())
					.setComments(actionTarget.getTarget().getComments())
					.setSource((byte)8) // Marketing
					.setStatus((byte)0)
					.setStatusDate(new Date())
					.setProbability(0);
			
			AON.saveProjectCommercial(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), projectCommercial);
			
			mkActionTarget.setActionTargetStatus((byte)6); // Enviado
			AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), mkActionTarget);
		} else if(actionTarget.getMarketingAction().getSellerDistribution() == MarketingSellerDistribution.AUTOMATIC && null != actionTarget.getMarketingAction().getWorkgroup().getId()) {
			Seller nextSeller = getNextLinealSellerByWorkgroup(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), actionTarget.getMarketingAction().getWorkgroup().getId());
			if(null != nextSeller) {
				ProjectCommercial projectCommercial = new ProjectCommercial()
						.copy(new Project()
							.setDomain(target.getDomain())
							.setRegistry(target.get())
							.setName(actionTarget.getMarketingAction().getDescription())
							.setDate(new Date())
							.setTas(false)
							.setCommercial(true)
							.setReservation(false)
							.setActive(true)
						)
						.setTarget(target.getId())
						.setSeller(nextSeller.getId())
						.setComments(actionTarget.getTarget().getComments())
						.setSource((byte)8) // Marketing
						.setStatus((byte)0)
						.setStatusDate(new Date())
						.setProbability(0);
				
				AON.saveProjectCommercial(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), projectCommercial);
				
				mkActionTarget.setActionTargetStatus((byte)6); // Enviado
				AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), mkActionTarget);
			}
		}
	}
	
	private static Seller getNextLinealSellerByWorkgroup(String domainName, int domain, String user, int workgroup) throws AonCoreException {
		List<TaskHolder> taskHolders = AON.getTaskHolderWorkgroupStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getDomainProperty().eq(domain), workgroup, 0, Integer.MAX_VALUE).filter(taskHolder -> taskHolder.isActive()).collect(Collectors.toList());
		Integer[] taskHolderIds = new Integer[taskHolders.size()];
		taskHolders.stream().map(taskHolder -> taskHolder.getId()).collect(Collectors.toList()).toArray(taskHolderIds);
		LinkedList<Seller> sellerList = AON.getSellerList(domainName, domain, user, f -> f.getStatusProperty().eq((byte)0).and(f.getTaskHolderProperty().in(taskHolderIds)));
		Map<Seller, Date> sellerProjects = new HashMap<Seller, Date>();
		
		for(Seller seller : sellerList) {
			LinkedList<ProjectCommercial> projectCommercials = AON.getProjectCommercialList(domainName, domain, user, f -> f.getSellerProperty().eq(seller.getId()));
			
			// Si esta activo y no tiene ninguna operacion comercial se devuelve este
			if(projectCommercials.isEmpty()) return seller;
			
			projectCommercials.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
			sellerProjects.put(seller, projectCommercials.get(0).getDate());
		}
		
		Optional<Date> oldestDate = sellerProjects.values().stream().sorted((d1, d2) -> d1.compareTo(d2)).findFirst();
		if(oldestDate.isEmpty()) return null;
		else {
			for(Entry<Seller, Date> entry : sellerProjects.entrySet()) {
				if(entry.getValue().equals(oldestDate.get())) return entry.getKey();
			}
			return null;
		}
	}
	
	private static void createEnterprise(AonApiData api, ActionTarget actionTarget, Target target) throws Exception {
		
		if(actionTarget.isTrial()) {
			System.out.println("----------------------- Create Enterprise -----------------------");
			
			enterpriseNameMail = actionTarget.getTarget().getName();
			
			Company company = new Company();
			company.setName(actionTarget.getTarget().getName());
			company.setDocument(actionTarget.getTarget().getDocument());
			company.setLegalPerson(AonDocumentUtil.isValidCIF(company.getDocument()));
			
			checkCompany(api, company);
			
			Domain parent = api.getDomain().isParent() ? 
					api.getDomain() : 
					AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f->f.getIdProperty().eq(api.getDomain().getParentId()));
					
			String domainName = company.getDocument() + "-" + parent.getName();
			Domain d = new Domain()
				.setName(domainName.toLowerCase())
				.setDescription(company.getName())
				.setOwner(api.getDomain().getOwner())
				.setParentId(parent.getId())
				.setActive(true)
				.setDomainType(DomainType.ENTERPRISE)
				.setEnableHeredity(true)
				.setDomainManagement(false);
			
			Domain domain = AON_SOLUTIONS.insertDomain(api.getDomain(), api.getUser(), d, company);
			Company c = AON.getCompany(domain.getName(), domain.getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(domain.getId()));
			
			urlMail = domain.getName();
			
			Integer comapnyRaddressId = null;
			if(AonStringUtils.isNotBlank(actionTarget.getTarget().getAddress())) {
				Optional<GeoZone> geozoneOpt = AON.geozoneStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getCodeProperty().eq(actionTarget.getTarget().getGeozoneCode()))).findFirst();			
				
				RegistryAddress registryAddress = new RegistryAddress()
						.setDomain(domain.getId())
						.setRegistry(c.getId())
						.setMain(true)
						.setStreetType(StreetType.getForAeatCode(actionTarget.getTarget().getStreetType(), AonLanguage.SPANISH))
						.setAddress(actionTarget.getTarget().getAddress())
						.setNumber(actionTarget.getTarget().getNumber())
						.setZip(actionTarget.getTarget().getZip())
						.setCity(actionTarget.getTarget().getCity())
						.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null)
						.setGeozoneCode(actionTarget.getTarget().getGeozoneCode())
						.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null)
						;
				
				registryAddress = AON.save(api.getDomain(), api.getUser().getLogin(), registryAddress);
				comapnyRaddressId = registryAddress.getId();
			}
			
			saveMedia(api, domain.getId(), c.getId(), MediaType.CELLULAR, actionTarget.getTarget().getPhone(), comapnyRaddressId);
			saveMedia(api, domain.getId(), c.getId(), MediaType.EMAIL, actionTarget.getTarget().getEmail(), comapnyRaddressId);
			
			RegistryAddress raddress = AON.getRegistryAddress(domain, new User(), f-> f.getDomainProperty().eq(domain.getId()).and(f.getTypeProperty().eq((byte)0)));
			
			if(raddress!=null && raddress.getId()!=null) {
				
				Workplace workplace = new Workplace()
					.setActive(true)
					.setDescription("PRINCIPAL")
					.setDomain(domain.getId())
					.setEnterprise(c.getId())
					.setAddress(raddress.getId());
				
				AON.saveWorkplace(domain, new User(), workplace);
			}
			
			// Create Registry Relationship
			RegistryRelationship rrelationship = new RegistryRelationship();
			rrelationship.setDomain(target.getDomain());
			rrelationship.setRegistry(target.getId());
			rrelationship.setRelatedRegistry(c.getId());
			rrelationship.setComments(c.getDomain().getName());
			
			AON_SOLUTIONS.saveRegistryRelationship( api.getDomain(), api.getUser(), rrelationship);
			
			ApplicationParameter trailParam = new ApplicationParameter()
					.setDomain(domain.getId())
					.setName(AppParam.TRIAL)
					.setValue("50")
					;
			
			AON.insertApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), trailParam);
			
			// Create Default User
			createDefaultUser(api, c, target, actionTarget);
			
			System.out.println("----------------------- Create Enterprise (END) -----------------------");
			
		}

	}
	
	private static void checkCompany(AonApiData api, Company company) throws AonApiException {
		if(AonStringUtils.isBlank(company.getDocument())) {
			throw new AonApiException("El documento de la empresa est?vac?.");
		}
		
		if(!AonDocumentUtil.isValid(company.getDocument())) {
			throw new AonApiException("El documento de la empresa no es v?ido");
		}
		
		if(AonStringUtils.isBlank(company.getName())) {
			throw new AonApiException("El nombre de la empresa est?vac?");
		}
	}
	
	private static void createDefaultUser(AonApiData api, Company company, Target target, ActionTarget actionTarget) throws Exception {
		Domain domain = company.getDomain();
		
		if(Utils.isEmail(actionTarget.getTarget().getEmail())) {
			
			String login = ramdonLogin();
			String pass = null;
			
			/*
			Auth  authx = new Auth().setDocument(target.getDocument());
			if(!AonStringUtils.isBlank(authx.getDocument())) {
				for (Auth r : AON_SOLUTIONS.getAuths(f -> f.getDocumentProperty().eq(authx.getDocument()))) {
					
					User user = AON.getDomainUserStream(domain.getName(), domain.getId(), "", 
						f -> f.getAuthProperty().eq(r.getAuth())).findFirst().orElse(new User());
					
					if(user != null && user.getId() != null)
						throw new Exception("El documento (" + target.getDocument() + ") introducido ya está asociado a otro usuario.");
				}
			}
			*/
			
			Auth auth = AON_SOLUTIONS.getAuth(actionTarget.getTarget().getEmail());
			if(auth.getUuid() == null)
				auth = createAuth(domain, target, login, pass, actionTarget.getTarget().getEmail(), actionTarget.getTarget().getPhone());
			
			if(auth.getAuth() != null) {
				User user = createUser(company, auth, login, target.getName());
				userMail = company.getDocument();
				userLogingMail = user.getLogin();
				passwordMail = user.getLogin();
				
				setUserAppRole(domain, user);
				
				if(domain.isChild() || domain.isStandalone()) {
					saveTaskHolder(domain, user);
				}
			}
			
		} else {
			throw new Exception("El email (" + actionTarget.getTarget().getEmail() + ") no tiene un formato correcto.");
		}
	}
	
	private static String ramdonLogin() {
		Random rnd = new Random();
		Integer i = rnd.nextInt(100000000-10000000+1)+10000000;
		return i.toString();	
	}
	
	private static Auth createAuth(Domain domain, Target target, String login, String pass, String email, String phone) {
		if(pass == null) {
			pass = Utils.createPasswordHash(email, login);
		}
		Auth auth = new Auth()
			.setEmail(email)
			.setPassword(pass)
			.setName(target.getName())
			.setSurname(null)
			.setDocument(target.getDocument())
			.setPhone(phone);
		
		auth = AON_SOLUTIONS.insertAuth(domain.getName(), domain.getId(), auth);
		
		return auth;
	}
	
	private static User createUser(Company company, Auth auth, String login, String name) {
		Domain domain = company.getDomain();
		
		User user = new User()
			.setAuth(auth)
			.setActive(true)
			.setDomain(domain.getId())
			.setLogin(company.getDocument())
			.setName(AonStringUtils.isNotBlank(name) ? name :  company.getDocument())
			.setShared(false)
			.setEnterprise(company.getId())
			.setToolbar(UserToolbar.GOOGLE);
		
		if(auth.getDocument() != null) {
			String document = auth.getDocument();
			
			if(!AonStringUtils.isBlank(document) && AonDocumentUtil.isValid(document)) {
				Integer registryId = null;
				Optional<Person> p = AON.getPerson(domain.getName(), domain.getId(), user.getLogin(), 
						f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(document)));
				
				if(p.isPresent() && p.get().getId() != null)
					registryId = p.get().getId();
				
				if(registryId == null) {
					Registry r = AON.getRegistry(domain.getName(), domain.getId(), "", 
							f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(document)));
					registryId = r.getId();
				}
				
				user.setRegistry(new Registry().setId(registryId));
			}
		}
		
		user = AON.save(domain.getName(), domain.getId(), "", user);
		
		AON.updateUserPassword(domain.getName(), domain.getId(), user.getLogin(), user.getId(), auth.getPassword());
		
		Scope s = getScope(company.getDomain(), user);
		
		if(s != null) {
			AON.insertUserScope(domain.getName(), domain.getId(), user.getLogin(), new UserScope()
					.setDomain(domain.getId())
					.setScope(s.getId())
					.setUserId(user.getId()));
		}
		if(domain.getScope() != null) {
			AON.insertUserScope(domain.getName(), domain.getId(), user.getLogin(), new UserScope()
					.setDomain(domain.getId())
					.setScope(domain.getScope())
					.setUserId(user.getId()));
		}
		
		ApplicationParameter a = AON.getApplicationParameter(domain.getName(), domain.getId(), user.getLogin(), AppParam.AON_PORTAL);
		ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(domain.getId())
				.setValue("288")
				.setName(AppParam.AON_PORTAL.getValue());

		if(a == null || a.getId() == null)
			AON.insertApplicationParameter(domain.getName(), domain.getId(), user.getLogin(), appParam);
	
		AON_SOLUTIONS.saveUserFinancePortal(domain, user.getLogin(), user.getId());		
		
		return user;
	}
	
	private static void setUserAppRole(Domain domain, User user){
		String login = user.getLogin();
		Integer userId = user.getId();

		LinkedList<AonRole> aRoles = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> f.getUserIdProperty().eq(userId))
				.map(r -> r.getRole()).collect(Collectors.toCollection(LinkedList::new));
		
		LinkedList<AonRole> tRoles = new LinkedList<AonRole>();
		tRoles.add(AonRole.ENTERPRISE);

		AonRole.stream().forEach(role -> {	
			if(aRoles.contains(role) && !tRoles.contains(role)) {
				AON_SOLUTIONS.deleteUserAppRole(domain.getName(), domain.getId(), login, f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getUserIdProperty().eq(userId))
					.and(f.getRoleProperty().eq(role.value())));
			}
			if(!aRoles.contains(role) && tRoles.contains(role)) {
				AON_SOLUTIONS.insertUserAppRole(domain.getName(), domain.getId(), "", new UserAppRole()
						.setApp(null)
						.setDomain(domain.getId())
						.setRole(role)
						.setUser(userId));
			}
		});
	}
	
	private static JSONObject saveTaskHolder(Domain domain, User user) {
		Integer userId = user.getId();
		
		TaskHolder th = AON.getTaskHolder(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getUserIdProperty().eq(userId)));
		
		if(th == null || th.getId() == null) {
			User u = AON.getUser(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(userId));
			Auth a = AON_SOLUTIONS.getAuth(domain.getName(), domain.getId(), u.getAuth().getAuth());

			Registry r = null;
			if(!AonStringUtils.isBlank(a.getDocument())) {
				r = AON.getRegistry(domain.getName(), domain.getId(), "", f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(a.getDocument())));
			}
			if(r == null || r.getId() == null) {
				r = AON.save(domain.getName(), domain.getId(), "", new Registry()
						.setDocument(a.getDocument())
						.setName(a.getName()+ " "+ a.getSurname())
						.setAlias(a.getName())
						.setDomain(domain));
			}
			Integer registryId = r.getId();
			th = AON.getTaskHolder(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq(registryId)));
			if(th != null && th.getId() != null) {
				th.setActive(true);
				if(th.getUserId() == null)  
					th.setUserId(userId);
			} else {
				th = new TaskHolder().copy(r)
					.setActive(true)
					.setUserId(userId);
			}
		} else if(!th.isActive()) {
			th.setActive(true);
		}
		
		th = AON.save(domain.getName(), domain.getId(), user.getLogin(), th);
		
		if(user != null && th != null && th.getId() != null) {
			user.setRegistry(new Registry().setId(th.getId()));
			user = AON.save(domain.getName(), domain.getId(), user.getLogin(), user);
		}
		
		return UserJSON.toJSON(user);
	}
	
	private static Scope getScope(Domain domain, User user) {
		
		Scope s = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getDescriptionProperty().eq("GENERAL")))
				.findFirst().orElse(null);
		
		if(s == null && domain.getParentId() != null) {
			s =   AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),
					f -> f.getDomainProperty().eq(domain.getParentId()).and(f.getDescriptionProperty().eq("GENERAL")))
					.findFirst().orElse(null);
		}
		
		if(s== null){
			s = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),
					f -> f.getDomainProperty().eq(domain.getId()))
					.findFirst().orElse(null);
		}
		
		if(s == null && domain.getParentId() != null) {
			s = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),
					f -> f.getDomainProperty().eq(domain.getParentId()))
					.findFirst().orElse(null);
		}
		
		return s;
	}
	
	private static void sendTrailEnterpriseMail() {
		String fromTo = "booking@aonsolutions.es";
		
		SESMessage msg = new SESMessage()
				.setAlias(enterpriseNameMail)
				.setReplyTo(fromTo)
				.setTo(fromTo)
				.setSubject(enterpriseNameMail + " (TRIAL)")
				.setBody(createBody());
		
		String emailSent = SES.sendEmail(msg);
		
		System.out.println("Email sent : " + emailSent);
	}
	
	private static String createBody() {
		String body = "";
		
		body += "<div style=\"background-color: #ffffff; padding: 20px; border-radius: 10px; max-width: 600px; margin: 0 auto; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">\n"
				+ "  <h2 style=\"text-align: center; color: #333;\">" + (errors.isEmpty() ? "Informaci\u00f3n de Creación de Empresa" : "Error Creaci\u00f3n de Empresa") + "</h2>\n"
				+ "\n"
				
				+ "  <p style=\"font-size: 16px; color: #333;\"><strong>Nombre de la Empresa:</strong> <span id=\"nombre-empresa\">" + enterpriseNameMail + "</span></p>\n"
				+ "\n"
				+ "  <p style=\"font-size: 16px; color: #333;\"><strong>URL de la Empresa:</strong> <a href=\"" + urlMail + "\" id=\"url-empresa\" style=\"color: #007bff;\">" + urlMail + "</a></p>\n"
				+ "\n"
				+ "  <p style=\"font-size: 16px; color: #333;\"><strong>Auth:</strong> <span id=\"usuario\">" + userMail + "</span></p>\n"
				+ "\n"
				+ "  <p style=\"font-size: 16px; color: #333;\"><strong>Usuario:</strong> <span id=\"usuario\">" + userLogingMail + "</span></p>\n"
				+ "\n"
				+ "  <p style=\"font-size: 16px; color: #333;\"><strong>Contraseña:</strong> <span id=\"contraseña\">" + passwordMail + "</span></p>\n"
				+ "\n"
				;
		
		if(!errors.isEmpty()) {
			body   += "  <p style=\"font-size: 16px; color: #333;\"><strong>Errores</strong></p>\n"
					;
			
			for(String error : errors) {
				body   += "  <div style=\"color: red; margin-bottom: 15px; font-size: 14px;\">\n"
						+ "   " + error + "\n"
						+ "  </div>\n"
						+ "\n"
						;
			}
		}
		
		body   += "  <p style=\"font-size: 12px; color: #666; text-align: center; margin-top: 20px;\">\n"
				+ "    Si tienes alguna duda, por favor contacta con soporte (booking@aonsolutions.es).\n"
				+ "  </p>\n"
				+ "</div>";
		
		return body;
	}
	
}
