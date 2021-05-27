package com.esferalia.aon.ui.pms.event;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.esferalia.aon.ui.pms.controller.ReservationRequestRoomController;

public class ReservationRequestRoomControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestRoom to = (ReservationRequestRoom)event.getController().getTo();
		to.setUnits(1);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestRoomController controller = (ReservationRequestRoomController)event.getController();
		ReservationRequestRoom to = (ReservationRequestRoom)controller.getTo();
		to.setReservationRequest((ReservationRequest)controller.getMasterController().getTo());
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestRoomController controller = (ReservationRequestRoomController)event.getController();
		ReservationRequestRoom to = (ReservationRequestRoom)controller.getTo();
		try {
			to.setRoomIndex(calculateNextIndex(controller.getManagerBean(), to.getReservationRequest()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestRoomController controller = (ReservationRequestRoomController)event.getController();
		ReservationRequestRoom to = (ReservationRequestRoom)controller.getTo();
		if (controller.getAvailableRoomStayMap().containsKey(to.getId()) && roomStayPaxesChanged(to)) {
			controller.getAvailableRoomStayMap().remove(to.getId());
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestRoomController controller = (ReservationRequestRoomController)event.getController();
		ReservationRequestRoom to = (ReservationRequestRoom)controller.getTo();
		try {
			reOrderLines(to);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		controller.onSearch(null);
	}

	private	int calculateNextIndex(IManagerBean bean, ReservationRequest request) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ROOM_RESERVATION_REQUEST_ID), request.getId());
		Projection projection = Projection.max(bean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ROOM_ROOM_INDEX));
		Object value = bean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	private boolean roomStayPaxesChanged(ReservationRequestRoom requestRoom) {
		String select = "SELECT reservation_request_room.id id " +
    					"FROM reservation_request_room as reservation_request_room " +
    					"WHERE " + DomainManager.getSQLWhereClause("reservation_request_room.domain") + " " +
    					"AND reservation_request_room.id = " + requestRoom.getId() + " " +
    					"AND (reservation_request_room.units != " + requestRoom.getUnits() + " " +
    						"OR reservation_request_room.adults != " + requestRoom.getAdults() + " " +
    						"OR reservation_request_room.children != " + requestRoom.getChildren() + " " +
    						"OR reservation_request_room.babies != " + requestRoom.getBabies() + ")";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(select);
        return !query.addScalar("id", Hibernate.INTEGER).list().isEmpty();
	}

	private void reOrderLines(ReservationRequestRoom requestRoom) throws ManagerBeanException {
		ReservationRequest request = requestRoom.getReservationRequest();
		IManagerBean requestRoomBean = BeanManager.getManagerBean(ReservationRequestRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(requestRoomBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ROOM_RESERVATION_REQUEST_ID), request.getId());
		criteria.addGreaterThanExpression(requestRoomBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ROOM_ROOM_INDEX), requestRoom.getRoomIndex());
		criteria.addOrder(requestRoomBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ROOM_ROOM_INDEX));
		for (ITransferObject ito : requestRoomBean.getList(criteria)) {
			ReservationRequestRoom reservationRequestRoom = (ReservationRequestRoom)ito;
			reservationRequestRoom.setRoomIndex(reservationRequestRoom.getRoomIndex()-1);
			requestRoomBean.update(reservationRequestRoom);
		}
	}

}