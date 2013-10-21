package com.esferalia.aon.ui.payroll.event.contract;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;

public class ContractLeaveSearchListener extends ControllerSearchListener{
	
	private Contract contract;
	
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	@Override
	protected void init() throws ManagerBeanException {
		IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
		setContract((Contract) contractBean.createNewTo());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ((getContract() != null) && (getContract().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_CONTRACT_ID), getContract().getId());			
		} else {
			// TODO: FUTURE: filtrar los contratos segun se este en el dominio parent o no
		}
	}
	
}
