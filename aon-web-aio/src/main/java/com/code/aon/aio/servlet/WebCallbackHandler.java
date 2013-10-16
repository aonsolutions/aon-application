package com.code.aon.aio.servlet;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.servlet.ServletRequest;

public class WebCallbackHandler implements CallbackHandler {

	private String userName;
	private String password;

	public WebCallbackHandler(ServletRequest request) {
		userName = request.getParameter("j_username");
		password = request.getParameter("j_password");
	}

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		// Add the username and password from the request parameters to the Callbacks
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof NameCallback) {
				NameCallback nameCall = (NameCallback) callbacks[i];
				nameCall.setName(userName);
			} else if (callbacks[i] instanceof PasswordCallback) {
				PasswordCallback passCall = (PasswordCallback) callbacks[i];
				passCall.setPassword(password.toCharArray());
			} else {
				throw new UnsupportedCallbackException(callbacks[i],
						"The CallBacks are unrecognized in class: " + getClass().getName());
			}
		}
	}

}
