/**
 * 
 */
package es.code.cdr.ui.controller;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.ecm.ECMQName;
import es.code.ecm.HierarchyManager;
import es.code.ecm.SessionManager;
import es.code.ecm.Widget;
import es.code.ecm.event.WidgetEvent;
import es.code.ecm.event.WidgetListener;
import es.code.ecm.nodes.User;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 10/07/2007
 *
 */
public class CDRMediator implements WidgetListener, Serializable {

	private static final long serialVersionUID = 7585804319800307314L;

	/** CDRMediator class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( CDRMediator.class.getName() );

	FoldersTree folders;
	DocumentsList documents;
	CategoryList categories;
	UserList users;
	CDRMenu menu;
	InfoTabbedPane info;
	User user;
	/** Application message bundle. */
	transient ResourceBundle bundle;
	

	/**
	 * Constructs a <code>CDRMediator</code> object.
	 * 
	 * @throws WidgetLoadingException 
	 */
	public CDRMediator() throws WidgetLoadingException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Locale locale = ctx.getExternalContext().getRequestLocale();
		bundle = ResourceBundle.getBundle( ctx.getApplication().getMessageBundle(), locale );
		createWidgets();
	}

	/**
	 * @return
	 */
	public String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
		return formatter.format( new Date() ).toUpperCase();
	}

	/**
	 * @return the documents
	 */
	public DocumentsList getDocuments() {
		return documents;
	}

	/**
	 * @return the folders
	 */
	public FoldersTree getFolders() {
		return folders;
	}

	/**
	 * @return the categories
	 */
	public CategoryList getCategories() {
		return categories;
	}

	/**
	 * @return the users
	 */
	public UserList getUsers() {
		return users;
	}

	/**
	 * @return the menu
	 */
	public CDRMenu getMenu() {
		return menu;
	}

	/**
	 * @return the bundle
	 */
	public ResourceBundle getBundle() {
		return bundle;
	}

	/**
	 * Returns the authenticated user name.
	 * 
	 * @return
	 * @throws RepositoryException 
	 */
	public String getUserName() throws RepositoryException {
		if ( this.user == null ) {
			HttpSession session = 
				(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession( false );
			HierarchyManager hm = SessionManager.getInstance().getHierarchyManager( session.getId() );
			String groupRelPath = hm.getWorkspace().getName() + "_" + ECMQName.AON_GROUP_SUFFIX;
			NodeIterator ni = hm.getRootNode().getNode( groupRelPath ).getNodes( hm.getUserId() );
			if ( ni.getSize() > 0 ) {
				this.user = new User( ni.nextNode() );
			}
		}
        return (this.user != null)? this.user.getName(): "";
	}

	/**
	 * Returns the authenticated user name.
	 * 
	 * @return
	 * @throws RepositoryException 
	 */
	public String getDomainName() throws RepositoryException {
		if ( this.user == null ) {
			HttpSession session = 
				(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession( false );
			HierarchyManager hm = SessionManager.getInstance().getHierarchyManager( session.getId() );
			String groupRelPath = hm.getWorkspace().getName() + "_" + ECMQName.AON_GROUP_SUFFIX;
			NodeIterator ni = hm.getRootNode().getNode( groupRelPath ).getNodes( hm.getUserId() );
			if ( ni.getSize() > 0 ) {
				this.user = new User( ni.nextNode() );
			}
		}
        return (this.user != null)? this.user.getDomain(): "";
	}

	/**
	 * @param event
	 */
	public void onNewFolder(ActionEvent event) {
		menu.setShowFolderModalPanel( true );
		folders.setName( null );
	}

	/**
	 * @param event
	 */
	public void onNewDocument(ActionEvent event) {
		menu.setShowDocumentModalPanel( true );
		documents.reset( event );
	}

	public void onLogout(ActionEvent event) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ( (HttpSession) ctx.getExternalContext().getSession( false ) ).invalidate();
	}

	/**
	 * @param event
	 */
	public void onAddDocument(ActionEvent event) {
		try {
			documents.add( folders.getSelectedNode().getNode() );
		} catch (RepositoryException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

	/**
	 * @param event
	 */
	public void onStartCheckin(ActionEvent event) {
		menu.setShowCheckinModalPanel( true );
		documents.reset( event );
	}

	/**
	 * @param event
	 * @throws RepositoryException 
	 */
	public void onCheckin(ActionEvent event) throws RepositoryException {
		try {
			documents.getUpload().setSelected( 0 );
			documents.checkin( event );
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

    /**
     * Check if the user has <b>Administrator</b> role.
     * 
     * @return
     */
    public boolean isAdministrator() {
    	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    	return ec.isUserInRole( "Administrator" );
    }

    /**
     * Check if the user has <b>Manager</b> or <b>ContentManagement</b> role.
     * 
     * @return
     */
    public boolean isContentManagement() {
    	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    	return ec.isUserInRole( "Manager" ) || ec.isUserInRole( "ContentManagement" );
    }

// *********************************** WidgetListener methods implementation **********************************
	public void selected(WidgetEvent event) {
		Widget widget = (Widget) event.getSource();
		if ( widget instanceof FoldersTree ) {
			documents.perform( widget );
		}
		if ( widget instanceof DocumentsList ) {
			menu.perform( widget );
			info.perform( widget );
		}
	}
// ******************************** End of WidgetListener methods implementation ******************************

	/**
	 * Create the widgets and initialize its references to them.
	 * 
	 * @throws WidgetLoadingException
	 */
	private void createWidgets() throws WidgetLoadingException{
		folders = new FoldersTree();
		folders.addWidgetListener( this );
		documents = new DocumentsList();
		documents.addWidgetListener( this );
		categories = new CategoryList();
		categories.addWidgetListener( this );
		users = new UserList();
		users.addWidgetListener( this );
		menu = new CDRMenu();
		menu.addWidgetListener( this );
		info = new InfoTabbedPane();
		info.addWidgetListener( this );
		folders.setRootTreeNodeSelected();
	}

}
