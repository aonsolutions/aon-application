package com.esferalia.aon.ui.pms.event;

import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class RackSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Hotel hotel;
	private Item item;
	private String roomName;
	private Integer[] features;

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

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Integer[] getFeatures() {
		return features;
	}

	public void setFeatures(Integer[] features) {
		this.features = features;
	}

	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		return PmsUtils.getRoomItems(getHotel());
	}

	@Override
	protected void init() throws ManagerBeanException {
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setRoomName(null);
		setFeatures(null);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IEntityAlias.ROOM_ACTIVE), Boolean.TRUE);
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel().getId());
		}
		if (getItem() != null && getItem().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.ROOM_ITEM_ID), getItem().getId());
		}
		if (StringUtils.isNotEmpty(getRoomName())) {
			String roomExpr = (getRoomName().indexOf("*") >= 0) ? (getRoomName().replace('*', '%')) : ("%" + getRoomName() + "%");
			criteria.addExpression(ExpressionUtilities.getLikeExpression(getFieldName(IEntityAlias.ROOM_ASSET_NAME), roomExpr));
		}
		if (!ArrayUtils.isEmpty(getFeatures())) {
			String status = getController().resolveAlias("Room.features.feature.id");
			addEnumToCriteria(criteria, status, getFeatures());
		}
	}

}