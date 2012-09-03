package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.AON_AIO_APPLICATION;
import static com.code.aon.ui.admin.controller.IAdminConstants.AON_PLATFORM;
import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Application;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.DomainApplicationInfo;
import com.code.aon.ui.admin.DomainModuleInfo;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainController extends BasicController {
	
	public final static int DEFAULT_MAX_TOTAL_DOCUMENT_SIZE = 100;	
	
	private DomainApplicationInfo aioInfo;
	
	private DomainModuleInfo documental;

	private List<DomainApplicationInfo> applicationInfos;
	
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
	
	private void updateModules( DomainApplicationInfo appInfo ) throws ManagerBeanException {
		if (! DomainManager.isParentDomain() ) {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			Integer parentDomainId = AdminUtil.getParentDomain(DomainManager.getCurrentDomain());
			Application application = appInfo.getDomainApplication().getApplication();
			Integer da = AdminUtil.getDomainApplication(parentDomainId, application.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN), parentDomainId);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID), da);
			for( ITransferObject to : bean.getList(criteria) ) {
				DomainApplicationModule dam = (DomainApplicationModule) to;
				DomainModuleInfo info = appInfo.getModuleInfo(dam.getModule());
				if ( info != null ) {
					info.setApplicationModule(dam);
					info.setChecked(true);
					info.setDisabled(true);
				}
			}			
		}
		if ( getDomain().getType() != DomainType.GARAGE ) {
			DomainModuleInfo garage = this.aioInfo.getModuleInfo(Module.GARAGE);
			garage.setChecked(false);
			appInfo.register();
			appInfo.getApplicationModules().remove(garage);
		}
		if ( getDomain().getType() != DomainType.ACADEMY ) {
			DomainModuleInfo academy = this.aioInfo.getModuleInfo(Module.ACADEMY);
			academy.setChecked(false);
			appInfo.register();
			appInfo.getApplicationModules().remove(academy);
		}
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
	
}