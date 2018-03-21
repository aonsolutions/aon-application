package com.code.aon.ui.audit;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
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
	
	private final static Module[] AON_ONE_MODULES =
		{Module.AON_ONE, Module.DOCUMENT, Module.ACCOUNTING, Module.CALL_CENTER};
		
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
		DomainType parentDomainType = (parentDomainId != null) ? DomainType.values()[AdminUtil.getDomainType(parentDomainId)] : null;
		Set<Module> parentDomainModules = Collections.emptySet();
		if ( parentDomainId != null ) {
			parentDomainModules = getEnabledModuleList(parentDomainId);
		}
		boolean userOfParentDomain = false;
		if ( user != null ) {
			userOfParentDomain = parentDomainId == null || ObjectUtils.equals(user.getDomain(), parentDomainId);
			if ( userOfParentDomain ) {
				enabledModules.addAll( parentDomainModules );
			}
		}
		Set<Module> domainModules = (parentDomainId == null || parentDomainType == DomainType.CONSULTANCY) ? getEnabledModuleList(domainId) : new HashSet<Module>();
		boolean addConfiguration = true;
		if ( domainModules.contains(Module.AON_ONE) ) {
			for (Iterator<Module> iterator = domainModules.iterator(); iterator.hasNext();) {
			    if (! ArrayUtils.contains(AON_ONE_MODULES, iterator.next()) ) {
			        iterator.remove();
			    }
			}						
			addConfiguration = userOfParentDomain;
		}
		enabledModules.addAll( domainModules );
		enabledModules.remove(Module.PAYROLL_PORTAL);
		enabledModules.remove(Module.CONTRATA);
		enabledModules.remove(Module.DOCUMENT_PORTAL);		
		if ( ds.getType() == DomainType.ACADEMY ) {
			enabledModules.add(Module.ACADEMY);
		}
		if ( ds.getType() == DomainType.GARAGE ) {
			enabledModules.add(Module.GARAGE);
		}
		if ( ds.getType() == DomainType.HOTEL ) {
			enabledModules.add(Module.HOTEL);
		}
		if ( addExtraModules) {
			enabledModules.add(Module.CRM);
			enabledModules.add(Module.MANAGEMENT);
			enabledModules.add(Module.WAREHOUSE);
			enabledModules.add(Module.GROUPWARE);
			enabledModules.add(Module.POS);
			if ( parentDomainModules.contains(Module.PAYROLL_PORTAL) ) {
				enabledModules.add(Module.PAYROLL_PORTAL);
				enabledModules.add(Module.DOCUMENT);
			}
		}
		if ( addConfiguration ) {
			enabledModules.add(Module.CONFIGURATION);
		}
		if ( (user != null) && (DomainType.ENTERPRISE == ds.getType()) && (parentDomainId != null) && (ObjectUtils.equals(user.getDomain(), domainId)) ) {
			if (! hasModule(parentDomainId, Module.FISCAL) ) {
				enabledModules.remove(Module.FISCAL);
			}
			if (! hasModule(parentDomainId, Module.PAYROLL) ) {
				enabledModules.remove(Module.PAYROLL);
			}
		}
		if (parentDomainId == null || enabledModules.contains(Module.MANAGEMENT) || enabledModules.contains(Module.AON_ONE) || enabledModules.contains(Module.AON_FINANCE)) {
			enabledModules.remove(Module.FINANCE_PORTAL);
		}
		if (ds.isEnabledGoToParent() && ds.isConsultancyDomain() && DomainType.OFFICE != ds.getType()) {
			enabledModules.remove(Module.CRM);
			enabledModules.remove(Module.MANAGEMENT);
			enabledModules.remove(Module.WAREHOUSE);
			enabledModules.remove(Module.GROUPWARE);
			enabledModules.remove(Module.POS);
			enabledModules.remove(Module.AON_ONE);
			enabledModules.remove(Module.CALL_CENTER);
			enabledModules.remove(Module.ACADEMY);
			enabledModules.remove(Module.GARAGE);
			enabledModules.remove(Module.HOTEL);
			enabledModules.remove(Module.PAYROLL_PORTAL);
			if (!enabledModules.contains(Module.AON_FINANCE)) {
				enabledModules.add(Module.AON_FINANCE);
			}
		}
		return enabledModules;
	}

	@Override
	public boolean isRenderPayrollConfig() {
		return ! isDeniedModule(Module.PAYROLL.getName()) && AonUtil.getRoleManager().isPayroll();
	}
	
}
