package com.esferalia.aon.pms.event;

import java.sql.Connection;
import java.sql.SQLException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.sql.SQLBooking;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ProjectReservationRoomBeanListener extends ManagerBeanListenerAdapter {

    @Override
    public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
    	ProjectReservationRoom to = (ProjectReservationRoom)evt.getTo();
   		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(CommonUtil.getDomainName(to.getDomain()));
			SQLBooking.insert(connection, to);
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new ManagerBeanException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(connection);
		}
    }

    @Override
    public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	ProjectReservationRoom to = (ProjectReservationRoom)evt.getTo();
    	if (to.isForceRefreshBooking()) {
    		Connection connection = null;
    		try {
    			connection = DatabaseUtil.getConnection(CommonUtil.getDomainName(to.getDomain()));
    			SQLBooking.delete(connection, to);
   				SQLBooking.insert(connection, to);
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
