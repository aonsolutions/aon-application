package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonArrayUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public enum MimeType implements Serializable {

    JPEG ("image/jpeg", "jpg", "image/pjpeg" ),
    GIF ("image/gif", "gif"),
    ICS ("text/calendar", "ics"),
    TXT ("text/plain", "txt"),
    HTML ("text/html", "html"),
    XML ("text/xml", "xml"),
    PNG ("image/png", "png"),
    BMP ("image/bmp", "bmp", "image/x-ms-bmp"),
    TIFF ("image/tiff", "tif"),
    ICO ("image/x-icon", "ico"),
    AVI ("video/x-msvideo", "avi"),
    MPEG ("video/mpeg", "mpg"),
    QUICKTIME ("video/quicktime", "mov"),
    MP3 ("audio/mp3", "mp3"),
    WAV ("audio/x-wav", "wav"),
    MID ("audio/mid", "mid"),
    RTF ("text/rtf", "rtf"),
    MS_WORD ("application/msword", "doc"),
    MS_EXCEL ("application/vnd.ms-excel", "xls"),
    MS_POWER_POINT ("application/vnd.ms-powerpoint", "ppt"),
    STAR_OFFICE_TEXT ("application/vnd.oasis.opendocument.text", "odt"),
    STAR_OFFICE_SPREADSHEET ("application/vnd.oasis.opendocument.spreadsheet", "ods"),
    PDF ("application/pdf", "pdf"),
    JAVASCRIPT ("text/javascript", "js"),
    ZIP ("application/zip", "zip"),
    CSS ("text/css", "css"),
    MS_WORD_2007 ("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    MS_EXCEL_2007 ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    MS_POWER_POINT_2007 ("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),
    SIGNED_PDF ("application/pdf", "pdf"),
    CSV ("text/csv", "csv"),
    RSS ("application/rss+xml", "rss"),
    OCTECT_STREAM ("application/octet-stream",""),
    XSIG ("text/xml", "xsig"),
    SIGNED_FACTURAE ("text/xml", "xml"),
    JSON("application/json","js")
    ;
    
    
	private String name;
	private String[] aliases;
	private String extension;

	private MimeType(String name, String extension, String ... aliases ) {
		this.name = name;
		this.extension = extension;
		if (! AonArrayUtils.isEmpty(aliases) ) {
			this.aliases = aliases;	
		}
	}
	
	
	public static MimeType get(String type) {
    	for( MimeType mt : MimeType.values() ) {
    		if (mt.getName().equals(type) ) {
    			return mt;
    		} else {    			
    			if ( mt.getAliases() != null ) {
    				
    				for( String alias : mt.getAliases() ) {
    					if  (AonStringUtils.equals(alias, type)) {
    		    			return mt;
    					}
    				}
    			}
    		}
    	}
    	return OCTECT_STREAM;
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
    
    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String[] getAliases() {
		return aliases;
	}
    
    public Byte value(){
    	return (byte) this.ordinal();
    }

    public Boolean isOffice(){
    	return this.equals(MS_EXCEL) || this.equals(MS_EXCEL_2007)
    			|| this.equals(MS_POWER_POINT) || this.equals(MS_POWER_POINT_2007)
    			|| this.equals(MS_WORD) || this.equals(MS_WORD_2007);
    }
    
    public boolean isImage(){
    	return this == JPEG 
			|| this == GIF
    		|| this == PNG 
    		|| this == BMP
			|| this == TIFF;
    }
	
    public boolean isPDF(){
    	return this == PDF || this == SIGNED_PDF;
    }
    
	public static MimeType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static MimeType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= MimeType.values().length) return null;
		return MimeType.values()[i];
	}
	public static MimeType safeValueFromContenType( String name ) {
		if (AonStringUtils.isBlank(name)) return null;
		for (MimeType mimeType : MimeType.values() ) {
			if (mimeType.getName().equals(name)) {
				return mimeType;
			}
		}
		return null;
	}
    
}