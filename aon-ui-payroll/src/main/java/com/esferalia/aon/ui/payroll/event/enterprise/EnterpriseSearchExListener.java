package com.esferalia.aon.ui.payroll.event.enterprise;

import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_CONTROLLER_NAME;
import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.InactiveLastPeriod;
import com.esferalia.aon.ui.payroll.controller.EnterpriseTree;

public class EnterpriseSearchExListener extends ControllerSearchListener {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseSearchExListener.class);
	
	private Enterprise enterprise; 

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	private EnterpriseTree getEnterpriseTree() {
		return (EnterpriseTree) AonUtil.getRegisteredBean(ENTERPRISE_TREE_CONTROLLER_NAME);
	}

	@Override
	protected void init() throws ManagerBeanException {
		EnterpriseController ec = (EnterpriseController) getController();
		ec.setFormAction(null);
		setEnterprise((Enterprise)BeanManager.getManagerBean(Enterprise.class).createNewTo());		
		EnterpriseTree et = getEnterpriseTree();
		et.setContract((Contract)BeanManager.getManagerBean(Contract.class).createNewTo());
		et.setActiveContract(true);
		et.setInactiveContract(false);
		et.setInactiveDate(null);
		et.setInactiveLastPeriod(InactiveLastPeriod.LAST_MONTH);
	}
	
	public void onEnterpriseChanged(LookupChangeEvent event) {
		if (event.getNewValue()!=null) {
			Enterprise enterprise = (Enterprise) event.getNewValue();
			try {			
				EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
				ec.select(null, enterprise);
			} catch (ManagerBeanException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
	}	

	public void onContractChanged(LookupChangeEvent event) {
		if (event.getNewValue()!=null) {
			Contract contract = (Contract) event.getNewValue();
			try {			
				getEnterpriseTree().setContract( contract );
				EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
				ec.select(null, contract.getWorkPlace().getEnterprise());
			} catch (ManagerBeanException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
	}		

}