package com.code.aon.ui.common.role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.ui.util.AonUtil;

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
		Integer applicationUser = AdminUtil.getApplicationUser(AonUtil.getAuthPrincipal(), DomainManager.getCurrentDomain());
		updateApplicationUserRoles(applicationUser);
		super.init();
	}

	private void updateApplicationUserRoles( Integer applicationUser ) {
		this.roleSet = new HashSet<String>();
		try {			
			List<Integer> profiles = AdminUtil.getProfiles(applicationUser);
			if ( (profiles != null) && (!profiles.isEmpty()) ) {
				for( Integer profile : profiles ) {
					List<String> list = AdminUtil.getProfileRoles(profile);
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
