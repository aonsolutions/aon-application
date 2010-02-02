package com.code.aon.ui.config.controller;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.config.User;
import com.code.aon.ui.form.BasicController;

public class UserController extends BasicController {
	
	public static final String MANAGER_BEAN_NAME = "user";

	/** User manager. */
    private UserManager userManager = new UserManager();

	/**
	 * @return Returns the userManager.
	 */
	public UserManager getUserManager() {
		return userManager;
	}

	/**
	 * @param userManager The userManager to set.
	 */
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}	

	public void setUserTO(User user) {
		super.setTo( user );
	}

	/**
	 * Return security administration console URL. 
	 * 
	 * @param event
	 */
	public String getAdminConsoleUrl() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) ctx.getExternalContext().getRequest();
        HttpServletResponse res = (HttpServletResponse) ctx.getExternalContext().getResponse();
        String url = 
        	req.getProtocol().substring(0, 4) + "://" + req.getLocalName() + ":" + req.getLocalPort() 
        		+ userManager.getSecurityApplicationCtx();
        return res.encodeURL( url );
	}

	/**
	 * Logout user from application invalidating current session.
	 * 
	 * @param event
	 */
	public void onLogout(ActionEvent event) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ( (HttpSession) ctx.getExternalContext().getSession( false ) ).invalidate();
	}
}
