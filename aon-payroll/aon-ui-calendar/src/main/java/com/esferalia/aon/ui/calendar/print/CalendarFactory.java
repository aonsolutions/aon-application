package com.esferalia.aon.ui.calendar.print;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.CalendarHoliday;
import com.esferalia.aon.calendar.HolidayDetail;
import com.esferalia.aon.calendar.dao.ICalendarAlias;
import com.esferalia.aon.ui.calendar.controller.CalendarHolidayDataController;

public class CalendarFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarFactory.class.getName());
	
	private List<PrintableMonth> monthList;
	private List<HolidayDay> holidayList;
	
	public List<PrintableMonth> getMonthList() {
		return monthList;
	}
	public void setMonthList(List<PrintableMonth> monthList) {
		this.monthList = monthList;
	}
	public List<HolidayDay> getHolidayList() {
		return holidayList;
	}
	public void setHolidayList(List<HolidayDay> holidayList) {
		this.holidayList = holidayList;
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
	
	public void loadHolidays(){
		CalendarHolidayDataController controller = (CalendarHolidayDataController) AonUtil.getRegisteredBean("calendarHolidayData");
		holidayList = new LinkedList<HolidayDay>();
		HolidayDay day;
		for(String key: controller.getHolidays().keySet()){
			day = new HolidayDay();
			day.setDescription(key);
			day.setDate(null);
			holidayList.add(day);
			for(ITransferObject to: controller.getHolidays().get(key)){
				HolidayDetail h = (HolidayDetail) to;
				day = new HolidayDay();
				day.setDescription(h.getDescription());
				day.setDate(h.getDate());
				holidayList.add(day);
			}
		}
		completeCalendarHolidays();
		asignHolidays();
	}
	
	private void completeCalendarHolidays(){
		Calendar calendar = (Calendar) FormUtil.getController("calendar").getTo();
		IManagerBean bean;
		Criteria criteria;
		try {
			bean = BeanManager.getManagerBean(CalendarHoliday.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICalendarAlias.CALENDAR_HOLIDAY_CALENDAR_ID), calendar.getId());
			HolidayDay day;
			day = new HolidayDay();
			day.setDescription("Otros");
			day.setDate(null);
			holidayList.add(day);
			for(ITransferObject to: bean.getList(criteria)){
				CalendarHoliday h = (CalendarHoliday) to;
				day = new HolidayDay();
				day.setDescription(h.getDescription());
				day.setDate(h.getDate());
				holidayList.add(day);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void asignHolidays() {
		java.util.Calendar cal;
		PrintableMonth month;
		for(HolidayDay d: getHolidayList()){
			if(d.getDate()!=null){
				cal = new GregorianCalendar();
				cal.setTime(d.getDate());
				month = getMonthList().get(cal.get(java.util.Calendar.MONTH));
				for(PrintableDay day: month.getDayList()){
					if(day.getDayOfMonth()!=null && day.getDayOfMonth().equals(cal.get(java.util.Calendar.DAY_OF_MONTH))){
						day.setHoliday(true);
					}
				}
			}
		}
	}

	public class HolidayDay {
		private String description;
		private Date date;
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
	}
	
	
}
