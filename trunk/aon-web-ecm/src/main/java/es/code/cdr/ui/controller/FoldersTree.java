/**
 * 
 */
package es.code.cdr.ui.controller;

import java.security.Principal;
import java.util.Calendar;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.servlet.http.HttpSession;

import org.apache.jackrabbit.core.security.AnonymousPrincipal;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import es.code.cdr.CDRQName;
import es.code.cdr.beans.CDRNode;
import es.code.cdr.beans.Folder;
import es.code.cdr.core.ContentRepository;
import es.code.cdr.core.SessionManager;
import es.code.cdr.core.Widget;
import es.code.cdr.core.WidgetSupport;
import es.code.cdr.event.WidgetListener;
import es.code.cdr.ui.util.CDRUtils;

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
        Node root = SessionManager.getInstance().getHierarchyManager( session.getId() ).getRootNode();
        try {
			Node cdr = root.getNode( ContentRepository.getNodeName( CDRQName.AON_CDR ) );
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
	 * Resets folder node name.  
	 * 
	 * @param event
	 * @throws RepositoryException 
	 */
	public void reset(ActionEvent event) throws RepositoryException {
		setName( null );
	}

	/**
	 * Adds a new Folder inside selected parent folder.
	 * 
	 * @param event
	 * @throws WidgetLoadingException 
	 */
	@SuppressWarnings("unchecked")
	public void add(ActionEvent event) throws RepositoryException, WidgetLoadingException {
		Principal p = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		String author = new AnonymousPrincipal().getName();
		Node selectedNode = ( (Folder) selected.getData() ).getNode();
		try {
			Folder newFolder = 
				new Folder( selectedNode.addNode( getName(), ContentRepository.getNodeName( CDRQName.AON_FOLDER ) ) );
			if ( p != null ) {
				author = p.getName();
			}
			newFolder.setAuthor( author );
			newFolder.setEntryDate( Calendar.getInstance() );
			newFolder.setRoles( new String[] {"Manager"} );
			selectedNode.save();
			TreeNodeImpl newNode = new TreeNodeImpl();
			newNode.setData( newFolder );
			selected.addChild( newFolder.getNode().toString(), newNode );
			setSelected( newNode );
		} catch (AccessDeniedException e) {
			CDRUtils.addWarningMessage( e.getMessage() );
			selectedNode.refresh( false );
		} catch (RepositoryException e) {
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
			CDRUtils.addWarningMessage( e.getMessage() );
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

	public CDRNode getSelectedNode() {
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
	        	String name = ContentRepository.getNodeName( CDRQName.AON_FOLDER );
	        	if ( node.getPrimaryNodeType().isNodeType( name ) ) {
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
			if( child.isNodeType( ContentRepository.getNodeName( CDRQName.AON_DOCUMENT ) ) )
				hasLock |= child.isLocked();
			else
				if(child.isNodeType( ContentRepository.getNodeName( CDRQName.AON_DOCUMENT ) ))
					hasLock |= hasLockedNodes(child);
		}
		return hasLock;
	}

}
