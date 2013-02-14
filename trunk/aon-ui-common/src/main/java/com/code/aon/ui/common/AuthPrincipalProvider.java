package com.code.aon.ui.common;

import com.code.aon.common.audit.IAuthPrincipalProvider;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;

public class AuthPrincipalProvider implements IAuthPrincipalProvider {

	@Override
	public AuthPrincipal getAuthPrincipal() {
		return AonUtil.getAuthPrincipal();
	}

}
