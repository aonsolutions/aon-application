package com.esferalia.aon.pms.event;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.pms.sql.SQLBooking;
import com.esferalia.aon.pms.sql.SQLStopSales;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ProjectReservationRoomBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservationRoom to = (ProjectReservationRoom)evt.getTo();
    	if (mustStopSale(to)) {
			throw new ManagerBeanVetoListenerException("Hay un Paro de Ventas definido para el Hotel en ese periodo y condiciones.");
    	}

    	try {
    		if (to.getRoomIndex() == 0) {
        		to.setRoomIndex(calculateNextIndex(to.getProjectReservation()));
    		}
    		if (StringUtils.isBlank(to.getAllotmentRateCode())) {
    			ReservationUtils reservationUtils = new ReservationUtils();
    			to.setAllotmentRateCode(reservationUtils.obtainAllotmentRateCode(to.getProjectReservation()));
    		}
    	} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage());
		}
    }

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservationRoom to = (ProjectReservationRoom)evt.getTo();
    	if (!to.isForceRefreshBooking()) {
    		to.setForceRefreshBooking(isRefreshBookingNeeded(to));
    	}
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservationRoom to = (ProjectReservationRoom)evt.getTo();
   		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(to.getDomain()));
			SQLBooking.delete(connection, to);
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new ManagerBeanVetoListenerException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(connection);
		}
	}

	private boolean mustStopSale(ProjectReservationRoom reservationRoom) {
   		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(reservationRoom.getProjectReservation().getDomain()));
			if (SQLStopSales.mustStopSale(connection, reservationRoom)) {
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

    private	Integer calculateNextIndex(ProjectReservation reservation) throws ManagerBeanException {
		String stmt = "SELECT MAX(room_index)" +
						" FROM project_reservation_room as project_reservation_room" +
						" WHERE project_reservation_room.project_reservation = :project";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(stmt);
		query.setInteger("project", reservation.getId());
		List<?> list = query.list();
		return !list.isEmpty() && !list.contains(null) ? ((Byte)list.get(0)).intValue() + 1 : 1; 
	}

	private boolean isRefreshBookingNeeded(ProjectReservationRoom reservationRoom) {
		String stmt = "SELECT 1" +
						" FROM project_reservation_room as project_reservation_room" +
    					" WHERE project_reservation_room.id = :id" +
    					" AND project_reservation_room.item = :item" +
    					" AND project_reservation_room.tariff = :tariff" +
    					" AND project_reservation_room.adults = :adults" +
    					" AND project_reservation_room.children = :children";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(stmt);
		query.setInteger("id", reservationRoom.getId());
		query.setInteger("item", reservationRoom.getItem().getId());
		query.setInteger("tariff", reservationRoom.getTariff().getId());
		query.setInteger("adults", reservationRoom.getAdults());
		query.setInteger("children", reservationRoom.getChildren());
        return query.list().isEmpty();
	}

}
