package com.esferalia.aon.ui.payroll.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.registry.controller.RegistryController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.TrainingCenter;

public class TrainingCenterController extends RegistryController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	/** The logo attach. */
	private RegistryAttachment logoAttach;

	/** The signature attach. */
	private RegistryAttachment signatureAttach;

	/** The uploaded logo file. */
	private AonFile logoFile;

	/** The uploaded signature file. */
	private AonFile signatureFile;
	
	/** Determines if the address has been changed and it hasn't been saved yet. */
	private boolean addressDirty;

	/** Determines if the phone has been changed and it hasn't been saved yet. */
	private boolean phoneDirty;

	/** Determines if the fax has been changed and it hasn't been saved yet. */
	private boolean faxDirty;

	/** Determines if the email has been changed and it hasn't been saved yet. */
	private boolean emailDirty;

	/** Determines if the web has been changed and it hasn't been saved yet. */
	private boolean webDirty;
	
	/** The phone. */
	private RegistryMedia phone;

	/** The fax. */
	private RegistryMedia fax;

	/** The email. */
	private RegistryMedia email;

	/** The web. */
	private RegistryMedia web;
	
	/** The main address. */
	private RegistryAddress mainAddress;

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
	
	/**
	 * Checks if is the address is dirty.
	 * 
	 * @return true, if the address is dirty
	 */
	public boolean isAddressDirty() {
		return addressDirty;
	}

	/**
	 * Checks if the email is dirty.
	 * 
	 * @return true, if the email is dirty
	 */
	public boolean isEmailDirty() {
		return emailDirty;
	}

	/**
	 * Checks if the fax is dirty.
	 * 
	 * @return true, if the fax is dirty
	 */
	public boolean isFaxDirty() {
		return faxDirty;
	}

	/**
	 * Checks if the phone is dirty.
	 * 
	 * @return true, if the phone is dirty
	 */
	public boolean isPhoneDirty() {
		return phoneDirty;
	}

	/**
	 * Checks if the web is dirty.
	 * 
	 * @return true, if the web is dirty
	 */
	public boolean isWebDirty() {
		return webDirty;
	}
	
	/**
	 * Gets the phone.
	 * 
	 * @return the phone
	 */
	public RegistryMedia getPhone() {
		return phone;
	}

	/**
	 * Sets the phone.
	 * 
	 * @param phone the phone
	 */
	public void setPhone(RegistryMedia phone) {
		this.phone = phone;
	}

	/**
	 * Gets the email.
	 * 
	 * @return the email
	 */
	public RegistryMedia getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 * 
	 * @param email the email
	 */
	public void setEmail(RegistryMedia email) {
		this.email = email;
	}

	/**
	 * Gets the fax.
	 * 
	 * @return the fax
	 */
	public RegistryMedia getFax() {
		return fax;
	}

	/**
	 * Sets the fax.
	 * 
	 * @param fax the fax
	 */
	public void setFax(RegistryMedia fax) {
		this.fax = fax;
	}

	/**
	 * Gets the web.
	 * 
	 * @return the web
	 */
	public RegistryMedia getWeb() {
		return web;
	}

	/**
	 * Sets the web.
	 * 
	 * @param web the web
	 */
	public void setWeb(RegistryMedia web) {
		this.web = web;
	}
	
    /**
     * Gets the main address.
     * 
     * @return the main address
     */
    public RegistryAddress getMainAddress() {
		return mainAddress;
	}

	/**
	 * Sets the main address.
	 * 
	 * @param mainAddress the main address
	 */
	public void setMainAddress(RegistryAddress mainAddress) {
		this.mainAddress = mainAddress;
	}
	
	/**
	 * Reset all the flags.
	 */
	public void resetDirty(){
		addressDirty = false;
		phoneDirty = false;
		faxDirty = false;
		emailDirty = false;
		webDirty = false;
	}

	/**
	 * Phone changed. Sets the flag phoneDirty = true
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void phoneChanged(ValueChangeEvent event) throws ManagerBeanException {
		phoneDirty = true;
	}

	/**
	 * Fax changed. Sets the flag faxDirty = true
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void faxChanged(ValueChangeEvent event) throws ManagerBeanException {
		faxDirty = true;
	}

	/**
	 * Email changed. Sets the flag emailDirty = true
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void emailChanged(ValueChangeEvent event) throws ManagerBeanException {
		emailDirty = true;
	}

	/**
	 * Web changed. Sets the flag webDirty = true
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void webChanged(ValueChangeEvent event) throws ManagerBeanException {
		webDirty = true;
	}

	/**
	 * Address changed. Sets the flag addressDirty = true
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addressChanged(ValueChangeEvent event) throws ManagerBeanException {
		addressDirty = true;
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
	
	/**
	 * Gets the logo as input stream.
	 * 
	 * @return the logo as input stream
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws IOException the IO exception
	 */
	public InputStream getLogoAsInputStream() throws IOException, ManagerBeanException{
		RegistryAttachment attach = obtainTrainingCenterLogo();
		if(attach != null){
			return new ByteArrayInputStream(attach.getData());
		}
		return null;
	}
	
	/**
	 * Gets the signature as input stream.
	 * 
	 * @return the signature as input stream
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws IOException the IO exception
	 */
	public InputStream getSignatureAsInputStream() throws IOException, ManagerBeanException{
		RegistryAttachment attach = obtainTrainingCenterSignature();
		if(attach != null){
			return new ByteArrayInputStream(attach.getData());
		}
		return null;
	}
	
	public RegistryDirStaff obtainDirStaff() throws ManagerBeanException{
		if(this.getTo() == null){
			return null;
		}
		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID);
		criteria.addEqualExpression(alias, ((TrainingCenter)this.getTo()).getId());
		alias = bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DUE_DATE);
		criteria.addGreaterThanOrEqualExpression(alias, new Date());
		Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryDirStaff)iter.next();
		}
		return null;
	}
	
}
