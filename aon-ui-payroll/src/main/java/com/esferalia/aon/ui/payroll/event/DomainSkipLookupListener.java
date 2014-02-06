package com.esferalia.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class DomainSkipLookupListener extends ControllerSearchListener {

	private String domainAlias;
	
	public String getDomainAlias() {
		return domainAlias;
	}

	public void setDomainAlias(String domainAlias) {
		this.domainAlias = domainAlias;
	}

	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		PayrollUtils utils = PayrollUtils.getInstance();
		Integer[] ids = {0, DomainManager.getCurrentDomain(), utils.getParentDomainId()};
		criteria.setSkipDomainFilter( true );
		criteria.addInExpression(getDomainAlias(), ids);
	}
	
}