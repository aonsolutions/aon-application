package com.esferalia.aon.pms.event;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.AdminUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.esferalia.aon.pms.sql.SQLStopSales;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ReservationRequestRoomBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ReservationRequestRoom to = (ReservationRequestRoom)evt.getTo();
		if (mustStopSale(to)) {
			throw new ManagerBeanVetoListenerException("Hay un Paro de Ventas definido para el Hotel en ese periodo y condiciones.");
    	}
    }

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ReservationRequestRoom to = (ReservationRequestRoom)evt.getTo();
    	if (verifyStopSalesNeeded(to)) {
			if (mustStopSale(to)) {
				throw new ManagerBeanVetoListenerException("Hay un Paro de Ventas definido para el Hotel en ese periodo y condiciones.");
	    	}
    	}
	}

	private boolean mustStopSale(ReservationRequestRoom requestRoom) {
   		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(requestRoom.getReservationRequest().getDomain()));
			if (SQLStopSales.mustStopSale(connection, requestRoom)) {
				return true;
	    	}
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
		} finally {
			SQLUtils.closeQuietly(connection);
		}
		return false;
	}

	private boolean verifyStopSalesNeeded(ReservationRequestRoom requestRoom) {
		String stmt = "SELECT 1" +
						" FROM reservation_request_room as reservation_request_room" +
    					" WHERE reservation_request_room.id = :id" +
    					" AND reservation_request_room.item = :item" +
    					" AND reservation_request_room.units = :units";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(stmt);
		query.setInteger("id", requestRoom.getId());
		query.setInteger("item", requestRoom.getItem().getId());
		query.setInteger("units", requestRoom.getUnits());
        return query.list().isEmpty();
	}

}
