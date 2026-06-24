package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

public class UserScopeController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(UserScopeController.class);
	
	private List<Scope> scopeList;
	private String searchValue;
	private Scope[] scopes;
	
	private Scope[] selected;
	
	private User user;
	
	public Scope[] getScopes() {
		return scopes;
	}

	public void setScopes(Scope[] scopes) {
		this.scopes = scopes;
	}

	public Scope[] getSelected() {
		return selected;
	}

	public void setSelected(Scope[] selected) {
		this.selected = selected;
	}
	
	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Scope> getScopeList() {
		try {
			IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
			Criteria criteria = new Criteria();
			UserUtils.getInstance().addForceHeredityDomainCondition(criteria, scopeBean.getFieldName(IEntityAlias.SCOPE_DOMAIN) );			
			criteria.addOrder(scopeBean.getFieldName(IEntityAlias.SCOPE_DESCRIPTION));
			return (List) scopeBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e);
		}
		return null;
	}	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<UserScope> getUserScopes() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(UserScope.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_SCOPE_USER_ID), user.getId());
			return (List) bean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e);
		}
		return null;
	}	
	
	public void accept( ActionEvent event ) {
		try {
			List<Scope> _scopes = new LinkedList<Scope>(Arrays.asList(this.selected));
			IManagerBean bean = BeanManager.getManagerBean(UserScope.class);
			List<UserScope> oldScopes = getUserScopes();
			if (! oldScopes.isEmpty() ) {
				for( UserScope us : oldScopes ) {
					if ( _scopes.contains(us.getScope()) ) {
						_scopes.remove(us.getScope());
					} else {
						bean.remove(us);
					}
				}
			}
			if (! _scopes.isEmpty() ) {
				for( Scope scope : _scopes ) {
					UserScope us = new UserScope();
					us.setUserDBByUserId(user);
					us.setScope(scope);
					bean.insert(us);
				}			
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating user scope list", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}

	public void init( User user ) {
		this.user = user;
		this.searchValue = "";
		List<Scope> list = getScopeList();
		
		List<UserScope> userScopes = getUserScopes();
		this.selected = new Scope[userScopes.size()];
		for( int i = 0; i < this.selected.length; i++ ) {
			this.selected[i] = userScopes.get(i).getScope();
			list.remove(this.selected[i]);
		}
		scopeList = list;
		this.scopes = list.toArray(new Scope[list.size()]);
	}

	public String getParentDomainDescription() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		return ds.getParentDomainId() != null 
				? AdminUtil.getDomainDescription(ds.getParentDomainId())
				: null;
	}
	
	
	public void onChange( ActionEvent event ) {
		List<Scope> list = scopeList.stream().filter(f -> AonStringUtils.containsIgnoreCase(f.getDescription(), getSearchValue())).collect(Collectors.toCollection(LinkedList::new));
		this.scopes = list.toArray(new Scope[list.size()]);
	}
}