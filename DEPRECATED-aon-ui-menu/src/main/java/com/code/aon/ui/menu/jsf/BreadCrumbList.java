package com.code.aon.ui.menu.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.tree2.TreeNode;

import com.code.aon.ui.menu.IMenuItem;

/**
 * The Managed Bean used to show the breadcrumb list from the root node
 * of the menu to the current selected item.
 * 
 * @author Consulting & Development. Aimar Tellitu - 06-jun-2005
 * @since 1.0
 */
public class BreadCrumbList extends AbstractComponent {

    /**
	 * Logger initialization.
     */
	private static Logger LOGGER = Logger.getLogger(BreadCrumbList.class.getName());
	
	/**
	 * Gets the list of elements form the root node to the current selected item.
	 * 
	 * @return the list of elements form the root node to the current selected item.
	 */
	public List<TreeNode> getList() {
		LOGGER.fine("getList");
		String id = getMenuModel().getSelectedNodeId();
		return getPath(id);
	}

	/**
	 * Gets the title of the menu.
	 * 
	 * @return the title the menu.
	 */
	public String getTitle() {
		return getMenuModel().getSelectedNode().getDescription();
	}

	private ArrayList<TreeNode> getPath(String id) {
		ArrayList<TreeNode> list = new ArrayList<TreeNode>();
		if ( id != null ) {
			IMenuItem item = (IMenuItem) getMenuModel().getMenuItem(id);
			while ( item != null ) {
				list.add( 0, getMenuModel().getTreeNode( item.getId() ) );
				item = item.getParent();
			}
			if ( list.size() > 2 ) {
				list.remove( 0 );
				list.remove( list.size()-1 );
			} else {
				list.clear();
			}
		}
		return list;
	}
	
	@Override
	public void optionPressed(ActionEvent event) {
		UIComponent component =  event.getComponent();
		List list = component.getChildren();
		if (! list.isEmpty() ) {
			UIOutput hiddenText = (UIOutput) list.get(0);
			String id = (String) hiddenText.getValue();
			getMenuModel().setSelectedNode( id );
		}
	}

}
