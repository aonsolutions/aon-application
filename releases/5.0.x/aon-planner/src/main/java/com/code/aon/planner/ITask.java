/*
 * Created on 18-nov-2005
 *
 */
package com.code.aon.planner;

import java.util.Date;
import java.util.Set;

import com.code.aon.common.ITransferObject;
import com.code.aon.planner.enumeration.Priority;
import com.code.aon.planner.enumeration.TaskStatus;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18-nov-2005
 * @since 1.0
 */
public interface ITask extends IAction, ITransferObject {

    /**
     * Retorna el identificador de la tarea.
     * 
     * @return String
     */
    String getId();

    /**
     * @return Returns the title.
     */
    String getTitle();

    /**
     * Retorna la fecha de creación de la tarea.
     * 
     * @return La fecha de creación de la tarea.
     */
    Date getStartDate();

    /**
     * La fecha de vencimiento.
     * 
     * @return La fecha fin o de vencimiento de la tarea.
     */
    Date getDueDate();

    /**
     * @return Returns the priority.
     */
    Priority getPriority();

    /**
     * Retorna el estado en el que se encuentra el evento.
     * 
     * @return El estado de la tarea.
     */
    TaskStatus getStatus();

    /**
     * @return Returns the percent.
     */
    int getPercent();

    /**
     * Devuelve los recursos asociados a esta tarea.
     * 
     * @return Devuelve los recursos.
     */
    Set getResources();

    /**
     * @return Returns the alarmDate.
     */
    Date getAlarmDate();

    /**
     * Permite visitar la interfaz.
     * 
     * @param visitor
     */
    void visit(INodeVisitor visitor);

}