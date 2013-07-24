package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.invoicing.ReservationInvoiceTo;
import com.esferalia.aon.pms.invoicing.ReservationInvoiceTo.HotelService;
import com.esferalia.aon.pms.invoicing.ReservationInvoicing;
import com.esferalia.aon.ui.pms.IPmsMessages;

public class ServiceInvoiceController extends BasicController implements IPmsConstants, ICalculableContainer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInvoiceController.class);

	private ReservationInvoiceTo reservationInvoiceTo;
	private boolean showRectificationWindow;
	private Invoice invoiceToRectify;
	private ProjectReservation projectReservation;
	
	private IControllerListener currentReservationFilter;
	
	public ProjectReservation getProjectReservation() {
		return projectReservation;
	}

	public void setProjectReservation(ProjectReservation projectReservation) {
		this.projectReservation = projectReservation;
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
	
	public IControllerListener getCurrentReservationFilter() {
		if ( this.currentReservationFilter == null ) {
			this.currentReservationFilter = new ControllerAdapter() {
				@Override
				public void beforeModelSearched(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.BLOCKED);
						controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.CANCELLED);
						controller.getCriteria().addLessThanOrEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), new Date());
						controller.getCriteria().addGreaterThanOrEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_END_DATE), new Date());
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering reservation", e);
					}
				}
			};
		}
		return this.currentReservationFilter;
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), new Date());
		onSearch(event);
	}

	@Override
	public void onReset(ActionEvent event) {
		try {
			if (!PosUtils.isUserPosShiftOpened()) {
				String msg = "No se puede Facturar. El Usuario no ha abierto la Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			setNew(true);
			setProjectReservation((ProjectReservation)BeanManager.getManagerBean(ProjectReservation.class).createNewTo());
			setReservationInvoiceTo(new ReservationInvoiceTo(true));
			getReservationInvoiceTo().setHotel(obtainHotel());
			fillHotelData();
			onNewService(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private Hotel obtainHotel() throws ManagerBeanException {
		PmsCollectionsController collections = (PmsCollectionsController)AonUtil.getRegisteredBean(COLLECTIONS_CONTROLLER_NAME);
		List<SelectItem> hotelList = collections.getCurrentUserServiceHotels();
		return (hotelList.size() > 0) ? (Hotel)hotelList.get(0).getValue() : null;
	}

	public void onReservationChanged(LookupChangeEvent event) throws ManagerBeanException{
		ProjectReservation reservation = (ProjectReservation)event.getNewValue();
		if (reservation != null && reservation.getId() != null) {
			setReservationInvoiceTo(new ReservationInvoiceTo(true));
			getReservationInvoiceTo().setHotel(reservation.getHotel());
			fillHotelData();

			getReservationInvoiceTo().setServices(new LinkedList<HotelService>());
			onNewService(null);
		}
	}
	
	public void onInvoiceHotelChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			getReservationInvoiceTo().setHotel((Hotel)event.getNewValue());
			fillHotelData();

			getReservationInvoiceTo().setServices(new LinkedList<HotelService>());
			onNewService(null);
		}
	}

	private void fillHotelData() throws ManagerBeanException {
		getReservationInvoiceTo().getRegistry().setId(getReservationInvoiceTo().getHotel().getCustomer().getRegistry().getId());
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
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), true);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		if (rectification) {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_RECTIFICATION), true);
		} else {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), true);
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
			String alias = null;
			if (getProjectReservation() != null & getProjectReservation().getId() != null) {
				alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID);
				criteria.addEqualExpression(alias, getProjectReservation().getId());
				alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE);
				criteria.addEqualExpression(alias, DateUtils.addDays(getProjectReservation().getEndDate(),-1));
			} else {
				alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_HOTEL_ID);
				criteria.addEqualExpression(alias, getReservationInvoiceTo().getHotel().getId());
				alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE);
				criteria.addEqualExpression(alias, getReservationInvoiceTo().getIssueDate());
			}
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

			getReservationInvoiceTo().setServices(new LinkedList<HotelService>());
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

	public List<SelectItem> getServiceTypes() {
		List<SelectItem> serviceTypes = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(ProductType.SERVICE, AonUtil.getMessage(IPmsMessages.BUNDLE_KEY, IPmsMessages.PMS_SERVICES));
		serviceTypes.add(item);
		item = new SelectItem(ProductType.COMMERCIAL_PRODUCT, AonUtil.getMessage(IPmsMessages.BUNDLE_KEY, IPmsMessages.PMS_DEPOSITS));
		serviceTypes.add(item);
		item = new SelectItem(ProductType.EXTERNAL_WORK, AonUtil.getMessage(IPmsMessages.BUNDLE_KEY, IPmsMessages.PMS_DAMAGES));
		serviceTypes.add(item);
		return serviceTypes;
	}

	public void onInvoiceServiceTypeChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			getReservationInvoiceTo().setServiceType((ProductType)event.getNewValue());
			getReservationInvoiceTo().setServices(new LinkedList<HotelService>());
			onNewService(null);
		}
	}

	public void onNewService(ActionEvent event) {
		try {
			double quantity = 1;
			Date fromDate = new Date();
			Date toDate = new Date();
			if (getInvoiceServicesCount() > 0) {
				quantity = getReservationInvoiceTo().getLastService().getQuantity();
				fromDate = getReservationInvoiceTo().getLastService().getFromDate();
				toDate = getReservationInvoiceTo().getLastService().getToDate();
			} else if (getReservationInvoiceTo().getRoom() != null) {
				fromDate = getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation().getStartDate();
				toDate = getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation().getEndDate();
				toDate = DateUtils.addDays(toDate, -1);
			}

			HotelService hotelService = getReservationInvoiceTo().getNewService();
			hotelService.setFromDate(fromDate);
			hotelService.setToDate(toDate);
			hotelService.setItem((Item)getHotelServices().get(0).getValue());
			hotelService.setQuantity(quantity);
			getReservationInvoiceTo().getServices().add(hotelService);
			onInvoiceServiceChanged(null);
		} catch (ManagerBeanException ex) {
			String msg = "Error al seleccionar Servicio.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onRemoveService(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        int serviceIndex = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("hotelInvoiceServiceIndex"));
        getReservationInvoiceTo().getServices().remove(serviceIndex);
		onInvoiceServiceChanged(null);
	}

	public int getInvoiceServicesCount() {
		return getReservationInvoiceTo().getServicesCount();
	}

	public List<SelectItem> getHotelServices() throws ManagerBeanException {
		List<SelectItem> servicesList = new LinkedList<SelectItem>();
		if (getReservationInvoiceTo().getHotel() != null) {
			IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
			Criteria criteria = new Criteria();
			String alias = catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID);
			criteria.addEqualExpression(alias, getReservationInvoiceTo().getHotel().getServiceCatalogue().getId());
			alias = catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_PRODUCT_TYPE);
			criteria.addEqualExpression(alias, getReservationInvoiceTo().getServiceType());
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

	public double getServicesAmount() {
		IPriceStrategy strategy = PriceStrategyFactory.getPriceStrategy();
		for (HotelService hotelService : getReservationInvoiceTo().getServices()) {
			if (hotelService.getItem() != null) {
				double prices = 0;
				Date date = hotelService.getFromDate();
				while (date.compareTo(hotelService.getToDate()) <= 0) {
					prices += strategy.getUnitPrice(hotelService, date, getReservationInvoiceTo().getHotel().getCustomer().getTariff());
					date = DateUtils.addDays(date, 1);
				}
				hotelService.setPrice(CommonUtil.round(prices, 4));
				hotelService.setTaxableBase(strategy.getBasePrice(hotelService));
			}
		}
		return strategy.getTotalPrice(this, getReservationInvoiceTo().getHotel().getCustomer());
	}

	public void onNewFinance(ActionEvent event) {
		Finance finance = new Finance();
		finance.setAmount(CommonUtil.round(getServicesAmount() - getFinancesAmount()));
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
		if (validateInvoice()) {
			try {
				ProjectReservation reservation = null;
				if (getReservationInvoiceTo().getRoom() != null) {
					reservation = getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation();
				}
				getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
				getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
				getReservationInvoiceTo().setComments(obtainInvoiceComments(getReservationInvoiceTo().getServices()));
				getReservationInvoiceTo().setDirectCustomer(true);
				getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());

				ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
				Invoice invoice = reservationInvoicing.invoice(getReservationInvoiceTo(), reservation);

				onEditSearch(event);
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
				onSearch(event);
				getModel().setRowIndex(0);
				onSelect(event);
			} catch (ManagerBeanException ex) {
				AonUtil.addErrorMessage(ex.getMessage());
				throw new AbortProcessingException(ex.getMessage(), ex);
			}
		}
	}

	private String obtainInvoiceComments(List<HotelService> services) {
		String comments = "";
		for (HotelService service: services) {
			if (StringUtils.isNotEmpty(service.getItem().getDescription())) {
				comments += service.getItem().getProduct().getName() + " - "+ service.getItem().getDescription() + "\n";
			}
		}
		return comments;
	}

	private boolean validateInvoice() {
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

		return isServicesDatesOk();
	}

	public boolean isFinancesAmountOk() {
		return CommonUtil.round(getServicesAmount() - getFinancesAmount()) == 0;
	}

	public boolean isPayMethodOk() {
		for (Finance finance : getReservationInvoiceTo().getFinances()) {
			if (finance.getPayMethod() == null && finance.getTotalAmount() != 0) {
				return false;
			}
		}
		return true;
	}

	private boolean isServicesDatesOk() {
		ProjectReservation reservation = null;
		if (getReservationInvoiceTo().getRoom() != null) {
			reservation = getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation();
		}

		for (HotelService hotelService : getReservationInvoiceTo().getServices()) {
			Date fromDate = hotelService.getFromDate();
			Date toDate = hotelService.getToDate();
			if (fromDate.compareTo(toDate) > 0) {
				String msg = "La Fecha de inicio del Servicio " + hotelService.getItem().getProduct().getName() + " no puede ser mayor que la Fecha de fin.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else if (CommonUtil.getDaysBetweenDates(fromDate, toDate) > 30) {
				String msg = "No se puede facturar un Servicio de mas de 30 dias.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else if ((reservation != null) && (fromDate.compareTo(reservation.getStartDate()) < 0 || toDate.compareTo(reservation.getEndDate()) > 0)) {
				String msg = "Las fechas del Servicio no estan dentro de la Reserva.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		return true;
	}

	public void onRectifyInvoiceShow(ActionEvent event) {
		try {
			if (!getModel().isRowAvailable()) {
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
			setInvoiceToRectify((Invoice)getModel().getRowData());
			setReservationInvoiceTo(new ReservationInvoiceTo(true));
			getReservationInvoiceTo().setHotel(obtainRectificationHotel());
			getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private Hotel obtainRectificationHotel() throws ManagerBeanException {
		for (ITransferObject ito : getInvoiceToRectify().getDetailList()) {
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
			getReservationInvoiceTo().setSeries(obtainHotelRectificationSeries());
			getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));

			ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
			Invoice rectifier = reservationInvoicing.rectify(getInvoiceToRectify(), getReservationInvoiceTo());

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
			SaleInvoiceController invoiceController = (SaleInvoiceController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.load(event, ((Invoice)getModel().getRowData()).getId());
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