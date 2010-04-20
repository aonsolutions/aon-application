package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.Application;
import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.audit.OptionGroup;
import com.code.aon.ui.config.util.UserUtils;

/**
 * @author atellitu
 *
 */
public class ApplicationOptionController {
		
	private static final String VM_PATH_DEFAULT = "com/code/aon/ui/audit/controller/";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationOptionController.class);
	
	private Map<String,ApplicationOption> optionMap;
	
	private Map<String,OptionGroup> groupMap;
	
	private List<ApplicationCategory> categories;
	
	private Application application;
		
	private VelocityHelper velocityHelper;
	
	/**
	 * Instantiates a new application option controller.
	 */
	public ApplicationOptionController() {
		init();
	}
	
	public void addCategory( ApplicationCategory category ) {
		this.categories.add(category);
	}
	
	public Map<String, ApplicationOption> getOptionMap() {
		return this.optionMap;
	}

	public Map<String, OptionGroup> getGroupMap() {
		return groupMap;
	}

	public List<ApplicationCategory> getCategories() {
		return categories;
	}
	
	public List<ApplicationOption> getOptions( boolean allOptions ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		for( ApplicationCategory category : getCategories() ) {
			if ( allOptions || category.isRendered() ) {
				for( OptionGroup group : category.getGroups() ) {
					if ( allOptions || group.isRendered() ) {
						for( ApplicationOption option : group.getOptions() ) {
							if ( allOptions || option.isRendered() ) {
								list.add(option);	
							}
						}
					}
				}
			}
		}
		return list;
	}		

	public Application getApplication() {
		return application;
	}

	public Action getAction( String name ) throws ManagerBeanException {
		return AuditManager.getAction(name, application);		
	}
	
	private void init() {
		this.optionMap = new HashMap<String, ApplicationOption>();
		this.groupMap = new HashMap<String, OptionGroup>();
		this.categories = new ArrayList<ApplicationCategory>();
		AuthPrincipal principal = UserUtils.getInstance().getPrincipal();
		try {
			this.application = AuditManager.getApplication(principal);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error getting application for " + principal, e );
		}		
		new MenuParser().parse(this);
	}

	private VelocityHelper getVelocityHelper() {
		if ( this.velocityHelper == null ) {
			this.velocityHelper = new VelocityHelper();
			try {
				this.velocityHelper.init( VM_PATH_DEFAULT );
			} catch (Exception e) {
				LOGGER.error( "Velocity engine could not be initialized", e );
			}
		}
		return this.velocityHelper;
	}
	
	public String getTemplate( String template, Object ... objects  ) throws IOException {
		try {
			TemplateHelper th = getVelocityHelper().getTemplateHelper();
			for( int i = 0; i < objects.length; i++ ) {
				th.putInContext( (String) objects[i++], objects[i]);
			}
			return th.processTemplate(template);
		} catch (AonException e) {
			LOGGER.error( e.getMessage(), e);
		}		
		return null;
	}
	
}