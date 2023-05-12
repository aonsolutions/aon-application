package com.code.aon.oauth2.fnmt;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.oauth2.Utils;
import com.code.aon.oauth2.sessionInfo.SessionEnterpriseInfo;
import com.code.aon.oauth2.sessionInfo.SessionInfo;

public class FnmtAuthorizationCodeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static String name;
	public static String act;

	public static String getName() {
		return name;
	}

	public static String getAct() {
		return act;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		String authorizationUrl = "https://oauth2.aonsolutions.org/fnmt?" + "redirect_uri=" + getRedirectUri(req);

		onAuthorization(req, resp, authorizationUrl);
	}

	/** Returns the redirect URI for the given HTTP servlet request. */
	protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {

		String key = req.getServerName();
		String action = FnmtAuthorizationServletUtils.getContextURL(req) + "/j_security_check";
		name = key;
		act = action;
		if (!SessionInfo.table.containsKey(key)) {
			SessionEnterpriseInfo se = new SessionEnterpriseInfo();
			se.setAction(action);
			se.setDomain(key);
			SessionInfo.table.put(key, se);
		} else {
			SessionInfo.table.get(key).setDomain(key);
			SessionInfo.table.get(key).setAction(action);
		}

		return FnmtAuthorizationServletUtils.getLoginCallbackURL(req);
	}

	protected void onAuthorization(HttpServletRequest req, HttpServletResponse resp, String authorizationUrl)
			throws IOException {
		String pass = Utils.PasswordGenerator.getPassword(Utils.PasswordGenerator.MINUSCULAS
				+ Utils.PasswordGenerator.MAYUSCULAS + Utils.PasswordGenerator.NUMEROS, 10);

		String redirectUrl = authorizationUrl + "&state=" + req.getServerName() + "$" + pass;
		req.getSession().setAttribute("Oauth2callback.state", pass);
		resp.sendRedirect(redirectUrl);
	}

}
