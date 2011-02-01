package com.code.aon.ui.manager.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.AccessPolicy;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.Domain;
import com.code.aon.manager.DomainApplication;
import com.code.aon.manager.enumeration.AccessPolicyType;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;

public class DomainController extends LdapBasicController implements IAonObjectClasses, IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainController.class);
	
	private final static int DEFAULT_DOMAIN_NAME_MAX_LENGTH = 128;
	
	private AccessPolicy accessPolicy;
	
	private List<SelectItem> accessPolicies;
	
	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}	
	
	public int getDomainNameMaxLength() {
		return DEFAULT_DOMAIN_NAME_MAX_LENGTH;
	}
	
	public AccessPolicy getAccessPolicy() {
		return accessPolicy;
	}

	public void setAccessPolicy(AccessPolicy accessPolicy) {
		this.accessPolicy = accessPolicy;
	}
	
	@SuppressWarnings("unchecked")
	public List<Domain> getDomains() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}
	
	public Domain getDomain() {
		return (Domain) getTo();
	}

	protected String getInvalidMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, DOMAIN_INVALID_NAME, name);
	}
	
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, DOMAIN_DUPLICATED_NAME, name);
	}	

	private BasicManagerBean getAccessPolicyManagerBean( String domain ) {
		LdapDAO dao = new LdapDAO(AccessPolicy.class);
		Name baseDN = NameResolver.getDomainDN(domain);
		dao.setBaseDN( baseDN );			
		return new BasicManagerBean(dao);
	}		
	
	public List<SelectItem> getAccessPolicies() throws ManagerBeanException {
		if ( accessPolicies == null) {
			Locale locale = AonUtil.getCurrentLocale();
			this.accessPolicies = new LinkedList<SelectItem>();
			for (AccessPolicyType type : AccessPolicyType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type.getName(), name );
				accessPolicies.add(item);
			}			
		}
		return accessPolicies;
	}	
	
	public void insertOrUpdateAccessPolicy() throws ManagerBeanException {
		BasicManagerBean bean = getAccessPolicyManagerBean( getDomain().getCommonName() );
		bean.insertOrUpdate(accessPolicy);
	}

	public void initAccessPolicy() throws ManagerBeanException {
		this.accessPolicy = new AccessPolicy();
		if (! isNew() ) {
			BasicManagerBean bean = getAccessPolicyManagerBean( getDomain().getCommonName() );
			List<ITransferObject> list = bean.getList(null);
			if (! list.isEmpty() ) {
				this.accessPolicy = (AccessPolicy) list.get(0);
			}
		}
	}
	
	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	public void removeDomain( Domain domain ) {
		BasicLdap ldap = null;
		try {
			Properties properties = (Properties) BasicLdap.getLdapProperties().clone();
			properties.put(Context.REFERRAL, "ignore");
			ldap = new BasicLdap( properties );
			Name dn = domain.getId();
			if ( ldap.exists(dn, DOMAIN) ) {
				ldap.getLdapSession().deleteDepth(dn, true);	
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e);
		} finally {
			ldap.closeSession();
		}
	}	
	
	public void createOrganizationalUnits( Domain domain ) {
		BasicLdap ldap = new BasicLdap();
		String domainName = domain.getCommonName();
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

	public DBConnnection createAndRegister( Domain domain ) throws ManagerBeanException {
		DBConnnection dbc = new DBConnnection();
		DomainDBConnectionController ddbcc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		ddbcc.init( dbc, domain.getCommonName() );
		ddbcc.getManagerBean().insert(dbc);
		getManager().createDB(dbc);
		return dbc;
	}
	
	public void registerApplication( String name, DBConnnection dataSource ) throws ManagerBeanException {
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		DomainApplication application = new DomainApplication();
		application.setCommonName(name);
		application.setDataSource(dataSource);
		dac.getManagerBean().insert(application);
		dac.createOrganizationalUnits(application);
	}
	
}