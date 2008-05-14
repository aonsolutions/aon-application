package com.code.aon.ui.webmail.controller;

import javax.faces.event.AbortProcessingException;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;

public class LoginController extends BasicLdap implements ILdapConstants {

	private static final String ORGANIZATION_NAME_ATTRIBUTE = "o";

	private AuthPrincipal mailUser;
	
	private Entry aonUser;

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
	
	private Entry getAonUser( AuthPrincipal principal ) {
		Entry entry = null;
		try {
			LdapSession session = getLdapSession();
			DistinguishedName dn = AonDN.getUserDN(principal.getDomain(), principal.getShortName());
			String filter = LdapSession.getObjectClass("aonUser");
			entry = session.get(dn.toString(), filter);
		} catch ( LdapException e ) {
			throw new AbortProcessingException( "Error getting aonUser for " + principal + ". " + e.getMessage(), e );
		} finally {
			closeSession();
		}		
		return entry;
	}	
	
    private void login() {
		System.out.println("LoginController -> login");
		this.mailUser = Utils.getAuthPrincipal();
   		System.out.println(">>>>>>>>>>>>>>>>>> user.getShortName " + mailUser.getShortName());
   		this.aonUser = getAonUser(this.mailUser);
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
	
    public String getCompanyName(){
    	if (this.aonUser.containsKey(ORGANIZATION_NAME_ATTRIBUTE) ) {
    		return this.aonUser.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
    	}    	
    	return null;
    }

    public String getLoggedUserName() {
    	String userName = this.aonUser.getAsString(COMMON_NAME_ATTRIBUTE);
    	if (this.aonUser.containsKey(SURNAME_ATTRIBUTE) ) {
    		userName += " " + this.aonUser.getAsString(SURNAME_ATTRIBUTE);
    	}
        return userName;
    }

}
