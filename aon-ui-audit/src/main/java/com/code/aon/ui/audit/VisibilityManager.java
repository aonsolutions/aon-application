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
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class VisibilityManager extends BasicVisibilityManager {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(VisibilityManager.class);
		
	private boolean hasModule( Integer domainId, Module module ) {
		boolean defined = false;
		try {
			defined = AuditManager.hasModule(domainId, getApplicationId(), module);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return defined;
	}
	
	@Override
	public Set<Module> getEnabledModules( User user, boolean addExtraModules ) {
		Set<Module> enabledModules = new HashSet<Module>();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Integer domainId = ds.getDomainId();
		Integer parentDomainId = AdminUtil.getParentDomain(domainId);		
		boolean userOfParentDomain = false;
		if ( user != null ) {
			userOfParentDomain = ObjectUtils.equals(user.getDomain(), parentDomainId);
			if ( userOfParentDomain ) {
				enabledModules.addAll( getEnabledModuleList(false, true) );
			}
		}
		Set<Module> domainModules = getEnabledModuleList(true, false);
		boolean addConfiguration = true;
		if ( domainModules.contains(Module.AON_ONE) ) {
			boolean addDocumental = domainModules.contains(Module.DOCUMENT);
			domainModules.clear();
			domainModules.add(Module.AON_ONE);
			if ( addDocumental ) {
				domainModules.add(Module.DOCUMENT);
			}
			addConfiguration = userOfParentDomain;
		}
		enabledModules.addAll( domainModules );
		if ( ds.getType() == DomainType.ACADEMY ) {
			enabledModules.add(Module.ACADEMY);
		}
		if ( ds.getType() == DomainType.GARAGE ) {
			enabledModules.add(Module.GARAGE);
		}
		if ( ds.getType() == DomainType.HOTEL ) {
			enabledModules.add(Module.HOTEL);
		}
		if ( addExtraModules ) {
			enabledModules.add(Module.MARKETING);
			enabledModules.add(Module.COMMERCIAL);
			enabledModules.add(Module.MANAGEMENT);
			enabledModules.add(Module.TREASURY);
			enabledModules.add(Module.WAREHOUSE);
			enabledModules.add(Module.GROUPWARE);
			enabledModules.add(Module.POS);
		}
		if ( addConfiguration ) {
			enabledModules.add(Module.CONFIGURATION);	
		}
		if ( (user != null) && (ObjectUtils.equals(user.getDomain(), domainId)) && (DomainType.ENTERPRISE == ds.getType()) ) {
			if (! hasModule(parentDomainId, Module.FISCAL) ) {
				enabledModules.remove(Module.FISCAL);
			}
			if (! hasModule(parentDomainId, Module.PAYROLL) ) {
				enabledModules.remove(Module.PAYROLL);
			}
		}
		if ( enabledModules.contains(Module.PAYROLL) ) {
			enabledModules.remove(Module.PAYROLL_PORTAL);
		}
		enabledModules.remove(Module.CONTRATA);
		enabledModules.remove(Module.DOCUMENT_PORTAL);
		return enabledModules;		
	}

	@Override
	public boolean isRenderDocumentModule() {
		return true;
	}

	@Override
	public boolean isRenderAccountingModule() {
		return true;
	}

	@Override
	public boolean isRenderPayrollConfig() {
		return ! isDeniedModule(Module.PAYROLL.getName()) && AonUtil.getRoleManager().isPayroll();
	}
	
}
