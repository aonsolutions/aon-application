/**
 * 
 */
package es.code.cdr.ui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.jcr.Value;
import javax.jcr.ValueFormatException;

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

}
