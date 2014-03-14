package com.esferalia.aon.ui.payroll.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class FanBatchSearchListener extends ControllerSearchListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	protected void completeCriteria(Criteria criteria)
			throws ManagerBeanException, ExpressionException {
		SEPEUtils.getInstance().completeChildDomainCriteria(criteria, "Certifica2Batch.domain", false);
	}

}