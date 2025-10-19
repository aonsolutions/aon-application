package com.code.aon.aio.servlet;
import java.security.Principal;
import java.util.ResourceBundle;

import org.apache.catalina.connector.Request;
import org.json.JSONObject;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.controller.FailedLogin;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;

import jakarta.servlet.ServletRequest;
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
		
		ResourceBundle commonBundle = ResourceBundle.getBundle(ICommonMessages.BUNDLE_RESOURCE, httpRequest.getLocale());

		Request request = LoginServlet.getRealRequest(httpRequest);
		if ( request != null ) {
			
			JSONObject json = Utils.getRequestJSON(httpRequest);
			String token = JsonUtils.getString(json, IJsonNames.TOKEN, "").trim();
			String username = JsonUtils.getString(json, IJsonNames.USERNAME, "").trim();
			String password = JsonUtils.getString(json, IJsonNames.PASSWORD, "").trim();
			
			Principal principal = request.getContext().getRealm().authenticate(username, password);

			if ( (principal == null) ) {
				error( httpRequest, httpResponse, HttpServletResponse.SC_UNAUTHORIZED, getMessage(request));
				
			} else {
				request.setUserPrincipal(principal);
				httpResponse.setHeader("p3p", "CP=\"NOI ADM DEV COM NAV OUR STP\"");
				AuthPrincipal authPrincipal = (AuthPrincipal) request.getUserPrincipal();
				token = AonToken.build(authPrincipal.getUuid(), null);
				success(httpRequest, httpResponse, token);
			}
			
		} else {
			error( httpRequest, httpResponse, HttpServletResponse.SC_UNAUTHORIZED, getMessage(httpRequest));
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
		jsonObject.put(IJsonNames.TYPE, IJsonNames.ERROR);
    	resp.setContentType("application/json;charset=UTF-8");
    	response(req, resp, jsonObject);
	}
	
	public String getMessage(ServletRequest request) {
		return new FailedLogin().getMessage(request);
	}
	
	
}
