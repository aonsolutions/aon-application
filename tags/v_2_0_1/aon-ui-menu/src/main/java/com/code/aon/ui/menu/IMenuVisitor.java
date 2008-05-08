package com.code.aon.ui.menu;

/**
 * Interface that implements the Visitor pattern for the menu tree model.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-mar-2005
 * @since 1.0
 *  
 */
public interface IMenuVisitor {
	
    /**
     * Visits an {@link IRoot} object that represents the root node of the menu.
     * 
     * @param root
     *			the root node to visit.			            
     * @throws MenuVisitorException
     *			if an unexpected error occurs.
     */
    void visitRoot(IRoot root) throws MenuVisitorException;

    /**
     * Visits an {@link IMenu} object.
     * 
     * @param menu
     *			the {@link IMenu} object to visit.
     * @throws MenuVisitorException
     *			if an unexpected error occurs.
     */
    void visitMenu(IMenu menu) throws MenuVisitorException;

    /**
     * Visits an {@link IOption} object.
     * 
     * @param option
     *			the {@link IOption} object to visit.
     * @throws MenuVisitorException
     *			if an unexpected error occurs.
     */
    void visitOption(IOption option) throws MenuVisitorException;

    /**
     * Visits an {@link IActionListener} object.
     * 
     * @param actionListener
     *			the {@link IActionListener} object to visit.
     * @throws MenuVisitorException
     *			if an unexpected error occurs.
     */
    void visitActionListener(IActionListener actionListener) throws MenuVisitorException;
}