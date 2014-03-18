package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.registry.controller.IRegistryConstants.CORPORATE_IDENTITY_ATTACHMENT_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

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

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.code.aon.ui.webmail.controller.MessageController;

public class BatchDocument extends DataScrollerState {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(BatchDocument.class);
	
	private Set<IAttachment> documents;
	
	/** A list that contains the selected objects of the model. */
	private Set<IAttachment> checkList;

	public BatchDocument() {
		this.documents = new HashSet<IAttachment>();
		setModel(new SerializableListDataModel());
		this.checkList = new HashSet<IAttachment>();
	}
	
    private File getZipFile() throws IOException {
    	File file = File.createTempFile( "documents", "." + MimeType.MIME_ZIP.getExtension());
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		for (IAttachment ra : documents) {
			String name = DownloadUtil.getFileName(ra.getDescription(), ra.getMimeType());
            zipOut.putNextEntry(new ZipEntry(name));
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
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {			
			try {
				controller.onNewMessage(event);
				if (! this.documents.isEmpty() ) {
					controller.addAttachment( getDocumentsZip() );
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}				
		}
	}
	
	public void onClear(ActionEvent event) {
		this.documents.clear();
		setModel(new SerializableListDataModel());
	}
	
	public boolean isInBatch( IAttachment attachment ) {
		return this.documents.contains(attachment);
	}
	
	private void updateModel() {
		setModel(new ArrayDataModel(this.documents.toArray()));		
	}

	public void addToBatch(IAttachment attachment) {
		this.documents.add(attachment);
		updateModel();
	}
	
	public void onAddToBatch(ActionEvent event) {
		IController controller = FormUtil.getController(CORPORATE_IDENTITY_ATTACHMENT_CONTROLLER_NAME);
		IAttachment ed = (IAttachment) controller.getTo();
		this.documents.add(ed);
		updateModel();
	}

	public void onRemoveFromtBatch(ActionEvent event) {
		if ( getDirectModel().isRowAvailable() ) {
			IAttachment ed = (IAttachment) getDirectModel().getRowData();
			this.documents.remove(ed);
			updateModel();		
		}
	}
	
	public void onAddDocuments(ActionEvent event) {
		for( IAttachment ed : checkList ) {
			this.documents.add(ed);
		}
		updateModel();
	}	

	private IAttachment getCurrentDocument() throws ManagerBeanException {
		IController controller = FormUtil.getController(CORPORATE_IDENTITY_ATTACHMENT_CONTROLLER_NAME);
		DataModel model = controller.getModel();
		if ( model.isRowAvailable() ) {
			return (IAttachment) model.getRowData();			
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
		IAttachment ed = getCurrentDocument();
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

    public void downloadAttachment( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
        String id = context.getExternalContext().getRequestParameterMap().get("index");
        IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
        IAttachment attachment = (IAttachment) bean.get(Integer.valueOf(id));
        DownloadUtil.downloadAttachment( attachment );    	
    }
	
}
