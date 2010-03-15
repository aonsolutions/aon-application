package com.code.aon.common.tree.jsf;

import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeNode;

/**
 * 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 05-sep-2005
 * @since 1.0
 *  
 */
public abstract class AbstractTree2 extends AbstractComponent {

	/**
	 * Obtiene un <code>Logger</code> apropiado.
	 */
	private static Logger LOGGER = Logger.getLogger(AbstractTree2.class.getName());

    /**
     * Componente Html que visualiza el árbol.
     */
	private HtmlTree htmlTree;

    /**
     * @return Returns the htmlTree.
     */
    public HtmlTree getHtmlTree() {
        return this.htmlTree;
    }

    /**
     * @param htmlTree The htmlTree to set.
     */
    public void setHtmlTree(HtmlTree htmlTree) {
        this.htmlTree = htmlTree;
    }

    /**
     * Indica si el elegido se encuentra seleccionado.
     * 
     * @return
     */
	public boolean isSelected() {
		TreeNode node = this.htmlTree.getNode();
		return node.getIdentifier().equals( getTreeModel().getSelectedNodeId() );
	}

    /**
     * 
     * @return
     */
	public String treeAction() {
		return treeAction(true);
	}

    /**
     * 
     * @return
     */
	public String treeTextAction() {
		return treeAction(false);
	}

    /*
     *  (non-Javadoc)
     * @see com.code.aon.common.tree.jsf.AbstractComponent#pressed(javax.faces.event.ActionEvent)
     */
    public void pressed(ActionEvent e) {
        TreeNode node = this.htmlTree.getNode();
        LOGGER.fine("selected: " + node.getDescription() );
        getTreeModel().setSelectedNode( node.getIdentifier() );
    }

    /**
     * Esta acción actua sobre el árbol, desplegandolo o comprimiendolo, y sobre el nodo seleccionado,
     * ejecutando la acción asociada al mismo.
     * 
     * @param toggleExpanded
     * @return
     */
	private String treeAction(boolean toggleExpanded) {
		String id = getTreeModel().getSelectedNodeId();
		if ( ! this.htmlTree.isNodeExpanded() || toggleExpanded ) {
			LOGGER.fine( "menuAction: " + id + " toggle expanded" );
			this.htmlTree.toggleExpanded();			
		}
		return nodeAction();
	}

}
