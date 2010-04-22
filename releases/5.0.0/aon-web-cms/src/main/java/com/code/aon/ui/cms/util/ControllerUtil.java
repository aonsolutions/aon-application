package com.code.aon.ui.cms.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.cms.Config;
import com.code.aon.cms.ConfigDetail;
import com.code.aon.cms.Language;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.controller.ConfigController;
import com.code.aon.ui.cms.controller.I18NController;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.util.AonUtil;

public class ControllerUtil implements Constants, ICMSConstants {

	public static I18NController getI18NController() {
		return (I18NController) AonUtil.getRegisteredBean(I18N);
	}
	
	public static Language getCurrentLanguage() {
		I18NController i18n = (I18NController) AonUtil.getRegisteredBean(I18N);
		return i18n.getCurrentLanguage();
	}	

	public static ConfigController getConfigController() {
		return (ConfigController) AonUtil.getRegisteredBean(CONFIG);
	}
	
	public static Config getCurrentConfig() {
		ConfigController config = (ConfigController)AonUtil.getRegisteredBean(CONFIG);
		return config.getCurrentConfig();
	}

	public static ConfigDetail getCurrentConfigDetail() {
		ConfigController config = (ConfigController)AonUtil.getRegisteredBean(CONFIG);
		return config.getCurrentConfigDetail();
	}	

	public static File getDomainPath() {
		return new File( DOMAINS_PATH, getCurrentConfig().getDomain() );
	}

	private static File getApplicationResourcePath() {
		File resourcePath = new File( getDomainPath(), RESOURCE_PATH );
		return new File( resourcePath, APLICATION_NAME );
	}	
	
	public static File getTemporalPath() {
		return new File( getApplicationResourcePath(), TEMPORAL_PATH );
	}	

	public static File getTemplatePath() {
		return new File( getApplicationResourcePath(), TEMPLATE_PATH );
	}	

	public static File getCurrentTemplatePath() {
		return new File( getTemplatePath(), getCurrentConfig().getTemplate() );
	}	
	
	public static File getCurrentVmTemplatePath() {
		return new File( getCurrentTemplatePath(), VM_PATH );
	}	

	public static File getBundlePath() {
		return new File( getCurrentTemplatePath(), BUNDLE_PATH );
	}	

	public static File getCssTemplatePath() {
		return new File( getCurrentTemplatePath(), CSS_PATH );
	}	

	public static File getJsTemplatePath() {
		return new File( getCurrentTemplatePath(), JS_PATH );
	}	

	public static File getWebSitePath() {
		return new File( getDomainPath(), WEBSITE_PATH );
	}
	
	public static File getPreviewPath() {
		return new File( getWebSitePath(), getCurrentConfig().getPreviewUrl() );
	}	
	
	public static File getImagesPath() {
		return new File( getPreviewPath(), IMAGES_PATH );		
	}
	
	public static File getDocumentsPath() {
		return new File( getPreviewPath(), DOCUMENTS_PATH );
	}
	
	public static String fixPath( String path ) {
		if ( File.separator.equals("\\") ) {
			return StringUtils.replace(path, "/", File.separator);
		} else {
			return StringUtils.replace(path, "\\", File.separator);
		}  
	}

	public static String getRelativePath( File path, File file ) {
		String fullPath = FilenameUtils.normalizeNoEndSeparator(file.getAbsolutePath());
		String basePath = FilenameUtils.normalizeNoEndSeparator(path.getAbsolutePath());
		String value = StringUtils.substring(fullPath, basePath.length());
		value = FilenameUtils.separatorsToUnix(value);
		return value;
	}
	
	public static File getImagePath( String relativePath ) {
		return new File( getImagesPath(), fixPath(relativePath) );
	}
	
	public static String getPreviewURL() {
		String url = null;
		url = "http://" + getCurrentConfig().getPreview_host() + 
						"." + getCurrentConfig().getDomain() + "/"; 
		return url;
	}

	public static String getWebURL() {
		String url = null;
		url = "http://" + getCurrentConfig().getHost() + 
						"." + getCurrentConfig().getDomain(); 
		return url;
	}

	public static File getLanguagePreviewPath() {
		String language = getCurrentLanguage().getLanguage().getLocale().getLanguage();
		return new File( getPreviewPath(), language );
	}

	public static File getConfigPath() {
		return new File( getCurrentTemplatePath(), CONFIG_PATH);
	}	

}
