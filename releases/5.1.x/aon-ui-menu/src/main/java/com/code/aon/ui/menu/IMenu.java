package com.code.aon.ui.menu;

import java.util.List;

/**
 * The interface that defines a container of {@link IMenuItem}.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-mar-2005
 * @since 1.0
 *  
 */
public interface IMenu extends IMenuItem {
	
    /**
     * Appends <code>item</code> to the end of the element list of the menu.
     * 
     * @param item
     *            element to be appended to this list.
     */
    void addItem(IMenuItem item);

    /**
     * Returns an iterator over the elements in this menu.
     * 
     * @return the elements in this menu. 
     */
    List<IMenuItem> getItems();

    /**
     * Returns the number of children in the menu.
     * 
     * @return the number of children in the menu.
     */
    int getChildCount();

    /**
     * Searchs an element in the menu structure by the element identifier. 
     * 
     * @param id
     * 			identifier of the element that is found.
     * @return 
     * 		the found element in this list; returns null if the object is not found.
     */
    IMenuItem find(String id);
    
    /**
     * Searchs an element in the menu structure by the element key. 
     * 
     * @param key
     * 			key of the element that is found.
     * @return 
     * 		the found element in this list; returns null if the object is not found.
     */
    IMenuItem findByKey(String key);

    /**
     * Searchs an element in the menu structure by the element key. 
     * 
     * @param key
     * 			key of the element that is found.
     * @return 
     * 		the found element in this list; returns null if the object is not found.
     */
    IOption findOptionByKey(String key);
    
}