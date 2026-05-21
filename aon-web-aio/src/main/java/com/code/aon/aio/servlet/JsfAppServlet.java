package com.code.aon.aio.servlet;

import static com.code.aon.aio.servlet.LoginServlet.LOGIN_SERVLET_FAIL_ATTRIBUTE;
import static com.code.aon.aio.servlet.LoginServlet.getRealRequest;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;

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
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.LocaleElement;
import com.code.aon.ui.common.controller.ConfigurationController;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.gob.afirma.core.misc.http.HttpError;
import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
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
	private static final String THEME = "theme";
	private static final String VIEW_ID = "viewId";
	private static final String ACTION = "action";
	private static final String READ_ONLY = "readOnly";
	private static final String DOMAIN_ID = "domainId";
	private static final String DOMAIN_NAME = "domainName";
	private static final String LANGUAGE = "language";
	private static final String REDIRECT_URL = "redirectUrl";
	private static final String EL_EXPRESSION = "elExpression";
	private static final String EXPIRE_SESSION = "expireSession";
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
			initDomainSwitcher(req);
			initDesktopController(req);
			initConfigurationController(req);
			initLoggedUser(req);
			initElExpression(req);
			initCompanyController(req);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			
			externalContext.redirect( AonStringUtils.defaultIfBlank(req.getParameter(REDIRECT_URL), "app.jsf") );
		} finally {
			releaseFacesContext();
		}	
	}

	private void initDomainSwitcher(HttpServletRequest req) {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil
				.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		Integer domainId = AonNumberUtils.toInteger(req.getParameter(DOMAIN_ID));
		String domainName = req.getParameter(DOMAIN_NAME);
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
		appController.setTheme(getTheme(req));
		appController.setViewId(req.getParameter(VIEW_ID));
		appController.setAction(req.getParameter(ACTION));
		appController.setActionListener(req.getParameter(ACTION_LISTENER));
		appController.setReadOnly(Boolean.parseBoolean(req.getParameter(READ_ONLY)));
	}
	
	private void initCompanyController(HttpServletRequest req) {
    	CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
    	controller.obtainCompany();
	}
	
	private void initLoggedUser(HttpServletRequest req){
		//This is the way to init logged user, AON way :-(  
		UserUtils.getInstance().getLoggedUser();
	}
	
	private void initElExpression(HttpServletRequest req) {
		String elExpression = req.getParameter(EL_EXPRESSION);
		try {
			if ( AonStringUtils.isNotBlank(elExpression) ) {
				evaluate("#{"+elExpression+"}");
			}
		} catch (Exception e) {
			// Do nothing
		}
	}
	
	
	protected void doLogin(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {

		Request request = getRealRequest(httpRequest);
		if ( request != null ) {
			boolean expireSession = Boolean.parseBoolean(request.getParameter(EXPIRE_SESSION));
			if ( expireSession ) {
				expireSession(request);
			}

			Session session = request.getSessionInternal();
            //Manager manager = request.getContext().getManager();
            //String sessionId  = manager.getSessionIdGenerator().generateSessionId();
            //manager.changeSessionId(session, sessionId );
            //request.changeSessionId(session.getId());
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
	
	private void evaluate (String elExpression) {
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		ExpressionFactory expressionFactory = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
		expressionFactory.createValueExpression(elContext, elExpression, Object.class).getValue(elContext);
	}
	
	
	private String getTheme(HttpServletRequest req) {
		String theme = req.getParameter(THEME);
		if ( AonStringUtils.isBlank(theme) ) {
			return "/css/theme/customview.css";
		}
		return theme;
	}
	
}
