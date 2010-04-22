package com.code.aon.ui.asset.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.asset.Asset;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.dao.IAssetAlias;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class ActivityBasicController extends BasicController{
	
	private String fromTimeHours;
	private String fromTimeMins;
	private String toTimeHours;
	private String toTimeMins;
	
	private String who;
	private Asset asset;
	
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
	
	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}
	
	public void buildFromTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(fromTimeHours));
		cal.set(Calendar.MINUTE, Integer.parseInt(fromTimeMins));
		cal.set(Calendar.SECOND, 0);
		((AssetActivity)this.getTo()).setFromTime(cal.getTime());
	}

	public void buildToTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(toTimeHours));
		cal.set(Calendar.MINUTE, Integer.parseInt(toTimeMins));
		cal.set(Calendar.SECOND, 0);
		((AssetActivity)this.getTo()).setToTime(cal.getTime());
	}

	public void setControllerTime() {
		AssetActivity to = (AssetActivity)this.getTo(); 
		Calendar fromTime = new GregorianCalendar();
		Calendar toTime = new GregorianCalendar();
		fromTime.setTime(to.getFromTime());
		toTime.setTime(to.getToTime());
		this.setFromTimeHours(((Integer)fromTime.get(Calendar.HOUR_OF_DAY)).toString());
		this.setFromTimeMins(((Integer)fromTime.get(Calendar.MINUTE)).toString());
		this.setToTimeHours(((Integer)toTime.get(Calendar.HOUR_OF_DAY)).toString());
		this.setToTimeMins(((Integer)toTime.get(Calendar.MINUTE)).toString());
	}
	
	public void initializePendingList(ActionEvent event){
		try {
			((ActivityDialogController)AonUtil.getRegisteredBean(("activityDialog"))).setRequest(false);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(this.getFieldName(IAssetAlias.ASSET_ACTIVITY_STATUS), ActivityStatus.PENDING);
			this.setCriteria(criteria);
			this.onSearch(null);
			this.setCriteria(new Criteria());
		} catch (ManagerBeanException e) {
			String msg="Error al recuperar las reservas pendientes";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(e.getMessage(),e);
		}
		
	}

	public void acceptPendingAssetList(ActionEvent event){
		try {
			changeActivityStatus(ActivityStatus.ACCEPTED);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error al aceptar la reserva. "+e.getMessage());
			throw new AbortProcessingException(e.getMessage(),e);
		}
		initializePendingList(event);
	}
	
	public void cancelPendingAssetList(ActionEvent event){
		try {
			changeActivityStatus(ActivityStatus.REFUSED);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error al denegar la reserva.");
			throw new AbortProcessingException(e.getMessage(),e);
		}
		initializePendingList(event);
	}
	
	private void changeActivityStatus(ActivityStatus status) throws ManagerBeanException{
		this.onSelectFirst(null);
		AssetActivity to;
		while(!this.isInLast()){
			to = (AssetActivity)this.getTo();
			if(to.isCheck()){
				to.setStatus(status);
				System.out.println(to.getId());
				this.update();
			}
			this.onSelectNext(null);
		}
		to = (AssetActivity)this.getTo();
		if(to.isCheck()){
			to.setStatus(status);
			System.out.println(to.getId());
			this.update();
		}
	}
	
	public List<SelectItem> getStatusList() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> statusList = new LinkedList<SelectItem>();
		for (ActivityStatus e : ActivityStatus.values()) {
			String name = e.getName(locale);
			SelectItem item = new SelectItem(e, name);
			statusList.add(item);
		}
		return statusList;
	}
	
	public List<SelectItem> getWhoList() {
		List<SelectItem> whoList = new LinkedList<SelectItem>();
		try {
			Projection projection = Projection.group(getManagerBean().getFieldName(IAssetAlias.ASSET_ACTIVITY_WHO));
			ProjectionList pl = new ProjectionList();
			pl.add(projection);
			Object valueList = getManagerBean().getList(pl, null);
			ArrayList<String> list = (ArrayList<String>) valueList; 
			for (String who : list) {
				String name = who;
				SelectItem item = new SelectItem(who, name);
				whoList.add(item);
			}
		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return whoList;
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		super.onSearch(event);
	}
}
