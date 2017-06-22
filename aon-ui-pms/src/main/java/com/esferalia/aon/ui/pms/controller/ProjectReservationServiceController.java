package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.CONFIG_INVALID_END_DATE;
import static com.code.aon.ui.common.ICommonMessages.DATE_PATTERN;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationServiceController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private IPriceStrategy priceStrategy;
	private ItemPricesManager pricesManager;
	private boolean showServiceDetailWindow;
	private ProjectReservationRoom serviceReservationRoom;
	private Date serviceFromDate;
	private Date serviceToDate;
	private double serviceQuantity;
	private double servicePrice;
	private boolean showServicePricesWindow;
	private List<ITransferObject> reservationServiceDetails;

	public IPriceStrategy getPriceStrategy(){
		if (priceStrategy == null) {
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public ItemPricesManager getPricesManager(){
		if (pricesManager == null) {
			pricesManager = new ItemPricesManager();
		}
		return pricesManager;
	}

	public boolean isShowServiceDetailWindow() {
		return showServiceDetailWindow;
	}

	public void setShowServiceDetailWindow(boolean showServiceDetailWindow) {
		this.showServiceDetailWindow = showServiceDetailWindow;
	}

	public ProjectReservationRoom getServiceReservationRoom() {
		return serviceReservationRoom;
	}

	public void setServiceReservationRoom(ProjectReservationRoom serviceReservationRoom) {
		this.serviceReservationRoom = serviceReservationRoom;
	}

	public Date getServiceFromDate() {
		return serviceFromDate;
	}

	public void setServiceFromDate(Date serviceFromDate) {
		this.serviceFromDate = serviceFromDate;
	}

	public Date getServiceToDate() {
		return serviceToDate;
	}

	public void setServiceToDate(Date serviceToDate) {
		this.serviceToDate = serviceToDate;
	}

	public double getServiceQuantity() {
		return serviceQuantity;
	}

	public void setServiceQuantity(double serviceQuantity) {
		this.serviceQuantity = CommonUtil.round(serviceQuantity);
	}

	public double getServicePrice() {
		return servicePrice;
	}

	public void setServicePrice(double servicePrice) {
		this.servicePrice = CommonUtil.round(servicePrice, 4);
	}

	public boolean isShowServicePricesWindow() {
		return showServicePricesWindow;
	}

	public void setShowServicePricesWindow(boolean showServicePricesWindow) {
		this.showServicePricesWindow = showServicePricesWindow;
	}

	public List<ITransferObject> getReservationServiceDetails() {
		return reservationServiceDetails;
	}

	public void setReservationServiceDetails(List<ITransferObject> reservationServiceDetails) {
		this.reservationServiceDetails = reservationServiceDetails;
	}

	@Override
	public void onReset(ActionEvent event) {
		ProjectReservationController masterController = (ProjectReservationController)getMasterController();
		masterController.accept(event);
		try {
			ProjectReservationService reservationService = new ProjectReservationService();
			reservationService.setProjectReservation((ProjectReservation)getMasterController().getTo());
			setTo(reservationService);
			masterController.getReservationPermission().setReservationService(reservationService);

			setNevv(true);
			fillReservationServiceValues(reservationService);
		} catch (ManagerBeanException ex) {
			String msg = "No es posible asignar Servicio";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	@Override
	public void onSelect(ActionEvent event) {
		ProjectReservationController masterController = (ProjectReservationController)getMasterController();
		masterController.accept(event);
		try {
			if (getModel().isRowAvailable()) {
				super.onSelect(event);
				ProjectReservationService reservationService = (ProjectReservationService)getTo();
				reservationService.setProjectReservation((ProjectReservation)getMasterController().getTo());
				masterController.getReservationPermission().setReservationService(reservationService);

				fillReservationServiceValues(reservationService);
			}
		} catch (ManagerBeanException ex) {
			String msg = "No es posible asignar Servicio";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void fillReservationServiceValues(ProjectReservationService reservationService) throws ManagerBeanException {
		if (isNevv()) {
			reservationService.setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());

			setServiceReservationRoom(null);
			setServiceFromDate(reservationService.getProjectReservation().getStartDate());
			setServiceToDate(DateUtils.addDays(reservationService.getProjectReservation().getEndDate(), -1));
			setServiceQuantity(0);
			setServicePrice(0);
		} else {
			setServiceReservationRoom(reservationService.getReservationRoom());
			IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
			Criteria criteria = new Criteria();
			String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
			criteria.addEqualExpression(alias, reservationService.getId());
			criteria.addOrder(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE), false);
			for (ITransferObject ito : reservationServiceDetailBean.getList(criteria, 0, 1)) {
				ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
				setServiceQuantity(reservationServiceDetail.getQuantity());
				setServicePrice(reservationServiceDetail.getSalesPrice());
			}
		}
	}

	public List<SelectItem> getReservationRoomItems() throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)getMasterController().getTo();
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		criteria.addOrder(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ROOM_INDEX));
		List<SelectItem> roomItemList = new LinkedList<SelectItem>();
		for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
			if (reservationRoom.getRoomNumber() != null) {
				SelectItem selectItem = new SelectItem(reservationRoom, reservationRoom.getRoomNumber());
				roomItemList.add(selectItem);
			}
		}
		return roomItemList;
	}

	public List<ProjectReservationRoom> getReservationRooms() throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)getMasterController().getTo();
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		criteria.addOrder(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_ROOM_INDEX));
		List<ProjectReservationRoom> reservationRoomList = new LinkedList<ProjectReservationRoom>();
		for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
			if (reservationRoom.getRoomNumber() != null) {
				reservationRoomList.add(reservationRoom);
			}
		}
		return reservationRoomList;
	}

	public List<SelectItem> getReservationDates() {
		List<SelectItem> reservationDates = new LinkedList<SelectItem>();
		ProjectReservation reservation = (ProjectReservation)getMasterController().getTo();
		for (Date date = reservation.getStartDate(); date.before(reservation.getEndDate()); date = DateUtils.addDays(date, 1)) {
			reservationDates.add(new SelectItem(date, new SimpleDateFormat(AonUtil.getMessage(DATE_PATTERN)).format(date)));
		}
		return reservationDates;
	}

	public void onItemChanged(ValueChangeEvent event) throws ManagerBeanException {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			reservationService.setItem(item);
			reservationService.setDescription(item.getProduct().getName());
			if (getServiceQuantity() == 0) {
				setServiceQuantity(1);
			}

			ProjectReservationServiceDetail reservationServiceDetail = new ProjectReservationServiceDetail();
			reservationServiceDetail.setProjectReservationService(reservationService);
			reservationServiceDetail.setQuantity(getServiceQuantity());

			double vatPercent = item.getProduct().getVat().getDatedPercentage(reservationService.getProjectReservation().getDate());
			double price = getPriceStrategy().getUnitPrice(reservationServiceDetail, getServiceFromDate(), reservationService.getProjectReservation().getCustomer());
			setServicePrice(getPricesManager().getSalesPrice(vatPercent, 0, price));
		}
	}	

	public void onQuantityChanged(ValueChangeEvent event) throws ManagerBeanException {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		Item item = reservationService.getItem();
		if (item != null && item.getId() != null && event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			setServiceQuantity(CommonUtil.round((Double)event.getNewValue()));

			ProjectReservationServiceDetail reservationServiceDetail = new ProjectReservationServiceDetail();
			reservationServiceDetail.setProjectReservationService(reservationService);
			reservationServiceDetail.setQuantity(getServiceQuantity());

			double vatPercent = item.getProduct().getVat().getDatedPercentage(reservationService.getProjectReservation().getDate());
			double price = getPriceStrategy().getUnitPrice(reservationServiceDetail, getServiceFromDate(), reservationService.getProjectReservation().getCustomer());
			setServicePrice(getPricesManager().getSalesPrice(vatPercent, 0, price));
		} else {
			setServiceQuantity(1);
		}
	}	

	public void onSalesPriceChanged(ValueChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		Item item = reservationService.getItem();
		if (item != null && item.getId() != null) {
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				setServicePrice(CommonUtil.round((Double)event.getNewValue()));
			} else {
				setServicePrice(0);
			}
		}
	}

	public void onAcceptReservationService(ActionEvent event) throws ManagerBeanException {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		boolean isNevv = isNevv();
		if (isNevv) {
			validateServiceDates();	
		}

		ReservationUtils reservationUtils = new ReservationUtils();
		double quantity = getServiceQuantity();
		double vatPercent = reservationService.getItem().getProduct().getVat().getDatedPercentage(reservationService.getProjectReservation().getDate());
		double price = getPricesManager().getPrice(vatPercent, 0, getServicePrice(), 4);
		ProjectReservationRoom reservationRoom = getServiceReservationRoom();

		onAccept(event);

		if (isNevv) {
			Date fromDate = getServiceFromDate();
			Date toDate = getServiceToDate();
			reservationUtils.insertProjectReservationServiceDetails(reservationService, fromDate, toDate, quantity, price, reservationRoom, getPriceStrategy());
		} else {
			reservationUtils.updateProjectReservationServiceDetails(reservationService, quantity, price, null, getPriceStrategy());
			reservationService.setRoomNumber(null);
		}

		refreshReservationTotals();
	}

	private void validateServiceDates() {
		if (getServiceFromDate().after(getServiceToDate())) {
			String message = AonUtil.getMessage(CONFIG_INVALID_END_DATE);
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
	}

	public void onAssignReservationRoom(ActionEvent event) throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		Integer reservationRoomId = new Integer(params.get(IPmsConstants.AVAILABLE_SERVICE_ROOM));
		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)BeanManager.getManagerBean(ProjectReservationRoom.class).get(reservationRoomId);

		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		ReservationUtils reservationUtils = new ReservationUtils();
		reservationUtils.updateProjectReservationServiceDetails(reservationService, null, null, reservationRoom, getPriceStrategy());
		reservationService.setRoomNumber(null);

    	IController reservationServiceController = (IController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_SERVICE_CONTROLLER_NAME);
    	reservationServiceController.onSearch(event);
	}

	public void onRemoveReservationService(ActionEvent event) throws ManagerBeanException {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();

		if (reservationService.getProjectReservation().isDirty()) {
			String message = "La Reserva ha sido modificada por otro usuario. Refrescar para obtener los datos actualizados.";
			throw new AbortProcessingException(message);
		}

		ReservationUtils reservationUtils = new ReservationUtils();
    	reservationUtils.removeProjectReservationServiceDetails(reservationService);
    	if (!reservationService.isRemoved()) {
    		onRemove(event);
    	} else {
    		onSearch(event);
    	}

    	refreshReservationTotals();
	}

	private void refreshReservationTotals() throws ManagerBeanException {
		ProjectReservationController masterController = (ProjectReservationController)getMasterController();
		((ProjectReservation)masterController.getTo()).setForceCalculateTotals(true);
		masterController.accept(null);
		((ProjectReservation)masterController.getTo()).setForceCalculateTotals(false);
	}

	public void onShowServiceDetails(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservationService reservationService = (ProjectReservationService)getModel().getRowData();
			reservationService.setShowServiceDetail(true);
		}
	}

	public void onHideServiceDetails(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservationService reservationService = (ProjectReservationService)getModel().getRowData();
			reservationService.setShowServiceDetail(false);
		}
	}

	public boolean isShowServiceDetail() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservationService reservationService = (ProjectReservationService)getModel().getRowData();
			return reservationService.isShowServiceDetail();
		}
		return false;
	}

	public List<ITransferObject> getServiceDetailList() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservationService reservationService = (ProjectReservationService)getModel().getRowData();
			return getServiceDetailList(reservationService);
		}
		return null;
	}

	private List<ITransferObject> getServiceDetailList(ProjectReservationService service) throws ManagerBeanException {
		IManagerBean serviceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(serviceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID), service.getId());
		criteria.addOrder(serviceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE));
		return serviceDetailBean.getList(criteria);
	}

	public void onShowServicePrices(ActionEvent event) throws ManagerBeanException {
		List<ITransferObject> serviceDetailList = getServiceDetailList();
		for (ITransferObject ito : serviceDetailList) {
			ProjectReservationServiceDetail serviceDetail = (ProjectReservationServiceDetail)ito;
			serviceDetail.setEditableSalesPrice(serviceDetail.getSalesPrice());
		}
		setReservationServiceDetails(serviceDetailList);
	}

	public void onAcceptServicePrices(ActionEvent event) throws ManagerBeanException {
		IManagerBean serviceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		for (ITransferObject ito : getReservationServiceDetails()) {
			ProjectReservationServiceDetail serviceDetail = (ProjectReservationServiceDetail)ito;
			double vatPercent = serviceDetail.getItem().getProduct().getVat().getDatedPercentage(serviceDetail.getProjectReservation().getDate());
			double price = getPricesManager().getPrice(vatPercent, 0, serviceDetail.getEditableSalesPrice(), 4);
			if (price != serviceDetail.getPrice()) {
				serviceDetail.setPrice(price);
				serviceDetail.setTaxableBase(getPriceStrategy().getBasePrice(serviceDetail));
				serviceDetailBean.update(serviceDetail);
			}
		}

		refreshReservationTotals();
	}

}