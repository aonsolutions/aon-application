package com.code.aon.ui.common.controller;

import static com.code.aon.ui.common.ICommonConstants.LOGGED_USER_CONTROLLER_NAME;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.util.AonUtil;

/**
 * The Class LoggedInCheck.
 */
public class LoggedInCheck implements PhaseListener {

	private static final long serialVersionUID = 2066812258680315064L;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LoggedInCheck.class);
	
	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	@Override
	public void beforePhase(PhaseEvent event) {
	}

	@Override
	public void afterPhase(PhaseEvent event) {
		FacesContext fc = event.getFacesContext();
		if ( fc.getViewRoot() != null ) {
			String viewId = fc.getViewRoot().getViewId();
			if ( StringUtils.endsWith(viewId, ".xhtml") ) {
				boolean loginPage = StringUtils.endsWith(viewId, "login.xhtml");
		        if (!loginPage && !loggedIn()) {
		        	LOGGER.warn( "User no logged for viewId: {}", viewId );
		        	try {
		        		ExternalContext ec = fc.getExternalContext();
		        		ec.redirect( ec.getRequestContextPath() );
		            	HttpSession session = (HttpSession) ec.getSession(false);
		            	session.invalidate();    	
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
		        }				
			}
        }
	}

	private boolean loggedIn() {
		LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(LOGGED_USER_CONTROLLER_NAME);
        return loggedUser.isLogged();
    }
}
