package com.code.aon.ui.admin;

import com.code.aon.audit.enumeration.Module;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.DomainApplication;


public class DomainModuleInfoManagement extends DomainModuleInfo {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private DomainModuleInfo management;
	
	private DomainModuleInfo treasury;
	
	public DomainModuleInfoManagement(DomainModuleInfo management, DomainModuleInfo treasury) {
		this.management = management;
		this.treasury = treasury;
		this.treasury.setChecked(management.isChecked());
		this.treasury.setDisabled(management.isDisabled());
	}
	
	@Override
	public Module getModule() {
		return this.management.getModule();
	}	
	
	@Override
	public boolean isChecked() {
		return this.management.isChecked();
	}	

	@Override
	public void setChecked(boolean checked) {
		this.management.setChecked(checked);
		this.treasury.setChecked(checked);
	}

	@Override
	public boolean isDisabled() {
		return this.management.isDisabled();
	}

	@Override
	public void setDisabled(boolean disabled) {
		this.management.setDisabled(disabled);
		this.treasury.setDisabled(disabled);
	}

	@Override
	public void remove() throws ManagerBeanException {
		this.management.remove();
		this.treasury.remove();
	}
	
	@Override
	public boolean update( DomainApplication domainApplication ) throws ManagerBeanException {
		boolean updated = this.management.update(domainApplication);
		updated |= this.treasury.update(domainApplication);		
		return updated;
	}
	
}
