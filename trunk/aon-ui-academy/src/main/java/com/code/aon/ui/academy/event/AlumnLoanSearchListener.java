package com.code.aon.ui.academy.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class AlumnLoanSearchListener extends ControllerSearchListenerEx {

	private boolean notReturned;

	public boolean isNotReturned() {
		return notReturned;
	}

	public void setNotReturned(boolean notReturned) {
		this.notReturned = notReturned;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setNotReturned(true);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if ( isNotReturned() ) {
			String field = getController().getFieldName(IEntityAlias.ALUMN_LOAN_END_DATE);
			criteria.addNullExpression(field);			
		}
	}

}