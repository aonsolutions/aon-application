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
import com.auth0.jwt.interfaces.DecodedJWT;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.security.User;

@SuppressWarnings("serial")
@WebServlet(name = "LoginServlet", urlPatterns = {"/login/*"})
public class LoginServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(LoginServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Login Servlet - GET METHOD");
		String token = req.getHeader("session_id");
		System.out.println(token);
		DecodedJWT jwt = JWT.decode(token);
		System.out.println(jwt.getSubject());
		Utils.giveBack(req, resp, jwt.getSubject(), new JSONObject());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Login Servlet - POST METHOD");
		JSONObject json = Utils.getRequestJSON(req);
		String username = json.getString("username");
		String password = json.getString("password");
	
	    JSONArray domainArray = new JSONArray();
	    JSONArray userArray = new JSONArray();
	    Boolean ok = false;
	    if(isEmail(username)) {
			LinkedList<User> users = AON_SOLUTIONS.getUsersByEmail(username);

			for (User user : users) {
				domainArray.put(user.getDomain());
				userArray.put(user.getId());
				try {
					String pass = createPasswordHash(user.getLogin(), password, "digestCallback");
					String expectedPass = AON_SOLUTIONS.getUserPassword(user.getId());
					ok = ok || pass.equals(expectedPass);
				} catch (LoginException e) {
					e.printStackTrace();
				}
			}
		}
	    if(domainArray.isEmpty()) {
	    	resp.sendError(401, "El Usuario No existe.");
	    } else {
	     
		String token = "";
		try {
		    Algorithm algorithm = Algorithm.HMAC256("aonsecret");

		    JSONObject object = new JSONObject();
		    object.put("domains", domainArray);
		    object.put("users", userArray);
		    object.put("username", username);

		    token = JWT.create()
	    		.withIssuer("auth0")
	    		.withSubject(object.toString())
	    		.withIssuedAt(new Date())
		        .sign(algorithm);
		} catch (JWTCreationException exception){

		}
		
		// TODO ERRROR RESPONSE!!! 
		JSONObject response = new JSONObject();
		response.put("session_id", token);
		response.put("domains", domainArray.length());
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
