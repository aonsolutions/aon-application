package com.code.aon.jaas.auth.spi.db;

import javax.security.auth.login.LoginException;

public class NoPasswordLoginModule extends OpenIDLoginModule {
	
	

	@Override
	protected String getUsersPassword() throws LoginException {
		String[] info = getUsernameAndPassword();
		String user = info[0];
		String password = info[1];
		super.getUsersPassword(); // TODO: No comments, only remove it.
		return createPasswordHash(user, password, "storeDigestCallback");

	}

	
	// -------------------------------------

	

}
