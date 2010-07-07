package com.code.aon.ui.company.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class WorkActivitySearchListener extends ControllerSearchListener {
	
	private Enterprise enterprise;
	
	private WorkPlace workPlace;
	
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setEnterprise(new Enterprise());
		setWorkPlace(new WorkPlace());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria( criteria );
		if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
			criteria.addEqualExpression(getFieldName("WorkActivity_workPlace_enterprise_id"), getEnterprise().getId());
		}		
		if ((getWorkPlace() != null) && (getWorkPlace().getId() != null)) {
			criteria.addEqualExpression(getFieldName(ICompanyAlias.WORK_ACTIVITY_WORK_PLACE_ID), getWorkPlace().getId());
		}		
	}
	
}