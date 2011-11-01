package com.code.aon.ui.document.controller;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.IAttachmentController;
import com.code.aon.ui.document.AlfrescoDAO;
import com.code.aon.ui.document.EnterpriseDocument;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class EnterpriseDocumentController extends LinesController implements IAttachmentController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseDocumentController.class);
	
	private AlfrescoDAO alfrescoDAO;
	
	private BasicManagerBean alfrescoManagerBean;
	
	private AonFile aonFile;
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.alfrescoManagerBean == null ) {
			LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(LoggedUser.LOGGED_USER);
			String user = loggedUser.getPrincipal().getShortName();
			this.alfrescoDAO = new EnterpriseDocumentDAO(user, user);
			this.alfrescoManagerBean = new BasicManagerBean(this.alfrescoDAO);			
		}
		return this.alfrescoManagerBean;
	}

	/**
	 * File uploaded.
	 * 
	 * @param event the event
	 */
	public void fileUploaded(UploadEvent event) {
		AttachmentUtil.fileUploaded(event, this);
		EnterpriseDocument ed = (EnterpriseDocument) getTo();
		ed.setName( getAonFile().getFileName() );
	}
	
    public void downloadAttachment( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
        String id = context.getExternalContext().getRequestParameterMap().get("index");
        IAttachment attachment = (IAttachment) getManagerBean().get(id);
        DownloadUtil.downloadAttachment( attachment );    	
    }	

	@Override
	public IAttachment getAttachment() {
		return null;
	}

	@Override
	public AonFile getAonFile() {
		return aonFile;
	}

	@Override
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	@Override
	public long getMaximumSize() {
		return -1;
	}	
	
	
}
