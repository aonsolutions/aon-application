package com.esferalia.aon.ui.pms.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.data.enumeration.DataAttachmentSource;
import com.code.aon.data.enumeration.DataAttachmentType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DataTransmissionController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public List<SelectItem> getNavSources() {
		List<SelectItem> sources = new LinkedList<SelectItem>();
		sources.add(new SelectItem(DataAttachmentSource.INVOICE, DataAttachmentSource.INVOICE.getName(AonUtil.getCurrentLocale()))); 
		sources.add(new SelectItem(DataAttachmentSource.FBATCH, DataAttachmentSource.FBATCH.getName(AonUtil.getCurrentLocale()))); 
		sources.add(new SelectItem(DataAttachmentSource.PRODUCTION, DataAttachmentSource.PRODUCTION.getName(AonUtil.getCurrentLocale()))); 
		return sources;
	}

	public List<SelectItem> getNavTypes() {
		List<SelectItem> types = new LinkedList<SelectItem>();
		types.add(new SelectItem(DataAttachmentType.REQUEST, DataAttachmentType.REQUEST.getName(AonUtil.getCurrentLocale()))); 
		types.add(new SelectItem(DataAttachmentType.RESPONSE_OK, DataAttachmentType.RESPONSE_OK.getName(AonUtil.getCurrentLocale()))); 
		types.add(new SelectItem(DataAttachmentType.RESPONSE_ERROR, DataAttachmentType.RESPONSE_ERROR.getName(AonUtil.getCurrentLocale()))); 
		return types;
	}

    public void downloadAttachment(ActionEvent event) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
        String id = context.getExternalContext().getRequestParameterMap().get("index");
        IAttachment attachment = (IAttachment) getManagerBean().get(Integer.valueOf(id));
        DownloadUtil.downloadAttachment(attachment);
    }

}
