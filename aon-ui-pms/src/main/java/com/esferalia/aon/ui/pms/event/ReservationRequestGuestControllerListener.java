package com.esferalia.aon.ui.pms.event;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestGuest;
import com.esferalia.aon.ui.pms.controller.ReservationRequestGuestController;

public class ReservationRequestGuestControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestGuestController controller = (ReservationRequestGuestController)event.getController();
		ReservationRequestGuest to = (ReservationRequestGuest)controller.getTo();
		try {
			to.setGuestIndex(calculateNextIndex(controller.getManagerBean(), to.getReservationRequest()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private	int calculateNextIndex(IManagerBean bean, ReservationRequest request) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.RESERVATION_REQUEST_GUEST_RESERVATION_REQUEST_ID), request.getId());
		Projection projection = Projection.max(bean.getFieldName(IEntityAlias.RESERVATION_REQUEST_GUEST_GUEST_INDEX));
		Object value = bean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}