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

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.cdr.core.SessionManager;
import es.code.cdr.core.Widget;
import es.code.cdr.event.WidgetEvent;
import es.code.cdr.event.WidgetListener;

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
	CDRMenu menu;
	InfoTabbedPane info;
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
	 */
	public String getUserName() {
		return SessionManager.getInstance().getUserName();
	}

	/**
	 * @param event
	 */
	public void onNewFolder(ActionEvent event) {
		menu.setShowFolderModalPanel( true );
		try {
			folders.reset( event );
		} catch (RepositoryException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

	/**
	 * @param event
	 */
	public void onNewDocument(ActionEvent event) {
		menu.setShowDocumentModalPanel( true );
		documents.reset( event );
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
	 */
	public void onCheckin(ActionEvent event) {
		try {
			documents.getUpload().setSelected( 0 );
			documents.checkin( event );
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see es.code.cdr.ui.controller.event.WidgetListener#selected(es.code.cdr.ui.controller.event.WidgetEvent)
	 */
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
		menu = new CDRMenu();
		menu.addWidgetListener( this );
		info = new InfoTabbedPane();
		info.addWidgetListener( this );
		folders.setRootTreeNodeSelected();
	}

}
