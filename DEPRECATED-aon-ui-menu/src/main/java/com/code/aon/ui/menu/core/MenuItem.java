package com.code.aon.ui.menu.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.code.aon.ui.menu.IActionListener;
import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuItem;

/**
 * Basic implementation of any element of the menu. 
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-mar-2005
 * @since 1.0
 * 
 */
public abstract class MenuItem implements IMenuItem {

	/**
	 * Identificador único del elemento.
	 */
	private String id;

	/**
	 * Clave del fichero de internacionalización par obtener el label del
	 * elemento.
	 */
	private String key;

	/**
	 * Elemento padre.
	 */
	private IMenuItem parent;

	/**
	 * Referencia externa o hipervínculo.
	 */
	private String reference;

	/**
	 * Clave del fichero de internacionalización par obtener la descripción del
	 * elemento.
	 */
	private String descriptionKey;

	/**
	 * Icono para la vista del menú.
	 */
	private String icon;

	/**
	 * Mapa con los roles de seguridad.
	 */
	private Set<String> roles;

	private List<IActionListener> actionListeners;
	
	private String visible;

	/**
     * Constructs a new menu item.
	 */
	public MenuItem() {
		this.roles = Collections.<String> emptySet();
		this.actionListeners = Collections.<IActionListener> emptyList();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public String getDescriptionKey() {
		return descriptionKey;
	}

	public String getIcon() {
		return icon;
	}

	/**
	 * Sets the icon path of the element.
	 * 
	 * @param icon the icon path of the element.
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public IMenuItem getParent() {
		return parent;
	}

	public void setParent(IMenu parent) {
		this.parent = parent;
	}

	public Iterator getMenuPath() {
		IMenuItem mi = this;
		ArrayList<IMenuItem> list = new ArrayList<IMenuItem>();
		list.add(mi);
		while (mi.getParent() != null) {
			mi = mi.getParent();
			list.add(mi);
		}
		if (list == null) {
			return null;
		}
		Collections.reverse(list);
		return list.iterator();
	}

	/**
	 * Sets the key in the resource bundle to obtain the label of this element.
	 * 
	 * @param key
	 * 			the key in the resource bundle to obtain the label of this element.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Sets the key in the resource bundle to obtain the description
	 * of this element. 
	 * 
	 * @param descriptionkey
	 * 			the key in the resource bundle to obtain the description
	 * 			of this element. 
	 */
	public void setDescriptionKey(String descriptionkey) {
		this.descriptionKey = descriptionkey;
	}

	public String getReference() {
		return reference;
	}

	/**
	 * Sets an {@link String} with the action to executed.
	 * 
	 * @param reference
	 * 			an {@link String} with the action to executed.
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	public void setRole(String roles) {
		StringTokenizer st = new StringTokenizer(roles, ", ");
		if (st.hasMoreTokens()) {
			this.roles = new HashSet<String>();
			while (st.hasMoreTokens()) {
				this.roles.add(st.nextToken());
			}
		}
	}

	public Set<String> getRoles() {
		return this.roles;
	}

	public Iterator<IActionListener> actionListeners() {
		return this.actionListeners.iterator();
	}

	public void addActionListener(IActionListener actionListener) {
		if (this.actionListeners.isEmpty()) {
			this.actionListeners = new LinkedList<IActionListener>();
		}
		this.actionListeners.add(actionListener);
		actionListener.setMenuItem(this);
	}

	public String getVisible() {
		return this.visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}
	
}