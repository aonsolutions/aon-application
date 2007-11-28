	package com.code.aon.faces.component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;

import org.apache.commons.digester.Digester;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.Tag;

public class ComponentManager {
	
	private static final Logger LOGGER = Logger.getLogger(ComponentManager.class.getName());	

	private Map<String,ComponentInfo> components;

	public ComponentManager( String resource ) {
		this.components = new HashMap<String, ComponentInfo>();
		init(resource);
	}
	
	private String getFullName( Tag tag ) {
		return tag.getNamespace() + "/" + tag.getLocalName();
	}
	
	public ComponentInfo getComponent( Tag tag ) {
		return this.components.get( getFullName(tag) );
	}
	
	public void addComponent( ComponentInfo componentInfo ) {
		this.components.put( componentInfo.getFullName(), componentInfo );
	}

	private void init( String resource ) {
		try {
			InputStream in = ComponentManager.class.getResourceAsStream(resource);
			getDigester().parse( in );
			in.close();
		} catch ( Throwable th ) {
			LOGGER.severe( th.getMessage() );
		}
	}
	
	private Digester getDigester() {
		Digester digester = new Digester();
		digester.setValidating(false);

		digester.push( this );
		
		digester.addObjectCreate( "*/component", ComponentInfo.class );
		digester.addSetProperties( "*/component" );
		digester.addSetNext("*/component", "addComponent");

		digester.addObjectCreate( "*/attribute", AttributeInfo.class );
		digester.addSetProperties( "*/attribute" );
		digester.addSetNext("*/attribute", "addAttribute");
		
		return digester;
	}	
	
	public ComponentInfo getComponentInfo( AonComponentHandler aonComponent ) {
		Tag tag = aonComponent.getConfig().getTag();
		ComponentInfo componentInfo = getComponent( tag );
		return componentInfo;
	}

	public void updateMetaRuleset( AonComponentHandler aonComponent, MetaRuleset set ) {
		ComponentInfo componentInfo = getComponentInfo( aonComponent );
		if ( componentInfo != null ) {
			for( AttributeInfo attribute : componentInfo.getAttributes() ) {
				if ( attribute.isIgnore() ) {
					set.ignore( attribute.getName() );
				} else if ( attribute.getAlias() != null ) {
					set.alias( attribute.getName(), attribute.getAlias() );
				}
			}
		}
	}
	
	public void setAttributes( AonComponentHandler aonComponent, FaceletContext ctx, UIComponent component ) {
		ComponentInfo componentInfo = getComponentInfo( aonComponent );
		if ( componentInfo != null ) {
			for( AttributeInfo attribute : componentInfo.getAttributes() ) {
				attribute.update(aonComponent, ctx, component);
			}
		}
	}

}
