package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.OptionGroup;
import com.code.aon.ui.util.AonUtil;

/**
 * @author atellitu
 *
 */
public class ApplicationOptionController {
		
	private static final String VM_PATH_DEFAULT = "com/code/aon/ui/audit/controller/";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationOptionController.class);
	
	public static final String CONTEXT_PROPERTY = "com.code.aon.ApplicationOptionController";
	
	private Map<String,ApplicationOption> optionMap;
	
	private Map<String,OptionGroup> groupMap;
	
	private List<ApplicationCategory> categories;
	
	private VelocityHelper velocityHelper;
	
	/**
	 * Instantiates a new application option controller.
	 */
	public ApplicationOptionController( ServletContext servletContext ) {
		init(servletContext);
	}
	
	public static ApplicationOptionController getInstance() {
		return (ApplicationOptionController) AonUtil.getServletContextAttribute(CONTEXT_PROPERTY); 
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

	public List<ApplicationCategory> getCategories( boolean sorted ) {
		if ( sorted ) {
			Collections.sort( this.categories );
		}
		return categories;
	}
	
	public List<ApplicationCategory> getCategories() {
		return getCategories(false);
	}

	public ApplicationCategory getCategory( String action ) {
		for( ApplicationCategory category : categories ) {
			if ( category.getAction().equals(action) ) {
				return category;
			}
		}
		return null;
	}
	
	private void init( ServletContext servletContext ) {
		this.optionMap = new HashMap<String, ApplicationOption>();
		this.groupMap = new HashMap<String, OptionGroup>();
		this.categories = new ArrayList<ApplicationCategory>();
		new MenuParser(servletContext).parse(this);
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