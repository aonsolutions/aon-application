/*
 * Created on 21-nov-2005
 *
 */
package com.code.aon.ui.planner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.planner.EventException;
import com.code.aon.planner.IEvent;
import com.code.aon.planner.IOwner;
/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 27/12/2006
 * TODO
 *
 */

public class ReassignManager {

	public static final String MANAGER_BEAN_NAME = "reassign";

	/** */
	private DataModel mColumns;
    /** */
	private Map<RowColumnKey, Boolean> mValueMap = new HashMap<RowColumnKey, Boolean>();
    /** Lista de eventos a reasignar. */
	private DataModel events;
    /** Planificador. */
    private IPlannerCallbackHandler cb;
    /** Propietario del calendario al cual se le van a asignar los eventos seleccionados. */
    IOwner owner;
    /** Fecha y hora en la cual reasignar las citas. */
    Date date;

	/**
	 * @return Returns the events.
	 */
	public DataModel getEvents() {
		return events;
	}

	/**
	 * @param events The events to set.
	 */
	public void setEvents(DataModel events) {
		this.events = events;
	}

	/**
	 * @return Returns the owner.
	 */
	public IOwner getOwner() {
		return owner;
	}

	/**
	 * @param owner The owner to set.
	 */
	public void setOwner(IOwner owner) {
		this.owner = owner;
	}
    
	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	public void initialize(DataModel events, IPlannerCallbackHandler cb, IOwner owner) {
    	this.cb = cb;
		this.events = events;
		this.owner = owner;
		this.mValueMap = new HashMap<RowColumnKey, Boolean>();
	}

	public void execute(ActionEvent e) throws EventException {
//        MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "Se Necesita información acerca de los pasos a tener en cuenta en la Reasignación.", null);
//		FacesContext ctx = FacesContext.getCurrentInstance();
//		ValueBinding vb = ctx.getApplication().createValueBinding(PlannerConstants.PLANNER);
//        CalendarManagerBean cmb = (CalendarManagerBean)vb.getValue(ctx);
        AonCalendar aonCalendar = null;//TODO = cmb.getCalendar( this.owner.getCalendar().getId() );
		Iterator iter = ( (List)this.events.getWrappedData() ).iterator();
		while (iter.hasNext()) {
			IEvent event = (IEvent) iter.next();
			aonCalendar.getCalendar().getComponents().add(event.getComponent());
			this.cb.remove(event);
			this.cb.refresh(event);
		}
//	TODO	cmb.updateCalendar( this.owner.getCalendar(), aonCalendar );
	}

//	/**
//     * Añade la lista de eventos al calendario del trabajador seleccionado e informa al planificador
//     * de lo sucedido.
//     *  
//     * @param e the action event
//     * @throws ManagerBeanException 
//     * @throws ExpressionException 
//     */
//    public void update(ActionEvent e) {
//        FacesContext ctx = FacesContext.getCurrentInstance();
//        ValueBinding vb = ctx.getApplication().createValueBinding(PlannerConstants.CALENDAR_MANAGER);  
//        CalendarManagerBean wcc = (CalendarManagerBean) vb.getValue(ctx);
//        if (worker.getCalendar() != null) {
//	        AonCalendar calendar = wcc.getCalendar(worker.getCalendar().getId());
//	        calendar.setOwner(worker.getWorker().getId());
//	        calendar.getCalendar().getComponents().addAll(events);
//	        wcc.updateCalendar(calendar);
//	        IReassignListener listener = getListener();
//	        if ( listener != null ) {
//	            listener.update(this.events, this.date);
//	            this.worker = ServiceWorker.getInstance();
//	        }
//        } else {
//        	String doctor = worker.getWorker().getSurname() + " " + worker.getWorker().getSurname2() + ", " + worker.getWorker().getName() + " " + worker.getWorker().getName2();
//			ResourceBundle bundle = ResourceBundle.getBundle( Constants.MESSAGES_FILE, ctx.getViewRoot().getLocale() );
//			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, 
//									bundle.getString("aon_diary_not_found_error"), new String[] {doctor});
//		}
//	}

    /**
     * Cancela la edición del evento.
     * 
     * @return
     */
	public String onCancel() {
		return this.cb.getOutcome();
	}

	public DataModel getColumnDataModel() {
		if (mColumns == null) {
            String[] result = new String[] {"all"};
			mColumns = new ListDataModel( new ArrayList<String>( Arrays.asList(result) ) );
		}
		return mColumns;
	}

	public void setColumnValue(Boolean value) {
		if (this.events.isRowAvailable()) {
			IEvent row = (IEvent) this.events.getRowData();
			DataModel columnDataModel = getColumnDataModel();
			if (columnDataModel.isRowAvailable()) {
				Object column = columnDataModel.getRowData();
                RowColumnKey key = new RowColumnKey(row.getId(), column);
				mValueMap.put(key, value);
			}
		}
	}

	public Boolean getColumnValue() {
		if (this.events.isRowAvailable()) {
			IEvent row = (IEvent) this.events.getRowData();
			DataModel columnDataModel = getColumnDataModel();
			if (columnDataModel.isRowAvailable()) {
				Object column = columnDataModel.getRowData();
                RowColumnKey key = new RowColumnKey(row.getId(), column);
				if (!mValueMap.containsKey(key)) {
					mValueMap.put(key, new Boolean(false));
				}
				return (Boolean) mValueMap.get(key);
			}
		}
		return new Boolean(false);
	}

	private class RowColumnKey {

		private final Object mRow;
		private final Object mColumn;

		/**
		 * @param row
		 * @param column
		 */
		public RowColumnKey(Object row, Object column) {
			mRow = row;
			mColumn = column;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj instanceof RowColumnKey) {
				RowColumnKey other = (RowColumnKey) obj;
				return other.mRow.equals(mRow) && other.mColumn.equals(mColumn);
			}
			return super.equals(obj);
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return (37 * 3 + mRow.hashCode()) * (37 * 3 + mColumn.hashCode());
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return mRow.toString() + "," + mColumn.toString();
		}
	}

}
