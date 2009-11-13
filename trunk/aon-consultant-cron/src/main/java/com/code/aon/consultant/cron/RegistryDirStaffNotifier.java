package com.code.aon.consultant.cron;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Name;

import org.apache.commons.lang.time.StopWatch;
import org.hibernate.cfg.Environment;
import org.jboss.varia.scheduler.Schedulable;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.User;
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

public class RegistryDirStaffNotifier implements Schedulable, ILdapConstants, IAonObjectClasses {

	private static final Logger LOGGER = Logger.getLogger(RegistryDirStaffNotifier.class.getName());
	
	private static final String AON_CONSULTANT = "aon-consultant";
	
	private static final String AON_ADMIN_PROFILE = "AonAdmin";
	
	private BasicLdap ldap;
	
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
	
	private void checkRdirStaffs( Properties properties ) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT id,name,due_date  FROM rdir_staff");
			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			int margin = 4;// margen de dias antes de la fecha de vencimiento
			int numAlams = 0;

			while (rs.next()) {
				System.out.print(rs.getInt(1) + " " + rs.getString(2) + " "	+ rs.getDate(3));
			
				GregorianCalendar today = new GregorianCalendar();
				GregorianCalendar regDate = new GregorianCalendar();
				regDate.setTime(rs.getDate(3));
				int days1 = 0;
				int days2 = 0;
				int maxYear = Math.max(today.get(Calendar.YEAR), regDate.get(Calendar.YEAR));
				GregorianCalendar gctmp = (GregorianCalendar) today.clone();
				for (int f = gctmp.get(Calendar.YEAR); f < maxYear; f++) {
					days1 += gctmp.getActualMaximum(Calendar.DAY_OF_YEAR);
					gctmp.add(Calendar.YEAR, 1);
				}
				gctmp = (GregorianCalendar) regDate.clone();
				for (int f = gctmp.get(Calendar.YEAR); f < maxYear; f++) {
					days2 += gctmp.getActualMaximum(Calendar.DAY_OF_YEAR);
					gctmp.add(Calendar.YEAR, 1);
				}
				days1 += today.get(Calendar.DAY_OF_YEAR) - 1;
				days2 += regDate.get(Calendar.DAY_OF_YEAR) - 1;
				int days = days2 - days1;				
				User user = new User();
				user.setId(30);
				
				if (days >= 0 && days < margin) {
					System.out.println("Fin de contrato de " + rs.getString(2)+ " el día  " + rs.getDate(3));
					IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
					IManagerBean noticeBean = BeanManager.getManagerBean(Notice.class);
					Alarm alarm = new Alarm();
					Notice notice = new Notice();
					notice.setType(NoticeType.COMMUNICATION);
					notice.setStatus(NoticeStatus.CALL);
					notice.setDate(today.getTime());
					notice.setSender(user);
					notice.setSource("Servidor");
					notice.setPriority(Priority.NORMAL);
					notice.setRecipient(user);
					notice.setSubject("Fin de contrato de " + rs.getString(2)+ " el día  " + rs.getDate(3));
					noticeBean.insert(notice);

					alarm.setAlarmDate(today.getTime());
					alarm.setUser(user);
					alarm.setSource(AlarmSource.NOTICE);
					notice.setStatus(NoticeStatus.CALL);
					alarm.setStatus(AlarmStatus.PENDING);
					alarm.setPriority(Priority.NONE);
					alarm.setDescription("Fin de contrato de "+ rs.getString(2) + " el día  " + rs.getDate(3));
					alarmBean.insert(alarm);
					numAlams++;
				}

				if (days < 0) {
					System.out.println("Finalizó el contrato de "+ rs.getString(2) + " el día  " + rs.getDate(3));
					IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
					IManagerBean noticeBean = BeanManager.getManagerBean(Notice.class);
					Alarm alarm = new Alarm();
					Notice notice = new Notice();
					notice.setType(NoticeType.COMMUNICATION);
					notice.setStatus(NoticeStatus.CALL);
					notice.setSender(user);
					notice.setDate(today.getTime());
					notice.setSource("Servidor");
					notice.setPriority(Priority.HIGH);
					notice.setRecipient(user);
					notice.setSubject("Finalizó el contrato de "+ rs.getString(2) + " el día  " + rs.getDate(3));
					noticeBean.insert(notice);
					
					alarm.setAlarmDate(today.getTime());
					alarm.setUser(user);
					alarm.setSource(AlarmSource.NOTICE);
					alarm.setStatus(AlarmStatus.PENDING);
					alarm.setPriority(Priority.HIGH);
					alarm.setDescription("Finalizó el contrato de "+ rs.getString(2) + " el día  " + rs.getDate(3));
					alarmBean.insert(alarm);

					numAlams++;
				}
				
			}

			System.out.println("----------->" + numAlams+ " alarmas insertadas");

		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public void perform(Date pTimeOfCall, long pRemainingRepetitions) {
		StopWatch sw = new StopWatch();
		sw.start();
		LOGGER.info( "Starting RegistryDirStaffNotifier: " + pTimeOfCall );
		try {
			for( String domain : getDomains() ) {
				if ( hasApplicationRegistered(domain, AON_CONSULTANT) ) {
					List<String> users = getUsers( domain, AON_CONSULTANT, AON_ADMIN_PROFILE );
					if (! users.isEmpty() ) {
						Name dataSource = getDataSource(domain, AON_CONSULTANT);
						if ( dataSource != null ) {
							Properties properties = getDBConnectionsProperties(dataSource);
							// checkRdirStaffs( properties );						
						}						
					}
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

	
}
