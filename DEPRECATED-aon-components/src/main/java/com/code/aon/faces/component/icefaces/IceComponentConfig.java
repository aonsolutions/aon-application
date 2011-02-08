package com.code.aon.faces.component.icefaces;

import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class IceComponentConfig implements ComponentConfig {

	private static final String ICEFACES_NAMESPACE = "http://www.icesoft.com/icefaces/component";
	
	private ComponentConfig config;
	
	private Tag tag;

	public IceComponentConfig( ComponentConfig config ) {
    	this.config = config;
    	this.tag = modify( config.getTag() );
	}
	
	private Tag modify( Tag tag ) {
		Tag newTag = new Tag( tag.getLocation(), ICEFACES_NAMESPACE, tag.getLocalName(), tag.getQName(), tag.getAttributes() );
		return newTag;
	}

    public String getComponentType() {
        return config.getComponentType();
    }

    public String getRendererType() {
        return config.getRendererType();
    }

    public FaceletHandler getNextHandler() {
        return config.getNextHandler();
    }

    public Tag getTag() {
        return this.tag;
    }

    public String getTagId() {
        return this.config.getTagId();
    }
	
    
    
}
