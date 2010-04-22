package com.code.aon.ui.audit.controller;

import java.net.URL;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.OptionGroup;
import com.code.aon.ui.util.AonUtil;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.impl.ResourceResolver;
import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.ConfigNavigationCase;

public class MenuParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(MenuParser.class);
	
	private static final String MENU_TEMPLATE_PATH = "/facelet/homepage/menu.xhtml";
	
	public static final String AON_COMMAND_LINK = "aon:commandLink";
		
	private static final String AON_OUTPUTTEXT = "aon:outputText";

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
	
	private static final String RENDERED_ATTRIBUTE = "rendered";
	
	private static final String TEST_ATTRIBUTE = "test";
	
	private static final String STYLE_CLASS_ATTRIBUTE = "styleClass";
	
	private static final String ID_ATTRIBUTE = "id";
	
	private static final String CATEGORY_EXPRESSION = "#{category}";
	
	private static final String MENU_ACTION_PREFFIX = "menu_";
	
	private ApplicationOptionController controller;
	
	private ApplicationCategory category;
	
	private OptionGroup group;
	
	private Element lastPanelGrid;
	
	private ResourceResolver resolver;
	
	public MenuParser() {
		this.resolver = new DefaultResourceResolver();
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
	
	private String getStringValue(String expression) {
		return (String) AonUtil.getValue(expression);
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
	}
	
	@SuppressWarnings("unchecked")
	private void parseMenu( Document document ) {
		List<Element> list = document.selectNodes("//" + AON_COMMAND_LINK );
		for ( Element element : list ) {
			parseCategory(element);
        }		
	}

	private void parseCategory( Element element ) {
		String action = element.attributeValue(ACTION_ATTRIBUTE);
		if ( StringUtils.startsWith(action, MENU_ACTION_PREFFIX) ) {
			String categoryName = getStringValue(element.attributeValue(VALUE_ATTRIBUTE));
			String alias = StringUtils.substringAfter(action, MENU_ACTION_PREFFIX);
			category = new ApplicationCategory(categoryName, alias);
			String styleClass = element.attributeValue(STYLE_CLASS_ATTRIBUTE);
			if (! StringUtils.isEmpty(styleClass) ) {
				category.setStyleClass(styleClass);	
			}
			String rendered = getRendered(element);
			if (! StringUtils.isEmpty(rendered) ) {
				category.setRendered(rendered);
			}
			controller.addCategory(category);
			String viewId = getPath(action);
			Document document = getDocument(viewId);
			if ( document != null ) {
				parseTemplate(document);
			}
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
	
	private String getOptionGroupDescription( Element panelGrid ) {
		String path = panelGrid.getUniquePath() + "/" + F_FACET + "/" + AON_OUTPUTTEXT;
		Element outputText = (Element) panelGrid.selectSingleNode(path );
		return getStringValue(outputText.attributeValue(VALUE_ATTRIBUTE));
	}
	
	private OptionGroup getOptionGroup( Element commandLink ) {
		Element panelGrid = getPanelGrid(commandLink);
		if ( (panelGrid != null) && (panelGrid != lastPanelGrid) ) {
			String description = getOptionGroupDescription(panelGrid);
			this.group = new OptionGroup(category, description);
			String id = panelGrid.attributeValue(ID_ATTRIBUTE);
			if (! StringUtils.isEmpty(id) ) {
				if ( StringUtils.contains(id, CATEGORY_EXPRESSION) ) {
					id = StringUtils.replace(id, CATEGORY_EXPRESSION, group.getCategory().getAlias());
				}
				if (! controller.getGroupMap().containsKey(id) ) {
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
	
	private ApplicationOption getApplicationOption( Element element ) {
		ApplicationOption option = null;
		String action = element.attributeValue(ACTION_ATTRIBUTE);
		if (! StringUtils.isEmpty(action) ) {
			option = new ApplicationOption();
			option.setAction(action);
			String id = element.attributeValue(ID_ATTRIBUTE);
			if (! StringUtils.isEmpty(id) ) {
				if ( StringUtils.contains(id, CATEGORY_EXPRESSION) ) {
					id = StringUtils.replace(id, CATEGORY_EXPRESSION, group.getCategory().getAlias());
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
			option.setGroup( getOptionGroup(element) );
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
			LOGGER.debug( "Duplicated action for option {}", option );
		}		
		String id = option.getId();
		if ( (! StringUtils.isEmpty(id)) && isDuplicatedId(id) ) {
			LOGGER.error( "Duplicated id {}", id );
		}				
		option.getGroup().addOption(option);
	}
	
}
