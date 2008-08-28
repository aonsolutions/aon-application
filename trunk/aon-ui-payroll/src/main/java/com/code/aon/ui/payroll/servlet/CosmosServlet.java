package com.code.aon.ui.payroll.servlet;

import java.io.IOException;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.payroll.controller.PayrollController;

public class CosmosServlet extends HttpServlet {

	private static final long serialVersionUID = 7256216276084389612L;

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
	    String uri = req.getRequestURI().replaceFirst( req.getContextPath() + "/view", "" );
		req.getRequestDispatcher( uri ).forward( req, resp );
	}

}
