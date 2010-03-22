package com.code.aon.ui.menu.jsf;

import java.util.Locale;

import org.apache.myfaces.custom.tree2.TreeNode;

import com.code.aon.ui.menu.IMenuItem;
import com.code.aon.ui.menu.IOption;
import com.code.aon.ui.menu.MenuBeanException;

/**
 * A generic base interface for Managed Beans that will hold the context of the menu.
 */
public interface IMenuModel {
	
	/**
	 * Initialize the model.
	 * @param locale 
	 *			<code>java.util.Lcale</code> that will be used to load menu labels. 
	 * @throws MenuBeanException
	 * 			if an unexpected error occurs.
	 */
	void init( Locale locale ) throws MenuBeanException;

	/**
	 * Add the specified {@link MenuListener} to the set of listeners
	 * registered to receive event notifications from this {@link IMenuModel}. 
	 * 
	 * @param menuListener The {@link MenuListener} to be registered.
	 */
	void addMenuListener(MenuListener menuListener);

	/**
	 * Remove the specified {@link MenuListener} from the set of listeners
	 * registered to receive event notifications from this {@link IMenuModel}.
	 * 
	 * @param menuListener The {@link MenuListener} to be deregistered.
	 */
	void removeMenuListener(MenuListener menuListener);
	
	/**
	 * Gets the selected node id.
	 * 
	 * @return the selected node id
	 */
	String getSelectedNodeId();

	/**
	 * Gets the selected node.
	 * 
	 * @return the selected node
	 */
	TreeNode getSelectedNode();
	
	/**
	 * Sets the selected node by its identifier.
	 * 
	 * @param id the identifier of the selected node.
	 */
	void setSelectedNode(String id);
	
	/**
	 * Gets the root node of the menu.
	 * 
	 * @return the root node of the menu
	 */
	TreeNode getRoot();
	
	/**
	 * Gets the tree node specified by <code>identifier</code>.
	 * 
	 * @param identifier the identifier
	 * 
	 * @return the tree node
	 */
	TreeNode getTreeNode(String identifier);
	
	/**
	 * Gets the menu item.
	 * 
	 * @param identifier the identifier
	 * 
	 * @return the menu item
	 */
	Object getMenuItem(String identifier);

	/**
	 * Gets the menu id.
	 * 
	 * @return the menu id
	 */
	String getMenuId();
	
	/**
	 * @param key
	 * @return IMenuItem
	 */
	IMenuItem getMenuItemByKey(String key);

	/**
	 * @param key
	 * @return IOption
	 */
	IOption getOptionByKey(String key);
	
}
