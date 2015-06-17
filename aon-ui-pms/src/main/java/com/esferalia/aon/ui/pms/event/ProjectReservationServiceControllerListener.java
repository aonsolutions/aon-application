package com.esferalia.aon.ui.pms.event;

import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.enumeration.MealPlan;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationServiceControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationService to = (ProjectReservationService)event.getController().getTo();
		to.setServiceIndex(0);
		to.setMealPlan(obtainMealPlan(to.getItem().getDetail()));
		linkServiceToRoom(to);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationService to = (ProjectReservationService)event.getController().getTo();
		to.setMealPlan(obtainMealPlan(to.getItem().getDetail()));
		linkServiceToRoom(to);
	}

	private MealPlan obtainMealPlan(String mealPlanValue) {
		ReservationUtils reservationUtils = new ReservationUtils();
		return reservationUtils.obtainMealPlan(mealPlanValue);
	}

	private void linkServiceToRoom(ProjectReservationService reservationService) throws ControllerListenerException {
		if (reservationService.getProjectReservationRoom() == null) {
			try {
				IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
				Criteria criteria = new Criteria();
				String alias = reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID);
				criteria.addEqualExpression(alias, reservationService.getProjectReservation().getId());
				List<ITransferObject> reservationRoomList = reservationRoomBean.getList(criteria);
				if (reservationRoomList.size() == 1) {
					ProjectReservationRoom reservationRoom = (ProjectReservationRoom)reservationRoomList.get(0);
					reservationService.setProjectReservationRoom(reservationRoom.getId());
				}
			} catch (ManagerBeanException ex) {
				throw new ControllerListenerException(ex.getMessage());
			}
		}
	}

}