package com.code.aon.ui.audit;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class DeprecatedVisibilityManager extends BasicVisibilityManager implements IVisibilityManager {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DeprecatedVisibilityManager.class);

	@Override
	public Set<Module> getEnabledModules( User user, boolean addExtraModules ) {
		Set<Module> enabledModules = new HashSet<Module>();
		try {
			boolean skipParentModules = true;
			Integer domainId = DomainManager.getCurrentDomain();
			Integer applicationId = getApplicationId();
			Integer parentDomainId = AdminUtil.getParentDomain(domainId);
			boolean consultancyParent = false;
			if ( parentDomainId != null ) {
				consultancyParent = (DomainSwitcher.getDomainType(parentDomainId) == DomainType.CONSULTANCY);
			}
			if ( user != null ) {
				if ( consultancyParent ) {
					boolean domainParentUser = ObjectUtils.equals(user.getDomain(), parentDomainId);
					if ( domainParentUser &&
						AuditManager.hasModule(parentDomainId, applicationId, Module.FISCAL) ) {
							enabledModules.add(Module.ACCOUNTING);
							enabledModules.add(Module.MANAGEMENT);
							enabledModules.add(Module.TREASURY);
					}
					skipParentModules = consultancyParent && (!domainParentUser);
				}
			}
			if ( addExtraModules ) {
				if ( (DomainSwitcher.getDomainType(domainId) == DomainType.CONSULTANCY) && 
						AuditManager.hasModule(domainId, applicationId, Module.FISCAL) ) {
					enabledModules.add(Module.ACCOUNTING);
					enabledModules.add(Module.MANAGEMENT);
					enabledModules.add(Module.TREASURY);
				}
			}
			enabledModules.addAll( getEnabledModuleList(true, !skipParentModules) );
			if ( consultancyParent && enabledModules.contains(Module.DOCUMENT_PORTAL) ) {
				enabledModules.remove(Module.DOCUMENT_PORTAL);
				enabledModules.add(Module.DOCUMENT);
			}
			enabledModules.add(Module.CONFIGURATION);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading enabled modules for " + user, e);
		}			
		return enabledModules;		
	}

	@Override
	public boolean isRenderDocumentModule() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		return (ds.getType() == DomainType.CONSULTANCY) || (! ds.isDomainManagementAvailable());
	}

	@Override
	public boolean isRenderAccountingModule() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		return ! ds.isDomainManagementAvailable();
	}

	@Override
	public boolean isRenderPayrollConfig() {
		if (! isDeniedModule(Module.CONTRATA.getName()) ) {
			return true;
		}
		return ! isDeniedModule(Module.PAYROLL.getName()) && AonUtil.getRoleManager().isPayroll();
	}
	
}
