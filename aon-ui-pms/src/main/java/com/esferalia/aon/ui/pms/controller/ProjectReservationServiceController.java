package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.product.Item;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationServiceController extends LinesController {

	private IPriceStrategy priceStrategy;
	private ItemPricesManager pricesManager;
	private boolean showServiceDetailWindow;
	private ProjectReservationRoom serviceReservationRoom;
	private Date serviceFromDate;
	private Date serviceToDate;
	private double serviceQuantity;
	private double servicePrice;

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public ItemPricesManager getPricesManager(){
		if(pricesManager == null){
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
		this.serviceQuantity = serviceQuantity;
	}

	public double getServicePrice() {
		return servicePrice;
	}

	public void setServicePrice(double servicePrice) {
		this.servicePrice = servicePrice;
	}

	@Override
	public void onReset(ActionEvent arg0) {
		try {
			ProjectReservationService reservationService = new ProjectReservationService();
			reservationService.setProjectReservation((ProjectReservation)getMasterController().getTo());
			setTo(reservationService);

			setNew(true);
			fillReservationServiceValues(reservationService);
		} catch (ManagerBeanException ex) {
			String msg = "No es posible asignar Servicio";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	@Override
	public void onSelect(ActionEvent event) {
		try {
			if (getModel().isRowAvailable()) {
				ProjectReservationService reservationService = (ProjectReservationService)getModel().getRowData();
				setTo(reservationService);

				fillReservationServiceValues(reservationService);
			}
		} catch (ManagerBeanException ex) {
			String msg = "No es posible asignar Servicio";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void fillReservationServiceValues(ProjectReservationService reservationService) throws ManagerBeanException {
		if (isNew()) {
			reservationService.setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());

			setServiceReservationRoom(null);
			setServiceFromDate(reservationService.getProjectReservation().getStartDate());
			setServiceToDate(reservationService.getProjectReservation().getEndDate());
			setServiceQuantity(0);
			setServicePrice(0);
		} else {
			setServiceReservationRoom(reservationService.getProjectReservationRoom());
			IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
			Criteria criteria = new Criteria();
			String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
			criteria.addEqualExpression(alias, reservationService.getId());
			criteria.addOrder(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE));
			for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
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

	public void onItemChanged(LookupChangeEvent event) {
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
			Tariff tariff = (getServiceReservationRoom() != null) ? getServiceReservationRoom().getTariff() : null;
			setServicePrice(getPricesManager().getSalesPrice(item, getPriceStrategy().getUnitPrice(reservationServiceDetail, getServiceFromDate(), tariff)));
		}
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		Item item = reservationService.getItem();
		if (item != null && item.getId() != null) {
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				setServiceQuantity(CommonUtil.round((Double)event.getNewValue()));
	
				ProjectReservationServiceDetail reservationServiceDetail = new ProjectReservationServiceDetail();
				reservationServiceDetail.setProjectReservationService(reservationService);
				reservationServiceDetail.setQuantity(getServiceQuantity());
				Tariff tariff = (getServiceReservationRoom() != null) ? getServiceReservationRoom().getTariff() : null;
				setServicePrice(getPricesManager().getSalesPrice(item, getPriceStrategy().getUnitPrice(reservationServiceDetail, getServiceFromDate(), tariff)));
			} else {
				setServiceQuantity(1);
			}
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

	public void onAssignReservationService(ActionEvent event) throws ManagerBeanException {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();

		boolean isNew = isNew();
		Date fromDate = getServiceFromDate();
		Date toDate = getServiceToDate();
		double quantity = getServiceQuantity();
		double price = getPricesManager().getPrice(reservationService.getItem(), getServicePrice(), 4);
		ProjectReservationRoom reservationRoom = getServiceReservationRoom();

		onAccept(event);

		ReservationUtils reservationUtils = new ReservationUtils();
		if (isNew) {
			reservationUtils.insertProjectReservationServiceDetails(reservationService, fromDate, toDate, quantity, price, reservationRoom, getPriceStrategy());
		} else {
			if (reservationService.getProjectReservation().isCrs()) {
				reservationUtils.updateProjectReservationServiceDetails(reservationService, null, null, reservationRoom, null);
			} else {
				reservationUtils.updateProjectReservationServiceDetails(reservationService, quantity, price, reservationRoom, getPriceStrategy());
			}
			reservationService.setRoomNumber(null);
		}

		refreshReservationTotals();
	}

	public void onRemoveReservationService(ActionEvent event) throws ManagerBeanException {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();

		ReservationUtils reservationUtils = new ReservationUtils();
    	reservationUtils.removeProjectReservationServiceDetails(reservationService);

    	onRemove(event);

    	refreshReservationTotals();
	}

	private void refreshReservationTotals() throws ManagerBeanException {
		ProjectReservationController masterController = (ProjectReservationController)getMasterController();
		masterController.accept(null);
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

}