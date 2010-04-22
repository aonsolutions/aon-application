package com.code.aon.ui.menu;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.menu.jsf.IJSFMenu;

/**
 * Interface that represents any element of the menu that can have an action.
 * 
 * @author Consulting & Development. Aimar Tellitu - 03-oct-2005
 * @since 1.0
 *  
 */
public interface IActionListener {

	/**
	 * Gets the action of the Managed BEan.
	 * 
	 * @return the action of the Managed Bean
	 */
	String getAction();
	
	/**
	 * Sets the action of the Managed Bean.
	 * 
	 * @param action the action of the Managed Bean
	 */
	void setAction(String action);
	
    /**
     * Gets the full name of the class that implements {@link javax.faces.event.ActionListener}.
     * 
     * @return The name of the class.
     */
	String getType();
	
    /**
     * Sets the full name of the class that implements {@link javax.faces.event.ActionListener}.
     * 
     * @param type
     *            The name of the class.
     */
	void setType(String type);
	
    /**
     * Gets the linked {@link IMenuItem}.
     * 
     * @return The linked {@link IMenuItem}.
     */
	IMenuItem getMenuItem();

    /**
     * Sets the linked {@link IMenuItem}.
     * 
     * @param menuItem
     *            The linked {@link IMenuItem}.
     */
    void setMenuItem(IMenuItem menuItem);
    
    /**
     * Executes the definied action or type defined.
     * 
     * @param event
     *            {@link ActionEvent} for the current request.
     * @param menu
     *            {@link IJSFMenu} for the current request.
     */
	void execute( ActionEvent event, IJSFMenu menu );
	
    /**
     * Call the visit method of <code>visitor</code> for this object.
     * 
     * @param visitor
     *            An object that implements {@link IMenuVisitor}.
     * @throws MenuVisitorException
     *             if an unexpected error occurs.
     */
    void visit(IMenuVisitor visitor) throws MenuVisitorException;
	
}