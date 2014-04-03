package com.code.aon.ui.common.hibernate;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.code.aon.common.dao.hibernate.HibernateUtil;

public class ServletContextHibernateListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		HibernateUtil.getSessionFactory(null);		
	}

}
