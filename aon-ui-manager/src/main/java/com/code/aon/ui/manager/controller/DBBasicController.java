package com.code.aon.ui.manager.controller;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.manager.ManagerBeanWrapper;

public class DBBasicController extends BasicController implements IManagerConstants {
	
	private ManagerBeanWrapper wrapper;
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		return wrapper.getManagerBean();
	}

	@Override
	public void setPojo(String bean) {
		super.setPojo(bean);
		this.wrapper = new ManagerBeanWrapper(bean);
	}
	
}
