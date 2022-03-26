package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.code.aon.common.util.Classpath;
import com.code.aon.ui.audit.ActionSource;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.IOption;
import com.code.aon.ui.audit.OptionGroup;
import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.ConfigNavigationCase;

public class MenuParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(MenuParser.class);
	
	private static final String FACES_CONFIG_FILE = "faces-config.xml";
	
	private static final String NAVIGATON_CASE = "/faces-config/*[name()='navigation-rule']/*[name()='navigation-case']";
	
	private static final String FROM_OUTCOME = "from-outcome";
	
	private static final String TO_VIEW_ID = "to-view-id";
	
	private static final String OLD_MENU_TEMPLATE_PATH = "/facelet/old/menu.xhtml";
	
	public static final String AON_COMMAND_LINK = "aon:commandLink";
	
	public static final String AON_MENU_ITEM = "aon:menuItem";
	
	public static final String A4J_COMMAND_LINK = "a4j:commandLink";
		
	private static final String AON_OUTPUTTEXT = "aon:outputText";
	
	private static final String AON_ACTION_LISTENER = "aon:actionListener";
	
	private static final String F_SET_PROPERTY_ACTION_LISTENER = "f:setPropertyActionListener";

	private static final String AON_IF = "aon:if";
	
	private static final String UI_INCLUDE = "ui:include";
	
	private static final String UI_DECORATE = "ui:decorate";

	private static final String AON_PANEL_GRID = "aon:panelGrid";
	
	private static final String F_FACET = "f:facet";
	
	private static final String VALUE_ATTRIBUTE = "value";
	
	public static final String STYLE_ATTRIBUTE = "style";
	
	public static final String STYLE_CLASS_ATTRIBUTE = "styleClass";

	public static final String ACTION_ATTRIBUTE = "action";
		
	private static final String SRC_ATTRIBUTE = "src";
	
	private static final String TEMPLATE_ATTRIBUTE = "template";
	
	private static final String HEADER_CLASS_ATTRIBUTE = "headerClass";
	
	private static final String ACTION_LISTENER_ATTRIBUTE = "actionListener";
	
	private static final String RENDERED_ATTRIBUTE = "rendered";
	
	private static final String TEST_ATTRIBUTE = "test";
	
	public static final String ID_ATTRIBUTE = "id";
	
	private static final String METHOD_ATTRIBUTE = "method";
	
	private static final String TARGET_ATTRIBUTE = "target";
	
	private static final String CATEGORY_EXPRESSION = "#{category}";
	
	public static final String MENU_ACTION_PREFFIX = "menu_";
	
	private static final String CONFIG_MENU_PREFFIX = "config_";
	
	public static final String FISCAL_STYLE_CLASS = "aon-fiscal-link-box";
	
	private ApplicationOptionController controller;
	
	private ApplicationCategory category;
	
	private OptionGroup group;
	
	private Element lastPanelGrid;
	
	private ServletContext servletContext;
	
	public MenuParser( ServletContext servletContext ) {
		this.servletContext = servletContext;
	}
	
	public static Document getDocument( URL url ) {
        Document document = null;
		try {
		    SAXReader reader = new SAXReader();
			reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			document = reader.read(url);
		} catch (SAXException | DocumentException e) {
			LOGGER.error( "Error parsing " + url, e );
		}
		return document;
	}

	private Document getDocument( String path ) {
		return getDocument( resolveURL(path) );
	}
	
	private URL resolveURL( String resource ) {
		URL url = null;
		try {
			url = servletContext.getResource(resource);
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return url;
	}
	
	private String getPath( String action ) {
		ApplicationAssociate associate = ApplicationAssociate.getInstance(this.servletContext);
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
			if ( AON_IF.equals(parent.getQualifiedName()) ) {
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
		Document document = getDocument(OLD_MENU_TEMPLATE_PATH);
		if ( document != null ) {
			parseMenu( document );
		}		
		parseFacesConfigs(getFacesConfigURLs());
	}
	
	public void checkSerialization() {
		SerializationChecker checker = new SerializationChecker();
		checker.check(getFacesConfigURLs());			
	}
	
	@SuppressWarnings("unchecked")
	private void parseMenu( Document document ) {
		List<Element> list = document.selectNodes("//" + AON_COMMAND_LINK );
		for ( Element element : list ) {
			parseCategory(element);
        }		
		parseMenuItems(document);
	}
	
	@SuppressWarnings("unchecked")
	private void parseMenuItems( Document document ) {
		this.group = new OptionGroup(category, "#{bundle.aon_general}");
		this.group.setId("config_general");
		this.group.setStyleClass(HEADER_CLASS_ATTRIBUTE);	
		List<Element> list = document.selectNodes("//" + AON_MENU_ITEM );
		for ( Element element : list ) {
			parseMenuItem(element);
        }				
	}
	
	private void updateMenuItemXml( ApplicationOption option ) {
		String xml = StringUtils.replace(option.getXml(), AON_MENU_ITEM, AON_COMMAND_LINK);
		option.setXml(xml);
	}
	
	private void parseMenuItem( Element menuItem ) {
		String id = getId(menuItem);
		if ( StringUtils.startsWith(id, CONFIG_MENU_PREFFIX) ) {
			ApplicationOption option = getApplicationOption(menuItem);
			if ( option != null ) {
				updateMenuItemXml(option);
				addOption(option);
			}												
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
					LOGGER.error( "Duplicated id for option group {}, {}", id, panelGrid );
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
	
	private String getDescription( Element element ) {
		String description = element.attributeValue(VALUE_ATTRIBUTE);
		if (StringUtils.isBlank(description)) {
			StringBuffer sb = new StringBuffer();
			for( Object _element : element.elements() ) {
				Element child = (Element) _element;
				String value = child.attributeValue(VALUE_ATTRIBUTE);
				if (! StringUtils.isEmpty(value) ) {
					sb.append(value);
					String styleClass = child.attributeValue(STYLE_CLASS_ATTRIBUTE);
					if ( FISCAL_STYLE_CLASS.equals(styleClass) ) {
						sb.append("-");
					}
				}
			}
			description = sb.toString();	
		}
		return description;
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
			String description = getDescription(element);
			option.setDescription(description);
			if ( StringUtils.isEmpty(description) ) {
				LOGGER.error( "Null description for {}", option );
			}
			element.addAttribute(ID_ATTRIBUTE, IOption.ID_PATTERN);
			option.setXml( element.asXML() );
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
			LOGGER.error( "Duplicated action for option {}, {}", option.getAction(), option );
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
	
	private List<URL> getFacesConfigURLs() {
		List<URL> list = new LinkedList<URL>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
	        URL[] urls = Classpath.search(cl, "META-INF/", FACES_CONFIG_FILE);
	        if (! ArrayUtils.isEmpty(urls) ) {
	        	list.addAll(Arrays.asList(urls));
	        }
	        URL url = resolveURL("/WEB-INF/"+ FACES_CONFIG_FILE);
	        if ( url != null ) {
	        	list.add(url);      	
	        }
		} catch (IOException e) {
        	LOGGER.error("Error searching report config files", e);
        }		
        return list;
	}
	
	
	private Map<String,String> getViewIdMap( List<URL> list ) {
       	Map<String,String> viewIdMap = new HashMap<String, String>();
		for( URL url : list ) {
			parseFacesConfig(url, viewIdMap);
		}       	
        return viewIdMap;
	}
	
	private void parseFacesConfigs( List<URL> list ) {
       	Map<String,String> viewIdMap = getViewIdMap(list);
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
