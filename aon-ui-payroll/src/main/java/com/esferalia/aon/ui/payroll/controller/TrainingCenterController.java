package com.esferalia.aon.ui.payroll.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.registry.controller.RegistryController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.TrainingCenter;

public class TrainingCenterController extends RegistryController {
	
	/** The logo attach. */
	private RegistryAttachment logoAttach;

	/** The signature attach. */
	private RegistryAttachment signatureAttach;

	/** The uploaded logo file. */
	private AonFile logoFile;

	/** The uploaded signature file. */
	private AonFile signatureFile;

	/**
	 * Gets the logo RegistryAttach.
	 * 
	 * @return the logo attachment
	 */
	public RegistryAttachment getLogoAttach() {
		return logoAttach;
	}

	/**
	 * Sets the logo RegistryAttach.
	 * 
	 * @param attach the logo attachment
	 */
	public void setLogoAttach(RegistryAttachment logoAttach) {
		this.logoAttach = logoAttach;
	}

	/**
	 * Gets the signature RegistryAttach.
	 * 
	 * @return the signature attachment
	 */
	public RegistryAttachment getSignatureAttach() {
		return signatureAttach;
	}
	
	/**
	 * Sets the signature RegistryAttach.
	 * 
	 * @param attach the signature attachment
	 */
	public void setSignatureAttach(RegistryAttachment signatureAttach) {
		this.signatureAttach = signatureAttach;
	}
	
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
		this.signatureFile = signatureFile;
	}

	public void logoFileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName( item.getFileName() );
			f.setMimeType( MimeType.get(item.getContentType()) );
			setLogoFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void signatureFileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName( item.getFileName() );
			f.setMimeType( MimeType.get(item.getContentType()) );
			setSignatureFile(f);
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
		if (getLogoFile() != null && getLogoFile().getData() != null) {
			out.write(getLogoFile().getData());
		}
	}

	/**
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public void createCurrentSignatureContent(OutputStream out, Object data) throws IOException {
		if (getSignatureFile() != null && getSignatureFile().getData() != null) {
			out.write(getSignatureFile().getData());
		}
	}
	
	/**
	 * Obtains TrainingCenter logo.
	 * 
	 * @return the registry attachment
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public RegistryAttachment obtainTrainingCenterLogo() throws ManagerBeanException {
		if(this.getTo() == null){
			return null;
		}
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, ((TrainingCenter)this.getTo()).getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}
	
	/**
	 * Obtains TrainingCenter signature.
	 * 
	 * @return the registry attachment
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public RegistryAttachment obtainTrainingCenterSignature() throws ManagerBeanException {
		if(this.getTo() == null){
			return null;
		}
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, ((TrainingCenter)this.getTo()).getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.SIGNATURE);
		Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}
	
}
