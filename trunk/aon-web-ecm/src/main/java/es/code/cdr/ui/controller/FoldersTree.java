/**
 * 
 */
package es.code.cdr.ui.controller;

import java.security.Principal;
import java.util.Calendar;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.servlet.http.HttpSession;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.jackrabbit.core.security.AnonymousPrincipal;

import com.icesoft.faces.component.tree.IceUserObject;

import es.code.cdr.CDRQName;
import es.code.cdr.beans.CDRNode;
import es.code.cdr.beans.Folder;
import es.code.cdr.core.ContentRepository;
import es.code.cdr.core.SessionManager;
import es.code.cdr.core.Widget;
import es.code.cdr.core.WidgetSupport;
import es.code.cdr.core.event.WidgetListener;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 11/07/2007
 *
 */
public class FoldersTree implements Widget {

	/** A description of any WidgetListeners which have been registered. */
	WidgetSupport support;

    // tree default model, used as a value for the tree component
    private DefaultTreeModel model;
    private DefaultMutableTreeNode rootTreeNode;
	/** Selected user object node. */
    CDRUserObject selected;
	/** New Folder node name. */
	String name;

	/**
	 * Constructs a <code>FoldersTree</code> object.
	 * 
	 * @throws WidgetLoadingException 
	 */
	public FoldersTree() throws WidgetLoadingException {
        HttpSession session = 
        	(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession( false ); 
        Session jcrSession = SessionManager.getInstance().get( session.getId() );
        try {
			Node cdr = 
				jcrSession.getRootNode().getNode( ContentRepository.getNodeName( CDRQName.AON_CDR ) );
	        rootTreeNode = createDefaultMutableTreeNode( new Folder( cdr ) );
	        model = new DefaultTreeModel( rootTreeNode );
			load( rootTreeNode );
        } catch (RepositoryException e) {
        	throw new WidgetLoadingException( e );
		}
    }

    /**
     * Gets the tree's default model.
     *
     * @return tree model.
     */
    public DefaultTreeModel getModel() {
        return model;
    }

    /**
     * Sets the tree's default model.
     *
     * @param model new default tree model
     */
    public void setModel(DefaultTreeModel model) {
        this.model = model;
    }

    /**
     * Sets the root tree node folder as selected.
     * @throws WidgetLoadingException 
     */
	public void setRootTreeNodeSelected() throws WidgetLoadingException {
		setSelected( (CDRUserObject) rootTreeNode.getUserObject() );
	}

	/**
	 * Sets the selected Folder. This changes a local instance variable used for
	 * display, it does not directly change the tree node state.
	 *
	 * @param selected.
	 * @throws WidgetLoadingException 
	 */
	public void setSelected(CDRUserObject selected) throws WidgetLoadingException {
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
	public void add(ActionEvent event) throws RepositoryException, WidgetLoadingException {
		Principal p = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		String author = new AnonymousPrincipal().getName();
		Node selectedNode = selected.getFolder().getNode();
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
			DefaultMutableTreeNode newNode = createDefaultMutableTreeNode( newFolder );
			selected.getWrapper().add( newNode );
			setSelected( (CDRUserObject) newNode.getUserObject() );
		} catch (RepositoryException e) {
			e.printStackTrace();
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
	public void remove(ActionEvent event) throws RepositoryException, WidgetLoadingException {
		Node selectedNode = selected.getFolder().getNode();
		if( hasLockedNodes( selectedNode ) )
            throw new LockException("Can't delete a locked node");
		
		selectedNode.remove();
		DefaultMutableTreeNode treeNode = selected.getWrapper();
		CDRUserObject userObject = 
			(CDRUserObject) ( (DefaultMutableTreeNode) treeNode.getParent() ).getUserObject();
		treeNode.removeFromParent();
		setSelected( userObject );
		selected.getFolder().getNode().save();
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
	public CDRNode getSelectedNode() {
		return selected.getFolder();
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#perform(es.code.cdr.ui.controller.Widget)
	 */
	public void perform(Widget dependentWidget) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @author Consulting & Development. Iñaki Ayerbe - 12/07/2007
	 *
	 */
	protected class CDRUserObject extends IceUserObject {

		Folder folder;

		public CDRUserObject(DefaultMutableTreeNode wrapper, Folder folder) throws RepositoryException {
			super(wrapper);

	        setLeafIcon("/css/iceCss/images/aon-icon/aon-icon-folder-contracted.png");
	        setBranchContractedIcon("/css/iceCss/images/aon-icon/aon-icon-folder-contracted.png");
	        setBranchExpandedIcon("/css/iceCss/images/aon-icon/aon-icon-folder-expanded.png");

			setText( folder.getName() );
			setExpanded( true );
			this.folder = folder;
		}

		/**
		 * @return the folder
		 */
		public Folder getFolder() {
			return folder;
		}

		/**
		 * Registers a user click with this object .
		 *
		 * @param event that fired this method
		 * @throws WidgetLoadingException 
		 */
		public void nodeClicked(ActionEvent event) throws WidgetLoadingException {
			setSelected( this );
		}
	}

	/**
	 * Loads folders tree model.
	 * 
	 * @param current
	 * @throws WidgetLoadingException
	 */
	private void load(DefaultMutableTreeNode defNode) throws WidgetLoadingException {
		try {
			CDRUserObject userObject = (CDRUserObject) defNode.getUserObject();
	        NodeIterator iter = userObject.getFolder().getNode().getNodes();
	        while ( iter.hasNext() ) {
	        	Node node = (Node) iter.next();
	        	String name = ContentRepository.getNodeName( CDRQName.AON_FOLDER );
	        	if ( node.getPrimaryNodeType().isNodeType( name ) ) {
		        	DefaultMutableTreeNode branchNode = 
		        		createDefaultMutableTreeNode( new Folder( node ) );
		        	defNode.add( branchNode );
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
	 * Creates a <code>DefaultMutableTreeNode</code>.
	 * 
	 * @param folder
	 * @return
	 * @throws RepositoryException
	 */
	private DefaultMutableTreeNode createDefaultMutableTreeNode(Folder folder) throws RepositoryException {
		DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode();
		CDRUserObject branchObject = new CDRUserObject( branchNode, folder );
		branchNode.setUserObject( branchObject );
		return branchNode;
	}

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
