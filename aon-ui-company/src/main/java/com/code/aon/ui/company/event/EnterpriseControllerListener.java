package com.code.aon.ui.company.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener added to the EnterpriseController
 * 
 */
public class EnterpriseControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		controller.onLoad(null);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)	throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		Enterprise enterprise = (Enterprise) controller.getTo();
		enterprise.getRegistry().setType(RegistryType.LEGAL);
		enterprise.getRegistry().setNationality(Country.ES);
		enterprise.getRegistry().setDocumentCountry(Country.ES);
		enterprise.getRegistry().setDocumentType(DocumentType.CIF);

		controller.reset();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		try {
			controller.reset();
			controller.initRegistryInfo();					
			controller.initMainWorkPlace();			
			controller.initMainDirStaff();			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
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
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			EnterpriseController controller = (EnterpriseController) event.getController();
			controller.saveMainAddress();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		companyController.accept(null);
	}
	
	private WorkPlace insertWorkPlace( Enterprise enterprise, RegistryAddress address ) throws ManagerBeanException {
		WorkPlace workPlace = new WorkPlace();
		workPlace.setEnterprise( enterprise );
		workPlace.setActive( true );
		workPlace.setAddress( address );
		workPlace.setDescription( address.getShortAddress() );
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		bean.insert( workPlace );
		return workPlace;
	}
	
}
