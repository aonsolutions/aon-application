package net.aonsolutions.aon.api.servlet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "LoginServlet", urlPatterns = {"/ms/api/login/*"})
public class LoginServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(LoginServlet.class.getName());
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("Login Servlet - POST METHOD");
		JSONObject json = Utils.getRequestJSON(req);
		String username = json.optString("username");
		String password = json.optString("password");
		String login = "";
		if(username.contains("=")) {
			String[] strs = username.split("=");
			login = strs[0];
			username = strs[1];
		}
		Boolean ok = false;
		Auth auth = new Auth();
		String token = null;
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
	    	
// TODO AUTH with dynamodb
//	    	if(auth.isEmpty()) {
//		    	auth = AuthDyn.getAuth(username);
//	    		String pass = Utils.createPasswordHash(auth.getEmail(), password);
//				ok = pass.equals(auth.getPassword());
//	    	}
	    	
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
	    	
	    	if(!auth.isEmpty()) token = AonToken.build(auth, null);
		} else {
			String domainName = req.getServerName();
			if(AonStringUtils.isNotBlank(domainName) && !"aonsolutions.org".equals(domainName) 
					&& !"aon.solutions".equals(domainName) && !"localhost".contentEquals(domainName) ) {
				String aux = username;
				Domain domain = AON.getDomain(domainName, 0, aux, f -> f.getNameProperty().eq(domainName));
				User user = AON.getUser(domain, aux, f -> f.getLoginProperty().eq(aux));
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
						String userPassword = SECURITY.getUserPassword(domain.getName(), domain.getId(), user.getLogin(), user.getId());
						auth = new Auth()
								.setName(user.getName())
								.setPassword(userPassword)
								.setEmail(String.format("%s@%s", user.getLogin(), domain.getName()));
						Registry userRegistry = user.getRegistry();
						if ( userRegistry != null ) {
							auth.setDocument(userRegistry.getDocument());
						}
						auth = AON_SOLUTIONS.insertAuth( domain.getName(), domain.getId(), auth );
						AON_SOLUTIONS.assignAuthToUser(domain.getName(), domain.getId(), user, auth.getAuth());
						token = AonToken.build(auth, null);
					} catch ( Exception e ) {
						token = AonToken.build(user, null, domain.getName());
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
		

}
