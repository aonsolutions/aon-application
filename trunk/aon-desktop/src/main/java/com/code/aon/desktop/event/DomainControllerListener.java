package com.code.aon.desktop.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.code.aon.desktop.DBConnnection;
import com.code.aon.desktop.Domain;
import com.code.aon.desktop.controller.DomainController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DomainControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(DomainControllerListener.class.getName());

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		Domain domain = (Domain) event.getController().getTo();
		String name = domain.getCommonName() + "." + domainController.getDomainSuffix();
		domain.setCommonName(name);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		Domain domain = (Domain) event.getController().getTo();
		String name = domain.getCommonName();
		try {
			DBConnnection dbConnection = domainController.createDB(name);
			domainController.createUsers(dbConnection, name);
			domainController.createApplications(dbConnection, name);
			domainController.addAccessPolicy(name);
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			domainController.removeDomain(name, true);
			domain.setCommonName(StringUtils.substringBefore(domain.getCommonName(), "."));
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		Domain domain = (Domain) event.getController().getTo();
		domainController.removeDomain(domain.getCommonName(), true);
	}
	
}
