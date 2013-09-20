package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE_PATTERN;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestGuest;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class ReservationRequestController extends BasicController implements IPmsConstants {
	
	private String selectedTab;
	private int nights;
	private ReservationRequestGuest requestGuest;
	private boolean skipResetAvailabilityMap;
	private boolean showConfirmWindow;
	private boolean showAuditInfoWindow;
	private Boolean agencyUser;
	
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public int getNights() {
		if (nights <= 0) {
			ReservationRequest request = (ReservationRequest)getTo();
			nights = request.getNights();
		}
		return nights;
	}
	public void setNights(int nights) {
		this.nights = nights;
	}
	public void resetNights() {
		setNights(0);
	}

	public ReservationRequestGuest getRequestGuest() {
		return requestGuest;
	}
	public void setRequestGuest(ReservationRequestGuest requestGuest) {
		this.requestGuest = requestGuest;
	}

	public void setSkipResetAvailabilityMap(boolean value) {
		this.skipResetAvailabilityMap = value;
	}

	public boolean isShowConfirmWindow() {
		return showConfirmWindow;
	}
	public void setShowConfirmWindow(boolean showConfirmWindow) {
		this.showConfirmWindow = showConfirmWindow;
	}
	
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}	

	public boolean isAgencyUser() {
		if (agencyUser == null) {
			try {
				agencyUser = PmsUtils.isAgencyUser();
			} catch (ManagerBeanException ex) {
				String msg = "Se produjo un error al buscar el Usuario de Agencia. [" + ex.getMessage() + "]";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, ex);
			}
		}
		return agencyUser;
	}

	public void onStartDateChanged(ActionEvent event) {
		ReservationRequest request = (ReservationRequest)getTo();
		if (request.getStartDate() == null) {
			request.setStartDate(DateUtils.truncate(new Date(), Calendar.DATE));
		}
		request.setEndDate(DateUtils.addDays(request.getStartDate(), getNights()));
	}

	public void onNightsChanged(ValueChangeEvent event) {
		ReservationRequest request = (ReservationRequest)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			setNights((Integer)event.getNewValue());
		} else {
			resetNights();
		}
		request.setEndDate(DateUtils.addDays(request.getStartDate(), getNights()));
	}

	public void onEndDateChanged(ActionEvent event) {
		ReservationRequest request = (ReservationRequest)getTo();
		if (request.getEndDate() != null && request.getStartDate().compareTo(request.getEndDate()) < 0) {
			resetNights();
		} else {
			setNights(1);
			request.setEndDate(DateUtils.addDays(request.getStartDate(), getNights()));
		}
	}
	
	public void onHolderChanged(ValueChangeEvent event) throws ManagerBeanException {
		ReservationRequest request = (ReservationRequest)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			request.setBookingHolder((BookingHolder)event.getNewValue());
			if (!request.isAgencyHolder()) {
				request.setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
			} else {
				if (!AonUtil.getRoleManager().isCommercialOperator()) {
					request.setRemarks("IMPREVISTO AGENCIA");
				}
			}
			if (!request.isCompanyHolder()) {
				request.setCompany((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
			}
		}
	}

	@Override
	public void accept(ActionEvent event) {
		ReservationRequest request = (ReservationRequest)getTo();
		if (validateRequest(request)) {
			if (isNew() && reservationExists(request)) {
				setShowConfirmWindow(true);
			} else {
				acceptRequest(event);
			}
		}
	}

	private boolean validateRequest(ReservationRequest request) {
		Date yesterday = DateUtils.truncate(DateUtils.addDays(new Date(), -1), Calendar.DATE);
		String yesterdayStr = new SimpleDateFormat(AonUtil.getMessage(DATE_PATTERN)).format(yesterday);
		if (request.getStartDate().before(yesterday)) {
			String msg = "La Fecha de Entrada no puede ser anterior a " + yesterdayStr + ".";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (!request.getEndDate().after(request.getStartDate())) {
			String msg = "La Fecha de Salida deber ser posterior a la de Entrada.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	public void acceptRequest(ActionEvent event) {
		super.accept(event);
		if (!skipResetAvailabilityMap) {
			ReservationRequestRoomController requestRoomController = (ReservationRequestRoomController)AonUtil.getRegisteredBean(RESERVATION_REQUEST_ROOM_CONTROLLER_NAME);
			requestRoomController.setAvailableRoomStayMap(null);
		}
	}

	private boolean reservationExists(ReservationRequest request) {
		if (request.isAgencyHolder()) {
			try {
				IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CODE), request.getCode());
				criteria.addNotEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.CANCELLED);
				return (reservationBean.getCount(criteria) > 0);
			} catch (ManagerBeanException ex) {
				String msg = "Se produjo un error al buscar si ya existe la Reserva. [" + ex.getMessage() + "]";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, ex);
			}
		}
		return false;
	}

}