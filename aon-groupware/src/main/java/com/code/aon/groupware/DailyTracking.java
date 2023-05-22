package com.code.aon.groupware;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.DailyTrackingDB;

@Entity
@Table(name="daily_tracking")
public class DailyTracking extends DailyTrackingDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private int hours;
	private int minutes;

	@Transient
	public Double getAmount() {
		return CommonUtil.round(getTrackingDuration() * getCost());
	}

	@Transient
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}

	@Transient
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	@Override
	public void setTrackingDuration(double trackingDuration) {
		super.setTrackingDuration(trackingDuration);
		setHours( (int) trackingDuration  );
		double minutes = (trackingDuration % 1);
		minutes =  minutes * 60; 
		setMinutes( (int) CommonUtil.round(minutes,0));
	}
	
}