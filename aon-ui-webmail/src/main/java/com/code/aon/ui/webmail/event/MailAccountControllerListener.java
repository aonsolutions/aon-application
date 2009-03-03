package com.code.aon.ui.webmail.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.controller.LoginController;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.dao.IWebMailAlias;
import com.code.aon.webmail.enumeration.MailAccountStatus;

public class MailAccountControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
    	LoginController loginController = (LoginController)AonUtil.getRegisteredBean(AonConstants.BEAN_LOGIN);
       	User user = loginController.getMailUser();
		IController controller = event.getController();
		Criteria criteria = new Criteria();
		try {
			criteria.addEqualExpression(controller.getFieldName(IWebMailAlias.MAIL_ACCOUNT_USER_ID), user.getId());
			criteria.addOrder(controller.getFieldName(IWebMailAlias.MAIL_ACCOUNT_EMAIL));
			controller.setCriteria(criteria);
		} catch (ManagerBeanException e) {
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
    	LoginController loginController = (LoginController)AonUtil.getRegisteredBean(AonConstants.BEAN_LOGIN);
       	User user = loginController.getMailUser();
		IController controller = event.getController();
		MailAccount mailAccount = (MailAccount) controller.getTo();
		mailAccount.setUser(user);
		mailAccount.setStatus(MailAccountStatus.INACTIVE);
	}
	
}
