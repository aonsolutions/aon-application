package com.esferalia.aon.ui.pms.event;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.ui.pms.controller.ProjectReservationServiceController;

public class ProjectReservationServiceControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationServiceController controller = (ProjectReservationServiceController)event.getController();
		ProjectReservationService to = (ProjectReservationService)controller.getTo();
		to.setTaxableBase(CommonUtil.round(to.getQuantity() * to.getPrice()));
		try {
			to.setServiceIndex(calculateNextIndex(controller.getManagerBean(), to.getProjectReservation()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private	int calculateNextIndex(IManagerBean bean, ProjectReservation reservation) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID), reservation.getId());
		Projection projection = Projection.max(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_SERVICE_INDEX));
		Object value = bean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}