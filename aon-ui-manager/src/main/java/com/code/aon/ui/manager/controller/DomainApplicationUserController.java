package com.code.aon.ui.manager.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DomainApplicationUser;
import com.code.aon.manager.DomainUser;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationUserController extends LdapBasicController {
	
	@Override
	public void updateBaseDN(Name parent) {
		Name baseDN = NameResolver.getName( NameResolver.ou(NameResolver.USERS), parent );
		getLdapDAO().setBaseDN(baseDN);
	}

	@SuppressWarnings("unchecked")
	public List<DomainApplicationUser> getDomainApplicationUsers() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}
	
	private boolean isRegisteredUser( List<DomainApplicationUser> das, String name ) {
		for( DomainApplicationUser da : das ) {
			if ( StringUtils.equals(da.getCommonName(), name) ) {
				return true;
			}
		}
		return false;
	}
	
	public List<SelectItem> getAvailableUsers() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		List<DomainApplicationUser> das = getDomainApplicationUsers();
		DomainUserController controller = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
		for (DomainUser user : controller.getUsers()) {
			if (! isRegisteredUser(das, user.getUid()) ) {
				SelectItem item = new SelectItem(user.getUid(), user.getUid() );
				list.add(item);				
			}
		}
		return list;
	}	
	
}
