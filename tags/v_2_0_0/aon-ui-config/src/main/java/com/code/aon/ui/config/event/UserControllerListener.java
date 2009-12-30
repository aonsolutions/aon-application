package com.code.aon.ui.config.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.UserController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class UserControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		UserController userController = (UserController)event.getController();
		try {
			Criteria criteria = userController.getCriteria();
			criteria.addOrder(userController.getFieldName(IConfigAlias.USER_NAME));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining user model");
		}
	}
}