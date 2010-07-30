package com.code.aon.ui.company.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.customer.Customer;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.EnterpriseTree;
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

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		try {
			controller.initMainActiviy();			
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
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
		EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME);
		tree.loadTree();
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		try {
			controller.saveMainActivity();			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
		
		Enterprise enterprise = (Enterprise) controller.getTo();

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
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
}
