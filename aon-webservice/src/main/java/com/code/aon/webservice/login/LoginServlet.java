package com.code.aon.webservice.login;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.security.User;

@SuppressWarnings("serial")
@WebServlet(name = "LoginServlet", urlPatterns = {"/login/*"})
public class LoginServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(LoginServlet.class.getName());
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Login Servlet - POST METHOD");
		JSONObject json = Utils.getRequestJSON(req);
		String username = json.getString("username");
		String password = json.getString("password");

	    JSONArray tokenObject = new JSONArray();

	    Boolean ok = false;
	    Boolean empty = true;
	    if(isEmail(username)) {	
	    	for(String schema : AONContext.getSchemas()) {
	    		System.out.println(schema);
	    	    JSONObject object = new JSONObject();
	    	    JSONArray domainArray = new JSONArray();
	    	    JSONArray userArray = new JSONArray();
	    	    LinkedList<User> users = AON_SOLUTIONS.getUsersByEmail(schema, username);

	    	    for (User user : users) {
					domainArray.put(user.getDomain());
					userArray.put(user.getId());
					try {
						String pass = createPasswordHash(user.getLogin(), password, "digestCallback");
						String expectedPass = AON_SOLUTIONS.getUserPassword(schema, user.getId());
						ok = ok || pass.equals(expectedPass);
					} catch (LoginException e) {
						e.printStackTrace();
					}
				}
	    	    
	    	    object.put("schema", schema);
			    object.put("domains", domainArray);
			    object.put("users", userArray);
			    object.put("email", username);
			    tokenObject.put(object);
			    if(!domainArray.isEmpty()) {
			    	empty = false;
			    }
	    	}
		}
	    if(empty) {
	    	resp.sendError(401, "El Usuario No existe.");
	    } else {
	     
	    	String token = "";
	    	try {
	    		Algorithm algorithm = Algorithm.HMAC256("aonsecret");

	    		token = JWT.create()
	    				.withIssuer("auth0")
	    				.withSubject(tokenObject.toString())
	    				.withIssuedAt(new Date())
	    				.sign(algorithm);
	    	} catch (JWTCreationException exception){

	    	}	
		
	    	JSONObject response = new JSONObject();
	    	response.put("session_id", token);
	    	resp.setContentType("application/json;charset=UTF-8");
	    	Utils.addCorsHeader(resp);
	    	PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
	    	os.println(response.toString());
	    	os.flush();
	    	os.close();
	    }
	}
	
	protected String createPasswordHash(String username, String password, String digestOption) throws LoginException {
		String hashAlgorithm="SHA";
		String hashEncoding="BASE64";
	    String passwordHash = Util.createPasswordHash(hashAlgorithm, hashEncoding, null, username, password);
	    return passwordHash;
	}
	   
	private boolean isEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                "[a-zA-Z0-9_+&*-]+)*@" + 
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex); 
		if (email == null) 
			return false; 
		return pat.matcher(email).matches();
	}
}
