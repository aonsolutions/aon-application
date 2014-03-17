package com.esferalia.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class FanBatchSearchListener extends ControllerSearchListener {

	
	@Override
	protected void completeCriteria(Criteria criteria)
			throws ManagerBeanException, ExpressionException {
		SEPEUtils.getInstance().completeChildDomainCriteria(criteria, "FanBatch.domain", false);
	}

}