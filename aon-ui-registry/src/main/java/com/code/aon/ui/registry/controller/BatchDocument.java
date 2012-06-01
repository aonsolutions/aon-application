package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.registry.controller.IRegistryConstants.CORPORATE_IDENTITY_ATTACHMENT_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;

public class BatchDocument {

	private final static Logger LOGGER = LoggerFactory.getLogger(BatchDocument.class);
	
	private Set<RegistryAttachment> documents;
	
	private int pageLimit = BasicController.LIMIT;
	
	private String beanName;
	
	private DataModel model;
	
	/** A list that contains the selected objects of the model. */
	private Set<RegistryAttachment> checkList;

	public BatchDocument() {
		this.documents = new HashSet<RegistryAttachment>();
		this.model = new ListDataModel();
		this.checkList = new HashSet<RegistryAttachment>();
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
	
    private File getZipFile() throws IOException {
    	File file = File.createTempFile( "documents", "." + MimeType.MIME_ZIP.getExtension());
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		for (RegistryAttachment ra : documents) {
            zipOut.putNextEntry(new ZipEntry(ra.getDescription()));
            zipOut.write(ra.getData());
        	zipOut.closeEntry();
        }
		zipOut.close();
		return file;
    }
	
	public AonFile getDocumentsZip() throws IOException {
		AonFile aonFile = new AonFile();
		aonFile.setFile(getZipFile());
		aonFile.setFileName( "documents.zip" );
		aonFile.setMimeType(MimeType.MIME_ZIP);
		return aonFile;
	}	

	public void onSendInvoiceByEmail(ActionEvent event) {
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		if (mailConfig.getMailAccountCount() > 0) {
			MessageController messageController = (MessageController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
			messageController.initNewMessage();
			messageController.setShowNewMessageWindow(true);
			if (! this.documents.isEmpty() ) {
				try {
					messageController.addAttachment( getDocumentsZip() );
				} catch (IOException e) {
					LOGGER.error(">>>> onSendInvoiceByEmail exception: ", e);
					AonUtil.addErrorMessage(e.getMessage());
					throw new AbortProcessingException(e.getMessage(), e);
				}				
			}
		} else {
			AonUtil.addErrorMessageFromBundle(IWebMailConstants.BUNDLE_NAME, IWebMailConstants.NOT_MAIL_ACCOUNTS);
		}
	}
	
	public void onClear(ActionEvent event) {
		this.documents.clear();
		this.model = new ListDataModel();
	}
	
	public boolean isInBatch() {
		IController controller = FormUtil.getController(CORPORATE_IDENTITY_ATTACHMENT_CONTROLLER_NAME);
		RegistryAttachment ed = (RegistryAttachment) controller.getTo();
		return this.documents.contains(ed);
	}

	public boolean isCurrentInBatch() throws ManagerBeanException {
		return this.documents.contains(getCurrentDocument());
	}
	
	private void updateModel() {
		this.model = new ArrayDataModel(this.documents.toArray());		
	}

	public void onAddCurrentToBatch(ActionEvent event) throws ManagerBeanException {
		this.documents.add(getCurrentDocument());
		updateModel();
	}
	
	public void onAddToBatch(ActionEvent event) {
		IController controller = FormUtil.getController(CORPORATE_IDENTITY_ATTACHMENT_CONTROLLER_NAME);
		RegistryAttachment ed = (RegistryAttachment) controller.getTo();
		this.documents.add(ed);
		updateModel();
	}

	public void onRemoveFromtBatch(ActionEvent event) {
		if ( model.isRowAvailable() ) {
			RegistryAttachment ed = (RegistryAttachment) this.model.getRowData();
			this.documents.remove(ed);
			updateModel();		
		}
	}
	
	public void onAddDocuments(ActionEvent event) {
		for( RegistryAttachment ed : checkList ) {
			this.documents.add(ed);
		}
		updateModel();
	}	

	private RegistryAttachment getCurrentDocument() throws ManagerBeanException {
		IController controller = FormUtil.getController(CORPORATE_IDENTITY_ATTACHMENT_CONTROLLER_NAME);
		DataModel model = controller.getModel();
		if ( model.isRowAvailable() ) {
			return (RegistryAttachment) model.getRowData();			
		}
		return null;
	}

	/**
	 * Gets the if the selected row is checked.
	 * 
	 * @return the row checked
	 * @throws ManagerBeanException 
	 */
	public boolean getRowChecked() throws ManagerBeanException {
		return checkList.contains( getCurrentDocument() );
	}	

	/**
	 * Sets the selected row checked.
	 * 
	 * @param rowChecked
	 *            the row checked
	 * @throws ManagerBeanException 
	 */
	public void setRowChecked(boolean rowChecked) throws ManagerBeanException {
		RegistryAttachment ed = getCurrentDocument();
		if (rowChecked) {
			if (!checkList.contains(ed)) {
				checkList.add(ed);
			}
		} else {
			if (checkList.contains(ed)) {
				checkList.remove(ed);
			}
		}
	}

	/**
	 * Clears the selected list.
	 * 
	 * @param event the event
	 */
	public void checkNone(ActionEvent event) {
		this.checkList.clear();
	}
	
	/**
	 * Check all.
	 * 
	 * @param event the event
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		IController controller = FormUtil.getController(CORPORATE_IDENTITY_ATTACHMENT_CONTROLLER_NAME);
		List<RegistryAttachment> list = (List) controller.getManagerBean().getList(controller.getCriteria());
		this.checkList.clear();
		this.checkList.addAll(list);
	}	
	
}
