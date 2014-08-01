package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.APPLICATION_USER_CONTROLLER_NAME;
import static com.esferalia.aon.entity.IEntityAlias.APPLICATION_USER_DOMAIN_APPLICATION_ID;
import static com.esferalia.aon.entity.IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.Profile;
import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.DomainApplication;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.controller.AdminMainController;
import com.code.aon.ui.admin.controller.ApplicationProfileController;
import com.code.aon.ui.admin.controller.DomainApplicationUserController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainApplicationControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationControllerListener.class);
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getAdmin().resetTermsOfServiceAccepted();	
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplication da = (DomainApplication) event.getController().getTo();
		try {		
			DomainApplicationUserController dausc = (DomainApplicationUserController) AonUtil.getRegisteredBean(APPLICATION_USER_CONTROLLER_NAME);
			dausc.removeApplicationUsers( APPLICATION_USER_DOMAIN_APPLICATION_ID, da.getId() );
			FormUtil.remove(DomainApplicationModule.class, da.getId(), DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID);
			removeProfiles(da);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}	
	}	
	
	private void removeProfiles( DomainApplication da ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Profile.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_DOMAIN_ID), da.getDomain());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_APPLICATION_ID), da.getApplication().getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			ApplicationProfileController.removeLines( (Profile) to );
			bean.remove( to );
		}	
	}    
	
}