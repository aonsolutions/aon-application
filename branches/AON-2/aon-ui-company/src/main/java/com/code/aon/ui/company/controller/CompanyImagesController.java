package com.code.aon.ui.company.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.LinesController;

public class CompanyImagesController extends LinesController {

	private UploadedFile file;
	
	private RegistryAttachment attach;

	
	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public RegistryAttachment getAttach() {
		return attach;
	}

	public void setAttach(RegistryAttachment attach) {
		this.attach = attach;
	}
	
	public List<SelectItem> getRegistryAttachmentTypes() {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> rAttachTypes = new LinkedList<SelectItem>();
        SelectItem item = new SelectItem(RegistryAttachmentType.ADDITIONAL_IMAGE, RegistryAttachmentType.ADDITIONAL_IMAGE.getName(locale));
        rAttachTypes.add( item );
        item = new SelectItem(RegistryAttachmentType.BANNER, RegistryAttachmentType.BANNER.getName(locale));
        rAttachTypes.add( item );
        return rAttachTypes;
    }
	
	public String getModelImageUrl() throws ManagerBeanException{
		RegistryAttachment rAttach = (RegistryAttachment)this.getModel().getRowData();
		return rAttach.getId().toString() + ".companyImage";
	}
}