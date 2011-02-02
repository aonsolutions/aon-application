package com.code.aon.ui.infoweb.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.infoweb.controller.IInfoWebConstants;
import com.code.aon.ui.infoweb.velocity.VelocityConstants;
import com.code.aon.ui.util.AonUtil;

public class PathUtil implements VelocityConstants {

	public static File getWebInfoProperties() {
		return new File( IInfoWebConstants.WEB_INFO_PROPERTIES );
	}
	
	public static File getTemplatesPath() {
		return new File( TEMPLATE_PATH );
	}

	public static File getTemplatePath( String template) {
		return new File( getTemplatesPath(), template );
	}

	public static File getStyleDefaults( String template) {
		return new File( getTemplatePath(template), CSS_STYLE_DEFAULTS );
	}

	public static File getCssPath( String template) {
		return new File( getTemplatePath(template), CSS_PATH );
	}

	public static File getStyleTemplate( String template) {
		return new File( getCssPath(template), STYLE_TEMPLATE );
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
