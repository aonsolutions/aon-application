package com.esferalia.aon.ui.pms.event;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationDivert;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;

public class DivertControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ProjectReservationDivert divert = (ProjectReservationDivert) event.getController().getTo();
		try {
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			divert.setProjectReservation((ProjectReservation) reservationBean.createNewTo());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		divert.setDivertDate(new Date());
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ProjectReservationDivert divert = (ProjectReservationDivert) event.getController().getTo();
		checkDate(divert);
		checkRoomAssignation(divert);
		divert.setRequestHotel(divert.getProjectReservation().getHotel());
		divert.setStatus(ReservationDivertStatus.PENDING);
		divert.setRequestUser(UserUtils.getInstance().getLoggedUser());
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ProjectReservationDivert divert = (ProjectReservationDivert) event.getController().getTo();
		checkDate(divert);
		checkRoomAssignation(divert);
		divert.setStatus(ReservationDivertStatus.PENDING);
		divert.setRequestUser(UserUtils.getInstance().getLoggedUser());
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ProjectReservationDivert divert = (ProjectReservationDivert) this.getController().getTo();
		if( divert.getStatus() == ReservationDivertStatus.ACCEPTED ){
			getController().onCancel(null);
		}
	}

	private void checkDate(ProjectReservationDivert divert)
			throws ControllerListenerException {
		if (divert.getDivertDate().before(divert.getProjectReservation().getStartDate())
				|| divert.getDivertDate().after(DateUtils.addDays(divert.getProjectReservation().getEndDate(), -1)) ) {
			String msg = "Error en la solicitud. Fecha de desvio fuera de la reserva.";
			throw new ControllerListenerException(msg);
		}
	}
	
	private void checkRoomAssignation(ProjectReservationDivert divert) throws ControllerListenerException  {
		try {
			if(!DateUtils.isSameDay(divert.getDivertDate(), divert.getProjectReservation().getStartDate()) 
					&& getRoomDetailList(divert.getProjectReservation()).isEmpty()){
				String msg = "Error en la solicitud. No se puede desviar una reserva iniciada sin habitaciones asignadas.";
				throw new ControllerListenerException(msg);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error en la solicitud. No se puede desviar una reserva iniciada sin habitaciones asignadas.";
			throw new ControllerListenerException(msg);
		}
	}
	
	private List<ITransferObject> getRoomDetailList(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		criteria.addOrder(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE));
		return reservationRoomDetailBean.getList(criteria);
	}
	
}