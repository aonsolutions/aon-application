package com.esferalia.aon.pms.event;

import java.sql.Connection;
import java.sql.SQLException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.sql.SQLBooking;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ProjectReservationBeanListener extends ManagerBeanListenerAdapter {

    @Override
    public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	ProjectReservation to = (ProjectReservation)evt.getTo();
    	if (to.isForceRefreshBooking()) {
    		Connection connection = null;
    		try {
    			connection = DatabaseUtil.getConnection(CommonUtil.getDomainName(to.getDomain()));
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
