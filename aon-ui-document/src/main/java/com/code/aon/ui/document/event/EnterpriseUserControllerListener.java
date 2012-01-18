package com.code.aon.ui.document.event;

import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.document.AlfrescoUserManager;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.document.controller.ManagerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener added to the EnterpriseController
 * 
 */
public class EnterpriseUserControllerListener extends ControllerAdapter {

	private AlfrescoUserManager getUserManager() {
		ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		return mc.getUserManager();
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseUser user = (EnterpriseUser) event.getController().getTo();
		user.setActive(true);
		EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		user.setEnterprise( ec.getEnterprise() );
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseUser user = (EnterpriseUser) event.getController().getTo();
		try {
			user.setPassword(user.getLogin());
			getUserManager().createUser(user);
		} catch (DAOException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseUser user = (EnterpriseUser) event.getController().getTo();
		try {
			getUserManager().updateUser(user);
		} catch (DAOException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseUser user = (EnterpriseUser) event.getController().getTo();
		try {
			getUserManager().deleteUser(user.getLogin());
		} catch (DAOException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
	
}