package com.code.aon.ui.common;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuthPrincipalProvider;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;

public class AuthPrincipalProvider implements IAuthPrincipalProvider, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public AuthPrincipal getAuthPrincipal() {
		return AonUtil.getAuthPrincipal();
	}

}
