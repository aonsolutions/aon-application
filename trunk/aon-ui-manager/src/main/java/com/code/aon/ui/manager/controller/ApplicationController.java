package com.code.aon.ui.manager.controller;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.Application;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

import static com.code.aon.ldap.IAonObjectClasses.*;

public class ApplicationController extends BasicController implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);
	
	private BasicManagerBean ldapManagerBean;
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			LdapDAO dao = new LdapDAO(Application.class);
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}	
	
	public Application getApplication() {
		return (Application) getTo();
	}
	
	@SuppressWarnings("unchecked")
	public List<Application> getApplications() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}
	
	public void createOrganizationalUnits( String application ) {
		BasicLdap ldap = new BasicLdap();
		Name rolesDN = NameResolver.getApplicationRolesDN(application);
		if (! ldap.exists(rolesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(rolesDN);
		}
		Name profilesDN = NameResolver.getApplicationProfilesDN(application);
		if (! ldap.exists(profilesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(profilesDN);
		}
	}	
	
	private boolean exists( String name ) {
		try {
			for( Application app : getApplications() ) {
				if ( StringUtils.equals(name, app.getCommonName()) ) {
					return true;
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}	

	public void dbConnectionNameCheck(FacesContext context, UIComponent component, Object value) {
		String name = value.toString();
		if (! name.matches("[a-zA-Z][a-zA-Z0-9_-]*") ) {
			String summary = AonUtil.getMessage(BUNDLE_NAME, APPLICATION_INVALID_NAME);
			throw new ValidatorException( new FacesMessage(summary) );
		}
		if ( exists(name) ) {
			String summary = AonUtil.getMessage(BUNDLE_NAME, APPLICATION_DUPLICATED_NAME, name);
			throw new ValidatorException( new FacesMessage(summary) );			
		}
	}	
}