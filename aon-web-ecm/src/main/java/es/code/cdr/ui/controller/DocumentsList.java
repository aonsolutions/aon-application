/**
 * 
 */
package es.code.cdr.ui.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.version.Version;

import es.code.cdr.ui.controller.query.QueryMenu;
import es.code.cdr.ui.util.CDRDataModel;
import es.code.ecm.ContentRepository;
import es.code.ecm.ECMQName;
import es.code.ecm.IConstants;
import es.code.ecm.Widget;
import es.code.ecm.WidgetSupport;
import es.code.ecm.event.WidgetListener;
import es.code.ecm.nodes.Document;
import es.code.ecm.nodes.ECMNode;
import es.code.ecm.util.DocumentUpload;
import es.code.ecm.util.ECMUtil;
import es.code.ecm.util.JCRUtils;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 11/07/2007
 *
 */
public class DocumentsList implements Widget {

	/** A description of any WidgetListeners which have been registered. */
	WidgetSupport support;
	/** Folder node list of documents. */
	DataModel model;
	/** Selected Document node. */
	Document selected;
	/** Selected document version history model. */
	DataModel versionHistoryModel;
	/** Document uploading manager. */
	DocumentUploadBean upload;

	/**
	 * Constructs a <code>DocumentsList</code> object.
	 */
	public DocumentsList() {
		upload = new DocumentUploadBean();
	}

	/**
	 * @return the model
	 */
	public DataModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(DataModel model) {
		this.model = model;
	}

	/**
	 * @return the version history
	 */
	public DataModel getVersionHistoryModel() {
		try {
			versionHistoryModel = new CDRDataModel( selected.getVersionHistory() );
		} catch (RepositoryException e) {
			versionHistoryModel = new CDRDataModel();
			ECMUtil.addErrorMessage( e.getMessage() );
		}
		return versionHistoryModel;
	}

	/**
	 * @return the upload
	 */
	public DocumentUploadBean getUpload() {
		return upload;
	}

	/**
	 * @param upload the upload to set
	 */
	public void setUpload(DocumentUploadBean upload) {
		this.upload = upload;
	}

	/**
	 * @return the selected
	 */
	public Document getSelected() {
		return selected;
	}

	/**
     * Sets the selected Document. This changes a local instance variable used for
     * display, it does not directly change the tree node state.
     * 
	 * @param selected the document to set
	 */
	public void setSelected(Document selected) {
		this.selected = selected;
    	support.fireWidgetSelected();
	}

	/**
	 * Registers a user selection with this object .
	 *
	 * @param event that fired this method
	 */
	public void documentSelected(ActionEvent event) {
		setSelected( (Document) this.model.getRowData() );
	}

	/**
	 * Resets document node name.  
	 * 
	 * @param event
	 */
	public void reset(ActionEvent event) {
		upload.clearUploadData( event );
	}

	/**
	 * Adds a new Document inside selected folder.
	 * 
	 * @throws WidgetLoadingException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public synchronized void add(Node folderNode) throws RepositoryException, IOException {
		DocumentUpload dup = upload.getSelectedDocument();
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		try {
			String primaryNodeTypeName = ContentRepository.getNodeName( ECMQName.AON_DOCUMENT );
			Node documentNode = folderNode.addNode( dup.getName(), primaryNodeTypeName );
			Document document = new Document( documentNode );
			String userUUID = JCRUtils.getUserUUID( ec.getUserPrincipal(), documentNode.getSession() );
			document.setUser( userUUID );
			document.add( dup );
			folderNode.save();
			document.checkin();
			List l = (List) model.getWrappedData();
			int index = l.size();
			l.add( index, document );
			model.setRowIndex( index );
			setSelected( document );
			upload.clearSelectedUploadData();
		} catch (AccessDeniedException e) {
			ECMUtil.addWarningMessage( e.getMessage() );
			folderNode.refresh( false );
		} catch (RepositoryException e) {
			ECMUtil.addWarningMessage( e.getMessage() );
			folderNode.refresh( false );
		}
	}

	/**
	 * Updates selected Document.
	 * 
	 * @throws WidgetLoadingException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public synchronized void update(ActionEvent event) {
		try {
			selected.save();
		} catch (RepositoryException e) {
			ECMUtil.addWarningMessage( e.getMessage() );
		}
	}

	/**
	 * Removes selected Document.
	 * 
	 * @param event
	 * @throws WidgetLoadingException 
	 * @throws RepositoryException 
	 */
	public synchronized void remove(ActionEvent event) throws RepositoryException, WidgetLoadingException {
		if( selected.getNode().isLocked() ) {
			String messageId = "aon_cdr_remove_a_locked_document_exception";
			Locale locale = ECMUtil.getCurrentLocale( FacesContext.getCurrentInstance() );
			String msg = 
				ECMUtil.getMessage( IConstants.ECM_BUNDLE_NAME, locale, messageId, new String[] {selected.getName()} ).getSummary();
			ECMUtil.addErrorMessage( msg );
			return;
		}

		Node parent = null; 
		try {
			parent = selected.getNode().getParent();
			selected.getNode().remove();
			parent.save();
			setSelected( null );
			load( parent );
		} catch (AccessDeniedException e) {
			ECMUtil.addWarningMessage( e.getMessage() );
			parent.refresh( false );
		} catch (RepositoryException e) {
			if ( parent != null )
				parent.refresh( false );
		}
	}

	/**
	 * Downloads selected document.
	 * 
	 * @param event
	 */
	public void download(ActionEvent event) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			Document d = getSelected();
			ECMUtil.download( ctx, d.getName(), d.getMimeType(), d.getContent() );
		} catch (IOException e) {
			ECMUtil.addErrorMessage( e.getMessage() );
		} catch (RepositoryException e) {
			ECMUtil.addErrorMessage( e.getMessage() );
		}
	}

	/**
	 * Downloads selected document version.
	 * 
	 * @param event
	 */
	public void downloadVersion(ActionEvent event) {
		try {
			Version version = (Version) versionHistoryModel.getRowData();
			Document d = getSelected();
			InputStream is = selected.getDocument4Version( version );
			ECMUtil.download( FacesContext.getCurrentInstance(), d.getName(), d.getMimeType(), is );
		} catch (IOException e) {
			ECMUtil.addErrorMessage( e.getMessage() );
		} catch (RepositoryException e) {
			ECMUtil.addErrorMessage( e.getMessage() );
		}
	}

	/**
	 * Checks in selected document.
	 * 
	 * @param event
	 * @throws IOException 
	 */
	public synchronized void checkin(ActionEvent event) throws IOException, RepositoryException {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			String userUUID = JCRUtils.getUserUUID( ec.getUserPrincipal(), selected.getNode().getSession() );
			selected.setUser( userUUID );
			selected.checkin( upload.getSelectedDocument() );
			upload.clearUploadData( event );
		} catch (RepositoryException e) {
			ECMUtil.addErrorMessage( e.getMessage() );
		}
	}

	/**
	 * Checks out selected document.
	 * 
	 * @param event
	 */
	public void checkout(ActionEvent event) {
		try {
			selected.refresh( false );
			selected.checkout();
			download( event );
		} catch (LockException e) {
			ECMUtil.addErrorMessage( ECMUtil.parseJackrabbitException( e.getMessage() ) );
		} catch (RepositoryException e) {
			ECMUtil.addFatalMessage( e.getMessage() );
		}
	}

	/**
	 * Tells if the selected document is checked out, otherwise false.
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public boolean isCheckedOut() throws RepositoryException {
		return ( selected == null )? false: selected.isCheckedOut();
	}

	/**
	 * Restores selected document. This method cancels previously checked out document. 
	 * 
	 * @param event
	 */
	public void restore(ActionEvent event) {
		try {
			selected.refresh( false );
			selected.restore();
		} catch (RepositoryException e) {
			ECMUtil.addErrorMessage( e.getMessage() );
		}
	}

	/**
	 * Restores selected document version.
	 * 
	 * @param event
	 */
	public void restoreVersion(ActionEvent event) {
		Version version = (Version) versionHistoryModel.getRowData();
		try {
			selected.refresh( false );
			selected.restoreVersion( version.getName() );
		} catch (RepositoryException e) {
			ECMUtil.addErrorMessage( e.getMessage() );
		}
	}

	/**
	 * Locks selected Document.
	 * 
	 * @param event
	 * @throws WidgetLoadingException 
	 */
	public void lock(ActionEvent event) {
        try {
			selected.refresh( false );
			selected.lock();
		} catch (LockException e) {
			ECMUtil.addErrorMessage( ECMUtil.parseJackrabbitException( e.getMessage() ) );
		} catch (RepositoryException e) {
			ECMUtil.addFatalMessage( e.getMessage() );
		}
	}

	/**
	 * Unlocks selected Document.
	 * 
	 * @param event
	 * @throws WidgetLoadingException 
	 * @throws RepositoryException 
	 */
	public void unlock(ActionEvent event) {
        try {
			selected.refresh( false );
			selected.unlock();
		} catch (LockException e) {
			ECMUtil.addErrorMessage( ECMUtil.parseJackrabbitException( e.getMessage() ) );
		} catch (RepositoryException e) {
			ECMUtil.addFatalMessage( e.getMessage() );
		}		
	}

//****************** Widget interface methods implementation ******************************************* 
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
		return selected;
	}

	public void perform(Widget dependentWidget) {
		if ( dependentWidget instanceof FoldersTree ) {
			ECMNode parentNode = dependentWidget.getSelectedNode();
			load( parentNode.getNode() );
	        setSelected( null );
		}
		if ( dependentWidget instanceof QueryMenu ) {
			load( null );
	        setSelected( null );
		}
	}
//****************** End of Widget interface methods implementation ************************************ 

	/**
	 * Loads parent child document nodes inside <code>DataModel</code>.
	 * 
	 * @param parent
	 */
	private void load(Node parent) {
		ArrayList<Document> l = new ArrayList<Document>();
		if ( parent != null ) {
			try {
				NodeIterator iter = parent.getNodes();
		        while ( iter.hasNext() ) {
		        	Node node = (Node) iter.next();
		        	String name = ContentRepository.getNodeName( ECMQName.AON_DOCUMENT );
		        	if ( node.getPrimaryNodeType().isNodeType( name ) ) {
		        		l.add( new Document( node ) );
		        	}
		        }
			} catch (RepositoryException e) {
			}
		}
        model = new CDRDataModel( l );
	}

}
