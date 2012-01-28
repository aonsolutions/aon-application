package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import com.code.aon.registry.IAddress;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.reservation.ReservationInvoicing;

public class ProjectReservationController extends BasicController {

	private String selectedTab;
	private int nights;
	private String guestName;
	private String guestSurname;
	private Item roomItem;
	private Tariff roomTariff;
	private boolean showInvoiceWindow;
	private String invoiceSeries;
	private int invoiceNumber;
	private Registry invoiceRegistry;
	private IAddress invoiceAddress;
	private List<Finance> invoiceFinances;
	private boolean showRectificationWindow;
	private Invoice invoiceToRectificate;
	private Date rectificationDate;
	private String rectificationCause;
	private DataModel invoiceModel;

	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
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

	public String getInvoiceSeries() {
		return invoiceSeries;
	}

	public void setInvoiceSeries(String invoiceSeries) {
		this.invoiceSeries = invoiceSeries;
	}

	public int getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(int invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Registry getInvoiceRegistry() {
		return invoiceRegistry;
	}

	public void setInvoiceRegistry(Registry invoiceRegistry) {
		this.invoiceRegistry = invoiceRegistry;
	}

	public IAddress getInvoiceAddress() {
		return invoiceAddress;
	}

	public void setInvoiceAddress(IAddress invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
	}

	public List<Finance> getInvoiceFinances() {
		return invoiceFinances;
	}

	public void setInvoiceFinances(List<Finance> invoiceFinances) {
		this.invoiceFinances = invoiceFinances;
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

	public Date getRectificationDate() {
		return rectificationDate;
	}

	public void setRectificationDate(Date rectificationDate) {
		this.rectificationDate = rectificationDate;
	}

	public String getRectificationCause() {
		return rectificationCause;
	}

	public void setRectificationCause(String rectificationCause) {
		this.rectificationCause = rectificationCause;
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

	public void resetHotel() throws ManagerBeanException {
		PmsCollectionsController collections = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		if (collections.getCurrentUserHotelsCount() > 0) {
			ProjectReservation reservation = (ProjectReservation)getTo();
			reservation.setHotel((Hotel)collections.getCurrentUserHotels().get(0).getValue());
		}
	}

	public void onHotelChanged(ValueChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setHotel((Hotel)event.getNewValue());
			resetRoomTariff();
		}
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

	public void onHolderChanged(ValueChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setBookingHolder((BookingHolder)event.getNewValue());
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

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), new Date());
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.ACTIVE);
		onSearch(event);
	}

	public void onBlock(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setStatus(ReservationStatus.BLOCKED);
		accept(event);
	}
	
	public void onUnblock(ActionEvent event) throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setStatus(ReservationStatus.ACTIVE);
		accept(event);
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
		setInvoiceSeries(obtainHotelInvoiceSeries());
		setInvoiceNumber(obtainSeriesMaxNumber(getInvoiceSeries()));

		setInvoiceRegistry(reservation.getProject().getRegistry());
		if (reservation.getProject().getRegistry().getId() == reservation.getHotel().getCustomer().getRegistry().getId()) {
			IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), reservation.getId());
			criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX), 1);
			for (ITransferObject ito : reservationGuestBean.getList(criteria)) {
				ProjectReservationGuest reservationGuest = (ProjectReservationGuest)ito;
				getInvoiceRegistry().setName(reservationGuest.getFullName());

				setInvoiceAddress(new InvoiceAddress());
				getInvoiceAddress().setAddress(StringUtils.abbreviate(reservationGuest.getAddress(), 45));
				getInvoiceAddress().setZip(StringUtils.abbreviate(reservationGuest.getZip(), 16));
				getInvoiceAddress().setCity(StringUtils.abbreviate(reservationGuest.getCity(), 45));
				getInvoiceAddress().setProvince(StringUtils.abbreviate(reservationGuest.getProvince(), 45));
			}
		} else {
			setInvoiceAddress(getInvoiceRegistry().getDefaultAddress());
		}

		setInvoiceFinances(new LinkedList<Finance>());
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
			setInvoiceSeries((String)event.getNewValue());
			obtainSeriesMaxNumber(getInvoiceSeries());
		}
	}

	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	public void onNewFinance(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		Finance finance = new Finance();
		double amount = CommonUtil.round(reservation.getTotal() - getFinancesAmount());
		finance.setAmount(amount);
		getInvoiceFinances().add(finance);
	}

	private double getFinancesAmount() {
		double amount = 0;
		for (Finance finance : getInvoiceFinances()) {
			amount += CommonUtil.round(finance.getAmount());
		}
		return CommonUtil.round(amount);
	}

	public boolean isFinancesAmountOk() {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		return CommonUtil.round(reservation.getTotal() - getFinancesAmount()) == 0;
	}

	public Finance getLastInvoiceFinance() {
		return getInvoiceFinances().get(getInvoiceFinances().size()-1);
	}

	public void onInvoice(ActionEvent event) {
		setInvoiceModel(null);
		if (isFinancesAmountOk()) {
			ProjectReservation reservation = (ProjectReservation)this.getTo();
			try {
				ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
				reservationInvoicing.invoice(reservation, getInvoiceSeries(), getInvoiceNumber(), getInvoiceRegistry(), getInvoiceAddress(), getInvoiceFinances());
	
				reservation.setStatus(ReservationStatus.INVOICED);
				accept(event);
			} catch (ManagerBeanException ex) {
				AonUtil.addErrorMessage(ex.getMessage());
				throw new AbortProcessingException(ex.getMessage(), ex);
			}
		} else {
			String msg = "El importe de los Pagos no coincide con el importe de la Reserva.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
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
			setInvoiceSeries(obtainHotelRectificationSeries());
			setInvoiceNumber(obtainSeriesMaxNumber(getInvoiceSeries()));
			setRectificationDate(new Date());
			setRectificationCause(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onRectify(ActionEvent event) {
		setInvoiceModel(null);
		try {
			ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
			reservationInvoicing.rectify(getInvoiceToRectificate(), getInvoiceSeries(), getInvoiceNumber(), getRectificationDate(), getRectificationCause());
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onPrintInvoice(ActionEvent event) throws ManagerBeanException {
		if (invoiceModel.isRowAvailable()) {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID), ((Invoice)getInvoiceModel().getRowData()).getId());

			((IController)AonUtil.getRegisteredBean(IPmsConstants.SALE_INVOICE_CONTROLLER_NAME)).setCriteria(criteria);
		}
	}

}