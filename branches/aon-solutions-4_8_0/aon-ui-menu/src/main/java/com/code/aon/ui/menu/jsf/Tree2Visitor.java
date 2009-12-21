package com.code.aon.ui.menu.jsf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.custom.tree2.TreeNode;

import com.code.aon.ui.menu.IActionListener;
import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuConstants;
import com.code.aon.ui.menu.IMenuItem;
import com.code.aon.ui.menu.IMenuVisitor;
import com.code.aon.ui.menu.IOption;
import com.code.aon.ui.menu.IRoot;
import com.code.aon.ui.menu.MenuVisitorException;

/**
 * Class that walks throught the menu structure to build a tree of {@link TreeNode} 
 * for Tree2 component of Tomahawk.
 * 
 * @author Consulting & Development. Aimar Tellitu - 26-ago-2005
 * @since 1.0
 */
public class Tree2Visitor implements IMenuVisitor, IMenuConstants {

	private TreeNode root;

	private TreeNode currentParent;

	private Map<String, TreeNode> nodeMap;

	private ExternalContext externalContext;

	private boolean omitRoles;

	/**
	 * Message file.
	 */
	private ResourceBundle bundle;

	/**
	 * Constructor of this class specifying the basic needed arguments. 
	 * 
	 * @param security specify whether or not security roles are checked.
	 * @param externalContext the external context of current session.
	 * @param bundle the {@link ResourceBundle} that will be used for retreiving the menu labels.
	 */
	public Tree2Visitor(ResourceBundle bundle, ExternalContext externalContext, boolean security) {
		this.bundle = bundle;
		this.externalContext = externalContext;
		this.nodeMap = new HashMap<String, TreeNode>();
		this.omitRoles = !security;
	}

	public void visitRoot(IRoot root) throws MenuVisitorException {
		this.omitRoles |= !root.isRolesDefined();
		this.root = getTreeNode(root, ROOT_TYPE, false);
		scanMenu(root, this.root);
	}

	@SuppressWarnings("unchecked")
	public void visitMenu(IMenu menu) throws MenuVisitorException {
		TreeNode node = getTreeNode(menu, MENU_TYPE, false);
		scanMenu(menu, node);
		if ( isInsertable(menu) || (node.getChildCount() > 0)) {
			this.currentParent.getChildren().add(node);
		}
	}

	@SuppressWarnings("unchecked")
	public void visitOption(IOption option) throws MenuVisitorException {
		TreeNode node = getTreeNode(option, OPTION_TYPE, true);
		if ( isInsertable(option) ) {
			this.currentParent.getChildren().add(node);
		}
	}

	public void visitActionListener(IActionListener menu) throws MenuVisitorException {
	}

	/**
	 * Visits all the children of <code>menu</code>.
	 * 
	 * @param menu
	 *			the {@link com.code.aon.ui.menu.IMenu} which children will be visited.
	 * @throws MenuVisitorException
	 *             if an unexpected error occurs.
	 */
	private void scanMenu(IMenu menu, TreeNode node) throws MenuVisitorException {
		TreeNode oldParent = this.currentParent;
		this.currentParent = node;
		for( IMenuItem menuItem : menu.getItems() ) {			
			menuItem.visit(this);
		}
		this.currentParent = oldParent;
	}

	/**
	 * Gets the node map.
	 * 
	 * @return the node map
	 */
	public Map<String, TreeNode> getNodeMap() {
		return this.nodeMap;
	}

	/**
	 * Gets the root.
	 * 
	 * @return the root
	 */
	public TreeNode getRoot() {
		return this.root;
	}

	private TreeNode getTreeNode(IMenuItem menuItem, String type, boolean leaf) {
		TreeNode node = new MenuTreeNode(menuItem, type, getLabel(menuItem), getDescription(menuItem),
				menuItem.getId(), leaf);
		this.nodeMap.put(node.getIdentifier(), node);
		return node;
	}

	private String getLabel(IMenuItem menuItem) {
		return bundle.getString(menuItem.getKey());
	}

	private String getDescription(IMenuItem menuItem) {
		return menuItem.getDescriptionKey() == null ? null : bundle.getString(menuItem.getDescriptionKey());
	}

	private boolean isAllowed(IMenuItem item) {
		if ((item != null)) {
			if (item.getRoles().isEmpty()) {
				if (item.getParent() == null) {
					return true;
				}
				return isAllowed(item.getParent());
			}
			Iterator i = item.getRoles().iterator();
			while (i.hasNext()) {
				String role = (String) i.next();
				if (this.externalContext.isUserInRole(role)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	private boolean isVisible(IMenuItem item) {
		boolean visible = true;
		String value = item.getVisible();		
		if ( ! StringUtils.isBlank(value) ) {
	        if ( UIComponentTag.isValueReference(value) ) {			
	        	FacesContext facesContext = FacesContext.getCurrentInstance();
	        	ValueBinding vb = facesContext.getApplication().createValueBinding(value);
	        	visible = (Boolean) vb.getValue(facesContext);
			} else {
				visible = Boolean.valueOf(value).booleanValue();
			}
		}
		return visible; 
	}

	private boolean isInsertable(IMenuItem item) {
		boolean visible = true;
		if (! this.omitRoles ) {
			visible = isAllowed(item);
		}
		if ( visible ) {
			visible = isVisible(item);
		}
		return visible; 
	}
	
}