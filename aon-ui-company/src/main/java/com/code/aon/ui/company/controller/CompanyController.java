package com.code.aon.ui.company.controller;

import java.io.IOException;
import java.io.OutputStream;

import org.richfaces.event.UploadEvent;

import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;

/**
 * Controller used in the company maintenance.
 */
public class CompanyController extends CompanyParentController {

	/** The uploaded logo file. */
	private AonFile logoFile;

	/** The uploaded signature file. */
	private AonFile signatureFile;

	/**
	 * Gets the uploaded logo file.
	 * 
	 * @return the file
	 */
	public AonFile getLogoFile() {
		return this.logoFile;
	}
	public AonFile getAonFile() {
		return this.logoFile;
	}

	/**
	 * Sets the logo file.
	 * 
	 * @param file
	 *            the file
	 */
	public void setLogoFile(AonFile logoFile) {
		if ( this.logoFile != null ) {
			this.logoFile.clean();	
		}		
		this.logoFile = logoFile;
	}

	/**
	 * Gets the uploaded signature file.
	 * 
	 * @return the file
	 */
	public AonFile getSignatureFile() {
		return this.signatureFile;
	}
	
	/**
	 * Sets the signature file.
	 * 
	 * @param file
	 *            the file
	 */
	public void setSignatureFile(AonFile signatureFile) {
		if ( this.signatureFile != null ) {
			this.signatureFile.clean();	
		}				
		this.signatureFile = signatureFile;
	}

	public void logoFileUploaded(UploadEvent event) {
		setLogoFile(AttachmentUtil.fileUploaded(event));
	}

	public void signatureFileUploaded(UploadEvent event) {
		setSignatureFile(AttachmentUtil.fileUploaded(event));
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createCurrentLogoContent(OutputStream out, Object data) throws IOException {
		if (getLogoFile() != null && (getLogoFile().getSize() >0)) {
			out.write(getLogoFile().getData());
		}
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createLogoContent(OutputStream out, Object data) throws IOException {
		if (getLogoAttach() != null) {
			out.write(getLogoAttach().getData());
		}
	}
	
	public String getLogoMimeType() {
		if ( getLogoAttach()!= null && getLogoAttach().getMimeType() != null ) {
			return getLogoAttach().getMimeType().getName();
		}
		return "*";	
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createCurrentSignatureContent(OutputStream out, Object data) throws IOException {
		if (getSignatureFile() != null && (getSignatureFile().getSize() > 0) ) {
			out.write(getSignatureFile().getData());
		}
	}

}