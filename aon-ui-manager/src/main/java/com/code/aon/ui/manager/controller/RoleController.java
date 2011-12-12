package com.code.aon.ui.manager.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.Role;
import com.code.aon.ui.webmail.controller.LdapBasicController;

public class RoleController extends LdapBasicController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

	@Override
	public boolean updateBaseDN(Name parent) {
		String application = NameResolver.getValue(parent, 0);
		Name rolesDN = NameResolver.getApplicationRolesDN(application);
		getLdapDAO().setBaseDN(rolesDN);
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<Role> getRoles() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}
	
    /**
     * Available roles list defined in application.
     * 
     * @return List
     * @throws ManagerBeanException
     */
    public List<SelectItem> getAvailableRoles() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        try {
			for( Role role : getRoles() ) {
			    SelectItem item = new SelectItem( role.getId(), role.getCommonName() );
			    list.add(item);        	
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
        return list;
    }	
	
}
