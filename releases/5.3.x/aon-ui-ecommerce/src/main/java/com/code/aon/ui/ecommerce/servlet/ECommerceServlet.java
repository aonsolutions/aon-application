package com.code.aon.ui.ecommerce.servlet;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

public abstract class ECommerceServlet extends HttpServlet{
	
	
	/**
	 * 
	 * You need an inner class to be able to call
	 * FacesContext.setCurrentInstance since it's a protected method.
	 * 
	 * @version $Revision$
	 */
	private abstract static class AbstractInnerFacesContext extends
			FacesContext {
		protected static void setFacesContextAsCurrentInstance(
				final FacesContext facesContext) {
			FacesContext.setCurrentInstance(facesContext);
		}
	}
	
	@SuppressWarnings("unused")
	private FacesContext getFacesContext(final ServletRequest request,
			final ServletResponse response) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			return facesContext;
		}
		FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle lifecycle = lifecycleFactory
				.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		facesContext = contextFactory.getFacesContext(this.getServletContext(),
				request, response, lifecycle);
		return facesContext;
	}
	
	public Object getBean(ServletRequest request, ServletResponse response, String name){
//		FacesContext ctx = FacesContext.getCurrentInstance();
		FacesContext ctx = getFacesContext(request, response);
		ELContext elctx = ctx.getELContext();
		ExpressionFactory ef = ctx.getApplication().getExpressionFactory();
		ValueExpression ve = ef.createValueExpression(elctx,"#{" + name + "}",Object.class);
		return ve.getValue(elctx);
	}

}
