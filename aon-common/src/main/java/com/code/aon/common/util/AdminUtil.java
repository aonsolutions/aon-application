package com.code.aon.common.util;

import java.security.MessageDigest;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.jaas.auth.AuthPrincipal;

public class AdminUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(AdminUtil.class);
	
	private static final String SHA_ALGORITHM = "SHA";
	

	public static Query getQuery( String query ) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		return HibernateUtil.getSession(sessionFactoryName).createQuery(query);		
	}
	
	public static void closeSession() {
		HibernateUtil.closeSession(HibernateUtil.getSessionFactoryName(), false);	
	}
	
	public static Integer getDomainApplication( Integer domain, Integer application ) {
		Integer domainApplication = null;
		try {
			Query query = getQuery("SELECT id FROM DomainApplication da WHERE da.active = true and da.domain = ? and da.application = ?");
			query.setInteger(0, domain).setInteger(1, application);
			domainApplication = (Integer) query.uniqueResult();			
		} finally {
			closeSession();
		}
		return domainApplication;
	}
		
	private static Integer getApplicationUserEx( Integer domain, Integer user, Integer application ) {
		Integer domainApplication = getDomainApplication(domain, application);
		if ( domainApplication != null ) {
			try {
				Query query = getQuery("SELECT id FROM ApplicationUser au WHERE au.active = true and au.domainApplication = ? and au.user = ?");
				query.setInteger(0, domainApplication).setInteger(1, user);
				return (Integer) query.uniqueResult();								
			} finally {
				closeSession();
			}
		}
		return null;
	}

	public static Integer getApplicationUser( Integer userDomain, Integer user, Integer application ) {
		Integer applicationUser = getApplicationUserEx(DomainManager.getCurrentDomain(), user, application);
		if (applicationUser == null ) {
			applicationUser = getApplicationUserEx(userDomain, user, application);
		}
		return applicationUser;
	}
	
	public static Integer getApplicationUser( AuthPrincipal principal ) {
		return getApplicationUser(principal.getUserDomainId(), principal.getUserId(), principal.getApplicationId());
	}
	
	@SuppressWarnings("unchecked")
	public static List<Integer> getProfiles( Integer applicationUser ) {
		List<Integer> profiles = null;
		try {
			Query query = getQuery("SELECT profile.id FROM ApplicationUserProfile aup WHERE aup.applicationUser = ?");
			query.setInteger(0, applicationUser);
			profiles = query.list();			
		} finally {
			closeSession();
		}
		return profiles;
	}

	@SuppressWarnings("unchecked")
	public static List<String> getProfileRoles( Integer profile ) {
		List<String> profileRoles = null;
		try {
			Query query = getQuery("SELECT pr.applicationRole.role.name FROM ProfileRole pr WHERE pr.profile = ?");
			query.setInteger(0, profile );
			profileRoles = query.list();			
		} finally {
			closeSession();	
		}
		return profileRoles;
	}

	public static String encodeSHA( String value ) {
		String shaPassword = null;
		try {
			byte[] hash = MessageDigest.getInstance(SHA_ALGORITHM).digest(value.getBytes());
			shaPassword = new String( Base64.encodeBase64(hash) );
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}        		
		return shaPassword;
	}	
	
	public static String getUserPassword( Integer user ) {
		String password = null;
		try {
			Query query = getQuery("SELECT password FROM User u WHERE u.id = ?");
			query.setInteger(0, user);
			password = (String) query.uniqueResult();			
		} finally {
			closeSession();	
		}
		return password;
	}

	public static Integer getParentDomain( Integer domainId ) {
		Integer parentDomain = null;
		try {
			Query query = getQuery("SELECT parent.id FROM Domain d WHERE d.id = ?");
			query.setInteger(0, domainId);
			parentDomain = (Integer) query.uniqueResult();
		} finally {
			closeSession();	
		}
		return parentDomain;
	}

	public static Integer getAdminDomain() {
		Integer adminDomain = null;
		try {
			Query query = getQuery("SELECT id FROM Domain d WHERE d.type = 5");
			query.setMaxResults(1);
			adminDomain = (Integer) query.uniqueResult();			
		} finally {
			closeSession();	
		}
		return adminDomain;
	}

	public static Integer getCompanyId( Integer domainId ) {
		Integer companyId = null;
		try {
			Query query = getQuery("SELECT id FROM Company c WHERE c.domain = ?");
			query.setInteger(0, domainId);
			companyId = (Integer) query.uniqueResult();			
		} finally {
			closeSession();	
		}
		return companyId;
	}

	@SuppressWarnings("unchecked")
	public static String getDomainName(int domainId) {
		String stmt = "SELECT name FROM domain as domain WHERE domain.id = :domainId";
		String domainName = null;
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		try {
			Session session = HibernateUtil.getSession(sessionFactoryName);
			SQLQuery query = session.createSQLQuery(stmt);
			query.setInteger("domainId", domainId);
			List<Object> list = query.list();
			domainName = !list.isEmpty() ? query.list().get(0).toString() : null;			
		} finally {
			closeSession();	
		}
		return domainName;
	}

	@SuppressWarnings("unchecked")
	public static String getDomainDescription(int domainId) {
		String stmt = "SELECT description FROM domain as domain WHERE domain.id = :domainId";
		String domainDescription = null;
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		try {
			Session session = HibernateUtil.getSession(sessionFactoryName);
			SQLQuery query = session.createSQLQuery(stmt);
			query.setInteger("domainId", domainId);
			List<Object> list = query.list();
			domainDescription = !list.isEmpty() ? query.list().get(0).toString() : null;			
		} finally {
			closeSession();	
		}
		return domainDescription;
	}
	
	@SuppressWarnings("unchecked")
	public static Integer getDomainType(int domainId) {
		String stmt = "SELECT type FROM domain as domain WHERE domain.id = :domainId";
		Integer domainType = null;
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		try {
			Session session = HibernateUtil.getSession(sessionFactoryName);
			SQLQuery query = session.createSQLQuery(stmt);
			query.setInteger("domainId", domainId);
			List<Object> list = query.list();
			domainType = !list.isEmpty() ? ((Byte)query.list().get(0)).intValue() : null;			
		} finally {
			closeSession();	
		}
		return domainType;
	}

}