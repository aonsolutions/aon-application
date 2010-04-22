package com.code.aon.ui.menu.jsf;

/**
 * An {@link MenuChangeEvent} represents the change of the current menu.
 */
public class MenuChangeEvent {

	private IMenuModel oldMenu;
	
	private IMenuModel newMenu;
	
	/**
	 * Construct a new event object from the specified <code>menu</code>
	 * and action command.
	 * 
	 * @param newMenu the new menu
	 * @param oldMenu the old menu
	 */
	public MenuChangeEvent( IMenuModel oldMenu, IMenuModel newMenu ) {
		this.oldMenu = oldMenu;
		this.newMenu = newMenu;
	}

	/**
	 * Gets the new menu.
	 * 
	 * @return the new menu
	 */
	public IMenuModel getNewMenu() {
		return newMenu;
	}

	/**
	 * Gets the old menu.
	 * 
	 * @return the old menu
	 */
	public IMenuModel getOldMenu() {
		return oldMenu;
	}


}
