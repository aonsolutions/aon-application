package com.esferalia.aon.ui.pms.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.code.aon.config.Series;
import com.code.aon.config.Tariff;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.reservation.ReservationInvoiceTo;
import com.esferalia.aon.pms.reservation.ReservationInvoicing;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.ui.pms.event.ProjectReservationSearchListener;

public class ProjectReservationController extends BasicController implements IPmsConstants {

	private String selectedTab;
	private String startTime;
	private String endTime;
	private int nights;
	private String guestName;
	private String guestSurname;
	private Item roomItem;
	private Tariff roomTariff;
	private boolean showInvoiceWindow;
	private ReservationInvoiceTo reservationInvoiceTo;
	private boolean showRectificationWindow;
	private Invoice invoiceToRectificate;
	private DataModel invoiceModel;

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
		if (nights == 0) {
			ProjectReservation reservation = (ProjectReservation)getTo();
			if (reservation.getStartDate() != null && reservation.getEndDate() != null) {
				nights = (int)CommonUtil.getDaysBetweenDates(reservation.getStartDate(), reservation.getEndDate());
			}
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

	public Invoice getInvoiceToRectificate() {
		return invoiceToRectificate;
	}

	public void setInvoiceToRectificate(Invoice invoiceToRectificate) {
		this.invoiceToRectificate = invoiceToRectificate;
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

	public List<SelectItem> getReservationTimes() {
		List<SelectItem> hours = new LinkedList<SelectItem>();
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		for (int i=0; i<24; i++) {
			calendar.set(Calendar.HOUR_OF_DAY, i);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			SelectItem item = new SelectItem(formatter.format(calendar.getTime()), formatter.format(calendar.getTime()));
			hours.add(item);

			calendar.set(Calendar.MINUTE, 30);
			item = new SelectItem(formatter.format(calendar.getTime()), formatter.format(calendar.getTime()));
			hours.add(item);
		}
		return hours;
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), new Date());
		ProjectReservationSearchListener searchController = (ProjectReservationSearchListener)AonUtil.getRegisteredBean(RESERVATION_SEARCH_LISTENER_NAME);
		searchController.setReservationStatuses(null);
		onSearch(event);
	}

	public boolean isPendingRoomAssignation() throws ManagerBeanException {
		boolean pendingRooms = true;
		if (getModel().isRowAvailable()) {
			ProjectReservation reservation = (ProjectReservation)getModel().getRowData();
			IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
			IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
			for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
				pendingRooms = false;
				ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
				criteria = new Criteria();
				String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
				criteria.addEqualExpression(alias, reservationRoom.getId());
				if (reservationRoomDetailBean.getCount(criteria) == 0) {
					return true;
				}
			}
		}
		return pendingRooms;
	}

	public boolean isPendingServiceAssignation() throws ManagerBeanException {
		boolean pendingServices = true;
		if (getModel().isRowAvailable()) {
			ProjectReservation reservation = (ProjectReservation)getModel().getRowData();
			IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
			IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID), reservation.getId());
			for (ITransferObject ito : reservationServiceBean.getList(criteria)) {
				pendingServices = false;
				ProjectReservationService reservationService = (ProjectReservationService)ito;
				criteria = new Criteria();
				String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
				criteria.addEqualExpression(alias, reservationService.getId());
				criteria.addNullExpression(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL));
				if (reservationServiceDetailBean.getCount(criteria) > 0) {
					return true;
				}
			}
		}
		return pendingServices;
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
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (reservation.getHotel() != null && reservation.getHotel().getId() != null) {
			return getHotelRoomItems(reservation.getHotel());
		}
		PmsCollectionsController collectionsController = (PmsCollectionsController)AonUtil.getRegisteredBean(COLLECTIONS_CONTROLLER_NAME);
		return collectionsController.getRoomItems(); 
	}

	private List<SelectItem> getHotelRoomItems(Hotel hotel) throws ManagerBeanException {
		List<Integer> items = new LinkedList<Integer>();
		IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), hotel.getId());
		for (ITransferObject ito : roomBean.getList(criteria)) {
			Room room = (Room)ito;
			if (!items.contains(room.getItem().getId())) {
				items.add(room.getItem().getId());
			}
		}

		List<SelectItem> roomItems = new LinkedList<SelectItem>();
		if (items.size() > 0) {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			criteria = new Criteria();
			criteria.addExpression(ExpressionUtilities.getInExpression(itemBean.getFieldName(IEntityAlias.ITEM_ID), items));
			criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_NAME));
			for (ITransferObject ito : itemBean.getList(criteria)) {
				Item item = (Item)ito;
				SelectItem roomItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
				roomItems.add(roomItem);
			}
		}
		return roomItems;
	}

	public void onStartDateChanged(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (reservation.getStartDate() != null) {
			reservation.setEndDate(DateUtils.addDays(reservation.getStartDate(), getNights()));
		} else {
			resetNights();
		}
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
		if (reservation.getEndDate() != null) {
			reservation.setStartDate(DateUtils.addDays(reservation.getEndDate(), 0-getNights()));
		} else {
			resetNights();
		}
	}

	public void onAgencyChanged(LookupChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setAgency((Customer)event.getNewValue());
			resetRoomTariff();
		}
	}

	public void onCompanyChanged(LookupChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setCompany((Customer)event.getNewValue());
			resetRoomTariff();
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
			resetRoomTariff();
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

	public boolean isActive() {
		ProjectReservation reservation = (ProjectReservation)getTo();
		return reservation.isActive();
	}

	public boolean isBlocked() {
		ProjectReservation reservation = (ProjectReservation)getTo();
		return reservation.isBlocked();
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

	public boolean isCancelled() {
		ProjectReservation reservation = (ProjectReservation)getTo();
		return reservation.isCancelled();
	}

	public boolean isCancellable() throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)getTo();
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), reservation.getId());
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
		Projection projection = Projection.sum(invoiceBean.getFieldName(IEntityAlias.INVOICE_TOTAL));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		return (value == null || ((Double)value).doubleValue() == 0);
	}

	public void onCancelReservation(ActionEvent event) throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)this.getTo();

		ReservationUtils reservationUtils = new ReservationUtils();
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
	    	reservationUtils.removeProjectReservationRoomDetails(reservationRoom, false);
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
			if (!isInvoiceable(reservation)) {
				setShowInvoiceWindow(false);
				String msg = "No se puede Facturar. Existen Servicios sin Habitación asignada.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			setReservationInvoiceTo(new ReservationInvoiceTo());
			fillInvoiceData(reservation);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public boolean isInvoiceable() throws ManagerBeanException  {
		return isInvoiceable((ProjectReservation)this.getTo());
	}

	private boolean isInvoiceable(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, reservation.getId());
		criteria.addNullExpression(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL));
		return (reservationServiceDetailBean.getCount(criteria) == 0);
	}

	private void fillInvoiceData(ProjectReservation reservation) throws ManagerBeanException {
		getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
		getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));

		getReservationInvoiceTo().setDirectCustomer(reservation.getProject().getRegistry().getId() == reservation.getHotel().getCustomer().getRegistry().getId());
		getReservationInvoiceTo().setRegistry(reservation.getProject().getRegistry());
		if (getReservationInvoiceTo().isDirectCustomer()) {
			getReservationInvoiceTo().setAddress(new InvoiceAddress());
			IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), reservation.getId());
			criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX), 1);
			for (ITransferObject ito : reservationGuestBean.getList(criteria)) {
				ProjectReservationGuest reservationGuest = (ProjectReservationGuest)ito;
				getReservationInvoiceTo().setGuest(reservationGuest);
				fillGuestData(reservationGuest);
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
		List<SelectItem> seriesList = new LinkedList<SelectItem>();
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), ((ProjectReservation)this.getTo()).getHotel().getScope().getId());
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), new Boolean(true));
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		if (rectification) {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_RECTIFICATION), new Boolean(true));
		} else {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), new Boolean(true));
		}
		for (ITransferObject ito : seriesBean.getList(criteria)) {
			Series series = (Series)ito;
			SelectItem selectItem = new SelectItem(series.getCode(), series.getCode());
			seriesList.add(selectItem);
		}
		return seriesList;
	}

	public void onInvoiceSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			getReservationInvoiceTo().setSeries((String)event.getNewValue());
			getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
		}
	}

	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
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
		double amount = CommonUtil.round(reservation.getTotal() - getFinancesAmount());
		finance.setAmount(amount);
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
		if (validateInvoice()) {
			ProjectReservation reservation = (ProjectReservation)this.getTo();
			try {
				ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
				reservationInvoicing.invoice(getReservationInvoiceTo(), reservation, false);
	
				reservation.setStatus(ReservationStatus.INVOICED);
				accept(event);
			} catch (ManagerBeanException ex) {
				AonUtil.addErrorMessage(ex.getMessage());
				throw new AbortProcessingException(ex.getMessage(), ex);
			}
		}
	}

	private boolean validateInvoice() {
		if (!isFinancesAmountOk()) {
			String msg = "El importe de los Pagos no coincide con el importe de la Reserva.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (!isPayMethodOk()) {
			String msg = "La Forma de Pago no es válida.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	public boolean isFinancesAmountOk() {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		return CommonUtil.round(reservation.getTotal() - getFinancesAmount()) == 0;
	}

	public boolean isPayMethodOk() {
		return (getReservationInvoiceTo().getFinances().get(0).getPayMethod() != null);
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

	public void onRectifyInvoiceShow(ActionEvent event) {
		if (!invoiceModel.isRowAvailable()) {
			setShowRectificationWindow(false);
			String msg = "No se puede Abonar. Factura no disponible.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		try {
			setInvoiceToRectificate((Invoice)getInvoiceModel().getRowData());
			setReservationInvoiceTo(new ReservationInvoiceTo());
			getReservationInvoiceTo().setSeries(obtainHotelRectificationSeries());
			getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onRectify(ActionEvent event) {
		setInvoiceModel(null);
		try {
			ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
			reservationInvoicing.rectify(getInvoiceToRectificate(), getReservationInvoiceTo());

			IController reservationServiceController = (IController)AonUtil.getRegisteredBean(RESERVATION_SERVICE_CONTROLLER_NAME);
			reservationServiceController.onSearch(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onPrintInvoice(ActionEvent event) throws ManagerBeanException {
		if (invoiceModel.isRowAvailable()) {
			BasicController controller = (BasicController) ((IController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME));
			controller.select(event, ((Invoice)getInvoiceModel().getRowData()).getId());
		}
	}

}