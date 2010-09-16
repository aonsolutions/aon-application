package com.code.aon.ui.manager.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.manager.DomainApplicationUser;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DBBasicController;
import com.code.aon.ui.manager.controller.DomainApplicationController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationUserControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationUserControllerListener.class);
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try {		
			updateLines( (DomainApplicationUser) event.getController().getTo() );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	private void updateLines( DomainApplicationUser user ) throws ManagerBeanException {
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		if ( dac.isAonDB() ) {
			DBBasicController us = (DBBasicController) AonUtil.getRegisteredBean(USER_SCOPE_CONTROLLER_NAME);
			updateLine( us, user );
			DBBasicController uwg = (DBBasicController) AonUtil.getRegisteredBean(USER_WORK_GROUP_CONTROLLER_NAME);
			updateLine( uwg, user );
		}
	}	
	
	private void updateLine( DBBasicController controller, DomainApplicationUser user ) throws ManagerBeanException {
		controller.updateDAO();
		controller.clearCriteria();
		Criteria criteria = controller.getCriteria();
		String alias = controller.getPojoShortName() + ".user.login";
		criteria.addEqualExpression(alias, user.getCommonName());
		controller.onSearch(null);
	}

}
