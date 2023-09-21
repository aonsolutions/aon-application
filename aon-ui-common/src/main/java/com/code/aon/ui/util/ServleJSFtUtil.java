package com.code.aon.ui.util;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The Class ServleJSFtUtil is used to access faces context and backing beans in a servlet filter.
 */
public class ServleJSFtUtil {

	// You need an inner class to be able to call
	// FacesContext.setCurrentInstance
	// since it's a protected method
	/**
	 * The Class InnerFacesContext.
	 */
	private abstract static class InnerFacesContext extends FacesContext {
		
		/**
		 * Sets the faces context as current instance.
		 * 
		 * @param facesContext the faces context
		 */
		protected static void setFacesContextAsCurrentInstance(FacesContext facesContext) {
			FacesContext.setCurrentInstance(facesContext);
		}
	}

	/**
	 * Gets the faces context.
	 * 
	 * @param request the request
	 * @param response the response
	 * 
	 * @return the faces context
	 */
	public static FacesContext getFacesContext(ServletRequest request, ServletResponse response) {
		// Try to get it first
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null)
			return facesContext;

		FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

		// Either set a private member servletContext =
		// filterConfig.getServletContext();
		// in you filter init() method or set it here like this:
		ServletContext servletContext = ((HttpServletRequest)request).getSession().getServletContext();
		// Note that the above line would fail if you are using any other
		// protocol than http

		// Doesn't set this instance as the current instance of
		// FacesContext.getCurrentInstance
		facesContext = contextFactory.getFacesContext(servletContext, request, response, lifecycle);

		// Set using our inner class
		InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);

		// set a new viewRoot, otherwise context.getViewRoot returns null
		UIViewRoot view = facesContext.getApplication().getViewHandler()
				.createView(facesContext, "yourOwnID");
		facesContext.setViewRoot(view);

		return facesContext;
	}
	
	/**
	 * Gets the managed bean.
	 * 
	 * @param request the request
	 * @param name the name
	 * @param response the response
	 * 
	 * @return the managed bean
	 */
	public static Object getManagedBean( ServletRequest request, ServletResponse response, String name ) {
		FacesContext fc = getFacesContext( request, response );
		ELContext elctx = fc.getELContext();
		Application application = fc.getApplication();
		ExpressionFactory ef = application.getExpressionFactory();
		ValueExpression ve = ef.createValueExpression(elctx,"#{" + name + "}",Object.class);
		return ve.getValue(elctx);
//		return application.getVariableResolver().resolveVariable(fc, name);
	}
}
