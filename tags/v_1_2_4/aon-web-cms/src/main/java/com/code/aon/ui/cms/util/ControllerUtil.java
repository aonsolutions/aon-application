package com.code.aon.ui.cms.util;

import com.code.aon.cms.Config;
import com.code.aon.cms.ConfigDetail;
import com.code.aon.cms.Language;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.controller.ConfigController;
import com.code.aon.ui.cms.controller.I18NController;
import com.code.aon.ui.util.AonUtil;

public class ControllerUtil implements Constants {

	public static I18NController getI18NController() {
		return (I18NController) AonUtil.getRegisteredBean("i18n");
	}
	
	public static Language getCurrentLanguage() {
		I18NController i18n = (I18NController) AonUtil.getRegisteredBean("i18n");
		return i18n.getCurrentLanguage();
	}	

	public static ConfigController getConfigController() {
		return (ConfigController) AonUtil.getRegisteredBean("config");
	}
	
	public static Config getCurrentConfig() {
		ConfigController config = (ConfigController)AonUtil.getRegisteredBean("config");
		return config.getCurrentConfig();
	}

	public static ConfigDetail getCurrentConfigDetail() {
		ConfigController config = (ConfigController)AonUtil.getRegisteredBean("config");
		return config.getCurrentConfigDetail();
	}	

	public static String getTemporalPath() {
		String path = null;
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
								"/" + RESOURCE_PATH + 
								"/" + APLICATION_NAME + 
								"/" + TEMPORAL_PATH;
		return path;
	}	

	public static String getTemplatePath() {
		String path = null;
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
									"/" + RESOURCE_PATH + 
									"/" + APLICATION_NAME + 
									"/" + TEMPLATE_PATH;
		return path;
	}	

	public static String getCurrentVmTemplatePath() {
		String path = null;
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
									"/" + RESOURCE_PATH + 
									"/" + APLICATION_NAME + 
									"/" + TEMPLATE_PATH + 
									"/" + getCurrentConfig().getTemplate() + 
									"/" + VM_PATH; 
		return path;
	}	

	public static String getBundlePath() {
		String path = null;
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
									"/" + RESOURCE_PATH + 
									"/" + APLICATION_NAME + 
									"/" + TEMPLATE_PATH + 
									"/" + getCurrentConfig().getTemplate() + 
									"/" + BUNDLE_PATH; 
		return path;
	}	

	public static String getCssTemplatePath() {
		String path = null;
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
									"/" + RESOURCE_PATH + 
									"/" + APLICATION_NAME + 
									"/" + TEMPLATE_PATH + 
									"/" + getCurrentConfig().getTemplate() + 
									"/" + CSS_PATH; 
		return path;
	}	

	public static String getJsTemplatePath() {
		String path = null;
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
									"/" + RESOURCE_PATH + 
									"/" + APLICATION_NAME + 
									"/" + TEMPLATE_PATH + 
									"/" + getCurrentConfig().getTemplate() + 
									"/" + JS_PATH; 
		return path;
	}	

	public static String getImagesPath() {
		String path = null;
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
									"/" + WEBSITE_PATH + 
									"/" + getCurrentConfig().getPreviewUrl() +
									"/" + IMAGES_PATH;
		return path;
	}
	
	public static String getDocumentsPath() {
		String path = null;
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
									"/" + WEBSITE_PATH + 
									"/" + getCurrentConfig().getPreviewUrl() +
									"/" + DOCUMENTS_PATH;
		return path;
	}
	
	public static String getPreviewPath() {
		String path = null;
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
									"/" + WEBSITE_PATH + 
									"/" + getCurrentConfig().getPreviewUrl();
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
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
									"/" + WEBSITE_PATH + 
									"/" + getCurrentConfig().getPreviewUrl() +
									"/" + getCurrentLanguage().getLanguage().getLocale().getLanguage();
		return path;
	}

	public static String getConfigPath() {
		String path = null;
		path = DOMAINS_PATH + "/" + getCurrentConfig().getDomain() + 
									"/" + RESOURCE_PATH + 
									"/" + APLICATION_NAME + 
									"/" + TEMPLATE_PATH + 
									"/" + getCurrentConfig().getTemplate() + 
									"/" + CONFIG_PATH; 
		return path;
	}	

}
