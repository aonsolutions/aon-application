package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.io.File;
import java.io.IOException;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.marketing.Template;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;

/**
 * Controller used in the offer maintenance.
 */
public class TemplateController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateController.class.getName());
	
	public void onSendEmail( ActionEvent event ) throws ManagerBeanException {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onNewMessage(event);
		controller.setShowNewMessageWindow(true);
		initController(controller, (Template) getTo());
	}

	public static void initController( MessageController controller, Template template ) {
		controller.setAppendSignature(true);
		controller.setSkipSignature(!template.isAppendSignature());
		controller.setSubject(template.getSubject());		
		if (! StringUtils.isEmpty(template.getData()) ) {
			controller.updateMessageBody(template.getData());	
		}		
		IAttachment attach = template.getRegistryAttachment();
		if ( (attach != null) && (attach.getId() != null) ) {
			AonFile aonFile = getAonFile(attach);
			if ( aonFile != null ) {
				controller.addAttachment(getAonFile(attach));	
			}
		}
	}

	private static AonFile getAonFile( IAttachment attach ) {
		String fileName = attach.getDescription();
		String extension = (attach.getMimeType() != null) ? "." + attach.getMimeType().getExtension() : "";
		try {
			File file = File.createTempFile( fileName, extension );
			FileUtils.writeByteArrayToFile(file, attach.getData());
			AonFile aonFile = new AonFile();
			aonFile.setFile(file);	
			aonFile.setFileName( fileName + extension );
			aonFile.setMimeType(attach.getMimeType());
			return aonFile;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
}