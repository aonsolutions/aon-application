package com.code.aon.desktop.event;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.code.aon.common.AonException;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.User;
import com.code.aon.desktop.DBConnnection;
import com.code.aon.desktop.Domain;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.desktop.controller.AonDomainController;
import com.code.aon.desktop.controller.DomainController;
import com.code.aon.desktop.dao.IDesktopAlias;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class UserDomainManagementListener extends ControllerAdapter implements ILdapConstants, IAonObjectClasses, IDesktopConstants {

	private static final Logger LOGGER = Logger.getLogger(DomainController.class.getName());
	
	private void addUser( Domain domain, User user, DomainController controller ) throws AonException {
		DBConnnection dbc = controller.getDBConnection( domain.getCommonName() );
		if ( dbc != null ) {
			List<User> users = Arrays.asList( new User[]{user} );
			controller.replicateUsers(dbc, users);
		} else {
			LOGGER.warning( "No DBConnection found for domain " + domain.getCommonName() );
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		AonDomainController adc = (AonDomainController) AonUtil.getRegisteredBean(CURRENT_DOMAIN_CONTROLLER_NAME);
		if ( adc.isDomainManagement() ) {
			User user = (User) event.getController().getTo();
			DomainController controller = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
			Domain domain = adc.getDomain();
			Criteria criteria = new Criteria();
			try {
				criteria.addEqualExpression(controller.getFieldName(IDesktopAlias.DOMAIN_PARENT_DOMAIN), domain.getId().toString());
				List<ITransferObject> list = controller.getManagerBean().getList(criteria);
				for( ITransferObject to : list ) {
					Domain memberDomain = (Domain) to;
					addUser( memberDomain, user, controller);
				}
			} catch (AonException e) {
				throw new ControllerListenerException( e.getMessage(), e );
			}
			
		}
	}
	
}
