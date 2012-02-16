package com.esferalia.aon.gwt.employee.server;

import java.sql.Connection;

import javax.faces.context.FacesContext;
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

	protected void initFacesContext() {
		ServletContext context = getServletContext();
		HttpServletRequest request = getThreadLocalRequest();
		HttpServletResponse response = getThreadLocalResponse();
		AonServletUtils.initFacesContext(context, request, response);
	}
	
	protected void releaseFacesContext() {
		AonServletUtils.releaseFacesContext();
	}

}
