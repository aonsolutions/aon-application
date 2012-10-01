package com.code.aon.ui.infoweb.util;

import java.io.File;

import com.code.aon.ui.infoweb.velocity.VelocityConstants;

public class PathUtil implements VelocityConstants {

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

	public static File getDomainsPath() {
		return new File( DOMAINS_PATH );
	}
	
	public static File getDomainPath( String domain ) {
		return new File( getDomainsPath(), domain );
	}

	public static File getTempPath( String domain ) {
		return new File( TMP_PATH );
	}
	
	public static File getPreviewPath( String domain) {
		return new File( getTempPath(domain), PREVIEW_PREFIX + domain );
	}
	
}
