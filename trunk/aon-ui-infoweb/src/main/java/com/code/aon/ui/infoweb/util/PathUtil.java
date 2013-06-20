package com.code.aon.ui.infoweb.util;

import java.io.File;

import com.code.aon.ui.infoweb.velocity.VelocityConstants;
import com.code.aon.ui.util.AonUtil;

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

	public static File getTempPath() {
		return new File( TMP_PATH );
	}
	
	public static File getPreviewPath() {
		String domain = AonUtil.getAuthPrincipal().getDomain();
		return new File( getTempPath(), PREVIEW_PREFIX + domain );
	}
	
}
