package com.code.aon.desktop.event;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.desktop.DBConnnection;
import com.code.aon.desktop.Domain;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.desktop.controller.AonDomainController;
import com.code.aon.desktop.controller.DomainController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DomainControllerListener extends ControllerAdapter implements IDesktopConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainControllerListener.class);

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		Domain domain = (Domain) event.getController().getTo();
		String name = domain.getCommonName() + "." + domainController.getDomainSuffix();
		domain.setCommonName(name);
		AonDomainController adc = (AonDomainController) AonUtil.getRegisteredBean(CURRENT_DOMAIN_CONTROLLER_NAME);
		domain.setParentDomain( adc.getDomain() );		
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
			LOGGER.error(e.getMessage(), e);
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
