package com.code.aon.ui.menu;

/**
 * Interface that represents the leaf elements of the menu.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-mar-2005
 * @since 1.0
 * 
 */
public interface IOption extends IMenuItem {
	/**
	 * <code>True</code> if the link is external to the application,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>True</code> if the link is external to the application,
	 *         <code>false</code> otherwise.
	 */
	boolean isExternal();

	/**
	 * <code>True</code> if the link is context relative,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>True</code> if the link is context relative,
	 *         <code>false</code> otherwise.
	 */
	public boolean isContextRelative();
	
	/**
	 * Target where it will be shown the linked paged, similar to
	 * <code>target</code> attribute of the <code>A</code>  element of HTML.
	 * 
	 * @return Target where it will be shown the linked paged, similar to
	 *         <code>target</code> attribute of the <code>A</code>  element of HTML.
	 */
	String getTarget();

}