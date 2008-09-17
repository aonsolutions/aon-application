package com.code.aon.ui.menu.jsf;

import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeNode;

import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuItem;

/**
 * Defines common for Managed Bean that work the Tree2 component of Tomahawk.
 * 
 * @author Consulting & Development. Aimar Tellitu - 06-jun-2005
 * @since 1.0
 */
public abstract class AbstractTree2Menu extends AbstractComponent implements IJSFMenu {

    /** Logger initialization. */
	private static Logger LOGGER = Logger.getLogger(AbstractTree2Menu.class.getName());

	/**
	 * Gets the html tree.
	 * 
	 * @param e the e
	 * 
	 * @return the html tree
	 */
	private HtmlTree getHtmlTree(ActionEvent e) {
		UIComponent component = e.getComponent();
		while ( !(component instanceof HtmlTree) ) {
			component = component.getParent();
		}
		return (HtmlTree) component;
	}
	
	/**
	 * Invoked when an {@link IMenu} is pressed..
	 * 
	 * @param event The {@link ActionEvent} that has occurred
	 */
	public void menuPressed(ActionEvent event) {
		pressed( event );		
	}

	@Override
	public void optionPressed(ActionEvent event) {
		pressed( event );
	}

	/**
	 * Invoked when an {@link IMenuItem} is pressed..
	 * 
	 * @param event The {@link ActionEvent} that has occurred
	 */
	protected void pressed( ActionEvent event ) {
		HtmlTree htmlTree = getHtmlTree(event);
		TreeNode node = htmlTree.getNode();
		LOGGER.fine("pressed: " + node.getDescription() );
		getMenuModel().setSelectedNode( node.getIdentifier() );
	}
	
}
