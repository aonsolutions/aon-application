package com.code.aon.ui.company.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the EnterpriseController
 * 
 */
public class EnterpriseControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		Enterprise enterprise = (Enterprise) controller.getTo();
		enterprise.getRegistry().setType(RegistryType.LEGAL);
		controller.initDocument();
		controller.reset();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		try {
			controller.reset();
			controller.initMainActiviy();			
			controller.initRegistryInfo();					
			controller.initMainWorkPlace();			
			controller.initMainDirStaff();			
			controller.initLogo();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		Enterprise enterprise = (Enterprise) controller.getTo();
		try {
			controller.initRegistryInfo();
			WorkPlace workPlace = insertWorkPlace(enterprise, controller.getMainAddress() );
			controller.setWorkplace(workPlace);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		try {
			controller.saveMainAddress();
			controller.saveLogo();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	private WorkPlace insertWorkPlace( Enterprise enterprise, RegistryAddress address ) throws ManagerBeanException {
		WorkPlace workPlace = new WorkPlace();
		workPlace.setEnterprise( enterprise );
		workPlace.setActive( true );
		workPlace.setAddress( address );
		workPlace.setDescription( address.getFullAddress() );
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		bean.insert( workPlace );
		return workPlace;
	}
	
}
