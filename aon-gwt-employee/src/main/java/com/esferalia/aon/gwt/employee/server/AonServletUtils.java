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

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.esferalia.aon.gwt.employee.shared.Salary;

class AonServletUtils {

	protected static Connection getConnection() {
		String sessionFactory = HibernateUtil
				.getSessionFactoryName(Salary.class.getName());
		return HibernateUtil.getSQLConnection(sessionFactory);
	}

	protected static String getExtn(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}

	protected static String getWithoutExtn(String path) {
		String fileName = path.substring(path.lastIndexOf('/') + 1);
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	protected static void initFacesContext(ServletContext context, HttpServletRequest request, HttpServletResponse response ) {
		try {
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
					.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
	
			Lifecycle lifecycle = lifecycleFactory
					.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	
	
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

	protected static void releaseFacesContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			facesContext.release();
		}
	}

}
