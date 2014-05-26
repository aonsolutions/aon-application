package com.code.aon.ui.registry.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.DownloadUtil;

public class RecordDataController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RecordDataController.class);
	
	private AonFile aonFile;
	
	public AonFile getAonFile() {
		return aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		if ( this.aonFile != null ) {
			this.aonFile.clean();	
		}
		this.aonFile = aonFile;
	}
	
	public String getAttachDescription(){
		long sizeKb = aonFile.getSize() >> 10;
		return aonFile.getFileName() + " (" + sizeKb + " Kb)";
	}
	
	public void fileUploaded(UploadEvent event) {
		setAonFile(AttachmentUtil.fileUploaded(event));
	}	

	public void downloadAttachment(ActionEvent event) {		
		InputStream in = null;
		try {
			in = aonFile.openStream();
			DownloadUtil.downloadAttachment(aonFile.getFileName(), aonFile.getMimeType(), in, aonFile.getSize());
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
			throw new AbortProcessingException(e);
		} finally {
			IOUtils.closeQuietly(in);	
		}
	}
	
	public void onAttachRemove(ActionEvent event) {
		setAonFile(null);
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