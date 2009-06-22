package com.code.aon.faces.component;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;

import org.richfaces.component.html.HtmlCalendar;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.util.Classpath;

public class ComponentManager {

	private static final String SUFFIX = ".aonlib.xml";

	private static final String DISABLED_STYLE_CLASS = "disabledStyleClass";		
	
	private static final String SELECT_INPUT_DATE_STYLE_CLASS = "inputClass";
	
    private final static Logger log = Logger.getLogger(ComponentManager.class.getName());	
	
	private static final ComponentManager SINGLETON = new ComponentManager();
	
	private Map<String,ComponentLibrary> libraries;
	
	private ComponentManager() {
		this.libraries = new HashMap<String, ComponentLibrary>();
		loadImplicit();
	}
	
    public void loadImplicit() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
	        URL[] urls = Classpath.search(cl, "META-INF/", SUFFIX);
	        for (int i = 0; i < urls.length; i++) {
	            try {
	            	addComponentLibrary( urls[i] );
	                log.info("Added Library from: " + urls[i]);
	            } catch (Exception e) {
	                log.log(Level.SEVERE, "Error Loading Library: " + urls[i], e);
	            }
	        }
        } catch ( IOException ioe ) {
        	log.log(Level.SEVERE, "Error searching files with suffix: " + SUFFIX, ioe);
        }
    }
	
	public static ComponentManager getInstance() {
		return SINGLETON;
	}
	
	private void addComponentLibrary( URL resource ) {
		ComponentLibrary library = new ComponentLibrary(resource);
		this.libraries.put( library.getNamespace(), library );
	}
	
	public ComponentInfo getComponentInfo( AonComponentHandler aonComponent ) {
		Tag tag = aonComponent.getConfig().getTag();
		return getComponentInfo( tag );
	}

	public ComponentInfo getComponentInfo( Tag tag ) {
		ComponentLibrary library = this.libraries.get(tag.getNamespace());
		if ( library != null ) {
			return library.getComponent( tag.getLocalName() );			
		}
		return null;
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
	
	private String getInputStyleClass( UIComponent c ) {
		if ( HtmlCalendar.COMPONENT_FAMILY.equals(c.getFamily()) ) {
			return SELECT_INPUT_DATE_STYLE_CLASS;
		}
		return HTML.STYLE_CLASS_ATTR;
	}
	
	public void updateDisabledStyleClass(Tag tag, FaceletContext ctx, UIComponent c) {
		TagAttribute disabled = FaceletUtil.getAttribute(tag, HTML.DISABLED_ATTR);
		if ( (disabled != null) && disabled.getBoolean(ctx) ) {
			String disabledClass = null;
			TagAttribute disabledClassTag = FaceletUtil.getAttribute(tag, DISABLED_STYLE_CLASS);
			if ( disabledClassTag != null ) {
				disabledClass = disabledClassTag.getValue();
			} else {
				disabledClass = (String) FaceletUtil.getProperty(ctx.getFacesContext(), c, DISABLED_STYLE_CLASS);
			}
			if ( disabledClass != null ) {
				UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), c, getInputStyleClass(c), disabledClass);
			}
		}
	}		
	
	public void setAttributes( Tag tag, FaceletContext ctx, UIComponent component ) {
		ComponentInfo componentInfo = getComponentInfo( tag );
		if ( componentInfo != null ) {
			for( AttributeInfo attribute : componentInfo.getAttributes() ) {
				attribute.update( tag, ctx, component );
			}
		}
		updateDisabledStyleClass(tag, ctx, component);
	}
	
}
