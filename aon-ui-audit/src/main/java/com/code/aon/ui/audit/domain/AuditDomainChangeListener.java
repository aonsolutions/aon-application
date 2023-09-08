package com.code.aon.ui.audit.domain;

import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.domain.DomainEvent;
import com.code.aon.common.domain.IDomainChangeListener;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.util.AonUtil;

public class AuditDomainChangeListener implements IDomainChangeListener, Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditDomainChangeListener.class);

	@Override
	public void beforeDomainChanged(DomainEvent event) {
		if (event.getOldDomain() != null) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			Object servletRequest = ec.getRequest();
			if ( (servletRequest != null) && (servletRequest instanceof HttpServletRequest) ) {
				HttpServletRequest request = (HttpServletRequest) servletRequest;
				HttpSession httpSession = request.getSession(false);
				
				closeLoginAudit(httpSession);
			}
		}
	}

	private void closeLoginAudit( HttpSession httpSession ) {
		try {
			AuditManager.closeLoginAudit( httpSession );	
		} catch ( Throwable th ) {
			LOGGER.error( "Error closing login audit", th );
		}
	}	

	@Override
	public void afterDomainChanged(DomainEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Object servletRequest = ec.getRequest();
		
		if ( (servletRequest != null) && (servletRequest instanceof HttpServletRequest) ) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpSession httpSession = request.getSession(false);

			if ( httpSession != null) {
				AuditLevel auditLevel = AuditManager.getAuditLevel(httpSession);
				if ( auditLevel == null ) {
					AuthPrincipal principal = AonUtil.getAuthPrincipal();
					AuditManager.insertLoginAudit(httpSession, request, event.getNewDomain(), principal );
				}
			}
		}
	}
	
}
