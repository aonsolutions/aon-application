package com.esferalia.aon.pms.event;

import java.sql.Connection;
import java.sql.SQLException;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.AdminUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.sql.SQLBooking;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ProjectReservationBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	ProjectReservation to = (ProjectReservation)evt.getTo();
    	if (to.isForceRefreshBooking()) {
    		Connection connection = null;
    		try {
    			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(to.getDomain()));
    			SQLBooking.delete(connection, to);
    			if (!to.isCancelled() && !to.isNoShow()) {
    				SQLBooking.insert(connection, to);
    			}
			} catch (Throwable e) {
				try {
					connection.rollback();
				} catch (SQLException ex) {
				}
				throw new ManagerBeanException(e.getMessage());
			} finally {
				SQLUtils.closeQuietly(connection);
			}
    		to.setForceRefreshBooking(false);
    	}
    }

}
