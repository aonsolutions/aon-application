package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Template;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;

/**
 * Controller used in the offer maintenance.
 */
public class TemplateController extends BasicController {
	
	public void onSendEmail( ActionEvent event ) throws ManagerBeanException {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onNewMessage(event);
		controller.setShowNewMessageWindow(true);
		controller.setAppendSignature(true);
		Template template = (Template) getTo();
		if (! StringUtils.isEmpty(template.getData()) ) {
			controller.updateMessageBody(template.getData());	
		}
	}
	
}