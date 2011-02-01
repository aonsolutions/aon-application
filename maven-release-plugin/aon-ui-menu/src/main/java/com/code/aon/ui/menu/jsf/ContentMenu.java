package com.code.aon.ui.menu.jsf;

import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.myfaces.custom.tree2.TreeNode;

/**
 * The Managed Bean used to show only one level of the menu. Usually used to represent
 * the children of the current select itemn in the menu.
 * 
 * @author Consulting & Development. Aimar Tellitu - 06-jun-2005
 * @since 1.0
 */
public class ContentMenu extends AbstractMenu {
	
    /**
	 * Logger initialization.
     */
	private static Logger LOGGER = Logger.getLogger(ContentMenu.class.getName());

	public TreeNode getMenu() {
		LOGGER.fine("getMenu");		
		return getPartialMenu();
	}

	public String getTitle() {
		return getMenuModel().getSelectedNode().getDescription();
	}

	@SuppressWarnings("unchecked")
	private TreeNode getPartialMenu() {
		TreeNode selectedNode = getMenuModel().getSelectedNode();
		TreeNode root = clone( selectedNode );
		Iterator i = selectedNode.getChildren().iterator();
		while ( i.hasNext() ) {
			TreeNode child = (TreeNode) i.next();
			TreeNode node = clone( child );
			node.setLeaf( false );
			root.getChildren().add( node );
		}
		return root;
	}
	
}
