package com.code.aon.ui.finance.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.richfaces.event.UploadEvent;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.IAttachmentController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.sign.controller.ISignatureController;

public class InvoiceAttachmentListener extends ControllerAdapter implements IAttachmentController {

	/** The uploaded file. */
	private AonFile aonFile;
	
	private long maximumSize;
	
	private IAttachment attachment;
	
	/**
	 * Instantiates a new attachment controller.
	 */
	public InvoiceAttachmentListener() {
		this.maximumSize = -1;
	}
	
	private ISignatureController getSignatureController() {
		return (ISignatureController) getController();
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

	public boolean isAttachmentAvailable() {
		return (attachment != null) && (attachment.getId() != null);
	}
	
	public IAttachment getAttachment() {
		return attachment;
	}

	public void setAttachment(IAttachment attachment) {
		this.attachment = attachment;
	}
	
	private void reset( ISignatureController controller ) {
		setAonFile(null);
		setAttachment( controller.newAttachment(controller.getTo()) );
	}
	
	/**
	 * Checks if is uploaded.
	 * 
	 * @return true, if is uploaded
	 */
	public boolean isUploaded() {
		return AttachmentUtil.isUploaded(this);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		reset( getSignatureController() );
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ISignatureController controller = (ISignatureController) event.getController();
		IAttachment attachment = controller.generateReportAttachment( controller.getTo() );
		AonFile aonFile = null;
		if ( attachment == null ) {
			reset( controller );
		} else {
			setAttachment(attachment);
			aonFile = new AonFile();
			aonFile.setData( attachment.getData() );
			aonFile.setMimeType( attachment.getMimeType() );
			aonFile.setFileName( attachment.getDescription() );
			setAonFile(aonFile);
		}
	}

	public void fileUploaded(UploadEvent event) {
		AttachmentUtil.fileUploaded(event, this);
		ISignatureController controller = getSignatureController();
		try {		
			AttachmentUtil.checkFileData(this, controller.isNew(), false);
			AttachmentUtil.updateAttachment(this);
		} catch (ControllerListenerException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}		
		try {		
			controller.getAttachmentBean().insertOrUpdate( getAttachment() );	
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}
    
    public void removeAttachment( ActionEvent event ) {
    	ISignatureController controller = getSignatureController();
		try {		
			if (isAttachmentAvailable() ) {
				controller.getAttachmentBean().remove( getAttachment() );
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}   	
		reset( controller );
	
    }
	
}