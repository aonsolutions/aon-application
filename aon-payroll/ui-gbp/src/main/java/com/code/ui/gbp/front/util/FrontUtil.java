package com.code.ui.gbp.front.util;

import com.code.aon.ui.util.AonUtil;
import com.code.gbp.Supplier;
import com.code.ui.gbp.front.controller.FrontLoginController;

public class FrontUtil {
	
	private static final String FRONT_LOGIN_CONTROLLER_NAME = "login";

	public static Supplier getCurrentSupplier(){
		FrontLoginController loginController = (FrontLoginController)AonUtil.getRegisteredBean(FRONT_LOGIN_CONTROLLER_NAME);
		return loginController.getSupplier();
	}
}
