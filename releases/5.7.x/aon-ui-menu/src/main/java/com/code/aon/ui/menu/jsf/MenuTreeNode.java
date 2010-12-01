package com.code.aon.ui.menu.jsf;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.code.aon.ui.menu.IMenuItem;
import com.code.aon.ui.menu.IOption;

/**
 * Extension of the {@link TreeNodeBase} class needed to store
 * the reference to the equivalent {@link IMenuItem} and a
 * large description.
 */
public class MenuTreeNode extends TreeNodeBase {
	
	private IMenuItem menuItem;
	private String largeDescription;

	/**
	 * The same constructor of {@link TreeNodeBase} plus 
	 * the reference of the {@link IMenuItem} and a large description of the menu item.
	 * 
	 * @param type the type name of the node
	 * @param description the description
	 * @param leaf specify whether or not is a leaf.
	 * @param identifier the identifier of the node.
	 * @param menuItem the menu item
	 * @param largeDescription the large description
	 */
	public MenuTreeNode(IMenuItem menuItem, String type, String description,String largeDescription,
			String identifier, boolean leaf) {
		super(type, description, identifier, leaf);
		this.menuItem = menuItem;
		this.largeDescription = largeDescription;
	}

	/**
	 * Gets the {@link IMenuItem} associated in the menu model.
	 * 
	 * @return the {@link IMenuItem} associated in the menu model
	 */
	public IMenuItem getMenuItem() {
		return menuItem;
	}

	/**
	 * Sets the {@link IMenuItem} associated in the menu model
	 * 
	 * @param menuItem the {@link IMenuItem} associated in the menu model
	 */
	public void setMenuItem(IMenuItem menuItem) {
		this.menuItem = menuItem;
	}
	
	/**
	 * Gets the large description of the node.
	 * 
	 * @return the large description of the node
	 */
	public String getLargeDescription() {
		return largeDescription;
	}
	
	/**
     * Indicates whether this node is an Hyperview.
     * 
     * @return <code>true</code> if so; <code>false</code> otherwise.
	 */
	public boolean isHyperView() {
		String ref = this.getMenuItem().getReference(); 
		return (ref != null && ref.contains("hyperview")); 
	}

	/**
	 * Gets the current option's attribute value.
	 * 
	 * @return the current option's attribute value
	 */
	public String getOptionValue() {
		IOption option = (IOption) getMenuItem();		
		String value = option.getReference();
        if ( UIComponentTag.isValueReference(value) ) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ValueBinding vb = facesContext.getApplication().createValueBinding(value);
            value = (String) vb.getValue(facesContext);
        }
		if ( option.isContextRelative() ) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            value = facesContext.getExternalContext().getRequestContextPath() + value;
		}
		return value;
	}
	
}