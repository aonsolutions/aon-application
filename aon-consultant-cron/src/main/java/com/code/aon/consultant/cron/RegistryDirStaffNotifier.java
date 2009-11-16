package com.code.aon.consultant.cron;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Name;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.StopWatch;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.jboss.varia.scheduler.Schedulable;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.DefaultConfigurationFactory;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.hibernate.IConfigurationFactory;
import com.code.aon.common.dao.hibernate.ISessionFactoryNameProvider;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.User;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.Notice;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.NoticeStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.dao.IRegistryAlias;

public class RegistryDirStaffNotifier implements Schedulable, ILdapConstants, IAonObjectClasses {

	private static final Logger LOGGER = Logger.getLogger(RegistryDirStaffNotifier.class.getName());
	
	private static final String AON_CONSULTANT = "aon-consultant";
	
	private static final String AON_ADMIN_PROFILE = "AonAdmin";
	
	private static final int DAYS_MARGIN = 7;
	
	private BasicLdap ldap;
	
	private Date startDate;
	
	private Date today;
	
	private Date dueDate;
	
	public RegistryDirStaffNotifier() {
		Properties ldapProperties = new Properties();
		ldapProperties.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
		ldapProperties.put("java.naming.security.principal", "cn=Manager,o=aondirectory");
		ldapProperties.put("java.naming.provider.url", "ldap://192.168.2.100:389/o=aondirectory");
		ldapProperties.put("java.naming.security.credentials", "GeForce");
		this.ldap = new BasicLdap( ldapProperties );
	}

	private List<String> getDomains() {
		List<String> domains = new LinkedList<String>();
		try {
			LdapSession session = this.ldap.getLdapSession();
			String objectClass = NameResolver.getObjectClass(DOMAIN);
			Name dn = NameResolver.getDomainsDN();
			List<Entry> list = session.search(dn, objectClass, COMMON_NAME_ATTRIBUTE );
			for( Entry entry : list ) {
				String name = entry.getAsString(COMMON_NAME_ATTRIBUTE);
				domains.add( name );
			}
		} catch ( Throwable th ) {
			LOGGER.log(Level.SEVERE, "Error getting domain list", th );
		} finally {
			this.ldap.closeSession();
		}
		return domains;	
	}
	
	private boolean hasApplicationRegistered( String domain, String application ) {
		Name dn = NameResolver.getDomainApplicationDN(domain, application);
		return this.ldap.exists(dn, DOMAIN_APPLICATION);
	}
	
	private Name getDataSource( String domain, String application ) {
		Name dataSource = null;
		Name applicationDN = NameResolver.getDomainApplicationDN(domain, application);
		Entry domainApplication = ldap.get(applicationDN, DOMAIN_APPLICATION, DATA_SOURCE_ATTRIBUTE);
		if ( (domainApplication != null) && domainApplication.containsKey(DATA_SOURCE_ATTRIBUTE) ) {
			String value = domainApplication.getAsString(DATA_SOURCE_ATTRIBUTE);
			Name dn = NameResolver.getName(value);
			if ( (dn != null) && ldap.exists(dn, DB_CONNECTION) ) {
				dataSource = dn;
			}
		}
		return dataSource;
	}	
	
	private Properties getDBConnectionsProperties( Name dataSource ) {
		Properties properties = new Properties();
		Entry ds = ldap.get(dataSource, DB_CONNECTION);
		if ( ds != null ) {		
			properties.put(Environment.USER, ds.getAsString(USER_ID_ATTRIBUTE));
			byte[] password = ds.getAsByteArray(USER_PASSWORD_ATTRIBUTE);
			properties.put(Environment.PASS, new String(password) );
			properties.put(Environment.URL, ds.getAsString(LABELED_URI_ATTRIBUTE));
			properties.put(Environment.DRIVER, ds.getAsString(DRIVER_CLASS_NAME_ATTRIBUTE));
			properties.put(Environment.SHOW_SQL, Boolean.TRUE);
		}
		return properties;		
	}

	private List<String> getUsers( String domain, String application, String profile ) {
		List<String> users = new LinkedList<String>();
		try {
			LdapSession session = this.ldap.getLdapSession();
			Name profileDN = session.getFullDN(NameResolver.getApplicationProfileDN(application, profile));
			String objectClass = NameResolver.getObjectClass(DOMAIN_APPLICATION_USER);
			String expr = NameResolver.getEqualExpression(MEMBER_ATTRIBUTE, profileDN.toString());
			String filter = NameResolver.getAndExpression( objectClass, expr );
			Name dn = NameResolver.getDomainApplicationUsersDN(domain, application);
			List<Entry> list = session.search(dn, filter, COMMON_NAME_ATTRIBUTE );
			for( Entry entry : list ) {
				String name = entry.getAsString(COMMON_NAME_ATTRIBUTE);
				users.add( name );
			}
		} catch ( Throwable th ) {
			LOGGER.log(Level.SEVERE, "Error getting users of domain " + domain, th );
		} finally {
			this.ldap.closeSession();
		}
		return users;	
	}
	
	private void execute( String domain, Properties properties, List<String> users ) {
		ISessionFactoryNameProvider sfnp = HibernateUtil.getSessionFactoryNameProvider();
		IConfigurationFactory cf = HibernateUtil.getConfigurationFactory();
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();		
		SessionFactory sessionFactory = null;
		try {
			HibernateUtil.setSessionFactoryNameProvider( new BasicNameProvider(domain) );
			HibernateUtil.setConfigurationFactory( new BasicConfigurationFactory(properties) );
			HibernateUtil.setBeginTransaction( false );
			HibernateUtil.setCloseSession( false );
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				sessionFactory = HibernateUtil.getSessionFactory(sessionName);
				HibernateUtil.beginTransaction(sessionName);

				checkRdirStaffs( users );
				
				HibernateUtil.commitTransaction(sessionName);
			} catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					LOGGER.log(Level.SEVERE, "Unable to rollback transaction!", th);
				}
			} finally {
				HibernateUtil.closeSession(sessionName);
			}			
		} finally {
			HibernateUtil.setSessionFactoryNameProvider( sfnp );
			HibernateUtil.setConfigurationFactory( cf );
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);			
			if ( (sessionFactory != null) && (! sessionFactory.isClosed()) ) {
				sessionFactory.close();
			}
		}
	}
	
	private List<User> getUsers( List<String> userNames ) throws ManagerBeanException {
		List<User> users = new LinkedList<User>();
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		for( String name : userNames ) {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IConfigAlias.USER_LOGIN), name);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				users.add( (User) list.get(0) );
			}
		}
		return users;
	}
	
	private void checkRdirStaffs( List<String> userNames ) throws ManagerBeanException {
		List<User> users = getUsers(userNames);
		if ( users.isEmpty() ) {
			LOGGER.severe( "Users not found in DB: " + userNames );
			return;
		}
		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
		IManagerBean noticeBean = BeanManager.getManagerBean(Notice.class);
		Criteria criteria = new Criteria();
		String dueDateField = bean.getFieldName(IRegistryAlias.REGISTRY_DIR_STAFF_DUE_DATE);
		criteria.addGreaterThanOrEqualExpression(dueDateField, this.today);
		criteria.addLessThanOrEqualExpression(dueDateField, this.dueDate);
		List<ITransferObject> list = bean.getList(criteria);
		// stmt.append("SELECT id,name,due_date  FROM rdir_staff");
		for( ITransferObject to : list ) {
			RegistryDirStaff dirStaff = (RegistryDirStaff) to;
			String company = dirStaff.getRegistry().getName();
			String description = company + ": Cargo de " + dirStaff.getName() + " caduca el " + dirStaff.getDueDate();
			for( User user : users ) {
				Notice notice = new Notice();
				notice.setType(NoticeType.COMMUNICATION);
				notice.setStatus(NoticeStatus.CALL);
				notice.setDate(this.startDate);
				notice.setSender(user);
				notice.setSource("Servidor");
				notice.setPriority(Priority.NORMAL);
				notice.setRecipient(user);
				notice.setSubject( description );
				noticeBean.insert(notice);

				Alarm alarm = new Alarm();
				alarm.setAlarmDate(this.startDate);
				alarm.setUser(user);
				alarm.setSource(AlarmSource.NOTICE);
				notice.setStatus(NoticeStatus.CALL);
				alarm.setStatus(AlarmStatus.PENDING);
				alarm.setPriority(Priority.NONE);
				alarm.setDescription( description );
				alarmBean.insert(alarm);
			}
		}

		criteria = new Criteria();
		criteria.addLessThanExpression(dueDateField, this.today);
		list = bean.getList(criteria);
		for( ITransferObject to : list ) {
			RegistryDirStaff dirStaff = (RegistryDirStaff) to;
			String company = dirStaff.getRegistry().getName();
			String description = company + ": Cargo caducado de " + dirStaff.getName() + " el " + dirStaff.getDueDate();
			for( User user : users ) {
				Notice notice = new Notice();
				notice.setType(NoticeType.COMMUNICATION);
				notice.setStatus(NoticeStatus.CALL);
				notice.setSender(user);
				notice.setDate(this.startDate);
				notice.setSource("Servidor");
				notice.setPriority(Priority.HIGH);
				notice.setRecipient(user);
				notice.setSubject(description);
				noticeBean.insert(notice);
				
				Alarm alarm = new Alarm();
				alarm.setAlarmDate(this.startDate);
				alarm.setUser(user);
				alarm.setSource(AlarmSource.NOTICE);
				alarm.setStatus(AlarmStatus.PENDING);
				alarm.setPriority(Priority.HIGH);
				alarm.setDescription(description);
				alarmBean.insert(alarm);
			}				
		}
	}
	
	private void init( Date pTimeOfCall ) {
		this.startDate = pTimeOfCall;
		this.today = DateUtils.truncate(pTimeOfCall, Calendar.DATE);
		this.dueDate = DateUtils.addDays( today, DAYS_MARGIN);
	}

	@Override
	public void perform(Date pTimeOfCall, long pRemainingRepetitions) {
		StopWatch sw = new StopWatch();
		sw.start();
		LOGGER.info( "Starting RegistryDirStaffNotifier: " + pTimeOfCall );
		init( pTimeOfCall );
		try {
			for( String domain : getDomains() ) {
				LOGGER.info( "Processing: " + domain );
				if ( hasApplicationRegistered(domain, AON_CONSULTANT) ) {
					List<String> users = getUsers( domain, AON_CONSULTANT, AON_ADMIN_PROFILE );
					if (! users.isEmpty() ) {
						Name dataSource = getDataSource(domain, AON_CONSULTANT);
						if ( dataSource != null ) {
							Properties properties = getDBConnectionsProperties(dataSource);
							execute(domain, properties, users);						
						} else {
							LOGGER.severe( "Domain: " + domain + " have not correct dataSource: " + dataSource );							
						}
					} else {
						LOGGER.fine( "Domain: " + domain + " have not users in " + AON_CONSULTANT + " with profile " + AON_ADMIN_PROFILE );
					}
				} else {
					LOGGER.fine( "Domain: " + domain + " have not registered " + AON_CONSULTANT );
				}
			}
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
		}
		sw.stop();
		LOGGER.info( "Finished RegistryDirStaffNotifier: " + sw.toString() );
	}

	public static void main(String[] args) throws ManagerBeanException {
		RegistryDirStaffNotifier rdsn = new RegistryDirStaffNotifier();
		rdsn.perform( new Date(), -1);
	}

	private class BasicNameProvider implements ISessionFactoryNameProvider {

		private String name;
		
		public BasicNameProvider(String name) {
			this.name = name;
		}

		@Override
		public String getName(String pojoClass) {
			return name;
		}
		
	}
	
	private class BasicConfigurationFactory extends DefaultConfigurationFactory {
		
		private Properties properties;
		
		public BasicConfigurationFactory(Properties properties) {
			this.properties = properties;
		}

		@Override
		protected void completeConfiguration(Configuration configuration) {
			configuration.addProperties(properties);
		}
		
	}
	
}
