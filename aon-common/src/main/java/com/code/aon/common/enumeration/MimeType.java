package com.code.aon.common.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.ArrayUtils;


/**
 * Enumeration MIME type.
 * 
 * @author Consulting & Development. Aimar Tellitu - 29-jul-2005
 * @version 1.0
 * @since 1.0
 * 
 */
public enum MimeType implements IResourceable {

    /**
     * JPEG file MIME Type.
     */
    MIME_JPEG ("image/jpeg", "jpg", "image/pjpeg" ),

    /**
     * GIF file MIME Type.
     */
    MIME_GIF ("image/gif", "gif"),

    /**
     * ICS (ICalendar) file MIME Type.
     */
    MIME_ICS ("text/calendar", "ics"),
    
    /**
     * Text Plain file MIME Type.
     */
    MIME_TXT ("text/plain", "txt"),
    
    /**
     * HTML file MIME Type.
     */
    MIME_HTML ("text/html", "html"),
    
    /**
     * XML file MIME Type.
     */
    MIME_XML ("text/xml", "xml"),

    /**
     * PNG file MIME Type.
     */
    MIME_PNG ("image/png", "png"),

    /**
     * BMP file MIME Type.
     */
    MIME_BMP ("image/bmp", "bmp", "image/x-ms-bmp"),

    /**
     * TIFF file MIME Type.
     */
    MIME_TIFF ("image/tiff", "tif"),

    /**
     * ICO file MIME Type.
     */
    MIME_ICO ("image/x-icon", "ico"),

    /**
     * AVI file MIME Type.
     */
    MIME_AVI ("video/x-msvideo", "avi"),

    /**
     * MPEG file MIME Type.
     */
    MIME_MPEG ("video/mpeg", "mpg"),

    /**
     * Quicktime file MIME Type.
     */
    MIME_QUICKTIME ("video/quicktime", "mov"),

    /**
     * MP3 file MIME Type.
     */
    MIME_MP3 ("audio/mp3", "mp3"),

    /**
     * WAV file MIME Type.
     */
    MIME_WAV ("audio/x-wav", "wav"),

    /**
     * MID file MIME Type.
     */
    MIME_MID ("audio/mid", "mid"),

    /**
     * RTF file MIME Type.
     */
    MIME_RTF ("text/rtf", "rtf"),

    /**
     * MS Word file MIME Type.
     */
    MIME_MS_WORD ("application/msword", "doc"),

    /**
     * MS Excel file MIME Type.
     */
    MIME_MS_EXCEL ("application/vnd.ms-excel", "xls"),
    
    /**
     * MS Power Point file MIME Type.
     */
    MIME_MS_POWER_POINT ("application/vnd.ms-powerpoint", "ppt"),

    /**
     * Star Office OpenDocument(Ver 2) Text Document file MIME Type.
     */
    MIME_STAR_OFFICE_TEXT ("application/vnd.oasis.opendocument.text", "odt"),

    /**
     * Star Office OpenDocument(Ver 2) Spreadsheet file MIME Type.
     */
    MIME_STAR_OFFICE_SPREADSHEET ("application/vnd.oasis.opendocument.spreadsheet", "ods"),
    
    /**
     * PDF file MIME Type.
     */
    MIME_PDF ("application/pdf", "pdf"),

    /**
     * JavaScript file MIME Type.
     */
    MIME_JAVASCRIPT ("text/javascript", "js"),

    /**
     * ZIP file MIME Type.
     */
    MIME_ZIP ("application/zip", "zip"),

    /**
     * CSS file MIME Type.
     */
    MIME_CSS ("text/css", "css"),

    /**
     * MS Word 2007 XML Document file MIME Type.
     */
    MIME_MS_WORD_2007 ("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    
    /**    
    * MS Excel 2007 XML Workbook file MIME Type.
    */
   MIME_MS_EXCEL_2007 ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
   
   /**
    * MS PowerPoint 2007 XML Presentation file MIME Type.
    */
   MIME_MS_POWER_POINT_2007 ("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),
    
   /**
    * Signed PDF file MIME Type.
    */
   MIME_SIGNED_PDF ("application/pdf", "pdf"),
   
   /**
    * CSV file MIME Type.
    */
   MIME_CSV ("text/csv", "csv"),

    /**
     * RSS file MIME Type.
     */
    MIME_RSS ("application/rss+xml", "rss"),
    
    /**
     * Without extension file MIME Type
     */
    MIME_OCTECT_STREAM ("application/octet-stream",""),
    
    /**
     * Facturae for FACe file MIME Type.
     */
    MIME_XSIG ("text/xml", "xsig"),

    /**
     * Signed Facturae file MIME Type.
     */
    MIME_SIGNED_FACTURAE ("text/xml", "xml"),
    
    /**
     * SVG file MIME Type.
     */
    MIME_SVG ("image/svg+xml", "svg")
    
    ;
    
    
    /**
     * Messages key prefix. 
     */
	private static final String MSG_KEY_PREFIX = "aon_enum_mimetype_";

    /**
     * MIME type name.
     */
	private String name;

    /**
     * MIME type aliases.
     */
	private String[] aliases;
	
    /**
     * MIME type extension.
     */
	private String extension;

    /**
     * Constructor.
     * 
     * @param name
     * @param extension
     */
	private MimeType(String name, String extension, String ... aliases ) {
		this.name = name;
		this.extension = extension;
		if (! ArrayUtils.isEmpty(aliases) ) {
			this.aliases = aliases;	
		}
	}
	
    /**
     * Test if its the same MIME type.
     *
     * @param type the type
     * @return true, if successful
     */
    public boolean match( String type ) {
    	if (! name.equals(type) ) {
    		if ( aliases != null ) {
    			return ArrayUtils.contains(aliases, type);
    		}
    		return false;
    	}
    	return true;
    }

    /**
     * Return the MIME type.
     * 
     * @param type MIME type identifier.
     * @return The MIME type.
     */
	public static MimeType get(String type) {
    	for( MimeType mimeType : MimeType.values() ) {
    		if ( mimeType.match(type) ) {
    			return mimeType;
    		}
    	}
    	return null;
	}

    /**
     * Return the MIME type.
     * 
     * @param extension
     * @return The MIME type.
     */
    public static MimeType getByExtension(String extension) {
    	String value = extension.toLowerCase();
    	for( MimeType mimeType : MimeType.values() ) {
    		if ( mimeType.extension.equals(value) ) {
    			return mimeType;
    		}
    	}
    	return null;
    }
    
    /**
     * Return MIME type name.
     * 
     * @return The MIME type name.
     */
    public String getName() {
        return name;
    }

    /**
     * Return MIME type extension.
     * 
     * @return The MIME type extension.
     */
    public String getExtension() {
        return extension;
    }
    
    /**
     * Gets the aliases.
     *
     * @return the aliases
     */
    public String[] getAliases() {
		return aliases;
	}

	@Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}