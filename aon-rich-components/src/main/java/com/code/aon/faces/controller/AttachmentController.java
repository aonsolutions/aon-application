package com.code.aon.faces.controller;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.FilenameUtils;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.FileInfo;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.DownloadUtil;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

/**
 * The Class AttachmentController.
 */
public class AttachmentController extends LinesController implements
		IAttachmentController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(AttachmentController.class);
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
	 * @param maximumSize
	 *            the new maximum size
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
	 * @param aonFile
	 *            the new aon file
	 */
	public void setAonFile(AonFile aonFile) {
		if (this.aonFile != null) {
			this.aonFile.clean();
		}
		this.aonFile = aonFile;
	}

	/**
	 * File uploaded.
	 * 
	 * @param event
	 *            the event
	 */
	public void fileUploaded(UploadEvent event) {
		AttachmentUtil.fileUploaded(event, this);
		String description = FilenameUtils.getBaseName(getAonFile()
				.getFileName());
		getAttachment().setDescription(description);
	}

	public void downloadAttachment(ActionEvent event)
			throws NumberFormatException, ManagerBeanException {
		FacesContext context = FacesContext.getCurrentInstance();
		String id = context.getExternalContext().getRequestParameterMap()
				.get("index");
		IAttachment attachment = (IAttachment) getManagerBean().get(
				Integer.valueOf(id));
		if (attachment.getData() != null) {
			DownloadUtil.downloadAttachment(attachment);
		}else if (attachment.getDriveId() != null) {
			try {

				DomainGserviceaccount googleAccount = DatabaseSync
						.getServiceAccount(attachment.getDomain());
				Drive drive = DriveUtils.serviceInitialize(googleAccount);
				File file = DriveUtils.getFile(attachment.getDriveId());
				InputStream in = DriveUtils.downloadFile(drive, file);
				long size = file.getFileSize();
				DownloadUtil.downloadAttachment(attachment.getDescription(),
						attachment.getMimeType(), in, size);
			
			} catch (KeyStoreException e) {
				LOGGER.error(e.getMessage()); 
			} catch (IOException e) {
				LOGGER.error(e.getMessage()); 
			} catch (GeneralSecurityException e) {
				LOGGER.error(e.getMessage()); 
			} catch (SQLException e) {
				LOGGER.error(e.getMessage()); 
			}
		}

	}

	@Override
	public void onCancel(ActionEvent event) {
		super.onCancel(event);
		if (this.aonFile != null) {
			this.aonFile.clean();
		}
	}

}