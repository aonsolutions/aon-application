package com.code.aon.ui.payroll.servlet;

import java.io.IOException;
import java.security.Principal;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanServerLocator;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AonGenericPrincipal;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ui.payroll.controller.PayrollController;

public class CosmosServlet extends HttpServlet {

	private static final long serialVersionUID = 7256216276084389612L;

	/** CosmosServlet Logger */
	private static final Log LOGGER = LogFactory.getLog( CosmosServlet.class.getName() );

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process( req, resp );
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process( req, resp );
	}

	@SuppressWarnings("deprecation")
	private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) req;
		AonGenericPrincipal agp = null;
		try {
			agp = Utils.getSSOPrincipal( httpRequest.getSession().getId() );
		} catch (DeploymentException e) {
			LOGGER.error( e );
		}
		if ( agp != null && httpRequest.getAuthType().equals( IConstants.AUTH_TYPE ) ) {
			AuthPrincipal principal = (AuthPrincipal) agp.getUserPrincipal();
			if ( principal != null ) {
				String username = principal.getShortName() + IConstants.IDENTITY_SEPARATOR 
									+ principal.getDomain() + httpRequest.getContextPath();
				Principal p = agp.getRealm().authenticate( username, (String) agp.getCredentials() ); 
				if( p != null ) {
					register( agp.getRequest(), p, IConstants.AUTH_TYPE );
					LifecycleFactory lFactory = 
						(LifecycleFactory) FactoryFinder.getFactory( FactoryFinder.LIFECYCLE_FACTORY );
					Lifecycle lifecycle = lFactory.getLifecycle( LifecycleFactory.DEFAULT_LIFECYCLE );
					FacesContextFactory fcFactory = 
						(FacesContextFactory) FactoryFinder.getFactory( FactoryFinder.FACES_CONTEXT_FACTORY );
					FacesContext facesContext = fcFactory.getFacesContext( getServletContext(), req, resp,lifecycle );
					Application application = facesContext.getApplication();
					PayrollController payroll = 
						(PayrollController) application.getVariableResolver().resolveVariable( facesContext, "payrollController" );
					payroll.setMainMenuEnabled( false );
				    String uri = req.getRequestURI().replaceFirst( "/view", "" );
				    agp.getRequest().getResponse().sendRedirect( uri );
				} else {
					unregister( agp.getRequest() );
					agp.getRequest().getResponse().sendRedirect( "/login/errorlogin.xhtml" );
				}
			}
			String sessionId = httpRequest.getSession().getId();
			try {
				Util.removeSSOPrincipal( MBeanServerLocator.locateJBoss(), new ObjectName( "jboss.admin:service=AonSessionManager" ), sessionId );
			} catch (Exception e) {
				LOGGER.error( "Error removing SSO principal for this session:" + sessionId + ". " + e.getMessage(), e );
			}
		}
	}

	/**
	 * Register the principal with the request, session etc just the way AuthenticatorBase does.
	 * 
	 * @param request Catalina Request
	 * @param principal User Principal generated via authentication
     * @param authType The authentication type to be registered
	 */
	private void register(Request request, Principal principal, String authType) {
		request.setAuthType( authType );
		request.setUserPrincipal( principal ); 
		//Cache the authentication principal in the session
		Session session = request.getSessionInternal( false );
		if(session != null) {
			session.setAuthType( authType );
			session.setPrincipal( principal );
		}
	}

	/**
	 * UnRegister the principal with the request and session.
	 * 
	 * @param request Catalina Request
	 */
	private void unregister(Request request) {
		request.setUserPrincipal( null ); 
		Session session = request.getSessionInternal( false );
		if(session != null) {
			session.setPrincipal( null );
		}
	}
}
