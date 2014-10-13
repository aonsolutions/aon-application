package com.code.aon.faces.component;

import java.net.URL;

import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;
import org.richfaces.component.html.HtmlCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.faces.component.richfaces.componentGroup.ComponentGroup;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;

public class ComponentManager {

	private static final String AON_LIBRARY = "META-INF/aon-rich-components.aonlib.xml";

	private static final String DISABLED_STYLE_CLASS = "disabledStyleClass";		
	
	private static final String SELECT_INPUT_DATE_STYLE_CLASS = "inputClass";
	
    private final static Logger LOGGER = LoggerFactory.getLogger(ComponentManager.class);
	
	private static final ComponentManager SINGLETON = new ComponentManager();
	
	private ComponentLibrary library;
	
	private ComponentManager() {
		loadImplicit();
	}
	
    public void loadImplicit() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(AON_LIBRARY);
        if ( url != null ) {
            try {
            	this.library = new ComponentLibrary(url);
                LOGGER.debug("Added Library from: " + url);
            } catch (Exception e) {
            	LOGGER.error("Error Loading Library: " + url, e);
            }        	
        } else {
        	LOGGER.error("Error Library not found: {}", AON_LIBRARY);
        }
    }
	
	public static ComponentManager getInstance() {
		return SINGLETON;
	}
	
	public ComponentInfo getComponentInfo( AonComponentHandler aonComponent ) {
		Tag tag = aonComponent.getConfig().getTag();
		return getComponentInfo( tag );
	}

	public ComponentInfo getComponentInfo( Tag tag ) {
		return library.getComponent( tag.getLocalName() );			
	}
	
	public void updateMetaRuleset( Tag tag, MetaRuleset set ) {
		ComponentInfo componentInfo = getComponentInfo( tag );
		if ( componentInfo != null ) {
			for( AttributeInfo attribute : componentInfo.getAttributes() ) {
				if ( attribute.isIgnore() ) {
					set.ignore( attribute.getName() );
				} else if ( attribute.getAlias() != null ) {
					set.alias( attribute.getName(), attribute.getAlias() );
				}
			}			
		}
		set.ignore(DISABLED_STYLE_CLASS);
	}
	
	public static String getInputStyleClass( UIComponent c ) {
		if ( HtmlCalendar.COMPONENT_FAMILY.equals(c.getFamily()) ) {
			return SELECT_INPUT_DATE_STYLE_CLASS;
		}
		return HTML.STYLE_CLASS_ATTR;
	}
	
	private String getDisabledStyleClass( Tag tag, FaceletContext ctx, UIComponent c ) {
		String disabledClass = null;
		TagAttribute disabledClassTag = FaceletUtil.getAttribute(tag, DISABLED_STYLE_CLASS);
		if ( disabledClassTag != null ) {
			disabledClass = disabledClassTag.getValue();
		} else {
			disabledClass = (String) FaceletUtil.getProperty(ctx.getFacesContext(), c, DISABLED_STYLE_CLASS);
		}
		return disabledClass;
	}
	
	private void updateDisabledStyleClass(Tag tag, FaceletContext ctx, UIComponent c) {
		String disabledClass = getDisabledStyleClass(tag, ctx, c);
		TagAttribute disabled = FaceletUtil.getAttribute(tag, HTML.DISABLED_ATTR);
		if ( disabled != null && ! StringUtils.isBlank(disabledClass) ) {
			if ( FaceletUtil.getBoolean(ctx,disabled) ) {
				FaceletUtil.addStyleClass(ctx.getFacesContext(), c, getInputStyleClass(c), disabledClass);
			} else {
				String styleClass = StringUtils.substringBeforeLast(disabledClass, "-disabled");
				FaceletUtil.replaceStyleClass(ctx.getFacesContext(), c, getInputStyleClass(c), disabledClass, styleClass);
			}
		}
	}		
	
	private void updateComponent(FaceletContext ctx, UIComponent c, UIComponent parent) {
		ComponentGroup componentGroup = ComponentGroup.getComponentGroup(ctx, c);
		if ( componentGroup != null ) {
			componentGroup.apply(ctx, c, parent);
		}		
	}
	
	public void setAttributes( Tag tag, FaceletContext ctx, UIComponent component ) {
		ComponentInfo componentInfo = getComponentInfo( tag );
		if ( componentInfo != null ) {
			for( AttributeInfo attribute : componentInfo.getAttributes() ) {
				attribute.update( tag, ctx, component );
			}
		}
	}
	
	public void onComponentCreated(FaceletContext ctx, UIComponent component, UIComponent parent) {
		updateComponent(ctx, component, parent);
	}

	public void onComponentPopulated(Tag tag, FaceletContext ctx, UIComponent component, UIComponent parent) {
		updateDisabledStyleClass(tag, ctx, component);
	}
}
