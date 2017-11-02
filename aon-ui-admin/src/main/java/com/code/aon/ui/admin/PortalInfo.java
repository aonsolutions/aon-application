package com.code.aon.ui.admin;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.admin.controller.IAdminConstants;
import com.code.aon.ui.admin.controller.PortalAccessController;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class PortalInfo implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PortalAccessController.class);

	private int value;
	
	private boolean showAccountingInfo;
	private boolean showFiscalInfo;
	private boolean showPayrollInfo;
	private boolean showDocumentalInfo;
	private boolean showFinanceManagement;
	
	public PortalInfo() {
		this.value = AppParamUtil.getValueAsInt(AppParam.AON_PORTAL);
	}

	public static boolean isPortalActive( int value ) {
		return (value != 0) && ((value & IAdminConstants.INACTIVE_PORTAL) == 0);
	}
	
	public void reset() {
		this.value = 0;
	}
	
	public void init() {
		setActive(isPortalActive(value));
		calculateAvalilableOptions();
	}
	
	private void calculateAvalilableOptions() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Integer parentDomainId = ds.getParentDomainId();
		Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
		try {
			this.showAccountingInfo = AuditManager.hasModule(parentDomainId, appId, Module.ACCOUNTING);
			if (! this.showAccountingInfo ) {
				setAccountingInfo(false);
			}
			this.showFiscalInfo = AuditManager.hasModule(parentDomainId, appId, Module.FISCAL);
			if (! this.showFiscalInfo ) {
				setFiscalInfo(false);
			}
			this.showPayrollInfo = AuditManager.hasModule(parentDomainId, appId, Module.PAYROLL);
			if (! this.showPayrollInfo ) {
				setPayrollInfo(false);
			}
			this.showDocumentalInfo = AuditManager.hasModule(parentDomainId, appId, Module.DOCUMENT);
			if (! this.showDocumentalInfo ) {
				setDocumentalInfo(false);
			}
			this.showFinanceManagement = AuditManager.hasModule(parentDomainId, appId, Module.FINANCE_PORTAL);
			if (! this.showFinanceManagement ) {
				setFinanceManagement(false);
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}										
	}	
	
	public void update() {
		setPortalValue(!isActive(), IAdminConstants.INACTIVE_PORTAL);
		AppParamUtil.insertParameter(AppParam.AON_PORTAL, value);
	}
	
	public boolean isShowAccountingInfo() {
		return showAccountingInfo;
	}
	public boolean isShowFiscalInfo() {
		return showFiscalInfo;
	}
	public boolean isShowPayrollInfo() {
		return showPayrollInfo;
	}
	public boolean isShowDocumentalInfo() {
		return showDocumentalInfo;
	}
	public boolean isShowFinanceManagement() {
		return showFinanceManagement;
	}

	private boolean getPortalValue(int bitwise) {
		return (this.value & bitwise) != 0;
	}

	private void setPortalValue(boolean value, int bitwise) {
		if ( value ) {
			this.value |= bitwise;	
		} else {
			this.value &= (~bitwise);
		}
	}
	
	public boolean isActive() {
		return getPortalValue(IAdminConstants.ACTIVE_PORTAL);
	}

	public void setActive(boolean active) {
		setPortalValue(active, IAdminConstants.ACTIVE_PORTAL);
	}

	public boolean isAccountingInfo() {
		return getPortalValue(IAdminConstants.ACCOUNTING_PORTAL);
	}
		
	public void setAccountingInfo(boolean accountingInfo) {
		setPortalValue(accountingInfo, IAdminConstants.ACCOUNTING_PORTAL);
	}	
	
	public boolean isFiscalInfo() {
		return getPortalValue(IAdminConstants.FISCAL_INFO_PORTAL);
	}
		
	public void setFiscalInfo(boolean fiscalInfo) {
		setPortalValue(fiscalInfo, IAdminConstants.FISCAL_INFO_PORTAL);
	}

	public boolean isPayrollInfo() {
		return getPortalValue(IAdminConstants.PAYROLL_INFO_PORTAL);
	}

	public void setPayrollInfo(boolean payrollInfo) {
		setPortalValue(payrollInfo, IAdminConstants.PAYROLL_INFO_PORTAL);
	}

	public boolean isPayrollPortal() {
		return getPortalValue(IAdminConstants.PAYROLL_PORTAL);
	}

	public void setPayrollPortal(boolean payrollPortal) {
		setPortalValue(payrollPortal, IAdminConstants.PAYROLL_PORTAL);
	}
	
	public boolean isDocumentalInfo() {
		return getPortalValue(IAdminConstants.DOCUMENTAL_INFO_PORTAL);
	}

	public void setDocumentalInfo(boolean documentalInfo) {
		setPortalValue(documentalInfo, IAdminConstants.DOCUMENTAL_INFO_PORTAL);
	}
	
	public boolean isDocumentalManagement() {
		return getPortalValue(IAdminConstants.DOCUMENTAL_MANAGEMENT_PORTAL);
	}

	public void setDocumentalManagement(boolean documentalManagement) {
		setPortalValue(documentalManagement, IAdminConstants.DOCUMENTAL_MANAGEMENT_PORTAL);
	}
	
	public boolean isFinanceManagement() {
		return getPortalValue(IAdminConstants.FINANCE_MANAGEMENT_PORTAL);
	}

	public void setFinanceManagement(boolean financeManagement) {
		setPortalValue(financeManagement, IAdminConstants.FINANCE_MANAGEMENT_PORTAL);
	}
	
	public boolean isShowManagement() {
		return isShowFinanceManagement() || isShowDocumentalInfo();
	}
	
	public boolean isShowInfo() {
		return isShowDocumentalInfo() || isShowFiscalInfo() || isShowPayrollInfo() || isShowAccountingInfo();
	}
	
	public boolean isInfoEnabled() {
		return isDocumentalInfo() || isFiscalInfo() || isPayrollInfo() || isAccountingInfo();
	}	
	
}
