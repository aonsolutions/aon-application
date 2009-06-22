package com.code.aon.ui.asset.controller;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.code.aon.asset.AssetActivity;
import com.code.aon.ui.form.BasicController;

public class ActivityBasicController extends BasicController{
	
	private String fromTimeHours;
	private String fromTimeMins;
	private String toTimeHours;
	private String toTimeMins;
	
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
}
