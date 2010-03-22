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

import es.code.cdr.ui.controller.DocumentsList;
import es.code.cdr.ui.controller.WidgetLoadingException;
import es.code.cdr.ui.util.CDRDataModel;
import es.code.ecm.IConstants;
import es.code.ecm.InvalidStatementException;
import es.code.ecm.QueryManager;
import es.code.ecm.QueryParameters;
import es.code.ecm.SessionManager;
import es.code.ecm.Widget;
import es.code.ecm.event.WidgetEvent;
import es.code.ecm.event.WidgetListener;
import es.code.ecm.nodes.Document;
import es.code.ecm.util.ECMUtil;

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
    		Locale locale = ECMUtil.getCurrentLocale( FacesContext.getCurrentInstance() );
			bundle = ResourceBundle.getBundle( IConstants.ECM_BUNDLE_NAME, locale );
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
				ECMUtil.addErrorMessage( getBundle().getString( "aon_cdr_document_not_found" ) );
		} catch (RepositoryException e) {
			ECMUtil.addErrorMessage( getBundle().getString( "aon_cdr_document_not_found" ) );
		} catch (InvalidStatementException e) {
			ECMUtil.addErrorMessage( getBundle().getString( "aon_cdr_empty_expression" ) );
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