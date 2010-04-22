/**
 * 
 */
package es.code.cdr.ui.controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.servlet.http.HttpSession;

import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import es.code.ecm.ContentRepository;
import es.code.ecm.ECMQName;
import es.code.ecm.HierarchyManager;
import es.code.ecm.SessionManager;
import es.code.ecm.Widget;
import es.code.ecm.WidgetSupport;
import es.code.ecm.event.WidgetListener;
import es.code.ecm.nodes.ECMNode;
import es.code.ecm.nodes.Folder;
import es.code.ecm.util.ECMUtil;
import es.code.ecm.util.JCRUtils;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 11/07/2007
 *
 */
public class FoldersTree implements Widget {

	private static final long serialVersionUID = -5736845221746376317L;

	/** A description of any WidgetListeners which have been registered. */
	WidgetSupport support;

	@SuppressWarnings("unchecked")
	TreeNode rootTreeNode;
	String rootTreeNodeId;
	/** Selected folder TreeNode. */
    @SuppressWarnings("unchecked")
	TreeNode selected;
	/** New Folder node name. */
	String name;

	/**
	 * Constructs a <code>FoldersTree</code> object.
	 * 
	 * @throws WidgetLoadingException 
	 */
	@SuppressWarnings("unchecked")
	public FoldersTree() throws WidgetLoadingException {
        HttpSession session = 
        	(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession( false );
        HierarchyManager hm = SessionManager.getInstance().getHierarchyManager( session.getId() ); 
        try {
			String folderRootRelPath = hm.getWorkspace().getName() + "_" + ECMQName.AON_FOLDER_SUFFIX;
			Node cdr = hm.getRootNode().getNode( folderRootRelPath );
			rootTreeNode = new TreeNodeImpl();
			TreeNode cdrTreeNode = new TreeNodeImpl();
			Folder cdrFolder = new Folder( cdr );
			cdrTreeNode.setData( cdrFolder );
			rootTreeNode.addChild( rootTreeNodeId = cdr.toString(), cdrTreeNode );
			rootTreeNode.setData( null );
			load( cdrTreeNode );
        } catch (RepositoryException e) {
        	throw new WidgetLoadingException( e );
		}
    }

    @SuppressWarnings("unchecked")
	public TreeNode getRootTreeNode() {
		return rootTreeNode;
	}

	/**
     * Sets the root tree node folder as selected.
     * @throws WidgetLoadingException 
     */
	public void setRootTreeNodeSelected() throws WidgetLoadingException {
		setSelected( rootTreeNode.getChild( rootTreeNodeId ) );
	}

	/**
	 * Sets the selected Folder. This changes a local instance variable used for
	 * display, it does not directly change the tree node state.
	 *
	 * @param selected.
	 * @throws WidgetLoadingException 
	 */
	@SuppressWarnings("unchecked")
	public void setSelected(TreeNode selected) throws WidgetLoadingException {
		this.selected = selected;
		support.fireWidgetSelected();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Initializes folders tree.  
	 * 
	 * @param event
	 * @throws RepositoryException 
	 */
	public void reset(ActionEvent event) {
		try {
			setRootTreeNodeSelected();
		} catch (WidgetLoadingException e) {
			ECMUtil.addErrorMessage( e.getMessage() );
		}
	}

	/**
	 * Adds a new Folder inside selected parent folder.
	 * 
	 * @param event
	 * @throws WidgetLoadingException 
	 */
	@SuppressWarnings("unchecked")
	public void add(ActionEvent event) throws RepositoryException, WidgetLoadingException {
		Node selectedNode = ( (Folder) selected.getData() ).getNode();
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		try {
			String primaryNodeTypeName = ContentRepository.getNodeName( ECMQName.AON_FOLDER );
			Folder newFolder = new Folder( selectedNode.addNode( getName(), primaryNodeTypeName ) );
			newFolder.setName( getName() );
			String userUUID = JCRUtils.getUserUUID( ec.getUserPrincipal(), selectedNode.getSession() );
			newFolder.setUser( userUUID );
			selectedNode.save();
			TreeNodeImpl newNode = new TreeNodeImpl();
			newNode.setData( newFolder );
			selected.addChild( newFolder.getNode().toString(), newNode );
		} catch (AccessDeniedException e) {
			ECMUtil.addWarningMessage( e.getMessage() );
			selectedNode.refresh( false );
		} catch (RepositoryException e) {
			ECMUtil.addErrorMessage( e.getMessage() );
			selectedNode.refresh( false );
		}
	}

	/**
	 * Removes selected Folder.
	 * 
	 * @param event
	 * @throws WidgetLoadingException 
	 * @throws RepositoryException 
	 */
	@SuppressWarnings("unchecked")
	public void remove(ActionEvent event) throws RepositoryException, WidgetLoadingException {
		Node selectedNode = ( (Folder) selected.getData() ).getNode();
		if ( hasLockedNodes( selectedNode ) )
            throw new LockException("Can't delete a locked node");

		Node parentNode = selectedNode.getParent();
		TreeNode parentTreeNode = selected.getParent();
		selectedNode.remove();
		try {
			parentNode.save();
			Folder folder = (Folder) selected.getData();
			parentTreeNode.removeChild( folder.getNode().toString() );
		} catch (AccessDeniedException e) {
			ECMUtil.addWarningMessage( e.getMessage() );
			parentNode.refresh( false );
		}
		setSelected( parentTreeNode );
	}

	/**
	 * Selects tree node.
	 * 
	 * @param event
	 * @throws WidgetLoadingException 
	 */
	public void nodeSelected(NodeSelectedEvent event) throws WidgetLoadingException {
		UITree tree = (UITree) event.getComponent();
		System.out.println( tree.getRowIndex() + " " + tree.getRowKey() + " " + tree.getRowKey().getClass() );
		setSelected( tree.getTreeNode() );
	}

//	************************************** Widget methods implementation ****************************************
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

	public void removeWidgetListener(WidgetListener l) {
		if ( l != null ) {
			synchronized (this) {
				if ( support != null ) {
					support.removeWidgetListener( l );
				}
			}
		}
	}

	public ECMNode getSelectedNode() {
		return (Folder) selected.getData();
	}

	public void perform(Widget dependentWidget) {
		// TODO Auto-generated method stub
		
	}
//	********************************** Ends of Widget methods implementation ************************************

	/**
	 * Loads folders tree.
	 * 
	 * @param current
	 * @throws WidgetLoadingException
	 */
	@SuppressWarnings("unchecked")
	private void load(TreeNode defNode) throws WidgetLoadingException {
		try {
			Folder folder = (Folder) defNode.getData();
	        NodeIterator iter = folder.getNode().getNodes();
	        while ( iter.hasNext() ) {
	        	Node node = (Node) iter.next();
	        	String primaryNodeTypeName = ContentRepository.getNodeName( ECMQName.AON_FOLDER );
	        	if ( node.getPrimaryNodeType().isNodeType( primaryNodeTypeName ) ) {
	    			TreeNodeImpl branchNode = new TreeNodeImpl();
	    			Folder branchFolder = new Folder( node );
	    			branchNode.setData( branchFolder );
	    			defNode.addChild( branchFolder.getNode().toString(), branchNode );
		        	if ( node.hasNodes() ) {
		        		load( branchNode );
		        	}
	        	}
	        }
		} catch (RepositoryException e) {
			throw new WidgetLoadingException( e );
		}
	}

	/**
	 * Tells if node passed by parameter has locked nodes inside.
	 * 
	 * @param node
	 * @return
	 * @throws RepositoryException
	 */
	private boolean hasLockedNodes(Node node) throws RepositoryException {
		boolean hasLock = false;
		for(NodeIterator ni = node.getNodes(); ni.hasNext();) {
			Node child = ni.nextNode();
			if( child.isNodeType( ContentRepository.getNodeName( ECMQName.AON_DOCUMENT ) ) )
				hasLock |= child.isLocked();
			else
				if(child.isNodeType( ContentRepository.getNodeName( ECMQName.AON_DOCUMENT ) ))
					hasLock |= hasLockedNodes(child);
		}
		return hasLock;
	}

}
