package com.code.aon.ui.asset.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.asset.Asset;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.dao.IAssetAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AssetCalendarController extends BasicController{
	
	private Date calendarDay;
	private Criteria criteria;
	private boolean fractionCell;
	
	private int numFraction = (IAssetConstants.END_TIME - IAssetConstants.START_TIME) * (60 / IAssetConstants.FRACTION_TIME); 
	private List<Integer> timeFractionList = new ArrayList<Integer>();
	private List<Integer> hoursList = new ArrayList<Integer>();
	
	
	public boolean isFractionCell() {
		return fractionCell;
	}

	public void setFractionCell(boolean fractionCell) {
		this.fractionCell = fractionCell;
	}
	
	public Date getCalendarDay() {
		return calendarDay;
	}

	public void setCalendarDay(Date calendarDay) {
		this.calendarDay = calendarDay;
	}

	public void onDaySelected(ActionEvent event) {
		
		setCalendarDay(Calendar.getInstance().getTime());
		
		try {
			restoreCriteria();
			
			this.onSearch(null);
			initializeAssetFractionList();
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			// controlar la excepcion, igual tb abortProcessException, ....
			e.printStackTrace();
		}
		
	}
	
	public void onInitialize(ActionEvent event) {
		getTimeFractionList();
		onDaySelected(event);
		ActivityDialogController adc = new ActivityDialogController();
		adc.onInitializeRequest(event);
	}
	
	public void refreshSelectedDay(ValueChangeEvent event) {
		setCalendarDay((Date)event.getNewValue());
		ActivityDialogController adc = (ActivityDialogController)AonUtil.getRegisteredBean(IAssetConstants.ACTIVITY_DIALOG_CONTROLLER_NAME);
		adc.setFromDate((Date)event.getNewValue());
		adc.setToDate((Date)event.getNewValue());
		
		try {
			restoreCriteria();
			
			this.onSearch(null);
			initializeAssetFractionList();
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			// controlar la excepcion, igual tb abortProcessException, ....
			e.printStackTrace();
		}
	}
	
	private void restoreCriteria() throws ManagerBeanException{
		this.clearCriteria();
		criteria = this.getCriteria();
		criteria.addEqualExpression(getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE), getCalendarDay());
	}
	
	public List<Integer> getTimeFractionList() {
		timeFractionList = new ArrayList<Integer>();
		
		int blank=60/IAssetConstants.FRACTION_TIME;
		int inc=0;
		for(int i=0; i<numFraction; i++){
			if(blank<60/IAssetConstants.FRACTION_TIME){
				timeFractionList.add(i,blank*IAssetConstants.FRACTION_TIME);
				blank++;
				fractionCell=true;
			} else {
				timeFractionList.add(i,IAssetConstants.START_TIME+inc);
				blank=1;
				inc++;
				fractionCell=false;
			}
		}
		return timeFractionList;
	}

	public List<Integer> getHoursList() {
		hoursList = new ArrayList<Integer>();
		
		for(int i=8; i<IAssetConstants.END_TIME; i++){
			hoursList.add(i);
		}
		return hoursList;
	}
	
	public int getFractionTime() {
		return IAssetConstants.FRACTION_TIME;
	}
		
	/* *****************************************************/
	/* *****************************************************/
	/* *****************************************************/
	/* *****************************************************/
	
	private DataModel dayAssetModel;
	private List<DayAssetList> dayAssetList;
	
	public DataModel getDayAssetModel() throws ManagerBeanException {
		dayAssetModel = new ListDataModel(getDayAssetList());
		return dayAssetModel;
	}
	public void setDayAssetModel(DataModel dayAssetModel) {
		this.dayAssetModel = dayAssetModel;
	}
	
	public List<DayAssetList> getDayAssetList() {
		return dayAssetList;
	}

	public void setDayAssetList(List<DayAssetList> dayAssetList) {
		this.dayAssetList = dayAssetList;
	}	

	public void initializeAssetFractionList() throws ManagerBeanException{
		
		List<ITransferObject> activityList = BeanManager.getManagerBean(AssetActivity.class).getList(criteria);
		
		initializeAssetList();
		
		for(ITransferObject to:activityList){
			AssetActivity aa = (AssetActivity)to;
			
			boolean exist=false;
			Iterator<DayAssetList> it = dayAssetList.iterator();
			int numIterations;
			int position;
			
			while(!exist && it.hasNext()){
				DayAssetList dait = it.next();
				
				if(dait.getAsset().getName().equals(aa.getAsset().getName())){
					position = fractionPosition(aa.getFromTime());
					numIterations = fractionIterations(aa.getFromTime(), aa.getToTime());
					for(int i=0; i<numIterations;i++)
						dait.getFractions().set(position+i,new Fraction(true));
					dait.getFractions().get(position).setFirst(true);
					dait.getFractions().get(position).setActivity(aa);
					exist = true;
				} else {
					dait.getFractions().add(new Fraction(false));
				}
			}
		}
	}
	
	public void initializeAssetList() throws ManagerBeanException {
		List<ITransferObject> assetList = BeanManager.getManagerBean(Asset.class).getList(null);
		dayAssetList = new ArrayList<DayAssetList>();
		DayAssetList dal;
		List<Fraction> fractions;
		
		for(ITransferObject to:assetList){
			Asset aa = (Asset)to;
			fractions = new ArrayList<Fraction>();
			for(int i=0; i<numFraction; i++){
				fractions.add(null);
			}
			dal = new DayAssetList();
			dal.setFractions(fractions);
			dal.setAsset(aa);
			dayAssetList.add(dal);
		}
	}
	
	private int fractionPosition(Date time) throws ManagerBeanException {
		int hours, minutes;
		Calendar c = new GregorianCalendar();
		c.setTime(time);
		
		hours = c.get(Calendar.HOUR_OF_DAY);
		minutes = c.get(Calendar.MINUTE);
		if(timeFractionList.indexOf(hours)+(minutes/IAssetConstants.FRACTION_TIME) >= 0)
			return timeFractionList.indexOf(hours)+(minutes/IAssetConstants.FRACTION_TIME);
		else
			throw new ManagerBeanException("Fraction start time out of bound");
			// buscar la excepcion adecuada
	}
	
	private int fractionIterations(Date fromTime, Date toTime){
		Long hours, minutes, result;
		
		hours=fromTime.getTime();
		minutes=toTime.getTime();
		result=(minutes-hours)/(1000*60);
		
		return result.intValue()/IAssetConstants.FRACTION_TIME;
	}
	
	/* ****************/
	/* CLASES WRAPPED */
	/* ****************/
	
	public class DayAssetList {

		private Asset asset;
		private List<Fraction> fractions;

		public List<Fraction> getFractions() {
			return fractions;
		}

		public void setFractions(List<Fraction> fractions) {
			this.fractions = fractions;
		}
		
		public Asset getAsset() {
			return asset;
		}
		public void setAsset(Asset asset) {
			this.asset = asset;
		}
	}

	public class Fraction {
		private AssetActivity activity;
		
		private boolean reserved;
		private boolean first;

		public Fraction(boolean reserved){
			this.reserved = reserved;
			//this.first = true;
		}

		public boolean isReserved() {
			return reserved;
		}

		public void setReserved(boolean reserved) {
			this.reserved = reserved;
		}

		public boolean isFirst() {
			return first;
		}

		public void setFirst(boolean first) {
			this.first = first;
		}
		public AssetActivity getActivity() {
			return activity;
		}

		public void setActivity(AssetActivity activity) {
			this.activity = activity;
		}
	}


	
}
