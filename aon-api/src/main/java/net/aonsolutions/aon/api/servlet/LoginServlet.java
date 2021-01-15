package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "LoginServlet", urlPatterns = {"/ms/api/login/*"})
public class LoginServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(LoginServlet.class.getName());
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Login Servlet - POST METHOD");
		JSONObject json = Utils.getRequestJSON(req);
		String username = json.getString("username");
		String password = json.getString("password");

		JSONObject tokenObject = new JSONObject();

	    Boolean ok = false;
		Auth auth = new Auth();
	    if(Utils.isEmail(username)) {
	    	List<String> schemas = AONContext.getSchemas();
	    	for(String schema: schemas) {
	    		String domain = AONContext.getSchemaFirstDomain(schema);
	    		
	    		if(auth.getUuid() == null && !AonStringUtils.isBlank(domain)) {
    				auth = AON_SOLUTIONS.getAuth(domain, 0, username);

	    	    	if(auth.getUuid() != null) {
	    	    		String pass = Utils.createPasswordHash(auth.getEmail(), password);
						ok = pass.equals(auth.getPassword());
	    	    		tokenObject
	    	    			.put("schema", schema)
	    	    			.put("schema_first_domain", domain)
	    	    			.put("uuid", auth.getUuid());
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
    							if(auth.getUuid() != null) {
    								tokenObject
    									.put("schema", schema)
    									.put("schema_first_domain", domain)
    									.put("uuid", auth.getUuid());
    							}
    						}
    						AON_SOLUTIONS.assignAuthToUser(domain, 0, user, auth.getUuid());
	    				}
	    			}
	    		}
	    	}
		}
    	JSONObject response = new JSONObject();
	    if(auth.getUuid() == null) {
	    	resp.setStatus(401);
	    	response.put("message", "El Usuario No existe.");
	    	response.put("type", "error");
    	} else if(!ok) {
	    	resp.setStatus(401);
	    	response.put("message", "La Contraseña no coincide.");
	    	response.put("type", "error");
    	} else {
	    	String token = "";
	    	try {
	    		Algorithm algorithm = Algorithm.HMAC256("aonsecret");

	    		token = JWT.create()
	    				.withIssuer("auth0")
	    				.withSubject(tokenObject.toString())
	    				.withIssuedAt(new Date())
	    				//.withExpiresAt(AonDateUtils.addDays(new Date(), 1))
	    				.sign(algorithm);
	    	} catch (JWTCreationException exception){

	    	}	
	    	response.put("session_id", token);
	    }
    	resp.setContentType("application/json;charset=UTF-8");
	    Utils.addCorsHeader(resp);
    	PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
    	os.println(response.toString());
    	os.flush();
    	os.close();
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
