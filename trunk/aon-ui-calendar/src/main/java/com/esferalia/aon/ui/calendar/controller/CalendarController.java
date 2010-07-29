package com.esferalia.aon.ui.calendar.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.richfaces.model.CalendarDataModel;

import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.calendar.enumeration.DayType;

public class CalendarController extends BasicController{

	private List<SelectItem> dayTypes;
	private Integer year;
	private CalendarDataModel januaryModel;
	private Date date0;
	private Date date1;
	private Date date2;
	private Date date3;
	private Date date4;
	private Date date5;
	private Date date6;
	private Date date7;
	private Date date8;
	private Date date9;
	private Date date10;
	private Date date11;
	
	
	public Date getDate0() {
		return date0;
	}

	public void setDate0(Date date0) {
		this.date0 = date0;
	}

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public Date getDate3() {
		return date3;
	}

	public void setDate3(Date date3) {
		this.date3 = date3;
	}

	public Date getDate4() {
		return date4;
	}

	public void setDate4(Date date4) {
		this.date4 = date4;
	}

	public Date getDate5() {
		return date5;
	}

	public void setDate5(Date date5) {
		this.date5 = date5;
	}

	public Date getDate6() {
		return date6;
	}

	public void setDate6(Date date6) {
		this.date6 = date6;
	}

	public Date getDate7() {
		return date7;
	}

	public void setDate7(Date date7) {
		this.date7 = date7;
	}

	public Date getDate8() {
		return date8;
	}

	public void setDate8(Date date8) {
		this.date8 = date8;
	}

	public Date getDate9() {
		return date9;
	}

	public void setDate9(Date date9) {
		this.date9 = date9;
	}

	public Date getDate10() {
		return date10;
	}

	public void setDate10(Date date10) {
		this.date10 = date10;
	}

	public Date getDate11() {
		return date11;
	}

	public void setDate11(Date date11) {
		this.date11 = date11;
	}

	public CalendarDataModel getJanuaryModel() {
		return januaryModel;
	}

	public void setJanuaryModel(CalendarDataModel januaryModel) {
		this.januaryModel = januaryModel;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<SelectItem> getAllDayTypes() {
		
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			dayTypes = new LinkedList<SelectItem>();
			for (DayType day : DayType.values()) {
				String name = day.getName(locale);
				SelectItem item = new SelectItem(day, name);
				dayTypes.add(item);
			}
		
		return dayTypes;
	}

	public List<SelectItem> getDayTypes() {
		
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			dayTypes = new LinkedList<SelectItem>();
			for (DayType day : DayType.values()) {
				String name = day.getName(locale);
				SelectItem item = new SelectItem(day, name);
				if (item.getLabel().equals("V")||item.getLabel().equals("F")) {
					return dayTypes;
				} else {
					dayTypes.add(item);
				}
			}
		
		return dayTypes;
	}
	
	public void onReset(ActionEvent e){
		
		GregorianCalendar cal= new GregorianCalendar();
		this.setYear(cal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH,0);
		this.setDate0(cal.getTime());
		cal.set(Calendar.MONTH,1);
		this.setDate1(cal.getTime());
		cal.set(Calendar.MONTH,2);
		this.setDate2(cal.getTime());
		cal.set(Calendar.MONTH,3);
		this.setDate3(cal.getTime());
		cal.set(Calendar.MONTH,4);
		this.setDate4(cal.getTime());
		cal.set(Calendar.MONTH,5);
		this.setDate5(cal.getTime());
		cal.set(Calendar.MONTH,6);
		this.setDate6(cal.getTime());
		cal.set(Calendar.MONTH,7);
		this.setDate7(cal.getTime());
		cal.set(Calendar.MONTH,8);
		this.setDate8(cal.getTime());
		cal.set(Calendar.MONTH,9);
		this.setDate9(cal.getTime());
		cal.set(Calendar.MONTH,10);
		this.setDate10(cal.getTime());
		cal.set(Calendar.MONTH,11);
		this.setDate11(cal.getTime());
	}

}
