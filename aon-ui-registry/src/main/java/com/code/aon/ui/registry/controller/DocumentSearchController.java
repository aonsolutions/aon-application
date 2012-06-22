package com.code.aon.ui.registry.controller;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

public class DocumentSearchController extends BasicController {

	@Override
	public void clearCriteria() throws ManagerBeanException {
		super.clearCriteria();
		getCriteria().setSkipDomainFilter(true);
	}

	@Override
	public void setCriteria(Criteria criteria) throws ManagerBeanException {
		super.setCriteria(criteria);
		getCriteria().setSkipDomainFilter(true);		
	}
	
}