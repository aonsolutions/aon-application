package com.code.aon.common.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This Class reads a XML constants file.
 *  
 * @author Consulting & Development. Aimar Tellitu - 29-jun-2005
 * @since 1.0
 * 
 */
public class DAOConstantsReader {

    private final static Logger LOGGER = LoggerFactory.getLogger(DAOConstantsReader.class);

    private static final String BEAN_ELEMENT = "bean";
	private static final String ALIAS_ELEMENT = "alias";
	private static final String SQL_ELEMENT = "sql";
	private static final String HIBERNATE_ELEMENT = "hibernate";
	
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String PARENT_ATTRIBUTE = "parent";
	private static final String NAMES_ATTRIBUTE = "names";
	private static final String SQL_NAME_ATTRIBUTE = "sqlName";

	private static SAXParserFactory spf;

	/**
	 * Parse the stream.
	 * 
	 * @param resource
	 * @param in
	 */
	public static void parse( String resource, InputStream in ) {
		SAXParser saxParser = getSAXParser();
		SAXDAOConstantsReader reader = new SAXDAOConstantsReader();
		try {
			saxParser.parse( in, reader );
			in.close();
		} catch ( IOException ioe ) {
            LOGGER.error("Error reading " + resource, ioe);
		} catch ( SAXException saxe ) {
			LOGGER.error("Error parsing " + resource, saxe);			
		}
	}

	/**
	 * Return IDAO constants SAX reader.
	 * 
	 * @return IDAO constants SAX reader.
	 */
	private static SAXParser getSAXParser() {
		if ( spf == null ) {
			spf = SAXParserFactory.newInstance();
		}
		SAXParser saxParser = null;
		try {
			saxParser = spf.newSAXParser();
		} catch (ParserConfigurationException pce) {
			LOGGER.error("Error creating a SAX Parser", pce);
		} catch (SAXException sxe) {
			LOGGER.error("Error creating a SAX Parser", sxe);			
		}
		return saxParser;
	}

	/**
	 * IDAO Constants base class for SAX2 event handlers.
	 * 
	 * @author Consulting & Development. 
	 *
	 */
	private static class SAXDAOConstantsReader extends DefaultHandler {

        private final static Logger LOG = LoggerFactory.getLogger(SAXDAOConstantsReader.class);

        private DAOConstantsEntry entry;
		private String sqlName;		
		
		public void startDocument() throws SAXException {
            LOG.debug( "startDocument" );
		}

		public void endDocument() throws SAXException {
			LOG.debug( "endDocument" );
		}

		private void startBean( Attributes attributes ) {
			String name = attributes.getValue(CLASS_ATTRIBUTE);
			try {
				Class.forName( name );
			} catch (ClassNotFoundException e) {
				LOG.error("Error cargando la clase " +name, e);				
			}
			String parentName = attributes.getValue(PARENT_ATTRIBUTE);
			if ( (parentName != null) && (parentName.length() > 0) ) {

				try {
					Class.forName( parentName );
				} catch (ClassNotFoundException e) {
					LOG.error("Error cargando la clase padre " + parentName, e);				
				}
			}
			this.entry = new DAOConstantsEntry( name, parentName);
			this.sqlName = attributes.getValue(SQL_NAME_ATTRIBUTE);
			if ( (this.sqlName == null) || (this.sqlName.length() == 0) ) {
				this.sqlName = this.entry.getName();
			}
		}

		private void endBean() {
			DAOConstants.addBeanEntry( this.entry );
			this.entry = null;
		}

		private String[] getArray( String names, String mainName, char separator ) {
			StringTokenizer st = new StringTokenizer( names, "," );
			String[] result = new String[st.countTokens()];
			for( int i = 0; st.hasMoreTokens(); i++ ) {
				result[i] = mainName + separator + st.nextToken(); 
			}
			return result;
		}
		
		private void startAlias( Attributes attributes ) {
			String names = attributes.getValue(NAMES_ATTRIBUTE);
			String[] alias = getArray(names, this.entry.getName(), '_' );
			this.entry.setBeanAliasNames( alias );			
		}

		private void startSql( Attributes attributes ) {
			String names = attributes.getValue(NAMES_ATTRIBUTE);
			String[] sql = getArray(names, this.sqlName, '.' );
			this.entry.setSqlNames( sql );			
		}

		private void startHibernate( Attributes attributes ) {
			String names = attributes.getValue(NAMES_ATTRIBUTE);
			String[] hibernate = getArray(names, this.entry.getName(), '.' );
			this.entry.setHibernateNames( hibernate );
		}
		
		public void startElement( String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if ( BEAN_ELEMENT.equals(qName) ) {
				startBean( attributes );
			} else if ( ALIAS_ELEMENT.equals(qName) ) {
				startAlias( attributes );
			} else if ( SQL_ELEMENT.equals(qName) ) {
				startSql( attributes );
			} else if ( HIBERNATE_ELEMENT.equals(qName) ) {
				startHibernate( attributes );
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ( BEAN_ELEMENT.equals(qName) ) {
				endBean();
			}
		}

		public void warning(SAXParseException exception) throws SAXException {
			LOG.warn("warning", exception);
		}

		public void error(SAXParseException exception) throws SAXException {
			LOG.error("error", exception);
		}

		public void fatalError(SAXParseException exception) throws SAXException	{
			LOG.error("fatalError", exception);
		}

	}
	
}

