package com.code.aon.ui.menu.jsf;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.menu.IMenuItem;
import com.code.aon.ui.menu.core.ActionListener;

/**
 * An implementation of the {@link AbstractTree2Menu} abstract class
 * to serve as a basis for Managed Beans that can receive the pressed
 * event and need to notify to their listeners. 
 * 
 * @author Consulting & Development. Aimar Tellitu - 06-jun-2005
 * @since 1.0
 *  
 */
public abstract class AbstractMenu extends AbstractTree2Menu {

	@Override	
	protected void pressed(ActionEvent event ) {
		super.pressed( event );
		String id = getMenuModel().getSelectedNodeId();
		IMenuItem menuItem = (IMenuItem) getMenuModel().getMenuItem( id );
		Iterator i = menuItem.actionListeners();
		while ( i.hasNext() ) {
			ActionListener actionListener = (ActionListener) i.next();
			actionListener.execute( event, this );
		}
	}
	
}
