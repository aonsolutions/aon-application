/*
 * Created on 22-nov-2005
 *
 */
package com.code.aon.planner.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.code.aon.calendar.enumeration.EventCategory;

/**
 * Filtro con las categorías del evento.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 23-nov-2005
 * @since 1.0
 *
 */
public class EventCategoryFilter {

    /** Lista de las categorías validadas por el filtro */
    List<EventCategory> categories = new ArrayList<EventCategory>();

    /**
     * Devuelve verdadero si la categoría pasada por parámetro esta en la lista de categorías, 
     * falso en el resto de casos.
     * 
     * @param category
     * @return
     */
    public boolean accept(int category) {
    	if ( this.categories.size() > 0 ) {
	        Iterator iter = this.categories.iterator();
	        while (iter.hasNext()) {
	            EventCategory element = (EventCategory) iter.next();
	            if (element.ordinal() == category)
	                return true;
	        }
    	} else {
    		return true;
    	}
        return false;
    }

    /**
     * Añade una nueva categoría a la lista.
     * 
     * @param eventCategory
     */
    public void add(EventCategory eventCategory) {
        categories.add(eventCategory);
    }
}
