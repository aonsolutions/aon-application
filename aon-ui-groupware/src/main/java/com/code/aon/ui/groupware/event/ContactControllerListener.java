package com.code.aon.ui.groupware.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.Contact;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.ContactController;

public class ContactControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		User user = UserUtils.getLoggedUser();
		Contact contact = (Contact)event.getController().getTo();
		contact.setUser(user);
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		User user = UserUtils.getLoggedUser();
		ContactController contactController = (ContactController)event.getController();
		try {
			Criteria criteria = contactController.getCriteria();
			criteria.addEqualExpression(contactController.getFieldName(IGroupWareAlias.CONTACT_USER_ID), user.getId());
			criteria.addOrder(contactController.getFieldName(IGroupWareAlias.CONTACT_NAME));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining contacts for user=" + user.getLogin(), e);
		}
	}
}