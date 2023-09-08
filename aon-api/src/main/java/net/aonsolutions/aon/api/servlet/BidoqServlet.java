package net.aonsolutions.aon.api.servlet;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "BidoqServlet", urlPatterns = {"/ms/api/bidoq/*"})
public class BidoqServlet extends AonApiHttpServlet {
	private static final Logger LOGGER  = Logger.getLogger(BidoqServlet.class.getName());

	private static final String BIDOQ_SESSION_ID = "AONd95770f269e711eb94390242ac130002";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			if(BIDOQ_SESSION_ID.equals(api.getToken()) || BIDOQ_SESSION_ID.equals(api.getData().optString(IJsonNames.SESSION_ID))) {
				switch (api.getPath()) {
				case "/":
					response(req, resp, bidoq(api));
					break;
				case "/app":
					response(req, resp, bidoqApp(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
				}
			} else {
				LOGGER.info("TOKEN RECIBIDO: " + api.getToken());
				throw new AonApiException("El token es incorrecto.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	private JSONObject bidoq(AonApiData api) {
		String user = api.getData().optString("user");
		String company = api.getData().optString("company");
		String action = api.getData().optString("action");

		if(AonStringUtils.isEmpty(user)) {
			throw new AonApiException("El campo user está vacío");
		}
		
		if(AonStringUtils.isEmpty(company)) {
			throw new AonApiException("El campo company está vacío");
		}
		
		Auth auth = AON_SOLUTIONS.getAuthByDocument(user);
		Company cp = new Company();
		String token = "";
		if(auth.getUuid() != null) {
			String domain = AONContext.getSchemaFirstDomain(auth.getSchema());
			token = AonToken.build(auth, AonDateUtils.addDays(new Date(), 1));
			cp = AON.getCompany(domain, 0, "", f -> f.getDocumentProperty().eq(company));
		}
   	
		if(cp.getId() == null) {
			List<String> schemas = AONContext.getSchemas();
			Integer index = 0;
			while(cp.getId() == null && index < schemas.size()) {
				String domainName = AONContext.getSchemaFirstDomain(schemas.get(index));
				if(!AonStringUtils.isBlank(domainName)) {
					cp = AON.getCompany(domainName, 0, "", f -> f.getDocumentProperty().eq(company));
			   	}
				index++;
			}
		}
		if(cp.getId() == null) {
			throw new AonApiException("La empresa no existe");
		}
		
		if(auth.getUuid() == null) {
			auth = createAuth(cp.getDomain(), null, user);
			token = AonToken.build(auth, AonDateUtils.addDays(new Date(), 1), cp.getDomain().getName());
		}
		
		byte[] a = auth.getAuth();
		Domain domain = cp.getDomain();
		User usr = AON.getUser(cp.getDomain().getName(), cp.getDomain().getId(), "", f ->
			f.getAuthProperty().eq(a)
			.and(f.getDomainProperty().eq(domain.getId())
				.or(f.getDomainProperty().eq(domain.getParentId()))));
		if(usr == null || usr.getId() == null) {
			createUser(cp, auth);
		}
		
		String url = "";
		if(AonStringUtils.isBlank(action)) {
			url = "https://" +  cp.getDomain().getName() +"/login?token=" + token;
		} else {
			url = "https://" +  cp.getDomain().getName() +"/login?initAction=" + action + "&token=" + token;
		}

		JSONObject json = new JSONObject();
		json.put("url", url);
		json.put("session_id", token);
		json.put("domain_id", cp.getDomain().getId());
		json.put("domain_name", cp.getDomain().getName());
		
		return json;
	}
	
	private JSONObject bidoqApp(AonApiData api) throws Exception {
		String user = api.getData().optString("user");
		String company = api.getData().optString("company");
		String action = api.getData().optString("action");
		String email = api.getData().optString("email");
		
		if(AonStringUtils.isEmpty(user)) {
			throw new Exception("El campo user está vacío");
		}
		
		if(AonStringUtils.isEmpty(company)) {
			throw new Exception("El campo company está vacío");
		}
		
		Auth auth = new Auth();
		if(Utils.isEmail(email)) {
			auth = AON_SOLUTIONS.getAuth(email);
		}
		
		if(auth.isEmpty()) {
			auth = AON_SOLUTIONS.getAuthByDocument(user);
		}
		
		Company cp = new Company();
		String token = "";
		if(auth.getUuid() != null) {
			String domain = AONContext.getSchemaFirstDomain(auth.getSchema());
			token = AonToken.build(auth, AonDateUtils.addDays(new Date(), 1));
			cp = AON.getCompany(domain, 0, "", f -> f.getDocumentProperty().eq(company));
		}
   	
		if(cp.getId() == null) {
			List<String> schemas = AONContext.getSchemas();
			Integer index = 0;
			while(cp.getId() == null && index < schemas.size()) {
				String domainName = AONContext.getSchemaFirstDomain(schemas.get(index));
				if(!AonStringUtils.isBlank(domainName)) {
					cp = AON.getCompany(domainName, 0, "", f -> f.getDocumentProperty().eq(company));
			   	}
				index++;
			}
		}
		
		if(cp.getId() == null) {
			throw new Exception("La empresa no existe");
		}
		
		JSONObject json = new JSONObject();
		if(auth.getUuid() == null && !Utils.isEmail(email)) {
			json.put("success", false);
			json.put("appMessage", "Hay una nueva aplicación disponible. Se requiere una cuenta de correo electrónico para acceder.");
			json.put("appBlocked", true);
			return json;
		} else if(auth.getUuid() == null) {
			auth = createAuth(cp.getDomain(), email, user);
			sendAuthCreateInfoMail(email, user, cp);
			token = AonToken.build(auth, AonDateUtils.addDays(new Date(), 1), cp.getDomain().getName());
		} 
		
		byte[] a = auth.getAuth();
		Domain domain = cp.getDomain();
		User usr = AON.getUser(cp.getDomain().getName(), cp.getDomain().getId(), "", f ->
			f.getAuthProperty().eq(a)
			.and(f.getDomainProperty().eq(domain.getId())
				.or(f.getDomainProperty().eq(domain.getParentId()))));
		if(usr == null || usr.getId() == null) {
			createUser(cp, auth);
		}
		
		String url = "";
		if(AonStringUtils.isBlank(action)) {
			url = "https://" +  cp.getDomain().getName() +"/login?token=" + token;
		} else {
			url = "https://" +  cp.getDomain().getName() +"/login?initAction=" + action + "&token=" + token;
		}

		json.put("url", url);
		json.put("session_id", token);
		json.put("domain_id", cp.getDomain().getId());
		json.put("domain_name", cp.getDomain().getName());
		json.put("success", true);
		json.put("appBlocked", true);
		json.put("appMessage", "Hay una nueva aplicación disponible. Su usuario de acceso es " + auth.getEmail());

		if(api.getData().opt("app") != null && api.getData().getString("app").equalsIgnoreCase("android")) {
			json.put("appStore", "itms-apps://itunes.apple.com/app/aon.solutions");
		} else if(api.getData().opt("app") != null && api.getData().getString("app").equalsIgnoreCase("ios")) {
			json.put("appStore", "market://details?id=aon.solutions");
		}
		
		return json;
	}
	
	private Auth createAuth(Domain domain, String email, String document) {
		email = AonStringUtils.isBlank(email) ? document + "@aon.solutions" : email;;
		String pass = Utils.createPasswordHash(email, document);

		Auth auth = new Auth()
			.setEmail(email)
			.setPassword(pass)
			.setName("")
			.setSurname("")
			.setDocument(document)
			.setPhone("");
		auth = AON_SOLUTIONS.insertAuth(domain.getName(), domain.getId(), auth);
		return auth;
	}
	
	private User createUser(Company cp, Auth auth) {
		User user = new User()
			.setAuth(auth)
			.setActive(true)
			.setDomain(cp.getDomain().getId())
			.setLogin(auth.getDocument())
			.setName(cp.getName())
			.setShared(false)
			.setEnterprise(cp.getId())
			.setToolbar(UserToolbar.GOOGLE);
		
		if(!AonStringUtils.isBlank(auth.getDocument()) && AonDocumentUtil.isValid(auth.getDocument())) {
			Registry registry = new Registry();
			Optional<Person> p = AON.getPerson(cp.getDomain().getName(), cp.getDomain().getId(), "", f -> 
				f.getDomainProperty().eq(cp.getDomain().getId())
				.and(f.getDocumentProperty().eq(auth.getDocument())));
			if(p.isPresent() && p.get().getId() != null) {
				registry = p.get();
			}
				
			if(registry == null || registry.isEmpty()) {
				registry = AON.getRegistry(cp.getDomain().getName(), cp.getDomain().getId(), "", f -> 
					f.getDomainProperty().eq(cp.getDomain().getId())
					.and(f.getDocumentProperty().eq(auth.getDocument())));
			}
			user.setRegistry(registry);
		}
			
		user = AON.save(cp.getDomain().getName(), cp.getDomain().getId(), "", user);
		AON.updateUserPassword(cp.getDomain().getName(), cp.getDomain().getId(), "", user.getId(), auth.getPassword());
		Scope s = getScope(cp.getDomain(), "");
		if(s != null) {
			AON.insertUserScope(cp.getDomain().getName(), cp.getDomain().getId(), "", new UserScope()
					.setDomain(cp.getDomain().getId())
					.setScope(s.getId())
					.setUserId(user.getId()));
		}
				
//		int value = 0;
//		value |= 1; 	//	PAYROLL_INFO_PORTAL
//		value |= 2; 	//	FISCAL_INFO_PORTAL
//		value |= 4; 	// 	DOCUMENTAL_INFO_PORTAL
//		value |= 8; 	// 	PAYROLL_PORTAL
//		value |= 16; 	//	ACCOUNTING_PORTAL
//		value |= 32; 	//	ACTIVE_PORTAL
//		value |= 64; 	//	INACTIVE_PORTAL
//		value |= 128;	//	DOCUMENTAL_MANAGEMENT_PORTAL
//		value |= 256; 	//	FINANCE_MANAGEMENT_PORTAL
		
		ApplicationParameter a = AON.getApplicationParameter(cp.getDomain().getName(), cp.getDomain().getId(), "", AppParam.AON_PORTAL);
		ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(cp.getDomain().getId())
				.setValue("288")
				.setName(AppParam.AON_PORTAL.getValue());

		if(a == null || a.getId() == null)
			AON.insertApplicationParameter(cp.getDomain().getName(), cp.getDomain().getId(), "", appParam);
	
		AON_SOLUTIONS.saveUserFinancePortal(cp.getDomain(), "", user.getId());		
		
		return user;
	}
	
	private Scope getScope(Domain domain, String login) {
		Scope s = AON.getScopeStream(domain.getName(), domain.getId(), login,
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getDescriptionProperty().eq("GENERAL")))
				.findFirst().orElse(null);
		if(s == null && domain.getParentId() != null) {
			s =   AON.getScopeStream(domain.getName(), domain.getId(), login,
					f -> f.getDomainProperty().eq(domain.getParentId()).and(f.getDescriptionProperty().eq("GENERAL")))
					.findFirst().orElse(null);
		}
		
		if(s== null){
			s = AON.getScopeStream(domain.getName(), domain.getId(), login,
					f -> f.getDomainProperty().eq(domain.getId()))
					.findFirst().orElse(null);
		}
		if(s == null && domain.getParentId() != null) {
			s = AON.getScopeStream(domain.getName(), domain.getId(), login,
					f -> f.getDomainProperty().eq(domain.getParentId()))
					.findFirst().orElse(null);
		}
		return s;
	}
	
	private void sendAuthCreateInfoMail(String email, String password, Company cp) {
		SESMessage msg = new SESMessage()
				.setTo(email)
				.setAlias(cp.getName())
				.setBody(authCreateInfoContent(email, password))
				.setSubject("NUEVO USUARIO | AON SOLUTIONS");
		SES.sendEmail(msg);
	}
	
	private String authCreateInfoContent(String email, String password) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
			
		VelocityContext context = new VelocityContext();
		context.put("email", email);
		context.put("password", password);
			
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/auth_create_info.vm");
			
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
}
