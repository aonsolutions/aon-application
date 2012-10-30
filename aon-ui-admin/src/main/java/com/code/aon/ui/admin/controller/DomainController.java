package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.AON_AIO_APPLICATION;
import static com.code.aon.ui.admin.controller.IAdminConstants.AON_PLATFORM;
import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_NAME_DUPLICATED;
import static com.code.aon.ui.common.ICommonConstants.AON_CUSTOMIZE_ID;
import static com.code.aon.ui.common.ICommonConstants.AON_CUSTOMIZE_OEM;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.company.Company;
import com.code.aon.config.Application;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.DomainApplicationInfo;
import com.code.aon.ui.admin.DomainModuleInfo;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainController.class);
	
	public final static int DEFAULT_MAX_TOTAL_DOCUMENT_SIZE = 100;	
	
	private DomainApplicationInfo aioInfo;
	
	private DomainModuleInfo documental;

	private List<DomainApplicationInfo> applicationInfos;
	
	private DomainApplication domainApplication;
		
	private boolean OEM;
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	public Domain getDomain() {
		return (Domain) getTo();
	}	
	
	public Domain getParentDomain() {
		Domain parent = getDomain().getParent();
		if ( (parent != null) && (parent.getId() != null) ) {
			return parent;
		}		
		return null;
	}

	public void onInit( ActionEvent event ) {
		initDomainApplication();
		getAdmin().resetTermsOfServiceAccepted();
	}
	
	public DomainModuleInfo getDocumental() {
		return documental;
	}

	public void onDocumentalChanged( ActionEvent event ) {
		if (! documental.isChecked() ) {
			getDomain().setMaxTotalDocumentSize(DEFAULT_MAX_TOTAL_DOCUMENT_SIZE);
		}
	}

	public List<DomainApplicationInfo> getApplicationInfos() {
		return applicationInfos;
	}
	
	private boolean isConsultancyParent() throws ManagerBeanException {
		Domain parent = getParentDomain();
		return (parent != null) && (parent.getType()  == DomainType.CONSULTANCY);
	}

	private boolean hasModule( Domain domain, Module module ) throws ManagerBeanException {
		Application application = DomainApplicationInfo.getApplication(AON_AIO_APPLICATION);
		return AuditManager.hasModule(domain.getId(), application.getId(), module);			
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
				if ( hasModule(getParentDomain(), Module.FISCAL) ) {
					list.add(Module.MANAGEMENT);
					list.add(Module.TREASURY);									
				}								
			}
		}
		return list;
	}	
	
	private void updateModules( DomainApplicationInfo appInfo ) throws ManagerBeanException {
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
		List<Module> visibleModules = AuditManager.getVisibleModules(getDomain().getId(), appInfo.getApplication().getId());
		for( int i = appInfo.getApplicationModules().size()-1; i >= 0; i-- ) {
			DomainModuleInfo info =  appInfo.getApplicationModules().get(i);
			if (! visibleModules.contains(info.getModule()) ) {
				appInfo.removeModuleInfo(info);
				LOGGER.debug( "Removed from list: {}", info );
			}
		}
		appInfo.updateApplicationModules();
		appInfo.sortApplicationModules();
	}

	public void initApplicationInfos() throws ManagerBeanException {
		this.applicationInfos = new LinkedList<DomainApplicationInfo>();
		this.aioInfo = DomainApplicationInfo.getApplicationInfos(getDomain(), AON_AIO_APPLICATION);
		this.aioInfo.setDescription(AonUtil.getMessage(BUNDLE_NAME, AON_PLATFORM));
		this.documental = this.aioInfo.getModuleInfo(Module.DOCUMENT);
		this.applicationInfos.add(this.aioInfo);
		updateModules(this.aioInfo);
	}

	public void saveApplications() throws ManagerBeanException {
		for( DomainApplicationInfo dai : this.applicationInfos ) {
			if ( dai.isChecked() ) {
				dai.register();
			} else {
				dai.unregister();
			}
		}
	}	
	
	public boolean isShowApplications() {
		return this.aioInfo.isChecked();
	}

	private void initDomainApplication() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
			Criteria criteria = new Criteria();
			Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_DOMAIN), getDomain().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_APPLICATION_ID), appId);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				this.domainApplication = (DomainApplication) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
	}	

	public void updateDomainApplication() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
			bean.update(domainApplication);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
	}

	public DomainApplication getDomainApplication() {
		return domainApplication;
	}

	public void setDomainApplication(DomainApplication domainApplication) {
		this.domainApplication = domainApplication;
	}	
	
	public boolean isOEM() {
		return OEM;
	}

	public void setOEM(boolean oEM) {
		OEM = oEM;
	}

	public void initOEM() throws ManagerBeanException {
		String value = AppParamUtil.getValue(AON_CUSTOMIZE_OEM);
		this.OEM = StringUtils.equals(value, Boolean.TRUE.toString());
	}

	public void saveOEM() throws ManagerBeanException {
		AppParamUtil.insertParameter(AON_CUSTOMIZE_OEM, String.valueOf(isOEM()) );
		if ( isOEM() ) {
			CompanyController cc = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
			Company company = cc.obtainCompany();
			if ( (company != null) && (company.getId() != null) ) {
				AppParamUtil.insertParameter(AON_CUSTOMIZE_ID, String.valueOf(company.getId()) );				
			}
		}

	}	

	public void domainNameCheck(FacesContext context, UIComponent component, Object value) throws ManagerBeanException {
		String name = (String) value;
		if (! StringUtils.equals(name, getDomain().getName()) ) {
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(getFieldName(IEntityAlias.DOMAIN_NAME), name);
			if ( getManagerBean().getCount(criteria) > 0 ) {
				String message = AonUtil.getMessage(BUNDLE_NAME, DOMAIN_NAME_DUPLICATED, name);
				throw new ValidatorException(new FacesMessage(message));
			}			
		}
	}	
	
}