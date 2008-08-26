/**
 * 
 */
package com.code.aon.ui.audit.session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class AudictSessionListener implements HttpSessionListener {

	/** Obtiene un logger apropiado. */
	private static final Logger LOGGER = Logger
			.getLogger(AudictSessionListener.class.getName());

	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		HttpSession session = httpSessionEvent.getSession();

		String sessionScopeUser = (String) session.getAttribute("User");
		if (sessionScopeUser == null || sessionScopeUser.equals("")) {
			session.setAttribute("User", "?????");
		}

		Long sessionScopeLoginDateTime = (Long) session
				.getAttribute("LoginDateTime");
		if (sessionScopeLoginDateTime == null) {
			session.setAttribute("LoginDateTime", System.currentTimeMillis());
		}
	}

	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

		HttpSession session = httpSessionEvent.getSession();

		String user = (String) session.getAttribute("User");
		Long loginDateTimeLong = (Long) session.getAttribute("LoginDateTime");

		Date loginDateTime = new Date(loginDateTimeLong);
		Date logoutDateTime = new Date(System.currentTimeMillis());

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		String strLoginDateTime = format.format(loginDateTime);
		String strLogoutDateTime = format.format(logoutDateTime);

		// JDBC to write the values to the database
	}

}
