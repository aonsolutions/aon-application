package com.code.aon.ui.manager.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class UserLinesController extends BasicController implements IManagerConstants {
	
	@Override
	public void accept(ActionEvent event) {
		try {
			if (isNew()) {
				DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
				updateUser(duc.getUser(), getTo());
			}
			super.accept(event);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void updateUser(User user, ITransferObject lineTo ) throws ManagerBeanException {
		try {
			PropertyUtils.setProperty(lineTo, "user", user);
		} catch (Throwable e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}	
}