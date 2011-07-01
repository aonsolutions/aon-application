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
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.calendar.controller.CalendarController;

public class EnterpriseController extends RegistryController implements ICompanyConstants {
	
	private RegistryInfo info = new RegistryInfo();

	private boolean showActivityNode;
	private WorkPlace workplace;
	private RegistryDirStaff dirStaff;
	
	private boolean treeView;
	
	private AonFile aonFile;
	private RegistryAttachment attach;
	
    public boolean isTreeView() {
		return treeView;
	}

	public void setTreeView(boolean treeView) {
		this.treeView = treeView;
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
		criteria.addEqualExpression(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), enterprise.getRegistry().getId());
		criteria.addOrder(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS));
		List<ITransferObject> list = registryAddressBean.getList(criteria);
		for (ITransferObject to : list) {
			RegistryAddress rAddress = (RegistryAddress)to;
			addresses.add(new SelectItem(rAddress, rAddress.getShortAddress()));
		}
    	return addresses;
    }	
    
    /**
     * Gets the CCCs of the enterprise.
     * 
     * @return the CCCs of the enterprise
     * @throws ManagerBeanException 
     */
//    public List<SelectItem> getCCCs() throws ManagerBeanException {
//    	LinkedList<SelectItem> cccs = new LinkedList<SelectItem>();
//    	Enterprise enterprise = (Enterprise) getTo();
//		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), enterprise.getId());
//		criteria.addOrder(bean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_CCC));
//		List<ITransferObject> list = bean.getList(criteria);
//		for (ITransferObject to : list) {
//			EnterpriseCCC ccc = (EnterpriseCCC)to;
//			cccs.add(new SelectItem(ccc, ccc.getCcc()));
//		}
//    	return cccs;
//    }	    

//	private void loadMainActivity() throws ManagerBeanException {
//		IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(activityBean.getFieldName(ICompanyAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), getEnterprise().getId());
//		criteria.addEqualExpression(activityBean.getFieldName(ICompanyAlias.ENTERPRISE_ACTIVITY_TYPE), EnterpriseActivityType.PRINCIPAL);
//		List<ITransferObject> activities = activityBean.getList(criteria);
//		if (! activities.isEmpty() ) {
//			setActivity( (EnterpriseActivity) activities.get(0) );
//			IManagerBean cccBean = BeanManager.getManagerBean(EnterpriseCCC.class);
//			Criteria cccCriteria = new Criteria();
//			cccCriteria.addEqualExpression(cccBean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_ACTIVITY_ID), getActivity().getId());
//			cccCriteria.addEqualExpression(cccBean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_TYPE), CCCType.PRINCIPAL);
//			List<ITransferObject> cccs = cccBean.getList(cccCriteria);
//			if (! cccs.isEmpty() ) {
//				setCcc( (EnterpriseCCC) cccs.get(0) );
//			}
//			this.showActivityNode = (activities.size() > 1) || (cccs.size() > 1);
//		}
//	}    
	
	public void reset() {
    	this.showActivityNode = false;
    	setWorkplace(null);
    	setDirStaff(null);
    	setAonFile(null);
    	this.info.reset();
	}
    
//    public void initMainActiviy() throws ManagerBeanException {
//    	if (! isNew() ) {
//    		loadMainActivity();
//    	}
//    }
    
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
    	criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId() );
    	if ( (getMainAddress() != null) && (getMainAddress().getId() != null) ) {
    		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ADDRESS_ID), getMainAddress().getId() );
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
    	criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), getEnterprise().getRegistry().getId() );
    	criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_DIR_STAFF_REPRESENTATIVE_LABOR), true );
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
 
	public AonFile getAonFile() {
		return this.aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}
	
	public void initLogo() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, getEnterprise().getId());
		String type = bean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			attach = (RegistryAttachment) list.get(0);
			AonFile f = new AonFile();
			f.setData(attach.getData());
			f.setFileName(attach.getDescription());
			f.setMimeType(attach.getMimeType());
			setAonFile(f);
		} else {
			attach = new RegistryAttachment();
			attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
			attach.setRegistry( getEnterprise().getRegistry() );
			attach.setDescription("aon-logo");
		}
	}	
	
    public void saveLogo() throws ManagerBeanException {
    	if (getAonFile() != null && getAonFile().getData() != null) {
			attach.setData(getAonFile().getData());
			MimeType mt = CompanyImagesController.getMimeType(getAonFile().getFileName(), getAonFile().getData());
			attach.setMimeType(mt);
			IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
			bean.insertOrUpdate(attach);
    	}
    }	
    
	public void createCurrentLogoContent(OutputStream out, Object data) throws IOException {
		if (getAonFile() != null && getAonFile().getData() != null) {
			out.write(getAonFile().getData());
		}
	}
 
	public void fileUploaded(UploadEvent event) {
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
			setAonFile(f);
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
	
}