package com.code.aon.ui.webmail.controller;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;

public class LoginController implements WebMailConstants {

	private static final String LOGIN_SUCCESS = NAVIGATION_FOLDER;
	
	private static final String LOGIN_ERROR = NAVIGATION_LOGIN;

	private boolean logged;

	private String page;
	
	private String error = null;

	public LoginController(){
		page = startWebmail();
	}
	
	private String startWebmail(){
		logged = false;
		try{
			AuthPrincipal mailUser = Utils.getAuthPrincipal();
			if (mailUser != null) {
				System.out.println("LoginController -> startWebmail -> initWebmail");
	    		WebMailController webmail = (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL);
	    		webmail.initDefault(mailUser);
	    		if (webmail.getServer()!=null){
	    			logged = true;
	    			return LOGIN_SUCCESS;
	    		}
	    	}
		} catch (Exception e) {
			System.out.println("LoginController -> startWebmail -> " + e.getMessage());
			e.printStackTrace();
			error = e.getMessage();
	    	return LOGIN_ERROR;
		}
    	return LOGIN_ERROR;
    }

    public boolean isLogged(){
    	return logged;
    }
    
	public String getError() {
		return error;
	}

	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}
	
}
