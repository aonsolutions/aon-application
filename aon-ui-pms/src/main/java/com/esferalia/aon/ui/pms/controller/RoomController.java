package com.esferalia.aon.ui.pms.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.reservation.InventoryManager;

public class RoomController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Room lastRoomAdded;
	private boolean showRoomBlockWindow;
	private boolean blockMode;
	private Date blockFromDate;
	private Date blockToDate;
	private ActivityStatus blockStatus;
	private String blockRemarks;

	public Room getLastRoomAdded() {
		return lastRoomAdded;
	}
	public void setLastRoomAdded(Room lastRoomAdded) {
		this.lastRoomAdded = lastRoomAdded;
	}

	public boolean isShowRoomBlockWindow() {
		return showRoomBlockWindow;
	}
	public void setShowRoomBlockWindow(boolean showRoomBlockWindow) {
		this.showRoomBlockWindow = showRoomBlockWindow;
	}

	public boolean isBlockMode() {
		return blockMode;
	}
	public void setBlockMode(boolean blockMode) {
		this.blockMode = blockMode;
	}

	public Date getBlockFromDate() {
		return blockFromDate;
	}
	public void setBlockFromDate(Date blockFromDate) {
		this.blockFromDate = blockFromDate;
	}

	public Date getBlockToDate() {
		return blockToDate;
	}
	public void setBlockToDate(Date blockToDate) {
		this.blockToDate = blockToDate;
	}

	public ActivityStatus getBlockStatus() {
		return blockStatus;
	}
	public void setBlockStatus(ActivityStatus blockStatus) {
		this.blockStatus = blockStatus;
	}

	public String getBlockRemarks() {
		return blockRemarks;
	}
	public void setBlockRemarks(String blockRemarks) {
		this.blockRemarks = blockRemarks;
	}

	public void onShowRoomBlockWindow(ActionEvent event) throws ManagerBeanException {
		setBlockFromDate(new Date());
		setBlockToDate(new Date());
		setBlockStatus(null);
		setBlockRemarks(null);
	}

	public List<SelectItem> getBlockStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> blockStatuses = new LinkedList<SelectItem>();
		for (ActivityStatus status : ActivityStatus.values()) {
			if (status != ActivityStatus.BUSY) {
				SelectItem item = new SelectItem(status, status.getName(locale));
				blockStatuses.add(item);			
			}
		}
		return blockStatuses;
	}

	public void onBlockOrUnblockRooms(ActionEvent event) {
		if (isBlockMode()) {
			onRoomBlock(event);
		} else {
			onRoomUnblock(event);
		}
		clearCheckedRooms();
	}

	public void onRoomBlock(ActionEvent event) {
		if (getCheckedCount() > 0) {
			List<Item> inventoryItems = new LinkedList<Item>();
			for (Room room : getCheckedRooms()) {
				if (!inventoryItems.contains(room.getItem())) {
					inventoryItems.add(room.getItem());
				}

				try {
					List<Date> occupiedDates = new LinkedList<Date>();
					IManagerBean assetActivityBean = BeanManager.getManagerBean(AssetActivity.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_ID), room.getAsset().getId());
					criteria.addGreaterThanOrEqualExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), getBlockFromDate());
					criteria.addLessThanOrEqualExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), getBlockToDate());
					Projection prjDate = Projection.property(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE));
					for (Object obj : assetActivityBean.getList(new ProjectionList(prjDate), criteria)) {
						occupiedDates.add((Date)obj);
					}

					Date fromDate = getBlockFromDate();
					while (fromDate.compareTo(getBlockToDate()) <= 0) {
						if (!occupiedDates.contains(fromDate)) {
							AssetActivity assetActivity = new AssetActivity();
							assetActivity.setAsset(room.getAsset());
							assetActivity.setDate(fromDate);
							assetActivity.setFromTime(fromDate);
							assetActivity.setToTime(fromDate);
							assetActivity.setComments(getBlockRemarks());
							assetActivity.setStatus(getBlockStatus());
							assetActivityBean.insert(assetActivity);
						}
						fromDate = DateUtils.addDays(fromDate, 1);
					}
				} catch (ManagerBeanException ex) {
					String msg = "Error bloqueando Habitaciones!";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}

			sendInventoryData(getCheckedRooms().get(0).getHotel(), inventoryItems, getBlockFromDate(), getBlockToDate());
		}
	}

	public void onRoomUnblock(ActionEvent event) {
		if (getCheckedCount() > 0) {
			List<Integer> checkedRoomsIds = new LinkedList<Integer>();
			List<Item> inventoryItems = new LinkedList<Item>();
			for (Room room : getCheckedRooms()) {
				checkedRoomsIds.add(room.getAsset().getId());
				if (!inventoryItems.contains(room.getItem())) {
					inventoryItems.add(room.getItem());
				}
			}

			try {
				IManagerBean assetActivityBean = BeanManager.getManagerBean(AssetActivity.class);
				Criteria criteria = new Criteria();
				criteria.addExpression(ExpressionUtilities.getInExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_ID), checkedRoomsIds));
				criteria.addGreaterThanOrEqualExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), getBlockFromDate());
				criteria.addLessThanOrEqualExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), getBlockToDate());
				criteria.addNotEqualExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_STATUS), ActivityStatus.BUSY);
				for (ITransferObject ito : assetActivityBean.getList(criteria)) {
					assetActivityBean.remove((AssetActivity)ito);
				}

				sendInventoryData(getCheckedRooms().get(0).getHotel(), inventoryItems, getBlockFromDate(), getBlockToDate());
			} catch (ManagerBeanException ex) {
				String msg = "Error desbloqueando Habitaciones!";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
	}

    private void sendInventoryData(Hotel hotel, List<Item> inventoryItems, Date startDate, Date endDate) {
    	InventoryManager manager = new InventoryManager();
		for (Item item : inventoryItems) {
			manager.processInventoryQuery(null, hotel, item, startDate, endDate);
		}
    }


	private ArrayList<Room> checks = new ArrayList<Room>();

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		Room to = (Room)model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Room to = (Room)model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Room to = (Room)model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<Room> getCheckedRooms() {
		return checks;
	}
	
	public void clearCheckedRooms() {
		checks = new ArrayList<Room>();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : this.getManagerBean().getList(this.getCriteria())) {
			Room room = (Room)ito;
			if (!checks.contains(room)) {
				checks.add(room);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedRooms();
	}

	public int getCheckedCount() {
		return getCheckedRooms().size();
	}

}