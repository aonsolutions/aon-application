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
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
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
import com.esferalia.aon.occam.api.AON;

public class BookingInfo implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BookingInfo.class);
	
	private final static Module[] AON_ONE_MODULES = {Module.CALL_CENTER, Module.ACCOUNTING, Module.DOCUMENT};

	private Domain domain;
	
	private Domain parentDomain;
	
	private DomainApplicationInfo aioInfo;
	
	private List<DomainModuleInfo> bookingModules;
	
	private List<DomainModuleInfo> displayModules;
	
	private DomainModuleInfo documental;
	
	private int externalApplications;
	
	private Domain payerDomain;
	
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
	
	private void resetUnusedModules() {
		List<DomainModuleInfo> modules = new LinkedList<DomainModuleInfo>(this.bookingModules);
		modules.addAll(this.displayModules);
		for ( DomainModuleInfo dmi : aioInfo.getApplicationModules() ) {
			if(! modules.contains(dmi) ) {
				dmi.setChecked(false);	
			}
		}				
	}

	public boolean init() throws ManagerBeanException {
		initExternalApplications();
		initPayerDomain();
		this.aioInfo = DomainApplicationInfo.getApplicationInfos(getDomain(), AON_AIO_APPLICATION);
		this.bookingModules = calculateBookingModules();
		this.displayModules = calculateDisplayModules();
		return updateModules();
	}
	
	private void initExternalApplications() {
		externalApplications = AppParamUtil.getValueAsInt(AppParam.AON_EXTERNAL_APPLICATIONS, getDomain().getId());
		boolean tirant = isTirant();
		boolean dehOnline = !getDomain().isDomainManagement() && isDehOnline();
		externalApplications = 0;
		setTirant(tirant);
		setDehOnline(dehOnline);
	}	
	
	private void initPayerDomain() throws ManagerBeanException {
		this.payerDomain = null;
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Integer id = AppParamUtil.getValueAsInteger(AppParam.AON_DOMAIN_PAYER);
		if ( id != null ) {
			this.payerDomain = (Domain) bean.get(id);
		}
		if ( this.payerDomain == null ) {
			this.payerDomain = (Domain) bean.createNewTo();
		}
	}	
		
	public List<DomainModuleInfo> getBookingModules() {
		return bookingModules;
	}

	public List<DomainModuleInfo> getSelectableBookingModules() {
		List<DomainModuleInfo> list = new LinkedList<DomainModuleInfo>();
		for( DomainModuleInfo dim : bookingModules ) {
			if ( dim.isRendered() ) {
				list.add(dim);
			}
		}
		return list;
	}
	
	public List<DomainModuleInfo> getDisplayModules() {
		return displayModules;
	}
	
	public void save() throws ManagerBeanException {
		saveExternalApplications();
		savePayerDomain();
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
	
	private void savePayerDomain() throws ManagerBeanException {
		if ( (this.payerDomain != null) && (this.payerDomain.getId() != null) ) {
			String id = String.valueOf(this.payerDomain.getId());
			AppParamUtil.insertParameter(AppParam.AON_DOMAIN_PAYER, id );			
		} else {
			AppParamUtil.removeParameter(AppParam.AON_DOMAIN_PAYER);
		}
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

	public DomainModuleInfo getDocumental() {
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
	
	private boolean updateModules() throws ManagerBeanException {
		convertToNewCofiguration();
		this.documental = this.aioInfo.getModuleInfo(Module.DOCUMENT);
		if ( getDomain().getType() == DomainType.ENTERPRISE ) {
			updateEnterpriseModules();	
		}
		resetUnusedModules();
		return this.aioInfo.updateApplicationModules();
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
			if (! ArrayUtils.contains(AON_ONE_MODULES, dmi.getModule()) ) {
				dmi.setChecked(false);	
			}
		}
		DomainModuleInfo aonOneModule = aioInfo.getModuleInfo(Module.AON_ONE);
		aonOneModule.setChecked(true);		
	}
	
	private List<DomainModuleInfo> calculateBookingModules() {
		List<DomainModuleInfo> list = new LinkedList<DomainModuleInfo>();
		list.add(aioInfo.getModuleInfo(Module.ACCOUNTING));
		com.esferalia.aon.occam.api.model.Domain domain = AON.getDomain(AonUtil.getDomainName(), aioInfo.getDomain().getId(), AonUtil.getRemoteUser());
		if(domain != null && domain.getDomainManagement() != 1)
			list.add(aioInfo.getModuleInfo(Module.CALL_CENTER));
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
				list.add(aioInfo.getModuleInfo(Module.PAYROLL));
				list.add(aioInfo.getModuleInfo(Module.DOCUMENT));
				list.add(aioInfo.getModuleInfo(Module.PAYROLL_PORTAL));
				break;
			case OFFICE:
				list.add(aioInfo.getModuleInfo(Module.FISCAL));
				list.add(aioInfo.getModuleInfo(Module.PAYROLL));
				break;
			case ENTERPRISE:
				DomainModuleInfo aonOne = aioInfo.getModuleInfo(Module.AON_ONE);
				list.add(aonOne);
				aonOne.setRendered(false);
				break;
			case GARAGE:
				DomainModuleInfo garage = aioInfo.getModuleInfo(Module.GARAGE); 
				list.add(garage);
				garage.setRendered(false);
				garage.setChecked(true);
				break;
			case ACADEMY:
				DomainModuleInfo academy = aioInfo.getModuleInfo(Module.ACADEMY); 
				list.add(academy);
				academy.setRendered(false);
				academy.setChecked(true);
				break;
			case HOTEL:
				DomainModuleInfo hotel = aioInfo.getModuleInfo(Module.HOTEL); 
				list.add(hotel);
				hotel.setRendered(false);
				hotel.setChecked(true);
				break;
		default:
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
		list.add(aioInfo.getModuleInfo(Module.CRM));
		list.add(aioInfo.getModuleInfo(Module.MANAGEMENT));
		list.add(aioInfo.getModuleInfo(Module.TREASURY));
		list.add(aioInfo.getModuleInfo(Module.WAREHOUSE));
		list.add(aioInfo.getModuleInfo(Module.GROUPWARE));
		list.add(aioInfo.getModuleInfo(Module.POS));
		if ( getDomain().getType() != DomainType.CONSULTANCY ) {
			list.add(aioInfo.getModuleInfo(Module.DOCUMENT));
		}
		if ( getDomain().getType() == DomainType.GENERIC ) {
			list.add(aioInfo.getModuleInfo(Module.INFOWEB));
			list.add(aioInfo.getModuleInfo(Module.ECOMMERCE));
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

	public Domain getPayerDomain() {
		return payerDomain;
	}

	public void setPayerDomain(Domain payerDomain) {
		this.payerDomain = payerDomain;
	}
		
	private void updateEnterpriseModules() throws ManagerBeanException {
		if (isAonOne()) {
			this.bookingModules.clear();
			this.bookingModules.add(aioInfo.getModuleInfo(Module.AON_ONE));
			this.bookingModules.add(aioInfo.getModuleInfo(Module.ACCOUNTING));
			this.bookingModules.add(aioInfo.getModuleInfo(Module.CALL_CENTER));
			this.bookingModules.add(this.documental);
			this.displayModules.remove(this.documental);
		} else {
			this.bookingModules.clear();
			this.bookingModules.add(aioInfo.getModuleInfo(Module.AON_ONE));
			this.bookingModules.add(aioInfo.getModuleInfo(Module.ACCOUNTING));
			this.bookingModules.add(aioInfo.getModuleInfo(Module.CALL_CENTER));
			if ( getParentDomain() != null ) {
				Integer parentDomainId = getParentDomain().getId();
				Integer applicationId = aioInfo.getApplication().getId();
				boolean parentUser = !getDomain().getId().equals(AonUtil.getAuthPrincipal().getDomainId());				
				DomainModuleInfo fiscal = aioInfo.getModuleInfo(Module.FISCAL);
				if ( AuditManager.hasModule(parentDomainId, applicationId, Module.FISCAL) ) {
					this.bookingModules.add(fiscal);
					fiscal.setDisabled(!parentUser);
				}
				DomainModuleInfo payroll = aioInfo.getModuleInfo(Module.PAYROLL);
				if ( AuditManager.hasModule(parentDomainId, applicationId, Module.PAYROLL) ) {
					this.bookingModules.add(payroll);
					payroll.setDisabled(!parentUser);
				}
			}			
			if (! this.displayModules.contains(this.documental) ) {
				this.displayModules.add(this.documental);
			}
		}
		this.aioInfo.sortApplicationModules(this.bookingModules);
		this.aioInfo.sortApplicationModules(this.displayModules);
	}
	
	public void onAonOneChanged( ActionEvent event ) {
		try {
			updateEnterpriseModules();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
}
