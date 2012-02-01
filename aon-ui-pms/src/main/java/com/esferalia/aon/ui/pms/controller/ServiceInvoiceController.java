package com.esferalia.aon.ui.pms.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.reservation.ReservationInvoiceTo;
import com.esferalia.aon.pms.reservation.ReservationInvoicing;

public class ServiceInvoiceController extends BasicController implements ICalculableContainer {

	private ReservationInvoiceTo reservationInvoiceTo;
	private boolean showRectificationWindow;
	private Invoice invoiceToRectificate;

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

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), new Date());
		onSearch(event);
	}

	@Override
	public void onReset(ActionEvent event) {
		setNew(true);
		try {
			setReservationInvoiceTo(new ReservationInvoiceTo());
			fillInvoiceData();
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private void fillInvoiceData() throws ManagerBeanException {
		getReservationInvoiceTo().setHotel(obtainHotel());
		fillHotelData();
		getReservationInvoiceTo().getRegistry().setId(getReservationInvoiceTo().getHotel().getCustomer().getRegistry().getId());
		onNewService(null);
	}

	private Hotel obtainHotel() throws ManagerBeanException {
		PmsCollectionsController collections = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		List<SelectItem> hotelList = collections.getCurrentUserHotels();
		return (hotelList.size() > 0) ? (Hotel)hotelList.get(0).getValue() : null;
	}

	public void onInvoiceHotelChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			getReservationInvoiceTo().setHotel((Hotel)event.getNewValue());
			fillHotelData();

			getReservationInvoiceTo().setServices(new LinkedList<InvoiceDetail>());
			onNewService(null);
		}
	}

	private void fillHotelData() throws ManagerBeanException {
		getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
		getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
		getReservationInvoiceTo().setRoom(null);
		getReservationInvoiceTo().setGuest(null);
	}

	private String obtainHotelInvoiceSeries() throws ManagerBeanException {
		List<ITransferObject> seriesList = getHotelSeries(false);
		return (seriesList.size() > 0) ? ((Series)seriesList.get(0)).getCode() : "";
	}

	private String obtainHotelRectificationSeries() throws ManagerBeanException {
		List<ITransferObject> seriesList = getHotelSeries(true);
		return (seriesList.size() > 0) ? ((Series)seriesList.get(0)).getCode() : "";
	}

	public List<ITransferObject> getHotelSeries(boolean rectification) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), getReservationInvoiceTo().getHotel().getScope().getId());
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), new Boolean(true));
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		if (rectification) {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_RECTIFICATION), new Boolean(true));
		} else {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), new Boolean(true));
		}
		return seriesBean.getList(criteria);
	}

	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	public List<SelectItem> getRoomList() throws ManagerBeanException {
		List<SelectItem> roomList = new LinkedList<SelectItem>();
		if (getReservationInvoiceTo().getHotel() != null) {
			IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
			Criteria criteria = new Criteria();
			String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_HOTEL_ID);
			criteria.addEqualExpression(alias, getReservationInvoiceTo().getHotel().getId());
			alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE);
			criteria.addEqualExpression(alias, getReservationInvoiceTo().getIssueDate());
			criteria.addOrder(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ASSET_NAME));
			for (ITransferObject ito : reservationRoomDetailBean.getList(criteria)) {
				ProjectReservationRoomDetail reservationRoomDetail = (ProjectReservationRoomDetail)ito;
				SelectItem selectItem = new SelectItem(reservationRoomDetail, reservationRoomDetail.getRoom().getAsset().getName());
				roomList.add(selectItem);
			}
		}
		return roomList;
	}

	public void onInvoiceRoomChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			getReservationInvoiceTo().setRoom((ProjectReservationRoomDetail)event.getNewValue());
			getReservationInvoiceTo().setServiceFromDate(getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation().getStartDate());

			Calendar toCalendar = new GregorianCalendar();
			toCalendar.setTime(getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation().getEndDate());
			toCalendar.add(Calendar.DATE, -1);
			getReservationInvoiceTo().setServiceToDate(toCalendar.getTime());

			getReservationInvoiceTo().setServices(new LinkedList<InvoiceDetail>());
			onNewService(null);
		}
	}

	public List<SelectItem> getGuests() throws ManagerBeanException {
		List<SelectItem> guestList = new LinkedList<SelectItem>();
		if (getReservationInvoiceTo().getRoom() != null) {
			IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
			Criteria criteria = new Criteria();
			String alias = reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID);
			criteria.addEqualExpression(alias, getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation().getId());
			criteria.addOrder(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX));
			for (ITransferObject ito : reservationGuestBean.getList(criteria)) {
				ProjectReservationGuest reservationGuest = (ProjectReservationGuest)ito;
				SelectItem selectItem = new SelectItem(reservationGuest, reservationGuest.getFullName());
				guestList.add(selectItem);
			}
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

	public void onServiceFromDateChanged(ActionEvent event) {
	}

	public void onServiceToDateChanged(ActionEvent event) {
	}

	public void onNewService(ActionEvent event) {
		try {
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setItem((Item)getHotelServices().get(0).getValue());
			invoiceDetail.setQuantity(1);
			invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
			getReservationInvoiceTo().getServices().add(invoiceDetail);
			onInvoiceServiceChanged(null);
		} catch (ManagerBeanException ex) {
			String msg = "Error al seleccionar Servicio.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public int getInvoiceServicesCount() {
		return getReservationInvoiceTo().getServices().size();
	}

	public List<SelectItem> getHotelServices() throws ManagerBeanException {
		List<SelectItem> servicesList = new LinkedList<SelectItem>();
		if (getReservationInvoiceTo().getHotel() != null) {
			IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
			Criteria criteria = new Criteria();
			String alias = catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID);
			criteria.addEqualExpression(alias, getReservationInvoiceTo().getHotel().getServiceCatalogue().getId());
			criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_PRODUCT_NAME));
			for (ITransferObject ito : catalogueItemBean.getList(criteria)) {
				CatalogueItem catalogueItem = (CatalogueItem)ito;
				SelectItem selectItem = new SelectItem(catalogueItem.getItem(), catalogueItem.getItem().getProduct().getName());
				servicesList.add(selectItem);
			}
		}
		return servicesList;
	}

	public void onInvoiceServiceChanged(ActionEvent event) {
		getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
		onNewFinance(null);
	}

	public boolean verifyServicesDates() {
		Date from = getReservationInvoiceTo().getServiceFromDate();
		Date to = getReservationInvoiceTo().getServiceToDate();
		ProjectReservation reservation = null;
		if (getReservationInvoiceTo().getRoom()!=null) {
			reservation = getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation();
		}

		String msg = "";
		if (from.compareTo(to) > 0) {
			msg = "La fecha de inicio del Servicio no puede ser mayor que la fecha de fin.";
		} else if (reservation != null && (from.compareTo(reservation.getStartDate()) < 0 || to.compareTo(reservation.getEndDate()) > 0)) {
			msg = "Las fechas del Servicio no estan dentro de la Reserva.";
		} else if (CommonUtil.getDaysBetweenDates(from, to) > 30) {
			msg = "No se puede facturar un servicio de mas de 30 dias.";
		} else {
			return true;
		}

		AonUtil.addErrorMessage(msg);
		throw new AbortProcessingException(msg);
	}

	private double getServicesAmount() {
		IPriceStrategy strategy = PriceStrategyFactory.getPriceStrategy();
		for (InvoiceDetail invoiceService : getReservationInvoiceTo().getServices()) {
			if (invoiceService.getItem() != null) {
				double prices = 0;
				Calendar fromCalendar = new GregorianCalendar();
				fromCalendar.setTime(getReservationInvoiceTo().getServiceFromDate());
				Calendar toCalendar = new GregorianCalendar();
				toCalendar.setTime(getReservationInvoiceTo().getServiceToDate());
				while (fromCalendar.compareTo(toCalendar) <= 0) {
					prices += strategy.getUnitPrice(invoiceService, fromCalendar.getTime(), getReservationInvoiceTo().getHotel().getCustomer().getTariff());
					fromCalendar.add(Calendar.DATE, 1);
				}
				invoiceService.setPrice(CommonUtil.round(prices, 4));
				invoiceService.setTaxableBase(strategy.getBasePrice(invoiceService));
			}
		}
		return strategy.getTotalPrice(this, getReservationInvoiceTo().getHotel().getCustomer());
	}

	public void onNewFinance(ActionEvent event) {
		Finance finance = new Finance();
		double amount = CommonUtil.round(getServicesAmount() - getFinancesAmount());
		finance.setAmount(amount);
		getReservationInvoiceTo().getFinances().add(finance);
	}

	private double getFinancesAmount() {
		double amount = 0;
		for (Finance finance : getReservationInvoiceTo().getFinances()) {
			amount += CommonUtil.round(finance.getAmount());
		}
		return CommonUtil.round(amount);
	}

	public boolean isFinancesAmountOk() {
		return CommonUtil.round(getServicesAmount() - getFinancesAmount()) == 0;
	}

	public Finance getLastInvoiceFinance() {
		return getReservationInvoiceTo().getFinances().get(getReservationInvoiceTo().getFinances().size()-1);
	}

	public void onInvoice(ActionEvent event) {
		if (verifyServicesDates()) {
			if (isFinancesAmountOk()) {
				try {
					ProjectReservation reservation = null;
					if (getReservationInvoiceTo().getRoom() != null) {
						reservation = getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation();
					}
	
					ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
					Invoice invoice = reservationInvoicing.invoice(getReservationInvoiceTo(), reservation, true);

					onEditSearch(event);
					getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
					onSearch(event);
					getModel().setRowIndex(0);
					onSelect(event);
				} catch (ManagerBeanException ex) {
					AonUtil.addErrorMessage(ex.getMessage());
					throw new AbortProcessingException(ex.getMessage(), ex);
				}
			} else {
				String msg = "El importe de los Pagos no coincide con el importe de los Servicios.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
	}

	public void onRectifyInvoiceShow(ActionEvent event) {
		if (!model.isRowAvailable()) {
			setShowRectificationWindow(false);
			String msg = "No se puede Abonar. Factura no disponible.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		try {
			setInvoiceToRectificate((Invoice)getModel().getRowData());
			setReservationInvoiceTo(new ReservationInvoiceTo());
			getReservationInvoiceTo().setHotel(obtainRectificationHotel());
			getReservationInvoiceTo().setSeries(obtainHotelRectificationSeries());
			getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private Hotel obtainRectificationHotel() throws ManagerBeanException {
		for (ITransferObject ito : getInvoiceToRectificate().getDetailList()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_ID), invoiceDetail.getWorkPlace().getId());
			for (ITransferObject itr : hotelBean.getList(criteria)) {
				return (Hotel)itr;
			}
		}
		return null;
	}

	public void onRectify(ActionEvent event) {
		try {
			ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
			Invoice rectifier = reservationInvoicing.rectify(getInvoiceToRectificate(), getReservationInvoiceTo());

			onEditSearch(event);
			getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), rectifier.getId());
			onSearch(event);
			getModel().setRowIndex(0);
			onSelect(event);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onPrintInvoice(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BasicController controller = (BasicController) ((IController)AonUtil.getRegisteredBean(IPmsConstants.SALE_INVOICE_CONTROLLER_NAME));
			controller.select(event, ((Invoice)getModel().getRowData()).getId());
		}
	}


	@Override
	public Date getDate() {
		return getReservationInvoiceTo().getIssueDate();
	}

	@Override
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0.0");
	}

	@Override
	public List<?> getDetailList() {
		return getReservationInvoiceTo().getServices();
	}

}