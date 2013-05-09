package com.code.aon.ui.manager.controller;

import static com.code.aon.ui.manager.controller.IManagerConstants.DOMAIN_USER_CONTROLLER_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DomainApplicationUser;
import com.code.aon.manager.DomainUser;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.manager.converter.LdapTransferObjectConverter;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationUserController extends LdapBasicController {
	
	private Converter scopeConverter;
	
	private Converter workgroupConverter;
	
	@Override
	public boolean updateBaseDN(Name parent) {
		Name baseDN = NameResolver.getName( NameResolver.ou(NameResolver.USERS), parent );
		getLdapDAO().setBaseDN(baseDN);
		return true;
	}
	
	public DomainApplicationUser getDomainApplicationUser() {
		return (DomainApplicationUser) getTo();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
	
	public Converter getScopeConverter() {
		if ( scopeConverter == null ) {
			IController scopeController = (IController) AonUtil.getRegisteredBean(ConfigConstants.SCOPE);
			this.scopeConverter = new LdapTransferObjectConverter(scopeController);			
		}
		return scopeConverter;
	}
	
	public Converter getWorkgroupConverter() {
		if ( workgroupConverter == null ) {
			IController wgController = (IController) AonUtil.getRegisteredBean(ConfigConstants.WORK_GROUP);
			this.workgroupConverter = new LdapTransferObjectConverter(wgController);			
		}
		return workgroupConverter;
	}
	
}
