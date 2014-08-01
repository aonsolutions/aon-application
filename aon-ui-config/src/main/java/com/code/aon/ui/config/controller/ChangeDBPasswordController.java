package com.code.aon.ui.config.controller;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.User;
import com.code.aon.ui.util.AonUtil;

public class ChangeDBPasswordController extends BasicChangePasswordController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(ChangeDBPasswordController.class);
	
	protected void updatePassword( String newPassword ) {
		try {		
			IManagerBean bean = BeanManager.getManagerBean(User.class);
			User user = (User) bean.get(getPrincipal().getUserId());
			user.setPassword( AdminUtil.encodeSHA(newPassword) );
			Date newDate = DateUtils.addDays(new Date(), 180);
			user.setPasswordExpiration(newDate);
			bean.update(user);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("Error cambiando la contraseña" );
		}
	}

	protected boolean isCorrectPassword() {
		byte[] value = AdminUtil.getUserPassword(getPrincipal().getUserId()).getBytes();
		byte[] _password = AdminUtil.encodeSHA(getPassword()).getBytes();
		return Arrays.equals(value, _password);
	}
	
}