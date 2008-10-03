package com.code.aon.ui.groupware.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.dao.IContactAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.ContactController;

public class ContactControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		User user = UserUtils.getLoggedUser();
		ContactController contactController = (ContactController)event.getController();
		try {
			Criteria criteria = contactController.getCriteria();
			criteria.addOrder(contactController.getFieldName(IContactAlias.CONTACT_NAME));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining contacts for user=" + user.getLogin(), e);
		}
	}
}