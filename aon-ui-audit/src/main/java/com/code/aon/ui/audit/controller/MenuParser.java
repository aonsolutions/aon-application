package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.Classpath;
import com.code.aon.ui.audit.ActionSource;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.IOption;
import com.code.aon.ui.audit.OptionGroup;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.impl.ResourceResolver;
import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.ConfigNavigationCase;

public class MenuParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(MenuParser.class);
	
	private static final String FACES_CONFIG_FILE = "faces-config.xml";
	
	private static final String NAVIGATON_CASE = "/faces-config/*[name()='navigation-rule']/*[name()='navigation-case']";
	
	private static final String FROM_OUTCOME = "from-outcome";
	
	private static final String TO_VIEW_ID = "to-view-id";
	
	private static final String MENU_TEMPLATE_PATH = "/facelet/homepage/menu.xhtml";
	
	public static final String AON_COMMAND_LINK = "aon:commandLink";
		
	private static final String AON_OUTPUTTEXT = "aon:outputText";
	
	private static final String AON_ACTION_LISTENER = "aon:actionListener";
	
	private static final String F_SET_PROPERTY_ACTION_LISTENER = "f:setPropertyActionListener";

	private static final String C_IF = "c:if";
	
	private static final String UI_INCLUDE = "ui:include";
	
	private static final String UI_DECORATE = "ui:decorate";

	private static final String AON_PANEL_GRID = "aon:panelGrid";
	
	private static final String F_FACET = "f:facet";
	
	private static final String VALUE_ATTRIBUTE = "value";

	private static final String ACTION_ATTRIBUTE = "action";
		
	private static final String SRC_ATTRIBUTE = "src";
	
	private static final String TEMPLATE_ATTRIBUTE = "template";
	
	private static final String HEADER_CLASS_ATTRIBUTE = "headerClass";
	
	private static final String ACTION_LISTENER_ATTRIBUTE = "actionListener";
	
	private static final String RENDERED_ATTRIBUTE = "rendered";
	
	private static final String TEST_ATTRIBUTE = "test";
	
	private static final String ID_ATTRIBUTE = "id";
	
	private static final String METHOD_ATTRIBUTE = "method";
	
	private static final String TARGET_ATTRIBUTE = "target";
	
	private static final String CATEGORY_EXPRESSION = "#{category}";
	
	public static final String MENU_ACTION_PREFFIX = "menu_";
	
	private ApplicationOptionController controller;
	
	private ApplicationCategory category;
	
	private OptionGroup group;
	
	private Element lastPanelGrid;
	
	private ResourceResolver resolver;
	
	public MenuParser() {
		this.resolver = new DefaultResourceResolver();
	}
	
	private Document getDocument( URL url ) {
	    SAXReader reader = new SAXReader();
        Document document = null;
		try {
			document = reader.read(url);
		} catch (DocumentException e) {
			LOGGER.error( "Error parsing " + url, e );
		}
		return document;
	}

	private Document getDocument( String path ) {
		return getDocument( resolver.resolveUrl(path) );
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
	
	private String stripExpression( String value ) {
		return StringUtils.removeEnd(StringUtils.removeStart(value, "#{"), "}");
	}
	
	private String getAndExpression( String value, String newValue ) {
		if ( StringUtils.isEmpty(value) ) {
			return newValue;
		}
		String e1 = stripExpression(value);
		String e2 = stripExpression(newValue);
		return "#{(" + e1 + ") and (" + e2 + ")}";
	}
	
	private String getRendered( Element element ) {
		String value = null;
		Element parent = element.getParent();
		while ( parent != null ) {
			if ( C_IF.equals(parent.getQualifiedName()) ) {
				value = getAndExpression(value, parent.attributeValue(TEST_ATTRIBUTE));
			}
			parent = parent.getParent();
		}
		return value;
	}

	private Element getPanelGrid( Element element ) {
		Element parent = element.getParent();
		while ( parent != null ) {
			if ( AON_PANEL_GRID.equals(parent.getQualifiedName()) ) {
				String path = parent.getUniquePath() + "/" + F_FACET + "[@name='header']";
				if ( parent.selectSingleNode(path) != null ) {
					return parent;	
				}
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	public void parse( ApplicationOptionController controller ) {
		this.controller = controller;
		Document document = getDocument(MENU_TEMPLATE_PATH);
		if ( document != null ) {
			parseMenu( document );
		}		
		parseFacesConfigs();
	}
	
	@SuppressWarnings("unchecked")
	private void parseMenu( Document document ) {
		List<Element> list = document.selectNodes("//" + AON_COMMAND_LINK );
		for ( Element element : list ) {
			parseCategory(element);
        }		
	}

	private void parseCategory( Element element ) {
		String id = getId(element);
		if ( StringUtils.startsWith(id, MENU_ACTION_PREFFIX) ) {
			String categoryName = element.attributeValue(VALUE_ATTRIBUTE);
			String alias = StringUtils.substringAfter(id, MENU_ACTION_PREFFIX);
			category = new ApplicationCategory(categoryName, alias);
			String rendered = getRendered(element);
			if (! StringUtils.isEmpty(rendered) ) {
				category.setRendered(rendered);
			}
			controller.addCategory(category);
			initOption(category, element);
			String action = getAction(element);
			category.setAction(action);
			if ( StringUtils.startsWith(action, MENU_ACTION_PREFFIX) ) {
				String viewId = getPath(action);
				Document document = getDocument(viewId);
				if ( document != null ) {
					parseTemplate(document);
				}
			}
			category.setId(id);
			element.addAttribute(ID_ATTRIBUTE, IOption.ID_PATTERN);
			element.addAttribute(VALUE_ATTRIBUTE, IOption.VALUE_PATTERN);
			category.setXml( element.asXML() );			
		}
	}

	@SuppressWarnings("unchecked")
	private void parseTemplate( Document document ) {
		String search = "//" + UI_DECORATE + " | //" + UI_INCLUDE + " | //" + AON_COMMAND_LINK;
		List<Element> list = document.selectNodes( search );
		for ( Element element : list ) {
			if ( AON_COMMAND_LINK.equals(element.getQualifiedName()) ) {
				parseCommandLink( element );
			} else {
				parseInclude( element );
			}
        }	
	}

	private void parseInclude( Element element ) {
		String viewId = element.attributeValue(SRC_ATTRIBUTE);
		if ( StringUtils.isEmpty(viewId) ) {
			viewId = element.attributeValue(TEMPLATE_ATTRIBUTE);
		}
		Document template = getDocument(viewId);
		if ( template != null ) {
			parseTemplate( template );
		}
	}		

	private void parseCommandLink( Element commandLink ) {
		ApplicationOption option = getApplicationOption(commandLink);
		if ( option != null ) {
			addOption(option);
		}									
	}
	
	private String getOptionGroupDescription( Element panelGrid, String id ) {
		String path = panelGrid.getUniquePath() + "/" + F_FACET + "[@name='header']//" + AON_OUTPUTTEXT;
		Element outputText = (Element) panelGrid.selectSingleNode(path );
		if ( outputText != null ) {
			return outputText.attributeValue(VALUE_ATTRIBUTE);	
		} else {
			LOGGER.warn( "Description not found for group {}", id );
			return id;
		}
	}
	
	public static boolean isReference( String value ) {
		return StringUtils.startsWith(value, "#{") && StringUtils.endsWith(value, "}");
	}
	
	private String getValue( String name, Element element ) {
		String value = StringUtils.trimToNull(element.attributeValue(name));
		if ( value != null ) {
			if ( isReference(value) && (!name.equals(ID_ATTRIBUTE)) ) {
				LOGGER.debug( "{} is reference for option {}", name, element );
				value = getValue(ID_ATTRIBUTE, element); 
			} else if ( StringUtils.contains(value, CATEGORY_EXPRESSION) ) {
				value = StringUtils.replace(value, CATEGORY_EXPRESSION, group.getCategory().getAlias());
			}
		}
		return value;
	}

	private String getId( Element element ) {
		return getValue(ID_ATTRIBUTE, element);
	}
	
	private String getAction( Element element ) {
		return getValue(ACTION_ATTRIBUTE, element);
	}
	
	private OptionGroup getOptionGroup( Element commandLink ) {
		Element panelGrid = getPanelGrid(commandLink);
		if ( (panelGrid != null) && (panelGrid != lastPanelGrid) ) {
			String id = getId(panelGrid);
			String description = getOptionGroupDescription(panelGrid, id);
			this.group = new OptionGroup(category, description);
			if ( id != null ) {
				if (! controller.getGroupMap().containsKey(id) ) {
					this.group.setId(id);
					controller.getGroupMap().put( id, this.group );
				} else {
					LOGGER.error( "Duplicated id for option group {}", panelGrid );
				}		
			} else {
				LOGGER.error( "Option group without id {}", panelGrid );
			}			
			String rendered = getRendered(panelGrid);
			if (! StringUtils.isEmpty(rendered) ) {
				group.setRendered(rendered);
			}		
			String styleClass = panelGrid.attributeValue(HEADER_CLASS_ATTRIBUTE);
			if (! StringUtils.isEmpty(styleClass) ) {
				group.setStyleClass(styleClass);	
			}		
			this.lastPanelGrid = panelGrid;
		}
		return group;
	}
	
	private boolean isTemplateCategoryOption( Element element ) {
		return StringUtils.contains(element.attributeValue(ID_ATTRIBUTE), CATEGORY_EXPRESSION);
	}
	
	@SuppressWarnings("unchecked")
	private void parseActionSources( IOption option, Element element ) {
		List<Element> list = element.elements();
		for ( Element child : list ) {
			String name = child.getQualifiedName();
			if ( AON_ACTION_LISTENER.equals(name) ) {
				ActionSource as = new ActionSource(child.attributeValue(METHOD_ATTRIBUTE));
				option.getActionSources().add(as);
			} else if ( F_SET_PROPERTY_ACTION_LISTENER.equals(name) ) {
				String target = child.attributeValue(TARGET_ATTRIBUTE);
				String value = child.attributeValue(VALUE_ATTRIBUTE);
				ActionSource as = new ActionSource(target, value);
				option.getActionSources().add(as);
			}
        }	
	}	
	
	private void initOption( IOption option, Element element ) {
		String actionListener = element.attributeValue(ACTION_LISTENER_ATTRIBUTE);
		if (! StringUtils.isEmpty(actionListener) ) {
			ActionSource as = new ActionSource(actionListener);
			option.getActionSources().add(as);
		}		
		parseActionSources( option, element );		
	}
	
	private ApplicationOption getApplicationOption( Element element ) {
		ApplicationOption option = null;
		String action = getAction(element);
		if ( action != null ) {
			option = new ApplicationOption();
			option.setAction(action);
			if ( isTemplateCategoryOption(element) ) {
				element.addAttribute(ACTION_ATTRIBUTE, action);	
			}
			String id = getId(element);
			if ( id != null ) {
				option.setId(id);	
			} else {
				LOGGER.warn( "Element without id {}", element );
			}
			String rendered = getRendered(element);
			if (! StringUtils.isEmpty(rendered) ) {
				option.setRendered(rendered);
				element.addAttribute(RENDERED_ATTRIBUTE, rendered);
			}
			initOption(option, element);
			option.setGroup( getOptionGroup(element) );
			option.setDescription( element.attributeValue(VALUE_ATTRIBUTE) );
			element.addAttribute(ID_ATTRIBUTE, IOption.ID_PATTERN);
			element.addAttribute(VALUE_ATTRIBUTE, IOption.VALUE_PATTERN);
			option.setXml( element.asXML() );
			if ( StringUtils.isEmpty(option.getDescription()) ) {
				LOGGER.error( "Null description for {}", option );
			}
		} else {
			LOGGER.error( "Null action in {} for {} ", category, element );
		}
		return option;
	}	

	private boolean isDuplicatedId( String id ) {
		for( ApplicationCategory category : controller.getCategories() ) {
			for( OptionGroup group : category.getGroups() ) {
				for( ApplicationOption option : group.getOptions() ) {
					if ( StringUtils.equals(option.getId(), id) ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void addOption( ApplicationOption option ) {
		if (! controller.getOptionMap().containsKey(option.getAction()) ) {
			controller.getOptionMap().put( option.getAction(), option );
		} else {
			LOGGER.error( "Duplicated action for option {}", option );
		}		
		String id = option.getId();
		if ( (! StringUtils.isEmpty(id)) && isDuplicatedId(id) ) {
			LOGGER.error( "Duplicated id {}", id );
		}				
		option.getGroup().addOption(option);
	}

	@SuppressWarnings("unchecked")
	private void parseFacesConfig( URL url, Map<String,String> viewIdMap ) {
        try {
        	LOGGER.debug("Faces config URL: {}", url);
    		Document document = getDocument(url);
    		if ( document != null ) {
    			List<Element> list = document.selectNodes(NAVIGATON_CASE );
    			for ( Element element : list ) {
    				Element action = element.element(FROM_OUTCOME);
    				if ( action != null ) {
    					String actionName = action.getText();
    					if (! StringUtils.isEmpty(actionName) ) {
    						Element viewId = element.element(TO_VIEW_ID);
    						if ( viewId != null ) {
    							String viewIdValue = viewId.getText();
    							if (! StringUtils.isEmpty(viewIdValue) ) {
    								viewIdMap.put(actionName, viewIdValue);
    							}
    						}	
    					}	
    				}
    	        }		
    		}		
        } catch (Exception e) {
            LOGGER.error("Error Loading Faces Config: {} {}",url, e.getMessage());
        }		
	}
	
	private Map<String,String> getViewIdMap() {
       	Map<String,String> viewIdMap = new HashMap<String, String>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
	        URL[] urls = Classpath.search(cl, "META-INF/", FACES_CONFIG_FILE);
	        if (! ArrayUtils.isEmpty(urls) ) {
		        for (URL url : urls) {
		        	parseFacesConfig(url, viewIdMap);
		        }	        	
	        }
	    	FacesContext ctx = FacesContext.getCurrentInstance();
	        URL url = ctx.getExternalContext().getResource("/WEB-INF/"+ FACES_CONFIG_FILE);
	        if ( url != null ) {
	        	parseFacesConfig(url, viewIdMap);        	
	        }
		} catch (IOException e) {
        	LOGGER.error("Error searching report config files", e);
        }		
        return viewIdMap;
	}
	
	private void parseFacesConfigs() {
       	Map<String,String> viewIdMap = getViewIdMap();
        for( ApplicationOption option : controller.getOptionMap().values() ) {
			String name = StringUtils.substringBefore(option.getAction(), "-");
			String viewId = viewIdMap.get(name);
			option.setViewId(viewId);
        }
        for( ApplicationCategory category : controller.getCategories() ) {
			String viewId = viewIdMap.get(category.getAction());
			category.setViewId(viewId);		        	
        }
	}
	
}
