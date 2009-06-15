package com.code.aon.desktop.controller;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.naming.Context;
import javax.naming.Name;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
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
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.desktop.applications.ApplicationsManager;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DomainController extends BasicController implements IDesktopConstants, IAonObjectClasses {

	private static final Logger LOGGER = Logger.getLogger(DomainController.class.getName());
	
	private String currentDomain;
	
	private String domainSuffix;
	
	private LdapDAO dbConnectionDAO;
	
	private BasicManagerBean dbConnectionManagerBean;

	private LdapDAO domainApplicationDAO;
	
	private BasicManagerBean domainApplicationManagerBean;
	
	private BasicManagerBean ldapManagerBean;
	
	private DBManager manager = new DBManager();
	
	private ApplicationsManager.App selectedApplication;

	public DomainController() {
		AonUserController auc = (AonUserController) AonUtil.getRegisteredBean(CURRENT_USER_CONTROLLER_NAME);
		this.currentDomain = auc.getDomain();
		String[] parts = StringUtils.split(this.currentDomain, ".");
		if ( ArrayUtils.getLength(parts) > 2 ) {
			this.domainSuffix = parts[parts.length-2] + "." + parts[parts.length-1]; 
		} else {
			this.domainSuffix = this.currentDomain;
		}
		manager = new DBManager();
	}

	public ApplicationsManager.App getSelectedApplication() {
		return selectedApplication;
	}

	public void setSelectedApplication(ApplicationsManager.App selectedApplication) {
		this.selectedApplication = selectedApplication;
	}
	
	public static String getCMSDomainURL( String domain ) {
		StringBuffer url = new StringBuffer( "http://cms" );
		String[] parts = StringUtils.split(domain, ".");
		if ( ArrayUtils.getLength(parts) > 2 ) {
			for( String part : parts ) {
				url.append(".").append(part);
			}
		} else {
			url.append(".").append(domain);
		}
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		if ( request.getRemotePort() != 80 ) {
			url.append( ":" ).append( String.valueOf(request.getLocalPort()) );
		}		
		return url.toString();
	}

	public String getCurrentDomainApplicationURL() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getModel().getRowData();
			if ( ApplicationsManager.AON_CMS.equals(selectedApplication.getId()) ) {
				return getCMSDomainURL(domain.getCommonName());
			} else {
				StringBuffer url = new StringBuffer( "http://" );
				url.append( domain.getCommonName() );
				FacesContext context = FacesContext.getCurrentInstance();
				HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
				if ( request.getRemotePort() != 80 ) {
					url.append( ":" ).append( String.valueOf(request.getLocalPort()) );
				}
				url.append( selectedApplication.getContext() );
				return url.toString();				
			}
		}
		return null;
	}
	
	public String getCurrentDomain() {
		return currentDomain;
	}
	
	public String getDomainSuffix() {
		return domainSuffix;
	}

	public void domainNameCheck(FacesContext context, UIComponent component, Object value) {
		String domainName = value.toString();
		if (! domainName.matches("[a-zA-Z][a-zA-Z0-9]*") ) {
			String summary = AonUtil.getMessage("appBundle", "desktop_domain_invalid_name");
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

	public BasicManagerBean getAccessPolicyManagerBean( String domain ) {
		LdapDAO dao = new LdapDAO(AccessPolicy.class);
		Name baseDN = NameResolver.getDomainDN(domain);
		dao.setBaseDN( baseDN );			
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
	
	private void addOrganizationUnit( Name dn ) {
		BasicLdap ldap = new BasicLdap();
		try {
			Entry entry = new Entry(dn);
			entry.addObjectClasses(new String[]{TOP, ORGANIZATIONAL_UNIT});
			entry.put( ILdapConstants.ORGANIZATIONAL_UNIT_NAME_ATTRIBUTE, NameResolver.getFirstValue(dn) );
			LOGGER.info( "Add Organization Unit entry: " + dn );
			ldap.getLdapSession().add(entry);
		} catch ( LdapException e ) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			ldap.closeSession();
		}
	}
	
	private void addReferral( Name dn, Name ref, String objectClass ) {
		BasicLdap ldap = new BasicLdap();
		try {
			if ( ldap.exists(ref, objectClass) ) {
				LdapSession session = ldap.getLdapSession();		
				Entry entry = new Entry(dn);
				entry.addObjectClasses(new String[]{TOP, REFERRAL, EXTENSIBLE_OBJECT});
				entry.put( ILdapConstants.ORGANIZATIONAL_UNIT_NAME_ATTRIBUTE, NameResolver.getFirstValue(dn) );
				String preffix = StringUtils.substringBeforeLast(ldap.getProperties().getProperty(Context.PROVIDER_URL), "/" );
				String url = preffix + "/" + session.getFullDN(ref);
				entry.put( ILdapConstants.REF_ATTRIBUTE, url );
				LOGGER.info( "Adding referral " + dn + " ->" + url );
				ldap.getLdapSession().add(entry);
			} else {
				LOGGER.severe( "Can't make a Referreal, ref doesn't exist: " + ref );
			}
		} catch ( LdapException e ) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			ldap.closeSession();
		}
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
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			ldap.closeSession();
		}
		try {
			DBConnnection dbc = calculateDBConnection(domain);
			if ( manager.exists(dbc) ) {
				manager.dropDB(dbc);
			}
		} catch ( Throwable e ) {
			LOGGER.log(Level.SEVERE, "Error removing db of " + domain + ", " + e.getMessage(), e);
		}
	}	
	
	public DBConnnection getDBConnection( String domain ) throws ManagerBeanException {
		Name bdsDN = NameResolver.getDomainBDsDN(domain);
		IManagerBean bean = getDBConnnectionManagerBean();
		this.dbConnectionDAO.setBaseDN(bdsDN);
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
	
	private DBConnnection calculateDBConnection( String domain ) throws ManagerBeanException {
		DBConnnection newDBConnection = (DBConnnection) getDBConnection(getCurrentDomain()).clone();
		newDBConnection.setId(null);
		newDBConnection.setCommonName(DBManager.AON_MASTER);
		String bdName = domain.replace('.', '-');
		String url = newDBConnection.getLabeledURI();
		String newUrl = StringUtils.substringBeforeLast(url, "/") + "/" + bdName;
		String suffix = StringUtils.substringAfter(url, "?");
		if (! StringUtils.isEmpty(suffix) ) {
			newUrl += "?" + suffix;
		}
		newDBConnection.setLabeledURI(newUrl);
		return newDBConnection;
	}
	
	public DBConnnection createDB( String domain ) throws AonException {
		Name bdsDN = NameResolver.getDomainBDsDN(domain);
		addOrganizationUnit(bdsDN);
		DBConnnection newDBConnection = calculateDBConnection( domain );
		this.dbConnectionDAO.setBaseDN(bdsDN);
		getDBConnnectionManagerBean().insert(newDBConnection);
		manager.createDB(newDBConnection);
		return newDBConnection;
	}
	
	@SuppressWarnings("unchecked")
	private List<DomainApplication> getCurrentDomainApplications() throws ManagerBeanException {
		Name dn = NameResolver.getDomainApplicationsDN(getCurrentDomain());
		IManagerBean bean = getDomainApplicationManagerBean();
		this.domainApplicationDAO.setBaseDN(dn);
		return (List) bean.getList(null);
	}
	
	private DomainApplication getDomainApplication( DomainApplication domainApplication, DBConnnection dbConnection, String domain ) {
		DomainApplication newDomainApplication = (DomainApplication) domainApplication.clone();
		newDomainApplication.setId(null);
		newDomainApplication.setDataSource(dbConnection);
		return newDomainApplication;
	}	
	
	public void createApplications( DBConnnection dbConnection, String domain ) throws ManagerBeanException {
		Name dasDN = NameResolver.getDomainApplicationsDN(domain);
		addOrganizationUnit(dasDN);
		List<DomainApplication> list = getCurrentDomainApplications();
		this.domainApplicationDAO.setBaseDN(dasDN);
		for( DomainApplication da : list ) {
			DomainApplication newDA = getDomainApplication(da, dbConnection, domain);
			getDomainApplicationManagerBean().insert(newDA);
			// Profiles
			Name currentProfilesDN = NameResolver.getDomainApplicationProfilesDN(getCurrentDomain(), newDA.getCommonName());
			Name profilesDN = NameResolver.getDomainApplicationProfilesDN(domain, newDA.getCommonName());
			addReferral(profilesDN, currentProfilesDN, ORGANIZATIONAL_UNIT);
			// Users
			Name currentUsersDN = NameResolver.getDomainApplicationUsersDN(getCurrentDomain(), newDA.getCommonName());
			Name usersDN = NameResolver.getDomainApplicationUsersDN(domain, newDA.getCommonName());
			addReferral(usersDN, currentUsersDN, ORGANIZATIONAL_UNIT);
		}
	}
	
	private SessionFactory getSessionFactory( DBConnnection dbConnection ) {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.setProperties( dbConnection.getHibernateProperties() );
   		configuration.configure();    			
   		configuration.buildMappings();
		return configuration.buildSessionFactory();
	}
	
	@SuppressWarnings("unchecked")
	private void replicateUsers( DBConnnection dbc ) throws AonException {
		IManagerBean userBean = BeanManager.getManagerBean(User.class);
		List<ITransferObject> users = userBean.getList(null);
		replicateUsers(dbc, (List) users);
	}

	public void replicateUsers( DBConnnection dbc, List<User> users ) throws AonException {
		LOGGER.info( "Replicating users in " + dbc );
		SessionFactory factory = getSessionFactory(dbc);
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
		Name currentUsersDN = NameResolver.getUsersDN(getCurrentDomain());
		Name usersDN = NameResolver.getUsersDN(domain);
		addReferral(usersDN, currentUsersDN, ORGANIZATIONAL_UNIT);
	}
	
	private void synchronize( Domain domain ) {
		
	}
	
	public void synchronize( ActionEvent event ) throws ManagerBeanException {
		IManagerBean bean = getManagerBean();
		Criteria criteria = new Criteria();
		String parentDomain = bean.getFieldName(IDesktopAlias.DOMAIN_PARENT_DOMAIN);
		criteria.addNotNullExpression(parentDomain);
		for( ITransferObject to : bean.getList(criteria) ) {
			synchronize( (Domain) to );
		}
	}
	
}