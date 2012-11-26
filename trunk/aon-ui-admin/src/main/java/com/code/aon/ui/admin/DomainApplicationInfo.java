package com.code.aon.ui.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Application;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.controller.IAdminConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainApplicationInfo {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationInfo.class);
	
	private boolean checked;
	
	private Application application;
	
	private String description;
	
	private DomainApplication domainApplication;
	
	private Domain domain;
	
	private List<DomainModuleInfo> applicationModules;

	public DomainApplicationInfo(Domain domain, Application application) {
		this.application = application;
		this.description = application.getDescription();
		this.domain = domain;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public DomainApplication getDomainApplication() {
		return domainApplication;
	}

	public void setDomainApplication(DomainApplication domainApplication) {
		this.domainApplication = domainApplication;
	}

	public Application getApplication() {
		return application;
	}

	public Domain getDomain() {
		return domain;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<DomainModuleInfo> getApplicationModules() {
		return applicationModules;
	}

	public void setApplicationModules(List<DomainModuleInfo> applicationModules) {
		this.applicationModules = applicationModules;
	}
	
	public DomainModuleInfo getModuleInfo( Module module ) {
		for( DomainModuleInfo dmi : this.applicationModules ) {
			if ( dmi.getModule() == module ) {
				return dmi;
			}
		}
		return null;
	}

	public void removeModuleInfo( DomainModuleInfo info ) throws ManagerBeanException {
		this.applicationModules.remove(info);
		IManagerBean damBean = BeanManager.getManagerBean(DomainApplicationModule.class);
		if ( info.getApplicationModule() != null ) {
			damBean.remove(info.getApplicationModule());	
			LOGGER.info( "Removed: {}", info.getApplicationModule() );
		}			
	}
	
	public void register() throws ManagerBeanException {
		DomainApplication da = getDomainApplication();
		IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
		if ( da == null ) {
			da = new DomainApplication();
			da.setActive(true);
			da.setDomain(domain.getId());
			da.setApplication(application);
			bean.insert(da);
			setDomainApplication(da);
		} else {
			da.setActive(true);
			bean.update(da);
		}
		updateApplicationModules();
	}
	
	public void updateApplicationModules() throws ManagerBeanException {
		IManagerBean damBean = BeanManager.getManagerBean(DomainApplicationModule.class);
		for( DomainModuleInfo dmi: getApplicationModules() ) {
			DomainApplicationModule dam = dmi.getApplicationModule();
			if ( dmi.isChecked() ) {
				if ( dam == null ) {
					dam = new DomainApplicationModule();
					dam.setDomainApplication(getDomainApplication());
					dam.setModule(dmi.getModule());
					damBean.insert(dam);
					dmi.setApplicationModule(dam);
					LOGGER.info( "Added: {}", dam );
				}
			} else {
				if ( dam != null ) {
					damBean.remove(dam);
					dmi.setApplicationModule(null);
					LOGGER.info( "Removed: {}", dam );
				}
			}
		}		
	}

	public void unregister() throws ManagerBeanException {
		DomainApplication da = getDomainApplication();
		if ( da != null ) {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
			da.setActive(false);
			bean.update(da);			
		}
	}
	
	public static Application getApplication( String name ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Application.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_NAME), name);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (Application) list.get(0);
		}
		return null;
	}

	public static DomainApplication getDomainApplication( Domain domain, Application application ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_APPLICATION_ID), application.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_DOMAIN), domain.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (DomainApplication) list.get(0);
		}
		return null;
	}	

	private static DomainApplicationModule getDomainApplicationModule( Module module, DomainApplication da ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID), da.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_MODULE), module);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (DomainApplicationModule) list.get(0);
		}
		return null;		
	}

	public void sortApplicationModules() {
		final Locale locale = AonUtil.getCurrentLocale();
    	Comparator<DomainModuleInfo> comparator = new Comparator<DomainModuleInfo>() {
			@Override
			public int compare(DomainModuleInfo o1, DomainModuleInfo o2) {
				return o1.getModule().getName(locale).compareTo(o2.getModule().getName(locale));
			}	    		
		};
    	Collections.sort( getApplicationModules(), comparator );					
	}
	
	public static DomainApplicationInfo getApplicationInfos( Domain domain, String applicationName ) throws ManagerBeanException {
		Application application = getApplication(applicationName);
		DomainApplicationInfo dai = new DomainApplicationInfo(domain, application);
		DomainApplication da = getDomainApplication(domain, application);
		dai.setDomainApplication(da);
		dai.setChecked( (da != null) && da.isActive() );
		List<DomainModuleInfo> modules = new LinkedList<DomainModuleInfo>();
		if ( IAdminConstants.AON_AIO_APPLICATION.equals(applicationName) ) {
			for( Module module : Module.values() ) {
				DomainModuleInfo dmi = new DomainModuleInfo(module);
				if ( da != null ) {
					DomainApplicationModule dam = getDomainApplicationModule(module, da); 
					dmi.setApplicationModule(dam);
					dmi.setChecked( dam != null );
				}
				modules.add(dmi);
			}
		}
		dai.setApplicationModules(modules);
		return dai;
	}
 
	
}
