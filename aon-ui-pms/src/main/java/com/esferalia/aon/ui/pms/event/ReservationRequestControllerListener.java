package com.esferalia.aon.ui.pms.event;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestGuest;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.ReservationRequestController;
import com.esferalia.aon.ui.pms.controller.ReservationRequestRoomController;

public class ReservationRequestControllerListener extends ControllerAdapter implements IPmsConstants {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestController controller = (ReservationRequestController)event.getController();
		ReservationRequest request = (ReservationRequest)controller.getTo();

		controller.resetNights();
		controller.setRequestGuest(obtainRequestGuest(request));

		request.setStartDate(DateUtils.truncate(new Date(), Calendar.DATE));
		request.setEndDate(DateUtils.addDays(request.getStartDate(), 1));
		request.setBookingHolder(BookingHolder.GUEST);
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