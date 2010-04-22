package com.code.aon.ui.menu.core;

import com.code.aon.ui.menu.IMenuVisitor;
import com.code.aon.ui.menu.IOption;
import com.code.aon.ui.menu.MenuVisitorException;

/**
 * Implementation of {@link com.code.aon.ui.menu.IOption}. It represents an
 * executable option. 
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-mar-2005
 * @since 1.0
 */
public class Option extends MenuItem implements IOption {

	/**
	 * <code>True</code> si el link es externo a la aplicación,
	 * <code>false</code> de otra manera.
	 */
	private boolean external;

	/**
	 * Target donde se mostrará la página vinculada, similar al atributo
	 * <code>target</code> del elemento <code>A</code> de HTML.
	 */
	private String target;

	/**
	 * <code>True</code> si el link es relativo al contexto,
	 * <code>false</code> de otra manera.
	 */
	private boolean contextRelative;

	public void visit(IMenuVisitor visitor) throws MenuVisitorException {
		visitor.visitOption(this);
	}

	public boolean isExternal() {
		return external;
	}

	/**
	 * Specify whether or not the link is external to the application.
	 * 
	 * @param external
	 * 			when <code>true</code>, the link is external to the application.
	 */
	public void setExternal(boolean external) {
		this.external = external;
	}

	public boolean isContextRelative() {
		return contextRelative;
	}

	/**
	 * Specify whether or not the link is context relative.
	 * 
	 * @param contextRelative
	 * 			when <code>true</code>, the link is context relative.
	 */
	public void setContextRelative(boolean contextRelative) {
		this.contextRelative = contextRelative;
	}
	
	public String getTarget() {
		return target;
	}

	/**
	 * Sets the target where it will be shown the linked paged.
	 * 
	 * @param target
	 *		Target where it will be shown the linked paged.
	 */
	public void setTarget(String target) {
		this.target = target;
	}

}