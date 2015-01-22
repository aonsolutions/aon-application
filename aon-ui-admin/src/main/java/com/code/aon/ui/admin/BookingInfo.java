package com.code.aon.ui.admin;

import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonConstants.AON_AIO_APPLICATION;
import static com.code.aon.ui.registry.controller.DocumentManager.MAX_TOTAL_DOCUMENT_SIZE_VALUES;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FileUtils;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.admin.controller.DomainController;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.util.AonUtil;

public class BookingInfo implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Domain domain;
	
	private Domain parentDomain;
	
	private DomainApplicationInfo aioInfo;
	
	private List<DomainModuleInfo> bookingModules;
	
	private List<DomainModuleInfo> displayModules;
	
	private DomainModuleInfo documental;
	
	private int externalApplications;
	
	public BookingInfo(Domain domain, Domain parentDomain) {
		this.domain = domain;
		this.parentDomain = parentDomain;
	}

	private Domain getDomain() {
		return domain;
	}

	private Domain getParentDomain() {
		return parentDomain;
	}
	
	private void setRendered( List<DomainModuleInfo> modulesInfos, boolean value ) {
		for ( DomainModuleInfo dmi : modulesInfos ) {
			dmi.setRendered(value);
		}				
	}
	
	private void resetUnusedModules() {
		for ( DomainModuleInfo dmi : aioInfo.getApplicationModules() ) {
			if(! dmi.isRendered() ) {
				dmi.setChecked(false);	
			}
		}				
	}

	public void init() throws ManagerBeanException {
		this.aioInfo = DomainApplicationInfo.getApplicationInfos(getDomain(), AON_AIO_APPLICATION);
		setRendered(aioInfo.getApplicationModules(), false);
		this.bookingModules = calculateBookingModules();
		setRendered(this.bookingModules, true);
		this.displayModules = calculateDisplayModules();
		setRendered(this.displayModules, true);
		updateModules(this.aioInfo, AonUtil.getRoleManager().isSysAdmin());
		resetUnusedModules();
		initExternalApplications();
	}
	
	private void initExternalApplications() {
		externalApplications = AppParamUtil.getValueAsInt(AppParam.AON_EXTERNAL_APPLICATIONS, getDomain().getId());
		boolean tirant = isTirant();
		boolean dehOnline = !getDomain().isDomainManagement() && isDehOnline();
		externalApplications = 0;
		setTirant(tirant);
		setDehOnline(dehOnline);
	}	
		
	public List<DomainModuleInfo> getBookingModules() {
		return bookingModules;
	}
	
	public List<DomainModuleInfo> getDisplayModules() {
		return displayModules;
	}
	
	public void save() throws ManagerBeanException {
		saveExternalApplications();
		if ( isAonOne() ) {
			updateAonOneModules();
		}
		if ( this.aioInfo.isChecked() ) {
			this.aioInfo.register();
		} else {
			this.aioInfo.unregister();
		}
		init();
	}	
	
	private void saveExternalApplications() {
		if ( externalApplications != 0 ) {
			AppParamUtil.insertParameter(AppParam.AON_EXTERNAL_APPLICATIONS, externalApplications);	
		} else {
			AppParamUtil.removeParameter(AppParam.AON_EXTERNAL_APPLICATIONS);
		}
		if (! isDehOnline() ) {
			AppParamUtil.removeParameter(AppParam.AON_DEH_ONLINE_USER);
			AppParamUtil.removeParameter(AppParam.AON_DEH_ONLINE_PASSWORD);			
		}
	}		
	
	public boolean isShowDisplayModules() {
		if ( ((getDomain().getType() == DomainType.CONSULTANCY) && getDomain().isDomainManagement()) || isAonOne() ) {
			return false;
		}
		return true;
	}

	public void onSaveDisplayModules( ActionEvent event ) throws ManagerBeanException {
		boolean updated = this.aioInfo.updateApplicationModules(getDisplayModules());
		if ( updated ) {
			DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
			dc.reloadModuleConfiguration(AonUtil.getAuthPrincipal());					
		}
	}	
	
	public DomainApplicationInfo getAioInfo() {
		return aioInfo;
	}

	private DomainModuleInfo getDocumental() {
		return documental;
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

	public void onDocumentalChanged( ActionEvent event ) {
		if (! getDocumental().isChecked() ) {
			getDomain().setMaxTotalDocumentSize(DocumentManager.MINIMUM_MAX_TOTAL_DOCUMENT_SIZE);
		}
	}
	
	private void updateModules( DomainApplicationInfo appInfo, boolean sysAdmin ) throws ManagerBeanException {
		convertToNewCofiguration();
		appInfo.updateApplicationModules();
		this.documental = this.aioInfo.getModuleInfo(Module.DOCUMENT);
	}	

	private void convertToNewCofiguration() throws ManagerBeanException {
		DomainModuleInfo contrata =  aioInfo.getModuleInfo(Module.CONTRATA);
		contrata.setChecked(false);

		DomainModuleInfo payrollPortal = this.aioInfo.getModuleInfo(Module.PAYROLL_PORTAL);
		DomainModuleInfo documentPortal = this.aioInfo.getModuleInfo(Module.DOCUMENT_PORTAL);
		if ( (domain.getType() == DomainType.CONSULTANCY ) && documentPortal.isChecked() ) {
			payrollPortal.setChecked(true);
		}
		documentPortal.setChecked(false);
		String description = AonUtil.getMessage(ICommonMessages.ADMIN_GLOBAL_PORTAL);
		payrollPortal.setDescription(description);
	}
	
	private void updateAonOneModules() {
		for( DomainModuleInfo dmi : aioInfo.getApplicationModules() ) {
			dmi.setChecked(false);
		}
		DomainModuleInfo aonOneModule = aioInfo.getModuleInfo(Module.AON_ONE);
		aonOneModule.setChecked(true);		
	}
	
	private List<DomainModuleInfo> calculateBookingModules() {
		List<DomainModuleInfo> list = new LinkedList<DomainModuleInfo>();
		switch ( getDomain().getType() ) {
			case GENERIC:
				list.add(aioInfo.getModuleInfo(Module.FISCAL));
				list.add(aioInfo.getModuleInfo(Module.PAYROLL));
				list.add(aioInfo.getModuleInfo(Module.HOTEL));
				list.add(aioInfo.getModuleInfo(Module.ACADEMY));
				list.add(aioInfo.getModuleInfo(Module.GARAGE));
				break;
			case CONSULTANCY:
				list.add(aioInfo.getModuleInfo(Module.FISCAL));
				list.add(aioInfo.getModuleInfo(Module.ACCOUNTING));
				list.add(aioInfo.getModuleInfo(Module.PAYROLL));
				list.add(aioInfo.getModuleInfo(Module.DOCUMENT));
				list.add(aioInfo.getModuleInfo(Module.PAYROLL_PORTAL));
				break;
			case OFFICE:
				list.add(aioInfo.getModuleInfo(Module.FISCAL));
				list.add(aioInfo.getModuleInfo(Module.PAYROLL));
				break;
			case ENTERPRISE:
				list.add(aioInfo.getModuleInfo(Module.AON_ONE));
				break;
			case GARAGE:
				aioInfo.getModuleInfo(Module.GARAGE).setChecked(true);
				break;
			case ACADEMY:
				aioInfo.getModuleInfo(Module.ACADEMY).setChecked(true);
				break;
			case HOTEL:
				aioInfo.getModuleInfo(Module.HOTEL).setChecked(true);
				break;
		}
		this.aioInfo.sortApplicationModules(list);
		return list;
	}
	
	public boolean isAonOne() {
		DomainModuleInfo aonOne =  aioInfo.getModuleInfo(Module.AON_ONE);
		return (getDomain().getType() == DomainType.ENTERPRISE) && aonOne.isChecked();
	}

	public void setAonOne( boolean value) {
		DomainModuleInfo aonOne =  aioInfo.getModuleInfo(Module.AON_ONE);
		aonOne.setChecked(value);
	}
	
	private List<DomainModuleInfo> calculateDisplayModules() throws ManagerBeanException {
		List<DomainModuleInfo> list = new LinkedList<DomainModuleInfo>();
		list.add(aioInfo.getModuleInfo(Module.MARKETING));
		list.add(aioInfo.getModuleInfo(Module.COMMERCIAL));
		list.add(aioInfo.getModuleInfo(Module.MANAGEMENT));
		list.add(aioInfo.getModuleInfo(Module.TREASURY));
		list.add(aioInfo.getModuleInfo(Module.WAREHOUSE));
		list.add(aioInfo.getModuleInfo(Module.GROUPWARE));
		list.add(aioInfo.getModuleInfo(Module.POS));
		if ( getDomain().getType() != DomainType.CONSULTANCY ) {
			list.add(aioInfo.getModuleInfo(Module.ACCOUNTING));
			list.add(aioInfo.getModuleInfo(Module.DOCUMENT));
		}
		if ( getDomain().getType() == DomainType.GENERIC ) {
			list.add(aioInfo.getModuleInfo(Module.INFOWEB));
		}
		if ( getParentDomain() != null ) {
			Integer parentDomainId = getParentDomain().getId();
			Integer applicationId = aioInfo.getApplication().getId();
			DomainModuleInfo fiscal = aioInfo.getModuleInfo(Module.FISCAL);
			if ( !this.bookingModules.contains(fiscal) &&
				AuditManager.hasModule(parentDomainId, applicationId, Module.FISCAL) ) {
				list.add(aioInfo.getModuleInfo(Module.FISCAL));
			}
			DomainModuleInfo payroll = aioInfo.getModuleInfo(Module.PAYROLL);
			if ( !this.bookingModules.contains(payroll) &&
					AuditManager.hasModule(parentDomainId, applicationId, Module.PAYROLL) ) {
				list.add(aioInfo.getModuleInfo(Module.PAYROLL));
			}
		}
		this.aioInfo.sortApplicationModules(list);
		return list;
	}
	
	private boolean getExternalApplicationsValue(int bitwise) {
		return (externalApplications & bitwise) != 0;
	}

	private void setExternalApplicationsValue(int bitwise, boolean value) {
		if ( value ) {
			this.externalApplications |= bitwise;	
		} else {
			this.externalApplications &= (~bitwise);
		}
	}
	
	public boolean isDehOnline() {
		return getExternalApplicationsValue(ICommonConstants.DEH_ONLINE_EXTERNAL_APP);
	}

	public void setDehOnline(boolean value) {
		setExternalApplicationsValue(ICommonConstants.DEH_ONLINE_EXTERNAL_APP, value);
	}

	public boolean isTirant() {
		return getExternalApplicationsValue(ICommonConstants.TIRANT_EXTERNAL_APP);
	}

	public void setTirant(boolean value) {
		setExternalApplicationsValue(ICommonConstants.TIRANT_EXTERNAL_APP, value);
	}		
	
}
