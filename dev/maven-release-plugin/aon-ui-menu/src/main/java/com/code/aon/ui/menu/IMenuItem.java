package com.code.aon.ui.menu;

import java.util.Iterator;
import java.util.Set;

/**
 * Interfce that represents any element in the menu tree model.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-mar-2005
 * @since 1.0
 * 
 */
public interface IMenuItem {

	/**
	 * Returns an unique identifier of this element.
	 * 
	 * @return an unique identifier of this element.
	 */
	String getId();

	/**
	 * Set the EL expression of the visible attribute.
	 * 
	 * @param visible
	 *			the EL expression.
	 */
	void setVisible(String visible);

	/**
	 * Return EL expression that will be evaluated as the visible attribute.
	 * 
	 * @return if the EL expression.
	 */
	String getVisible();

	/**
	 * Sets an unique identifier for this element. 
	 * 
	 * @param id
	 *			an unique identifier for this element.
	 */
	void setId(String id);
	
	/**
	 * Returns the key in the resource bundle to obtain the label of this element. 
	 * 
	 * @return the key in the resource bundle to obtain the label of this element.
	 */
	String getKey();

	/**
	 * Returns the key in the resource bundle to obtain the description
	 * of this element. 
	 * 
	 * @return
	 * 		the key in the resource bundle to obtain the description
	 *		of this element.
	 */
	String getDescriptionKey();

	/**
	 * Returns the icon path of the element.
	 * 
	 * @return the icon path of the element.
	 */
	String getIcon();

	/**
	 * Return the parent element or <code>null</code> if it is the root node.
	 * 
	 * @return the parent element.
	 */
	IMenuItem getParent();

	/**
	 * Sets the parent element.
	 * 
	 * @param menuItem
	 *			the parent element.
	 */
	void setParent(IMenu menuItem);

	/**
	 * Returns an {@link java.util.Iterator} object of {@link IMenuItem} that
	 * reprensets the path from this node to the root node.
	 * 
	 * @return an {@link java.util.Iterator} object of {@link IMenuItem}.
	 */
	Iterator getMenuPath();

	/**
	 * Returns an {@link String} with the action to executed.
	 * 
	 * @return an {@link String} with the action to executed.
	 */
	String getReference();

	/**
	 * Sets the security roles that are allowed for this element.
	 * 
	 * @param roles
	 * 			Comma-separated list of security roles.
	 */
	void setRole(String roles);

	/**
     * Returns a <code>Set</code> with the security roles that are allowed
     * in this element.
	 * 
	 * @return a <code>Set</code> with the security roles, it can be empty. 
	 */
	Set getRoles();

	/**
     * Appends <code>actionListener</code> to the end of the listener list. 
	 * 
	 * @param actionListener
	 *            element to be appended to this list.
	 */
	void addActionListener(IActionListener actionListener);

	/**
     * Returns an iterator over the listeners in this element. 
	 * 
	 * @return an iterator over the listeners in this menu.
	 */
	Iterator actionListeners();

	/**
	 * Method needed by this element to allow to be visited by an {@link IMenuVisitor}. 
	 * 
	 * @param visitor
	 *			an implementation of the {@link IMenuVisitor}.
	 * @throws MenuVisitorException
	 *             if an unexpected error occurs.
	 */
	void visit(IMenuVisitor visitor) throws MenuVisitorException;

}