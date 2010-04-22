package com.code.aon.planner;

import java.util.Iterator;

/**
 * El interface <code>ICompositeTask</code> provee una manera de obtener las
 * tareas que posee un objeto.
 * 
 * @author Consulting & Development. Jorge Diez - 24-feb-2005
 * @since 1.0
 *
 */
public interface ICompositeTask extends ITask {

    /**
     * Devuleve las tareas que posee el objeto.
     * 
     * @return Un <code>Iterator</code> de <code>AbstractTask</code>.
     */
    Iterator getTasks(); 
    
    /**
     * Añade la tarea inidicada a la lista de tareas a realizar por este
     * <code>ICompositeTask</code>.
     * 
     * @param task
     *            La tarea a añadir.
     */
    void addTask(ITask task);

}