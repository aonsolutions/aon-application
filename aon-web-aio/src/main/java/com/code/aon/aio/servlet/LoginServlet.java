package com.code.aon.aio.servlet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.Principal;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.AuthPrincipal;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class.getName());
	
	public static final String LOGIN_CONTEXT_INIT_PARAM = "loginContext";
	
	private String loginContext;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	    this.loginContext = StringUtils.trimToNull(config.getInitParameter(LOGIN_CONTEXT_INIT_PARAM));
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private Request getRealRequest( HttpServletRequest request ) {
		try {
			Field f = request.getClass().getDeclaredField("request");
			f.setAccessible(true); // grant access to (protected) field
			return (Request)f.get(request);				
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private AuthPrincipal getAuthPrincipal( HttpServletRequest request ) {
		// The CallbackHandler gets the username and password from
		// request parameters in the URL; therefore, the ServletRequest is
		// passed to the CallbackHandler constructor
		WebCallbackHandler webcallback = new WebCallbackHandler(request);

		try {
			LoginContext lcontext = new LoginContext(loginContext, webcallback);
			lcontext.login();
			for( Principal principal : lcontext.getSubject().getPrincipals() ) {
				if ( principal instanceof AuthPrincipal ) {
					return (AuthPrincipal) principal;
				}
			}				
		} catch (LoginException e) {
			LOGGER.error(e.getMessage(),e);
		}		
		return null;
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Request req = getRealRequest(request);
		if ( req != null ) {
			Session session = req.getSessionInternal(false);
			if ( session == null ) {
				session = req.getSessionInternal();
				session.setAuthType(Constants.FORM_METHOD);
			}
			Principal principal = session.getPrincipal(); 
			if ( principal == null ) {
				principal = getAuthPrincipal(request);
				if ( principal != null ) {
					session.setPrincipal(principal);
					// req.setUserPrincipal(principal);
				}
			}
			if ( principal != null ) {
				response.sendRedirect("./home.jsf");
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);			
		}
		
	}

}
