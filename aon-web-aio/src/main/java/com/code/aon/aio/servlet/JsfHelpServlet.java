package com.code.aon.aio.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import com.code.aon.aio.controller.AppController;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.LocaleElement;
import com.code.aon.ui.common.controller.ConfigurationController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "JsfHelpServlet", urlPatterns = { "/jsfhelp/*" })
public class JsfHelpServlet extends HttpServlet {

	private static final String VIEW_ID = "viewId";
	private static final String ACTION = "action";
	private static final String LANGUAGE = "language";
	private static final String REDIRECT_URL = "redirectUrl";
	private static final String ACTION_LISTENER = "actionListener";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			initFacesContext(req, resp);
			initAppController(req);
			initConfigurationController(req);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			
			externalContext.redirect( AonStringUtils.defaultIfBlank(req.getParameter(REDIRECT_URL), "help.jsf") );
		} finally {
			releaseFacesContext();
		}	
	}


	private void initConfigurationController(HttpServletRequest req) {
		ConfigurationController configController =  (ConfigurationController) AonUtil.getRegisteredBean(ICommonConstants.CONFIGURATION_CONTROLLER_NAME);
		String language = req.getParameter(LANGUAGE);
		Arrays.stream(configController.getLocales())
		.filter(locale -> Objects.equals(locale.getLanguage(), language))
		.findFirst().ifPresent(LocaleElement::changeLanguage);
	}

	private void initAppController(HttpServletRequest req) {
		AppController appController =  (AppController) AonUtil.getRegisteredBean(AppController.CONTROLLER_NAME);
		appController.setViewId(req.getParameter(VIEW_ID));
		appController.setAction(req.getParameter(ACTION));
		appController.setActionListener(req.getParameter(ACTION_LISTENER));
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
	
	
}
