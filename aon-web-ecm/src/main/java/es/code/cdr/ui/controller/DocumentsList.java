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
import javax.faces.model.ListDataModel;
import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.servlet.http.HttpSession;

import org.apache.jackrabbit.core.security.AnonymousPrincipal;

import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.context.effects.JavascriptContext;

import es.code.cdr.CDRQName;
import es.code.cdr.beans.CDRNode;
import es.code.cdr.beans.Document;
import es.code.cdr.core.ContentRepository;
import es.code.cdr.core.Widget;
import es.code.cdr.core.WidgetSupport;
import es.code.cdr.event.WidgetListener;
import es.code.cdr.ui.controller.query.QueryMenu;

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
	DocumentUpload upload;

	/**
	 * Constructs a <code>DocumentsList</code> object.
	 */
	public DocumentsList() {
		upload = new DocumentUpload();
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
			versionHistoryModel = new ListDataModel( selected.getVersionHistory() );
		} catch (RepositoryException e) {
			versionHistoryModel = new ListDataModel();
			AonUtil.addErrorMessage( e.getMessage() );
		}
		return versionHistoryModel;
	}

	/**
	 * @return the upload
	 */
	public DocumentUpload getUpload() {
		return upload;
	}

	/**
	 * @param upload the upload to set
	 */
	public void setUpload(DocumentUpload upload) {
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
		try {
			removeDocument4Download();
		} catch (RepositoryException e) {
		}
		this.selected = selected;
    	support.fireWidgetSelected();
	}

	/**
	 * Registers a user selection with this object .
	 *
	 * @param event that fired this method
	 */
	public void documentSelected(RowSelectorEvent event) {
		setSelected( (Document) this.model.getRowData() );
	}

	/**
	 * Resets document node name.  
	 * 
	 * @param event
	 */
	public void reset(ActionEvent event) {
		upload = new DocumentUpload();
	}

	/**
	 * Adds a new Document inside selected folder.
	 * 
	 * @throws WidgetLoadingException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void add(Node folderNode) throws RepositoryException, IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		String author = getAuthor( ec );
		try {
			Document document = new Document( folderNode.addNode( upload.getName(), ContentRepository.getNodeName( CDRQName.AON_DOCUMENT ) ) );
			document.setAuthor( author );
			document.setEntryDate( Calendar.getInstance( ec.getRequestLocale() ) );
			document.setKeywords( upload.getKeywords() );
			document.setNowords( upload.getNowords() );
			document.setCategory( upload.getCategory() );
			document.setLanguage( upload.getLanguage() );
			document.setRoles( new String[] {"Manager"} );
			document.addContent( upload.getResource() );
			folderNode.save();
			document.checkin();
			List l = (List) model.getWrappedData();
			int index = l.size();
			l.add( index, document );
			model.setRowIndex( index );
			setSelected( document );
		} catch (AccessDeniedException e) {
			AonUtil.addWarningMessage( e.getMessage() );
			folderNode.refresh( false );
		} catch (RepositoryException e) {
			folderNode.refresh( false );
		} finally {
			upload.clear();
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
			AonUtil.addErrorMessage( "Can't delete locked document[" + selected.getName() + "]" );
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
			AonUtil.addWarningMessage( e.getMessage() );
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
//	Removes selected document from current session.
			HttpSession session = removeDocument4Download();
			session.removeAttribute( selected.getName() );
			session.setAttribute( selected.getName(), selected );
//			String js = "window.open(\"" + URL_TO_PDF+ "\", 'popup_window');"; Con URL directamente.
//			String js = "document.forms[0].action='http://localhost:8180/aon-cr/download?selected="+ selected.getName() +"';document.forms[0].submit();";
			String js = "popup_window = window.open('file.download?selected="+ selected.getName() +"','popup_window','location=0,status=1,scrollbars=0,width=200,height=50');";
			JavascriptContext.addJavascriptCall( FacesContext.getCurrentInstance(), js ); 
		} catch (RepositoryException e) {
			e.printStackTrace();
			AonUtil.addErrorMessage( e.getMessage() );
		}
	}

	/**
	 * Downloads selected document version.
	 * 
	 * @param event
	 */
	public void downloadVersion(ActionEvent event) {
		try {
//	Removes selected document from current session.
			HttpSession session = removeDocument4Download();
			session.removeAttribute( selected.getName() );
			Version version = (Version) versionHistoryModel.getRowData();
			session.setAttribute( selected.getName(), selected.getDocument4Version( version ) );
			String js = "popup_window = window.open('download?selected="+ selected.getName() +"','popup_window','location=0,status=1,scrollbars=0,width=200,height=50');";
			JavascriptContext.addJavascriptCall( FacesContext.getCurrentInstance(), js ); 
		} catch (RepositoryException e) {
			AonUtil.addErrorMessage( e.getMessage() );
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
			selected.checkin( upload.getResource() );
		} catch (RepositoryException e) {
			AonUtil.addErrorMessage( e.getMessage() );
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
			AonUtil.addErrorMessage( e.getMessage() );
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
	 * Restores selected document.
	 * 
	 * @param event
	 */
	public void restore(ActionEvent event) {
		try {
			selected.refresh( false );
			selected.restore();
		} catch (RepositoryException e) {
			AonUtil.addErrorMessage( e.getMessage() );
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
			AonUtil.addErrorMessage( e.getMessage() );
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
			AonUtil.addErrorMessage( e.getMessage() );
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
			AonUtil.addErrorMessage( e.getMessage() );
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
        model = new ListDataModel( l );
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

	/**
	 * Removes selected document used in download phase.
	 *  
	 * @return
	 * @throws RepositoryException
	 */
	private HttpSession removeDocument4Download() throws RepositoryException {
		HttpSession session = 
			(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession( false );
//	Removes selected document from current session.
		if ( session != null && this.selected != null )
			session.removeAttribute( this.selected.getName() );
		return session;
	}

}
