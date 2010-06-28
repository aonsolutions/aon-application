package com.code.aon.ui.publisher.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.util.AonUtil;

public class PathUtil {
	
	private static final String PUBLISHER_PROPERTIES = "/home/COMMON-RESOURCES/aon-publisher/ftp.properties";	
	private static final String DOMAINS_PATH = "/home/DOMAINS";
	private static final String WEBSITE_PATH = "WEBSITES";
	private static final String PREVIEW_PREFIX = "preview.";
	
	public static File getPublisherProperties() {
		return new File( PUBLISHER_PROPERTIES );
	}
	
	public static String getDomainSuffix( String domain ) {
		String[] parts = StringUtils.split( domain, "." );
		if ( parts.length > 2 ) {
			return StringUtils.join(parts, ".", 1, parts.length);
		}
		return domain;
	}	

	public static File getDomainsPath() {
		return new File( DOMAINS_PATH );
	}
	
	public static File getDomainPath( String domain ) {
		return new File( getDomainsPath(), domain );
	}

	public static File getWebSitesPath( String domain ) {
		return new File( getDomainPath(domain), WEBSITE_PATH );
	}
	
	public static File getPreviewPath( String domain) {
		return new File( getWebSitesPath(domain), PREVIEW_PREFIX + domain );
	}

	public static boolean isReadableDirectory( File directory ) {
		if (!directory.exists()) {
			AonUtil.addErrorMessage("No se ha encontrado el directorio '" + directory + "'");
			return false;
		}
		if (!directory.canRead()) {
			AonUtil.addErrorMessage("El directorio '" + directory + "' no tiene permiso de lectura");
			return false;
		}		
		return true;
	}
	
}
