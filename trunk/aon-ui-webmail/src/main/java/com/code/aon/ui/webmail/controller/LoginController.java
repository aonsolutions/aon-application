package com.code.aon.ui.webmail.controller;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;

public class LoginController {

	private AuthPrincipal mailUser;

	private boolean logged;

	private String page;
	
	private String error = null;

	public LoginController(){
		System.out.println("LoginController -> instantiate");
		page = startWebmail();
	}
	
	/**
	 * @return the mailUser
	 */
	public AuthPrincipal getMailUser() {
		return mailUser;
	}
	
	private String startWebmail(){
		System.out.println("LoginController -> startWebmail v3.0.3.0");
		logged = false;
		mailUser = null;
		try{
			login();
			System.out.println("LoginController -> startWebmail -> logged");
	    	if (mailUser != null) {
				System.out.println("LoginController -> startWebmail -> initWebmail");
	    		WebMailController webmail = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
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
	
    private void login() {
		System.out.println("LoginController -> login");
		this.mailUser = Utils.getAuthPrincipal();
   		System.out.println(">>>>>>>>>>>>>>>>>> user.getShortName " + mailUser.getShortName());
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
    
	private static String LOGIN_SUCCESS = AonConstants.NAVIGATION_FOLDER;
	private static String LOGIN_ERROR = AonConstants.NAVIGATION_LOGIN;
	
}
