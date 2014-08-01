package com.esferalia.aon.ui.calendar.print;

import static com.esferalia.aon.ui.calendar.controller.ICalendarConstants.CALENDAR_CONTROLLER_NAME;
import static com.esferalia.aon.ui.calendar.controller.ICalendarConstants.CALENDAR_HOLIDAY_CONTROLLER_NAME;
import static com.esferalia.aon.ui.calendar.controller.ICalendarConstants.CALENDAR_HOLIDAY_DATA_CONTROLLER_NAME;
import static com.esferalia.aon.ui.calendar.controller.ICalendarConstants.CALENDAR_PERIOD_CONTROLLER_NAME;



import java.io.Serializable;
//import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.CalendarHoliday;
import com.esferalia.aon.calendar.CalendarPeriod;
import com.esferalia.aon.calendar.HolidayDetail;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.ui.calendar.controller.CalendarController;
import com.esferalia.aon.ui.calendar.controller.CalendarHolidayDataController;

public class CalendarFactory implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarFactory.class.getName());
	
	private List<CalendarDay> dayList;
	private List<CalPeriod> periodList;
	private List<PrintableMonth> monthList;
	
	public List<CalendarDay> getDayList() {
		return dayList;
	}
	public void setDayList(List<CalendarDay> dayList) {
		this.dayList = dayList;
	}
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
		this.loadInheritDays();
		this.loadCalendarDays();
		buildMonthList();
		this.loadInheritPeriodDays();
		this.loadPeriodDays();
		this.asignDays(dayList);
	}
	
	@SuppressWarnings("unchecked")
	private void loadPeriodDays() {
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(CALENDAR_CONTROLLER_NAME);
		try {
			List<ITransferObject> list = (List<ITransferObject>) FormUtil.getController(CALENDAR_PERIOD_CONTROLLER_NAME).getModel().getWrappedData();
			CalPeriod period;
			for(ITransferObject to: list){
				CalendarPeriod p = (CalendarPeriod) to;
				period = new CalPeriod();
				period.setDescription(p.getDescription());
				period.setMonth(p.getMonth());
				period.setStartDay(p.getStartDay());
				period.setEndDay(p.getEndDay());
				periodList.add(period);
				java.util.Calendar cal = new GregorianCalendar();
				int d = p.getStartDay();
				while(d <= p.getEndDay()){
					cal.set(controller.getYear(), p.getMonth().ordinal(), d);
					asignPeriodDay(p, cal);
					d++;
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("ERROR in loadPeriodDays");
		}
	}

	private void loadInheritPeriodDays(){
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(CALENDAR_CONTROLLER_NAME);
		periodList = new LinkedList<CalPeriod>();
		CalPeriod period;
		for(ITransferObject to: controller.getInheritPeriods()){
			CalendarPeriod p = (CalendarPeriod) to;
			period = new CalPeriod();
			period.setDescription(p.getDescription());
			period.setMonth(p.getMonth());
			period.setStartDay(p.getStartDay());
			period.setEndDay(p.getEndDay());
			periodList.add(period);
			java.util.Calendar cal = new GregorianCalendar();
			int d = p.getStartDay();
			while(d <= p.getEndDay()){
				cal.set(controller.getYear(), p.getMonth().ordinal(), d);
				asignPeriodDay(p, cal);
				d++;
			}
		}
	}
	
	private void loadHolidays(){
		dayList = new LinkedList<CalendarDay>();
		CalendarHolidayDataController controller = (CalendarHolidayDataController) AonUtil.getRegisteredBean(CALENDAR_HOLIDAY_DATA_CONTROLLER_NAME);
		if(controller.getHolidays()!=null){
			for(String key: controller.getHolidays().keySet()){
				CalendarDay day = new CalendarDay();
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
	}
	
	private void loadInheritDays(){
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(CALENDAR_CONTROLLER_NAME);
		List<ITransferObject> list = controller.getInheritHolidays();
		if(!list.isEmpty()){
			CalendarDay day = new CalendarDay();
			Locale locale = AonUtil.getCurrentLocale();
			ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.calendar.i18n.messages", locale);	
			day.setDescription(bundle.getString("aon_enum_daytype_full_OTHER"));
			day.setDate(null);
			dayList.add(day);
			for(ITransferObject to: list){
				CalendarHoliday h = (CalendarHoliday) to;
				day = new CalendarDay();
				day.setDescription(h.getDescription());
				day.setDate(h.getDate());
				day.setHours(h.getHours());
				day.setType(h.getDayType());
				searchExistingDay(day);
				dayList.add(day);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadCalendarDays(){
		try {
			IController controller = (IController) AonUtil.getRegisteredBean(CALENDAR_HOLIDAY_CONTROLLER_NAME);
			List<ITransferObject> list = (List<ITransferObject>) controller.getModel().getWrappedData();
			CalendarDay day = new CalendarDay();
			for(ITransferObject to: list){
				CalendarHoliday h = (CalendarHoliday) to;
				day = new CalendarDay();
				day.setDescription(h.getDescription());
				day.setDate(h.getDate());
				day.setHours(h.getHours());
				day.setType(h.getDayType());
				searchExistingDay(day);
				dayList.add(day);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void searchExistingDay(CalendarDay day) {
		for(CalendarDay d: dayList){
			if(d.getDate()!=null && d.getDate().equals(day.getDate())){
				d.setOverwritten(true);
			}
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

	private void asignPeriodDay(CalendarPeriod period, java.util.Calendar calendar) {
		PrintableMonth month = getMonthList().get(calendar.get(java.util.Calendar.MONTH));
		for(PrintableDay day: month.getDayList()){
			if(day.getDayOfMonth()!=null){
				if(day.getDayOfMonth().equals(calendar.get(java.util.Calendar.DAY_OF_MONTH))){
					day.disableAllTypes();
					addPeriodInfo(day, calendar, period);
				}
			}
		}
	}
	
	private void buildMonthList() {
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(CALENDAR_CONTROLLER_NAME);
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
		java.util.Calendar cal = new GregorianCalendar();
		cal.set(year, month.ordinal(), 1);
		// se agrega al principio de la lista dias vacios 
		// hasta llegar al dia de la semana que sea el dia de mes
		// Ej: si el dia 1 es miercoles, se agregan 2 dias vacios
		while(dayOfWeek(month.ordinal(), year).compareTo(list.size())>0){
			day = new PrintableDay();
			day.setDayOfMonth(null);
			list.add(day);
		}
		while(cal.get(java.util.Calendar.DAY_OF_MONTH) < monthDays(month.ordinal(), year)){
			day = new PrintableDay();
			day.setDayOfMonth(cal.get(java.util.Calendar.DAY_OF_MONTH));
			addCalendarInfo(day, cal);
			list.add(day);
			cal.add(java.util.Calendar.DATE, 1);
		}
		day = new PrintableDay();
		day.setDayOfMonth(cal.get(java.util.Calendar.DAY_OF_MONTH));
		addCalendarInfo(day, cal);
		list.add(day);
		return list;
	}
	
	private void addPeriodInfo(PrintableDay day, java.util.Calendar cal, CalendarPeriod period){
		if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.MONDAY){
			day.setHours(period.getMondayHours());
			setDayType(day,period.getMonday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.TUESDAY){
			day.setHours(period.getTuesdayHours());
			setDayType(day,period.getTuesday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.WEDNESDAY){
			day.setHours(period.getWednesdayHours());
			setDayType(day,period.getWednesday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.THURSDAY){
			day.setHours(period.getThursdayHours());
			setDayType(day,period.getThursday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.FRIDAY){
			day.setHours(period.getFridayHours());
			setDayType(day,period.getFriday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.SATURDAY){
			day.setHours(period.getSaturdayHours());
			setDayType(day,period.getSaturday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.SUNDAY){
			day.setHours(period.getSundayHours());
			setDayType(day,period.getSunday());
		}
	}

	private void addCalendarInfo(PrintableDay day, java.util.Calendar cal){
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(CALENDAR_CONTROLLER_NAME);
		Calendar c = (Calendar) controller.getTo();
		if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.MONDAY){
			day.setHours(c.getMondayHours());
			setDayType(day,c.getMonday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.TUESDAY){
			day.setHours(c.getTuesdayHours());
			setDayType(day,c.getTuesday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.WEDNESDAY){
			day.setHours(c.getWednesdayHours());
			setDayType(day,c.getWednesday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.THURSDAY){
			day.setHours(c.getThursdayHours());
			setDayType(day,c.getThursday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.FRIDAY){
			day.setHours(c.getFridayHours());
			setDayType(day,c.getFriday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.SATURDAY){
			day.setHours(c.getSaturdayHours());
			setDayType(day,c.getSaturday());
		} else if(cal.get(java.util.Calendar.DAY_OF_WEEK)==java.util.Calendar.SUNDAY){
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
		java.util.Calendar cal = new GregorianCalendar();
		cal.set(year, month, 1);
		int num = cal.get(java.util.Calendar.DAY_OF_WEEK);
		if(num==java.util.Calendar.SUNDAY){
			return num+5;
		}
		return num-2;
	}
	
	private Integer monthDays(int month, Integer year) {
		java.util.Calendar cal = new GregorianCalendar();
		cal.set(year, month, 1);
		cal.add(java.util.Calendar.MONTH, 1);
		cal.add(java.util.Calendar.DATE, -1);
		int num = cal.get(java.util.Calendar.DAY_OF_MONTH);
		return num;
	}
		
	
}
