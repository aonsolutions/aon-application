package com.code.aon.faces.component.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.faces.component.AttributeInfo;
import com.code.aon.faces.component.ComponentInfo;
import com.code.aon.faces.component.ComponentManager;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributes;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class AonComponentConfig implements ComponentConfig {

	private ComponentConfig config;
	
	private Tag tag;

	public AonComponentConfig( ComponentConfig config, List<AttributeInfo> extraAttributes ) {
    	this.config = config;
    	this.tag = duplicate( config.getTag(), extraAttributes );
	}
	
	public AonComponentConfig( ComponentConfig config ) {
		this( config, null );
	}
	
	private Tag duplicate( Tag tag, List<AttributeInfo> extraAttributes ) {
		TagAttributes attributes = newTagAttributes( tag, extraAttributes );
		return new Tag(tag, attributes);
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
    
    private TagAttribute newTagAttribute( TagAttribute ta, String name ) {
    	return new TagAttribute( ta.getLocation(), ta.getNamespace(), name, name, ta.getValue() );
    }

    private TagAttribute newTagAttributeValue( TagAttribute ta, String value ) {
    	return new TagAttribute( ta.getLocation(), ta.getNamespace(), ta.getLocalName(), ta.getQName(), value );
    }
    
    private void proccess( List<AttributeInfo> attributes, Tag tag, Map<String,TagAttribute> map ) {
		for( AttributeInfo ainfo : attributes ) {    	
			if ( ainfo.isIgnore() ) {
				map.remove( ainfo.getName() );
			} else {
				TagAttribute ta = map.get( ainfo.getName() );
				if ( ta != null ) {
					if ( ainfo.getAlias() != null  ) {
						map.remove( ta.getLocalName() );
						ta = newTagAttribute( ta, ainfo.getAlias() );
						map.put( ta.getLocalName(), ta );
					}
					if ( ainfo.isForce() ) {
						ta = newTagAttributeValue( ta, ainfo.getValue() );
						map.put( ta.getLocalName(), ta );
					}
				} else if ( ainfo.getValue() != null ) {
					ta = new TagAttribute( tag.getLocation(), "", ainfo.getName(), ainfo.getName(), ainfo.getValue() );
					map.put( ta.getLocalName(), ta );
				}
			}
		}
    }
    
    private TagAttributes newTagAttributes( Tag tag, List<AttributeInfo> extraAttributes ) {
    	Map<String,TagAttribute> map = new HashMap<String, TagAttribute>();
    	for( TagAttribute attribute : tag.getAttributes().getAll() ) {
    		map.put( attribute.getLocalName(), attribute );
    	}
		ComponentInfo componentInfo = ComponentManager.getInstance().getComponentInfo(tag);
		if ( componentInfo != null ) {
			proccess( componentInfo.getAttributes(), tag, map );
		}
		if ( extraAttributes != null ) {
			proccess( extraAttributes, tag, map );			
		}
		TagAttribute[] array = new TagAttribute[map.size()];
		return new TagAttributes( map.values().toArray(array) );
    }
	
}
