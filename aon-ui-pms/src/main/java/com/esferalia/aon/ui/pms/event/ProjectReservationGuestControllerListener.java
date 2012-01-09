package com.esferalia.aon.ui.pms.event;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.dao.IPmsAlias;
import com.esferalia.aon.ui.pms.controller.ProjectReservationGuestController;

public class ProjectReservationGuestControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationGuestController controller = (ProjectReservationGuestController)event.getController();
		ProjectReservationGuest to = (ProjectReservationGuest)controller.getTo();
		try {
			to.setGuestIndex(calculateNextIndex(controller.getManagerBean(), to.getProjectReservation()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private	int calculateNextIndex(IManagerBean bean, ProjectReservation reservation) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPmsAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), reservation.getId());
		Projection projection = Projection.max(bean.getFieldName(IPmsAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX));
		Object value = bean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}