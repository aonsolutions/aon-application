package com.esferalia.aon.ui.pms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.reservation.AvailableRoomStay;
import com.esferalia.aon.pms.reservation.ReservationRequestManager;

public class ReservationRequestRoomController extends LinesController {

	private Map<Integer,List<AvailableRoomStay>> availableRoomStayMap;

	public Map<Integer,List<AvailableRoomStay>> getAvailableRoomStayMap() {
		return availableRoomStayMap;
	}
	public void setAvailableRoomStayMap(Map<Integer,List<AvailableRoomStay>> availableRoomStayMap) {
		this.availableRoomStayMap = availableRoomStayMap;
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
			requestController.accept(event);

			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			ReservationRequestManager manager = new ReservationRequestManager();
			getAvailableRoomStayMap().put(requestRoom.getId(), manager.processAvailabilityQuery(requestRoom));
		}
	}

	public boolean isIdInAvailableRoomStayMap() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			return getAvailableRoomStayMap().containsKey(requestRoom.getId());
		}
		return false;
	}

	public List<AvailableRoomStay> getAvailableRoomStayList() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			return getAvailableRoomStayMap().get(requestRoom.getId());
		}
		return null;
	}

}