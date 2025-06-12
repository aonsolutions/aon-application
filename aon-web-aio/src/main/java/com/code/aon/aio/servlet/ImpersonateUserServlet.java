package com.code.aon.aio.servlet;

import static com.code.aon.aio.servlet.LoginServlet.LOGIN_SERVLET_FAIL_ATTRIBUTE;
import static com.code.aon.aio.servlet.LoginServlet.getRealRequest;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;

import com.code.aon.aio.controller.AppController;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.LocaleElement;
import com.code.aon.ui.common.controller.ConfigurationController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.gob.afirma.core.misc.http.HttpError;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "Impersonate User Servlet", urlPatterns = { "/impuser/*" })
public class ImpersonateUserServlet extends HttpServlet {

	private static final long serialVersionUID = -6742452296361375484L;
	
	private static final String VIEW_ID = "viewId";
	private static final String ACTION = "action";
	private static final String LANGUAGE = "language";
	private static final String ACTION_LISTENER = "actionListener";

	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			doLogin(req, resp);
			
			initFacesContext(req, resp);
			initDesktopController(req);
			initConfigurationController(req);
			initDomainSwitcher(req);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			 
			externalContext.redirect( externalContext.getRequestContextPath() + "/home.jsf" );
		} finally {
			releaseFacesContext();
		}	
	}

	private void initDomainSwitcher(HttpServletRequest req) {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil
				.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		Integer domainId = AonNumberUtils.toInteger(req.getParameter("toDomain"));
		String domainName = req.getParameter("toDomainName");
		String currentDomainName = domainSwitcher.getCurrentDomainName();
		if ( AonStringUtils.notEquals(currentDomainName, domainName ) ) {
			domainSwitcher.select(domainId, domainName);
		}
	}

	private void initConfigurationController(HttpServletRequest req) {
		ConfigurationController configController =  (ConfigurationController) AonUtil.getRegisteredBean(ICommonConstants.CONFIGURATION_CONTROLLER_NAME);
		String language = req.getParameter(LANGUAGE);
		Arrays.stream(configController.getLocales())
		.filter(locale -> Objects.equals(locale.getLanguage(), language))
		.findFirst().ifPresent(LocaleElement::changeLanguage);
	}

	private void initDesktopController(HttpServletRequest req) {
		AppController appController =  (AppController) AonUtil.getRegisteredBean(AppController.CONTROLLER_NAME);
		appController.setViewId(req.getParameter(VIEW_ID));
		appController.setAction(req.getParameter(ACTION));
		appController.setActionListener(req.getParameter(ACTION_LISTENER));
	}
	
	protected void doLogin(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException, ServletException {
		
		Request request = getRealRequest(httpRequest);
		
		if ( request != null ) {
			Integer fromDomain = AonNumberUtils.toInteger(request.getParameter("fromDomain"));
			String fromDomainName = request.getParameter("fromDomainName");
			String fromUser = request.getParameter("fromUser");
			Integer toDomain = AonNumberUtils.toInteger(request.getParameter("toDomain"));
			String toDomainName = request.getParameter("toDomainName");
			String toUser = request.getParameter("toUser");
			
			if (fromDomain == null ) {
				String msg = "No se ha ha indicado el dominio desde el que se quiere acceder.";
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg );
				throw new ServletException(msg);
			} else if ( AonStringUtils.isBlank(fromDomainName)) {
				String msg = "No se ha ha indicado el nombre de dominio desde el que se quiere acceder.";
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
				throw new ServletException(msg);				
			} else if ( AonStringUtils.isBlank(fromUser)) {
				String msg = "No se ha ha indicado el usuario del dominio desde el que se quiere acceder.";
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
				throw new ServletException(msg);				
			} else if (toDomain == null ) {
				String msg = "No se ha ha indicado el dominio al que se quiere acceder.";
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
				throw new ServletException(msg);				
			} else if ( AonStringUtils.isBlank(toDomainName)) {
				String msg = "No se ha ha indicado el nombre de dominio al que se quiere acceder.";
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
				throw new ServletException(msg);				
			} else if ( AonStringUtils.isBlank(toUser)) {
				String msg = "No se ha ha indicado el usuario con del que se quiere acceder al dominio.";
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
				throw new ServletException(msg);				
			} else {
				Occam occam = new Occam()
					.setDomain(toDomain)
					.setDomainName(toDomainName)
					.setUser( toUser );
				if (AonNumberUtils.notEquals(0,fromDomain )) { // Console Domain
					try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
						Domain domain = DomainDAO.getDomain( ctx, toDomain );
						if (domain == null) {
							String msg = "Dominio " + toDomain + " no encontrado.";
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
							throw new ServletException(msg);				
						}
						if (AonNumberUtils.notEquals(fromDomain, domain.getParentId() )) {
							String msg = "Al dominio " + toDomain + " solo se puede acceder desde el dominio padre.";
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
							throw new ServletException(msg);				
						}
					}
				}
				
				Session session = request.getSessionInternal();
				Principal principal = session.getPrincipal();
				if ( principal != null ) {
					expireSession( request );
				}
				session = request.getSessionInternal();
				principal = session.getPrincipal();
				if ( principal == null ) {
					boolean wasEnabled = CONSOLE.enableRemoteAccess( occam,  toDomain);
					
					// Si el acceso remoto no estaba habilitado, se da tiempo al 
					// login (10 segundos) para después deshabilitar el acceso remoto.
					if (!wasEnabled) {
						ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
						Runnable disableRemoteAccess = () -> {
							CONSOLE.switchRemoteAccess( occam,  toDomain);
						};
						scheduler.schedule(disableRemoteAccess, 10, TimeUnit.SECONDS);
					}
			        
					
					String username = "cau="+toUser; 
					String password = "aonc4u"; 
					principal = request.getContext().getRealm().authenticate(username, password);
					if ( principal != null ) {
						request.setUserPrincipal(principal);
						session.setPrincipal(principal);
					}
					
					boolean sessionUpdated = false;
					if ( session instanceof HttpSession httpSession ) {
						if ( principal == null ) {
							httpSession.setAttribute(LOGIN_SERVLET_FAIL_ATTRIBUTE, Boolean.TRUE.toString());
						} else {
							httpSession.removeAttribute(LOGIN_SERVLET_FAIL_ATTRIBUTE);
						}
						sessionUpdated =true;
					}
					if ( (principal == null) && (!sessionUpdated) ) {
						throw new HttpError(SC_INTERNAL_SERVER_ERROR, null, null);
					} else {
						response.setHeader("p3p", "CP=\"NOI ADM DEV COM NAV OUR STP\"");
					}
				}
			}
		} else {
			throw new HttpError(SC_INTERNAL_SERVER_ERROR, null, null);
		}
		
	}

	private void expireSession(Request request) {
		Session session = request.getSessionInternal(false);
		if ( session != null ) {
			session.expire();
		}
	}
	
	
	private void initFacesContext(HttpServletRequest request, HttpServletResponse response ) {
		ServletContext context = getServletContext();
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			if (facesContext != null) {
				return;
			}
			
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
					.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);

			Lifecycle lifecycle = lifecycleFactory
					.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

			facesContext = facesContextFactory.getFacesContext(context,
					request, response, lifecycle);

			UIViewRoot view = facesContext.getApplication().getViewHandler()
					.createView(facesContext, "/home.jsf");

			facesContext.setViewRoot(view);
			
			//This is the way to init logged user, AON way :-(  
			UserUtils.getInstance().getLoggedUser();
			
		} catch (Throwable throwable) {
			// TODO: Do some usefull with this.
			throwable.printStackTrace();
		}
	}
	
	private void releaseFacesContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			facesContext.release();
		}
	}
	
	
}
