package com.esferalia.aon.ui.pms.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.PmsCollectionsController;
import com.esferalia.aon.ui.pms.controller.RoomController;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class RoomSearchListener extends ControllerSearchListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Hotel hotel;
	private Item item;
	private Boolean active;
	
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		PmsCollectionsController collections = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		setHotel((Hotel)collections.getCurrentUserHotelList().get(0));
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setActive(Boolean.TRUE);

		((RoomController)getController()).clearCheckedRooms();
	}
	
	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		return PmsUtils.getRoomItems(getHotel());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel().getId());			
		}
		if (getItem() != null && getItem().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ROOM_ITEM_ID), getItem().getId());			
		}
		if (getActive() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ROOM_ACTIVE), getActive());			
		}
	}
	
}