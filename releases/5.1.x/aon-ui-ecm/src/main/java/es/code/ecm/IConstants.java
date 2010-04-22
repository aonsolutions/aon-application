/**
 * 
 */
package es.code.ecm;

import org.apache.jackrabbit.name.QName;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 20/07/2007
 *
 */
public interface IConstants {

	static final String ECM_BUNDLE_NAME = "es.code.ecm.ui.i18n.messages";
    /** External nodetypes properties file. */
    static final String RE_REGISTER_NODETYPES_FILE_KEY = "java.resources.nodetypes";
    /** Default nodetypes properties file. */
	static final String NODETYPES_PATH = "resources/reregister-nodetypes.properties";

	static final String ES = "es";
	static final String EN = "en";

	static final String AND_LINKER = "and";
	static final String OR_LINKER = "or";
	static final String BLANK = " ";
	static final String EMPTY_STRING = "";
	static final String SEMICOLON = ":";
	static final String UNKNOWN = "unknown";
	static final String SINGLE_QUOTATION_MARK = "'";

	static String 
		MIX_REFERENCEABLE = QName.NS_MIX_PREFIX + SEMICOLON + QName.MIX_REFERENCEABLE.getLocalName(),
		JCR_ROOTVERSION = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_ROOTVERSION.getLocalName(),
		JCR_CREATED = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_CREATED.getLocalName(),
		JCR_CONTENT = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_CONTENT.getLocalName(),
		JCR_MIMETYPE = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_MIMETYPE.getLocalName(),
		JCR_ENCODING = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_ENCODING.getLocalName(),
		JCR_DATA = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_DATA.getLocalName(),
		JCR_FROZENNODE = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_FROZENNODE.getLocalName(),
		JCR_LASTMODIFIED = QName.NS_JCR_PREFIX + SEMICOLON + QName.JCR_LASTMODIFIED.getLocalName()
	;
}
