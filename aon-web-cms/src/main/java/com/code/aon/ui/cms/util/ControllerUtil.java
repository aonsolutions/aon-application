package com.code.aon.ui.cms.util;

import java.io.File;

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

	public static String getDomainPath() {
		return DOMAINS_PATH + File.separator + getCurrentConfig().getDomain();
	}
	
	public static String getTemporalPath() {
		String path = null;
		path = getDomainPath() + 
					File.separator + RESOURCE_PATH + 
					File.separator + APLICATION_NAME + 
					File.separator + TEMPORAL_PATH;
		return path;
	}	

	public static String getTemplatePath() {
		String path = null;
		path = getDomainPath() + 
					File.separator + RESOURCE_PATH + 
					File.separator + APLICATION_NAME + 
					File.separator + TEMPLATE_PATH;
		return path;
	}	

	public static String getCurrentTemplatePath() {
		String path = null;
		path = getTemplatePath() + 
					File.separator + getCurrentConfig().getTemplate(); 
		return path;
	}	
	
	public static String getCurrentVmTemplatePath() {
		String path = null;
		path = getCurrentTemplatePath() +  
					File.separator + VM_PATH; 
		return path;
	}	

	public static String getBundlePath() {
		String path = null;
		path = getCurrentTemplatePath() + 
					File.separator + BUNDLE_PATH; 
		return path;
	}	

	public static String getCssTemplatePath() {
		String path = null;
		path = getCurrentTemplatePath() +  
					File.separator + CSS_PATH; 
		return path;
	}	

	public static String getJsTemplatePath() {
		String path = null;
		path = getCurrentTemplatePath() + 
					File.separator + JS_PATH; 
		return path;
	}	

	public static String getImagesPath() {
		String path = null;
		path = getDomainPath() + 
					File.separator + WEBSITE_PATH + 
					File.separator + getCurrentConfig().getPreviewUrl() +
					File.separator + IMAGES_PATH;
		return path;
	}
	
	public static String fixPath( String path ) {
		if ( File.separator.equals("\\") ) {
			return StringUtils.replace(path, "/", File.separator);
		} else {
			return StringUtils.replace(path, "\\", File.separator);
		}  
	}

	public static String getRelativePath( String path, File file ) {
		String fullPath = file.getAbsolutePath();
		String value = StringUtils.substring(fullPath, path.length());
		try{
			value = value.replaceAll(File.separator, "/");
		}catch(Throwable th){
			value = value.replaceAll(File.separator+File.separator, "/");
		}
		return value;
	}
	
	public static File getImagePath( String relativePath ) {
		return new File( getImagesPath(), fixPath(relativePath) );
	}
	
	public static String getDocumentsPath() {
		String path = null;
		path = getDomainPath() + 
					File.separator + WEBSITE_PATH + 
					File.separator + getCurrentConfig().getPreviewUrl() +
					File.separator + DOCUMENTS_PATH;
		return path;
	}
	
	public static String getPreviewPath() {
		String path = null;
		path = getDomainPath() + 
					File.separator + WEBSITE_PATH + 
					File.separator + getCurrentConfig().getPreviewUrl();
		return path;
	}

	public static String getPreviewURL() {
		String url = null;
		url = "http://" + getCurrentConfig().getPreview_host() + 
						"." + getCurrentConfig().getDomain(); 
		return url;
	}

	public static String getWebURL() {
		String url = null;
		url = "http://" + getCurrentConfig().getHost() + 
						"." + getCurrentConfig().getDomain(); 
		return url;
	}

	public static String getLanguagePreviewPath() {
		String path = null;
		path = getDomainPath() + 
					File.separator + WEBSITE_PATH + 
					File.separator + getCurrentConfig().getPreviewUrl() +
					File.separator + getCurrentLanguage().getLanguage().getLocale().getLanguage();
		return path;
	}

	public static String getConfigPath() {
		String path = null;
		path = getTemplatePath() + 
					File.separator + getCurrentConfig().getTemplate() + 
					File.separator + CONFIG_PATH; 
		return path;
	}	

}
