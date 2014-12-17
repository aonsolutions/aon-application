package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE_PATTERN;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestGuest;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class ReservationRequestController extends BasicController implements IPmsConstants, IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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

	public boolean isSkipResetAvailabilityMap() {
		return skipResetAvailabilityMap;
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

	public List<SelectItem> getStartDates() {
		List<SelectItem> startDates = new LinkedList<SelectItem>();
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		Date yesterday = DateUtils.addDays(today, -1);
		startDates.add(new SelectItem(yesterday, new SimpleDateFormat(AonUtil.getMessage(DATE_PATTERN)).format(yesterday)));
		startDates.add(new SelectItem(today, new SimpleDateFormat(AonUtil.getMessage(DATE_PATTERN)).format(today)));
		return startDates;
	}

	public void onStartDateChanged(ValueChangeEvent event) {
		ReservationRequest request = (ReservationRequest)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			request.setStartDate((Date)event.getNewValue());
		}
		request.setEndDate(DateUtils.addDays(request.getStartDate(), getNights()));
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
		acceptRequest(event);
	}

	public void acceptRequest(ActionEvent event) {
		super.accept(event);
		if (!isSkipResetAvailabilityMap()) {
			ReservationRequestRoomController requestRoomController = (ReservationRequestRoomController)AonUtil.getRegisteredBean(RESERVATION_REQUEST_ROOM_CONTROLLER_NAME);
			requestRoomController.setAvailableRoomStayMap(null);
		}
	}

}