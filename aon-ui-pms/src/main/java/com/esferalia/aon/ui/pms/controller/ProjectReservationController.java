package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE_PATTERN;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR;
import static com.code.aon.ui.common.ICommonMessages.REGISTRY_DOCUMENT_INCORRECT_ERROR;
import static com.code.aon.ui.common.ICommonMessages.TIMESTAMP_PATTERN;
import static com.code.aon.ui.common.ICommonMessages.TIME_2_PATTERN;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.ConexFlowPost;
import com.code.aon.conexflow.ConexFlowUtils;
import com.code.aon.conexflow.jooq.DBConsults;
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
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.marketing.MailProcess;
import com.code.aon.marketing.enumeration.MailProcessType;
import com.code.aon.marketing.util.MailProcessUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.marketing.controller.TemplateController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.db.MailAccount;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationDivert;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.card.CardOperationTo;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.invoicing.AdvanceInvoiceTo;
import com.esferalia.aon.pms.invoicing.AdvanceInvoicing;
import com.esferalia.aon.pms.invoicing.ReservationInvoiceTo;
import com.esferalia.aon.pms.invoicing.ReservationInvoicing;
import com.esferalia.aon.pms.invoicing.ReservationInvoiceTo.HotelService;
import com.esferalia.aon.pms.reservation.InventoryManager;
import com.esferalia.aon.pms.reservation.ReservationRequestManager;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.ui.pms.ProjectReservationConexFlow;
import com.esferalia.aon.ui.pms.ProjectReservationPermission;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class ProjectReservationController extends BasicController implements IPmsConstants, IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final String CONEXFLOW_RESULT_OK = "000";
	
	private ReservationUtils reservationUtils;
	private ProjectReservationPermission reservationPermission;
	private ProjectReservationConexFlow reservationConexFlow;
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
	private boolean showAdvanceInvoiceWindow;
	private AdvanceInvoiceTo advanceInvoiceTo;
	private boolean showTouristTaxInvoiceWindow;
	private boolean showTouristTaxFreeWindow;
	private boolean showInvoiceWindow;
	private ReservationInvoiceTo reservationInvoiceTo;
	private boolean showRectificationWindow;
	private Invoice invoiceToRectify;
	private boolean showModificationWindow;
	private Invoice invoiceToModify;
	private boolean showCreditCardWindow;
	private boolean showCreditCardPreauthorizationWindow;
	private CardOperationTo cardOperationTo;
	private List<Integer> multipleReservation;
	private DataModel invoiceModel;
	private boolean showNotifyWindow;

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
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

	public ProjectReservationConexFlow getReservationConexFlow() {
		if (reservationConexFlow == null) {
			ProjectReservation reservation = (ProjectReservation)this.getTo();
			reservationConexFlow = new ProjectReservationConexFlow(reservation, getDomain(reservation));
		}
		return reservationConexFlow;
	}
	
	public void setReservationConexFlow(ProjectReservationConexFlow reservationConexFlow) {
		this.reservationConexFlow = reservationConexFlow;
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

	public boolean isShowAdvanceInvoiceWindow() {
		return showAdvanceInvoiceWindow;
	}
	public void setShowAdvanceInvoiceWindow(boolean showAdvanceInvoiceWindow) {
		this.showAdvanceInvoiceWindow = showAdvanceInvoiceWindow;
	}

	public AdvanceInvoiceTo getAdvanceInvoiceTo() {
		return advanceInvoiceTo;
	}
	public void setAdvanceInvoiceTo(AdvanceInvoiceTo advanceInvoiceTo) {
		this.advanceInvoiceTo = advanceInvoiceTo;
	}

	public boolean isShowTouristTaxInvoiceWindow() {
		return showTouristTaxInvoiceWindow;
	}
	public void setShowTouristTaxInvoiceWindow(boolean showTouristTaxInvoiceWindow) {
		this.showTouristTaxInvoiceWindow = showTouristTaxInvoiceWindow;
	}

	public boolean isShowTouristTaxFreeWindow() {
		return showTouristTaxFreeWindow;
	}
	public void setShowTouristTaxFreeWindow(boolean showTouristTaxFreeWindow) {
		this.showTouristTaxFreeWindow = showTouristTaxFreeWindow;
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

	public boolean isShowCreditCardWindow() {
		return showCreditCardWindow;
	}
	public void setShowCreditCardWindow(boolean showCreditCardWindow) {
		this.showCreditCardWindow = showCreditCardWindow;
	}

	public boolean isShowCreditCardPreauthorizationWindow() {
		return showCreditCardPreauthorizationWindow;
	}
	public void setShowCreditCardPreauthorizationWindow(boolean showCreditCardPreauthorizationWindow) {
		this.showCreditCardPreauthorizationWindow = showCreditCardPreauthorizationWindow;
	}

	public CardOperationTo getCardOperationTo() {
		return cardOperationTo;
	}
	public void setCardOperationTo(CardOperationTo cardOperationTo) {
		this.cardOperationTo = cardOperationTo;
	}

	public List<Integer> getMultipleReservation() {
		return multipleReservation;
	}
	public void setMultipleReservation(List<Integer> multipleReservation) {
		this.multipleReservation = multipleReservation;
	}
	public int getMultipleReservationSize() {
		return (multipleReservation != null) ? multipleReservation.size() : 0;
	}

	public DataModel getInvoiceModel() {
		if (invoiceModel == null) {
			invoiceModel = new SerializableListDataModel(getReservationInvoiceList((ProjectReservation)getTo()));
		}
		return invoiceModel;
	}
	public void setInvoiceModel(DataModel invoiceModel) {
		this.invoiceModel = invoiceModel;
	}

	public boolean isShowNotifyWindow() {
		return showNotifyWindow;
	}
	public void setShowNotifyWindow(boolean showNotifyWindow) {
		this.showNotifyWindow = showNotifyWindow;
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), new Date());
		onSearch(event);
	}

	@Override
	protected void synchronizeAddedPojo() throws ManagerBeanException {
		super.synchronizeAddedPojo();
		getReservationPermission().setReservation((ProjectReservation)getTo());
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

	public void refreshEntireReservation(ActionEvent event) {
		try {
			refresh(event);
			IController reservationGuestController = FormUtil.getController(IPmsConstants.RESERVATION_GUEST_CONTROLLER_NAME);
			reservationGuestController.onSearch(event);
			IController reservationRoomController = FormUtil.getController(IPmsConstants.RESERVATION_ROOM_CONTROLLER_NAME);
			reservationRoomController.onSearch(event);
			IController reservationServiceController = FormUtil.getController(IPmsConstants.RESERVATION_SERVICE_CONTROLLER_NAME);
			reservationServiceController.onSearch(event);
			synchronizeAddedPojo();
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
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

	public Date obtainStartTime() {
		ProjectReservation reservation = (ProjectReservation)getTo();
		return obtainDateTime(reservation.getStartDate(), getStartTime());
	}

	public Date obtainEndTime() {
		ProjectReservation reservation = (ProjectReservation)getTo();
		return obtainDateTime(!reservation.isEarlyCheckOut() ? reservation.getEndDate() : reservation.getEndTime(), getEndTime());
	}

	private Date obtainDateTime(Date date, String time) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
		calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(3, time.length())));
		return calendar.getTime();
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

	public void onEarlyCheckOut(Date earlyCheckOutDate) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setEarlyCheckOut(true);
		reservation.setEndTime(earlyCheckOutDate);
		reservation.setCheckStatus(ReservationCheckStatus.CHECK_OUT);
		if (DateUtils.isSameDay(reservation.getStartDate(), earlyCheckOutDate) && reservation.getSavedStatus() == ReservationStatus.ACTIVE) {
			reservation.setStatus(ReservationStatus.CANCELLED);
		}
		reservation.setSkipDirtyControl(true);
		accept(null);
	}

	public void onUndoCheckStatus(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		if (reservation.isCheckIn()) {
			reservation.setCheckStatus(ReservationCheckStatus.NO_CHECK);
			accept(event);
		} else if (reservation.getCheckStatus() == ReservationCheckStatus.CANCEL_INVOICEABLE) {
			reservation.setCheckStatus(ReservationCheckStatus.CANCEL_NO_INVOICEABLE);
			accept(event);
		}
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

		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
	    	List<Item> inventoryItems = new LinkedList<Item>();
			if (StringUtils.isEmpty(reservation.getCrsCode())) {
		    	inventoryItems = getReservationUtils().getProjectReservationRoomDetailItems(reservationRoom);
		    	if (!inventoryItems.contains(reservationRoom.getItem())) {
		    		inventoryItems.add(reservationRoom.getItem());
		    	}
			}

	    	getReservationUtils().removeProjectReservationRoomDetails(reservationRoom, false, null);

			if (StringUtils.isEmpty(reservation.getCrsCode())) {
				sendInventoryData(reservationRoom, inventoryItems);
			}
		}

		boolean cancelOk = true;
		if (StringUtils.isNotEmpty(reservation.getCrsCode())) {
			ReservationRequestManager requestManager = new ReservationRequestManager();
			cancelOk = requestManager.processBookingCancelRequest(reservation);

			DateFormat dateFormat = new SimpleDateFormat(AonUtil.getMessage(TIMESTAMP_PATTERN));
			reservation.setRemarks((cancelOk ? "OK" : "ERROR") + " CANCEL CRS: " + dateFormat.format(new Date()) + "\n" + reservation.getRemarks());
		}

		reservation.setStatus(ReservationStatus.CANCELLED);
		if (StringUtils.isBlank(reservation.getCancellationUser())) {
			reservation.setCancellationUser(UserUtils.getInstance().getLoggedUser().getLogin());
		}
		if (reservation.getCancellationDate() == null) {
			reservation.setCancellationDate(new Date());
		}
		if (reservation.getPenaltyDays() == null) {
			reservation.setPenaltyDays(obtainCancelPenaltyDays(reservation, isConfirmNoShow()));
			if (isConfirmNoShow()) {
				if (reservation.getPenaltyDays() != null && reservation.getPenaltyDays() == 0 && reservation.getAdvancedAmount() == 0) {
					reservation.setCheckStatus(ReservationCheckStatus.NO_SHOW_NO_INVOICEABLE);
				} else {
					reservation.setCheckStatus(ReservationCheckStatus.NO_SHOW);
				}
			} else {
				if (reservation.getPenaltyDays() != null && reservation.getPenaltyDays() == 0 && reservation.getAdvancedAmount() == 0) {
					reservation.setCheckStatus(ReservationCheckStatus.CANCEL_NO_INVOICEABLE);
				} else {
					reservation.setCheckStatus(ReservationCheckStatus.CANCEL_INVOICEABLE);
				}
			}
		}
		accept(event);

		IController reservationRoomController = (IController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_ROOM_CONTROLLER_NAME);
    	reservationRoomController.onSearch(event);
		IController reservationServiceController = (IController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_SERVICE_CONTROLLER_NAME);
    	reservationServiceController.onSearch(event);

		if (isConfirmNoShow() && reservation.isAgencyHolder()) {
			sendAgencyNoShowEmail(reservation);
		}
	}

    private void sendInventoryData(ProjectReservationRoom reservationRoom, List<Item> inventoryItems) {
    	InventoryManager manager = new InventoryManager();
		for (Item item : inventoryItems) {
	    	manager.processInventoryQuery(reservationRoom, reservationRoom.getHotel(), item);
		}
    }

	private Integer obtainCancelPenaltyDays(ProjectReservation reservation, boolean noShow) throws ManagerBeanException {
		Integer penaltyDays = null;
		if (noShow) {
			penaltyDays = getReservationUtils().obtainNoShowPenaltyDays(reservation, reservation.getStartDate());
		} else {
			penaltyDays = getReservationUtils().obtainCancellationPenaltyDays(reservation, reservation.getCancellationDate());
		}
		if (penaltyDays != null && (penaltyDays < 0 || penaltyDays > CommonUtil.getDaysBetweenDates(reservation.getStartDate(), reservation.getEndDate()))) {
			penaltyDays = (int)CommonUtil.getDaysBetweenDates(reservation.getStartDate(), reservation.getEndDate());
		}
		return penaltyDays;
	}

	private void sendAgencyNoShowEmail(ProjectReservation reservation) throws ManagerBeanException {
		String agencyEmail = getReservationUtils().obtainAgencyAdministrativeEmail(reservation.getAgency());
		if (agencyEmail != null) {
			MailAccount companyMailAccount = obtainNoShowMailAccount();
			if (companyMailAccount != null) {
				try {
					Address companyMailAddress = new InternetAddress(companyMailAccount.getEmail(), companyMailAccount.getDisplayName());
					EmailSender mailSender = new EmailSender(companyMailAddress, companyMailAccount);
					AonMessage message = mailSender.createMessage();
					message.setRecipientsTo(agencyEmail);
					if (StringUtils.isNotBlank(reservation.getHotelReservation().getEmail())) {
						message.setRecipientsCc(reservation.getHotelReservation().getEmail());
					}
					setAgencyNoShowEmailContent(mailSender, message, reservation);
					mailSender.sendMessage(message);
				} catch (Exception ex) {
					setShowConfirmWindow(false);
					setConfirmNoShow(false);
					String msg = "Error al enviar email con la notificación del No Show a la Agencia.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
		}
	}

	public MailAccount obtainNoShowMailAccount() throws ManagerBeanException {
		MailProcess mailProcess = MailProcessUtil.get(MailProcessType.AGENCY_NO_SHOW);
		if (mailProcess != null) {
			return mailProcess.getMailAccount();
		}
		return getReservationUtils().obtainCompanyMailAccount();
	}
	
	public boolean isSendAgencyNoShowEmailEnabled() throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		return reservation.isAgencyHolder() && obtainNoShowMailAccount() != null;
	}

	private String createAgencyNoShowEmailSubject(ProjectReservation reservation) {
		StringBuffer message = new StringBuffer();
		message.append("NO SHOW - ");
		message.append(reservation.getCode() + " - ");
		message.append(StringUtils.substring(reservation.getHotelReservation().getWorkPlace().getDescription(), 0, 30));
		return message.toString();
	}
	
	private Map<String,String> getAgencyNoShowMap(ProjectReservation reservation) throws ManagerBeanException {
		DateFormat formatter = new SimpleDateFormat(AonUtil.getMessage(DATE_PATTERN));

		Map<String,String> agencyNoShowMap = new HashMap<String,String>();		
		agencyNoShowMap.put("hotel_description", reservation.getHotelReservation().getWorkPlace().getDescription());
		agencyNoShowMap.put("reservation_id", reservation.getId().toString());
		agencyNoShowMap.put("reservation_code", reservation.getCode().toString());
		agencyNoShowMap.put("reservation_startDate", formatter.format(reservation.getStartDate()));
		agencyNoShowMap.put("reservation_endDate", formatter.format(reservation.getEndDate()));
		agencyNoShowMap.put("reservation_guestFullName", reservation.getGuestFullName());
		agencyNoShowMap.put("reservation_roomCount", String.valueOf(reservation.getRoomCount()));
		agencyNoShowMap.put("reservation_personCount", String.valueOf(reservation.getPersonCount()));	
		return agencyNoShowMap;
	}
	
	private void setAgencyNoShowEmailContent(EmailSender sender, AonMessage message, ProjectReservation reservation) throws ManagerBeanException, WebmailException {
		String subject = null;
		MailProcess mailProcess = MailProcessUtil.get(MailProcessType.AGENCY_NO_SHOW);
		if (mailProcess != null) {
			Map<String,String> agencyNoShowMap = getAgencyNoShowMap(reservation);
			subject = TemplateController.createSubject(mailProcess.getTemplate(), agencyNoShowMap);
			String content = TemplateController.createContent(mailProcess.getTemplate(), agencyNoShowMap);
			sender.addMessageContent(message, content, MimeType.MIME_HTML);
		} else {
			subject = createAgencyNoShowEmailSubject(reservation);
			String content = createAgencyNoShowEmailMessage(reservation);
			sender.addMessageContent(message, content, MimeType.MIME_TXT);
		}
		message.setSubject(subject);
	}

	private String createAgencyNoShowEmailMessage(ProjectReservation reservation) throws ManagerBeanException {
		DateFormat formatter = new SimpleDateFormat(AonUtil.getMessage(DATE_PATTERN));
		StringBuffer message = new StringBuffer();
		message.append("Le informamos que la Reserva indicada a continuación ha sido marcada como No Show en el Hotel.");
		message.append("\n");
		message.append("Please note that the Booking indicated below has been marked as No Show at the Hotel.");
		message.append("\n");
		message.append("\n");
		message.append("Hotel: " + reservation.getHotelReservation().getWorkPlace().getDescription());
		message.append("\n");
		message.append("Reserva/Booking: " + reservation.getId());
		message.append("\n");
		message.append("Localizador/Code: " + reservation.getCode());
		message.append("\n");
		message.append("Fecha Entrada/Start Date: " + formatter.format(reservation.getStartDate()));
		message.append("\n");
		message.append("Fecha Salida/End Date: " + formatter.format(reservation.getEndDate()));
		message.append("\n");
		message.append("Huésped/Guest: " + reservation.getGuestFullName());
		message.append("\n");
		message.append("Habitaciones/Rooms: " + reservation.getRoomCount());
		message.append("\n");
		message.append("Personas/Pax: " + reservation.getPersonCount());
		message.append("\n");
		message.append("\n");
		message.append("Los gastos por No Show se aplicarán según política establecida por contrato.");
		message.append("\n");
		message.append("Expenses for No Show will apply as established by contract policy.");
		message.append("\n");
		return message.toString();
	}

	public void checkMultipleReservation() throws ManagerBeanException {
		multipleReservation = new LinkedList<Integer>();
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		Criteria criteria = new Criteria();
		criteria.addNotEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_ID), reservation.getId());
		criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_CODE), reservation.getCode());
		criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), reservation.getStartDate());
		criteria.addNotEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.CANCELLED);
		if (reservation.getAgency() != null && reservation.getAgency().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_AGENCY_ID), reservation.getAgency().getId());
		} else {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_BOOKING_HOLDER), BookingHolder.GUEST);
		}
		ProjectionList projectionList = new ProjectionList(Projection.property(getFieldName(IEntityAlias.PROJECT_RESERVATION_ID)));
		for (Object obj : getManagerBean().getList(projectionList, criteria)) {
			multipleReservation.add((Integer)obj);
		}
	}

	public void onLoadMultipleReservation(ActionEvent event) throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		select(event, new Integer(params.get(IPmsConstants.MULTIPLE_RESERVATION)));
	}

	public void onAdvanceInvoiceShow(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		try {
			if (!PosUtils.isUserPosShiftOpened()) {
				setShowAdvanceInvoiceWindow(false);
				String msg = "No se puede Facturar. El Usuario no ha abierto la Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (isReservationAlreadyInvoiced(reservation)) {
				reservation.setStatus(ReservationStatus.INVOICED);
				accept(event);
				setSelectedTab(INVOICE);

				setShowAdvanceInvoiceWindow(false);
				String msg = "La Reserva ya estaba Facturada.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			Item advanceItem = getReservationUtils().obtainAdvanceItem();
			if (advanceItem == null) {
				setShowAdvanceInvoiceWindow(false);
				String msg = "No se puede Facturar. No esta definido el Producto para Anticipos.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			setAdvanceInvoiceTo(new AdvanceInvoiceTo());
			setReservationInvoiceTo(new ReservationInvoiceTo(false));
			getAdvanceInvoiceTo().setReservationInvoiceTo(getReservationInvoiceTo());
			getAdvanceInvoiceTo().setGuestReservation(reservation.isGuestHolder());
			getAdvanceInvoiceTo().setIssueDate(new Date());
			getAdvanceInvoiceTo().setItem(advanceItem);
			getAdvanceInvoiceTo().setAmount(reservation.getAdvance());
			getAdvanceInvoiceTo().setFinanceDate(getAdvanceInvoiceTo().getIssueDate());
			getAdvanceInvoiceTo().setPosShift(PosUtils.getUserPosShift());
			fillInvoiceData(reservation);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onAdvanceInvoice(ActionEvent event) {
		setInvoiceModel(null);
		try {
			if (validateAdvanceInvoice()) {
				ProjectReservation reservation = (ProjectReservation)this.getTo();
				AdvanceInvoicing advanceInvoicing = new AdvanceInvoicing();
				advanceInvoicing.invoice(getAdvanceInvoiceTo(), reservation);

				reservation.setAdvanceInvoiced(true);
				reservation.setAdvance(0);
        		reservation.setAdvancedAmount(null);
        		reservation.setSkipDirtyControl(true);
				accept(event);
				setSelectedTab(INVOICE);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private boolean validateAdvanceInvoice() throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		if (getReservationInvoiceTo().getRegistry().isDocumentValidable() && !getReservationInvoiceTo().getRegistry().isValidDocument()) {
			String msg = AonUtil.getMessage(REGISTRY_DOCUMENT_INCORRECT_ERROR);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (getAdvanceInvoiceTo().getPayMethod() == null) {
			String msg = "La Forma de Pago es obligatoria.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (getAdvanceInvoiceTo().getAmount() <= 0) {
			String msg = "No se puede generar Anticipo. Importe incorrecto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		double pendingAmount = reservation.getPendingAmount();
		if (getAdvanceInvoiceTo().getAmount() > pendingAmount) {
			String msg = "No se puede generar Anticipo. El importe supera el Total pendiente de Facturar.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (getReservationPermission().isRoleUser()) {
			double advanceAmountAllowed = CommonUtil.round(reservation.getTotal() * 0.9 - (reservation.getTotal() - pendingAmount));
			if (getAdvanceInvoiceTo().getAmount() > advanceAmountAllowed) {
				String msg = "No se puede generar Anticipo. El importe supera el 90% del Total de la Reserva.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		return true;
	}

	public void onTouristTaxInvoiceShow(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		try {
			if (!PosUtils.isUserPosShiftOpened()) {
				setShowAdvanceInvoiceWindow(false);
				String msg = "No se puede Facturar. El Usuario no ha abierto la Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (isReservationAlreadyInvoiced(reservation)) {
				reservation.setStatus(ReservationStatus.INVOICED);
				accept(event);
				setSelectedTab(INVOICE);

				setShowAdvanceInvoiceWindow(false);
				String msg = "La Reserva ya estaba Facturada.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			Item touristTaxItem = getReservationUtils().obtainTouristTaxItem();
			if (touristTaxItem == null) {
				setShowTouristTaxInvoiceWindow(false);
				String msg = "No se puede Facturar. No esta definido el Producto para Tasas.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			setReservationInvoiceTo(new ReservationInvoiceTo(true));
			getReservationInvoiceTo().setIssueDate(reservation.getStartDate());
			getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());
			fillTouristTaxInvoiceData(reservation, touristTaxItem);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private void fillTouristTaxInvoiceData(ProjectReservation reservation, Item touristTaxItem) throws ManagerBeanException {
		getReservationInvoiceTo().setRegistry(reservation.getHotel().getCustomer().getRegistry());
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

		getReservationInvoiceTo().setTouristTax(true);
		getReservationInvoiceTo().setHotel(reservation.getHotel());

		getReservationInvoiceTo().setServices(new LinkedList<HotelService>());
		HotelService service = new HotelService(getReservationInvoiceTo());
		service.setItem(touristTaxItem);
		service.setFromDate(reservation.getStartDate());
		service.setToDate(DateUtils.addDays(reservation.getEndDate(), -1));
		service.setQuantity(reservation.getTouristTaxPending());
		getReservationInvoiceTo().getServices().add(service);

		getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
		Finance finance = new Finance();
		finance.setAmount(getReservationUtils().getReservationTouristTaxAmount(reservation, touristTaxItem, true));
		getReservationInvoiceTo().getFinances().add(finance);
	}

	public void onTouristTaxInvoice(ActionEvent event) {
		setInvoiceModel(null);
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		try {
			if (validateTouristTaxInvoice()) {
				getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
				getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));

				ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
				reservationInvoicing.invoice(getReservationInvoiceTo(), reservation);

        		reservation.setTouristTaxPayed(null);
				setSelectedTab(INVOICE);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onTouristTaxFreeShow(ActionEvent event) {
		setReservationInvoiceTo(new ReservationInvoiceTo(true));
		getReservationInvoiceTo().setTouristTaxFreeCause(null);
	}

	public void onTouristTaxFree(ActionEvent event) {
		if (getReservationInvoiceTo().getTouristTaxFreeCause() != null) {
			ProjectReservation reservation = (ProjectReservation)this.getTo();
			reservation.setTouristTaxFree(getReservationInvoiceTo().getTouristTaxFreeCause());
			accept(event);
		}
	}

	private boolean validateTouristTaxInvoice() throws ManagerBeanException {
		if (getReservationInvoiceTo().getRegistry().isDocumentValidable() && !getReservationInvoiceTo().getRegistry().isValidDocument()) {
			String msg = AonUtil.getMessage(REGISTRY_DOCUMENT_INCORRECT_ERROR);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (getReservationInvoiceTo().getFirstFinance().getPayMethod() == null) {
			String msg = "La Forma de Pago es obligatoria.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (getReservationInvoiceTo().getFirstFinance().getAmount() < 0) {
			String msg = "No se puede generar Factura. Importe incorrecto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return true;
	}

	public void onInvoiceShow(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		try {
			if (!PosUtils.isUserPosShiftOpened()) {
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

	@SuppressWarnings("unchecked")
	private void fillInvoiceData(ProjectReservation reservation) throws ManagerBeanException {
		getReservationInvoiceTo().setDirectCustomer(reservation.getProject().getRegistry().getId() == reservation.getHotelReservation().getCustomer().getRegistry().getId());
		getReservationInvoiceTo().setRegistry(reservation.getProject().getRegistry());
		if (getReservationInvoiceTo().isDirectCustomer()) {
			getReservationInvoiceTo().setAddress(new InvoiceAddress());

			boolean invoiceFound = false;
			if (getInvoiceModel() != null && getInvoiceModel().getRowCount() > 0) {
				List<ITransferObject> invoiceList = (List<ITransferObject>)getInvoiceModel().getWrappedData();
				ListIterator<ITransferObject> iterator = invoiceList.listIterator(invoiceList.size());
				while (iterator.hasPrevious()) {
					Invoice invoice = (Invoice)iterator.previous();
					if (!invoice.isService()) {
						invoiceFound = true;
						fillInvoiceModificationData(invoice);
						break;
					}
				}
			} 

			if (!invoiceFound) {
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
		getReservationInvoiceTo().getAddress().setNumber(StringUtils.abbreviate(reservationGuest.getNumber(), 12));
		getReservationInvoiceTo().getAddress().setAddress2(StringUtils.abbreviate(reservationGuest.getAddress2(), 45));
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
		finance.setAmount(CommonUtil.round(getReservationTotal(reservation) - reservation.getAdvancedAmount() - getFinancesAmount()));
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

	private double getReservationTotal(ProjectReservation reservation) throws ManagerBeanException {
		if (!reservation.isEarlyCheckOut()) {
			return reservation.getTotal();
		} else {
			return getReservationUtils().getReservationCalculatedTotal(reservation);
		}
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
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		try {
			if (validateInvoice()) {
				getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
				getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));

				ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
				reservationInvoicing.invoice(getReservationInvoiceTo(), reservation);

				if (reservation.getSavedStatus() != ReservationStatus.INVOICED || reservation.getSavedCheckStatus() == ReservationCheckStatus.NO_CHECK) {
					reservation.setStatus(ReservationStatus.INVOICED);
					reservation.setCheckStatus(reservation.getEndDate().after(new Date()) ? ReservationCheckStatus.CHECK_IN : ReservationCheckStatus.CHECK_OUT);
					reservation.setSkipDirtyControl(true);
					accept(event);
				} else {
					refreshEntireReservation(event);
				}

				setSelectedTab(INVOICE);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private boolean validateInvoice() throws ManagerBeanException {
		if (getReservationInvoiceTo().isDirectCustomer()) {
			if (getReservationInvoiceTo().getRegistry().isDocumentValidable() && !getReservationInvoiceTo().getRegistry().isValidDocument()) {
				String msg = AonUtil.getMessage(REGISTRY_DOCUMENT_INCORRECT_ERROR);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}

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
		return CommonUtil.round(getReservationTotal(reservation) - reservation.getAdvancedAmount() - getFinancesAmount()) == 0;
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
				ProjectReservation reservation = (ProjectReservation)this.getTo();
				return isInvoiceRectificable(invoice, reservation);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
		return false;
	}

	public static boolean isInvoiceRectificable(Invoice invoice, ProjectReservation reservation) throws ManagerBeanException {		
		if (invoice.isNoRectification()) {
			boolean financeOperator = AonUtil.getRoleManager().isFinanceOperator();
			if (invoice.isService()) {
				return financeOperator || invoice.isAllCommercialProducts();
			} else {
				if (invoice.isAdvance()) {
					return financeOperator && (reservation != null) && !reservation.isInvoiced();
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
		if (!PosUtils.isUserPosShiftOpened()) {
			setShowRectificationWindow(false);
			String msg = "No se puede Abonar. El Usuario no ha abierto la Caja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		Invoice invoice = (Invoice)getInvoiceModel().getRowData();
		if (!FinanceUtil.isValidLimitRectificationDate(invoice)) {
			String message = AonUtil.addErrorMessageFromBundle(FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR);
			throw new AbortProcessingException(message);
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
			getReservationInvoiceTo().setEarlyCheckOut(reservation.isEarlyCheckOut());

			ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
			reservationInvoicing.rectify(getInvoiceToRectify(), getReservationInvoiceTo(), false);

			if (reservation.getSavedStatus() != reservation.getStatus() || reservation.getSavedCheckStatus() != reservation.getCheckStatus()) {
				refreshEntireReservation(event);
			}

			if (getInvoiceToRectify().isAdvance()) {
				reservation.setAdvancedAmount(null);
			}
			if (getInvoiceToRectify().isService()) {
				IController reservationServiceController = (IController)AonUtil.getRegisteredBean(RESERVATION_SERVICE_CONTROLLER_NAME);
				reservationServiceController.onSearch(null);
				reservation.setTouristTaxPayed(null);
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
			if (!PosUtils.isUserPosShiftOpened()) {
				setShowModificationWindow(false);
				String msg = "No se puede Modificar. El Usuario no ha abierto la Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			Invoice invoice = (Invoice)getInvoiceModel().getRowData();
			if (!FinanceUtil.isValidLimitRectificationDate(invoice)) {
				String message = AonUtil.addErrorMessageFromBundle(FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR);
				throw new AbortProcessingException(message);
			}

			setInvoiceToModify(invoice);
			setReservationInvoiceTo(new ReservationInvoiceTo(false));
			getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());
			fillInvoiceModificationData(invoice);
			fillInvoiceFinanceModificationData(invoice);
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
	}

	private void fillInvoiceFinanceModificationData(Invoice invoice) throws ManagerBeanException {
		getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
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
					reservationInvoicing.rectify(getInvoiceToRectify(), getReservationInvoiceTo(), false);

					getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
					getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
					reservationInvoicing.duplicate(invoice, reservationInvoiceTo);
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

	/********** CREDIT CARD OPERATIONS / CONEXFLOW **********/
	
	public void onCreditCardShow(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		getReservationUtils().decryptReservationCreditCardData(reservation);
		setShowCreditCardWindow(true);
		setReservationConexFlow(null);
	}

	public List<SelectItem> getCreditCardYears() {
		DateFormat formatter = new SimpleDateFormat("yy");
		List<SelectItem> years = new LinkedList<SelectItem>();
		for (int i=0,year=Integer.parseInt(formatter.format(new Date())); i<=7; i++) {
			SelectItem item = new SelectItem("" + (year+i));
			years.add(item);
		}
		return years;
	}

	public void onEditCreditCard(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setCreditCardNumber(null);
		reservation.setHrCreditCardNumber(null);
		reservation.setCreditCardCvv(null);
		reservation.setHrCreditCardCvv(null);
	}

	public void onSaveCreditCard(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		if (validateCreditCard(reservation.getHrCreditCardNumber())) {
			getReservationUtils().encryptReservationCreditCardData(reservation);
			accept(event);
			if (conexFlowData(reservation)) {
				setShowCreditCardWindow(false);
				setReservationConexFlow(null);
			}
		}
	}

	public void onConexflowOperation(ActionEvent event) {
		System.out.println("CONEXFLOW OPERATION "+ getReservationConexFlow().getConexflowOperation());
		ProjectReservation reservation = (ProjectReservation)this.getTo();		
		ConexFlowConnection connection = DBConsults.getConection(getDomain(reservation));
		if (connection.getActive()) {
			ConexFlow conexFlow = null;
			String errorMsg = null;
			Query query = null;
			
			ConexFlow cf = DBConsults.getConexFlowLastOperation(getDomain(reservation), AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.CREATE_TOKEN_OP);
			if(cf == null || cf.getRespuesta() == null){
				AonUtil.addErrorMessage("Error al realizar la operación");
				throw new AbortProcessingException("Error al realizar la operación");
			}
			String token = cf.getRespuesta().getToken();
			
			switch (getReservationConexFlow().getConexflowOperation()) {
			case ConexFlowConstant.PREAUTHORIZATION_OP:	
				query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection.getEmpresa().toString()
						, connection.getCentro().toString(), connection.getTpv().toString()
						, reservation.getCustomer().getId().toString(), token, getReservationConexFlow().getAmount());
				conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.PREAUTHORIZATION_OP, query, reservation.getId(), getDomain(reservation), false);
				if(conexFlow == null)
					errorMsg = "Error al realizar la operación"; 
				else if (!conexFlow.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)) {
					errorMsg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
				} 
				break;
			case ConexFlowConstant.SALE_OP: 
				query = ConexFlowUtils.getConexFlowCardPaymentQuery(connection.getEmpresa().toString()
						, connection.getCentro().toString(), connection.getTpv().toString(), token, getReservationConexFlow().getAmount()
						, reservation.getCustomer().getId().toString(), reservation.getCreditCardCvv());
				conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.	SALE_OP, query, reservation.getId(), getDomain(reservation), false);
				if(conexFlow == null)
					errorMsg = "Error al realizar la operación"; 
				else if (!conexFlow.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK))
					errorMsg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
				else getReservationConexFlow().onCollect();
				break;
			case ConexFlowConstant.CANCELATION_OP: 
				ConexFlow cf2 = null;
				if(getReservationConexFlow().getConexflowOperationCancelation().equals(ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP))
					cf2 = DBConsults.getConexFlowLastOperation(getDomain(reservation), AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
				if(getReservationConexFlow().getConexflowOperationCancelation().equals(ConexFlowConstant.PREAUTHORIZATION_OP))
					cf2 =  DBConsults.getConexFlowLastOperation(getDomain(reservation), AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
				if(getReservationConexFlow().getConexflowOperationCancelation().equals(ConexFlowConstant.SALE_OP))
					cf2 =  DBConsults.getConexFlowLastOperation(getDomain(reservation), AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.SALE_OP);
				if(getReservationConexFlow().getConexflowOperationCancelation().equals(ConexFlowConstant.REFUND_OP))
					cf2 =  DBConsults.getConexFlowLastOperation(getDomain(reservation), AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.REFUND_OP);
				
				query = ConexFlowUtils.getConexFlowCancelationQuery(connection.getEmpresa().toString()
						, connection.getCentro().toString(), connection.getTpv().toString()
						, cf2.getRespuesta().getOperacion(), getReservationConexFlow().getAmount(),Double.parseDouble(cf2.getRespuesta().getImporte()) 
						, cf2.getRespuesta().getAutorizacion(), reservation.getCustomer().getId().toString()
						, cf2.getRespuesta().getIdOperacion(), cf2.getRespuesta().getFecha());
				conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.	CANCELATION_OP, query, reservation.getId(), getDomain(reservation), false);
				
				if(conexFlow == null)
					errorMsg = "Error al realizar la operación"; 
				else if (!conexFlow.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)) 
					errorMsg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
				else if(getReservationConexFlow().getConexflowOperationCancelation().equals(ConexFlowConstant.REFUND_OP)){
					getReservationConexFlow().onRefundCancel();
				}
				if(conexFlow != null && conexFlow.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK))
					getReservationConexFlow().onCancel();
				break;
			case ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP: 
				ConexFlow cf3 =  DBConsults.getConexFlowLastOperation(getDomain(reservation), AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
				query = ConexFlowUtils.getConexFlowConfirmPreauthorizationQuery(connection.getEmpresa().toString()
						, connection.getCentro().toString(), connection.getTpv().toString()
						, reservation.getCustomer().getId().toString(), token, getReservationConexFlow().getAmount()
						, Double.parseDouble(cf3.getRespuesta().getImporte()), cf3.getRespuesta().getCF_ExpirationDate()
						, cf3.getRespuesta().getAutorizacion(), cf3.getRespuesta().getFecha()
						, cf3.getRespuesta().getIdOperacion());
				conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP, query, reservation.getId(), getDomain(reservation), false);
				if(conexFlow == null)
					errorMsg = "Error al realizar la operación"; 
				else if (!conexFlow.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)) 
					errorMsg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
				else getReservationConexFlow().onCollect();
				break;
			case ConexFlowConstant.REFUND_OP: 
				//TODO TENER ENCUENTA EL CARGO O LA CONFIRM PREAUTHO..
				query = ConexFlowUtils.getConexFlowRefundQuery(connection.getEmpresa().toString()
						, connection.getCentro().toString(), connection.getTpv().toString()
						, token, getReservationConexFlow().getAmount().toString(), reservation.getCustomer().getId().toString());
				conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.REFUND_OP, query, reservation.getId(), getDomain(reservation), false);
				if(conexFlow == null)
					errorMsg = "Error al realizar la operación"; 
				else if (!conexFlow.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)) 
					errorMsg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
				else getReservationConexFlow().onRefund();
				break;
			default:
				
				break;
			}
			
			if (errorMsg != null) {
				AonUtil.addErrorMessage(errorMsg);
				throw new AbortProcessingException(errorMsg);
			}
		}
		setShowNotifyWindow(true);
	}

	private boolean validateCreditCard(String creditCardNumber) {
		boolean valid = false;
		if (NumberUtils.isNumber(creditCardNumber)) {
			int length = creditCardNumber.length();
			int control = 0;
			for (int i=length-1; i>=0; i--) {
				int digit = Integer.parseInt(StringUtils.substring(creditCardNumber, i,i+1));
				if ((length - i) % 2 == 0) {
					digit = (digit * 2 < 10) ? digit * 2 : digit * 2 - 9;
				}
				control += digit;
			}
			if (control % 10 == 0) {
				valid = true;
			}
		}
		
		if (!valid) {
			String msg = "Número de Tarjeta no válido.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return valid;
	}

	private boolean conexFlowData(ProjectReservation reservation) {
		ConexFlowConnection connection = DBConsults.getConection(getDomain(reservation));
		if (connection.getActive()) {
			ConexFlow conexFlowCreateToken = null;
			String errorMsg = null;
			Query createTokenQuery = ConexFlowUtils.getConexFlowCreateTokenQuery(reservation.getHrCreditCardNumber()
					, connection.getEmpresa().toString(), connection.getCentro().toString(), connection.getTpv().toString()
					, reservation.getHrCreditCardExpirationMonth() + reservation.getHrCreditCardExpirationYear()
					, reservation.getCustomer().getId().toString());
			conexFlowCreateToken = ConexFlowPost.execute(connection,ConexFlowConstant.CREATE_TOKEN_OP, createTokenQuery, reservation.getId(), getDomain(reservation), false);
			if (!conexFlowCreateToken.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)) {
				errorMsg = "Error " + conexFlowCreateToken.getRespuesta().getResultado() + ": " + conexFlowCreateToken.getRespuesta().getDesResultado() + ".";
			}
			else{
				Query q ;
				ConexFlow cf;
				if(isAmex(reservation.getHrCreditCardNumber())) {
					q = ConexFlowUtils.getConexFlowCardPaymentQuery(connection.getEmpresa().toString()
							, connection.getCentro().toString(), connection.getTpv().toString()
							, conexFlowCreateToken.getRespuesta().getToken(), (Double) 0.01, reservation.getCustomer().getId().toString()
							, reservation.getHrSecureCreditCardNumber());
					cf = ConexFlowPost.execute(connection, ConexFlowConstant.SALE_OP, q, reservation.getId(), getDomain(reservation), true);
				}
				else {
					q = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery( connection.getEmpresa().toString()
						, connection.getCentro().toString(), connection.getTpv().toString()
						, reservation.getCustomer().getId().toString(), conexFlowCreateToken.getRespuesta().getToken(), (Double) 0.01);
					cf = ConexFlowPost.execute(connection,ConexFlowConstant.PREAUTHORIZATION_OP, q, reservation.getId(), getDomain(reservation), true);
				}
				if (!cf.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)) {
					errorMsg = "Error " + cf.getRespuesta().getResultado() + ": " + cf.getRespuesta().getDesResultado() + ".";
				} else{
					Query query;
					ConexFlow cf2;
					if(isAmex(reservation.getHrCreditCardNumber())){
						Double amount = (Double) 0.01;
						query = ConexFlowUtils.getConexFlowRefundQuery(connection.getEmpresa().toString()
								, connection.getCentro().toString(), connection.getTpv().toString()
								, conexFlowCreateToken.getRespuesta().getToken(), amount.toString() 
								, reservation.getCustomer().getId().toString());
						cf2 = ConexFlowPost.execute(connection, ConexFlowConstant.REFUND_OP, query,  reservation.getId(), getDomain(reservation), true);

					}else{
						query = ConexFlowUtils.getConexFlowCancelationQuery(connection.getEmpresa().toString()
								, connection.getCentro().toString(), connection.getTpv().toString()
								, ConexFlowConstant.PREAUTHORIZATION_OP, (Double) 0.01
								, (Double) 0.01, cf.getRespuesta().getAutorizacion()
								, cf.getRespuesta().getRefClient(), cf.getRespuesta().getIdOperacion(), cf.getRespuesta().getFecha());
						cf2 = ConexFlowPost.execute(connection, ConexFlowConstant.CANCELATION_OP, query,  reservation.getId(), getDomain(reservation), true);
					}
					if(cf2 != null){
						if (!cf2.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)) 
							errorMsg = "Error " + cf2.getRespuesta().getResultado() + ": " + cf2.getRespuesta().getDesResultado() + ".";
					}
					else errorMsg = "Los datos de conexión a conexFlow son incorrectos.";	
				}
			}
			
			if (errorMsg != null) {
				deleteReservationCreditCard(reservation);
				AonUtil.addErrorMessage(errorMsg);
				throw new AbortProcessingException(errorMsg);
			}
		}
		return true;
	}
	
	public void deleteReservationCreditCard(ProjectReservation reservation){
		Domain domain = getDomain(reservation);
		AON.deleteReservationCreditCard(domain.getName(), domain.getId(), AonUtil.getRemoteUser(), reservation.getId());
		reservation.setCreditCardCvv(null);
		reservation.setHrCreditCardCvv(null);
		reservation.setCreditCardExpirationMonth(null);
		reservation.setHrCreditCardExpirationMonth(null);
		reservation.setCreditCardExpirationYear(null);
		reservation.setHrCreditCardExpirationYear(null);
		reservation.setCreditCardHolder(null);
		reservation.setHrCreditCardHolder(null);
		reservation.setCreditCardNumber(null);
		reservation.setHrCreditCardNumber(null);
	}
	
	public void onCreditCardPreauthorizationShow(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		ConexFlow cf = DBConsults.getConexFlowLastOperation(getDomain(reservation), AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.CREATE_TOKEN_OP);
		ConexFlow cf2 = DBConsults.getConexFlowLastOperation(getDomain(reservation), AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
		setCardOperationTo(new CardOperationTo());
		getCardOperationTo().setCardNumber(cf.getRespuesta().getCF_PAN());
		if (cf2 != null) {
			try {
				String[] patterns = {"ddMMyyyy", "dd/MM/yyyy", "ddMMyy", "dd/MM/yy"};
				getCardOperationTo().setOperationDate(DateUtils.parseDateStrictly(cf2.getRespuesta().getFecha(), patterns));
				if (cf2.getRespuesta().getImporte() != null) {
					getCardOperationTo().setOperationAmount(Double.parseDouble(cf2.getRespuesta().getImporte()));
				}
				getCardOperationTo().setOperationOk(true);
			} catch (ParseException ex) {
				String errorMsg = "Formato de fecha no valido";
				AonUtil.addErrorMessage(errorMsg);
				throw new AbortProcessingException(errorMsg);
			}
		}
	}

	public void onCreditCardPreauthorization(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		ConexFlowConnection connection = DBConsults.getConection(getDomain(reservation));
		if(connection.getActive()){
			String errorMsg = null;
			if (reservation.getTotal() >= getCardOperationTo().getOperationAmount()) {
				ConexFlow cf = DBConsults.getConexFlowLastOperation(getDomain(reservation),  AonUtil.getRemoteUser(),
						reservation.getId(), ConexFlowConstant.CREATE_TOKEN_OP);
				Query query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(
					connection.getEmpresa().toString(), connection.getCentro().toString(), connection.getTpv().toString()
					, reservation.getCustomer().getId().toString(), cf.getRespuesta().getToken()
					, getCardOperationTo().getOperationAmount());
				ConexFlow cf2 = ConexFlowPost.execute(connection, ConexFlowConstant.PREAUTHORIZATION_OP, query, reservation.getId(), getDomain(reservation), false);
				if(cf2 != null){
					if (cf2.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)) 
						setShowCreditCardPreauthorizationWindow(false);
					else errorMsg = "Error " + cf2.getRespuesta().getResultado() + ": " + cf2.getRespuesta().getDesResultado() + ".";
				}
				else errorMsg = "Los datos de conexión a conexFlow son incorrectos.";	
			} else errorMsg = "El importe a pre-autorizar es mayor que el importe de la Reserva.";
			
			if(errorMsg != null){
				AonUtil.addErrorMessage(errorMsg);
				throw new AbortProcessingException(errorMsg);
			}
		}
	}
	
	public void onCreditCardCancelPreauthorization(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		ConexFlowConnection connection = DBConsults.getConection(getDomain(reservation));
		if(connection.getActive()){
			String errorMsg = null;
			ConexFlow cf = DBConsults.getConexFlowLastOperation(getDomain(reservation), AonUtil.getRemoteUser(), reservation.getId(), ConexFlowConstant.PREAUTHORIZATION_OP);
			DBConsults.deletePreuthorization(getDomain(reservation), reservation.getId());
			
			Double importe = Double.parseDouble(cf.getRespuesta().getImporte());
			Query query = ConexFlowUtils.getConexFlowCancelationQuery(connection.getEmpresa().toString()
				, connection.getCentro().toString(), connection.getTpv().toString()
				, ConexFlowConstant.PREAUTHORIZATION_OP, importe
				, importe, cf.getRespuesta().getAutorizacion()
				, cf.getRespuesta().getRefClient(), cf.getRespuesta().getIdOperacion(), cf.getRespuesta().getFecha());
		
			ConexFlow cf2 = ConexFlowPost.execute(connection, ConexFlowConstant.CANCELATION_OP, query, reservation.getId(), getDomain(reservation), false);
			if(cf2 != null){
				if (cf2.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)) 
					setShowCreditCardPreauthorizationWindow(false);
				else errorMsg = "Error " + cf2.getRespuesta().getResultado() + ": " + cf2.getRespuesta().getDesResultado() + ".";
			}
			else errorMsg = "Los datos de conexión a conexFlow son incorrectos.";	
			
			if(errorMsg != null){
				AonUtil.addErrorMessage(errorMsg);
				throw new AbortProcessingException(errorMsg);
			}
		}
	}

	
	public void onConexflowOperationChange(ActionEvent event) {
		if(getReservationConexFlow().getConexflowOperation().equals(ConexFlowConstant.CANCELATION_OP)){
			getReservationConexFlow().setShowCancelationOption(true);
			getReservationConexFlow().setShowRefundOption(false);
		}
		else if(getReservationConexFlow().getConexflowOperation().equals(ConexFlowConstant.REFUND_OP)){
			getReservationConexFlow().setShowCancelationOption(false);
			getReservationConexFlow().setShowRefundOption(true);
		}
		else{
			getReservationConexFlow().setShowCancelationOption(false);
			getReservationConexFlow().setShowRefundOption(false);
		}
	}
	
	private Boolean isAmex(String creditCard){
		return creditCard.substring(0, 2).equals("37") || creditCard.substring(0, 2).equals("34");
	}
	
	private Domain getDomain(ProjectReservation reservation) {
		Domain domain = new Domain();
		domain.setName(AonUtil.getDomainName());
		domain.setId(reservation.getDomain());
		return domain;
	}

}
