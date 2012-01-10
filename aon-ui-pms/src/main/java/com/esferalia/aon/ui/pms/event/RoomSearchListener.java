package com.esferalia.aon.ui.pms.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.dao.IPmsAlias;

public class RoomSearchListener extends ControllerSearchListener {

	private Hotel hotel;
	private Item item;
	
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
	
	@Override
	protected void init() throws ManagerBeanException {
		setHotel(null);
		setItem(null);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IPmsAlias.ROOM_HOTEL_ID), getHotel().getId());			
		}
		if (getItem() != null && getItem().getId() != null) {
			criteria.addEqualExpression(getFieldName(IPmsAlias.ROOM_ITEM_ID), getItem().getId());			
		}
	}

}