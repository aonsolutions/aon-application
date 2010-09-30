package com.code.aon.ui.manager.controller;

import java.util.LinkedList;
import java.util.List;

import javax.naming.Name;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.BasicProfile;
import com.code.aon.manager.Role;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;

public class ProfileController extends LdapBasicController implements IManagerConstants {
	
	@Override
	public void updateBaseDN(Name parent) {
		Name baseDN = NameResolver.getName( NameResolver.ou(NameResolver.PROFILES), parent );
		getLdapDAO().setBaseDN(baseDN);
	}
	
	public BasicProfile getProfile() {
		return (BasicProfile) getTo();
	}
	
	@SuppressWarnings("unchecked")
	public List<BasicProfile> getProfiles() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}	
	
	@SuppressWarnings("unchecked")
	public List<BasicProfile> getProfiles( Name parent ) throws ManagerBeanException {
		updateBaseDN(parent);
		return (List) getManagerBean().getList(null);
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
