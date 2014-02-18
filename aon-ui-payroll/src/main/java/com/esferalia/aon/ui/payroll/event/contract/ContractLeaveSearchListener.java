package com.esferalia.aon.ui.payroll.event.contract;

import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;

public class ContractLeaveSearchListener extends ControllerSearchListener{
	
	private Contract contract;
	private Object endDateFrom;
	private Object endDateTo;
	private boolean active;
	
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Object getEndDateFrom() {
		return endDateFrom;
	}

	public void setEndDateFrom(Object endDateFrom) {
		this.endDateFrom = endDateFrom;
	}

	public Object getEndDateTo() {
		return endDateTo;
	}

	public void setEndDateTo(Object endDateTo) {
		this.endDateTo = endDateTo;
	}

	@Override
	protected void init() throws ManagerBeanException {
		IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
		setContract((Contract) contractBean.createNewTo());
		setActive(true);
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ((getContract() != null) && (getContract().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_CONTRACT_ID), getContract().getId());			
		}
		
		if(isActive()){
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_END_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_END_DATE));
			criteria.addExpression( ExpressionUtilities.getOrExpression(expr1, expr2) );
		} else {
			if(getEndDateFrom()!=null){
				criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_END_DATE), getEndDateFrom());			
			}
			if(getEndDateTo()!=null){
				criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_END_DATE), getEndDateTo());			
			}
		}
		
	}
	
}
