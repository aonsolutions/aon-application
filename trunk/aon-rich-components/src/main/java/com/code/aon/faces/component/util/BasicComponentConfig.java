package com.code.aon.faces.component.util;

import java.util.List;

import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributes;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class BasicComponentConfig implements ComponentConfig {

	private ComponentConfig config;
	
	private String componentType;
	
	private String rendererType;
	
	private FaceletHandler nextHandler;
	
	private Tag tag;

	public BasicComponentConfig( ComponentConfig config, List<TagAttribute> attributes ) {
    	this.config = config;
    	TagAttribute[] array = new TagAttribute[attributes.size()];
    	TagAttributes tagAttributes = new TagAttributes( attributes.toArray(array) );
    	this.tag = new Tag(config.getTag(), tagAttributes);
	}
	
    public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

    public String getComponentType() {
    	if ( componentType != null ) {
    		return this.componentType;
    	}
        return config.getComponentType();
    }
    
	public void setRendererType(String rendererType) {
		this.rendererType = rendererType;
	}

	public String getRendererType() {
    	if ( rendererType != null ) {
    		return this.rendererType;
    	}
        return config.getRendererType();
    }

    public void setNextHandler(FaceletHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	public FaceletHandler getNextHandler() {
		if ( nextHandler != null ) {
			return this.nextHandler;
		}
        return config.getNextHandler();
    }

    public Tag getTag() {
        return this.tag;
    }

    public String getTagId() {
        return this.config.getTagId();
    }
    
    public static TagAttribute newAttribute( Tag tag, String name, String value ) {
    	return new TagAttribute( tag.getLocation(), "", name, name, value );
    }
	
}
