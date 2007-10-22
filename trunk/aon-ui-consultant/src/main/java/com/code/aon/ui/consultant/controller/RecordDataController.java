package com.code.aon.ui.consultant.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.consultant.RecordData;
import com.code.aon.consultant.dao.IConsultantAlias;
import com.code.aon.customer.Customer;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class RecordDataController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(RecordDataController.class.getName());
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";
	
	private UploadedFile file;
	
	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	@SuppressWarnings("unused")
	public void onRecordData(ActionEvent event){
		CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer)customerController.getTo();
		try {
			IManagerBean recordDataBean = BeanManager.getManagerBean(RecordData.class);
			this.clearCriteria();
			getCriteria().addEqualExpression(recordDataBean.getFieldName(IConsultantAlias.RECORD_DATA_REGISTRY_ID), customer.getId());
			this.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading fiscal record data for customer with id= " + customer.getId(), e);
		}
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
			response.setHeader("Content-Disposition", "filename=\"" + recordData.getAttach().getDescription() + "\";");
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