package com.code.aon.ui.manager.event;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.Domain;
import com.code.aon.manager.dao.IManagerAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.DomainDBConnectionController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;

public class DomainDBConnectionControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainDBConnectionControllerListener.class);
	
	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		DBConnnection dbc = (DBConnnection) event.getController().getTo();
		Properties properties = getManager().getProperties();
		dbc.setCommonName( properties.getProperty(IManagerAlias.DB_CONNECTION_COMMON_NAME) );
		dbc.setDriverClassName( properties.getProperty(IManagerAlias.DB_CONNECTION_DRIVER_CLASS_NAME) );
		dbc.setUid( properties.getProperty(IManagerAlias.DB_CONNECTION_UID) );
		dbc.setUserPasswordString( properties.getProperty(IManagerAlias.DB_CONNECTION_USER_PASSWORD) );
		DomainController domainController = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		String domain = ((Domain) domainController.getTo()).getCommonName();
		String text = properties.getProperty(IManagerAlias.DB_CONNECTION_LABELED_URI);
		String url = MessageFormat.format( text, domain );
		dbc.setLabeledURI(url);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainDBConnectionController controller = (DomainDBConnectionController) event.getController();
		if ( controller.isCreateDB() ) {
			DBConnnection dbc = (DBConnnection) event.getController().getTo();
			ManagerController manager = getManager();
			try {
				manager.createDB(dbc);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
				try {
					manager.removeDB(dbc);
				} catch ( SQLException sqle ) {
					LOGGER.error(sqle.getMessage(), sqle);
				}
				throw new ControllerListenerException( e.getMessage(), e );
			}
		}
	}

}
