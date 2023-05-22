package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "EmailVerifyServlet", urlPatterns = {"/ms/api/verify/*"})
public class EmailVerifyServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(EmailVerifyServlet.class.getName());
	private static final Integer ZERO = 0;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Email Verify Servlet - POST METHOD");


		String[] pathInfo = req.getPathInfo().split("/");
		
		HashMap<String, String> parameters = getParameters(pathInfo[1]);
		String user = parameters.get("user");
		String email = parameters.get("email");
		String domainName = parameters.get("domain");

		Auth auth = new Auth();
    	List<String> schemas = AONContext.getSchemas();
    	for(String schema: schemas) {
    		String domain = AONContext.getSchemaFirstDomain(schema);
    		if(auth.getUuid() == null && !AonStringUtils.isBlank(domain)) {
   				auth = AON_SOLUTIONS.getAuth(domain, 0, email);	    		
	    	}
    	}
    	
    	User u = AON.getUser(ZERO, domainName, "", Integer.parseInt(user));
    	if(auth.getUuid() == null) {
			String expectedPass = AON_SOLUTIONS.getUserPassword(domainName, ZERO, u.getId());
			auth = AON_SOLUTIONS.insertAuth(domainName, 0, new Auth().setEmail(email).setPassword(expectedPass));
    	}
    	if(!u.hasAuth()) {
			AON_SOLUTIONS.assignAuthToUser(domainName, ZERO, u, auth.getUuid());
    	}
	}
	
	public HashMap<String, String> getParameters(String value){
		HashMap<String, String> map = new HashMap<String, String>();
		String[] parameters = decode(value.getBytes()).split("&");
		for(String parameter : parameters){
			String[] values = parameter.split("=");
			map.put(values[0], values[1]);
		}
		return map;
	}
	
	public String decode(byte[] value){
		String decode = "";
		try{
			decode = new String(Base64.getDecoder().decode(value), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decode;
	}
}
