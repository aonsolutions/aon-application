/**
 * 
 */
package es.code.ecm.util;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IDomain;

import es.code.ecm.ContentRepository;
import es.code.ecm.ECMQName;
import es.code.ecm.nodes.ECMNode;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 16/07/2007
 *
 */
public class JCRUtils {

	public static final String EMPTY_STRING = "";
	public static final String SEMICOMMA = ";";
	public static final String COMMA = ",";

	/**
	 * Converts a semicomma or comma separated <code>String</code> into a <code>String[]</code>.
	 * 
	 * @param str
	 * @return
	 */
	public static final String[] string2array(String str) {
		if ( str == null || str.equals( EMPTY_STRING ) )
			return null;

		List<String> l = new ArrayList<String>();
		String delim = ( str.indexOf( SEMICOMMA ) > -1 )? SEMICOMMA: COMMA; 
		StringTokenizer st = new StringTokenizer( str, delim );
		while ( st.hasMoreElements() ) {
			String element = (String)  st.nextElement();
			l.add( element );
		}
		String[] values = new String[ l.size() ];
		for (int i = 0; i < l.size(); i++) {
			String element = l.get( i );
			values[ i ] = element;
		}
		return values;
	}

	/**
	 * Converts a <code>Value[]</code> into a <code>String</code>.
	 * 
	 * @param values
	 * @return
	 * @throws ValueFormatException
	 * @throws IllegalStateException
	 * @throws javax.jcr.RepositoryException
	 */
	public static String value2String(Value values[])
			throws ValueFormatException, IllegalStateException, javax.jcr.RepositoryException {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < values.length; i++)
			sb.append( values[i].getString() + SEMICOMMA );

		sb.deleteCharAt( sb.length() - 1 );
		return sb.toString();
	}

	/**
	 * Returns the <b>default</b> workspace name bound to <b>localhost</b> domain, otherwise return 
	 * the workspace name.
	 * 
	 * @param workspaceId
	 * @return
	 */
	public static String getDefaultWorkspaceName(String workspaceId) {
		//	Checks if workspaceId name equals to "localhost"
		return ( workspaceId.equals( IDomain.DEFAULT_DOMAIN_NAME ) )? 
				ContentRepository.DEFAULT_WORKSPACE: workspaceId;
	}

	/**
	 * Returns user UUID from requested principal, otherwise returns 'null'.
	 * 
	 * @param principal
	 * @param rootGroupNode
	 * @return
	 * @throws RepositoryException
	 */
	public static String getUserUUID(Principal principal, Session session) 
				throws RepositoryException {
		String groupRelPath = session.getWorkspace().getName() + "_" + ECMQName.AON_GROUP_SUFFIX;
		Node groupNode = session.getRootNode().getNode( groupRelPath );
		NodeIterator ni = 
			groupNode.getNodes( new AuthPrincipal(principal.getName() ).getShortName() );
		return ( ni.getSize() > 0 )? ni.nextNode().getUUID(): null;
	}

	/**
	 * Returns the type, it can be <b>aon:folder</b> or <b>aon:document</b> otherwise return null.
	 * 
	 * @param itemPath
	 * @return
	 */
	public static final String getNodeType(String itemPath) {
		if ( itemPath.indexOf( ECMNode.FOLDER_TYPE ) > -1 ) {
			return ContentRepository.getNodeName( ECMQName.AON_DOCUMENT );
		}
		if ( itemPath.indexOf( ECMNode.CATEGORY_TYPE ) > -1 ) {
			return ContentRepository.getNodeName( ECMQName.AON_CATEGORY );
		}
		return null;
	}
}
