package com.esferalia.aon.ui.pms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.pms.Allotment;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class AllotmentController extends BasicController implements IPmsConstants {

	private boolean showAuditInfoWindow;
	private Item item;
	private Item[] items;

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}	

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Item[] getItems() {
		return items;
	}

	public void setItems(Item[] items) {
		this.items = items;
	}

	public void onHotelChanged(ValueChangeEvent event) {
		Allotment allotment = (Allotment)getTo();
		allotment.setHotel((Hotel)event.getNewValue());
		setItems(null);
		setItem(null);
	}

	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		return PmsUtils.getRoomItems(((Allotment)getTo()).getHotel());
	}

	public void onItemChanged(ValueChangeEvent event) {
		setItem((Item)event.getNewValue());
	}

	public int getItemsSize() {
		return ArrayUtils.getLength(getItems());
	}

	public void onAddItem(ActionEvent event) {
		setItems((Item[])ArrayUtils.add(getItems(), getItem()));
		setItem(null);
	}

	public void onRemoveItem(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("allotmentItemIdx"));		
		setItems((Item[])ArrayUtils.remove(getItems(), index));
	}

}