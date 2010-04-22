package com.code.aon.ui.payroll.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.Name;
import javax.servlet.http.HttpSession;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;

public class PayrollController implements ILdapConstants, IAonObjectClasses {

	private static final DateFormat FORMATTER = new SimpleDateFormat("EEEE, dd MMMM yyyy");

	private static final String ORGANIZATION_NAME_ATTRIBUTE = "o";

	private Entry aonUser;
	private boolean isMainMenuEnabled = true;
	
    public String getCurrentDate() {
        return FORMATTER.format(new Date()).toUpperCase();
    }
    
	public boolean isMainMenuEnabled() {
		return isMainMenuEnabled;
	}

	public void setMainMenuEnabled(boolean isMainMenuEnabled) {
		this.isMainMenuEnabled = isMainMenuEnabled;
	}

	private Entry getAonUser( AuthPrincipal principal ) {
		BasicLdap ldap = new BasicLdap();
		Name dn = NameResolver.getUserDN(principal.getDomain(), principal.getShortName());
		return ldap.get(dn, USER);
	}		
    
    private Entry getAonUser() {
    	if ( this.aonUser == null ) {
    		this.aonUser = getAonUser(Utils.getAuthPrincipal());
    	}
    	return this.aonUser;
    }
    
    public String getCompanyName(){
    	if (getAonUser().containsKey(ORGANIZATION_NAME_ATTRIBUTE) ) {
    		return getAonUser().getAsString(ORGANIZATION_NAME_ATTRIBUTE);
    	}    	
    	return null;
    }

    public String getLoggedUserName() {
    	String userName = getAonUser().getAsString(COMMON_NAME_ATTRIBUTE);
    	if (getAonUser().containsKey(SURNAME_ATTRIBUTE) ) {
    		userName += " " + getAonUser().getAsString(SURNAME_ATTRIBUTE);
    	}
        return userName;
    }    

    public void logout( ActionEvent event ) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
    	session.invalidate();    	
    }

	public InputStream getLogo() throws IOException {
		return this.getClass().getResourceAsStream( "/com/code/aon/ui/payroll/logo.jpg" );
	}
}
