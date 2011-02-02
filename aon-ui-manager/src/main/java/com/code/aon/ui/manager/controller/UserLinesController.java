package com.code.aon.ui.manager.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ui.util.AonUtil;

public class UserLinesController extends DBBasicController implements IManagerConstants {
	
	@Override
	public void accept(ActionEvent event) {
		try {
			if (isNew()) {
				DomainApplicationUserController dauc = (DomainApplicationUserController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_USER_CONTROLLER_NAME);
				updateUser(dauc.getUser(), getTo());
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