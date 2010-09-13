package com.code.aon.ui.manager.controller;

import java.util.LinkedList;
import java.util.List;

import javax.naming.Name;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.BasicProfile;
import com.code.aon.manager.Role;
import com.code.aon.ui.util.AonUtil;

public class ProfileController extends LdapBasicController {
	
	@Override
	public void updateBaseDN(Name parent) {
		Name baseDN = NameResolver.getName( NameResolver.ou(NameResolver.PROFILES), parent );
		getLdapDAO().setBaseDN(baseDN);
	}
	
	public BasicProfile getProfile() {
		return (BasicProfile) getTo();
	}
	
	public Name[] getCurrentRoles() {
		BasicProfile profile = getProfile();
		if ( profile.getRoles() != null ) {
			List<Name> list = new LinkedList<Name>();
			for( Role role : profile.getRoles() ) {
				if ( role != null ) {
					list.add(role.getId());
				}
			}
			return list.toArray(new Name[list.size()]);
		}
		return new Name[0];
	}
	
	public void setCurrentRoles( Name[] list ) throws ManagerBeanException {
		BasicProfile profile = getProfile();
		List<Role> roles = new LinkedList<Role>();
		RoleController controller = (RoleController) AonUtil.getRegisteredBean(ROLE_CONTROLLER_NAME);
		for( Name name : list ) {
			roles.add( (Role) controller.getManagerBean().get(name) );
		}
		profile.setRoles(roles);
	}
	
}
