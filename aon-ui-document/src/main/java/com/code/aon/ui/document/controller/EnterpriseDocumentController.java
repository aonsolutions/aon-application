package com.code.aon.ui.document.controller;

import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_CONTROLLER_NAME;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.alfresco.webservice.types.Reference;
import org.apache.commons.lang.ArrayUtils;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Enterprise;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.document.AlfrescoCategoryManager;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.document.dao.AlfrescoDAO;
import com.code.aon.document.dao.EnterpriseDocumentDAO;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.IAttachmentController;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class EnterpriseDocumentController extends LinesController implements IAttachmentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseDocumentController.class);
	
	private AlfrescoDAO alfrescoDAO;
	
	private BasicManagerBean alfrescoManagerBean;
	
	private AonFile aonFile;
	
	private AlfrescoCategoryManager categoryManager;
	
	private List<SelectItem> categories;
	
	private List<SelectItem> mimeTypes;
	
	private String user;
	
	public EnterpriseDocumentController() {
		LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(LoggedUser.LOGGED_USER);
		this.user = loggedUser.getPrincipal().getShortName();
		this.categoryManager = new AlfrescoCategoryManager(user, user);
	}

	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.alfrescoManagerBean == null ) {
			this.alfrescoDAO = new EnterpriseDocumentDAO(user, user, categoryManager);
			this.alfrescoManagerBean = new BasicManagerBean(this.alfrescoDAO);			
		}
		return this.alfrescoManagerBean;
	}
	
	public AlfrescoCategoryManager getCategoryManager() {
		return categoryManager;
	}

	public EnterpriseDocument getEnterpriseDocument() {
		return (EnterpriseDocument) getTo();
	}

	/**
	 * File uploaded.
	 * 
	 * @param event the event
	 */
	public void fileUploaded(UploadEvent event) {
		AttachmentUtil.fileUploaded(event, this);
		getEnterpriseDocument().setName( getAonFile().getFileName() );
	}
	
    public void downloadAttachment( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> map = context.getExternalContext().getRequestParameterMap();
        Reference id = new Reference(AlfrescoDAO.STORE, map.get("uuid"), map.get("path") );
        EnterpriseDocument ed = (EnterpriseDocument) getManagerBean().get(id);
		InputStream in = new ByteArrayInputStream(ed.getData());
		long size = ArrayUtils.getLength(ed.getData());
		DownloadUtil.downloadAttachment(ed.getDescription(), ed.getMimeType(), in, size);   	
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

	public void reset() throws ManagerBeanException {
		setAonFile(null);
	}

	public void onEnterpriseChanged( LookupChangeEvent event ) {
		EnterpriseDocument ed = getEnterpriseDocument();
		if (event.getNewValue() != null) {
			Enterprise enterprise = (Enterprise)event.getNewValue(); 
			ed.setEnterprise( enterprise );
		} else {
			ed.setEnterprise(null);
		}
	}
	
	public void masiveUpload( ActionEvent event ) {
		try {
			getManagerBean().restoreNullSubPOJOs(getTo());
			accept();
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onAccept",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
		String description = getEnterpriseDocument().getDescription();
		AlfrescoCategory[] categories = getEnterpriseDocument().getCategories();
		onReset(event);
		getEnterpriseDocument().setDescription(description);
		getEnterpriseDocument().setCategories(categories);
	}


	public List<SelectItem> getCategories() throws ManagerBeanException {
		if ( categories == null ) {
			categories = new LinkedList<SelectItem>();
			EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
			AlfrescoCategoryManager cm = edc.getCategoryManager();
			for( AlfrescoCategory category : cm.getCategories() ) {
				SelectItem item = new SelectItem(category, category.getName());
				categories.add(item);
			}			
		}
		return categories;
	}	
	
	public List<SelectItem> getMimeTypes() {
		if ( mimeTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			mimeTypes = new LinkedList<SelectItem>();
			for( MimeType mimeType : MimeType.values() ) {
				String name = mimeType.getName(locale);
				SelectItem item = new SelectItem(mimeType, name);
				mimeTypes.add(item);			
			}
		}
		return mimeTypes;
	}	
	
}
