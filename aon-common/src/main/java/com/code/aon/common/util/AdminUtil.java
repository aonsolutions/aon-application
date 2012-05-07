package com.code.aon.common.util;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.Query;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.jaas.auth.AuthPrincipal;

public class AdminUtil {

	public static Query getQuery( String query ) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		return HibernateUtil.getSession(sessionFactoryName).createQuery(query);		
	}
	
	public static Integer getDomainApplication( Integer domain, Integer application ) {
		Query query = getQuery("SELECT id FROM DomainApplication da WHERE da.active = true and da.domain = ? and da.application = ?");
		query.setInteger(0, domain).setInteger(1, application);
		return (Integer) query.uniqueResult();
	}
		
	private static Integer getApplicationUser( Integer domain, Integer user, Integer application ) {
		Integer domainApplication = getDomainApplication(domain, application);
		if ( domainApplication != null ) {
			Query query = getQuery("SELECT id FROM ApplicationUser au WHERE au.active = true and au.domainApplication = ? and au.user = ?");
			query.setInteger(0, domainApplication).setInteger(1, user);
			return (Integer) query.uniqueResult();				
		}
		return null;
	}
	
	public static Integer getApplicationUser( Integer domain ) {
		AuthPrincipal principal = BasicPrincipal.getAuthPrincipal();
		Integer applicationUser = getApplicationUser(domain, principal.getUserId(), principal.getApplicationId());
		if ( (applicationUser == null) && (! ObjectUtils.equals(domain, principal.getDomainId())) ) {
			applicationUser = getApplicationUser(principal.getDomainId(), principal.getUserId(), principal.getApplicationId());
		}
		return applicationUser;
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

}
