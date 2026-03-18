package com.code.aon.aio.servlet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.IDN;
import java.security.Principal;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.code.aon.AonVersion;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

	static final String LOGIN_SERVLET_FAIL_ATTRIBUTE = "loginServletFail";

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class.getName());
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		boolean closeSession = closeSession(httpRequest);

		Request request = getRealRequest(httpRequest);
		if ( request != null ) {
			Session session = request.getSessionInternal(false);
			if(closeSession) {
				session.expire();
				session = null;
			}
			if ( session == null ) {
				session = request.getSessionInternal();
	            Manager manager = request.getContext().getManager();
	            String sessionId  = manager.getSessionIdGenerator().generateSessionId();
	            manager.changeSessionId(session, sessionId );
	            request.changeSessionId(session.getId());
			}
			
			Principal principal = session.getPrincipal(); 
			if ( principal == null ) {
				String username = httpRequest.getParameter("j_username");
				String password = httpRequest.getParameter("j_password");
				principal = request.getContext().getRealm().authenticate(username, password);
				if ( principal != null ) {
					request.setUserPrincipal(principal);
					session.setPrincipal(principal);
				}
			}
			boolean sessionUpdated = false;
			if ( session instanceof HttpSession ) {
				HttpSession httpSession = (HttpSession) session;
				if ( principal == null ) {
					httpSession.setAttribute(LOGIN_SERVLET_FAIL_ATTRIBUTE, Boolean.TRUE.toString());
				} else {
					httpSession.removeAttribute(LOGIN_SERVLET_FAIL_ATTRIBUTE);
				}
				sessionUpdated =true;
			}
			if ( (principal == null) && (!sessionUpdated) ) {
				httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} else {
				httpResponse.setHeader("p3p", "CP=\"NOI ADM DEV COM NAV OUR STP\"");
				httpResponse.sendRedirect("original");				
			}
			String initAction = httpRequest.getParameter("initAction");
			if(initAction != null) {
				AuthPrincipal authPrincipal = (AuthPrincipal) request.getUserPrincipal();
	    		authPrincipal.setInitAction(initAction);
				request.setUserPrincipal(authPrincipal);
//	    		session.setPrincipal(authPrincipal);
			}
			
		} else {
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);			
		}	
	}
	
	private boolean closeSession(HttpServletRequest request) {
		String token = request.getParameter("token") != null ? IDN.toUnicode(request.getParameter("token")) : null;
		if(token == null) return false;
		JSONObject json = decodeJWT(token);
		String supuser = json.optString(IJsonNames.SUP_USER);
		return AonStringUtils.isNotBlank(supuser);
	}
	
	public static JSONObject decodeJWT(String token) {
		DecodedJWT jwt = JWT.decode(token);
		return new JSONObject(jwt.getSubject());
	}

	static Request getRealRequest( HttpServletRequest request ) {
		try {
			Field f = request.getClass().getDeclaredField("request");
			f.setAccessible(true); // grant access to (protected) field
			return (Request)f.get(request);				
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}
	
}
