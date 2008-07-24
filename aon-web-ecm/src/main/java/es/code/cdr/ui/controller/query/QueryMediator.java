/**
 * 
 */
package es.code.cdr.ui.controller.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.http.HttpSession;

import es.code.cdr.IConstants;
import es.code.cdr.beans.Document;
import es.code.cdr.core.InvalidStatementException;
import es.code.cdr.core.QueryManager;
import es.code.cdr.core.QueryParameters;
import es.code.cdr.core.SessionManager;
import es.code.cdr.core.Widget;
import es.code.cdr.event.WidgetEvent;
import es.code.cdr.event.WidgetListener;
import es.code.cdr.ui.controller.DocumentsList;
import es.code.cdr.ui.controller.WidgetLoadingException;
import es.code.cdr.ui.util.CDRDataModel;
import es.code.cdr.ui.util.CDRUtils;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 17/07/2007
 *
 */
public class QueryMediator implements WidgetListener, Serializable {

	private static final long serialVersionUID = -1069531467214866277L;

	QueryMenu menu;
	QueryParameters parameters;
	DocumentsList documents;

	/** Application message bundle. */
	transient ResourceBundle bundle;
	
	/**
	 * Constructs a <code>QueryMediator</code> object.
	 * 
	 * @throws WidgetLoadingException 
	 */
	public QueryMediator() throws WidgetLoadingException {
		createWidgets();
	}

	/**
	 * @return the documents
	 */
	public DocumentsList getDocuments() {
		return documents;
	}

	/**
	 * @return the parameters
	 */
	public QueryParameters getParameters() {
		return parameters;
	}

	/**
	 * @return the menu
	 */
	public QueryMenu getMenu() {
		return menu;
	}

	/**
	 * @return the bundle
	 */
	public ResourceBundle getBundle() {
		if ( bundle == null ) {
    		Locale locale = CDRUtils.getCurrentLocale( FacesContext.getCurrentInstance() );
			bundle = ResourceBundle.getBundle( IConstants.CDR_BUNDLE_NAME, locale );
		}
		return bundle;
	}

	/**
	 * Initializes Bean.
	 * 
	 * @param event
	 */
	public void onReset(ActionEvent event) throws WidgetLoadingException {
		createWidgets();
	}

	/**
	 * Registers a user selection with this object .
	 *
	 * @param event that fired this method
	 */
	public void onDownloadSelectedDocument(ActionEvent event) {
		documents.documentSelected( event );
		documents.download( event );
	}

	/**
	 * @param event
	 */
	public void onExecute(ActionEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		String sessionId = ( (HttpSession) ctx.getExternalContext().getSession( false ) ).getId();
		List<Document> l = new ArrayList<Document>();
		try {
			Session session = SessionManager.getInstance().getHierarchyManager( sessionId ).getWorkspace().getSession();
			QueryResult result = 
				QueryManager.getInstance().execute( session, parameters, Query.XPATH );
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				Node n = it.nextNode();
				l.add( new Document( n ) );
			}
			if ( l.size() == 0 )
				CDRUtils.addErrorMessage( getBundle().getString( "aon_cdr_document_not_found" ) );
		} catch (RepositoryException e) {
			CDRUtils.addErrorMessage( getBundle().getString( "aon_cdr_document_not_found" ) );
		} catch (InvalidStatementException e) {
			CDRUtils.addErrorMessage( getBundle().getString( "aon_cdr_empty_expression" ) );
		}
		documents.setModel( new CDRDataModel( l ) );
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.event.WidgetListener#selected(es.code.cdr.ui.controller.event.WidgetEvent)
	 */
	public void selected(WidgetEvent event) {
		Widget widget = (Widget) event.getSource();
		if ( widget instanceof DocumentsList ) {
			menu.perform( widget );
		}
		if ( widget instanceof QueryMenu ) {
			documents.perform( widget );
			parameters.perform( widget );
		}
	}

	/**
	 * Create the widgets and initialize its references to them.
	 * 
	 * @throws WidgetLoadingException
	 */
	private void createWidgets() throws WidgetLoadingException{
		documents = new DocumentsList();
		documents.addWidgetListener( this );
		parameters = new QueryParameters();
		parameters.addWidgetListener( this );
		menu = new QueryMenu();
		menu.addWidgetListener( this );
	}

}