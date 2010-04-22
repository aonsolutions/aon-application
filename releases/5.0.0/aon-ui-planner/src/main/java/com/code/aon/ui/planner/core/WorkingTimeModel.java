package com.code.aon.ui.planner.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarUtil;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.planner.core.WorkingTime;

public class WorkingTimeModel {

    /** Tareas a realizar y creadas por el recurso seleccionado */
    private DataModel model;
    /** Indica el Horario de Trabajo seleccionado */
	private WorkingTime selected; 

	/**
	 * @return Returns the model.
	 */
	public DataModel getModel() {
		return model;
	}

	/**
	 * @param model The model to set.
	 */
	public void setModel(DataModel model) {
		this.model = model;
	}

	/**
	 * @return Returns the selected.
	 */
	public WorkingTime getSelected() {
		return selected;
	}

	/**
	 * @param selected The selected to set.
	 */
	public void setSelected(WorkingTime selected) {
		this.selected = selected;
	}

	/**
	 * Añade un nuevo Horario de Trabajo, y lo selecciona.
	 * 
	 * @param workingTime
	 */
	@SuppressWarnings("unchecked")
	public void add(WorkingTime workingTime) {
		if (model == null) {
			model = new ListDataModel(new ArrayList());
		}
		( (List)this.model.getWrappedData() ).add(workingTime);
	}

	/**
	 * Elimina el Horario de Trabajo seleccionado.
	 * 
	 * @return
	 */
	public WorkingTime remove() {
		List list = (List)this.model.getWrappedData();
		boolean removed = list.remove(this.selected);
		this.model.setWrappedData(list);
		return (removed)? this.selected: null;
	}

    /**
     * Carga los Horarios de Trabajo.
     * 
     * @return
     */
    public static final WorkingTimeModel getInstance(AonCalendar calendar, Date date) {
		WorkingTimeModel wtm = new WorkingTimeModel();
		ComponentList cl = calendar.getVEvents(EventCategory.WORK);
		DateTime startPeriod = CalendarUtil.getICalDateTime(date, true);
		Period period = new Period( startPeriod, new Dur(1,0,0,0) );
		PeriodList periods = calendar.getWorkingTime( period );
		Calendar _cal = Calendar.getInstance();
		_cal.setTime(date);
		while (periods.size() == 0) { 
			_cal.add(Calendar.DATE, -1);
			startPeriod = CalendarUtil.getICalDateTime(_cal.getTime(), true);
			period = new Period( startPeriod, new Dur(1,0,0,0) );
			periods = calendar.getWorkingTime( period );
		}
		Calendar cal = Calendar.getInstance();
		Iterator iter = periods.iterator();
		while (iter.hasNext()) {
			Period element = (Period) iter.next();
			WorkingTime wt = new WorkingTime();
			Date start = CalendarUtil.getDate(element.getStart());
			cal.setTime(start);
			wt.setShift(cal.get(Calendar.AM_PM));
			wt.setStart(cal.get(Calendar.HOUR_OF_DAY));
			Date end = CalendarUtil.getDate(element.getEnd());
			cal.setTime(end);
			wt.setEnd(cal.get(Calendar.HOUR_OF_DAY));
			Iterator hours = cl.iterator();
			while (hours.hasNext()) {
				Component c = (Component) hours.next();
				String dt = c.getProperties().getProperty(Property.DTSTART).getValue();
				dt = dt.substring(dt.indexOf('T'), dt.length());
				if (element.getStart().toString().indexOf(dt) > -1) {
					wt.setComponent(c);
					wt.setPeriod(element);
				}
			}
			wtm.add(wt);
		}
		wtm.setSelected((WorkingTime)wtm.getModel().getRowData());
		return wtm;
    }

}
