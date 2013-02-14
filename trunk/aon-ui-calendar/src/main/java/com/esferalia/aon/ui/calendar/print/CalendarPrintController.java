package com.esferalia.aon.ui.calendar.print;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.ui.calendar.controller.CalendarController;
import com.esferalia.aon.ui.calendar.controller.ICalendarConstants;

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
	public void onLoad(ActionEvent event){
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(ICalendarConstants.CALENDAR_CONTROLLER_NAME); 
		setPrintableCalendar(new PrintableCalendar());
		if(controller.getEnterpriseName()!=null){
			getPrintableCalendar().setEnterprise(controller.getEnterpriseName());
		}
		if(controller.getWorkPlaceName()!=null){
			getPrintableCalendar().setWorkPlace(controller.getWorkPlaceName());
		}
		if(controller.getContractName()!=null){
			getPrintableCalendar().setPerson(controller.getContractName());
		}
		getPrintableCalendar().setCalendar((Calendar) controller.getTo());
		getPrintableCalendar().setYear(controller.getYear());
		getPrintableCalendar().getCalendarFactory().buildCalendar();
	}
	
	
}
