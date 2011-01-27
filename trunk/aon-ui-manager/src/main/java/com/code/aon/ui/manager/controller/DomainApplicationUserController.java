package com.code.aon.ui.manager.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DomainApplicationUser;
import com.code.aon.manager.DomainUser;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.manager.converter.TransferObjectConverter;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;

public class DomainApplicationUserController extends LdapBasicController implements IManagerConstants {
	
	private User user;
	
	private Converter scopeConverter;
	
	private Converter workgroupConverter;
	
	@Override
	public void updateBaseDN(Name parent) {
		Name baseDN = NameResolver.getName( NameResolver.ou(NameResolver.USERS), parent );
		getLdapDAO().setBaseDN(baseDN);
	}
	
	public DomainApplicationUser getDomainApplicationUser() {
		return (DomainApplicationUser) getTo();
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
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
			IController scopeController = (IController) AonUtil.getRegisteredBean(SCOPE_CONTROLLER_NAME);
			this.scopeConverter = new TransferObjectConverter(scopeController);			
		}
		return scopeConverter;
	}
	
	public Converter getWorkgroupConverter() {
		if ( workgroupConverter == null ) {
			IController wgController = (IController) AonUtil.getRegisteredBean(WORK_GROUP_CONTROLLER_NAME);
			this.workgroupConverter = new TransferObjectConverter(wgController);			
		}
		return workgroupConverter;
	}
	
}
