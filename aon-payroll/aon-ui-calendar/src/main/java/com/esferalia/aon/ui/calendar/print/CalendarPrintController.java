package com.esferalia.aon.ui.calendar.print;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.calendar.controller.CalendarController;

public class CalendarPrintController implements Serializable, ICollectionProvider {
	
	private static final long serialVersionUID = 4417895326456828498L;

	private List<PrintableCalendar> list;
	private PrintableCalendar printableCalendar;
	
	public List<PrintableCalendar> getList() {
		return list;
	}
	public void setList(List<PrintableCalendar> list) {
		this.list = list;
	}

	public PrintableCalendar getPrintableCalendar() {
		return printableCalendar;
	}
	public void setPrintableCalendar(PrintableCalendar printableCalendar) {
		this.printableCalendar = printableCalendar;
	}
	
	
	
	
	
	
	

	public void loadList() throws ManagerBeanException {
		setList(new LinkedList<PrintableCalendar>());
		getList().add(getPrintableCalendar());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		try {
			loadList();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return getList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
	
	/*
	 * ACTION LISTENERS
	 */
	public void load(ActionEvent event){
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean("calendar"); 
		setPrintableCalendar(new PrintableCalendar());
		getPrintableCalendar().setEnterprise(controller.getEnterprise().getRegistry().getFullName());
		if(controller.getWorkPlace().getId()!=null){
			getPrintableCalendar().setWorkPlace(controller.getWorkPlace().getDescription());
		}
		if(controller.getContract().getId()!=null){
			getPrintableCalendar().setPerson(controller.getContract().getPerson().getFullName());
		}
		getPrintableCalendar().setCalendar(controller.getTo());
		getPrintableCalendar().setYear(controller.getYear());
		getPrintableCalendar().setCalendarFactory(new CalendarFactory());
		getPrintableCalendar().getCalendarFactory().setMonthList(getMonthList(controller.getYear()));
		getPrintableCalendar().getCalendarFactory().loadHolidays();
	}
	
	
	private List<PrintableMonth> getMonthList(Integer year) {
		LinkedList<PrintableMonth> months = new LinkedList<PrintableMonth>();
		PrintableMonth pm;
		Month[] m = Month.values();
		for (int i = 0; i < m.length; i++) {
			Month month = m[i];
			pm = new PrintableMonth();
			pm.setMonth(month);
			pm.setDayList(getDayList(year, month));
			months.add(pm);
		}
		return months;
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
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				day.setWeekEnd(true);
			}
			list.add(day);
			cal.add(Calendar.DATE, 1);
		}
		day = new PrintableDay();
		day.setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			day.setWeekEnd(true);
		}
		list.add(day);
		return list;
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
