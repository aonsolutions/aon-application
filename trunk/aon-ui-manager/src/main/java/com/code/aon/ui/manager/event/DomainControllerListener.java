package com.code.aon.ui.manager.event;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.Domain;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainApplicationController;
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.DomainDBConnectionController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.util.AonUtil;

public class DomainControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainControllerListener.class);

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

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		Domain domain = (Domain) event.getController().getTo();
		updateDomain(domain);
	}

	private void updateDomain( Domain domain ) {
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		dac.setDomain(domain.getCommonName());
		DomainDBConnectionController ddbc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		ddbc.setDomain(domain.getCommonName());		
	}
}
