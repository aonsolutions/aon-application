package com.code.aon.ui.manager.event;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.Domain;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.DomainDBConnectionController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.util.AonUtil;

public class DomainDBConnectionControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainDBConnectionControllerListener.class);
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		DBConnnection dbc = (DBConnnection) event.getController().getTo();
		dbc.setCommonName("aon_master");
		dbc.setDriverClassName("org.gjt.mm.mysql.Driver");
		dbc.setUid("dbuser");
		dbc.setUserPasswordString("serubd2000");
		DomainController domainController = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		String domain = ((Domain) domainController.getTo()).getCommonName();
		String url = "jdbc:mysql:replication://192.168.3.110,192.168.3.111,192.168.3.112:3306/" + domain + "?autoReconnect=true";
		dbc.setLabeledURI(url);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainDBConnectionController controller = (DomainDBConnectionController) event.getController();
		if ( controller.isCreateDB() ) {
			DBConnnection dbc = (DBConnnection) event.getController().getTo();
			try {
				controller.createDB(dbc);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
				try {
					controller.removeDB(dbc);
				} catch ( SQLException sqle ) {
					LOGGER.error(sqle.getMessage(), sqle);
				}
				throw new ControllerListenerException( e.getMessage(), e );
			}
		}
	}

}
