package com.esferalia.aon.ui.pms.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Allotment;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.sql.SQLAllotment;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class AllotmentController extends BasicController implements IPmsConstants, IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean rateCode;
	private boolean group;
	private Item item;
	private Item[] items;
	private Tariff tariff;
	private Tariff[] tariffs;
	private boolean showAuditInfoWindow;
	private boolean showAllotmentCopyWindow;
	private Allotment allotmentCopy;
	private Item itemCopy;
	private boolean showAllotmentFractionWindow;

	public boolean isRateCode() {
		return rateCode;
	}

	public void setRateCode(boolean rateCode) {
		this.rateCode = rateCode;
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

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}	

	public boolean isShowAllotmentCopyWindow() {
		return showAllotmentCopyWindow;
	}

	public void setShowAllotmentCopyWindow(boolean showAllotmentCopyWindow) {
		this.showAllotmentCopyWindow = showAllotmentCopyWindow;
	}	

	public Allotment getAllotmentCopy() {
		return allotmentCopy;
	}

	public void setAllotmentCopy(Allotment allotmentCopy) {
		this.allotmentCopy = allotmentCopy;
	}	

	public Item getItemCopy() {
		return itemCopy;
	}

	public void setItemCopy(Item itemCopy) {
		this.itemCopy = itemCopy;
	}	

	public boolean isShowAllotmentFractionWindow() {
		return showAllotmentFractionWindow;
	}

	public void setShowAllotmentFractionWindow(boolean showAllotmentFractionWindow) {
		this.showAllotmentFractionWindow = showAllotmentFractionWindow;
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
		if (!isRateCode()) {
			for (SelectItem roomItem : roomItemList) {
				roomItem.setDisabled(ArrayUtils.contains(getItems(), (Item)roomItem.getValue()));
			}
		}
		return roomItemList;
	}

	public List<SelectItem> getRoomTariffs() throws ManagerBeanException {
		List<SelectItem> roomTariffList = new LinkedList<SelectItem>();
		roomTariffList.addAll(((PmsCollectionsController)AonUtil.getRegisteredBean(COLLECTIONS_CONTROLLER_NAME)).getTariffs());
		for (SelectItem roomTariff : roomTariffList) {
			roomTariff.setDisabled(ArrayUtils.contains(getTariffs(), (Tariff)roomTariff.getValue()));
		}
		return roomTariffList;
	}

	public void onChangeHolder(ActionEvent event) {
		if (isRateCode()) {
			setRateCode(false);
			setGroup(false);
		} else {
			if (isGroup()) {
				setItems(null);
				setItem(null);
				setTariffs(null);
				setTariff(null);
			}
			setRateCode(isGroup());
			setGroup(!isGroup());
		}

		Allotment allotment = (Allotment)getTo();
		allotment.setRateCode(null);
		allotment.setAgency(null);
		allotment.setAgencyGroup(null);
	}

	public void onItemChanged(ValueChangeEvent event) {
		setItem((Item)event.getNewValue());
		if (isRateCode()) {
			setItems(null);
		}
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

	public void onAllotmentCopyShow(ActionEvent event) {
		Allotment allotment = (Allotment)getTo();
		setAllotmentCopy(new Allotment());
		getAllotmentCopy().setHotel(allotment.getHotel());
		setItemCopy(new Item());
	}

	public void onAllotmentCopy(ActionEvent event) throws ManagerBeanException {
		Allotment to = (Allotment)getTo();
		getAllotmentCopy().setRateCode(to.getRateCode());
		getAllotmentCopy().setAgency(to.getAgency() != null && to.getAgency().getId() != null ? to.getAgency() : null);
		getAllotmentCopy().setAgencyGroup(to.getAgencyGroup() != null && to.getAgencyGroup().getId() != null ? to.getAgencyGroup() : null);
		getAllotmentCopy().setStartDate(to.getStartDate());
		getAllotmentCopy().setEndDate(to.getEndDate());
		getAllotmentCopy().setQuantity(to.getQuantity());
		getAllotmentCopy().setActive(true);

		if (isAllotmentOverlap(getAllotmentCopy(), getItemCopy().getId().toString(), null)) {
			String message = "Ya existen Cupos definidos con esas condiciones en el periodo.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		} else {
			setNevv(true);
			setTo(getAllotmentCopy());
			setItems(null);
			setItem(getItemCopy());
			accept(event);
		}
	}

	public void onHotelCopyChanged(ValueChangeEvent event) {
		getAllotmentCopy().setHotel((Hotel)event.getNewValue());
	}

	public List<SelectItem> getHotelCopyRoomItems() throws ManagerBeanException {
		return PmsUtils.getRoomItems(getAllotmentCopy().getHotel());
	}

	public void onAllotmentFractionShow(ActionEvent event) {
	}

	public void onAllotmentFraction(ActionEvent event) {
	}

	public boolean isAllotmentOverlap(Allotment allotment, String items, String tariffs) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			return SQLAllotment.isAllotmentDefined(connection, allotment, items, tariffs);
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			return false;
		} finally {
			SQLUtils.closeQuietly(connection);
		}
	}

}