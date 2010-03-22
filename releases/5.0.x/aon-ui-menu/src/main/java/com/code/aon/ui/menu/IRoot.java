package com.code.aon.ui.menu;


/**
 * Interface that represents the root of the menu.
 * 
 * @author Consulting & Development. Aimar Tellitu - 09-sep-2005
 * @since 1.0
 *  
 */
public interface IRoot extends IMenu {
	
    /**
     * Indicates whether roles are defined.
     * 
     * @return <code>true</code> if so; <code>false</code> otherwise.
     */
    boolean isRolesDefined();

	/**
	 * Specify whether or not roles are defined. 
	 * 
	 * @param roles
	 *            when <code>true</code>, roles are defined.
	 */
    void setRolesDefined( boolean roles );

}