package com.code.aon.ui.registry.controller;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.io.AonFile;

public class PersonController extends RegistryController {

	private RegistryAttachment attach;
	private RegistryMedia phone;
	private RegistryMedia fax;
	private RegistryMedia email;
	private RegistryMedia web;
	private RegistryAddress mainAddress;
	private RegistryBank registryBank;
	private RegistryPayMethod registryPayMethod;

	private boolean addressDirty;
	private boolean phoneDirty;
	private boolean faxDirty;
	private boolean emailDirty;
	private boolean webDirty;
	private boolean registryBankDirty;
	private boolean registryPayMethodDirty;

	private AonFile aonFile;

	public AonFile getAonFile() {
		return this.aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public RegistryAddress getMainAddress() {
		return mainAddress;
	}
	public void setMainAddress(RegistryAddress mainAddress) {
		this.mainAddress = mainAddress;
	}
	
	public RegistryMedia getPhone() {
		return phone;
	}
	public void setPhone(RegistryMedia phone) {
		this.phone = phone;
	}

	public RegistryMedia getEmail() {
		return email;
	}
	public void setEmail(RegistryMedia email) {
		this.email = email;
	}

	public RegistryMedia getFax() {
		return fax;
	}
	public void setFax(RegistryMedia fax) {
		this.fax = fax;
	}

	public RegistryMedia getWeb() {
		return web;
	}
	public void setWeb(RegistryMedia web) {
		this.web = web;
	}

	public RegistryAttachment getAttach() {
		return attach;
	}
	public void setAttach(RegistryAttachment attach) {
		this.attach = attach;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank rBank) {
		this.registryBank = rBank;
	}

	public RegistryPayMethod getRegistryPayMethod() {
		return registryPayMethod;
	}
	public void setRegistryPayMethod(RegistryPayMethod registryPayMethod) {
		this.registryPayMethod = registryPayMethod;
	}

	public boolean isShowCompanyBanks() {
		return (registryPayMethod.getPayment() != null)
			&& (registryPayMethod.getPayment().getType() == PayMethodType.BANK_TRANSFER);
	}
	public boolean isCash() {
		return (registryPayMethod.getPayment() != null)
			&& (registryPayMethod.getPayment().getType() == PayMethodType.CASH_BASIS);
	}

	public boolean isAddressDirty() {
		return addressDirty;
	}

	public boolean isEmailDirty() {
		return emailDirty;
	}

	public boolean isFaxDirty() {
		return faxDirty;
	}

	public boolean isPhoneDirty() {
		return phoneDirty;
	}

	public boolean isWebDirty() {
		return webDirty;
	}
	public boolean isRegistryBankDirty() {
		return registryBankDirty;
	}
	public boolean isRegistryPayMethodDirty() {
		return registryPayMethodDirty;
	}

	public void resetDirty(){
		addressDirty = false;
		phoneDirty = false;
		faxDirty = false;
		emailDirty = false;
		webDirty = false;
		registryBankDirty = false;
		registryPayMethodDirty = false;
	}

	public boolean isImageAttached(){
		if(this.attach != null){
			return true;
		}
		return false;
	}

	public boolean isWithLogo() throws ManagerBeanException {
		Person person = (Person) getTo();
		if (person != null && person.getId() != null) {
			IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria criteria = new Criteria();
			String alias = registryAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
			criteria.addEqualExpression(alias, person.getId());
			String type = registryAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
			criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
			return registryAttachBean.getCount(criteria) > 0;
		}
		return false;
	}

	public InputStream getAttachAsInputStream() throws IOException, ManagerBeanException{
		RegistryAttachment attach = obtainPersonLogo();
		if(attach != null){
			return new ByteArrayInputStream(attach.getData());
		}
		return null;
	}
	
	public RegistryAttachment obtainPersonLogo() throws ManagerBeanException {
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, ((Person)this.getTo()).getId());
		String type = registryAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		Iterator<?> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}

	public void phoneChanged(ValueChangeEvent event) {
		phoneDirty = true;
	}

	public void faxChanged(ValueChangeEvent event) {
		faxDirty = true;
	}

	public void emailChanged(ValueChangeEvent event) {
		emailDirty = true;
	}

	public void webChanged(ValueChangeEvent event) {
		webDirty = true;
	}

	public void addressChanged(ValueChangeEvent event) {
		addressDirty = true;
	}
	public void registryBankChanged(ValueChangeEvent event) {
		registryBankDirty = true;
	}
	public void registryPayMethodChanged(ValueChangeEvent event) {
		registryPayMethodDirty = true;
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
			f.setFileName( item.getFileName() );
			f.setMimeType( MimeType.get(item.getContentType()) );
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void createCurrentLogoContent(OutputStream out, Object data) throws IOException {
		if (getAonFile() != null && getAonFile().getData() != null) {
			out.write(getAonFile().getData());
		}
	}

	public void createLogoContent(OutputStream out, Object data) throws IOException {
		if (getAttach() != null) {
			out.write(getAttach().getData());
		}
	}
	
	public String getLogoMimeType() {
		if ( getAttach().getMimeType() != null ) {
			return getAttach().getMimeType().getName();
		}
		return "*";	
	}

	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			this.registryPayMethod.setRegistryBank( new RegistryBank() );
			this.registryPayMethod.getRegistryBank().setBank( new Bank() );
			this.registryPayMethod.getRegistryBank().setBankAccount( new BankAccount() );		
		}
		registryBankChanged(event);
		registryPayMethodChanged(event);
		
	}

}
