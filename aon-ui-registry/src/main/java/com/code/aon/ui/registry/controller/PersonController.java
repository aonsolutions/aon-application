package com.code.aon.ui.registry.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.richfaces.event.UploadEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.registry.controller.event.PersonFormListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PersonController extends RegistryController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private RegistryAttachment logoAttach;

	private AonFile logoFile;

	public RegistryAttachment getLogoAttach() {
		return logoAttach;
	}

	public void setLogoAttach(RegistryAttachment logoAttach) {
		this.logoAttach = logoAttach;
	}

	public AonFile getLogoFile() {
		return this.logoFile;
	}
	public AonFile getAonFile() {
		return this.logoFile;
	}

	public void setLogoFile(AonFile logoFile) {
		if ( this.logoFile != null ) {
			this.logoFile.clean();	
		}				
		this.logoFile = logoFile;
	}
	
	public List<SelectItem> getMunicipalities(){
		PersonFormListener personForm = (PersonFormListener) AonUtil.getRegisteredBean(IRegistryConstants.PERSON_FORM_CONTROLLER_NAME);
		return personForm.getMunicipalities();
	}
	
	public void logoFileUploaded(UploadEvent event) {
		setLogoFile(AttachmentUtil.fileUploaded(event));
	}

	public void createCurrentLogoContent(OutputStream out, Object data) throws IOException {
		if (getLogoFile() != null && (getLogoFile().getSize() > 0)) {
			out.write(getLogoFile().getData());
		}
	}
	
	public RegistryAttachment obtainPersonLogo() throws ManagerBeanException {
		if(this.getTo() == null){
			return null;
		}
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, ((Person)this.getTo()).getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}

}
