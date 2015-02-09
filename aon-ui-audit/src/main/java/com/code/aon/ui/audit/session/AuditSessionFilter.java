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

import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.audit.AuditManager;

public class AuditSessionFilter implements Filter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	private void insertLoginAudit( HttpSession httpSession, HttpServletRequest request ) {
		AuthPrincipal principal = (AuthPrincipal) request.getUserPrincipal();
		int domainId = principal.getDomainId();
		AuditManager.insertLoginAudit(httpSession, request, domainId, principal );
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		if ( (servletRequest != null) && (servletRequest instanceof HttpServletRequest) ) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpSession httpSession = request.getSession(false);

			if ( httpSession != null) {
				AuditLevel auditLevel = AuditManager.getAuditLevel(httpSession);
				if ( auditLevel == null ) {
					insertLoginAudit(httpSession, request );
				}
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
	}
	
}
