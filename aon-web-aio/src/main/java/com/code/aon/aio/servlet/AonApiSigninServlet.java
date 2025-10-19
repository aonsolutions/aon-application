package com.code.aon.aio.servlet;
import java.security.Principal;

import org.apache.catalina.connector.Request;
import org.json.JSONObject;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.Utils;

@SuppressWarnings("serial")
@WebServlet(name = "SigninServlet", urlPatterns = {"/ms/api/signin/*"})
public class AonApiSigninServlet extends AonApiHttpServlet{
		
	@Override
	public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)  {
		Request request = LoginServlet.getRealRequest(httpRequest);
		if ( request != null ) {
			
			JSONObject json = Utils.getRequestJSON(httpRequest);
			String token = JsonUtils.getString(json, IJsonNames.TOKEN, "").trim();
			String username = JsonUtils.getString(json, IJsonNames.USERNAME, "").trim();
			String password = JsonUtils.getString(json, IJsonNames.PASSWORD, "").trim();
			
			Principal principal = request.getContext().getRealm().authenticate(username, password);

			if ( (principal == null) ) {
				error( httpRequest, httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "");
			} else {
				request.setUserPrincipal(principal);
				httpResponse.setHeader("p3p", "CP=\"NOI ADM DEV COM NAV OUR STP\"");
				AuthPrincipal authPrincipal = (AuthPrincipal) request.getUserPrincipal();
				token = AonToken.build(authPrincipal.getUuid(), null);
				success(httpRequest, httpResponse, token);
			}
			
		} else {
			error(httpRequest, httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "");			
		}	
	}

	
	public void success(HttpServletRequest req, HttpServletResponse resp, String token) {
		resp.setStatus(HttpServletResponse.SC_OK);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(IJsonNames.SESSION_ID, token);
    	resp.setContentType("application/json;charset=UTF-8");
    	response(req, resp, jsonObject);
	}

	public void error (HttpServletRequest req, HttpServletResponse resp, int sc, String message) {
		resp.setStatus(sc);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(IJsonNames.MESSAGE, message);
    	resp.setContentType("application/json;charset=UTF-8");
    	response(req, resp, jsonObject);
	}
}
