package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.TIMESTAMP_PATTERN;
import static com.code.aon.ui.common.ICommonMessages.TIME_2_PATTERN;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.Tariff;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationDivert;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.invoicing.ReservationInvoiceTo;
import com.esferalia.aon.pms.invoicing.ReservationInvoicing;
import com.esferalia.aon.pms.reservation.ReservationRequestManager;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.ui.pms.ProjectReservationPermission;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class ProjectReservationController extends BasicController implements IPmsConstants {

	private ReservationUtils reservationUtils;
	private ProjectReservationPermission reservationPermission;
	private String selectedTab;
	private String startTime;
	private String endTime;
	private int nights;
	private String guestName;
	private String guestSurname;
	private Item roomItem;
	private Tariff roomTariff;
	private boolean showConfirmWindow;
	private boolean confirmNoShow;
	private boolean showAuditInfoWindow;
	private boolean showInvoiceWindow;
	private ReservationInvoiceTo reservationInvoiceTo;
	private boolean showRectificationWindow;
	private Invoice invoiceToRectify;
	private boolean showModificationWindow;
	private Invoice invoiceToModify;
	private DataModel invoiceModel;

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils();
		}
		return reservationUtils;
	}

	public ProjectReservationPermission getReservationPermission() {
		if (reservationPermission == null) {
			reservationPermission = new ProjectReservationPermission();
		}
		return reservationPermission;
	}
	public void setReservationPermission(ProjectReservationPermission reservationPermission) {
		this.reservationPermission = reservationPermission;
	}

	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public String getStartTime() {
		if (StringUtils.isEmpty(startTime)) {
			ProjectReservation reservation = (ProjectReservation)getTo();
			if (reservation.getStartTime() != null) {
				startTime = new SimpleDateFormat("HH:mm").format(reservation.getStartTime());
			} else {
				startTime = "14:00";
			}
		}
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public void resetStartTime() {
		setStartTime(null);
	}

	public String getEndTime() {
		if (StringUtils.isEmpty(endTime)) {
			ProjectReservation reservation = (ProjectReservation)getTo();
			if (reservation.getEndTime() != null) {
				endTime = new SimpleDateFormat("HH:mm").format(reservation.getEndTime());
			} else {
				endTime = "12:00";
			}
		}
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public void resetEndTime() {
		setEndTime(null);
	}

	public int getNights() {
		if (nights <= 0) {
			ProjectReservation reservation = (ProjectReservation)getTo();
			nights = reservation.getNights();
		}
		return nights;
	}
	public void setNights(int nights) {
		this.nights = nights;
	}
	public void resetNights() {
		setNights(0);
	}

	public String getGuestName() {
		return guestName;
	}
	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getGuestSurname() {
		return guestSurname;
	}
	public void setGuestSurname(String guestSurname) {
		this.guestSurname = guestSurname;
	}
	public void resetGuestName() {
		setGuestName(null);
		setGuestSurname(null);
	}

	public Item getRoomItem() {
		return roomItem;
	}
	public void setRoomItem(Item roomItem) {
		this.roomItem = roomItem;
	}
	public void resetRoomItem() {
		setRoomItem(null);
	}

	public Tariff getRoomTariff() {
		return roomTariff;
	}
	public void setRoomTariff(Tariff roomTariff) {
		this.roomTariff = roomTariff;
	}
	public void resetRoomTariff() {
		ProjectReservation reservation = (ProjectReservation)getTo();
		setRoomTariff(obtainReservationTariff(reservation));
	}

	public boolean isShowConfirmWindow() {
		return showConfirmWindow;
	}
	public void setShowConfirmWindow(boolean showConfirmWindow) {
		this.showConfirmWindow = showConfirmWindow;
	}

	public boolean isConfirmNoShow() {
		return confirmNoShow;
	}
	public void setConfirmNoShow(boolean confirmNoShow) {
		this.confirmNoShow = confirmNoShow;
	}

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}	

	public boolean isShowInvoiceWindow() {
		return showInvoiceWindow;
	}

	public void setShowInvoiceWindow(boolean showInvoiceWindow) {
		this.showInvoiceWindow = showInvoiceWindow;
	}

	public ReservationInvoiceTo getReservationInvoiceTo() {
		return reservationInvoiceTo;
	}

	public void setReservationInvoiceTo(ReservationInvoiceTo reservationInvoiceTo) {
		this.reservationInvoiceTo = reservationInvoiceTo;
	}

	public boolean isShowRectificationWindow() {
		return showRectificationWindow;
	}

	public void setShowRectificationWindow(boolean showRectificationWindow) {
		this.showRectificationWindow = showRectificationWindow;
	}

	public Invoice getInvoiceToRectify() {
		return invoiceToRectify;
	}

	public void setInvoiceToRectify(Invoice invoiceToRectify) {
		this.invoiceToRectify = invoiceToRectify;
	}

	public boolean isShowModificationWindow() {
		return showModificationWindow;
	}

	public void setShowModificationWindow(boolean showModificationWindow) {
		this.showModificationWindow = showModificationWindow;
	}

	public Invoice getInvoiceToModify() {
		return invoiceToModify;
	}

	public void setInvoiceToModify(Invoice invoiceToModify) {
		this.invoiceToModify = invoiceToModify;
	}

	public DataModel getInvoiceModel() {
		if (invoiceModel == null) {
			invoiceModel = new ListDataModel(getReservationInvoiceList((ProjectReservation)getTo()));
		}
		return invoiceModel;
	}

	public void setInvoiceModel(DataModel invoiceModel) {
		this.invoiceModel = invoiceModel;
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), new Date());
		onSearch(event);
	}

	@Override
	public Object getSelectedTO() {
		try {
			return getManagerBean().get(((ProjectReservation)this.model.getRowData()).getId());
		} catch (ManagerBeanException ex) {
			String msg = "No se puede acceder a la Reserva. Recargue la lista y vuelva a intentarlo.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public boolean isEarlyCheckOut() throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (reservation.isInvoiced() && !reservation.isNoShow()) {
			IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
			Criteria criteria = new Criteria();
			String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID);
			criteria.addEqualExpression(alias, reservation.getId());
			alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE);
			criteria.addEqualExpression(alias, DateUtils.addDays(reservation.getEndDate(), -1));
			return (reservationRoomDetailBean.getCount(criteria) == 0);
		}
		return false;
	}

	public List<SelectItem> getReservationTimes() {
		DateFormat formatter = new SimpleDateFormat(AonUtil.getMessage(TIME_2_PATTERN));
		Date fromDate = DateUtils.truncate(((ProjectReservation)getTo()).getStartDate(), Calendar.DATE);
		Date toDate = DateUtils.addDays(fromDate, 1);

		List<SelectItem> hours = new LinkedList<SelectItem>();
		while (fromDate.before(toDate)) {
			SelectItem item = new SelectItem(formatter.format(fromDate));
			hours.add(item);
			item = new SelectItem(formatter.format(DateUtils.setMinutes(fromDate, 30)));
			hours.add(item);

			fromDate = DateUtils.addHours(fromDate, 1);
		}
		return hours;
	}

	public boolean isMyScope() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservation reservation = (ProjectReservation)getModel().getRowData();
			return UserUtils.getInstance().isScopeInUserScopes(reservation.getHotel().getScope());
		}
		return true;
	}

	public boolean isPendingRoomAssignation() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservation reservation = (ProjectReservation)getModel().getRowData();
			return getReservationUtils().isPendingRoomAssignation(reservation);
		}
		return true;
	}

	public boolean isToPendingRoomAssignation() throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)getTo();
		return getReservationUtils().isPendingRoomAssignation(reservation);
	}

	public boolean isPendingServiceAssignation() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservation reservation = (ProjectReservation)getModel().getRowData();
			return getReservationUtils().isPendingServiceAssignation(reservation, true);
		}
		return true;
	}

	public boolean isToPendingServiceAssignation() throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)getTo();
		return getReservationUtils().isPendingServiceAssignation(reservation, true);
	}

	public void resetHotel() throws ManagerBeanException {
		PmsCollectionsController collections = (PmsCollectionsController)AonUtil.getRegisteredBean(COLLECTIONS_CONTROLLER_NAME);
		if (collections.getCurrentUserHotelsCount() > 0) {
			ProjectReservation reservation = (ProjectReservation)getTo();
			reservation.setHotel((Hotel)collections.getCurrentUserHotels().get(0).getValue());
		}
	}

	public void onHotelChanged(ValueChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setHotel((Hotel)event.getNewValue());
			resetRoomItem();
			resetRoomTariff();
		}
	}

	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		return PmsUtils.getRoomItems(((ProjectReservation)getTo()).getHotel());
	}

	public void onStartDateChanged(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (reservation.getStartDate() == null) {
			reservation.setStartDate(DateUtils.truncate(new Date(), Calendar.DATE));
		}
		reservation.setEndDate(DateUtils.addDays(reservation.getStartDate(), getNights()));
	}

	public void onNightsChanged(ValueChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			setNights((Integer)event.getNewValue());
		} else {
			resetNights();
		}
		reservation.setEndDate(DateUtils.addDays(reservation.getStartDate(), getNights()));
	}

	public void onEndDateChanged(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (reservation.getEndDate() != null && reservation.getStartDate().compareTo(reservation.getEndDate()) < 0) {
			setNights((int)CommonUtil.getDaysBetweenDates(reservation.getStartDate(), reservation.getEndDate()));
		} else {
			setNights(1);
			reservation.setEndDate(DateUtils.addDays(reservation.getStartDate(), getNights()));
		}
	}

	public void onHolderChanged(ValueChangeEvent event) throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setBookingHolder((BookingHolder)event.getNewValue());
			if (reservation.isCompanyHolder()) {
				reservation.setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
			} else {
				reservation.setCompany((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
			}
			if (!reservation.isGuestHolder()) {
				reservation.setAdvance(0);
			}
			resetRoomTariff();
		}
	}

	public void onAgencyChanged(ValueChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setAgency((Customer)event.getNewValue());
			resetRoomTariff();
		}
	}

	public void onCompanyChanged(ValueChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setCompany((Customer)event.getNewValue());
			resetRoomTariff();
		}
	}

	public void onEnableAdvance(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		if (reservation.isAdvanceInvoiced()) {
			reservation.setAdvanceInvoiced(false);
			reservation.setAdvance(0);
			accept(event);
		}
	}

	private Tariff obtainReservationTariff(ProjectReservation reservation) {
		Tariff tariff = null;
		if (reservation.getBookingHolder() == BookingHolder.AGENCY && reservation.getAgency() != null && reservation.getAgency().getId() != null) {
			tariff = reservation.getAgency().getTariff();
		} else if (reservation.getBookingHolder() == BookingHolder.COMPANY && reservation.getCompany() != null && reservation.getCompany().getId() != null) {
			tariff = reservation.getCompany().getTariff();
		} else if (reservation.getHotel() != null && reservation.getHotel().getCustomer() != null) {
			tariff = reservation.getHotel().getCustomer().getTariff();
		}
		return tariff;
	}

	public void onCheckIn(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setCheckStatus(ReservationCheckStatus.CHECK_IN);
		accept(event);
	}

	public void onCheckOut(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setCheckStatus(ReservationCheckStatus.CHECK_OUT);
		accept(event);
	}

	public void onUndoCheckStatus(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setCheckStatus(ReservationCheckStatus.NO_CHECK);
		accept(event);
	}

	public void onBlock(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setStatus(ReservationStatus.BLOCKED);
		accept(event);
	}

	public void onUnblock(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setStatus(ReservationStatus.ACTIVE);
		accept(event);
	}

	public void onCancelReservation(ActionEvent event) throws ManagerBeanException {
		cancelReservation(event);
	}

	private void cancelReservation(ActionEvent event) throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)this.getTo();

		if (!reservation.isCancelled()) {
			IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
			for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
				ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
		    	getReservationUtils().removeProjectReservationRoomDetails(reservationRoom, false, null);
			}

			boolean cancelOk = true;
			if (StringUtils.isNotEmpty(reservation.getCrsCode())) {
				ReservationRequestManager requestManager = new ReservationRequestManager();
				cancelOk = requestManager.processBookingCancelRequest(reservation);
	
				DateFormat dateFormat = new SimpleDateFormat(AonUtil.getMessage(TIMESTAMP_PATTERN));
				reservation.setRemarks((cancelOk ? "OK" : "ERROR") + " CANCEL CRS: " + dateFormat.format(new Date()) + "\n" + reservation.getRemarks());
			}
		}

		if (isConfirmNoShow()) {
			reservation.setCheckStatus(ReservationCheckStatus.NO_SHOW);
		}
		reservation.setStatus(ReservationStatus.CANCELLED);
		accept(event);

		IController reservationRoomController = (IController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_ROOM_CONTROLLER_NAME);
    	reservationRoomController.onSearch(event);
		IController reservationServiceController = (IController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_SERVICE_CONTROLLER_NAME);
    	reservationServiceController.onSearch(event);
	}

	public void onInvoiceShow(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		try {
			if (reservation.isGuestHolder() && !PosUtils.isUserPosShiftOpened()) {
				setShowInvoiceWindow(false);
				String msg = "No se puede Facturar. El Usuario no ha abierto la Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (getReservationUtils().isPendingServiceAssignation(reservation, false)) {
				setShowInvoiceWindow(false);
				String msg = "No se puede Facturar. Existen Servicios sin Habitación asignada.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (isReservationAlreadyInvoiced(reservation)) {
				reservation.setStatus(ReservationStatus.INVOICED);
				accept(event);
				setSelectedTab(INVOICE);

				setShowInvoiceWindow(false);
				String msg = "La Reserva ya estaba Facturada.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			setReservationInvoiceTo(new ReservationInvoiceTo(false));
			getReservationInvoiceTo().setIssueDate(reservation.getStartDate());
			getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());
			fillInvoiceData(reservation);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private boolean isReservationAlreadyInvoiced(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), reservation.getId());
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_RECTIFICATION_TYPE), RectificationType.NONE);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ADVANCE), false);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), false);
		return (invoiceBean.getCount(criteria) > 0);
	}

	private void fillInvoiceData(ProjectReservation reservation) throws ManagerBeanException {
		getReservationInvoiceTo().setDirectCustomer(reservation.getProject().getRegistry().getId() == reservation.getHotelReservation().getCustomer().getRegistry().getId());
		getReservationInvoiceTo().setRegistry(reservation.getProject().getRegistry());
		if (getReservationInvoiceTo().isDirectCustomer()) {
			getReservationInvoiceTo().setAddress(new InvoiceAddress());
			IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), reservation.getId());
			criteria.addOrder(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX));
			for (ITransferObject ito : reservationGuestBean.getList(criteria)) {
				ProjectReservationGuest reservationGuest = (ProjectReservationGuest)ito;
				getReservationInvoiceTo().setGuest(reservationGuest);
				fillGuestData(reservationGuest);
				break;
			}
		} else {
			getReservationInvoiceTo().setAddress(getReservationInvoiceTo().getRegistry().getDefaultAddress());
		}

		getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
		onNewFinance(null);
	}

	private String obtainHotelInvoiceSeries() throws ManagerBeanException {
		List<SelectItem> seriesList = getHotelInvoiceSeries();
		return (seriesList.size() > 0) ? (String)seriesList.get(0).getValue() : "";
	}

	public List<SelectItem> getHotelInvoiceSeries() throws ManagerBeanException {
		return getHotelSeries(false);
	}

	private String obtainHotelRectificationSeries() throws ManagerBeanException {
		List<SelectItem> seriesList = getHotelRectificationSeries();
		return (seriesList.size() > 0) ? (String)seriesList.get(0).getValue() : "";
	}

	public List<SelectItem> getHotelRectificationSeries() throws ManagerBeanException {
		return getHotelSeries(true);
	}

	public List<SelectItem> getHotelSeries(boolean rectification) throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		Scope scope = (!rectification) ? reservation.getHotelReservation().getScope() : obtainRectifiedInvoiceScope();

		List<SelectItem> seriesList = new LinkedList<SelectItem>();
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), scope.getId());
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), true);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		if (rectification) {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_RECTIFICATION), true);
		} else {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), true);
		}
		for (ITransferObject ito : seriesBean.getList(criteria)) {
			Series series = (Series)ito;
			SelectItem selectItem = new SelectItem(series.getCode(), series.getCode());
			seriesList.add(selectItem);
		}
		return seriesList;
	}

	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private Scope obtainRectifiedInvoiceScope() throws ManagerBeanException {
		for (ITransferObject ito : getInvoiceToRectify().getDetailList()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_ID), invoiceDetail.getWorkPlace().getId());
			for (ITransferObject itr : hotelBean.getList(criteria)) {
				return ((Hotel)itr).getScope();
			}
			return invoiceDetail.getWorkPlace().getScope();
		}
		return getInvoiceToRectify().getScope();
	}

	public List<SelectItem> getGuests() throws ManagerBeanException {
		List<SelectItem> guestList = new LinkedList<SelectItem>();
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		Criteria criteria = new Criteria();
		String alias = reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, ((ProjectReservation)this.getTo()).getId());
		criteria.addOrder(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX));
		for (ITransferObject ito : reservationGuestBean.getList(criteria)) {
			ProjectReservationGuest reservationGuest = (ProjectReservationGuest)ito;
			SelectItem selectItem = new SelectItem(reservationGuest, reservationGuest.getFullName());
			guestList.add(selectItem);
		}
		return guestList;
	}

	public void onInvoiceGuestChanged(ValueChangeEvent event) throws ManagerBeanException {
		ProjectReservationGuest reservationGuest = new ProjectReservationGuest();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservationGuest = (ProjectReservationGuest)event.getNewValue();
		}
		fillGuestData(reservationGuest);
	}

	private void fillGuestData(ProjectReservationGuest reservationGuest) {
		getReservationInvoiceTo().getRegistry().setName(reservationGuest.getFullName());
		getReservationInvoiceTo().getRegistry().setDocumentType(reservationGuest.getDocumentType());
		getReservationInvoiceTo().getRegistry().setDocumentCountry(reservationGuest.getDocumentCountry());
		getReservationInvoiceTo().getRegistry().setDocument(reservationGuest.getDocument());

		getReservationInvoiceTo().getAddress().setAddress(StringUtils.abbreviate(reservationGuest.getAddress(), 45));
		getReservationInvoiceTo().getAddress().setZip(StringUtils.abbreviate(reservationGuest.getZip(), 16));
		getReservationInvoiceTo().getAddress().setCity(StringUtils.abbreviate(reservationGuest.getCity(), 45));
		getReservationInvoiceTo().getAddress().setProvince(StringUtils.abbreviate(reservationGuest.getProvince(), 45));
	}

	public void onNewFinance(ActionEvent event) throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		Finance finance = new Finance();
		if (!getReservationInvoiceTo().isDirectCustomer()) {
			RegistryPayMethod payMethod = getReservationInvoiceTo().getRegistry().getPayMethod();
			if (payMethod != null) {
				finance.setPayMethod(payMethod.getPayment());
			}
		}
		finance.setAmount(CommonUtil.round(reservation.getTotal() - reservation.getAdvancedAmount() - getFinancesAmount()));
		getReservationInvoiceTo().getFinances().add(finance);
	}

	public void onRemoveFinance(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        int financeIndex = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("hotelInvoiceFinanceIndex"));

        Finance financeToRemove = getReservationInvoiceTo().getFinances().get(financeIndex);
        getReservationInvoiceTo().getFinances().remove(financeIndex);

        Finance previousFinance = getReservationInvoiceTo().getFinances().get(financeIndex-1);
        previousFinance.setAmount(CommonUtil.round(previousFinance.getAmount() + financeToRemove.getAmount()));
	}

	public double getFinancesAmount() {
		double amount = 0;
		for (Finance finance : getReservationInvoiceTo().getFinances()) {
			amount += CommonUtil.round(finance.getAmount());
		}
		return CommonUtil.round(amount);
	}

	public void onInvoice(ActionEvent event) {
		setInvoiceModel(null);
		try {
			if (validateInvoice()) {
				getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
				getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));

				ProjectReservation reservation = (ProjectReservation)this.getTo();
				ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
				reservationInvoicing.invoice(getReservationInvoiceTo(), reservation);

				reservation.setStatus(ReservationStatus.INVOICED);
				if (reservation.getCheckStatus() == ReservationCheckStatus.NO_CHECK) {
					reservation.setCheckStatus(reservation.getEndDate().after(new Date()) ? ReservationCheckStatus.CHECK_IN : ReservationCheckStatus.CHECK_OUT);
				}
				accept(event);
				setSelectedTab(INVOICE);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private boolean validateInvoice() throws ManagerBeanException {
		if (!isFinancesAmountOk()) {
			String msg = "El importe de los Pagos no coincide con el importe de la Reserva.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (!isPayMethodOk()) {
			String msg = "La Forma de Pago es obligatoria.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	public boolean isFinancesAmountOk() throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		return CommonUtil.round(reservation.getTotal() - reservation.getAdvancedAmount() - getFinancesAmount()) == 0;
	}

	public boolean isPayMethodOk() {
		for (Finance finance : getReservationInvoiceTo().getFinances()) {
			if (finance.getPayMethod() == null && finance.getTotalAmount() != 0) {
				return false;
			}
		}
		return true;
	}

	private List<ITransferObject> getReservationInvoiceList(ProjectReservation reservation) {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), reservation.getId());
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
			criteria.addOrder(invoiceBean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE));
			return invoiceBean.getList(criteria);
		} catch (ManagerBeanException ex) {
			String msg = "Error al cargar los datos de Facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public boolean isInvoiceRectificable() {
		try {
			if (getInvoiceModel().isRowAvailable()) {
				Invoice invoice = (Invoice)getInvoiceModel().getRowData();
				ProjectReservation pr = (ProjectReservation)this.getTo();
				return isInvoiceRectificable(invoice, pr);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
		return false;
	}

	public static boolean isInvoiceRectificable( Invoice invoice, ProjectReservation pr ) throws ManagerBeanException {		
		if (invoice.isNoRectification()) {
			boolean financeOperator = AonUtil.getRoleManager().isFinanceOperator();
			if (invoice.isService()) {
				return financeOperator || invoice.isAllCommercialProducts();
			} else {
				if (invoice.isAdvance()) {
					return financeOperator && (pr != null) && !pr.isInvoiced();
				} else {
					return financeOperator && isLastReservationInvoice(invoice);
				}
			}
		}
		return false;
	}
	
	private static boolean isLastReservationInvoice(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), invoice.getProject().getId());
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), invoice.getType());
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_RECTIFICATION_TYPE), RectificationType.NONE);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ADVANCE), Boolean.FALSE);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), Boolean.FALSE);
		return invoiceBean.getCount(criteria) == 0;
	}

	public void onRectifyInvoiceShow(ActionEvent event) {
		if (!getInvoiceModel().isRowAvailable()) {
			setShowRectificationWindow(false);
			String msg = "No se puede Abonar. Factura no disponible.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		ProjectReservation reservation = (ProjectReservation)this.getTo();
		Invoice invoice = (Invoice)getInvoiceModel().getRowData();
		if ((reservation.isGuestHolder() || invoice.isService()) && !PosUtils.isUserPosShiftOpened()) {
			setShowRectificationWindow(false);
			String msg = "No se puede Abonar. El Usuario no ha abierto la Caja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setInvoiceToRectify(invoice);
		setReservationInvoiceTo(new ReservationInvoiceTo(false));
		getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());
	}

	public void onRectify(ActionEvent event) {
		setInvoiceModel(null);
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		try {
			getReservationInvoiceTo().setSeries(obtainHotelRectificationSeries());
			getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));

			ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
			reservationInvoicing.rectify(getInvoiceToRectify(), getReservationInvoiceTo(), false);

			if (!reservation.isCancelled()) {
				ProjectReservation savedReservation = (ProjectReservation)getManagerBean().get(reservation.getId());
				if (savedReservation.getStatus() != reservation.getStatus()) {
					reservation.setStatus(savedReservation.getStatus());
					if (reservation.getStatus() == ReservationStatus.ACTIVE) {
						reservation.setCheckStatus(ReservationCheckStatus.NO_CHECK);
					}
					accept(event);
				}
			}

			if (getInvoiceToRectify().isService()) {
				IController reservationServiceController = (IController)AonUtil.getRegisteredBean(RESERVATION_SERVICE_CONTROLLER_NAME);
				reservationServiceController.onSearch(null);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onModifyInvoiceShow(ActionEvent event) {
		try {
			if (!getInvoiceModel().isRowAvailable()) {
				setShowModificationWindow(false);
				String msg = "No se puede Modificar. Factura no disponible.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}

			ProjectReservation reservation = (ProjectReservation)this.getTo();
			Invoice invoice = (Invoice)getInvoiceModel().getRowData();
			if ((reservation.isGuestHolder() || invoice.isService()) && !PosUtils.isUserPosShiftOpened()) {
				setShowModificationWindow(false);
				String msg = "No se puede Modificar. El Usuario no ha abierto la Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			setInvoiceToModify(invoice);
			setReservationInvoiceTo(new ReservationInvoiceTo(false));
			getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());
			fillInvoiceModificationData(invoice);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private void fillInvoiceModificationData(Invoice invoice) throws ManagerBeanException {
		getReservationInvoiceTo().setDirectCustomer(true);
		getReservationInvoiceTo().setRegistry(invoice.getRegistry());
		getReservationInvoiceTo().getRegistry().setName(invoice.getRegistryName());
		getReservationInvoiceTo().getRegistry().setDocumentType(invoice.getRegistryDocumentType());
		getReservationInvoiceTo().getRegistry().setDocumentCountry(invoice.getRegistryDocumentCountry());
		getReservationInvoiceTo().getRegistry().setDocument(invoice.getRegistryDocument());

		IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAddressBean.getFieldName(IEntityAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		for (ITransferObject ito : invoiceAddressBean.getList(criteria)) {
			getReservationInvoiceTo().setAddress((InvoiceAddress)ito);
		}

		getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
		for (ITransferObject ito : financeBean.getList(criteria)) {
			getReservationInvoiceTo().getFinances().add((Finance)ito);
		}
	}

	private boolean isInvoiceInUserPosShift(Invoice invoice) {
		return (invoice.getPosShift() != null && invoice.getPosShift().equals(PosUtils.getUserPosShift()));
	}

	public boolean isFinancesPayMethodModifyAllowed() throws ManagerBeanException {
		Invoice invoice = getInvoiceToModify();
		return (!invoice.isAdvance() && isInvoiceInUserPosShift(invoice) && invoice.isAllFinancePending());
	}

	public boolean isFinancesAmountModifyAllowed() throws ManagerBeanException {
		Invoice invoice = getInvoiceToModify();
		return (!invoice.isService() && !invoice.isAdvance() && isInvoiceInUserPosShift(invoice) && invoice.isAllFinancePending());
	}

	public void onModifyInvoice(ActionEvent event) {
		setInvoiceModel(null);
		try {
			Invoice invoice = getInvoiceToModify();
			if (validateModificationInvoice(invoice)) {
				ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
				if (DateUtils.isSameDay(invoice.getIssueDate(), new Date()) && isInvoiceInUserPosShift(invoice) && invoice.isAllFinancePending()) {
					reservationInvoicing.modify(invoice, getReservationInvoiceTo());
				} else {
					setInvoiceToRectify(invoice);
					getReservationInvoiceTo().setSeries(obtainHotelRectificationSeries());
					getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
					getReservationInvoiceTo().setEarlyCheckOut(true); //Para que no borre los servicios asociados, en caso de Factura de Servicios.
					Invoice rectifierInvoice = reservationInvoicing.rectify(getInvoiceToRectify(), getReservationInvoiceTo(), false);

					getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
					getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
					Invoice newInvoice = reservationInvoicing.duplicate(invoice, reservationInvoiceTo);

					if (!isFinancesModified()) {
						reservationInvoicing.settle(rectifierInvoice, newInvoice);
					}
				}
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private boolean validateModificationInvoice(Invoice invoice) throws ManagerBeanException {
		if (!invoice.isAdvance() && !invoice.isService()) {
			return validateInvoice();
		} else if (invoice.isService()) {
			return isPayMethodOk();
		}
		return true;
	}

	private boolean isFinancesModified() throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), getInvoiceToModify().getId());
		for (ITransferObject ito : financeBean.getList(criteria)) {
			Finance invoiceFinance = (Finance)ito;
			boolean found = false;
			for (Finance modifyFinance : getReservationInvoiceTo().getFinances()) {
				if (invoiceFinance.getAmount() == modifyFinance.getAmount() && invoiceFinance.getPayMethod().equals(modifyFinance.getPayMethod())) {
					found = true;
				}
			}
			if (!found) {
				return true;
			}
		}
		return false;
	}

	public void onEarlyCheckOutShow(ActionEvent event) {
		EarlyCheckOutController earlyCheckOutController = (EarlyCheckOutController)AonUtil.getRegisteredBean(EARLY_CHECKOUT_CONTROLLER_NAME);
		earlyCheckOutController.setReservation((ProjectReservation)this.getTo());
		earlyCheckOutController.onInit();
	}

	public void onDivertModalShow(ActionEvent event) throws ManagerBeanException {
		IController divertController = (IController)AonUtil.getRegisteredBean(DIVERT_CONTROLLER_NAME);
		divertController.onReset(event);
		((ProjectReservationDivert)divertController.getTo()).setProjectReservation((ProjectReservation)this.getTo());
	}

	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		if (getInvoiceModel().isRowAvailable()) {
			BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.onLoad(event, ((Invoice)getInvoiceModel().getRowData()).getId(), RESERVATION_FORM_NAME, RESERVATION_CONTROLLER_NAME + ".refreshInvoices");
		}
	}

	public void refreshInvoices(ActionEvent event) {
		setInvoiceModel(null);
		getInvoiceModel();
	}

	public void onPrintInvoice(ActionEvent event) throws ManagerBeanException {
		if (getInvoiceModel().isRowAvailable()) {
			BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.load(event, ((Invoice)getInvoiceModel().getRowData()).getId());
		}
	}

}