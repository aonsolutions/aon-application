package com.code.aon.ui.manager.controller;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN;
import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.IAonObjectClasses.USER;
import static com.code.aon.ldap.NameResolver.DOMAINS;
import static com.code.aon.ui.common.ICommonConstants.DOMAIN_RESOLVER_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_WEBMAIL;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.MAIL_ACCOUNT_DUPLICATED;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.SHOW_DOMAIN_MAIL_ACCOUNTS_PROPERTY;

import java.util.LinkedList;
import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.MailAccount;
import com.code.aon.ui.common.controller.DomainResolver;
import com.code.aon.ui.manager.converter.LdapTransferObjectConverter;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IMailAccountController;

public class MailAccountController extends LdapBasicController implements IMailAccountController {

	private final static Logger LOGGER = LoggerFactory.getLogger(MailAccountController.class);
	
	private Converter converter;
	
	private List<SelectItem> domainMailAccounts;
	
	@Override
	public boolean updateBaseDN(Name parent) {
		boolean updated = false;
		String container = NameResolver.getValue(parent, 1);
		if ( StringUtils.equals(container, DOMAINS) ) {
			String domain = NameResolver.getFirstValue(parent);
			updated = updateBaseDN( domain );
		} else {
			String user = NameResolver.getFirstValue(parent);
			String domain = NameResolver.getValue(parent, 2);
			updated = updateBaseDN(domain, user);			
		}
		return updated;
	}
	
	@Override
	protected void initDAO() {
		AuthPrincipal auth = AonUtil.getAuthPrincipal();
		updateBaseDN(auth.getDomain(), auth.getShortName());
	}

	private boolean updateBaseDN( String domain )  {
		Name domainDN = NameResolver.getDomainDN(domain);
		if ( getLdapDAO().exists(domainDN, DOMAIN) ) { 
			Name baseDN = NameResolver.getDomainAccountsDN(domain);
			if (! getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
				getLdapDAO().addOrganizationUnit(baseDN);
			}
			getLdapDAO().setBaseDN( baseDN );
			return true;
		}
		LOGGER.warn( "LDAP entry not found: {}", domainDN );	
		return false;			
	}	
	
	private boolean updateBaseDN( String domain, String user )  {
		Name userDN = NameResolver.getUserDN(domain, user);
		if ( getLdapDAO().exists(userDN, USER) ) { 
			Name baseDN = NameResolver.getUserAccountsDN(domain, user);
			if (! getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
				getLdapDAO().addOrganizationUnit(baseDN);
			}
			getLdapDAO().setBaseDN( baseDN );
			return true;
		}
		LOGGER.warn( "LDAP entry not found: {}", userDN );	
		return false;			
	}		
	
	@Override
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, MAIL_ACCOUNT_DUPLICATED, name);
	}		

	@Override
	public Converter getConverter() {
		if ( converter == null ) {
			this.converter = new LdapTransferObjectConverter(this);			
		}
		return converter;
	}	
	
	public List<SelectItem> getMailAccounts() {
		List<SelectItem> mailAccounts = getUserMailAccounts();
		if ( AonUtil.isBeanValue(BEAN_WEBMAIL, SHOW_DOMAIN_MAIL_ACCOUNTS_PROPERTY) ) {
			mailAccounts.addAll(0, getDomainMailAccounts());
		}
		return mailAccounts;
	}

	private List<SelectItem> loadMailAccountList( boolean enterprise ) throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( ITransferObject to : getManagerBean().getList(getCriteria()) ) {
			MailAccount ma = (MailAccount) to;
			ma.setEnterpriseAccount(enterprise);
			String label = ma.getName() + " (" + ma.getEmail() + ")";
			SelectItem item = new SelectItem(ma, label);
			list.add(item);
		}
		return list;
	}	

	private List<SelectItem> getUserMailAccounts() {
		AuthPrincipal auth = AonUtil.getAuthPrincipal();
		Name dn = NameResolver.getUserDN(auth.getDomain(), auth.getShortName());
		return getMailAccounts(dn, false);
	}
	
	private List<SelectItem> getDomainMailAccounts() {
		if ( domainMailAccounts == null ) {
			DomainResolver domainResolver = (DomainResolver) AonUtil.getRegisteredBean(DOMAIN_RESOLVER_CONTROLLER_NAME);
			Name dn = NameResolver.getDomainDN( domainResolver.getDomain() );
			this.domainMailAccounts = getMailAccounts(dn, true);
		}
		return domainMailAccounts;
	}
	
	private List<SelectItem> getMailAccounts( Name dn, boolean enteprise ) {
		Name oldDN = getLdapDAO().getBaseDN();
		try {
			if ( updateBaseDN( dn ) ) {
				return loadMailAccountList(enteprise);	
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading mail accounts of " + dn, e );
		} finally {
			getLdapDAO().setBaseDN(oldDN);
		}
		return new LinkedList<SelectItem>();
	}
	
}