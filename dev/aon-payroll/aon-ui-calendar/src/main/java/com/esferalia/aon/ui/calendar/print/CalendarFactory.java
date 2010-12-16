package com.esferalia.aon.ui.calendar.print;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
//import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.CalendarHoliday;
import com.esferalia.aon.calendar.CalendarPeriod;
import com.esferalia.aon.calendar.HolidayDetail;
import com.esferalia.aon.calendar.dao.ICalendarAlias;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.ui.calendar.controller.CalendarController;
import com.esferalia.aon.ui.calendar.controller.CalendarHolidayDataController;

public class CalendarFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarFactory.class.getName());
	
	private List<CalendarDay> dayList;
//	private List<CalendarDay> periodDayList;
	private List<CalPeriod> periodList;
	private List<PrintableMonth> monthList;
	
	public List<CalendarDay> getDayList() {
		return dayList;
	}
	public void setDayList(List<CalendarDay> dayList) {
		this.dayList = dayList;
	}
//	public List<CalendarDay> getPeriodDayList() {
//		return periodDayList;
//	}
//	public void setPeriodDayList(List<CalendarDay> periodDayList) {
//		this.periodDayList = periodDayList;
//	}
	public List<CalPeriod> getPeriodList() {
		return periodList;
	}
	public void setPeriodList(List<CalPeriod> periodList) {
		this.periodList = periodList;
	}
	public List<PrintableMonth> getMonthList() {
		return monthList;
	}
	public void setMonthList(List<PrintableMonth> monthList) {
		this.monthList = monthList;
	}
	
	/*
	 * PrintableMonth por cada mes
	 */
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
	
	
	public void buildCalendar(){
		this.loadHolidays();
		this.loadCalendarDays();
		buildMonthList();
		this.loadPeriodDays();
//		this.asignPeriodDays(periodDayList);
		this.asignDays(dayList);
	}
	
	@SuppressWarnings("unchecked")
	private void loadPeriodDays() {
		// TODO
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean("calendar");
		periodList = new LinkedList<CalPeriod>();
//		periodDayList = new LinkedList<CalendarDay>();
		try {
			List<ITransferObject> list = (List<ITransferObject>) FormUtil.getController("calendarPeriod").getModel().getWrappedData();
			CalPeriod period;
			CalendarDay day;
			for(ITransferObject to: list){
				CalendarPeriod p = (CalendarPeriod) to;
				period = new CalPeriod();
				period.setDescription(p.getDescription());
				period.setMonth(p.getMonth());
				period.setStartDay(p.getStartDay());
				period.setEndDay(p.getEndDay());
				periodList.add(period);
				
				Calendar cal = new GregorianCalendar();
				int d = p.getStartDay();
				while(d <= p.getEndDay()){
					day = new CalendarDay();
					cal.set(controller.getYear(), p.getMonth().ordinal(), d);
										
//					day.setDate(cal.getTime());
//					day.setDescription(p.getDescription());
//					
//					addPeriodDayInfo(day, cal);
//					day.setType(type);
//					day.setHours(hours);
//					addCalendarInfo();
					
//					periodDayList.add(day);
					
					
					asignPeriodDay(p, cal);
					
					d++;
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("ERROR in loadPeriodDays");
		}
	}
	
	private void loadHolidays(){
		dayList = new LinkedList<CalendarDay>();
		CalendarHolidayDataController controller = (CalendarHolidayDataController) AonUtil.getRegisteredBean("calendarHolidayData");
		CalendarDay day;
		for(String key: controller.getHolidays().keySet()){
			day = new CalendarDay();
			day.setDescription(key);
			day.setDate(null);
			dayList.add(day);
			for(ITransferObject to: controller.getHolidays().get(key)){
				HolidayDetail h = (HolidayDetail) to;
				day = new CalendarDay();
				day.setDescription(h.getDescription());
				day.setDate(h.getDate());
				day.setType(DayType.HOLIDAY);
				dayList.add(day);
			}
		}
	}
	
	private void loadCalendarDays(){
		ITransferObject cal = (ITransferObject) FormUtil.getController("calendar").getTo();
		Integer id = ((com.esferalia.aon.calendar.Calendar)cal).getId();
		IManagerBean bean;
		Criteria criteria;
		try {
			bean = BeanManager.getManagerBean(CalendarHoliday.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICalendarAlias.CALENDAR_HOLIDAY_CALENDAR_ID), id);
			CalendarDay day;
			day = new CalendarDay();
			Locale locale = AonUtil.getCurrentLocale();
			ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.calendar.i18n.messages", locale);	
			day.setDescription(bundle.getString("aon_enum_daytype_full_OTHER"));
			day.setDate(null);
			dayList.add(day);
			for(ITransferObject to: bean.getList(criteria)){
				CalendarHoliday h = (CalendarHoliday) to;
				day = new CalendarDay();
				day.setDescription(h.getDescription());
				day.setDate(h.getDate());
				day.setHours(h.getHours());
				day.setType(h.getDayType());
				dayList.add(day);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void asignDays(List<CalendarDay> list) {
		java.util.Calendar cal;
		PrintableMonth month;
		for(CalendarDay d: list){
			if(d.getDate()!=null){
				cal = new GregorianCalendar();
				cal.setTime(d.getDate());
				month = getMonthList().get(cal.get(java.util.Calendar.MONTH));
				for(PrintableDay day: month.getDayList()){
					if(day.getDayOfMonth()!=null){
						if(day.getDayOfMonth().equals(cal.get(java.util.Calendar.DAY_OF_MONTH))){
							day.disableAllTypes();
							setDayType(day, d.getType());
							if(d.getHours()!=null){
								day.setHours(d.getHours());
							}
						}
					}
				}
			}
		}
	}

	private void asignPeriodDay(CalendarPeriod period, Calendar calendar) {
		// TODO
		
		PrintableMonth month = getMonthList().get(calendar.get(java.util.Calendar.MONTH));
		for(PrintableDay day: month.getDayList()){
			if(day.getDayOfMonth()!=null){
				if(day.getDayOfMonth().equals(calendar.get(java.util.Calendar.DAY_OF_MONTH))){
					day.disableAllTypes();
					addPeriodInfo(day, calendar, period);
//					setDayType(day, d.getType());
//					if(d.getHours()!=null){
//						day.setHours(d.getHours());
//					}
				}
			}
		}
		
		
//		java.util.Calendar cal;
//		PrintableMonth month;
//		for(CalendarDay d: list){
//			if(d.getDate()!=null){
//				cal = new GregorianCalendar();
//				cal.setTime(d.getDate());
//				month = getMonthList().get(cal.get(java.util.Calendar.MONTH));
//				for(PrintableDay day: month.getDayList()){
//					if(day.getDayOfMonth()!=null){
//						if(day.getDayOfMonth().equals(cal.get(java.util.Calendar.DAY_OF_MONTH))){
//							day.disableAllTypes();
//							setDayType(day, d.getType());
//							if(d.getHours()!=null){
//								day.setHours(d.getHours());
//							}
//						}
//					}
//				}
//			}
//		}
	}
	
	private void buildMonthList() {
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean("calendar");
		Integer year = controller.getYear();
		LinkedList<PrintableMonth> months = new LinkedList<PrintableMonth>();
		PrintableMonth pm;
		for(Month m: Month.values()){
			pm = new PrintableMonth();
			pm.setMonth(m);
			pm.setDayList(getDayList(year, m));
			months.add(pm);
		}
		setMonthList(months);
	}
	
	private List<PrintableDay> getDayList(Integer year, Month month){
		List<PrintableDay> list = new LinkedList<PrintableDay>();
		PrintableDay day;
		Calendar cal = new GregorianCalendar();
		cal.set(year, month.ordinal(), 1);
		// se agrega al principio de la lista dias vacios 
		// hasta llegar al dia de la semana que sea el dia de mes
		// Ej: si el dia 1 es miercoles, se agregan 2 dias vacios
		while(dayOfWeek(month.ordinal(), year).compareTo(list.size())>0){
			day = new PrintableDay();
			day.setDayOfMonth(null);
			list.add(day);
		}
		while(cal.get(Calendar.DAY_OF_MONTH) < monthDays(month.ordinal(), year)){
			day = new PrintableDay();
			day.setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
			addCalendarInfo(day, cal);
			list.add(day);
			cal.add(Calendar.DATE, 1);
		}
		day = new PrintableDay();
		day.setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
		addCalendarInfo(day, cal);
		list.add(day);
		return list;
	}
	
	private void addPeriodInfo(PrintableDay day, Calendar cal, CalendarPeriod period){
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean("calendar");
		com.esferalia.aon.calendar.Calendar c = controller.getTo();
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
			day.setHours(period.getMondayHours());
			setDayType(day,period.getMonday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY){
			day.setHours(period.getTuesdayHours());
			setDayType(day,period.getTuesday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY){
			day.setHours(period.getWednesdayHours());
			setDayType(day,period.getWednesday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY){
			day.setHours(period.getThursdayHours());
			setDayType(day,period.getThursday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
			day.setHours(period.getFridayHours());
			setDayType(day,period.getFriday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
			day.setHours(period.getSaturdayHours());
			setDayType(day,period.getSaturday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			day.setHours(period.getSundayHours());
			setDayType(day,period.getSunday());
		}
	}
//	private void setPeriodDayType(CalendarDay day, DayType type) {
//		if(type==DayType.NOT_WORKING_DAY){
//			day.setNotWorkingDay(true);
//		} else if(type==DayType.HOLIDAY){
//			day.setHoliday(true);
//		} else if(type==DayType.VACATION){
//			day.setVacation(true);
//		} else if(type==DayType.CONTINUOUS_TIME){
//			day.setContinuousTime(true);
//		} else if(type==DayType.OTHER){
//			day.setOther(true);
//		}
//	}
	private void addCalendarInfo(PrintableDay day, Calendar cal){
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean("calendar");
		com.esferalia.aon.calendar.Calendar c = controller.getTo();
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
			day.setHours(c.getMondayHours());
			setDayType(day,c.getMonday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY){
			day.setHours(c.getTuesdayHours());
			setDayType(day,c.getTuesday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY){
			day.setHours(c.getWednesdayHours());
			setDayType(day,c.getWednesday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY){
			day.setHours(c.getThursdayHours());
			setDayType(day,c.getThursday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
			day.setHours(c.getFridayHours());
			setDayType(day,c.getFriday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
			day.setHours(c.getSaturdayHours());
			setDayType(day,c.getSaturday());
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			day.setHours(c.getSundayHours());
			setDayType(day,c.getSunday());
		}
	}
	
	private void setDayType(PrintableDay day, DayType type) {
		if(type==DayType.NOT_WORKING_DAY){
			day.setNotWorkingDay(true);
		} else if(type==DayType.HOLIDAY){
			day.setHoliday(true);
		} else if(type==DayType.VACATION){
			day.setVacation(true);
		} else if(type==DayType.CONTINUOUS_TIME){
			day.setContinuousTime(true);
		} else if(type==DayType.OTHER){
			day.setOther(true);
		}
	}
	
	private Integer dayOfWeek(int month, Integer year) {
		Calendar cal = new GregorianCalendar();
		cal.set(year, month, 1);
		int num = cal.get(Calendar.DAY_OF_WEEK);
		if(num==Calendar.SUNDAY){
			return num+5;
		}
		return num-2;
	}
	
	private Integer monthDays(int month, Integer year) {
		Calendar cal = new GregorianCalendar();
		cal.set(year, month, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		int num = cal.get(Calendar.DAY_OF_MONTH);
		return num;
	}
		
	
}
