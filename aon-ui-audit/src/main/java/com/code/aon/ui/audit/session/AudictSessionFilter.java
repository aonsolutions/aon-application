package com.code.aon.ui.audit.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AudictSessionFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		if ( (servletRequest != null) && (servletRequest instanceof HttpServletRequest) ) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpSession session = request.getSession(false);

			if ( session != null ) {
				String sessionScopeUser = (String) session.getAttribute("User");
				if (sessionScopeUser == null || sessionScopeUser.equals("")) {
					String user = request.getUserPrincipal().toString();
	
					if (user != null) {
						session.setAttribute("User", user);
					}
				}
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
	}
	
}
