/**
 * 
 */
package es.code.cdr.ui.controller;

import es.code.ecm.Widget;
import es.code.ecm.WidgetSupport;
import es.code.ecm.event.WidgetListener;
import es.code.ecm.nodes.ECMNode;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 12/07/2007
 *
 */
public class CDRMenu implements Widget {

	private static final long serialVersionUID = -4377533127671999159L;

	/** A description of any WidgetListeners which have been registered. */
	WidgetSupport support;
	/** Tells whether a menu option is visible or not. */
	boolean visible;
	/** show or hide Folder popup panel. */
	boolean showFolderModalPanel = false;
	/** show or hide Document popup panel. */
	boolean showDocumentModalPanel = false;
	/** show or hide Checkin popup panel. */
	boolean showCheckinModalPanel = false;

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
	 * @return the showDocumentModalPanel
	 */
	public boolean isShowDocumentModalPanel() {
		return showDocumentModalPanel;
	}

	/**
	 * @param showDocumentModalPanel the showDocumentModalPanel to set
	 */
	public void setShowDocumentModalPanel(boolean showDocumentModalPanel) {
		this.showDocumentModalPanel = showDocumentModalPanel;
	}

	/**
	 * @return the showCheckinModalPanel
	 */
	public boolean isShowCheckinModalPanel() {
		return showCheckinModalPanel;
	}

	/**
	 * @param showCheckinModalPanel the showCheckinModalPanel to set
	 */
	public void setShowCheckinModalPanel(boolean showCheckinModalPanel) {
		this.showCheckinModalPanel = showCheckinModalPanel;
	}

	/**
	 * @return the showFolderModalPanel
	 */
	public boolean isShowFolderModalPanel() {
		return showFolderModalPanel;
	}

	/**
	 * @param showFolderModalPanel the showFolderModalPanel to set
	 */
	public void setShowFolderModalPanel(boolean showFolderModalPanel) {
		this.showFolderModalPanel = showFolderModalPanel;
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#addWidgetListener(es.code.cdr.ui.controller.event.WidgetListener)
	 */
	public void addWidgetListener(WidgetListener l) {
		if ( l != null ) {
			synchronized (this) {
				if ( support == null ) {
					support = new WidgetSupport(this);
				}
				support.addWidgetListener( l );
			}
		}
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#removeWidgetListener(es.code.cdr.ui.controller.event.WidgetListener)
	 */
	public void removeWidgetListener(WidgetListener l) {
		if ( l != null ) {
			synchronized (this) {
				if ( support != null ) {
					support.removeWidgetListener( l );
				}
			}
		}
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
