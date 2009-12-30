package com.code.aon.ui.webmail.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.controller.AutoCompleteEmailDictionary;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.groupware.Contact;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.dao.IWebMailAlias;

public class EmailControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
       	MailAccount account = webMailController.getServer().getAccount();
		IController controller = event.getController();
		Criteria criteria = new Criteria();
		try {
			criteria.addEqualExpression(controller.getFieldName(IGroupWareAlias.CONTACT_USER_ID), account.getUser().getId());
			criteria.addOrder(controller.getFieldName(IGroupWareAlias.CONTACT_NAME));
			controller.setCriteria(criteria);
		} catch (ManagerBeanException e) {
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
       	MailAccount account = webMailController.getServer().getAccount();
		IController controller = event.getController();
		Contact email = (Contact) controller.getTo();
		email.setUser(account.getUser());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		AutoCompleteEmailDictionary autoCompleteEmailDictionary = (AutoCompleteEmailDictionary)AonUtil.getRegisteredBean(AonConstants.BEAN_AUTOCOMPLETEEMAILDICC);
		autoCompleteEmailDictionary.init();
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		AutoCompleteEmailDictionary autoCompleteEmailDictionary = (AutoCompleteEmailDictionary)AonUtil.getRegisteredBean(AonConstants.BEAN_AUTOCOMPLETEEMAILDICC);
		autoCompleteEmailDictionary.init();
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		AutoCompleteEmailDictionary autoCompleteEmailDictionary = (AutoCompleteEmailDictionary)AonUtil.getRegisteredBean(AonConstants.BEAN_AUTOCOMPLETEEMAILDICC);
		autoCompleteEmailDictionary.init();
	}
}
