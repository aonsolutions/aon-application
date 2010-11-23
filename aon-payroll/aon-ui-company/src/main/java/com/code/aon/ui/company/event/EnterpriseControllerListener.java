package com.code.aon.ui.company.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.EnterpriseTree;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener added to the EnterpriseController
 * 
 */
public class EnterpriseControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		try {
			Enterprise enterprise = (Enterprise) controller.getTo();
			enterprise.getRegistry().setType(RegistryType.LEGAL);
			controller.initDocument();
			controller.initMainActiviy();			
			controller.initMainAddress();			
			controller.initMedias();			
			controller.initMainWorkPlace();			
			controller.initMainDirStaff();			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		try {
			controller.initMainActiviy();			
			controller.initMainAddress();			
			controller.initMedias();			
			controller.initMainWorkPlace();			
			controller.initMainDirStaff();			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
		if ( controller.isTreeView() ) {
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME);
			tree.loadTree();			
		} else {
			BasicController enterpriseActivity = (BasicController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_ACTIVITY_CONTROLLER_NAME);
			enterpriseActivity.onSelectFirst(null);			
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		Enterprise enterprise = (Enterprise) controller.getTo();
		try {
			fillData( controller );
			controller.saveMainActivity();			
			controller.saveMainAddress();			
			controller.saveMedias();			
			controller.saveMainWorkPlace();			
			controller.saveMainDirStaff();			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}

		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = companyController.obtainCompany();
		if (! company.getId().equals(enterprise.getId()) ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(Customer.class);
				Customer customer = new Customer();
				customer.setRegistry(enterprise.getRegistry());
				customer.setScope(enterprise.getScope());
				bean.insert(customer);
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException("Error creating customer from enterprise " + enterprise,e);
			}
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		try {
			controller.saveMainActivity();			
			controller.saveMainAddress();			
			controller.saveMedias();			
			controller.saveMainWorkPlace();			
			controller.saveMainDirStaff();			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	private void fillData( EnterpriseController controller ) {
		RegistryAddress address = controller.getMainAddress();
		EnterpriseCCC ccc = controller.getCCC();
		if ( ccc.getGeozone() == null ) {
			GeoZone geoZone = address.getGeozone();
			ccc.setGeozone( geoZone );
		}
		WorkPlace workPlace = controller.getWorkplace();
		workPlace.setAddress( address );
		workPlace.setDescription( address.getFullAddress() );
	}
	
}
