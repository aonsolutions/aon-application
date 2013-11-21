package com.code.aon.ui.admin.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainUserSearchListener extends ControllerSearchListener {

	private Enterprise enterprise; 
	
	private Registry registry;

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Override
	protected void init() throws ManagerBeanException {
		IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
		setEnterprise((Enterprise) enterpriseBean.createNewTo());
		IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
		setRegistry((Registry) registryBean.createNewTo());
	}

	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (getEnterprise() != null && getEnterprise().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.USER_ENTERPRISE), getEnterprise().getId());			
		}
		if (getRegistry() != null && getRegistry().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.USER_REGISTRY), getRegistry().getId());			
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			init();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		updateUser(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		updateUser(event);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		User user = (User) event.getController().getTo();
		try {
			IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
			Enterprise enterprise = null;
			if ( user.getEnterprise() != null ) {
				enterprise = (Enterprise) enterpriseBean.get(user.getEnterprise());	
			}			
			if ( enterprise == null ) {
				enterprise = (Enterprise) enterpriseBean.createNewTo(); 
			}
			setEnterprise( enterprise );
			
			IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
			Registry registry = null;
			if ( user.getRegistry() != null ) {
				registry = (Registry) registryBean.get(user.getRegistry());
			}
			if ( registry == null ) {
				registry = (Registry) registryBean.createNewTo(); 
			}
			setRegistry( registry );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private void updateUser( ControllerEvent event ) {
		User user = (User) event.getController().getTo();
		user.setRegistry( getRegistry().getId() );
		user.setEnterprise( getEnterprise().getId() );
	}
	
}