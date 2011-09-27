package com.esferalia.aon.ui.payroll.event.enterprise;

import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_CONTROLLER_NAME;
import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME;
import static com.esferalia.aon.ui.payroll.controller.IPayrollConstants.CONTRACT_FORM_TREE;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.ui.payroll.controller.EnterpriseTree;

public class EnterpriseSearchExListener extends ControllerSearchListener {

	private Contract contract;
	
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	@Override
	protected void init() throws ManagerBeanException {
		EnterpriseController ec = (EnterpriseController) getController();
		ec.setFormAction(null);
		setContract((Contract)BeanManager.getManagerBean(Contract.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if ((getContract() != null) && (getContract().getId() != null)) {
			String enterpriseId = getFieldName(ICompanyAlias.ENTERPRISE_ID);
			criteria.addEqualExpression(enterpriseId, getContract().getWorkPlace().getEnterprise().getId() );
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ENTERPRISE_TREE_CONTROLLER_NAME);
			tree.setContract( getContract() );
			EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
			ec.setFormAction(CONTRACT_FORM_TREE);
		}
	}

}