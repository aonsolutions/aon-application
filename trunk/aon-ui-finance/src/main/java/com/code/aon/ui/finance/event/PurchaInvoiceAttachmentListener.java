package com.code.aon.ui.finance.event;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.richfaces.event.UploadEvent;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.IAttachmentController;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.finance.controller.PurchaseInvoiceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PurchaInvoiceAttachmentListener extends ControllerAdapter implements IAttachmentController {

	/** The uploaded file. */
	private AonFile aonFile;
	
	private long maximumSize;
	
	private IAttachment attachment;
	
	/**
	 * Instantiates a new attachment controller.
	 */
	public PurchaInvoiceAttachmentListener() {
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
		return attachment.getId() != null;
	}
	
	public IAttachment getAttachment() {
		return attachment;
	}

	public void setAttachment(IAttachment attachment) {
		this.attachment = attachment;
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
		setAonFile(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		PurchaseInvoiceController controller = (PurchaseInvoiceController) event.getController();
		IAttachment attachment = controller.getInvoiceFile();
		AonFile aonFile = null;
		if ( attachment == null ) {
			attachment = controller.newAttachment(controller.getTo());
		} else {
			aonFile = new AonFile();
			aonFile.setData( attachment.getData() );
			aonFile.setMimeType( attachment.getMimeType() );
			aonFile.setFileName( attachment.getDescription() );
		}
		setAttachment(attachment);
		setAonFile(aonFile);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		InvoiceController controller = (InvoiceController) event.getController();
		AttachmentUtil.checkFileData(this, controller.isNew(), false);
		AttachmentUtil.updateAttachment(this);
		try {		
			if ( AttachmentUtil.isUploaded(this) ) {
				controller.getAttachmentBean().insertOrUpdate( getAttachment() );	
			} else if (isAttachmentAvailable() ) {
				controller.getAttachmentBean().remove( getAttachment() );
				setAttachment(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	public void fileUploaded(UploadEvent event) {
		AttachmentUtil.fileUploaded(event, this);
	}

    public void downloadAttachment( ActionEvent event ) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        AttachmentUtil.writeAttachment( getAonFile().getFileName(), getAonFile().getMimeType(), getAonFile().getData(), response );
        context.responseComplete();    	
    }
    
    public void removeAttachment( ActionEvent event ) {
    	setAonFile( null );
    }
	
}
