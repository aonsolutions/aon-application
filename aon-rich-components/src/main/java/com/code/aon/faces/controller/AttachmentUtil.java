package com.code.aon.faces.controller;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.sun.faces.util.MessageFactory;

public class AttachmentUtil implements ICommonMessages {

	/**
	 * Checks if is uploaded.
	 * 
	 * @return true, if is uploaded
	 */
	public static boolean isUploaded( IAttachmentController controller ) {
		AonFile aonFile = controller.getAonFile();
		return (aonFile != null) && (aonFile.getSize() > 0); 
	}
	
	/**
	 * Checks if is maximum size exceeded.
	 * 
	 * @return true, if is maximum size exceeded
	 */
	public static boolean isMaximumSizeExceeded( IAttachmentController controller ) {
		long maximumSize = controller.getMaximumSize();
		return ( maximumSize != -1 ) && ( controller.getAonFile().getSize() > maximumSize);
	}
	
	public static void checkFileData( IAttachmentController controller, boolean required ) throws ControllerListenerException {
		if ( isUploaded(controller) ) {
			if ( isMaximumSizeExceeded(controller) ) {
				String message = AonUtil.getMessage(DOCUMENT_MAX_SIZE_ERROR);
				throw new ControllerListenerException(message);
			}
		} else if ( required && controller.isNevv() ) {
			FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage(FILE_UPLOAD_ELEMENT) );
			throw new ControllerListenerException( message.getSummary() );						
		}
	}
	
	public static void updateAttachment( IAttachmentController controller ) throws ControllerListenerException {
		try {			
			if ( isUploaded(controller) ) {
				AonFile aonFile = controller.getAonFile();
				IAttachment attach = controller.getAttachment();
				attach.setData(aonFile.getData());
				attach.setMimeType(aonFile.getMimeType());
				if ( StringUtils.isBlank(attach.getDescription()) ) {
					attach.setDescription(FilenameUtils.getBaseName(aonFile.getFileName()));
				}
			}
		} catch (Throwable th) {
			throw new ControllerListenerException(AonUtil.getMessage(FILE_UPLOAD_ERROR));
		}		
	}

	/**
	 * File uploaded.
	 * 
	 * @param event the event
	 */
	public static void fileUploaded(UploadEvent event, IAttachmentController controller) {
		controller.setAonFile(fileUploaded(event));
	}

	/**
	 * File uploaded.
	 * 
	 * @param event the event
	 */
	public static AonFile fileUploaded(UploadEvent event) {
		UploadItem item = event.getUploadItem();
		AonFile f = new AonFile();
		if (item.isTempFile()) {
			f.setFile(item.getFile());
		} else {
			f.setData(item.getData());
		}
		f.setFileName(item.getFileName());
		MimeType mt = MimeType.get(item.getContentType());
		if ( mt == null ) {
			mt = f.resolveMimeType();
		}
		f.setMimeType(mt);
		return f;
	}
	
}
