package com.code.aon.faces.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.sun.faces.util.MessageFactory;

public class AttachmentUtil implements ICommonConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(AttachmentUtil.class);

	/**
	 * Checks if is uploaded.
	 * 
	 * @return true, if is uploaded
	 */
	public static boolean isUploaded( IAttachmentController controller ) {
		AonFile aonFile = controller.getAonFile();
		return (aonFile != null) && (! ArrayUtils.isEmpty(aonFile.getData())); 
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
	
	public static void checkFileData( IAttachmentController controller, boolean isNew, boolean required ) throws ControllerListenerException {
		boolean ok = true;
		if ( isNew ) {
			ok = isUploaded(controller);
		} else {
			IAttachment attach = controller.getAttachment();
			ok = (attach.getData() != null) && (! ArrayUtils.isEmpty(attach.getData()));
		}
		if ( (!ok) && required ) {
			FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage(FILE_UPLOAD_ELEMENT) );
			throw new ControllerListenerException( message.getSummary() );			
		} else if ( isUploaded(controller) && isMaximumSizeExceeded(controller) ) {
	        String message = AonUtil.getMessage(ICommonConstants.DEFAULT_BUNDLE, DOCUMENT_MAX_SIZE_ERROR, controller.getMaximumSize());
			throw new ControllerListenerException(message);			
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
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				byte[] data = FileUtils.readFileToByteArray(file);
				f.setData(data);
				FileUtils.deleteQuietly(file);
			}
			f.setFileName(item.getFileName());
			f.setMimeType(f.resolverMimeType());
			controller.setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public static void downloadAttachment(IAttachment attach) {
		InputStream in = new ByteArrayInputStream(attach.getData());
		long size = ArrayUtils.getLength(attach.getData());
		downloadAttachment(attach.getDescription(), attach.getMimeType(), in, size);
	}
	
	private static HttpServletResponse getResponse( FacesContext context ) {
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		if ( response instanceof HttpServletResponseWrapper) {
			response = (HttpServletResponse) ((HttpServletResponseWrapper) response).getResponse();
		}
		return response;
	}

	public static void downloadAttachment(String fileName, MimeType type, InputStream in, long size) {
		OutputStream out = null;
		try {
	        FacesContext context = FacesContext.getCurrentInstance();
	        HttpServletResponse response = getResponse(context);
			if ( type != null ) {
				response.setContentType( type.getName() );	
			}
			if (! StringUtils.isEmpty(fileName) ) {
				if ( (type != MimeType.MIME_PDF) && (type != MimeType.MIME_SIGNED_PDF) ) {
					response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");	
				} else {
					response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
				}
			}
			if ( size > 0 ) {
				response.setHeader("Content-Length", String.valueOf(size));	
			}
			out = new BufferedOutputStream(response.getOutputStream());
			IOUtils.copyLarge(in, out);
			out.close();
			response.flushBuffer();
	        context.responseComplete();    	
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			IOUtils.closeQuietly(out);
		}
	}	
}
