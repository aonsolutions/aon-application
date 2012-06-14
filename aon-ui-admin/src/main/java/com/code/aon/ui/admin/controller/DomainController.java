package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.AON_AIO_APPLICATION;
import static com.code.aon.ui.admin.controller.IAdminConstants.AON_EMPLOYEE_APPLICATION;
import static com.code.aon.ui.admin.controller.IAdminConstants.AON_PLATFORM;
import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.EMPLOYEE_PORTAL;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.ui.admin.DomainApplicationInfo;
import com.code.aon.ui.admin.DomainModuleInfo;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DomainController extends BasicController {
	
	private final static Module[] FISCAL_MODULES = new Module[] {
		Module.ACCOUNTING, Module.FISCAL, Module.TREASURY, Module.MANAGEMENT
	};
	
	public final static int DEFAULT_MAX_TOTAL_DOCUMENT_SIZE = 100;	
	
	private DomainApplicationInfo aioInfo;
	
	private DomainModuleInfo documental;
	
	private DomainApplicationInfo employeeInfo;

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

	public void initApplicationInfos() throws ManagerBeanException {
		this.applicationInfos = new LinkedList<DomainApplicationInfo>();
		this.aioInfo = DomainApplicationInfo.getApplicationInfos(getDomain(), AON_AIO_APPLICATION);
		this.aioInfo.setDescription(AonUtil.getMessage(BUNDLE_NAME, AON_PLATFORM));
		this.documental = this.aioInfo.getModuleInfo(Module.DOCUMENT);
		this.employeeInfo = DomainApplicationInfo.getApplicationInfos(getDomain(), AON_EMPLOYEE_APPLICATION);
		this.employeeInfo.setDescription(AonUtil.getMessage(BUNDLE_NAME, EMPLOYEE_PORTAL));
		this.applicationInfos.add(this.aioInfo);
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

	public void onFiscalPortal( ActionEvent event ) {
		this.employeeInfo.setChecked(false);
		this.aioInfo.setChecked(true);
		for( DomainModuleInfo dmi: this.aioInfo.getApplicationModules() ) {
			dmi.setChecked( ArrayUtils.contains(FISCAL_MODULES, dmi.getModule()) );
		}
	}
	
	public boolean isShowApplications() {
		return this.aioInfo.isChecked();
	}
	
}