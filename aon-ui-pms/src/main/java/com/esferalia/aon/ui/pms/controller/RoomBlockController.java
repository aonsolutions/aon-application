package com.esferalia.aon.ui.pms.controller;

import java.util.Calendar;
import java.util.Date;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Room;

public class RoomBlockController extends LinesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RoomBlockController.class.getName());
	
	private Date fromDate;
	private Date toDate;
	private String comments;
	
	private boolean showMultipleBlockWindow;
	
	public boolean isShowMultipleBlockWindow() {
		return showMultipleBlockWindow;
	}
	public void setShowMultipleBlockWindow(boolean showMultipleBlockWindow) {
		this.showMultipleBlockWindow = showMultipleBlockWindow;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public void onSelectRoom(ActionEvent event) throws ManagerBeanException {
		IController controller = FormUtil.getController(IPmsConstants.ROOM_CONTROLLER_NAME);
		controller.onSelect(event);
		searchRoomBlocks(event);
	}
	
	private void searchRoomBlocks(ActionEvent event) throws ManagerBeanException {
		IController controller = FormUtil.getController(IPmsConstants.ROOM_CONTROLLER_NAME);
		this.getCriteria().addEqualExpression(getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_ID), ((Room) controller.getTo()).getAsset().getId());
		this.onSearch(event);
	}
	
	public void onAcceptMultipleBlock(ActionEvent event) throws ManagerBeanException {
		IController controller = FormUtil.getController(IPmsConstants.ROOM_CONTROLLER_NAME);
		Room room = (Room) controller.getTo();
		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		fromCal.setTime(getFromDate());
		toCal.setTime(getToDate());
		
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			while(fromCal.before(toCal) || fromCal.equals(toCal)){
				AssetActivity aa = new AssetActivity();
				aa.setAsset(room.getAsset());
				aa.setDate(fromCal.getTime());
				aa.setComments(getComments());
				aa.setStatus(ActivityStatus.BLOCKED);
				aa.setFromTime(aa.getDate());
				aa.setToTime(aa.getDate());
				this.getManagerBean().insert(aa);
				fromCal.add(Calendar.DAY_OF_MONTH, 1);
			}
	
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
		
		searchRoomBlocks(event);
	}
	
		
}

