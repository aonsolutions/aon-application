package com.esferalia.aon.ui.pms.event;

import static com.code.aon.ui.common.ICommonMessages.DATE_PATTERN;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestGuest;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.ReservationRequestController;
import com.esferalia.aon.ui.pms.controller.ReservationRequestRoomController;

public class ReservationRequestControllerListener extends ControllerAdapter implements IPmsConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestController controller = (ReservationRequestController)event.getController();
		ReservationRequest request = (ReservationRequest)controller.getTo();

		controller.resetNights();
		controller.setRequestGuest(obtainRequestGuest(request));

		request.setStartDate(DateUtils.truncate(new Date(), Calendar.DATE));
		request.setEndDate(DateUtils.addDays(request.getStartDate(), 1));
		request.setBookingHolder(AonUtil.getRoleManager().isCommercialOperator() || controller.isAgencyUser() ? BookingHolder.AGENCY : BookingHolder.GUEST);
		request.setRequestCounter(0);
		request.setActive(true);

		resetAvailableRoomRequested();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestController controller = (ReservationRequestController)event.getController();
		ReservationRequest request = (ReservationRequest)controller.getTo();

		controller.resetNights();
		controller.setRequestGuest(obtainRequestGuest(request));

		resetAvailableRoomRequested();
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestController controller = (ReservationRequestController)event.getController();
		if (!controller.isSkipResetAvailabilityMap()) {
			ReservationRequest request = (ReservationRequest)controller.getTo();
			validateRequest(request);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestController controller = (ReservationRequestController)event.getController();
		if (!controller.isSkipResetAvailabilityMap()) {
			ReservationRequest request = (ReservationRequest)controller.getTo();
			validateRequest(request);
		}
	}

	private void validateRequest(ReservationRequest request) throws ControllerListenerException {
		Date yesterday = DateUtils.truncate(DateUtils.addDays(new Date(), -1), Calendar.DATE);
		String yesterdayStr = new SimpleDateFormat(AonUtil.getMessage(DATE_PATTERN)).format(yesterday);
		if (request.getStartDate().before(yesterday)) {
			throw new ControllerListenerException("La Fecha de Entrada no puede ser anterior a " + yesterdayStr + ".");
		}
		if (CommonUtil.getYear(request.getStartDate()) > (CommonUtil.getYear(new Date()) + 1)) {
			throw new ControllerListenerException("Fecha de Entrada de la Solicitud incorrecta.");
		}
		if (!request.getStartDate().before(request.getEndDate())) {
			throw new ControllerListenerException("Fecha de Salida de la Solicitud incorrecta.");
		}
		if (CommonUtil.getDaysBetweenDates(request.getStartDate(), request.getEndDate()) > 90) {
			throw new ControllerListenerException("La Estancia no puede ser superior a 90 días.");
		}
		try {
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CODE), request.getCode());
			criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), request.getStartDate());
			criteria.addNotEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.CANCELLED);
			if (request.getAgency() != null && request.getAgency().getId() != null) {
				criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_AGENCY_ID), request.getAgency().getId());
			} else {
				criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_BOOKING_HOLDER), BookingHolder.GUEST);
			}
			if (reservationBean.getCount(criteria) > 0) {
				StringBuffer message = new StringBuffer();
				message.append("Ya existe una Reserva con ese Localizador y Fecha de Entrada para ");
				if (request.getAgency() != null && request.getAgency().getId() != null) {
					message.append("la Agencia " + request.getAgency().getRegistry().getFullName() + ".");
				} else {
					message.append("un Cliente Directo.");
				}
				throw new ControllerListenerException(message.toString());
			}

			IManagerBean requestBean = BeanManager.getManagerBean(ReservationRequest.class);
			criteria = new Criteria();
			if (request.getId() != null) {
				criteria.addNotEqualExpression(requestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ID), request.getId());
			}
			criteria.addEqualExpression(requestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_CODE), request.getCode());
			criteria.addEqualExpression(requestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_START_DATE), request.getStartDate());
			if (request.getAgency() != null && request.getAgency().getId() != null) {
				criteria.addEqualExpression(requestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_AGENCY_ID), request.getAgency().getId());
			} else {
				criteria.addEqualExpression(requestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_BOOKING_HOLDER), BookingHolder.GUEST);
			}
			if (requestBean.getCount(criteria) > 0) {
				StringBuffer message = new StringBuffer();
				message.append("Ya existe una Solicitud con ese Localizador y Fecha de Entrada para ");
				if (request.getAgency() != null && request.getAgency().getId() != null) {
					message.append("la Agencia " + request.getAgency().getRegistry().getFullName() + ".");
				} else {
					message.append("un Cliente Directo.");
				}
				throw new ControllerListenerException(message.toString());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestController controller = (ReservationRequestController)event.getController();
		try {
			IManagerBean requestGuestBean = BeanManager.getManagerBean(ReservationRequestGuest.class);
			ReservationRequestGuest requestGuest = controller.getRequestGuest();
			requestGuest.setReservationRequest((ReservationRequest)controller.getTo());
			requestGuest.setGuestIndex(1);
			controller.setRequestGuest((ReservationRequestGuest)requestGuestBean.insertOrUpdate(requestGuest));
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage(), ex);
		}
	}

	private void resetAvailableRoomRequested() {
		ReservationRequestRoomController requestRoomController = (ReservationRequestRoomController)AonUtil.getRegisteredBean(RESERVATION_REQUEST_ROOM_CONTROLLER_NAME);
		requestRoomController.setAvailableRoomStayMap(null);
	}

	private ReservationRequestGuest obtainRequestGuest(ReservationRequest request) throws ControllerListenerException {
		if (request.getId() != null) {
			try {
				IManagerBean requestGuestBean = BeanManager.getManagerBean(ReservationRequestGuest.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(requestGuestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_GUEST_RESERVATION_REQUEST_ID), request.getId());
				criteria.addOrder(requestGuestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_GUEST_GUEST_INDEX));
				for (ITransferObject ito : requestGuestBean.getList(criteria)) {
					ReservationRequestGuest requestGuest = (ReservationRequestGuest)ito;
					return requestGuest;
				}
			} catch (ManagerBeanException ex) {
				throw new ControllerListenerException(ex.getMessage(), ex);
			}
		}
		return new ReservationRequestGuest();
	}

}