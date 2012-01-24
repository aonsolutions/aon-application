package com.code.aon.ui.document.controller;

import static com.code.aon.document.dao.AlfrescoCategoryDAO.EMPTY_CATEGORY;
import static com.code.aon.ui.document.controller.IDocumentConstants.ALFRESCO_CATEGORY_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
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
import com.code.aon.company.EnterpriseUser;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.document.dao.AlfrescoDAO;
import com.code.aon.document.dao.EnterpriseDocumentDAO;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.IAttachmentController;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.document.event.EnterpriseProjectListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class EnterpriseDocumentController extends BasicController implements IAttachmentController, IEnterpriseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseDocumentController.class);
	
	private EnterpriseDocumentDAO alfrescoDAO;
	
	private BasicManagerBean alfrescoManagerBean;
	
	private AonFile aonFile;
	
	private EnterpriseDocument lastDocument;
	
	private List<AlfrescoCategory> categories;
	
	private List<SelectItem> mimeTypes;
	
	private IControllerListener projectListener;
	
	private Criteria lastCriteria;
	
	public EnterpriseDocumentController() {
		this.projectListener = new EnterpriseProjectListener(this);
	}

	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.alfrescoManagerBean == null ) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			EnterpriseUser user = mc.getLoggedUser();
			AlfrescoCategoryController acc = (AlfrescoCategoryController) AonUtil.getRegisteredBean(ALFRESCO_CATEGORY_CONTROLLER_NAME);
			this.alfrescoDAO = new EnterpriseDocumentDAO(user.getLogin(), user.getPassword(), acc.getAlfrescoDAO());
			this.alfrescoDAO.setParentEnterprise(mc.getParentEnterprise());			
			String path = this.alfrescoDAO.getEnterprisePath(mc.getEnterprise());
			this.alfrescoDAO.setPath(path);
			this.alfrescoManagerBean = new BasicManagerBean(this.alfrescoDAO);			
		}
		return this.alfrescoManagerBean;
	}
	
	public EnterpriseDocumentDAO getAlfrescoDAO() {
		return alfrescoDAO;
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

	public EnterpriseDocument getLastDocument() {
		return lastDocument;
	}

	public void setLastDocument(EnterpriseDocument lastDocument) {
		this.lastDocument = lastDocument;
	}

	public void reset() throws ManagerBeanException {
		setAonFile(null);
		setLastDocument(null);
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
		setLastDocument( getEnterpriseDocument() );
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

	public List<AlfrescoCategory> getCategories() {
		return categories;
	}
	
	public void setCategories(List<AlfrescoCategory> categories) {
		this.categories = categories;
		if (categories.isEmpty()) {
			categories.add(EMPTY_CATEGORY);
		}		
	}

	public AlfrescoCategory[] getCategoryArray() {
		if ( categories != null ) {
			List<AlfrescoCategory> list = new LinkedList<AlfrescoCategory>();
			for( AlfrescoCategory category : categories ) {
				if ( (category != null) && (category.getId() != null) ) {
					list.add(category);
				}
			}
			if (! list.isEmpty() ) {
				return list.toArray(new AlfrescoCategory[list.size()]);			
			}			
		}
		return null;
	}
	
	public void setCategoryArray( AlfrescoCategory[] array ) {
		List<AlfrescoCategory> list = new LinkedList<AlfrescoCategory>(); 
		if (! ArrayUtils.isEmpty(array) ) {
			list.addAll( Arrays.asList(array) );
		}
		setCategories(list);
	}
	
	public int getCategoriesSize() {
		return (categories != null) ? categories.size() : 0;
	}
	
	public AlfrescoCategory getEmptyCategory() {
		return EMPTY_CATEGORY;
	}

	public void onAddCategory(ActionEvent event) {
		getCategories().add(EMPTY_CATEGORY);
	}
	
	public void onRemoveCategory(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		getCategories().remove(index);
		if (getCategories().isEmpty()) {
			getCategories().add(EMPTY_CATEGORY);
		}
	}			
	
	@Override
	public Enterprise getEnterprise() {
		return getEnterpriseDocument().getEnterprise();
	}

	public IControllerListener getProjectListener() {
		return projectListener;
	}

	public Criteria getLastCriteria() {
		return lastCriteria;
	}
	
	public void clearCriteriaEx() throws ManagerBeanException {
		this.lastCriteria = getCriteria();
		clearCriteria();
	}
}