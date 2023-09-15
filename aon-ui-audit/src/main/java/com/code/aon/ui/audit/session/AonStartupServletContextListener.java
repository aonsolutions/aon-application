package com.code.aon.ui.audit.session;

import java.net.MalformedURLException;
import java.net.URL;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.hibernate.IConfigurationFactory;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.common.controller.BeanConfiguration;

public class AonStartupServletContextListener implements ServletContextListener {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(AonStartupServletContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext sc = event.getServletContext();		
		initHibernate();
		initBeanConfiguration(sc);
		initApplicationOptionController(sc);
		//TODO A revisar funcionamiento en TOMCAT8 
		//initFacelets(sc);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
	
	private void initBeanConfiguration( ServletContext sc ) {
		LOGGER.info( "Init Bean Configuration");
		try {
			URL url = sc.getResource(BeanConfiguration.AON_CONFIG_XML);
			sc.setAttribute(BeanConfiguration.CONTEXT_PROPERTY, new BeanConfiguration(url));			
		} catch (MalformedURLException e) {
			LOGGER.error( "Error loading " + BeanConfiguration.AON_CONFIG_XML, e);
		}		
	}
	
	private void initApplicationOptionController( ServletContext sc ) {
		LOGGER.info( "Init Application Option Controller");
		ApplicationOptionController aop = new ApplicationOptionController(sc);
		sc.setAttribute(ApplicationOptionController.CONTEXT_PROPERTY, aop);			
	}
	
	private void initHibernate() {
		LOGGER.info( "Init Hibernate Configuration");
		StartupConfigurationFactory configurationFactory = new StartupConfigurationFactory(StartupConnectionProvider.class.getName());
		IConfigurationFactory oldConfigurationFactory = HibernateUtil.getConfigurationFactory();
		HibernateUtil.setConfigurationFactory(configurationFactory);
		HibernateUtil.getSessionFactory(null);
		HibernateUtil.setConfigurationFactory(oldConfigurationFactory);
	}
	
	private void initFacelets( ServletContext sc ) {
		LOGGER.info( "Init Facelets");
		JSFStartupUtil util = new JSFStartupUtil(sc);
		util.initFacelets();
	}	
}