package com.code.aon.ui.admin.controller;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import com.code.aon.admin.ApplicationUser;
import com.code.aon.admin.Profile;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainApplicationController extends BasicController {
	
	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public DomainApplication getDomainApplication() {
		return (DomainApplication) getTo();
	}

	public List<SelectItem> getProfiles() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		DomainApplication da = getDomainApplication();
		IManagerBean bean = BeanManager.getManagerBean(Profile.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_APPLICATION_ID), da.getApplication().getId());
		Expression expr1 = ExpressionUtilities.getEqualExpression("Profile.domain<id", da.getDomain());
		Expression expr2 = ExpressionUtilities.getNullExpression("Profile.domain");
		criteria.addExpression( ExpressionUtilities.getOrExpression(expr1, expr2));
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROFILE_NAME));
		List<ITransferObject> profiles = bean.getList(criteria);
		if (! profiles.isEmpty() ) {
			for( ITransferObject to : profiles ) {
				Profile profile = (Profile) to;
				SelectItem item = new SelectItem(profile, profile.getName() );
				list.add(item);
			}
		}		
		return list;
	}
	

	private Set<User> getRegisteredUsers() throws ManagerBeanException {
		DomainApplication da = getDomainApplication();
		Set<User> users = new HashSet<User>();
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUser.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_DOMAIN_APPLICATION_ID), da.getId());
		for (ITransferObject to : bean.getList(criteria)) {
			ApplicationUser user = (ApplicationUser) to;
			users.add(user.getUser());
		}
		return users;
	}	
	
	public List<SelectItem> getAvailableUsers() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_ACTIVE), Boolean.TRUE);
		criteria.addOrder(bean.getFieldName(IEntityAlias.USER_LOGIN));
		List<ITransferObject> users = bean.getList(criteria);
		if (! users.isEmpty() ) {
			Set<User> registeredUsers = getRegisteredUsers();
			for (ITransferObject to : users) {
				User user = (User) to;
				if (! registeredUsers.contains(user) ) {
					SelectItem item = new SelectItem(user, user.getLogin() );
					list.add(item);									
				}
			}			
		}
		return list;
	}		
	
}
