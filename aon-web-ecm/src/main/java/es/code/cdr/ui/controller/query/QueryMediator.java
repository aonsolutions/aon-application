/**
 * 
 */
package es.code.cdr.ui.controller.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.http.HttpSession;

import com.code.aon.ui.util.AonUtil;

import es.code.cdr.beans.Document;
import es.code.cdr.core.InvalidStatementException;
import es.code.cdr.core.QueryManager;
import es.code.cdr.core.QueryParameters;
import es.code.cdr.core.SessionManager;
import es.code.cdr.core.Widget;
import es.code.cdr.core.event.WidgetEvent;
import es.code.cdr.core.event.WidgetListener;
import es.code.cdr.ui.controller.DocumentsList;
import es.code.cdr.ui.controller.WidgetLoadingException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 17/07/2007
 *
 */
public class QueryMediator implements WidgetListener {

	QueryMenu menu;
	QueryParameters parameters;
	DocumentsList documents;

	/** Application message bundle. */
	ResourceBundle bundle;
	
	/**
	 * Constructs a <code>QueryMediator</code> object.
	 * 
	 * @throws WidgetLoadingException 
	 */
	public QueryMediator() throws WidgetLoadingException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Locale locale = ctx.getExternalContext().getRequestLocale();
		bundle = ResourceBundle.getBundle( ctx.getApplication().getMessageBundle(), locale );
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
		return bundle;
	}

	/**
	 * @param event
	 */
	public void onExecute(ActionEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		String sessionId = ( (HttpSession) ctx.getExternalContext().getSession( false ) ).getId();
		List<Document> l = new ArrayList<Document>();
		try {
			Session session = SessionManager.getInstance().get( sessionId );
			QueryResult result = 
				QueryManager.getInstance().execute( session, parameters, Query.XPATH );
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				Node n = it.nextNode();
				l.add( new Document( n ) );
			}
		} catch (RepositoryException e) {
			AonUtil.addErrorMessage( bundle.getString( "aon_cdr_document_not_found" ) );
		} catch (InvalidStatementException e) {
			AonUtil.addErrorMessage( bundle.getString( "aon_cdr_empty_expression" ) );
		}
		documents.setModel( new ListDataModel( l ) );
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