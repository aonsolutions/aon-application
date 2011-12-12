package com.code.aon.ui.document.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_WEBMAIL;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.NOT_SERVER_CONNECTED;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.ui.webmail.controller.WebMailController;

public class BatchDocument {
	
	private Set<EnterpriseDocument> documents;
	
	private int pageLimit = BasicController.LIMIT;
	
	private String beanName;
	
	private DataModel model;

	public BatchDocument() {
		this.documents = new HashSet<EnterpriseDocument>();
		this.model = new ListDataModel();
	}

	public int getPageLimit() {
		return pageLimit;
	}

	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public DataModel getModel() {
		return model;
	}
	
	public AonFile getDocumentsZip() throws IOException {
		File file = File.createTempFile( "documents", ".zip" );
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setFileName( "documents.zip" );
		aonFile.setMimeType(MimeType.MIME_ZIP);
		return aonFile;
	}	

	public void onSendInvoiceByEmail(ActionEvent event) throws ManagerBeanException, IOException {
		WebMailController webmailController = (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL);
		if (webmailController.isLogged()) {
			MessageController messageController = (MessageController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
			messageController.initNewMessage();
			messageController.addAttachment( getDocumentsZip() );
			messageController.setShowNewMessageWindow(true);
		} else {
			AonUtil.addErrorMessageFromBundle(IWebMailConstants.BUNDLE_NAME, NOT_SERVER_CONNECTED);
		}
	}
	
	public void onClear(ActionEvent event) {
		this.documents.clear();
		this.model = new ListDataModel();
	}
	
}
