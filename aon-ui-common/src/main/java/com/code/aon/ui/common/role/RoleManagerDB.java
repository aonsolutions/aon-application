package com.code.aon.ui.common.role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.controller.DomainResolver;

/**
 * Clase que controla los roles habituales de las aplicaciones AON.
 * 
 * @author ecastellano
 * 
 */
public class RoleManagerDB extends BasicRoleManager {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RoleManagerDB.class);
	
	private Set<String> roleSet;
	
	@Override
	public boolean isUserInRole(String role) {
		return this.roleSet.contains(role);
	}

	@Override
	public void init() {
		Integer applicationUser = getApplicationUser(DomainManager.getCurrentDomain());
		updateApplicationUserRoles(applicationUser);
		super.init();
	}
	
	private Integer getApplicationUser( Integer domain ) {
		AuthPrincipal principal = BasicPrincipal.getAuthPrincipal();
		String name = DomainResolver.getApplication(principal.getContext());
		Integer application = getApplication(name);
		Integer applicationUser = getApplicationUser(domain, principal.getUserId(), application);
		if ( (applicationUser == null) && (! ObjectUtils.equals(domain, principal.getDomainId())) ) {
			applicationUser = getApplicationUser(principal.getDomainId(), principal.getUserId(), application);
		}
		return applicationUser;
	}
	
	private Query getQuery( String query ) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		return HibernateUtil.getSession(sessionFactoryName).createQuery(query);		
	}
	
	private Integer getApplication( String name ) {
		Query query = getQuery("SELECT id FROM Application a WHERE a.name = ?").setString(0, name);
		return (Integer) query.uniqueResult();
	}

	private Integer getDomainApplication( Integer domain, Integer application ) {
		Query query = getQuery("SELECT id FROM DomainApplication da WHERE da.active = true and da.domain = ? and da.application = ?");
		query.setInteger(0, domain).setInteger(1, application);
		return (Integer) query.uniqueResult();
	}
	
	private Integer getApplicationUser( Integer domain, Integer user, Integer application ) {
		Integer domainApplication = getDomainApplication(domain, application);
		if ( domainApplication != null ) {
			Query query = getQuery("SELECT id FROM ApplicationUser au WHERE au.active = true and au.domainApplication = ? and au.user = ?");
			query.setInteger(0, domainApplication).setInteger(1, user);
			return (Integer) query.uniqueResult();				
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getProfiles( Integer applicationUser ) {
		Query query = getQuery("SELECT profile.id FROM ApplicationUserProfile aup WHERE aup.applicationUser = ?");
		query.setInteger(0, applicationUser);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	private List<String> getProfileRoles( Integer profile ) {
		Query query = getQuery("SELECT pr.applicationRole.role.name FROM ProfileRole pr WHERE pr.profile = ?");
		query.setInteger(0, profile );
		return query.list();
	}

	private void updateApplicationUserRoles( Integer applicationUser ) {
		this.roleSet = new HashSet<String>();
		try {			
			List<Integer> profiles = getProfiles(applicationUser);
			if ( (profiles != null) && (!profiles.isEmpty()) ) {
				for( Integer profile : profiles ) {
					List<String> list = getProfileRoles(profile);
					if ( (list != null) && (!list.isEmpty()) ) {
						roleSet.addAll(list);
					}
				}
			}			
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting roles of application_user " + applicationUser, th );
		}
	}
	
}
