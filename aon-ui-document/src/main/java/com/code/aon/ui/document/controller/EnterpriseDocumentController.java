package com.code.aon.ui.document.controller;

import static com.code.aon.ui.document.controller.IDocumentConstants.ALFRESCO_CATEGORY_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Enterprise;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.document.dao.AlfrescoDAO;
import com.code.aon.document.dao.EnterpriseDocumentDAO;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.IAttachmentController;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class EnterpriseDocumentController extends BasicController implements IAttachmentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseDocumentController.class);
	
	private AlfrescoDAO alfrescoDAO;
	
	private BasicManagerBean alfrescoManagerBean;
	
	private AonFile aonFile;
	
	private String title;
	
	private String description;
	
	private AlfrescoCategory[] categories;
	
	private List<SelectItem> mimeTypes;
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.alfrescoManagerBean == null ) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			String user = mc.getPrincipal().getShortName();
			AlfrescoCategoryController acc = (AlfrescoCategoryController) AonUtil.getRegisteredBean(ALFRESCO_CATEGORY_CONTROLLER_NAME);
			this.alfrescoDAO = new EnterpriseDocumentDAO(user, user, acc.getAlfrescoDAO());
			this.alfrescoManagerBean = new BasicManagerBean(this.alfrescoDAO);			
		}
		return this.alfrescoManagerBean;
	}

	@Override
	protected String getIdAlias() throws ManagerBeanException {
		return "ID";
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
	
	private String getName( EnterpriseDocument ed ) {
		String name = ed.getName();
		String extension = FilenameUtils.getExtension(name);
		if ( StringUtils.isEmpty(extension) ) {
			MimeType type = ed.getMimeType();
			if ( type != null ) {
				name += "." + type.getExtension();
			}
		}
		return name;
	}
	
    public void downloadAttachment( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> map = context.getExternalContext().getRequestParameterMap();
        Reference id = new Reference(AlfrescoDAO.STORE, map.get("uuid"), map.get("path") );
        EnterpriseDocument ed = (EnterpriseDocument) getManagerBean().get(id);
		InputStream in = new ByteArrayInputStream(ed.getData());
		long size = ArrayUtils.getLength(ed.getData());
		DownloadUtil.downloadAttachment(getName(ed), ed.getMimeType(), in, size);   	
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AlfrescoCategory[] getCategories() {
		return categories;
	}

	public void setCategories(AlfrescoCategory[] categories) {
		this.categories = categories;
	}

	public void reset() throws ManagerBeanException {
		setAonFile(null);
		setTitle(null);
		setDescription(null);
		setCategories(null);
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
		setDescription( getEnterpriseDocument().getTitle() );
		setDescription( getEnterpriseDocument().getDescription() );
		setCategories( getEnterpriseDocument().getCategories() );
		onReset(event);
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
