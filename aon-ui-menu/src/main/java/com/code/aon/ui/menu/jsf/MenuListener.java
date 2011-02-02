package com.code.aon.ui.menu.jsf;

/**
 * The listener interface for receiving value changed events. The class that
 * is interested in processing an value changed event implements this interface,
 * and the object created with that class is registered with a component, using
 * the component's addMenuListener method. When the value change event occurs,
 * that object's valueChanged method is invoked.
 * 
 * @author Consulting & Development. Aimar Tellitu - 06-jun-2005
 * @since 1.0
 */
public interface MenuListener {

	/**
	 * This method gets called when the selected menu item in the menu is changed.
	 * 
	 * @param oldMenuItemId identifier of the previous selected menu item
	 * @param newMenuItemId identifier of the new selected menu item 
	 */
	void menuItemSelected( String oldMenuItemId, String newMenuItemId );
	
}
