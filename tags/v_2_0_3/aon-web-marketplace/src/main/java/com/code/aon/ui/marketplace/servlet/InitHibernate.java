package com.code.aon.ui.marketplace.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * Servlet that loads all the information required by Hibernate whe application starts.
 */
public class InitHibernate implements ServletContextListener {

	/**
	 * Context destroyed. Empty method
	 * 
	 * @param arg0 the arg0
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	/**
	 * Starts hibernate
	 * 
	 * @param arg0 the arg0
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		HibernateUtil.getSessionFactory();
	}

}
