/*
 * Created on 05-sep-2006
 *
 */
package com.code.aon.common.tree.jsf;

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.myfaces.custom.tree2.TreeModelBase;

import com.code.aon.common.tree.ITreeNode;
import com.code.aon.common.tree.TreeModelException;
import com.code.aon.common.tree.event.TreeSelectionEvent;
import com.code.aon.common.tree.event.TreeSelectionListener;

/**
 * Abstract Tree Model class.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 05-sep-2006
 * @since 1.0
 *
 */

public abstract class AbstractTreeModel extends TreeModelBase {

    /** Proper class <code>Logger</code>. */
    private static Logger LOGGER = Logger.getLogger(AbstractTreeModel.class.getName());

    /** i18n <code>java.util.ResourceBundle</code>. */
    private ResourceBundle bundle;

    /** Selected node identifier. */
    private String selectedNodeId;

    /** Model listeners. */
    private Vector<TreeSelectionListener> listeners;

    /** Map of nodes. */
    private Map<String, ITreeNode> nodeMap;

    /**
     * Constructor.
     * 
     * @param root
     */
    public AbstractTreeModel(ITreeNode root) {
        super(root);
        this.listeners = new Vector<TreeSelectionListener>();
    }

    /**
     * Add a new <code>TreeSelectionListener</code>.
     * 
     * @param listener
     */
    public void addTreeSelectionListener(TreeSelectionListener listener) {
        if (listener == null) 
            throw new NullPointerException("addTreeSelectionListener[" + listener + "]");

        if ( !this.listeners.contains(listener) )
            this.listeners.add(listener);
    }

    /**
     * Remove a <code>TreeSelectionListener</code>.
     * 
     * @param listener
     */
    public void removeTreeSelectionListener(TreeSelectionListener listener) {
        if (this.listeners != null) 
            this.listeners.remove(listener);
    }

    /**
     * Returns an array of all the listeners which have been associated 
     * with the selection.
     * 
     * @return
     */
    public synchronized TreeSelectionListener[] getTreeSelectionListeners() {
    	TreeSelectionListener[] l = new TreeSelectionListener[ this.listeners.size() ];
    	int i = 0;
    	Iterator<TreeSelectionListener> iter = this.listeners.iterator(); 
    	while (iter.hasNext()) {
			l[i] = iter.next();
			i++;
		}
    	return l;
    }

    /**
     * Return the resource bundle.
     *  
	 * @return the bundle
     */
    public ResourceBundle getBundle() {
        return this.bundle;
    }

	/**
     * Assign the <code>ResourceBundle</code> for i18n.
     * 
     * @param bundle
     */
    public void setBundle(ResourceBundle bundle) {
        LOGGER.finest("Bundle " + bundle); //$NON-NLS-1$
        this.bundle = bundle;
    }

    /**
     * Return root node.
     * 
     * @return
     */
    public synchronized ITreeNode getRoot() {
        return (ITreeNode) super.getNodeById("0");
    }

    /**
     * Return selected node.
     * 
     * @return
     */
    public ITreeNode getSelectedNode() {
        return this.nodeMap.get( getSelectedNodeId() );
    }

    /**
     * Assign selected node.
     * 
     * @param id
     */
	public void setSelectedNode(String id) {
		TreeSelectionEvent event = new TreeSelectionEvent( this, this.getSelectedNodeId(), id );
		this.selectedNodeId = id;
		fireValueChanged( event );
	}

    /**
     * Return selected node identifier.
     * 
     * @return
     */
    public String getSelectedNodeId() {
        return (this.selectedNodeId != null) ? this.selectedNodeId : getRoot().getIdentifier();
    }

    /**
     * Return selected node action. 
     * 
     * @return
     */
    public String getAction() {
        return getSelectedNode().getReference();
    }

    /**
     * Initialize model.
     * 
     * @throws TreeModelException
     */
    public synchronized void init() throws TreeModelException {
        loadModel();
    }

    /**
     * Add a node to the nodes map.
     * 
     * @param treeNode
     */
    public void addNode2Map(ITreeNode treeNode) {
        this.nodeMap.put( treeNode.getIdentifier(), treeNode );
    }

    /**
     * Remove a node from the nodes map.
     * 
     * @param id
     */
    public void removeNodeFromMap(String id) {
        this.nodeMap.remove(id);
    }

    /**
     * Abstract method that loads tree model. This abstract method must be implemented by its subclasses.
     * 
     * @throws TreeModelException
     */
    protected abstract void loadModel() throws TreeModelException;

    /**
     * Assign node map.
     * 
     * @param nodeMap
     */
    protected void setNodeMap(Map<String, ITreeNode> nodeMap) {
        this.nodeMap = nodeMap;
    }

    /**
     * Return node map.
     * 
     * @return
     */
    protected Map<String, ITreeNode> getNodeMap() {
        return this.nodeMap;
    }

    /**
     * Notify all its listeners that the tree model selection node has changed.
     * 
     * @param event
     */
    protected void fireValueChanged(TreeSelectionEvent event) {
        LOGGER.fine( "Selected node: " + this.selectedNodeId + " - Selected Old node: " + this.getSelectedNode().getIdentifier() );
        Iterator i = this.listeners.iterator();
        while (i.hasNext()) {
            TreeSelectionListener listener = (TreeSelectionListener) i.next();
            if (event == null)
                event = new TreeSelectionEvent( this, this.getSelectedNode().getIdentifier(), this.selectedNodeId );
            listener.valueChanged(event);
        }
    }

}
