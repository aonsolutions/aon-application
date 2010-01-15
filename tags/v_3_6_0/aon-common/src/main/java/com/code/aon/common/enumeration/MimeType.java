package com.code.aon.common.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;


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
    MIME_JPEG ("image/jpeg", "jpg"),

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
    MIME_BMP ("image/bmp", "bmp"),

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
    MIME_MP3 ("audio/mpeg", "mp3"),

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
   MIME_SIGNED_PDF ("application/pdf", "pdf");
    
    /**
     * IE for JPEG file MIME Type.
     */
	public static final String MIME_IE_JPEG = "image/pjpeg";

    /**
     * Messages file base path.
     */
	private static final String BASE_NAME = "com.code.aon.common.i18n.messages";

    /**
     * Messages key prefix. 
     */
	private static final String MSG_KEY_PREFIX = "aon_enum_mimetype_";

    /**
     * MIME type name.
     */
	private String name;

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
	private MimeType(String name, String extension) {
		this.name = name;
		this.extension = extension;
	}

    /**
     * Return the MIME type.
     * 
     * @param b MIME type identifier.
     * @return The MIME type.
     */
	public static MimeType get(String b) {
    	for( MimeType mimeType : MimeType.values() ) {
    		if ( mimeType.name.equals(b) ) {
    			return mimeType;
    		}
    	}
    	if ( MIME_IE_JPEG.equals(b) ) {
    		return MIME_JPEG;
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

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}