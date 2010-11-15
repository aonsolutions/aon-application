package com.code.aon.ui.registry.controller;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.io.AonFile;

public class PersonController extends RegistryController {

	private RegistryAttachment attach;
	private AonFile aonFile;

	public AonFile getAonFile() {
		return this.aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}
	public RegistryAttachment getAttach() {
		return attach;
	}
	public void setAttach(RegistryAttachment attach) {
		this.attach = attach;
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

}
