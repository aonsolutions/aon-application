package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.APPLICATION_PROFILE_CONTROLLER_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.DomainApplication;
import com.code.aon.ui.admin.controller.AdminMainController;
import com.code.aon.ui.admin.controller.DomainApplicationController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainApplicationControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationControllerListener.class);
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getAdmin().resetTermsOfServiceAccepted();	
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		DomainApplication application = dac.getDomainApplication();
		getAdmin().getLogger().domainApplicationAddded(application);
	}	
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		DomainApplication application = dac.getDomainApplication();
		getAdmin().getLogger().domainApplicationRemoved(application);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplication da = (DomainApplication) event.getController().getTo();		
		IController profile = FormUtil.getController(APPLICATION_PROFILE_CONTROLLER_NAME);
		try {
			profile.clearCriteria();
			String alias = profile.getFieldName(IEntityAlias.PROFILE_APPLICATION_ID);
			profile.getCriteria().addEqualExpression(alias, da.getApplication().getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}	
	
}
