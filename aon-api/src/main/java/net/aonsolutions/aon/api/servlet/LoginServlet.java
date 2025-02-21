package net.aonsolutions.aon.api.servlet;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
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
		String username = json.optString("username");
		String password = json.optString("password");
		password = password.trim();
		String login = "";
		if(username.contains("=")) {
			String[] strs = username.split("=");
			login = strs[0];
			username = strs[1];
		}
		boolean ok = false;
		Auth auth = new Auth();
	    if(Utils.isEmail(username)) {
	       	List<String> schemas = AONContext.getSchemas();
	    	if(!AonStringUtils.isBlank(login)) {
	    		for(String schema: schemas) {
	    			String domain = AONContext.getSchemaFirstDomain(schema);
	    			if(!ok && !AonStringUtils.isBlank(domain)) {
	    				User user = AON.getUser(domain, 0, login);
	    				if(user.getId() != null) {
	    					String pass = SECURITY.getUserPassword(domain, 0, login, user.getId());
	    					String userPass = Utils.createPasswordHash(login, password);
	    					ok = pass.equals(userPass);
	    				}
	    			}
	    		}
	    	}
	    	
	    	for(String schema: schemas) {
	    		String domain = AONContext.getSchemaFirstDomain(schema);
	    		
	    		if(auth.getUuid() == null && !AonStringUtils.isBlank(domain)) {
    				auth = AON_SOLUTIONS.getAuth(domain, 0, username);
    				auth.setSchema(schema);
	    	    	if(!ok && auth.getUuid() != null) {
	    	    		String pass = Utils.createPasswordHash(auth.getEmail(), password);
						ok = pass.equals(auth.getPassword());
	    	    	} 
	    	    }	    		
	    	}
	    	if(auth.getUuid() == null) {
	    		for(String schema : schemas) {
	    			String domain = AONContext.getSchemaFirstDomain(schema);
	    			if(!AonStringUtils.isBlank(domain)){
	    				LinkedList<User> users = AON_SOLUTIONS.getUsersByEmail(domain, 0, username);
	    				for (User user : users) {
    						String pass = Utils.createPasswordHash(user.getLogin(), password);
    						String expectedPass = AON_SOLUTIONS.getUserPassword(domain, 0, user.getId());
    						ok = ok || pass.equals(expectedPass);
    						if(auth.getUuid() == null && pass.equals(expectedPass)) {
    							String authPass = Utils.createPasswordHash(username, password);
    							auth = AON_SOLUTIONS.insertAuth(domain, 0, new Auth().setEmail(username).setPassword(authPass));
    						}
    						AON_SOLUTIONS.assignAuthToUser(domain, 0, user, auth.getUuid());
	    				}
	    			}
	    		}
	    	}
	    	
	    	if(!auth.isEmpty()) {
	    		token = AonToken.build(auth, null);
	    	}
		} else if ( AonStringUtils.isNotBlank(token)) {
			JSONObject jsonToken = SECURITY.decodeJWT(token, AonSecret.getAonSecret());
				
			AonToken aonToken= AonToken.parse(jsonToken);
			if ( AonStringUtils.isBlank(aonToken.getUuid()) ) {
				
				String tokenLogin = jsonToken.getString(IJsonNames.LOGIN);
				Integer tokenDomainId = jsonToken.getInt(IJsonNames.DOMAIN); // relax , really user's  domain id, See AonToken
				String tokenDomainName = jsonToken.getString(IJsonNames.SCHEMA_FIRST_DOMAIN);
				
				Domain tokenDomain = AON.getDomain(tokenDomainName, tokenDomainId, tokenLogin, f -> f.getIdProperty().eq(tokenDomainId));
				
				User user = AON.getUser(tokenDomain, tokenLogin, f -> f.getLoginProperty().eq(tokenLogin)
						.and(f.getDomainProperty().in(arrayOf(tokenDomainId /*, domain.getId(), domain.getParentId()*/))));
				if(!user.getAuth().isEmpty()) {
					auth = AON_SOLUTIONS.getAuth(user.getAuth().getAuth());
					token = AonToken.build(auth, null);
				} else {
					auth = newAuthForUser(user);
					token = AonToken.build(auth, null);
				} 
				ok = true;
			} else {
				String domainName = req.getServerName();
				Domain domain = AON_SOLUTIONS.getDomain(domainName);
				if ( domain != null && Objects.equals(domain.getName(), domainName )) {
					User user = AON_SOLUTIONS.getUser(domain, token);
					ok = user != null && Objects.equals(user.getAuth().getUuid(), aonToken.getUuid());
					token = ok ? token /*AonToken.build(user.getAuth().getUuid(), null)*/ : null;
				} else {
					ok = true;
				}
			}

		} else {
			String domainName = req.getServerName();
			if(AonStringUtils.isNotBlank(domainName) 
					&& !"aon.solutions".equals(domainName) 
					&& !"aonsolutions.org".equals(domainName) 
					&& !"localhost".contentEquals(domainName) 
				) {
				String aux = username;
				Domain domain = AON.getDomain(domainName, 0, aux, f -> f.getNameProperty().eq(domainName));
				
				User user = AON.getUser(domain, username, f -> f.getLoginProperty().eq(aux)
						.and(f.getDomainProperty().in(arrayOf(domain.getId(), domain.getParentId()))));
				
				if(user.getId() != null) {
					String pass = SECURITY.getUserPassword(domain.getName(), domain.getId(), user.getLogin(), user.getId());
					String userPass = Utils.createPasswordHash(login, password);
					ok = pass.equals(userPass);
				}
				
				if(!user.getAuth().isEmpty()) {
					auth = AON_SOLUTIONS.getAuth(user.getAuth().getAuth());
					token = AonToken.build(auth, null);
				} else {
					try {
						auth = newAuthForUser(user);
						token = AonToken.build(auth, null);
					} catch ( Exception e ) {
						token = AonToken.build(user, null, user.getDomain().getName());
					}
				} 
			}
		}
    	JSONObject object = new JSONObject();
	    if(auth.getUuid() == null && AonStringUtils.isBlank(token)) {
	    	resp.setStatus(401);
	    	object.put("message", "El Usuario No existe.");
	    	object.put("type", "error");
    	} else if(!ok) {
	    	resp.setStatus(401);
	    	object.put("message", "La Contraseña no coincide.");
	    	object.put("type", "error");
    	} else {
    		object.put("session_id", token);
	    }
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
