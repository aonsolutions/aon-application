package com.code.aon.ui.admin;

import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;

public class DomainModuleInfo {

	private boolean checked;
	
	private Module module;
	
	private DomainApplicationModule applicationModule;
	
	private boolean disabled;

	public DomainModuleInfo(Module module) {
		this.module = module;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public DomainApplicationModule getApplicationModule() {
		return applicationModule;
	}

	public void setApplicationModule(DomainApplicationModule applicationModule) {
		this.applicationModule = applicationModule;
	}

	public Module getModule() {
		return module;
	}
	
}
