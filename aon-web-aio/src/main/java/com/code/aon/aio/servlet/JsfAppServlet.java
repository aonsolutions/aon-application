package com.code.aon.aio.servlet;

import static com.code.aon.aio.servlet.LoginServlet.LOGIN_SERVLET_FAIL_ATTRIBUTE;
import static com.code.aon.aio.servlet.LoginServlet.getRealRequest;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.security.Principal;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;

import com.code.aon.aio.controller.AppController;
import com.code.aon.aio.controller.DesktopController;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

import es.gob.afirma.core.misc.http.HttpError;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "JsfAppServlet", urlPatterns = { "/jsfapp/*" })
public class JsfAppServlet extends HttpServlet {

	private static final String TOKEN = "token";
	private static final String VIEW_ID = "viewId";
	private static final String ACTION = "action";
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
			

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			
			externalContext.redirect("app.jsf");
		} finally {
			releaseFacesContext();
		}	
	}

	private void initDesktopController(HttpServletRequest req) {
		AppController appController =  (AppController) AonUtil.getRegisteredBean(AppController.CONTROLLER_NAME);
		appController.setStandAlone(true);
		appController.setViewId(req.getParameter(VIEW_ID));
		appController.setAction(req.getParameter(ACTION));
		appController.setActionListener(req.getParameter(ACTION_LISTENER));
	}
	
	protected void doLogin(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws HttpError, IOException {

		Request request = getRealRequest(httpRequest);
		if ( request != null ) {
			expireSession(request);

			Session session = request.getSessionInternal();
            Manager manager = request.getContext().getManager();
            String sessionId  = manager.getSessionIdGenerator().generateSessionId();
            manager.changeSessionId(session, sessionId );
            request.changeSessionId(session.getId());
			Principal principal = session.getPrincipal();

			if ( principal == null ) {
				String username = ":-|"; 
				String password = ":-o"; 
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
				throw new HttpError(SC_INTERNAL_SERVER_ERROR, null, null);
			} else {
				httpResponse.setHeader("p3p", "CP=\"NOI ADM DEV COM NAV OUR STP\"");
			}
			String initAction = httpRequest.getParameter("initAction");
			if(initAction != null) {
				AuthPrincipal authPrincipal = (AuthPrincipal) request.getUserPrincipal();
	    		authPrincipal.setInitAction(initAction);
				request.setUserPrincipal(authPrincipal);
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
