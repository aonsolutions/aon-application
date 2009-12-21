/*
 * Created on 18-nov-2005
 *
 */
package com.code.aon.planner;

import net.fortuna.ical4j.model.Recur;

public interface INodeVisitor {

    /**
     * Visita el evento.
     * 
     * @param event
     */
    void visitEvent(IEvent event);

    /**
     * Visita la periodicidad del evento.
     * 
     * @param recur
     */
    void visitRecur(Recur recur);

    /**
     * Visita la tarea.
     * 
     * @param task
     */
    void visitTask(ITask task);

}
