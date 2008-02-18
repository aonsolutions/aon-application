package com.code.aon.ui.cms.velocity;

import java.io.File;

import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;

public class Generator {

	public static void generate(VelocityUtil vu, Templates type) {
		generate(vu, type, ""); 
	}

	public static void generate(VelocityUtil vu, Templates type, String name) {
		String template = getIndexTemplate();
		if (type == Templates.LANGUAGE) template = getLanguageTemplate();
		String content = getContentTemplate(type);
		String page = getPage(type);
		page = page.replaceAll("%NAME%", name);
		
	    if (template != null && content != null) {
	        vu.put("content", content);
	        vu.generate(template, page);
	        vu.remove("content");
	    }
	    else {
	    	vu.addMessage("No se ha encontrado plantilla " + type.getTemplateName(), VelocityUtil.ERROR);
	    }
	}

	private static String getIndexTemplate() {
		String template = Templates.INDEX.getTemplateName();
		if (validateTemplate(template)) return ControllerUtil.getCurrentVmTemplatePath() + "/" + template;
		else return null;
	}

	private static String getLanguageTemplate() {
		String template = Templates.LANGUAGE.getTemplateName();
		if (validateTemplate(template)) return ControllerUtil.getCurrentVmTemplatePath() + "/" + template;
		else return null;
	}

	private static String getContentTemplate(Templates t) {
		String content = t.getTemplateName();
		if (validateTemplate(content)) return content;
		else return null;
	}

	private static String getPage(Templates t) {
		String page = t.getHtmlName();
		String page_full_path = ControllerUtil.getLanguagePreviewPath() + "/" + page;
		if (t == Templates.LANGUAGE) page_full_path = ControllerUtil.getPreviewPath() + "/" + page;
		return page_full_path;
	}

	private static boolean validateTemplate(String template) {
		String full_path = ControllerUtil.getCurrentVmTemplatePath() + "/" + template;
		return validate(full_path);
	}
	
	private static boolean validate(String path) {
	    File f = new File(path);
	    if (f.exists()) return true;
	    else return false;
	}

}
