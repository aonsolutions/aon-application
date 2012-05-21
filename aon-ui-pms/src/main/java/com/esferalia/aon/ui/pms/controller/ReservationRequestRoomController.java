package com.esferalia.aon.ui.pms.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.reservation.AvailableRoomStay;
import com.esferalia.aon.pms.reservation.ReservationRequestManager;

public class ReservationRequestRoomController extends LinesController {

	private Map<Integer,List<AvailableRoomStay>> availableRoomStayMap;

	public Map<Integer,List<AvailableRoomStay>> getAvailableRoomStayMap() {
		if (availableRoomStayMap == null) {
			availableRoomStayMap = new HashMap<Integer,List<AvailableRoomStay>>();
		}
		return availableRoomStayMap;
	}
	public void setAvailableRoomStayMap(Map<Integer,List<AvailableRoomStay>> availableRoomStayMap) {
		this.availableRoomStayMap = availableRoomStayMap;
	}

	public boolean isAvailabilityRequested() {
		return (getAvailableRoomStayMap().size() > 0);
	}

	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		ReservationRequest request = (ReservationRequest)getMasterController().getTo();
		if (request.getHotel() != null && request.getHotel().getId() != null) {
			return getHotelRoomItems(request.getHotel());
		}
		return null;
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

	public void sendAvailabilityQuery(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestController requestController = (ReservationRequestController)getMasterController();
			ReservationRequest request = (ReservationRequest)requestController.getTo();
			request.setRequestCounter(request.getRequestCounter() + 1);
			requestController.setSkipResetAvailabilityMap(true);
			requestController.accept(event);
			requestController.setSkipResetAvailabilityMap(false);

			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			requestRoom.setReservationRequest(request);
			ReservationRequestManager manager = new ReservationRequestManager();
			getAvailableRoomStayMap().put(requestRoom.getId(), manager.processAvailabilityQuery(requestRoom));
		}
	}

	public List<AvailableRoomStay> getAvailableRoomStayList() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			return getAvailableRoomStayMap().get(requestRoom.getId());
		}
		return null;
	}

	public boolean isIdInAvailableRoomStayMap() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			return getAvailableRoomStayMap().containsKey(requestRoom.getId());
		}
		return false;
	}

	public void sendBookingQuery(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestController requestController = (ReservationRequestController)getMasterController();
			ReservationRequest request = (ReservationRequest)requestController.getTo();
			request.setRequestCounter(request.getRequestCounter() + 1);
			requestController.setSkipResetAvailabilityMap(true);
			requestController.accept(event);
			requestController.setSkipResetAvailabilityMap(false);

			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			requestRoom.setReservationRequest(request);
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			AvailableRoomStay availableRoomStay = getAvailableRoomStayList().get(Integer.parseInt(ec.getRequestParameterMap().get("availableRoomStayIndex")));
			ReservationRequestManager manager = new ReservationRequestManager();
			availableRoomStay = manager.processBookingRequest(requestRoom, requestController.getRequestGuest(), availableRoomStay);
			if (!availableRoomStay.isError()) {
				requestRoom.setCrsCode(availableRoomStay.getReservationId());
				requestRoom.setTariffCode(availableRoomStay.getTariffCode());
				requestRoom.setTariffDescription(availableRoomStay.getTariffDescription());
				requestRoom.setInventoryCode(availableRoomStay.getInventoryCode());
				requestRoom.setRoomCode(availableRoomStay.getRoomCode());
				requestRoom.setRoomDescription(availableRoomStay.getRoomDescription());
				requestRoom.setMealPlan(availableRoomStay.getMealPlan());
				requestRoom.setDailyPrice(availableRoomStay.getDailyPrice());
				requestRoom.setTotalPrice(availableRoomStay.getTotalPrice());
				requestRoom.setCancelPenalty(availableRoomStay.getCancelPenalty());
				getManagerBean().update(requestRoom);

				request.setActive(false);
				requestController.setSkipResetAvailabilityMap(true);
				requestController.accept(event);
				requestController.setSkipResetAvailabilityMap(false);

				getAvailableRoomStayMap().remove(requestRoom.getId());
			}
		}
	}
	
	public void onLoadRoomReservation(ActionEvent event) throws ManagerBeanException {
		ReservationRequestRoom room = (ReservationRequestRoom)this.getModel().getRowData();
		BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_CONTROLLER_NAME);
		IManagerBean bean = BeanManager.getManagerBean(ProjectReservation.class);
		reservationController.getCriteria().addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CRS_CODE), room.getCrsCode());
		reservationController.onSearch(event);
	}

}