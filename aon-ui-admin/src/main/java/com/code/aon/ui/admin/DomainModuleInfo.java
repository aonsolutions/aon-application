package com.code.aon.ui.admin;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.DomainApplication;
import com.code.aon.ui.util.AonUtil;

public class DomainModuleInfo implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainModuleInfo.class);
	
	private boolean checked;
	
	private Module module;
	
	private DomainApplicationModule applicationModule;
	
	private boolean disabled;
	
	private boolean rendered;
	
	private String description;

	public DomainModuleInfo() {
		this.rendered = true;
	}

	public DomainModuleInfo(Module module) {
		this();
		this.module = module;
		Locale locale = AonUtil.getCurrentLocale();
		this.description = module.getName(locale);
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

	public void setApplicationModule(DomainApplicationModule applicationModule) {
		this.applicationModule = applicationModule;
	}

	public Module getModule() {
		return module;
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void remove() throws ManagerBeanException {
		if ( this.applicationModule != null ) {
			IManagerBean damBean = BeanManager.getManagerBean(DomainApplicationModule.class);			
			damBean.remove(this.applicationModule);	
			LOGGER.info( "Removed: {}", this.applicationModule );
			setApplicationModule(null);
		}			
	}
	
	private void insert( DomainApplication domainApplication ) throws ManagerBeanException {
		if ( this.applicationModule == null && domainApplication != null ) {
			DomainApplicationModule dam = new DomainApplicationModule();
			dam.setDomainApplication( domainApplication );
			dam.setModule(getModule());
			IManagerBean damBean = BeanManager.getManagerBean(DomainApplicationModule.class);
			damBean.insert(dam);
			setApplicationModule(dam);
			LOGGER.info( "Added: {}", dam );
		}
	}	
	
	public void update( DomainApplication domainApplication ) throws ManagerBeanException {
		if ( isChecked() ) {
			insert(domainApplication);
		} else {
			remove();
		}
		
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("module", module)
			.append("checked", checked)
			.append("description", description)
			.append("disabled", disabled)
			.append("rendered", rendered)
			.append("applicationModule", applicationModule != null ? applicationModule.getId() : null)
			.toString();
	}
	
}
