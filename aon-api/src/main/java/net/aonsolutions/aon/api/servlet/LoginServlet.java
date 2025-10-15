package net.aonsolutions.aon.api.servlet;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "LoginServlet", urlPatterns = {"/ms/api/login/*"})
public class LoginServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(LoginServlet.class.getName());
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("Login Servlet - POST METHOD");
		JSONObject json = Utils.getRequestJSON(req);
		String token = json.optString("token");
		String username = JsonUtils.getString(json, IJsonNames.USERNAME, "").trim();
		String password = json.optString("password");
		password = password.trim();
		String login = "";
		if(username.contains("=")) {
			String[] strs = username.split("=");
			login = strs[0];
			username = strs[1];
		}
		boolean passSuccess = false;

		Auth auth = new Auth();
		String domainName = req.getServerName();
		
		Domain domain = AON_SOLUTIONS.getDomain(domainName);
		
		if(Utils.isEmail(username)) {
	       	List<String> schemas = AONContext.getSchemas(domainName);
	    	if(!AonStringUtils.isBlank(login)) {
	    		for(String schema: schemas) {
	    			String firstDomainName = AONContext.getSchemaFirstDomain(schema);
	    			if(!passSuccess && !AonStringUtils.isBlank(firstDomainName)) {
	    				User user = AON.getUser(firstDomainName, 0, login);
	    				if(user.getId() != null) {
	    					String pass = SECURITY.getUserPassword(firstDomainName, 0, login, user.getId());
	    					String userPass = Utils.createPasswordHash(login, password);
	    					passSuccess = pass.equals(userPass);
	    				}
	    			}
	    		}
	    	}
	    	
	    	for(String schema: schemas) {
	    		String firstDomainName = AONContext.getSchemaFirstDomain(schema);
	    		
	    		if(auth.getUuid() == null && !AonStringUtils.isBlank(firstDomainName)) {
    				auth = AON_SOLUTIONS.getAuth(firstDomainName, 0, username);
    				auth.setSchema(schema);
	    	    	if(!passSuccess && auth.getUuid() != null) {
	    	    		String pass = Utils.createPasswordHash(auth.getEmail(), password);
						passSuccess = pass.equals(auth.getPassword());
	    	    	} 
	    	    }	    		
	    	}
	    	if(auth.getUuid() == null) {
	    		for(String schema : schemas) {
	    			String firstDomain = AONContext.getSchemaFirstDomain(schema);
	    			if(!AonStringUtils.isBlank(firstDomain)){
	    				LinkedList<User> users = AON_SOLUTIONS.getUsersByEmail(firstDomain, 0, username);
	    				for (User user : users) {
    						String pass = Utils.createPasswordHash(user.getLogin(), password);
    						String expectedPass = AON_SOLUTIONS.getUserPassword(firstDomain, 0, user.getId());
    						passSuccess = passSuccess || pass.equals(expectedPass);
    						if(auth.getUuid() == null && pass.equals(expectedPass)) {
    							String authPass = Utils.createPasswordHash(username, password);
    							auth = AON_SOLUTIONS.insertAuth(firstDomain, 0, new Auth().setEmail(username).setPassword(authPass));
    						}
    						AON_SOLUTIONS.assignAuthToUser(firstDomain, 0, user, auth.getUuid());
	    				}
	    			}
	    		}
	    	}
	    	
	    	if(!auth.isEmpty()) {
	    		token = AonToken.build(auth.getUuid(), null);
	    	}
		} else if ( AonStringUtils.isNotBlank(token)) {
			JSONObject jsonToken = null;
			try {
				jsonToken = SECURITY.decodeJWT(token, AonSecret.getAonSecret());
			} catch (Exception e) {
				error(req, resp, e.getMessage());
				return;
			}
				
			AonToken aonToken= AonToken.parse(jsonToken);
			if(aonToken.isExpired()) { 
				error(req, resp, "Token expirado");
				return;
			}
			if ( AonStringUtils.isBlank(aonToken.getUuid()) ) {
				String tokenLogin = jsonToken.getString(IJsonNames.LOGIN);
				Integer tokenDomainId = jsonToken.getInt(IJsonNames.DOMAIN); // relax , really user's  domain id, See AonToken
				String tokenDomainName = jsonToken.getString(IJsonNames.SCHEMA_FIRST_DOMAIN);
				
				domain = AON.getDomain(tokenDomainName, tokenDomainId, tokenLogin, f -> f.getIdProperty().eq(tokenDomainId));
				
				User user = AON.getUser(domain, tokenLogin, f -> f.getLoginProperty().eq(tokenLogin)
						.and(f.getDomainProperty().in(arrayOf(tokenDomainId /*, domain.getId(), domain.getParentId()*/))));
				if(!user.getAuth().isEmpty()) {
					auth = AON_SOLUTIONS.getAuth(user.getAuth().getAuth());
					token = AonToken.build(auth.getUuid(), null);
				} else {
					auth = newAuthForUser(user);
					token = AonToken.build(auth.getUuid(), null);
				} 
				passSuccess = true;
			} else {
				//Domain domain = AON_SOLUTIONS.getDomain(domainName);
				
				if ( domain != null && Objects.equals(domain.getName(), domainName )) {
					User user = AON_SOLUTIONS.getUser(domain, token);
					passSuccess = user != null && AonStringUtils.equalsIgnoreCase(user.getAuth().getUuid(), aonToken.getUuid());
					token = passSuccess ? token /*AonToken.build(user.getAuth().getUuid(), null)*/ : null;
				} else {
					passSuccess = true;
				}
			}

		} else {
			if(AonStringUtils.isNotBlank(domainName) 
					&& !"aon.solutions".equals(domainName) 
					&& !"aonsolutions.org".equals(domainName) 
					&& !"localhost".contentEquals(domainName) 
				) {
				
				String aux = username;
				
				//Domain domain = AON.getDomain(domainName, 0, username, f -> f.getNameProperty().eq(domainName));
				Integer domainId = domain.getId();
				Integer domainParentId = domain.getParentId();
				User user = AON.getUser(domain, username, f -> f.getLoginProperty().eq(aux)
						.and(f.getDomainProperty().in(arrayOf(domainId, domainParentId))));
				
				if(user.getId() != null) {
					String pass = "";
					if ( AonStringUtils.isNotBlank(login)) {
						boolean isSuppportEnabled = AON
								.getApplicationParameterStream(domain.getName(), domain.getId(), user.getLogin(),
										f -> f.getNameProperty().eq(AppParam.AON_SUPPORT_ENABLED.toString()))
								.findAny().isPresent();
						if ( isSuppportEnabled ) {
							User loginUser = AON.getUser(domain.getName(), 0, login);
							pass = SECURITY.getUserPassword(domain.getName(), 0, login, loginUser.getId());
						}
					} else {
						pass = SECURITY.getUserPassword(domain.getName(), domain.getId(), user.getLogin(), user.getId());
					}
					String userPass = Utils.createPasswordHash(login, password);
					passSuccess = pass.equals(userPass);
				}
				
				if(!user.getAuth().isEmpty()) {
					auth = AON_SOLUTIONS.getAuth(user.getAuth().getAuth());
					token = AonToken.build(auth.getUuid(), null);
				} else {
					try {
						auth = newAuthForUser(user);
						token = AonToken.build(auth.getUuid(), null);
					} catch ( Exception e ) {
						token = AonToken.build(user, null, user.getDomain().getName());
					}
				} 
			}
		}
    	
	    JSONObject object = new JSONObject();
	    if( domain != null && !domain.isActuallyActive() ) {
	    	resp.setStatus(401);
			object.put("type", "error");
			object.put("message",
					String.format("El dominio %s se encuentra actualmente inactivo.", domain.getDescription()));
	    } else if( domain != null && domain.isExpired() ) {
			resp.setStatus(401);
			object.put("type", "error");
			object.put("message", String.format(
					"El periodo de contratación del dominio %s ha expirado. Contacte con soporte o su comercial asignado para más información.",
					domain.getDescription()));
	    } else if(auth.getUuid() == null && AonStringUtils.isBlank(token)) {
	    	resp.setStatus(401);
	    	object.put("message", "El Usuario No existe.");
	    	object.put("type", "error");
    	} else if(!passSuccess) {
	    	resp.setStatus(401);
	    	object.put("message", "La Contraseña no coincide.");
	    	object.put("type", "error");
    	} else {
    		object.put("session_id", token);
	    }
    	resp.setContentType("application/json;charset=UTF-8");

    	response(req, resp, object);
	}
	
	private void error (HttpServletRequest req, HttpServletResponse resp, String message) {
		resp.setStatus(401);
		JSONObject object = new JSONObject();
		object.put("message", message);
    	resp.setContentType("application/json;charset=UTF-8");
    	response(req, resp, object);
	}

	
	public static Auth getAuth(String email) {
		Auth auth = new Auth();
		if(Utils.isEmail(email)) {
		 	List<String> schemas = AONContext.getSchemas();
		   	for(String schema: schemas) {
		   		String domain = AONContext.getSchemaFirstDomain(schema);
		   		if(auth.getUuid() == null && !AonStringUtils.isBlank(domain)) {
	   				auth = AON_SOLUTIONS.getAuth(domain, 0, email);
		   		}	    		
		   	}
		}
		return auth;
	}
	
	private static Auth newAuthForUser(User user) {
		Auth auth;
		String userPassword = SECURITY.getUserPassword(user.getDomain().getName(), user.getDomain().getId(), user.getLogin(), user.getId());
		auth = new Auth()
				.setName(user.getName())
				.setPassword(userPassword)
				.setEmail(String.format("%s@%s", user.getLogin(), user.getDomain().getName()));
		Registry userRegistry = user.getRegistry();
		if ( userRegistry != null ) {
			auth.setDocument(userRegistry.getDocument());
		}
		auth = AON_SOLUTIONS.insertAuth( user.getDomain().getName(), user.getDomain().getId(), auth );
		AON_SOLUTIONS.assignAuthToUser(user.getDomain().getName(), user.getDomain().getId(), user, auth.getAuth());
		return auth;
	}
	
	private static Integer[] arrayOf( Integer ...ts ) {
		return Arrays.stream(ts).filter(Objects::nonNull).toArray(Integer[]::new);
	}
}
