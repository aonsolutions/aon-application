package com.code.aon.ui.audit.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;

public class AuditController implements ILdapConstants, IAonObjectClasses {

	private static final DateFormat FORMATTER = new SimpleDateFormat("EEEE, dd MMMM yyyy");

	private static final String ORGANIZATION_NAME_ATTRIBUTE = "o";

	private Entry aonUser;
	
    public String getCurrentDate() {
        return FORMATTER.format(new Date()).toUpperCase();
    }

	private Entry getAonUser( AuthPrincipal principal ) {
		Entry entry = null;
		BasicLdap ldap = new BasicLdap();
		try {
			DistinguishedName dn = AonDN.getUserDN(principal.getDomain(), principal.getShortName());
			String filter = LdapSession.getObjectClass(USER);
			entry = ldap.getLdapSession().get(dn.toString(), filter);
		} catch ( LdapException e ) {
			throw new AbortProcessingException( "Error getting aonUser for " + principal + ". " + e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return entry;
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
    
}
