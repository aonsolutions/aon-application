package com.code.aon.ui.company.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.io.AonFile;

/**
 * Controller used in the company maintenance.
 */
public class CompanyController extends CompanyParentController {

	/** The uploaded file. */
	private AonFile aonFile;

	/**
	 * The empty constructor.
	 * 
	 * @throws ManagerBeanException
	 *             the manager bean exception
	 */
	public CompanyController() throws ManagerBeanException {
		super();
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
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createCurrentLogoContent(OutputStream out, Object data) throws IOException {
		if (getAonFile() != null && getAonFile().getData() != null) {
			out.write(getAonFile().getData());
		}
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createLogoContent(OutputStream out, Object data) throws IOException {
		if (getAttach() != null) {
			out.write(getAttach().getData());
		}
	}
	
	public Date getTimeStamp() {
		return new Date();	
	}

}