package com.code.aon.ui.audit;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class VisibilityManager extends BasicVisibilityManager {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
	@Override
	public Set<Module> getEnabledModules( User user, boolean addExtraModules ) {
		Set<Module> enabledModules = new HashSet<Module>();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Integer domainId = ds.getDomainId();
		boolean userOfParentDomain = false;
		if ( user != null ) {
			Integer parentDomainId = AdminUtil.getParentDomain(domainId);
			userOfParentDomain = ObjectUtils.equals(user.getDomain(), parentDomainId);
			if ( userOfParentDomain ) {
				enabledModules.addAll( getEnabledModuleList(false, true) );
			}
		}
		Set<Module> domainModules = getEnabledModuleList(true, false);
		boolean addConfiguration = true;
		if ( domainModules.contains(Module.AON_ONE) ) {
			domainModules.clear();
			domainModules.add(Module.AON_ONE);
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
