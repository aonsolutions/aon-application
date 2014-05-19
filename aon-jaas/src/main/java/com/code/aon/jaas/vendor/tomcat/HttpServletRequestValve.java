package com.code.aon.jaas.vendor.tomcat;

import java.io.IOException;
import java.net.IDN;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

public class HttpServletRequestValve extends ValveBase {

	/** ThreadLocal to save the HttpServletRequest. */
	private static ThreadLocal<HttpServletRequest> httpRequest = new ThreadLocal<HttpServletRequest>();

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		try {
			// Set the ThreadLocal
			httpRequest.set(request.getRequest());

			// Perform the request
			getNext().invoke(request, response);
		} finally {
			// Unset the ThreadLocal
			httpRequest.set(null);
		}
	}

	public static HttpServletRequest getHttpServletRequest() {
		return HttpServletRequestValve.httpRequest.get();
	}

	public static String getServerName() {
		return IDN.toUnicode(getHttpServletRequest().getServerName());
	}
	
}
