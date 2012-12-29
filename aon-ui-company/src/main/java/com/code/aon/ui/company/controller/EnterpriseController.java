package com.code.aon.ui.company.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.calendar.controller.CalendarController;

public class EnterpriseController extends RegistryController implements ICompanyConstants {
	
	private static final String TREE_SUFFIX = "Tree";
	
	private RegistryInfo info = new RegistryInfo();

	private boolean showActivityNode;
	private WorkPlace workplace;
	private RegistryDirStaff dirStaff;
	
	private String treeTemplateSuffix;
	
	private AonFile logoFile;

	private AonFile signatureFile;
	
	private RegistryAttachment logoAttach;
	
	private RegistryAttachment signatureAttach;
	
	private String formAction;
	
	private boolean skipResetButton;
	
	private boolean skipRemoveButton;
	
	public boolean isSkipResetButton() {
		return skipResetButton;
	}

	public void setSkipResetButton(boolean skipResetButton) {
		this.skipResetButton = skipResetButton;
	}

	public boolean isSkipRemoveButton() {
		return skipRemoveButton;
	}

	public void setSkipRemoveButton(boolean skipRemoveButton) {
		this.skipRemoveButton = skipRemoveButton;
	}

	public boolean isTreeView() {
		return (treeTemplateSuffix != null);
	}

	public void setTreeView(boolean treeView) {
		setTreeTemplateSuffix( treeView ? TREE_SUFFIX : null );
	}
	
	public String getTreeTemplateSuffix() {
		return treeTemplateSuffix;
	}

	public void setTreeTemplateSuffix(String treeTemplateSuffix) {
		this.treeTemplateSuffix = treeTemplateSuffix;
	}

	public boolean isShowActivityNode() {
		return showActivityNode;
	}
	
	public RegistryAddress getMainAddress() {
		return info.getAddress();
	}
	
	public WorkPlace getWorkplace() {
		return workplace;
	}

	public void setWorkplace(WorkPlace workplace) {
		this.workplace = workplace;
	}

	public RegistryDirStaff getDirStaff() {
		return dirStaff;
	}

	public void setDirStaff(RegistryDirStaff dirStaff) {
		this.dirStaff = dirStaff;
	}
	    
    public RegistryMedia getPhone() {
		return info.getPhone();
	}

	public RegistryMedia getFax() {
		return info.getFax();
	}

	public RegistryMedia getEmail() {
		return info.getEmail();
	}

	public RegistryMedia getWeb() {
		return info.getWeb();
	}

	/**
     * Gets the addresses of the enterprise.
     * 
     * @return the addresses of the enterprise
     * @throws ManagerBeanException 
     */
    public List<SelectItem> getAddresses() throws ManagerBeanException {
    	LinkedList<SelectItem> addresses = new LinkedList<SelectItem>();
    	Enterprise enterprise = (Enterprise) getTo();
		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), enterprise.getRegistry().getId());
		criteria.addOrder(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS));
		List<ITransferObject> list = registryAddressBean.getList(criteria);
		for (ITransferObject to : list) {
			RegistryAddress rAddress = (RegistryAddress)to;
			addresses.add(new SelectItem(rAddress, rAddress.getShortAddress()));
		}
    	return addresses;
    }	
    
	public void reset() {
    	this.showActivityNode = false;
    	setWorkplace(null);
    	setDirStaff(null);
    	setLogoFile(null);
    	setSignatureFile(null);
    	this.info.reset();
	}
    
    public Enterprise getEnterprise() {
    	return (Enterprise) getTo();
    }
    
    public void initRegistryInfo() throws ManagerBeanException {
    	this.info.init( getEnterprise().getRegistry() );
    }
    
    public void saveMainAddress() throws ManagerBeanException {
    	IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
    	if(! StringUtils.isEmpty(getMainAddress().getAddress()) ){
    		bean.insertOrUpdate(getMainAddress());
    	}
    }
    
    public void initMainWorkPlace() throws ManagerBeanException {
    	WorkPlace workPlace = null;
    	IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId() );
    	if ( (getMainAddress() != null) && (getMainAddress().getId() != null) ) {
    		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_PLACE_ADDRESS_ID), getMainAddress().getId() );
    	}
    	List<ITransferObject> list = bean.getList(criteria);
    	if (! list.isEmpty() ) {
    		workPlace = (WorkPlace) list.get(0);	
    	}
		setWorkplace(workPlace);
    }
    
    public void initMainDirStaff() throws ManagerBeanException {
    	RegistryDirStaff dirStaff = null;
    	IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), getEnterprise().getRegistry().getId() );
    	criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REPRESENTATIVE_LABOR), true );
    	List<ITransferObject> list = bean.getList(criteria);
    	if (! list.isEmpty() ) {
    		dirStaff = (RegistryDirStaff) list.get(0);	
    	}
    	setDirStaff(dirStaff);
    }
    
    public void onTreeViewSelect(ActionEvent event){
    	setTreeView(true);
    }
    
    public void onBasicViewSelect(ActionEvent event){
    	setTreeView(false);
    }
    
    public boolean isRegistryTypeLegal(){
    	return ((Enterprise)this.getTo()).getRegistry().getType()==RegistryType.LEGAL;
    }
 
	public AonFile getLogoFile() {
		return this.logoFile;
	}

	public void setLogoFile(AonFile logoFile) {
		this.logoFile = logoFile;
	}
	
	public AonFile getSignatureFile() {
		return signatureFile;
	}

	public void setSignatureFile(AonFile signatureFile) {
		this.signatureFile = signatureFile;
	}

	public void initLogo() throws ManagerBeanException {
		Company company  = (Company) ((CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME)).getTo();
		setLogoFile(null);
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
//		criteria.addEqualExpression(alias, getEnterprise().getRegistry().getId());
		criteria.addEqualExpression(alias, company.getRegistry().getId());
		String type = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			logoAttach = (RegistryAttachment) list.get(0);
			AonFile f = new AonFile();
			f.setKey(logoAttach.getId());
			f.setData(logoAttach.getData());
			f.setFileName(logoAttach.getDescription());
			f.setMimeType(logoAttach.getMimeType());
			setLogoFile(f);
		} else {
			logoAttach = new RegistryAttachment();
			logoAttach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
			logoAttach.setRegistry( getEnterprise().getRegistry() );
			logoAttach.setDescription("aon-logo");
		}
	}	

	public void initSignature() throws ManagerBeanException {
		Company company  = (Company) ((CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME)).getTo();
		setSignatureFile(null);
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
//		criteria.addEqualExpression(alias, getEnterprise().getRegistry().getId());
		criteria.addEqualExpression(alias, company.getRegistry().getId());
		String type = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.SIGNATURE);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			signatureAttach = (RegistryAttachment) list.get(0);
			AonFile f = new AonFile();
			f.setKey(signatureAttach.getId());
			f.setData(signatureAttach.getData());
			f.setFileName(signatureAttach.getDescription());
			f.setMimeType(signatureAttach.getMimeType());
			setSignatureFile(f);
		} else {
			signatureAttach = new RegistryAttachment();
			signatureAttach.setRegistryAttachmentType(RegistryAttachmentType.SIGNATURE);
			signatureAttach.setRegistry( getEnterprise().getRegistry() );
			signatureAttach.setDescription("aon-signature");
		}
	}	
	
    public void saveLogo() throws ManagerBeanException {
    	if (getLogoFile() != null && getLogoFile().getData() != null) {
			logoAttach.setData(getLogoFile().getData());
			MimeType mt = CompanyImagesController.getMimeType(getLogoFile().getFileName(), getLogoFile().getData());
			logoAttach.setMimeType(mt);
			IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
			bean.insertOrUpdate(logoAttach);
    	}
    }	

    public void saveSignature() throws ManagerBeanException {
    	if (getSignatureFile() != null && getSignatureFile().getData() != null) {
    		signatureAttach.setData(getSignatureFile().getData());
    		MimeType mt = CompanyImagesController.getMimeType(getSignatureFile().getFileName(), getSignatureFile().getData());
    		signatureAttach.setMimeType(mt);
    		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
    		bean.insertOrUpdate(signatureAttach);
    	}
    }	
    
	public void createCurrentLogoContent(OutputStream out, Object data) throws IOException {
		if (getLogoFile() != null && getLogoFile().getData() != null) {
			out.write(getLogoFile().getData());
		}
	}

	public void createCurrentSignatureContent(OutputStream out, Object data) throws IOException {
		if (getSignatureFile() != null && getSignatureFile().getData() != null) {
			out.write(getSignatureFile().getData());
		}
	}
 
	public void logoFileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
				file.delete();
			}
			f.setFileName( item.getFileName() );
			f.setMimeType( MimeType.get(item.getContentType()) );
			setLogoFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void signatureFileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
				file.delete();
			}
			f.setFileName( item.getFileName() );
			f.setMimeType( MimeType.get(item.getContentType()) );
			setSignatureFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	public void onLoadCalendar( ActionEvent event ) {
		// TODO implementar la busqueda del calendario. si la entidad no tiene calendario, 
		// buscar el calendario en sus entidades superiores: contract -> workplace -> enterprise -> agreement
		Enterprise e =(Enterprise)getTo();
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(ICompanyConstants.CALENDAR_CONTROLLER_NAME);
		controller.setEnterpriseName(e.getRegistry().getFullName());
		controller.setCalendarId(e.getCalendar().getId());
		controller.onInitialize(event);
	}	
	
	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}
	
	@Override
	public String formAction() {
		if ( isTreeView() ) {
			return (formAction != null) ? formAction : super.formAction() + treeTemplateSuffix;	
		}
		return super.formAction();
	}

	@Override
	public String searchAction() {
		return isTreeView() ? super.searchAction() + treeTemplateSuffix : super.searchAction();
	}
	
	public void onActivate(ActionEvent event) {
		Enterprise enterprise = (Enterprise) getTo();
		DomainSwitcher switcher = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		switcher.select(enterprise.getDomain(), null );
	}
	
	public boolean isDomainEnterprise() {
		DomainSwitcher switcher = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return (switcher.isParentDomain() && switcher.isDomainManagementAvailable());
	}
	
	
}