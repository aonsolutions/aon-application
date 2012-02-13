package com.esferalia.aon.gwt.employee.server;

import java.sql.Connection;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.registry.Registry;
import com.esferalia.aon.gwt.employee.shared.Salary;
import com.esferalia.aon.web.employee.controller.ManagerController;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class AonRemoteServiceServlet extends RemoteServiceServlet {
	
	protected Integer getPersonID ( ) {
		HttpSession session = getSession();
		ManagerController controller = (ManagerController) session
				.getAttribute(ManagerController.CONTROLLER_NAME);

		EnterpriseUser enterpriseUser = controller.getLoggedUser();

		Registry registry = enterpriseUser.getRegistry();
		
		com.code.aon.company.Enterprise aonEnterprise = enterpriseUser
				.getEnterprise();
		
		
		return registry.getId();
	}

	protected Integer getEnterpriseID() {
		HttpSession session = getSession();
		ManagerController controller = (ManagerController) session
				.getAttribute(ManagerController.CONTROLLER_NAME);
		EnterpriseUser enterpriseUser = controller.getLoggedUser();
		com.code.aon.company.Enterprise aonEnterprise = enterpriseUser
				.getEnterprise();
		Registry registry = aonEnterprise.getRegistry();
		return registry.getId();
	}


	protected HttpSession getSession() {
		HttpServletRequest request = getThreadLocalRequest();
		return request.getSession(false);
	}

	
	protected  static Connection getConnection() {
		String sessionFactory = HibernateUtil
				.getSessionFactoryName(Salary.class.getName());
		return HibernateUtil.getSQLConnection(sessionFactory);
	}
	
	protected void initFacesContext() {
		try {
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
					.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);

			Lifecycle lifecycle = lifecycleFactory
					.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

			ServletContext context = getServletContext();
			HttpServletRequest request = getThreadLocalRequest();
			HttpServletResponse response = getThreadLocalResponse();

			FacesContext facesContext = facesContextFactory.getFacesContext(
					context, request, response, lifecycle);

			UIViewRoot view = facesContext.getApplication().getViewHandler()
					.createView(facesContext, "/home.xhtml");

			facesContext.setViewRoot(view);

		} catch (Throwable throwable) {
			// TODO: Do some usefull with this.
			throwable.printStackTrace();
		}
	}

	protected void releaseFacesContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			facesContext.release();
		}
	}

}
