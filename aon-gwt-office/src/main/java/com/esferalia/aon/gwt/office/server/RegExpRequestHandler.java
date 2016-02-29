package com.esferalia.aon.gwt.office.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

abstract class RegExpRequestHandler
		implements HttpRequestHandler {

	private Pattern pattern;
	private Matcher matcher;

	public RegExpRequestHandler(String regexp) {
		this.pattern = Pattern.compile(regexp);
	}

	@Override
	public boolean accept(HttpServletRequest req) {
		String action = getRequestAction(req);
		this.matcher = pattern.matcher(action);
		return matcher.matches();
	}

	protected String group(int group) {
		return matcher.group(group);
	}

	protected Integer getDomainId() {
		return Integer.parseInt(group(1));
	}

	protected String getDomainName() {
		return group(2);
	}

	private String getRequestAction(HttpServletRequest req) {

		String action = req.getRequestURI()
				.substring(req.getRequestURI().indexOf(req.getServletPath()));

		return action.substring(req.getServletPath().length());
	}

}
