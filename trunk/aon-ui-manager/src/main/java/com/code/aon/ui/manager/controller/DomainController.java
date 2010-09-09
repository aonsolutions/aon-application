package com.code.aon.ui.manager.controller;

import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.naming.Context;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.AccessPolicy;
import com.code.aon.manager.Domain;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DomainController extends BasicController implements IAonObjectClasses, IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainController.class);
	
	private final static int DOMAIN_NAME_MAX_LENGTH = 14;
	
	private String currentDomain;
	
	private BasicManagerBean ldapManagerBean;
	
	public DomainController() {
		AuthPrincipal auc = UserUtils.getInstance().getPrincipal();
		this.currentDomain = auc.getDomain();
	}
	
	public String getCurrentDomain() {
		return currentDomain;
	}
	
	public int getDomainNameMaxLength() {
		return DOMAIN_NAME_MAX_LENGTH;
	}

	public void domainNameCheck(FacesContext context, UIComponent component, Object value) {
		String domainName = value.toString();
		if (! domainName.matches("[a-zA-Z][a-zA-Z0-9]*") ) {
			String summary = AonUtil.getMessage(BUNDLE_NAME, DOMAIN_INVALID_NAME);
			throw new ValidatorException( new FacesMessage(summary) );
		}
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			LdapDAO dao = new LdapDAO(Domain.class);
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}

	private BasicManagerBean getAccessPolicyManagerBean( String domain ) {
		LdapDAO dao = new LdapDAO(AccessPolicy.class);
		Name baseDN = NameResolver.getDomainDN(domain);
		dao.setBaseDN( baseDN );			
		return new BasicManagerBean(dao);
	}		
	
	public void addAccessPolicy( String domain ) throws ManagerBeanException {
		BasicManagerBean bean = getAccessPolicyManagerBean(domain);
		AccessPolicy ap = new AccessPolicy();
		ap.setCommonName(IAccessPolicy.ACCESS_POLICY[1]);
		ap.setMaxAllowedUsers(999);
		ap.setMaxDefinedUsers(999);
		ap.setMaxSessions4User(999);
		ap.setExceptionThrowableIfMaximumExceeded(true);
		bean.insert(ap);
	}

	public void removeDomain( String domain, boolean selftDelete ) {
		BasicLdap ldap = null;
		try {
			Properties properties = (Properties) BasicLdap.getLdapProperties().clone();
			properties.put(Context.REFERRAL, "ignore");
			ldap = new BasicLdap( properties );
			Name dn = NameResolver.getDomainDN(domain);
			if ( ldap.exists(dn, DOMAIN) ) {
				ldap.getLdapSession().deleteDepth(dn, selftDelete);	
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e);
		} finally {
			ldap.closeSession();
		}
	}	
	
	public void createOrganizationalUnits( String domainName ) {
		BasicLdap ldap = new BasicLdap();
		Name applicationsDN = NameResolver.getDomainApplicationsDN(domainName);
		if (! ldap.exists(applicationsDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(applicationsDN);
		}
		Name bdsDN = NameResolver.getDomainBDsDN(domainName);
		if (! ldap.exists(bdsDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(bdsDN);
		}
		Name usersDN = NameResolver.getUsersDN(domainName);
		if (! ldap.exists(usersDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(usersDN);
		}
	}	
}