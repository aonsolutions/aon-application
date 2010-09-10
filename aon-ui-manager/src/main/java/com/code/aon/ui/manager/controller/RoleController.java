package com.code.aon.ui.manager.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.Role;
import com.code.aon.ui.form.BasicController;

public class RoleController extends BasicController implements IManagerConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RoleController.class);
	
	private LdapDAO ldapDAO;
	
	private BasicManagerBean ldapManagerBean;
	
	public RoleController() {
		this.ldapDAO = new LdapDAO(Role.class);		
		this.ldapManagerBean = new BasicManagerBean(this.ldapDAO);
	}

	public void setApplication( String application ) {
		Name rolesDN = NameResolver.getApplicationRolesDN(application);
		this.ldapDAO.setBaseDN(rolesDN);
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		return this.ldapManagerBean;
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
