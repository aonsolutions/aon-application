package com.code.aon.faces.controller;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.richfaces.event.UploadEvent;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.LinesController;

/**
 * The Class AttachmentController.
 */
public class AttachmentController extends LinesController implements IAttachmentController {

	/** The uploaded file. */
	private AonFile aonFile;
	
	private long maximumSize;
	
	/**
	 * Instantiates a new attachment controller.
	 */
	public AttachmentController() {
		this.maximumSize = -1;
	}

	/**
	 * Gets the maximum size.
	 * 
	 * @return the maximum size
	 */
	public long getMaximumSize() {
		return maximumSize;
	}

	/**
	 * Sets the maximum size.
	 * 
	 * @param maximumSize the new maximum size
	 */
	public void setMaximumSize(long maximumSize) {
		this.maximumSize = maximumSize;
	}

	/**
	 * Gets the attachment.
	 * 
	 * @return the attachment
	 */
	public IAttachment getAttachment() {
		return (IAttachment) getTo();
	}
	
	/**
	 * Gets the uploaded file.
	 * 
	 * @return the file
	 */
	public AonFile getAonFile() {
		return this.aonFile;
	}

	/**
	 * Sets the aon file.
	 * 
	 * @param aonFile the new aon file
	 */
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	/**
	 * File uploaded.
	 * 
	 * @param event the event
	 */
	public void fileUploaded(UploadEvent event) {
		AttachmentUtil.fileUploaded(event, this);
	}
	
    public void downloadAttachment( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
        String id = context.getExternalContext().getRequestParameterMap().get("index");
        IAttachment attachment = (IAttachment) getManagerBean().get(Integer.valueOf(id));
        AttachmentUtil.downloadAttachment( attachment );    	
    }
	
}