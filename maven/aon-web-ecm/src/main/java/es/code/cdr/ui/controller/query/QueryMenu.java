/**
 * 
 */
package es.code.cdr.ui.controller.query;

import javax.faces.event.ActionEvent;
import javax.jcr.RepositoryException;

import es.code.ecm.AbstractWidget;
import es.code.ecm.Widget;
import es.code.ecm.nodes.ECMNode;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 12/07/2007
 *
 */
public class QueryMenu extends AbstractWidget {

	private static final long serialVersionUID = -1131543133804398407L;

	/** Tells whether a menu option is visible or not. */
	boolean visible;

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Menu item selected.  
	 * 
	 * @param event
	 * @throws RepositoryException 
	 */
	public void selected(ActionEvent event) throws RepositoryException {
    	fireWidgetSelected();
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#getSelected()
	 */
	public ECMNode getSelectedNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#perform(es.code.cdr.ui.controller.Widget)
	 */
	public void perform(Widget dependentWidget) {
		setVisible( dependentWidget.getSelectedNode() != null );
	}

}
