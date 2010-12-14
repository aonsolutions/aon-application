package com.esferalia.aon.ui.calendar.print;

import java.util.List;

import com.code.aon.common.enumeration.Month;

public class MonthFactory {
	
	private List<PrintableMonth> monthList;
	
	public List<PrintableMonth> getMonthList() {
		return monthList;
	}
	public void setMonthList(List<PrintableMonth> monthList) {
		this.monthList = monthList;
	}
	
	public PrintableMonth getJanuary(){
		return getMonthList().get(Month.JANUARY.ordinal());
	}
	public PrintableMonth getFebruary(){
		return getMonthList().get(Month.FEBRUARY.ordinal());
	}
	public PrintableMonth getMarch(){
		return getMonthList().get(Month.MARCH.ordinal());
	}
	public PrintableMonth getApril(){
		return getMonthList().get(Month.APRIL.ordinal());
	}
	public PrintableMonth getMay(){
		return getMonthList().get(Month.MAY.ordinal());
	}
	public PrintableMonth getJune(){
		return getMonthList().get(Month.JUNE.ordinal());
	}
	public PrintableMonth getJuly(){
		return getMonthList().get(Month.JULY.ordinal());
	}
	public PrintableMonth getAugust(){
		return getMonthList().get(Month.AUGUST.ordinal());
	}
	public PrintableMonth getSeptember(){
		return getMonthList().get(Month.SEPTEMBER.ordinal());
	}
	public PrintableMonth getOctober(){
		return getMonthList().get(Month.OCTOBER.ordinal());
	}
	public PrintableMonth getNovember(){
		return getMonthList().get(Month.NOVEMBER.ordinal());
	}
	public PrintableMonth getDecember(){
		return getMonthList().get(Month.DECEMBER.ordinal());
	}
	
	
}
