package com.code.aon.ui.registry.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.form.LinesController;

public class RecordDataController extends LinesController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RecordDataController.class);
	
	private RegistryAttachment attach;

	public RegistryAttachment getAttach() {
		return attach;
	}
	
	public void setAttach(RegistryAttachment attach) {
		this.attach = attach;
	}

	public boolean isAttachAvailable() {
		return ( attach != null ) && (! ArrayUtils.isEmpty(attach.getData()) );
	}
	
	private String getName( RegistryAttachment attach ) {
		String name = attach.getDescription();
		if(attach.getMimeType() != null){
			name += "." + attach.getMimeType().getExtension();
		}
		return name;
	}
	
	public String getAttachDescription(){
		int sizeKb = attach.getData().length >> 10;
		return getName(attach) + " (" + sizeKb + " Kb)";
	}
	
	public void fileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				setAttach( new RegistryAttachment() );
				String name = item.getFileName();
				attach.setData( data );	
				attach.setMimeType(MimeType.getByExtension(FilenameUtils.getExtension(name)));
				attach.setDescription(FilenameUtils.getBaseName(name));
				attach.setCategory(null);
			}
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
			throw new AbortProcessingException(e.getMessage());
		}
	}	

	public void downloadAttachment(ActionEvent event) {		
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse)ctx.getExternalContext().getResponse();
			if(attach.getMimeType() != null){
				response.setContentType(attach.getMimeType().getName());
			}
			response.setHeader("Content-Disposition", "attachment; filename=\"" + getName(attach) + "\";");
			response.getOutputStream().write(attach.getData());
			response.flushBuffer();
			ctx.responseComplete();
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
			throw new AbortProcessingException(e);
		}
	}
	
	public void onAttachRemove(ActionEvent event) {
		setAttach( null );		
	}

	public void removeAttachment() throws ManagerBeanException {
		RecordData recordData = (RecordData) getTo();
		RegistryAttachment attach = recordData.getAttach();
		if ( (attach != null) && (attach.getId() != null) ) {
			recordData.setAttach( null );
			getManagerBean().update(recordData);
			IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			rAttachBean.remove(attach);
		}
	}

}