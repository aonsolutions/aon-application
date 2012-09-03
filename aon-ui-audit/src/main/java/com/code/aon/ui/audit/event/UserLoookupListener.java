package com.code.aon.ui.audit.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.DataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Application;
import com.code.aon.config.ApplicationUser;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.controller.DomainResolver;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class UserLoookupListener extends ControllerAdapter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(UserLoookupListener.class);
	
	private Integer domainApplication;
	
	public UserLoookupListener() {
		try {
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			if ( principal != null ) {
				domainApplication = getDomainApplication(principal);
			}
		} catch (Throwable e) {
			LOGGER.error( "Can't resolved domain application", e );
		}
		setDisabled( domainApplication == null );
	}
	
	private Application getApplication( String name ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		IManagerBean bean = BeanManager.getManagerBean(Application.class);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_NAME), name);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (Application) list.get(0);
		}
		return null;
	}
	
	private Integer getDomainApplication( AuthPrincipal principal ) throws ManagerBeanException {
		Integer id = null;
		String applicationName = DomainResolver.getApplication(principal.getContext());
		Application application = getApplication(applicationName);
		if ( application != null ) {
			Criteria criteria = new Criteria();
			IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_APPLICATION_ID), application.getId());			
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				id = ((DomainApplication) list.get(0)).getId();
			}
		}
		return id;
	}

	private boolean isValid( User user ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUser.class);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_DOMAIN_APPLICATION_ID), domainApplication);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_USER_ID), user.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_ACTIVE), Boolean.TRUE);
		return bean.getCount(criteria) > 0;
	}
	
	@Override
	@SuppressWarnings({ "unchecked"})
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			DataModel model = event.getController().getModel();
			if ( (model != null) && (model.getRowCount() > 0) ) {
				List<User> list = (List<User>) model.getWrappedData();
				List<User> users = new LinkedList<User>();
				for( User user : list ) {
					if ( isValid(user) ) {
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
