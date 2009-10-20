package com.code.aon.faces.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.LinesController;

/**
 * The Class AttachmentController.
 */
public class AttachmentController extends LinesController {

	private static final Logger LOGGER = Logger.getLogger(AttachmentController.class.getName());
	
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
	 * Checks if is uploaded.
	 * 
	 * @return true, if is uploaded
	 */
	public boolean isUploaded() {
		return (this.aonFile != null) && (! ArrayUtils.isEmpty(this.aonFile.getData())); 
	}
	
	/**
	 * Checks if is maximum size exceeded.
	 * 
	 * @return true, if is maximum size exceeded
	 */
	public boolean isMaximumSizeExceeded() {
		return ( this.maximumSize != -1 ) && ( this.aonFile.getSize() > this.maximumSize);
	}
	
	/**
	 * File uploaded.
	 * 
	 * @param event the event
	 */
	public void fileUploaded(UploadEvent event) {
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
			getAttachment().setDescription(FilenameUtils.getName(item.getFileName()));
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	private void writeAttachment(IAttachment attachment, HttpServletResponse response) {
		try {
			String filename = attachment.getDescription();
			if ( attachment.getMimeType() != null ) {
				response.setContentType( attachment.getMimeType().getName() );	
			}
			response.setHeader("Content-disposition", "attachment;filename=\"" + filename + "\"");
			byte[] data = attachment.getData();
			response.setHeader("Content-Length", String.valueOf(data.length));
			ServletOutputStream sos = response.getOutputStream();
			sos.write( data );
			sos.close();
			response.flushBuffer();
		} catch (IOException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
	}
	
    public void downloadAttachment( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
        String id = context.getExternalContext().getRequestParameterMap().get("index");
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        IAttachment attachment = (IAttachment) getManagerBean().get(Integer.valueOf(id));
        writeAttachment( attachment, response );
        context.responseComplete();    	
    }
	
}