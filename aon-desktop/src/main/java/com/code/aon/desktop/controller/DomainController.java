package com.code.aon.desktop.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.code.aon.common.AonException;
import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.desktop.AccessPolicy;
import com.code.aon.desktop.DBConnnection;
import com.code.aon.desktop.Domain;
import com.code.aon.desktop.DomainApplication;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.desktop.dao.DBManager;
import com.code.aon.desktop.dao.IDesktopAlias;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

public class DomainController extends BasicController implements IDesktopConstants, IAonObjectClasses {

	private static final Logger LOGGER = Logger.getLogger(DomainController.class.getName());
	
	private LdapDAO dbConnectionDAO;
	
	private BasicManagerBean dbConnectionManagerBean;

	private LdapDAO domainApplicationDAO;
	
	private BasicManagerBean domainApplicationManagerBean;
	
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

	public BasicManagerBean getDomainApplicationManagerBean() {
		if ( this.domainApplicationManagerBean == null ) {
			this.domainApplicationDAO = new LdapDAO(DomainApplication.class);		
			this.domainApplicationManagerBean = new BasicManagerBean(this.domainApplicationDAO);			
		}
		return this.domainApplicationManagerBean;
	}	
	
	private String getCurrentDomain() {
		/*
		AonUserController auc = (AonUserController) AonUtil.getRegisteredBean(CURRENT_USER_CONTROLLER_NAME);
		return auc.getDomain();
		*/
		return "inetserver.net";
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
	
	private void addReferral( DistinguishedName dn, DistinguishedName ref ) {
		BasicLdap ldap = new BasicLdap();
		try {
			LdapSession session = ldap.getLdapSession();
			Entry entry = new Entry(dn.toString());
			entry.addObjectClasses(new String[]{TOP, REFERRAL, EXTENSIBLE_OBJECT});
			entry.put( ILdapConstants.ORGANIZATIONAL_UNIT_NAME_ATTRIBUTE, dn.getLevelValue(0) );
			String preffix = StringUtils.substringBeforeLast(ldap.getProperties().getProperty(Context.PROVIDER_URL), "/" );
			String url = preffix + "/" + session.getFullDN(ref);
			entry.put( ILdapConstants.REF_ATTRIBUTE, url );
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
		criteria.addEqualExpression(bean.getFieldName(IDesktopAlias.DB_CONNECTION_COMMON_NAME), DBManager.AON_MASTER);
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
		newDBConnection.setCommonName(DBManager.AON_MASTER);
		String bdName = domain.replace('.', '-');
		String url = dbConnection.getLabeledURI();
		String newUrl = StringUtils.substringBeforeLast(url, "/") + "/" + bdName;
		String suffix = StringUtils.substringAfter(url, "?");
		if (! StringUtils.isEmpty(suffix) ) {
			newUrl += "?" + suffix;
		}
		newDBConnection.setLabeledURI(newUrl);
		return newDBConnection;
	}
	
	private void createDB( DBConnnection dbConnection ) {
		DBManager manager = new DBManager();
		try {
			manager.createDB(dbConnection);
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);			
		}
	}
	
	public DBConnnection createDB( String domain ) throws ManagerBeanException {
		DistinguishedName bdsDN = AonDN.getDomainBDsDN(domain);
		addOrganizationUnit(bdsDN);
		DBConnnection currentDBConnection = getCurrentDBConnection();
		DBConnnection newDBConnection = getDBConnection( currentDBConnection, domain );
		this.dbConnectionDAO.setBaseDN(bdsDN.toString());
		getDBConnnectionManagerBean().insert(newDBConnection);
		createDB(newDBConnection);
		return newDBConnection;
	}
	
	@SuppressWarnings("unchecked")
	private List<DomainApplication> getCurrentDomainApplications() throws ManagerBeanException {
		DistinguishedName dn = AonDN.getDomainApplicationsDN(getCurrentDomain());
		IManagerBean bean = getDomainApplicationManagerBean();
		this.domainApplicationDAO.setBaseDN(dn.toString());
		return (List) bean.getList(null);
	}
	
	private DomainApplication getDomainApplication( DomainApplication domainApplication, DBConnnection dbConnection, String domain ) {
		DomainApplication newDomainApplication = (DomainApplication) domainApplication.clone();
		newDomainApplication.setId(null);
		newDomainApplication.setDataSource(dbConnection);
		return newDomainApplication;
	}	
	
	public void createApplications( DBConnnection dbConnection, String domain ) throws ManagerBeanException {
		DistinguishedName dasDN = AonDN.getDomainApplicationsDN(domain);
		addOrganizationUnit(dasDN);
		List<DomainApplication> list = getCurrentDomainApplications();
		this.domainApplicationDAO.setBaseDN(dasDN.toString());
		for( DomainApplication da : list ) {
			DomainApplication newDA = getDomainApplication(da, dbConnection, domain);
			getDomainApplicationManagerBean().insert(newDA);
			// Profiles
			DistinguishedName currentProfilesDN = AonDN.getDomainApplicationProfilesDN(getCurrentDomain(), newDA.getCommonName());
			DistinguishedName profilesDN = AonDN.getDomainApplicationProfilesDN(domain, newDA.getCommonName());
			addReferral(profilesDN, currentProfilesDN);
			// Users
			DistinguishedName currentUsersDN = AonDN.getDomainApplicationUsersDN(getCurrentDomain(), newDA.getCommonName());
			DistinguishedName usersDN = AonDN.getDomainApplicationUsersDN(domain, newDA.getCommonName());
			addReferral(usersDN, currentUsersDN);
		}
	}
	
	private SessionFactory getSessionFactory( DBConnnection dbConnection ) {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.setProperties( dbConnection.getHibernateProperties() );
   		configuration.configure();    			
   		configuration.buildMappings();
		return configuration.buildSessionFactory();
	}
	
	private void replicateUsers( DBConnnection dbConnection ) throws AonException {
		IManagerBean userBean = BeanManager.getManagerBean(User.class);
		List<ITransferObject> users = userBean.getList(null);
		SessionFactory factory = getSessionFactory(dbConnection);
		Session session = null;
		try {
			session = factory.openSession();
			session.beginTransaction();
			for( ITransferObject user : users ) {
				session.replicate(user, ReplicationMode.EXCEPTION );
			}
			session.getTransaction().commit();
		} catch (HibernateException e) {
			if ( session != null ) {
				session.getTransaction().rollback();	
			}
			throw new AonException( e.getMessage(), e );	
		} finally {
			if ( session != null ) {
				session.close();
			}
		}
	}
	
	public void createUsers( DBConnnection dbConnection, String domain ) throws AonException {
		replicateUsers( dbConnection );
		DistinguishedName currentUsersDN = AonDN.getUsersDN(getCurrentDomain());
		DistinguishedName usersDN = AonDN.getUsersDN(domain);
		addReferral(usersDN, currentUsersDN);
	}

}