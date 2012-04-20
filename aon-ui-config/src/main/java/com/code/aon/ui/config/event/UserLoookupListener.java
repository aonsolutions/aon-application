package com.code.aon.ui.config.event;

import static com.code.aon.ldap.IAonObjectClasses.USER;
import static com.code.aon.ldap.ILdapConstants.ACTIVE_ATTRIBUTE;
import static com.code.aon.ui.common.ICommonConstants.DOMAIN_RESOLVER_CONTROLLER_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.common.controller.DomainResolver;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class UserLoookupListener extends ControllerAdapter {
	
	private String domain;
	
	private String application;
	
	public UserLoookupListener() {
		setDisabled( AonUtil.isSkipLdap() );
		if (! isDisabled() ) {
	    	DomainResolver resolver = (DomainResolver) AonUtil.getRegisteredBean(DOMAIN_RESOLVER_CONTROLLER_NAME);
	    	this.domain = resolver.getDomain();			
	    	this.application = StringUtils.removeStart(UserUtils.getInstance().getPrincipal().getContext(), "/" );			
		}
	}

	private boolean isValid( BasicLdap ldap, String user ) {
		Name userDN = NameResolver.getUserDN(domain, user);
		if ( ldap.exists(userDN, USER) ) {
			Entry entry = ldap.get(userDN, USER, ACTIVE_ATTRIBUTE);
			if ( (entry != null) && entry.toBoolean(ACTIVE_ATTRIBUTE) ) {
				Name appUserDN = NameResolver.getDomainApplicationUserDN(domain, application, user);
				if ( ldap.exists(appUserDN, IAonObjectClasses.DOMAIN_APPLICATION_USER) ) {
					return true;
				}				
			}
		}
		return false;
	}
	
	@Override
	@SuppressWarnings({ "unchecked"})
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			DataModel model = event.getController().getModel();
			if ( (model != null) && (model.getRowCount() > 0) ) {
				BasicLdap ldap = new BasicLdap();
				List<User> list = (List<User>) model.getWrappedData();
				List<User> users = new LinkedList<User>();
				for( User user : list ) {
					if ( isValid( ldap, user.getLogin()) ) {
						users.add(user);
					}
				}
				model.setWrappedData( users );
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
}
