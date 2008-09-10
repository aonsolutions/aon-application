package com.code.aon.ui.academy.controller;

import javax.faces.model.DataModel;

import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.form.LinesController;

public class AlumnLoanController extends LinesController {

	private boolean current;
	private boolean all;

	@Override
	public void initModel() {
		current = false;
		all = false;
		super.initModel();
	}

	public DataModel getCurrentLoanModel() throws ManagerBeanException {
		if (!current) {
			setCurrentMode(true);
			setModel(null);
			clearCriteria();
			getCriteria().addEqualExpression(getFieldName(IAcademyAlias.ALUMN_LOAN_CUSTOMER_ID), ((Customer)getMasterController().getTo()).getId());
			getCriteria().addNullExpression(getFieldName(IAcademyAlias.ALUMN_LOAN_END_DATE));
		}
		return getModel();
	}

	public DataModel getAllLoanModel() throws ManagerBeanException {
		if (!all) {
			setCurrentMode(false);
			setModel(null);
			clearCriteria();
			getCriteria().addEqualExpression(getFieldName(IAcademyAlias.ALUMN_LOAN_CUSTOMER_ID), ((Customer)getMasterController().getTo()).getId());
			getCriteria().addNotNullExpression(getFieldName(IAcademyAlias.ALUMN_LOAN_END_DATE));
		}
		return getModel();
	}

	private void setCurrentMode(boolean current) {
		this.current = current;
		this.all = !current;
	}

}
