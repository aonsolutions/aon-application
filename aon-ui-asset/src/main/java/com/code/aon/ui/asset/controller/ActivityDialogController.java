package com.code.aon.ui.asset.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.asset.Asset;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.dao.IAssetAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ActivityDialogController {

	private Asset asset;
	private Date fromDate;
	private Date toDate;
	private Date fromTime;
	private Date toTime;
	private String duration;
	private String who;
	private String why;

	private String fromTimeHours;
	private String fromTimeMins;
	private String toTimeHours;
	private String toTimeMins;

	private static final Logger LOGGER = Logger
			.getLogger(ActivityDialogController.class.getName());
	private boolean isNew;
	private boolean request;
	

	private List<SelectItem> hoursList;
	private List<SelectItem> minsList;

	public Asset getAsset() {
		return asset;

	}

	public void setAsset(Asset asset) {
		this.asset = asset;
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

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public String getWhy() {
		return why;
	}

	public void setWhy(String why) {
		this.why = why;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isRequest() {
		return request;
	}

	public void setRequest(boolean request) {
		this.request = request;
	}

	public String getFromTimeHours() {
		return fromTimeHours;
	}

	public void setFromTimeHours(String fromTimeHours) {
		this.fromTimeHours = fromTimeHours;
	}

	public String getFromTimeMins() {
		return fromTimeMins;
	}

	public void setFromTimeMins(String fromTimeMins) {
		this.fromTimeMins = fromTimeMins;
	}

	public String getToTimeHours() {
		return toTimeHours;
	}

	public void setToTimeHours(String toTimeHours) {
		this.toTimeHours = toTimeHours;
	}

	public String getToTimeMins() {
		return toTimeMins;
	}

	public void setToTimeMins(String toTimeMins) {
		this.toTimeMins = toTimeMins;
	}
	
	public void onInitialize(ActionEvent event) {
		setAsset(new Asset());
		setFromDate(Calendar.getInstance().getTime());
		setToDate(Calendar.getInstance().getTime());
		setFromTime(null);
		setToTime(null);
		setWho(null);
		setWhy(null);
		setNew(true);
		setRequest(false);
		setFromTimeHours("8");
		setFromTimeMins("00");
		setToTimeHours("8");
		setToTimeMins("00");
	}

	public void onInitializeRequest(ActionEvent event) {
		onInitialize(event);
		setRequest(true);
	}

	public void onAccept(ActionEvent event) {
		try {
			buildFromTime();
			buildToTime();
			if (isValidDate() && isValidTime()) {
				insertDays();
				onSearchInserted(event);
			}
		} catch (ManagerBeanException e) {
			if (e.getCause() instanceof ManagerBeanVetoListenerException) {
				AonUtil.addErrorMessageFromBundle("assetBundle","asset_error_hoverlap");
			} else {
				AonUtil.addErrorMessageFromBundle(e.getMessage());
			}
			LOGGER.severe(">>>> insertDays " + e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onEmail(ActionEvent event) {
		buildFromTime();
		buildToTime();
		if (isValidDate() && isValidTime()) {
			setNew(false);
			AonUtil.addInfoMessage("ENVIANDO MENSAJE");
			//LOGGER.info("ENVIANDO MENSAJE");
		}
	}

	public void onCancel(ActionEvent event) {
		onInitialize(event);
	}

	private boolean isValidDate() {
		if (fromDate.after(toDate)) {
			//ResourceBundle bundle = AonUtil.getResourceBundle("assetBundle");
			//AonUtil.addErrorMessage(bundle.getString("assetBundle","asset_error_date_range"));
			AonUtil.addErrorMessageFromBundle("assetBundle","asset_error_date_range");
			return false;
		}
		return true;
	}

	private boolean isValidTime() {
		if (fromTime.after(toTime) || fromTime.equals(toTime)) {
			//ResourceBundle bundle = AonUtil.getResourceBundle("assetBundle");
			//AonUtil.addErrorMessage(bundle.getString("asset_error_time_range"));
			AonUtil.addErrorMessageFromBundle("assetBundle","asset_error_time_range");
			return false;
		}
		return true;
	}

	private void buildFromTime() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(fromTimeHours));
		cal.set(Calendar.MINUTE, Integer.parseInt(fromTimeMins));
		cal.set(Calendar.SECOND, 0);
		setFromTime(cal.getTime());
	}

	private void buildToTime() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(toTimeHours));
		cal.set(Calendar.MINUTE, Integer.parseInt(toTimeMins));
		cal.set(Calendar.SECOND, 0);
		setToTime(cal.getTime());
	}

	private void insertDays() throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				Calendar cal = Calendar.getInstance();
				cal.setTime(getFromDate());
				IManagerBean bean = BeanManager.getManagerBean(AssetActivity.class);
				while (!cal.getTime().after(getToDate())) {
					AssetActivity to = new AssetActivity();
					to.setDate(cal.getTime());
					to.setFromTime(getFromTime());
					to.setToTime(getToTime());
					to.setWho(getWho());
					to.setWhy(getWhy());
					to.setAsset(getAsset());
					bean.insert(to);
					cal.add(Calendar.DAY_OF_MONTH, 1);
				}
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, e);
				}
				String msg = "Error on aon-asset:  " + e.getMessage() ;
				LOGGER.log(Level.SEVERE, msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private void onSearchInserted(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		IManagerBean bean = BeanManager.getManagerBean(AssetActivity.class);
		criteria.addEqualExpression(bean.getFieldName(IAssetAlias.ASSET_ACTIVITY_ASSET_ID), getAsset().getId());
		criteria.addBetweenExpression(bean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE), getFromDate(),getToDate());
		criteria.addOrder(bean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE));
		criteria.addOrder(bean.getFieldName(IAssetAlias.ASSET_ACTIVITY_FROM_TIME));
		BasicController controller = (BasicController) FormUtil.getController(IAssetConstants.ACTIVITY_BASIC_CONTROLLER_NAME);
		controller.setCriteria(criteria);
		controller.onSearch(null);
		setNew(false);
	}

	public List<SelectItem> getHours() {
		if (hoursList == null) {
			hoursList = new LinkedList<SelectItem>();
			SelectItem item;
			for (int i = IAssetConstants.START_TIME; i < IAssetConstants.END_TIME; i++) {
				item = new SelectItem(String.valueOf(i), String.valueOf(i));
				hoursList.add(item);
			}
		}
		return hoursList;
	}

	public List<SelectItem> getMins() {
		if (minsList == null) {
			minsList = new LinkedList<SelectItem>();
			SelectItem item;
			
			for (int i = 0; i < 60; i=i+IAssetConstants.FRACTION_TIME) {
				item = new SelectItem(String.valueOf(i), String.valueOf(i));
				minsList.add(item);
			}
		}
		return minsList;
	}

	
	

}
