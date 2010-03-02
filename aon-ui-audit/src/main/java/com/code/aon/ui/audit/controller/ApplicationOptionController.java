package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.Application;
import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.config.util.UserUtils;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.impl.ResourceResolver;
import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.ConfigNavigationCase;

/**
 * @author atellitu
 *
 */
public class ApplicationOptionController {
	
	private static final String ID_ATTRIBUTE = "id";
	
	private static final String VALUE_ATTRIBUTE = "value";

	private static final String ACTION_ATTRIBUTE = "action";
	
	private static final String SRC_ATTRIBUTE = "src";
	
	private static final String MENU_TEMPLATE_PATH = "/facelet/homepage/menu.xhtml";
	
	public static final String UI_INCLUDE = "ui:include";
	
	private static final String VM_PATH_DEFAULT = "com/code/aon/ui/audit/controller/";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationOptionController.class);
	
	private ResourceResolver resolver;
	
	private Map<String,ApplicationOption> optionMap;
	
	private List<ApplicationOption> options;
	
	private Application application;
		
	private VelocityHelper velocityHelper;

	
	/**
	 * Instantiates a new application option controller.
	 */
	public ApplicationOptionController() {
		this.resolver = new DefaultResourceResolver();
		init();
	}

	private String getPath( String action ) {
		ApplicationAssociate associate = ApplicationAssociate.getInstance(
				FacesContext.getCurrentInstance().getExternalContext());
		if (associate != null) {
			List<ConfigNavigationCase> list = associate.getNavigationCaseListMappings().get("*");
			for( ConfigNavigationCase cnc : list ) {
				if ( StringUtils.equals(action, cnc.getFromOutcome()) ) {
					return cnc.getToViewId();
				}	
			}
		}				
		return null;
	}
	
	public Map<String, ApplicationOption> getOptionMap() {
		return this.optionMap;
	}

	public List<ApplicationOption> getOptions() {
		return this.options;
	}
	
	public Application getApplication() {
		return application;
	}

	public Action getAction( String name ) throws ManagerBeanException {
		AuditManager manager = AuditManager.getInstance();
		return manager.getAction(name, application);		
	}
	
	private Document getDocument( String path ) {
	    SAXReader reader = new SAXReader();
        Document document = null;
		try {
			URL url = resolver.resolveUrl(path);
			document = reader.read(url);
		} catch (DocumentException e) {
			LOGGER.error( "Error parsing " + path, e );
		}
		return document;
	}
	
	private void init() {
		this.options = new ArrayList<ApplicationOption>();
		this.optionMap = new HashMap<String, ApplicationOption>();
		Document document = getDocument(MENU_TEMPLATE_PATH);
		if ( document != null ) {
			parseMenu( document );
		}
		AuthPrincipal principal = UserUtils.getInstance().getPrincipal();
		try {
			this.application = AuditManager.getInstance().getApplication(principal);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error getting application for " + principal, e );
		}		
	}
	
	private String getStringValue(String expression) {
		return (String) getValue(expression, String.class);
	}	
	
	private Object getValue(String expression, Class<?> _class) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ELContext elctx = ctx.getELContext();
		ExpressionFactory ef = ctx.getApplication().getExpressionFactory();
		ValueExpression ve = ef.createValueExpression(elctx, expression, _class);
		return ve.getValue(elctx);
	}	

	@SuppressWarnings("unchecked")
	private void parseMenu( Document document ) {
		List<Element> list = document.selectNodes("//" + ApplicationOption.AON_COMMAND_LINK );
		for ( Element element : list ) {
			parseMainCommandLink(element);
        }		
	}
	
	private boolean isDuplicatedId( String id ) {
		for( ApplicationOption option : this.options ) {
			if ( StringUtils.equals(option.getId(), id) ) {
				return true;
			}
		}
		return false;
	}
	
	private ApplicationOption getApplicationOption( Element element, String category ) {
		ApplicationOption option = null;
		String action = element.attributeValue(ACTION_ATTRIBUTE);
		if (! StringUtils.isEmpty(action) ) {
			option = new ApplicationOption();
			option.setAction(action);
			String id = element.attributeValue(ID_ATTRIBUTE);
			if (! StringUtils.isEmpty(id) ) {
				option.setId(id);	
			} else {
				LOGGER.warn( "Element without id {}", element );
			}
			option.setCategory(category);
			option.setDescription( getStringValue(element.attributeValue(VALUE_ATTRIBUTE)) );
			element.addAttribute(ID_ATTRIBUTE, ApplicationOption.ID_PATTERN);
			element.addAttribute(VALUE_ATTRIBUTE, ApplicationOption.VALUE_PATTERN);
			option.setXml( element.asXML() );
			if ( StringUtils.isEmpty(option.getDescription()) ) {
				LOGGER.error( "Null description for {}", option );
			}
		} else {
			LOGGER.error( "Null action for {}", option );
		}
		return option;
	}
	
	private void addOption( ApplicationOption option ) {
		if (! this.optionMap.containsKey(option.getAction()) ) {
			String id = option.getId();
			if ( (! StringUtils.isEmpty(id)) && isDuplicatedId(id) ) {
				LOGGER.error( "Duplicated id {}", id );
			}			
			options.add(option);
			optionMap.put( option.getAction(), option );
		} else {
			LOGGER.debug( "Duplicated action for option {}", option );
		}		
	}

	@SuppressWarnings("unchecked")
	private void parseTemplate( Document document, String category ) {
		List<Element> list = document.selectNodes("//" + ApplicationOption.AON_COMMAND_LINK );
		for ( Element element : list ) {
			ApplicationOption option = getApplicationOption(element, category);
			if ( option != null ) {
				addOption(option);
			}
        }	
		List<Element> includes = document.selectNodes("//" + UI_INCLUDE );
		for ( Element include : includes ) {
			String viewId = include.attributeValue(SRC_ATTRIBUTE);
			Document template = getDocument(viewId);
			if ( template != null ) {
				parseTemplate(template, category);
			}
		}
	}
	
	private void parseMainCommandLink( Element element ) {
		String action = element.attributeValue(ACTION_ATTRIBUTE);
		if ( StringUtils.startsWith(action, "menu") ) {
			String category = getStringValue(element.attributeValue(VALUE_ATTRIBUTE));
			String viewId = getPath(action);
			Document document = getDocument(viewId);
			if ( document != null ) {
				parseTemplate(document, category);
			}
		}
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