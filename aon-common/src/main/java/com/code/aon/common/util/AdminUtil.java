package com.code.aon.common.util;

import java.security.MessageDigest;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Query;
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
	
	public static Integer getDomainApplication( Integer domain, Integer application ) {
		Query query = getQuery("SELECT id FROM DomainApplication da WHERE da.active = true and da.domain = ? and da.application = ?");
		query.setInteger(0, domain).setInteger(1, application);
		return (Integer) query.uniqueResult();
	}
		
	private static Integer getApplicationUserEx( Integer domain, Integer user, Integer application ) {
		Integer domainApplication = getDomainApplication(domain, application);
		if ( domainApplication != null ) {
			Query query = getQuery("SELECT id FROM ApplicationUser au WHERE au.active = true and au.domainApplication = ? and au.user = ?");
			query.setInteger(0, domainApplication).setInteger(1, user);
			return (Integer) query.uniqueResult();				
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
		Query query = getQuery("SELECT profile.id FROM ApplicationUserProfile aup WHERE aup.applicationUser = ?");
		query.setInteger(0, applicationUser);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public static List<String> getProfileRoles( Integer profile ) {
		Query query = getQuery("SELECT pr.applicationRole.role.name FROM ProfileRole pr WHERE pr.profile = ?");
		query.setInteger(0, profile );
		return query.list();
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
		Query query = getQuery("SELECT password FROM User u WHERE u.id = ?");
		query.setInteger(0, user);
		return (String) query.uniqueResult();		
	}

	public static Integer getParentDomain( Integer domainId ) {
		Query query = getQuery("SELECT parent.id FROM Domain d WHERE d.id = ?");
		query.setInteger(0, domainId);
		return (Integer) query.uniqueResult();
	}

	public static Integer getAdminDomain() {
		Query query = getQuery("SELECT id FROM Domain d WHERE d.type = 5");
		return (Integer) query.uniqueResult();
	}

	public static Integer getCompanyId( Integer domainId ) {
		Query query = getQuery("SELECT id FROM Company c WHERE c.domain = ?");
		query.setInteger(0, domainId);
		return (Integer) query.uniqueResult();
	}
	
}