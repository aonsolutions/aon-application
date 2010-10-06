package com.code.aon.ui.manager.controller;

import java.util.LinkedList;
import java.util.List;

import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ldap.Scope;
import com.code.aon.manager.BasicProfile;
import com.code.aon.manager.Domain;
import com.code.aon.manager.Role;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;

public class ProfileController extends LdapBasicController implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
	
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
	
	private boolean isUsed(Name profileId, String domain) {
		BasicLdap ldap = new BasicLdap();
		try {
			Name dn = NameResolver.getDomainApplicationsDN(domain);
			String objectClass = NameResolver.getObjectClass(IAonObjectClasses.DOMAIN_APPLICATION_USER);
			String value = NameResolver.getEqualExpression(ILdapConstants.MEMBER_ATTRIBUTE, profileId.toString());
			String filter = NameResolver.getAndExpression(objectClass, value);
			List<Entry> list = ldap.getLdapSession().search(dn, filter, Scope.SUBTREE_SCOPE, ILdapConstants.MEMBER_ATTRIBUTE );
			return list.size() > 0;
		} catch ( LdapException e ) {
			LOGGER.error( "Error in checking if its used" + profileId, e );
		} finally {
			ldap.closeSession();
		}
		return false;
	}	
	
	@Override
	protected boolean isUsed(ILdapTransferObject to) throws ManagerBeanException {
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		for( Domain domain : dc.getDomains() ) {
			if ( isUsed(to.getId(), domain.getCommonName()) ) {
				return true;
			}
		}
		return false;
	}	
	
}
