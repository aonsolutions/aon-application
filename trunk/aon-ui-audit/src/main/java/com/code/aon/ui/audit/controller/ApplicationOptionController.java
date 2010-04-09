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
import com.code.aon.ui.audit.ApplicationCategory;
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
	
	private static final String CATEGORY_EXPRESSION = "#{category}";

	private static final String MENU_ACTION_PREFFIX = "menu_";

	private static final String ID_ATTRIBUTE = "id";
	
	private static final String VALUE_ATTRIBUTE = "value";

	private static final String ACTION_ATTRIBUTE = "action";
	
	private static final String RENDERED_ATTRIBUTE = "rendered";
	
	private static final String TEST_ATTRIBUTE = "test";
	
	private static final String STYLE_CLASS_ATTRIBUTE = "styleClass";
	
	private static final String SRC_ATTRIBUTE = "src";
	
	private static final String TEMPLATE_ATTRIBUTE = "template";
	
	private static final String MENU_TEMPLATE_PATH = "/facelet/homepage/menu.xhtml";
	
	public static final String AON_COMMAND_LINK = "aon:commandLink";
	
	public static final String UI_INCLUDE = "ui:include";
	
	public static final String UI_DECORATE = "ui:decorate";
	
	public static final String C_IF = "c:if";
	
	private static final String VM_PATH_DEFAULT = "com/code/aon/ui/audit/controller/";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationOptionController.class);
	
	private ResourceResolver resolver;
	
	private Map<String,ApplicationOption> optionMap;
	
	private List<ApplicationCategory> categories;
	
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

	public List<ApplicationCategory> getCategories() {
		return categories;
	}
	
	public List<ApplicationOption> getOptions( boolean allOptions ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		for( ApplicationCategory category : getCategories() ) {
			if ( allOptions || category.isRendered() ) {
				for( ApplicationOption option : category.getOptions() ) {
					if ( allOptions || option.isRendered() ) {
						list.add(option);	
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
		this.optionMap = new HashMap<String, ApplicationOption>();
		this.categories = new ArrayList<ApplicationCategory>();
		Document document = getDocument(MENU_TEMPLATE_PATH);
		if ( document != null ) {
			parseMenu( document );
		}
		AuthPrincipal principal = UserUtils.getInstance().getPrincipal();
		try {
			this.application = AuditManager.getApplication(principal);
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
		List<Element> list = document.selectNodes("//" + AON_COMMAND_LINK );
		for ( Element element : list ) {
			parseMainCommandLink(element);
        }		
	}
	
	private boolean isDuplicatedId( String id ) {
		for( ApplicationCategory category : this.categories ) {
			for( ApplicationOption option : category.getOptions() ) {
				if ( StringUtils.equals(option.getId(), id) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String getRendered( Element element ) {
		Element parent = element.getParent();
		while ( parent != null ) {
			if ( C_IF.equals(parent.getQualifiedName()) ) {
				return parent.attributeValue(TEST_ATTRIBUTE);
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	private ApplicationOption getApplicationOption( Element element, ApplicationCategory category ) {
		ApplicationOption option = null;
		String action = element.attributeValue(ACTION_ATTRIBUTE);
		if (! StringUtils.isEmpty(action) ) {
			option = new ApplicationOption();
			option.setAction(action);
			String id = element.attributeValue(ID_ATTRIBUTE);
			if (! StringUtils.isEmpty(id) ) {
				if ( StringUtils.contains(id, CATEGORY_EXPRESSION) ) {
					id = StringUtils.replace(id, CATEGORY_EXPRESSION, category.getAlias());
				}
				option.setId(id);	
			} else {
				LOGGER.warn( "Element without id {}", element );
			}
			String rendered = getRendered(element);
			if (! StringUtils.isEmpty(rendered) ) {
				option.setRendered(rendered);
				element.addAttribute(RENDERED_ATTRIBUTE, rendered);
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
			LOGGER.error( "Null action in {} for {} ", category, element );
		}
		return option;
	}
	
	private void addOption( ApplicationOption option ) {
		if (! this.optionMap.containsKey(option.getAction()) ) {
			optionMap.put( option.getAction(), option );
		} else {
			LOGGER.debug( "Duplicated action for option {}", option );
		}		
		String id = option.getId();
		if ( (! StringUtils.isEmpty(id)) && isDuplicatedId(id) ) {
			LOGGER.error( "Duplicated id {}", id );
		}				
		option.getCategory().addOption(option);
	}

	@SuppressWarnings("unchecked")
	private void parseTemplate( Document document, ApplicationCategory category ) {
		String search = "//" + AON_COMMAND_LINK + " | //" + UI_INCLUDE + " | //" + UI_DECORATE;
		List<Element> list = document.selectNodes( search );
		for ( Element element : list ) {
			if ( AON_COMMAND_LINK.equals(element.getQualifiedName()) ) {
				ApplicationOption option = getApplicationOption(element, category);
				if ( option != null ) {
					addOption(option);
				}				
			} else {
				String viewId = element.attributeValue(SRC_ATTRIBUTE);
				if ( StringUtils.isEmpty(viewId) ) {
					viewId = element.attributeValue(TEMPLATE_ATTRIBUTE);
				}
				Document template = getDocument(viewId);
				if ( template != null ) {
					parseTemplate(template, category);
				}
			}
        }	
	}
	
	private void parseMainCommandLink( Element element ) {
		String action = element.attributeValue(ACTION_ATTRIBUTE);
		if ( StringUtils.startsWith(action, MENU_ACTION_PREFFIX) ) {
			String categoryName = getStringValue(element.attributeValue(VALUE_ATTRIBUTE));
			String alias = StringUtils.substringAfter(action, MENU_ACTION_PREFFIX);
			ApplicationCategory category = new ApplicationCategory(categoryName, alias);
			String styleClass = element.attributeValue(STYLE_CLASS_ATTRIBUTE);
			if (! StringUtils.isEmpty(styleClass) ) {
				category.setStyleClass(styleClass);	
			}
			String rendered = getRendered(element);
			if (! StringUtils.isEmpty(rendered) ) {
				category.setRendered(rendered);
			}
			this.categories.add(category);
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