package com.code.aon.ui.admin;

import static com.code.aon.ui.common.ICommonConstants.AON_AIO_APPLICATION;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.code.aon.ui.registry.controller.DocumentManager.MAX_TOTAL_DOCUMENT_SIZE_VALUES;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Application;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DeprecatedBookingInfo implements Serializable, IBookingInfo {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DeprecatedBookingInfo.class);
	
	private Domain domain;
	
	private Domain parentDomain;
	
	private DomainApplicationInfo aioInfo;
	
	private DomainModuleInfo documental;
	
	private DomainModuleInfo documentPortal;

	private DomainModuleInfo payroll;
	
	private DomainModuleInfo payrollPortal;
	
	private List<SelectItem> payrollModules;
	
	private List<SelectItem> documentModules;
	
	private Module payrollModule;
	
	private Module documentModule;	
	
	public DeprecatedBookingInfo(Domain domain, Domain parentDomain) {
		this.domain = domain;
		this.parentDomain = parentDomain;
	}

	private Domain getDomain() {
		return domain;
	}

	private Domain getParentDomain() {
		return parentDomain;
	}

	@Override
	public void init() throws ManagerBeanException {
		this.aioInfo = DomainApplicationInfo.getApplicationInfos(getDomain(), AON_AIO_APPLICATION);
		updateModules(this.aioInfo, AonUtil.getRoleManager().isSysAdmin());
	}

	private boolean isConsultancyParent() throws ManagerBeanException {
		Domain parent = getParentDomain();
		return parent != null && parent.getType()==DomainType.CONSULTANCY;
	}
		
	private List<Module> getDisabledModules() throws ManagerBeanException {
		List<Module> list = new LinkedList<Module>();
		Domain parent = getParentDomain();
		if ( parent != null ) {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			Application application = DomainApplicationInfo.getApplication(AON_AIO_APPLICATION);
			Integer da = AdminUtil.getDomainApplication(parent.getId(), application.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN), parent.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID), da);
			for( ITransferObject to : bean.getList(criteria) ) {
				DomainApplicationModule dam = (DomainApplicationModule) to;
				list.add(dam.getModule());
			}
			if ( isConsultancyParent() ) {
				list.remove(Module.DOCUMENT);
				list.remove(Module.PAYROLL);
				list.remove(Module.DOCUMENT_PORTAL);
				list.remove(Module.PAYROLL_PORTAL);
				list.remove(Module.CONTRATA);
			}
		}
		return list;
	}
		
	private void updateModules( DomainApplicationInfo appInfo, boolean sysAdmin ) throws ManagerBeanException {
		if (! sysAdmin ) {
			List<Module> disabledModules = getDisabledModules();
			for( Module module : disabledModules ) {
				DomainModuleInfo info = appInfo.getModuleInfo(module);
				if ( info == null ) {
					info = new DomainModuleInfo(module);
					appInfo.getApplicationModules().add(info);
					LOGGER.debug( "Added: {}", module );
				}			
				info.setChecked(true);
				info.setDisabled(true);
				LOGGER.debug( "Checked and disabled: {}", info );
			}
			List<Module> visibleModules = getVisibleModules(appInfo.getApplication().getId());
			for( int i = appInfo.getApplicationModules().size()-1; i >= 0; i-- ) {
				DomainModuleInfo info =  appInfo.getApplicationModules().get(i);
				if (! visibleModules.contains(info.getModule()) ) {
					appInfo.removeModuleInfo(info);
					LOGGER.debug( "Removed from list: {}", info );
				}
			}
		} else {
			DomainModuleInfo info =  appInfo.getModuleInfo(Module.CONFIGURATION);
			appInfo.removeModuleInfo(info);
		}
		joinManagementTreasury();
		initPortalModules();
		appInfo.updateApplicationModules();
		appInfo.sortApplicationModules();
		if (! sysAdmin ) {
			DomainModuleInfo infoweb = this.aioInfo.getModuleInfo(Module.INFOWEB);
			if ( infoweb != null ) {
				this.aioInfo.getApplicationModules().remove(infoweb);
			}
		}
	}
	
	private void joinManagementTreasury() throws ManagerBeanException {
		DomainModuleInfo management = this.aioInfo.getModuleInfo(Module.MANAGEMENT);
		DomainModuleInfo treasury = this.aioInfo.getModuleInfo(Module.TREASURY);
		if ( management != null && treasury != null ) {
			DomainModuleInfoManagement dmim = new DomainModuleInfoManagement(management, treasury);
			this.aioInfo.getApplicationModules().remove(management);
			this.aioInfo.getApplicationModules().remove(treasury);
			this.aioInfo.getApplicationModules().add(dmim);
			dmim.setDescription(AonUtil.getMessage(ICommonMessages.MODULE_MANAGEMENT_FINANCE));				
		}
	}	

	private void initPortalModules() throws ManagerBeanException {
		setDocumentModule(null);
		setPayrollModule(null);
		this.documental = this.aioInfo.getModuleInfo(Module.DOCUMENT);
		this.documentPortal = this.aioInfo.getModuleInfo(Module.DOCUMENT_PORTAL);
		this.payroll = this.aioInfo.getModuleInfo(Module.PAYROLL);
		this.payrollPortal = this.aioInfo.getModuleInfo(Module.PAYROLL_PORTAL);		
		if ( isShowDocumentSelection() ) {
			getDocumental().setRendered(false);
			documentPortal.setRendered(false);
			if ( getDocumental().isChecked() ) {				
				setDocumentModule(Module.DOCUMENT);
				documentPortal.setChecked(false);
			}
			if ( documentPortal.isChecked() ) {
				setDocumentModule(Module.DOCUMENT_PORTAL);
			}
		}
		if ( isShowPayrollSelection() ) {		
			payroll.setRendered(false);
			payrollPortal.setRendered(false);
			if ( payroll.isChecked() ) {				
				setPayrollModule(Module.PAYROLL);
				payrollPortal.setChecked(false);
			}
			if ( payrollPortal.isChecked() ) {
				setPayrollModule(Module.PAYROLL_PORTAL);
			}
		}
		if ( (this.payrollPortal != null) && getDomain().isDomainManagement() && (getDomain().getType() == DomainType.CONSULTANCY) ) {
			String description = AonUtil.getMessage(ICommonMessages.ADMIN_GLOBAL_PORTAL);
			this.payrollPortal.setDescription(description);
		}
	}
		
	public boolean isShowApplications() {
		return this.aioInfo.isChecked();
	}

	@Override
	public List<DomainModuleInfo> getBookingModules() {
		return this.aioInfo.getApplicationModules();
	}	
	
	@Override
	public List<DomainModuleInfo> getDisplayModules() {
		return Collections.emptyList();
	}

	private DomainModuleInfo getDocumental() {
		return documental;
	}

	public Module getDocumentModule() {
		return documentModule;
	}

	public void setDocumentModule(Module documentModule) {
		this.documentModule = documentModule;
	}	

	public Module getPayrollModule() {
		return payrollModule;
	}

	public void setPayrollModule(Module payrollModule) {
		this.payrollModule = payrollModule;
	}
	
	public boolean isShowDocumentSelection() {
		if ( documental != null && documentPortal != null ) {
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			return ds.isChildDomain();
		}
		return false;
	}

	public boolean isShowPayrollSelection() {
		if ( payroll != null && payrollPortal != null ) {
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			return ds.isChildDomain();
		}
		return false;
	}	
	
	public List<SelectItem> getPayrollModules() {
		if ( payrollModules == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			payrollModules = new LinkedList<SelectItem>();
			payrollModules.add(new SelectItem(Module.PAYROLL_PORTAL, Module.PAYROLL_PORTAL.getName(locale)));		
			payrollModules.add(new SelectItem(Module.PAYROLL, Module.PAYROLL.getName(locale)));
		}
		return payrollModules;
	}

	public List<SelectItem> getDocumentModules() {
		if ( documentModules == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			documentModules = new LinkedList<SelectItem>();
			documentModules.add(new SelectItem(Module.DOCUMENT_PORTAL, Module.DOCUMENT_PORTAL.getName(locale)));		
			documentModules.add(new SelectItem(Module.DOCUMENT, Module.DOCUMENT.getName(locale)));			
		}
		return documentModules;
	}	

	private void updatePortalModules() throws ManagerBeanException {
		if ( isShowDocumentSelection()) {
			getDocumental().setChecked(false);
			documentPortal.setChecked(false);
			if ( getDocumentModule() == Module.DOCUMENT ) {
				getDocumental().setChecked(true);
			} else if ( getDocumentModule() == Module.DOCUMENT_PORTAL ) {
				documentPortal.setChecked(true);
			}
		}
		if ( isShowPayrollSelection() ) {
			payroll.setChecked(false);
			payrollPortal.setChecked(false);
			if ( getPayrollModule() == Module.PAYROLL ) {
				payroll.setChecked(true);
			} else if ( getPayrollModule() == Module.PAYROLL_PORTAL ) {
				payrollPortal.setChecked(true);
			}
		}
	}	

	@Override
	public void save() throws ManagerBeanException {
		updatePortalModules();
		if ( this.aioInfo.isChecked() ) {
			this.aioInfo.register();
		} else {
			this.aioInfo.unregister();
		}
	}	
		
	public void onDocumentalChanged( ActionEvent event ) {
		if (! getDocumental().isChecked() ) {
			getDomain().setMaxTotalDocumentSize(DocumentManager.MINIMUM_MAX_TOTAL_DOCUMENT_SIZE);
		}
	}

	public void onPortalDocumentalChanged( ActionEvent event ) {
		getDocumental().setChecked(getDocumentModule() == Module.DOCUMENT);
		onDocumentalChanged(event);
	}
	
	public DomainApplicationInfo getAioInfo() {
		return aioInfo;
	}

	public List<SelectItem> getMaxTotalDocumentSizes() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (int i = 0; i < MAX_TOTAL_DOCUMENT_SIZE_VALUES.length; i++) {
			int value = MAX_TOTAL_DOCUMENT_SIZE_VALUES[i];
			String name = FileUtils.byteCountToDisplaySize(value*FileUtils.ONE_MB);
			SelectItem item = new SelectItem(value, name);
			if (i > 0) {
				boolean disabled = (getDocumental() == null) || !getDocumental().isChecked(); 
				item.setDisabled(disabled);
			}
			list.add(item);					
		}
		return list;
	}		
	
	private List<Module> getVisibleModules( Integer applicationId ) throws ManagerBeanException {
		List<Module> list = new LinkedList<Module>();
		DomainType type = getDomain().getType();
		if ( type != DomainType.ADMIN ) {
			if ( getDomain().isDomainManagement() && (type == DomainType.CONSULTANCY)) {
				list.add(Module.FISCAL);
				list.add(Module.PAYROLL);
				list.add(Module.PAYROLL_PORTAL);
				list.add(Module.DOCUMENT_PORTAL);
				list.add(Module.CONTRATA);
			} else {
				list.add(Module.ACCOUNTING);
				list.add(Module.COMMERCIAL);
				list.add(Module.GROUPWARE);
				list.add(Module.MANAGEMENT);
				list.add(Module.MARKETING);
				list.add(Module.TREASURY);
				list.add(Module.INFOWEB);
				switch ( type ) {
					case ENTERPRISE:
						list.add(Module.WAREHOUSE);
						list.add(Module.POS);
						break;
					case GARAGE:
						list.add(Module.GARAGE);
						list.add(Module.WAREHOUSE);
						list.add(Module.POS);
						break;
					case ACADEMY:
						list.add(Module.ACADEMY);
						list.add(Module.WAREHOUSE);
						list.add(Module.POS);
						break;
					case HOTEL:
						list.add(Module.HOTEL);
						list.add(Module.WAREHOUSE);
						list.add(Module.POS);
						break;
					case CONSULTANCY:
						list.add(Module.FISCAL);
						list.add(Module.PAYROLL);
						break;
				}
				if ( getParentDomain() != null)  {
					Integer parentDomainId = getParentDomain().getId();
					if (DomainSwitcher.getDomainType(parentDomainId) == DomainType.CONSULTANCY) {
						if ( !list.contains(Module.PAYROLL) && AuditManager.hasModule(parentDomainId, applicationId, Module.PAYROLL) ) {
							list.add(Module.PAYROLL);
						}
						if (AuditManager.hasModule(parentDomainId, applicationId, Module.PAYROLL_PORTAL) ) {
							list.add(Module.PAYROLL_PORTAL);
						}
						if (AuditManager.hasModule(parentDomainId, applicationId, Module.DOCUMENT_PORTAL) ) {
							list.add(Module.DOCUMENT_PORTAL);
						}
						if (AuditManager.hasModule(parentDomainId, applicationId, Module.CONTRATA) ) {
							list.add(Module.CONTRATA);
						}
					}
				}			
			}
		}
		list.add(Module.DOCUMENT);
		return list;
	}	
	
}
