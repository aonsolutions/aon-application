package com.code.aon.ui.cms.velocity;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;

public class CommonGenerator extends Generator implements ICMSConstants {

	public void generateBasicPages() throws IOException {
		File previewPath = ControllerUtil.getPreviewPath();
		if (!previewPath.exists()) {
			previewPath.mkdirs();
		}
		File languagePreviewPath = ControllerUtil.getLanguagePreviewPath();
		if (!languagePreviewPath.exists()) {
			languagePreviewPath.mkdirs();
		}
		generateEmailSendPage();
		generateSearchPage();
		generateLanguagePage();		
		generatePage( Templates.CAPTCHA );		
	}

	private void generateEmailSendPage() {
		VelocityUtil vu = context.initVelocityUtil();		
		context.changeDefaultSection(vu);
		vu.put("smtpServer", ControllerUtil.getCurrentConfig().getSmtp_server());
		vu.put("username", ControllerUtil.getCurrentConfig().getSmtp_user());
		vu.put("password", ControllerUtil.getCurrentConfig().getSmtp_password());
		vu.put("from", ControllerUtil.getCurrentConfig().getFrom_email());
		vu.put("name_from", ControllerUtil.getCurrentConfig().getFrom_name());
		generate(vu, Templates.SENDMAIL);
	}

	private void generateSearchPage() {
		VelocityUtil vu = context.initVelocityUtil();		
		context.changeDefaultSection(vu);
		generate(vu, Templates.SEARCH);
	}
	
	private void generateLanguagePage() {
		VelocityUtil vu = context.initVelocityUtil();		
		generate(vu, Templates.LANGUAGE);
	}	

	private void generatePage( Templates template) throws IOException {
		File file = Generator.getTemplateFile(template);
		if ( (file != null) && file.exists() ) {
			FileUtils.copyFile( file, Generator.getPage(template) );
		}
	}

}
