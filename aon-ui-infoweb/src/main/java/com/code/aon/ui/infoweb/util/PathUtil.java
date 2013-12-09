package com.code.aon.ui.infoweb.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.infoweb.velocity.VelocityConstants;
import com.code.aon.ui.util.AonUtil;

public class PathUtil implements VelocityConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PathUtil.class.getName());

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

	private static String getDomainName() {
		DomainSwitcher dw = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) bean.get(dw.getDomainId());
			return domain.getName();
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
		return AonUtil.getAuthPrincipal().getDomain();
	}
	
	public static File getPreviewPath() {
		String domain = getDomainName();
		return new File( getTempPath(), PREVIEW_PREFIX + domain );
	}
	
}
