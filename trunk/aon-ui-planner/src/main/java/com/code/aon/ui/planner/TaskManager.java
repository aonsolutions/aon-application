/*
 * Created on 21-nov-2005
 *
 */
package com.code.aon.ui.planner;

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.planner.ITask;
import com.code.aon.planner.enumeration.Priority;
import com.code.aon.planner.enumeration.TaskStatus;

public class TaskManager {

    /** Se obtiene el Logger adecuado */
    private static final Logger LOGGER = Logger.getLogger(TaskManager.class.getName());

    /** Tarea */
    private ITask task;
    /** Indica el modo en el que se encuentra el Bean. Edición ó Nuevo */
    private boolean isNew = true;
    /** */
    private IPlannerCallbackHandler cb;

    public void initialize(ITask task, IPlannerCallbackHandler cb) {
    	this.cb = cb;
    	this.task = task;
    }

    /**
     * @return Returns the task.
     */
    public ITask getTask() {
        return task;
    }

    /**
     * @param event The task to set.
     */
    public void setTask(ITask task) {
        this.task = task;
    }

	/**
     * Comprueba si el evento esta en modo actualización o inserción.
     * 
     * @return
     */
    public boolean isNew() {
    	return isNew;
    }

    /**
	 * @param isNew The isNew to set.
	 */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

//	/**
//     * Indica si se trata de una tarea que tiene una notificacion.
//     * 
//     * @return
//     */
//    public boolean isAdvisable() {
//        return (this.task != null && this.task.getAlarmDate() != null); 
//    }
//
//    /**
//     * 
//     * @return
//     */
//    public void setAdvisable(boolean advise) {
//         
//    }
//
//    /**
//     * 
//     * @param e the action event
//     */
//    public void assign(ActionEvent e) {
//        
//    }

    /**
     * Devuelve una lista con los estados.
     * 
     * @return
     */
    public List getTaskStatus() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        List c = TaskStatus.getList();
        Iterator iter = c.iterator();
        while (iter.hasNext()){
            TaskStatus type = (TaskStatus) iter.next();
            SelectItem item = new SelectItem(type, type.getName(locale));
            types.add( item );
        }
        return types;
    }

    /**
     * Devuelve una lista con las prioridades.
     * 
     * @return
     */
    public List getPriorities() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        List c = Priority.getList();
        Iterator iter = c.iterator();
        while (iter.hasNext()){
            Priority type = (Priority) iter.next();
            SelectItem item = new SelectItem(type, type.getName(locale));
            types.add( item );
        }
        return types;
    }

    /**
     * Actualiza el evento en la agenda.
     * 
     * @param e the action event
     */
    public void accept(ActionEvent e) throws ParseException {
        if ( this.cb != null ) {
            if (isNew()) {
//            	this.cb.add(this.task);
                isNew = false;
            } else {
//            	this.cb.update(this.task);
            }
        } else {
            LOGGER.severe( "Planner Callback Handler is not defined." );            
        }
    }

    /**
     * Elimina el evento en la agenda.
     * 
     * @param e the action event
     */
    public void remove(ActionEvent e) throws ParseException {
        if ( this.cb != null ) {
//           	this.cb.remove(this.task); 
        } else {
            LOGGER.severe( "Planner Callback Handler is not defined." );            
        }
    }

	public String onCancel() {
		return this.cb.getOutcome();
	}

}
