package com.code.aon.ui.ecommerce.hibernate;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.code.aon.common.dao.hibernate.HibernateUtil;

public class HibernateConfigContextListener implements ServletContextListener {

	/**
	 * Parses the config file.
	 * 
	 * @param sce the ServletContextEvent
	 */
	public void contextInitialized(ServletContextEvent sce) {
		HibernateUtil.setConfigurationFactory( DomainConfigurationFactory.getInstance() );
	}

	/**
	 * Context destroyed.
	 * 
	 * @param sce the sce
	 */
	public void contextDestroyed(ServletContextEvent sce) {
	}	
}
