package com.code.aon.ui.common.domain;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import com.code.aon.common.domain.DomainManager;

public class ServletContextDomainListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		FacesDomainProvider facesDomainProvider = new FacesDomainProvider();
		DomainManager.setDomainProvider( facesDomainProvider );
	}

}
