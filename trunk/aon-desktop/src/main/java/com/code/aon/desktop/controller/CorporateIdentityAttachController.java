package com.code.aon.desktop.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.IAttachment;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.common.io.IAonFileListener;
import com.code.aon.ui.form.GridController;

public class CorporateIdentityAttachController extends GridController implements IAonFileListener {

	/** The uploaded file. */
	private AonFile aonFile;

	private long maximumSize = -1;
	

	public CorporateIdentityAttachController() {
		this.maximumSize = -1;
	}

	public long getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(long maximumSize) {
		this.maximumSize = 1048576;
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
			f.addAonFileListener(this);
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void fileDeleted(AonFile aonFile) {
		setAonFile(null);
	}

	public void fileRemove( ActionEvent ent ) throws IOException {
		IAttachment attach = getAttachment();
		if ( attach != null ) {
			attach.setData( null );
			attach.setMimeType( null );
		}
	}

}