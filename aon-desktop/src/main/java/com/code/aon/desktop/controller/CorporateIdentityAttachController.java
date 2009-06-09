package com.code.aon.desktop.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.IAttachment;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.BasicController;

public class CorporateIdentityAttachController extends BasicController {

	/** The uploaded file. */
	private AonFile aonFile;

	private long maximumSize;

	public long getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(long maximumSize) {
		this.maximumSize = maximumSize;
	}

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
	 * Sets the file.
	 * 
	 * @param file
	 *            the file
	 */
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public boolean isUploaded() {
		return (this.aonFile != null) && (! ArrayUtils.isEmpty(this.aonFile.getData())); 
	}
	
	public void fileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName(item.getFileName());
			getAttachment().setDescription(FilenameUtils.getName(item.getFileName()));
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
}