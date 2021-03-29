package net.aonsolutions.aon.api.servlet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
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
	    Boolean ok = false;
		Auth auth = new Auth();
	    if(Utils.isEmail(username)) {
	    	List<String> schemas = AONContext.getSchemas();
	    	for(String schema: schemas) {
	    		String domain = AONContext.getSchemaFirstDomain(schema);
	    		
	    		if(auth.getUuid() == null && !AonStringUtils.isBlank(domain)) {
    				auth = AON_SOLUTIONS.getAuth(domain, 0, username);
    				auth.setSchema(schema);
	    	    	if(auth.getUuid() != null) {
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
		}
    	JSONObject object = new JSONObject();
	    if(auth.getUuid() == null) {
	    	resp.setStatus(401);
	    	object.put("message", "El Usuario No existe.");
	    	object.put("type", "error");
    	} else if(!ok) {
	    	resp.setStatus(401);
	    	object.put("message", "La Contraseña no coincide.");
	    	object.put("type", "error");
    	} else {
    		object.put("session_id", AonToken.build(auth, null));
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
