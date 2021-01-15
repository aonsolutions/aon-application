package com.code.aon.ui.admin.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainUserSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Enterprise enterprise;
	private Boolean portal;
	private Registry registry;

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public Boolean getPortal() {
		return portal;
	}
	
	public void setPortal(Boolean portal) {
		if(portal) {
			this.enterprise.setId(getEnterpriseId());
		} else this.enterprise.setId(null);

		this.portal = portal;
	}
	
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	private Integer getEnterpriseId() {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = controller.obtainCompany();
		return company.getId();
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
		setEnterprise((Enterprise) enterpriseBean.createNewTo());
		setPortal(false);
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
		if (! AonUtil.getRoleManager().isSysAdmin() ) {
//			criteria.addNullExpression(getFieldName(IEntityAlias.USER_ENTERPRISE));
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
			setPortal(user.getEnterprise() != null);
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