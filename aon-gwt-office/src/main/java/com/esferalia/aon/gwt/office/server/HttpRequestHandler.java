package com.esferalia.aon.gwt.office.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpRequestHandler {
	boolean accept(HttpServletRequest req);

	void handler(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException;

}
