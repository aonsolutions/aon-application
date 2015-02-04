package com.esferalia.aon.ui.payroll.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class AgreementSearchListener extends ControllerSearchListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	protected void completeCriteria(Criteria criteria)
			throws ManagerBeanException, ExpressionException {
		criteria.addGreaterThanExpression(this.getFieldName(IEntityAlias.AGREEMENT_ID), 0);
	}

}