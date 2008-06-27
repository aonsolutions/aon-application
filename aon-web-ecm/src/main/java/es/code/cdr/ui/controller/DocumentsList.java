/**
 * 
 */
package es.code.cdr.ui.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;

import org.apache.jackrabbit.core.security.AnonymousPrincipal;

import es.code.cdr.CDRQName;
import es.code.cdr.beans.CDRNode;
import es.code.cdr.beans.Document;
import es.code.cdr.core.ContentRepository;
import es.code.cdr.core.Widget;
import es.code.cdr.core.WidgetSupport;
import es.code.cdr.event.WidgetListener;
import es.code.cdr.ui.controller.query.QueryMenu;
import es.code.cdr.ui.util.CDRDataModel;
import es.code.cdr.ui.util.CDRUtils;
import es.code.cdr.ui.util.DocumentUpload;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 11/07/2007
 *
 */
public class DocumentsList implements Widget {

	private static final long serialVersionUID = -8856985254062000747L;

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
			CDRUtils.addErrorMessage( e.getMessage() );
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
	public void add(Node folderNode) throws RepositoryException, IOException {
		DocumentUpload dup = upload.getSelectedDocument();
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		String author = getAuthor( ec );
		try {
			Document document = new Document( folderNode.addNode( dup.getName(), ContentRepository.getNodeName( CDRQName.AON_DOCUMENT ) ) );
			document.setAuthor( author );
			document.setEntryDate( Calendar.getInstance( ec.getRequestLocale() ) );
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
			CDRUtils.addWarningMessage( e.getMessage() );
			folderNode.refresh( false );
		} catch (RepositoryException e) {
			CDRUtils.addWarningMessage( e.getMessage() );
			folderNode.refresh( false );
		}
	}

	/**
	 * Removes selected Document.
	 * 
	 * @param event
	 * @throws WidgetLoadingException 
	 * @throws RepositoryException 
	 */
	public void remove(ActionEvent event) throws RepositoryException, WidgetLoadingException {
		if( selected.getNode().isLocked() ) {
			CDRUtils.addErrorMessage( "Can't delete locked document[" + selected.getName() + "]" );
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
			CDRUtils.addWarningMessage( e.getMessage() );
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
			CDRUtils.download( FacesContext.getCurrentInstance(), getSelected() );
		} catch (IOException e) {
			CDRUtils.addErrorMessage( e.getMessage() );
		} catch (RepositoryException e) {
			CDRUtils.addErrorMessage( e.getMessage() );
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
			CDRUtils.download( FacesContext.getCurrentInstance(), selected.getDocument4Version( version ) );
		} catch (IOException e) {
			CDRUtils.addErrorMessage( e.getMessage() );
		} catch (RepositoryException e) {
			CDRUtils.addErrorMessage( e.getMessage() );
		}
	}

	/**
	 * Checks in selected document.
	 * 
	 * @param event
	 * @throws IOException 
	 */
	public void checkin(ActionEvent event) throws IOException {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			selected.setAuthor( getAuthor( ec ) );
			selected.checkin( upload.getSelectedDocument() );
			upload.clearUploadData( event );
		} catch (RepositoryException e) {
			CDRUtils.addErrorMessage( e.getMessage() );
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
		} catch (RepositoryException e) {
			CDRUtils.addErrorMessage( e.getMessage() );
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
			CDRUtils.addErrorMessage( e.getMessage() );
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
			CDRUtils.addErrorMessage( e.getMessage() );
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
		} catch (RepositoryException e) {
			CDRUtils.addErrorMessage( e.getMessage() );
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
		} catch (RepositoryException e) {
			CDRUtils.addErrorMessage( e.getMessage() );
		}		
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
		return selected;
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#perform(es.code.cdr.ui.controller.Widget)
	 */
	public void perform(Widget dependentWidget) {
		if ( dependentWidget instanceof FoldersTree ) {
			CDRNode parentNode = dependentWidget.getSelectedNode();
			load( parentNode.getNode() );
	        setSelected( null );
		}
		if ( dependentWidget instanceof QueryMenu ) {
			load( null );
	        setSelected( null );
		}
	}

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
		        	String name = ContentRepository.getNodeName( CDRQName.AON_DOCUMENT );
		        	if ( node.getPrimaryNodeType().isNodeType( name ) ) {
		        		l.add( new Document( node ) );
		        	}
		        }
			} catch (RepositoryException e) {
			}
		}
        model = new CDRDataModel( l );
	}

	/**
	 * Returns author name from requested principal, otherwise returns an anonimous one.
	 * 
	 * @param ec
	 * @return
	 */
	private String getAuthor(ExternalContext ec) {
		Principal p = ec.getUserPrincipal();
		String author = new AnonymousPrincipal().getName();
		if ( p != null ) {
			author = p.getName();
		}
		return author;
	}

}
