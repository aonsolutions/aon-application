/**
 * 
 */
package es.code.ecm;

import java.text.MessageFormat;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.util.ISO8601;

import es.code.ecm.nodes.Category;


/**
 * @author Consulting & Development. Iñaki Ayerbe - 17/07/2007
 *
 */
public class QueryManager {

	static final QueryManager query = new QueryManager();

	/** Content statement message format. */
	private MessageFormat jcrContainsFunction = new MessageFormat("jcr:contains({0},{1})");

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
		String folderRootRelPath = session.getWorkspace().getName() + "_" + ECMQName.AON_FOLDER_SUFFIX;
		Query q = qm.createQuery( createStatement( session.getRootNode().getNode( folderRootRelPath ), params ), type);
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
		StringBuffer sb = new StringBuffer(  );
		boolean isEmptyStatement = true;
		sb.append( "//element(*," + ContentRepository.getNodeName( ECMQName.AON_DOCUMENT ) + ")[" );
		String statement = createStatementByContent( params );
		if ( statement != null ) {
			sb.append( statement );
			isEmptyStatement = false;
		}
		boolean isEmptyProperties = fillStatementWithAdvancedParams( params, sb, isEmptyStatement );
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
		}
		if ( statement.length() > 0 ) {
			statement = IConstants.SINGLE_QUOTATION_MARK + escapeContains( statement ) + IConstants.SINGLE_QUOTATION_MARK;
			return jcrContainsFunction.format( new String[] { IConstants.JCR_CONTENT, statement } );
		}
		return null;
	}

	private boolean fillStatementWithAdvancedParams(QueryParameters params, StringBuffer sb, boolean isEmptyLinker) {
		boolean isEmpty = true;
		if ( params.getTitle() != null && !params.getTitle().equals( IConstants.EMPTY_STRING ) ) {
			String statement = 
				IConstants.SINGLE_QUOTATION_MARK + params.getTitle() + IConstants.SINGLE_QUOTATION_MARK;
			String nodeType = ContentRepository.getNodeName( ECMQName.AON_NAME );
			sb.append( getLinker( isEmptyLinker ) + jcrContainsFunction.format( new String[] { "@" + nodeType, statement } ) );
			isEmptyLinker = isEmpty = false;
		}
		if ( params.getKeywords() != null && !params.getKeywords().equals( IConstants.EMPTY_STRING ) ) {
			String statement = 
				IConstants.SINGLE_QUOTATION_MARK + params.getKeywords() + IConstants.SINGLE_QUOTATION_MARK;
			String nodeType = ContentRepository.getNodeName( ECMQName.AON_KEYWORDS );
			sb.append( getLinker( isEmptyLinker ) + jcrContainsFunction.format( new String[] { "@" + nodeType, statement } ) );
			isEmptyLinker = isEmpty = false;
		}
		if ( params.getCategory() != null && !params.getCategory().equals( IConstants.UNKNOWN ) ) {
			String statement = 
				IConstants.SINGLE_QUOTATION_MARK + params.getCategory() + IConstants.SINGLE_QUOTATION_MARK;
			String nodeType = ContentRepository.getNodeName( ECMQName.AON_CATEGORYNAME );
			sb.append( getLinker( isEmptyLinker ) + jcrContainsFunction.format( new String[] { "@" + nodeType, statement } ) );
			isEmptyLinker = isEmpty = false;
		}
		if ( params.getLanguage() != null && !params.getLanguage().equals( IConstants.UNKNOWN ) ) {
			String statement = 
				IConstants.SINGLE_QUOTATION_MARK + params.getLanguage() + IConstants.SINGLE_QUOTATION_MARK;
			String nodeType = ContentRepository.getNodeName( ECMQName.AON_LANGUAGE );
			sb.append( getLinker( isEmptyLinker ) + jcrContainsFunction.format( new String[] { "@" + nodeType, statement } ) );
			isEmptyLinker = isEmpty = false;
		}
		if ( params.getFileformat() != null && !params.getFileformat().equals( IConstants.UNKNOWN ) ) {
			String statement = 
				IConstants.SINGLE_QUOTATION_MARK + params.getFileformat() + IConstants.SINGLE_QUOTATION_MARK;
			String nodeType = IConstants.JCR_CONTENT + "/" + IConstants.JCR_MIMETYPE;
			sb.append( getLinker( isEmptyLinker ) + jcrContainsFunction.format( new String[] { "@" + nodeType, statement } ) );
			isEmptyLinker = isEmpty = false;
		}
		if ( params.getPublishDate() != null ) {
			Calendar c = Calendar.getInstance();
			c.setTime( params.getPublishDate() );
			sb.append( getLinker( isEmptyLinker ) + "@" + IConstants.JCR_CREATED + " == xs:dateTime('" + ISO8601.format( c ) + "')" );
			isEmptyLinker = isEmpty = false;
		}
		return isEmpty;
	}

	private String escapeContains(String str) {
		String ret = str.replace("\\", "\\\\");
		ret = ret.replace("'", "\\'");
		ret = ret.replace("-", "\\-");
		ret = ret.replace("\"", "\\\"");
		ret = ret.replace("[", "\\[");
		ret = ret.replace("]", "\\]");
		ret = escapeXPath(ret);
		return ret;
	}

	private String escapeXPath(String str) {
		String ret = str.replace("'", "''");
		return ret;
	}

	private String getLinker(boolean isEmpty) {
		return (isEmpty)? IConstants.EMPTY_STRING: IConstants.BLANK + IConstants.AND_LINKER + IConstants.BLANK;
	}
}
