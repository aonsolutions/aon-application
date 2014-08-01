package com.esferalia.aon.ui.calendar.print;

import java.io.Serializable;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Month;

public class PrintableMonth implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<PrintableDay> dayList;
	private Month month;
	private Double hours;

	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}
	
	public List<PrintableDay> getDayList() {
		return dayList;
	}
	public void setDayList(List<PrintableDay> dayList) {
		this.dayList = dayList;
	}
	public Double getHours() {
		calculateHours();
		return hours;
	}
	public void setHours(Double hours) {
		this.hours = hours;
	}
	
	private void calculateHours(){
		hours = 0.0;
		for(PrintableDay day: getDayList()){
			if(!day.isHoliday() && !day.isVacation() && !day.isNotWorkingDay() && day.getHours()!=null){
				setHours(hours+day.getHours());
			}
		}
	}

}
