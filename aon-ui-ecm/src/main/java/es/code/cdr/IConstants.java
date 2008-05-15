/**
 * 
 */
package es.code.cdr;

import org.apache.jackrabbit.name.QName;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 20/07/2007
 *
 */
public interface IConstants {

	static final String CDR_BUNDLE_NAME = "es.code.cdr.ui.i18n.messages";
    /** External categories properties file. */
    static final String CATEGORIES_PATH_FILE_KEY = "java.resources.categories";
    /** Default categories properties file. */
	static final String CATEGORIES_PATH = "resources/categories.properties";

	static final String ES = "es";
	static final String EN = "en";

	static final String AND_LINKER = "and";
	static final String OR_LINKER = "or";
	static final String BLANK = " ";
	static final String EMPTY_STRING = "";
	static final String SEMICOLON = ":";
	static final String UNKNOWN = "unknown";

	static String 
		JCR_ROOTVERSION = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_ROOTVERSION.getLocalName(),
		JCR_MIMETYPE = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_MIMETYPE.getLocalName(),
		JCR_ENCODING = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_ENCODING.getLocalName(),
		JCR_DATA = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_DATA.getLocalName(),
		JCR_FROZENNODE = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_FROZENNODE.getLocalName(),
		JCR_LASTMODIFIED = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_LASTMODIFIED.getLocalName()
	;
}
