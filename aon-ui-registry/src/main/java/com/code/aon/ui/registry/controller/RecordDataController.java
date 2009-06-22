package com.code.aon.ui.registry.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.LinesController;

public class RecordDataController extends LinesController {
	
	private static final Logger LOGGER = Logger.getLogger(RecordDataController.class.getName());
	
	public String listNavigation;
	public String formNavigation;
	
	private AonFile aonFile;
	
	public String getListNavigation() {
		return listNavigation;
	}

	public void setListNavigation(String listNavigation) {
		this.listNavigation = listNavigation;
	}

	public String getFormNavigation() {
		return formNavigation;
	}

	public void setFormNavigation(String formNavigation) {
		this.formNavigation = formNavigation;
	}

	public AonFile getAonFile() {
		return aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	@SuppressWarnings("unused")
	public void onAttachDownload(ActionEvent event) throws ManagerBeanException{
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse)ctx.getExternalContext().getResponse();
			RecordData recordData = (RecordData)this.getTo();
			if(recordData.getAttach().getMimeType() != null){
				response.setContentType(recordData.getAttach().getMimeType().getName());
			}
			response.setHeader("Content-Disposition", "attachment; filename=\"" + recordData.getAttach().getDescription() + "\";");
			response.getOutputStream().write(recordData.getAttach().getData());
			ctx.responseComplete();
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	@SuppressWarnings("unused")
	public void onAttachRemove(ActionEvent event) throws ManagerBeanException{
		try {
			IManagerBean recordDataBean = BeanManager.getManagerBean(RecordData.class);
			RecordData recordData = (RecordData)this.getTo();
			RegistryAttachment attach = recordData.getAttach();
			recordData.setAttach(null);
			recordDataBean.update(recordData);
			deleteAttachment(attach);
			RegistryAttachment attachment = new RegistryAttachment();
			attachment.setRegistry(new Registry());
			recordData.setAttach(attachment);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException(e);
		}
	}

	private void deleteAttachment(RegistryAttachment attach) {
		try {
			IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			rAttachBean.remove(attach);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting attach with id= " + attach.getId(), e);
		}
	}

}