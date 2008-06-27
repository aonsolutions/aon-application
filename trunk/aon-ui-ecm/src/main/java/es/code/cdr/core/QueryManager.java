/**
 * 
 */
package es.code.cdr.core;

import java.text.MessageFormat;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.value.DateValue;

import es.code.cdr.CDRQName;
import es.code.cdr.IConstants;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 17/07/2007
 *
 */
public class QueryManager {

	static final QueryManager query = new QueryManager();

	/** Content statement message format. */
	private MessageFormat jcrContainsFunction = new MessageFormat("jcr:contains({0},\"{1}\")");

	/**
	 * Returns the singleton instance of this class.
	 * 
	 * @return
	 */
	public static final QueryManager getInstance() {
		return query;
	}

	/**
	 * Constructs a private <code>QueryManager</code> object.
	 */
	private QueryManager() {
	}

	/**
     * Executes this query and returns a <code>{@link QueryResult}</code>.
     * 
	 * @param session
	 * @param params
	 * @param type
	 * @return
	 * @throws InvalidStatementException
	 * @throws RepositoryException
	 */
	public QueryResult execute(Session session, QueryParameters params, String type) 
				throws InvalidStatementException, RepositoryException {
		Workspace workspace = session.getWorkspace();
		javax.jcr.query.QueryManager qm = workspace.getQueryManager();
//	el segundo parametro del createQuery es el tipo de lenguaje de consulta que se va a usar 
		javax.jcr.query.Query q = qm.createQuery( createStatement( null, params ), type);
		return q.execute();
	}

	/**
	 * Creates a findByContent statement.
	 * 
	 * @param root
	 * @param params
	 * @return
	 * @throws InvalidStatementException
	 * @throws RepositoryException
	 */
	private String createStatement(Node root, QueryParameters params) 
			throws InvalidStatementException, RepositoryException {
		StringBuffer sb = new StringBuffer();
		String defaultRootNode = ContentRepository.getNodeName( CDRQName.AON_CDR );
		sb.append( (root == null)? defaultRootNode: root.getPath() );		
		String linker = IConstants.EMPTY_STRING;
		boolean isEmptyStatement = true;
		String statement = createStatementByContent( params );
		if ( statement != null ) { 
			sb.append( "//element(*," + ContentRepository.getNodeName( CDRQName.AON_CONTENT ) + ")/jcr:data[" + statement );
			linker = IConstants.BLANK + IConstants.AND_LINKER + IConstants.BLANK;
			isEmptyStatement = false;
		} else {
			sb.append( "//*[" );
		}
		boolean isEmptyProperties = fillStatementWithAdvancedParams( params, sb, linker );
//	Order by score.
        sb.append("] order by @jcr:score descending");
        if ( isEmptyStatement && isEmptyProperties )
        	throw new InvalidStatementException();
        return sb.toString();
	}

	private String createStatementByContent(QueryParameters params) {
		String statement = params.getBycontent();
		if ( statement == null ) {
			statement = params.getAdvancedSearchContent();
			if ( statement != null && statement.length() > 0 )
//	Search inside document content.
				return jcrContainsFunction.format( new String[] { statement } );
		} else {
			return jcrContainsFunction.format( new String[] { ".", statement } );
		}
		return null;
	}

	private boolean fillStatementWithAdvancedParams(QueryParameters params, StringBuffer sb, String linker) {
//	Search document properties.
		boolean isEmpty = true;
		if ( params.getLanguage() != null && !params.getLanguage().equals( IConstants.UNKNOWN ) ) {
			String nodeType = ContentRepository.getNodeName( CDRQName.AON_LANGUAGE );
			String l = 
				jcrContainsFunction.format( new String[] { "@" + nodeType, params.getLanguage() } );
			sb.append( linker + l );
			linker = IConstants.BLANK + IConstants.AND_LINKER + IConstants.BLANK;
			isEmpty = false;
		}
		if ( params.getFileformat() != null && !params.getFileformat().equals( IConstants.UNKNOWN ) ) {
			String nodeType = ContentRepository.getNodeName( CDRQName.AON_CONTENT ) + "/" + IConstants.JCR_MIMETYPE;
			String l = 
				jcrContainsFunction.format( new String[] { "@" + nodeType, params.getFileformat() } );
			sb.append( linker + l );
			linker = IConstants.BLANK + IConstants.AND_LINKER + IConstants.BLANK;
			isEmpty = false;
		}			
		if ( params.getPublishDate() != null ) {
			String nodeType = ContentRepository.getNodeName( CDRQName.AON_ENTRYDATE );
			Calendar c = Calendar.getInstance();
			c.setTime( params.getPublishDate() );
			c.set( Calendar.HOUR_OF_DAY, 0 );
			c.set( Calendar.MINUTE, 0 );
			c.set( Calendar.SECOND, 0 );
			String l = 
				jcrContainsFunction.format( new Object[] { "@" + nodeType, new DateValue( c ) } );
			sb.append( linker + l );
			isEmpty = false;
		}
		return isEmpty;
	}
}
