package com.code.aon.ui.document.controller;

import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_WEBMAIL;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.NOT_SERVER_CONNECTED;

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
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.ui.webmail.controller.WebMailController;

public class BatchDocument {

	private final static Logger LOGGER = LoggerFactory.getLogger(BatchDocument.class);
	
	private Set<EnterpriseDocument> documents;
	
	private int pageLimit = BasicController.LIMIT;
	
	private String beanName;
	
	private DataModel model;
	
	/** A list that contains the selected objects of the model. */
	private Set<EnterpriseDocument> checkList;

	public BatchDocument() {
		this.documents = new HashSet<EnterpriseDocument>();
		this.model = new ListDataModel();
		this.checkList = new HashSet<EnterpriseDocument>();
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
		for (EnterpriseDocument ed : documents) {
            zipOut.putNextEntry(new ZipEntry(ed.getName()));
            zipOut.write(ed.getData());
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
		WebMailController webmailController = (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL);
		if (webmailController.isLogged()) {
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
			AonUtil.addErrorMessageFromBundle(IWebMailConstants.BUNDLE_NAME, NOT_SERVER_CONNECTED);
		}
	}
	
	public void onClear(ActionEvent event) {
		this.documents.clear();
		this.model = new ListDataModel();
	}
	
	public boolean isInBatch() {
		IController controller = FormUtil.getController(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		EnterpriseDocument ed = (EnterpriseDocument) controller.getTo();
		return this.documents.contains(ed);
	}

	private void updateModel() {
		this.model = new ArrayDataModel(this.documents.toArray());		
	}
	
	public void onAddToBatch(ActionEvent event) {
		IController controller = FormUtil.getController(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		EnterpriseDocument ed = (EnterpriseDocument) controller.getTo();
		this.documents.add(ed);
		updateModel();
	}

	public void onRemoveFromtBatch(ActionEvent event) {
		if ( model.isRowAvailable() ) {
			EnterpriseDocument ed = (EnterpriseDocument) this.model.getRowData();
			this.documents.remove(ed);
			updateModel();		
		}
	}
	
	public void onAddDocuments(ActionEvent event) {
		for( EnterpriseDocument ed : checkList ) {
			this.documents.add(ed);
		}
		updateModel();
	}	

	private EnterpriseDocument getCurrentDocument() throws ManagerBeanException {
		IController controller = FormUtil.getController(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		DataModel model = controller.getModel();
		if ( model.isRowAvailable() ) {
			return (EnterpriseDocument) model.getRowData();			
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
		EnterpriseDocument ed = getCurrentDocument();
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
		EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
		List<EnterpriseDocument> list = (List) edc.getManagerBean().getList(edc.getLastCriteria());
		this.checkList.clear();
		this.checkList.addAll(list);
	}	
	
}
