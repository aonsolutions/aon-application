package com.esferalia.aon.gwt.common.server;

import java.io.IOException;
import java.security.Principal;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;

//@WebFilter(filterName = "SessionLessFilter", urlPatterns = { "/aon_gwt_payroll/*" })
public class SessionLessFilter implements Filter {
	
	private static class HttpSessionLessRequest extends HttpServletRequestWrapper{

		public HttpSessionLessRequest(HttpServletRequest request) {
			super(request);
		}
		
		@Override
		public HttpSession getSession() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public HttpSession getSession(boolean create) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Principal getUserPrincipal() {
			throw new UnsupportedOperationException();
		}
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSessionLessRequest sessionLessRequest = new HttpSessionLessRequest(httpRequest);
		
		HttpServletRequestValve.setHttpServletRequest(sessionLessRequest);
		
		chain.doFilter(sessionLessRequest, response);
	}

	@Override
	public void destroy() {
	}

}
