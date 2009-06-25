package com.code.aon.ui.cms.velocity;

import java.io.File;

import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.IGeneratorLogger;
import com.code.aon.ui.cms.controller.GeneratorController;
import com.code.aon.ui.cms.controller.GeneratorStatusController;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.util.AonUtil;

public class Generator implements ICMSConstants, IVelocityConstants {
	
	protected GeneratorContext context;

	protected IGeneratorLogger logger;
	
	public Generator() {
		GeneratorController generator = (GeneratorController) AonUtil.getRegisteredBean(GENERATOR);
		this.context = generator.getContext();
		this.logger = this.context.getLogger();		
	}
	
    protected static IGeneratorLogger getLogger() {
    	return (GeneratorStatusController) AonUtil.getRegisteredBean(GENERATOR_STATUS);
	}	
	
	public void generate(VelocityUtil vu, Templates type) {
		generate(vu, type, ""); 
	}

	public void generate(VelocityUtil vu, Templates type, String name) {
		String contentTemplate = getContentTemplate(type);
		generate(vu, type, contentTemplate, name);
	}

	public void generate(VelocityUtil vu, Templates type, String contentTemplate, String name) {
		File template = getIndexTemplate();
		if (type == Templates.LANGUAGE) template = getLanguageTemplate();
		if (type == Templates.CAPTCHA) template = getCaptchaTemplate();
		String content = contentTemplate;
		File page = getPage(type, name);
		
	    if (template != null && content != null) {
	        vu.put("content", content);
	        vu.put("current_page", getPageHtmlName(type, name)); 
	        vu.generate(template, page);
	        vu.remove("current_page");
	        vu.remove("content");
	    } else {
	    	logger.error("No se ha encontrado plantilla " + type.getTemplateName());
	    }
	}

	private static File getIndexTemplate() {
		String template = Templates.INDEX.getTemplateName();
		File file = new File( ControllerUtil.getCurrentVmTemplatePath(), template );
		if ( file.exists() ) {
			return file;
		}
		return null;
	}

	private static File getLanguageTemplate() {
		String template = Templates.LANGUAGE.getTemplateName();
		File file = new File( ControllerUtil.getCurrentVmTemplatePath(), template );
		if ( file.exists() ) {
			return file;
		}
		return null;
	}

	private static File getCaptchaTemplate() {
		String template = Templates.CAPTCHA.getTemplateName();
		File file = new File( ControllerUtil.getCurrentVmTemplatePath(), template );
		if ( file.exists() ) {
			return file;
		}
		return null;
	}

	private static String getContentTemplate(Templates t) {
		String content = t.getTemplateName();
		if (validateTemplate(content)) return content;
		else return null;
	}

	private static File getPage(Templates t, String name) {
		String page = t.getHtmlName().replaceAll("%NAME%", name);
		if (t == Templates.LANGUAGE) {
			return new File( ControllerUtil.getPreviewPath(), page );
		}
		return new File( ControllerUtil.getLanguagePreviewPath(), page );
	}

	private static String getPageHtmlName(Templates t, String name) {
		String page = t.getHtmlName();
		page = page.replaceAll("%NAME%", name);
		return page;
	}

	private static boolean validateTemplate(String template) {
		File full_path = new File( ControllerUtil.getCurrentVmTemplatePath(), template );
		return full_path.exists();
	}

}
