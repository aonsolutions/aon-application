package com.esferalia.aon.ui.pms.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.product.Item;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.pms.Allotment;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class AllotmentController extends BasicController implements IPmsConstants {

	private boolean showAuditInfoWindow;
	private boolean group;
	private Item item;
	private Item[] items;
	private Tariff tariff;
	private Tariff[] tariffs;

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}	

	public boolean isGroup() {
		return group;
	}

	public void setGroup(boolean group) {
		this.group = group;
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

	public Tariff getTariff() {
		return tariff;
	}

	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}

	public Tariff[] getTariffs() {
		return tariffs;
	}

	public void setTariffs(Tariff[] tariffs) {
		this.tariffs = tariffs;
	}

	public void onHotelChanged(ValueChangeEvent event) {
		Allotment allotment = (Allotment)getTo();
		allotment.setHotel((Hotel)event.getNewValue());
		setItems(null);
		setItem(null);
	}

	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		List<SelectItem> roomItemList = new LinkedList<SelectItem>();
		roomItemList.addAll(PmsUtils.getRoomItems(((Allotment)getTo()).getHotel()));
		for (SelectItem roomItem : roomItemList) {
			roomItem.setDisabled(ArrayUtils.contains(getItems(), (Item)roomItem.getValue()));
		}
		return roomItemList;
	}

	public void onChangeHolder(ActionEvent event) {
		setGroup(!isGroup());

		Allotment allotment = (Allotment)getTo();
		if (isGroup()) {
			allotment.setAgency(null);
		} else {
			allotment.setAgencyGroup(null);
		}
	}

	public void onItemChanged(ValueChangeEvent event) {
		setItem((Item)event.getNewValue());
	}

	public Integer[] getItemsIds() {
		Integer[] itemsIds = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
		if (ArrayUtils.getLength(getItems()) > 0) {
			for (Item item : getItems()) {
				itemsIds = (Integer[])ArrayUtils.add(itemsIds, item.getId());
			}
		}
		return itemsIds;
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

	public void onTariffChanged(ValueChangeEvent event) {
		setTariff((Tariff)event.getNewValue());
	}

	public Integer[] getTariffsIds() {
		Integer[] tariffsIds = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
		if (ArrayUtils.getLength(getTariffs()) > 0) {
			for (Tariff tariff : getTariffs()) {
				tariffsIds = (Integer[])ArrayUtils.add(tariffsIds, tariff.getId());
			}
		}
		return tariffsIds;
	}

	public int getTariffsSize() {
		return ArrayUtils.getLength(getTariffs());
	}

	public void onAddTariff(ActionEvent event) {
		setTariffs((Tariff[])ArrayUtils.add(getTariffs(), getTariff()));
		setTariff(null);
	}

	public void onRemoveTariff(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("allotmentTariffIdx"));		
		setTariffs((Tariff[])ArrayUtils.remove(getTariffs(), index));
	}

}