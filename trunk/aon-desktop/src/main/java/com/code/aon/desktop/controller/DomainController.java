package com.code.aon.desktop.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.desktop.AccessPolicy;
import com.code.aon.desktop.DBConnnection;
import com.code.aon.desktop.Domain;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.desktop.dao.IDesktopAlias;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DomainController extends BasicController implements IDesktopConstants, IAonObjectClasses {

	private static final String AON_MASTER = "aon_master";

	private static final Logger LOGGER = Logger.getLogger(DomainController.class.getName());
	
	private LdapDAO dbConnectionDAO;
	
	private BasicManagerBean dbConnectionManagerBean;
	
	private BasicManagerBean ldapManagerBean;

	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			LdapDAO dao = new LdapDAO(Domain.class);
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}

	public BasicManagerBean getAccessPolicyManagerBean( String domain ) {
		LdapDAO dao = new LdapDAO(AccessPolicy.class);
		DistinguishedName baseDN = AonDN.getDomainDN(domain);
		dao.setBaseDN( baseDN.toString() );			
		return new BasicManagerBean(dao);
	}		

	public BasicManagerBean getDBConnnectionManagerBean() {
		if ( this.dbConnectionManagerBean == null ) {
			this.dbConnectionDAO = new LdapDAO(DBConnnection.class);		
			this.dbConnectionManagerBean = new BasicManagerBean(this.dbConnectionDAO);			
		}
		return this.dbConnectionManagerBean;
	}		
	
	private String getCurrentDomain() {
		AonUserController auc = (AonUserController) AonUtil.getRegisteredBean(CURRENT_USER_CONTROLLER_NAME);
		return auc.getDomain();
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
	
	private void addOrganizationUnit( DistinguishedName dn ) {
		BasicLdap ldap = new BasicLdap();
		try {
			Entry entry = new Entry(dn.toString() );
			entry.addObjectClasses(new String[]{TOP, ORGANIZATIONAL_UNIT});
			entry.put( ILdapConstants.ORGANIZATIONAL_UNIT_NAME_ATTRIBUTE, dn.getLevelValue(0) );
			ldap.getLdapSession().add(entry);
		} catch ( LdapException e ) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			ldap.closeSession();
		}
	}
	
	private DBConnnection getCurrentDBConnection() throws ManagerBeanException {
		DistinguishedName bdsDN = AonDN.getDomainBDsDN(getCurrentDomain());
		IManagerBean bean = getDBConnnectionManagerBean();
		this.dbConnectionDAO.setBaseDN(bdsDN.toString());
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IDesktopAlias.DB_CONNECTION_COMMON_NAME), AON_MASTER);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (DBConnnection) list.get(0);
		}
		list = bean.getList(null);
		if (! list.isEmpty() ) {
			return (DBConnnection) list.get(0);
		}
		return null;
	}
	
	private DBConnnection getDBConnection( DBConnnection dbConnection, String domain ) {
		DBConnnection newDBConnection = (DBConnnection) dbConnection.clone();
		newDBConnection.setId(null);
		newDBConnection.setCommonName(AON_MASTER);
		String bdName = domain.replace(',', '-');
		String url = dbConnection.getLabeledURI();
		String newUrl = StringUtils.substringBeforeLast(url, "/") + "/" + bdName;
		String suffix = StringUtils.substringAfter(url, "?");
		if (! StringUtils.isEmpty(suffix) ) {
			newUrl += "?" + suffix;
		}
		newDBConnection.setLabeledURI(url);
		return newDBConnection;
	}
	
	private void createDB( DBConnnection dbConnection ) {
		
	}
	
	public void createDB( String domain ) throws ManagerBeanException {
		DistinguishedName bdsDN = AonDN.getDomainBDsDN(domain);
		addOrganizationUnit(bdsDN);
		DBConnnection currentDBConnection = getCurrentDBConnection();
		DBConnnection newDBConnection = getDBConnection( currentDBConnection, domain );
		this.dbConnectionDAO.setBaseDN(bdsDN.toString());
		getDBConnnectionManagerBean().insert(newDBConnection);
		createDB(newDBConnection);
	}
	
	public void createApplications( String domain ) {
		DistinguishedName bdsDN = AonDN.getDomainApplicationsDN(domain);
		addOrganizationUnit(bdsDN);
	}
	
	
}