package com.esferalia.aon.pms.event;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.sql.SQLBooking;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ProjectReservationRoomBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservationRoom to = (ProjectReservationRoom)evt.getTo();
    	try {
    		if (to.getRoomIndex() == 0) {
        		to.setRoomIndex(calculateNextIndex(to.getProjectReservation()));
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex);
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
			connection = DatabaseUtil.getConnection(CommonUtil.getDomainName(to.getDomain()));
			SQLBooking.delete(connection, to);
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new ManagerBeanVetoListenerException(e);
		} finally {
			SQLUtils.closeQuietly(connection);
		}
	}

	private	Integer calculateNextIndex(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		Projection projection = Projection.max(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ROOM_INDEX));
		Object value = reservationRoomBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
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
