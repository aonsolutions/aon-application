	package com.code.aon.faces.component;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentLibrary {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ComponentLibrary.class);

	private String namespace;
	
	private Map<String,ComponentInfo> components;

	public ComponentLibrary( URL resource ) {
		this.components = new HashMap<String, ComponentInfo>();
		init(resource);
	}
	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public ComponentInfo getComponent( String localName ) {
		return this.components.get( localName );
	}
	
	public void addComponent( ComponentInfo componentInfo ) {
		this.components.put( componentInfo.getLocalName(), componentInfo );
	}

	private void init( URL resource ) {
		try {
			InputStream in = new BufferedInputStream(resource.openStream());
			getDigester().parse( in );
			in.close();
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		}
	}
	
	private Digester getDigester() {
		Digester digester = new Digester();
		digester.setValidating(false);

		digester.push(this);
		
		digester.addSetProperties( "components" );
		
		digester.addObjectCreate( "*/component", ComponentInfo.class );
		digester.addSetProperties( "*/component" );
		digester.addSetNext("*/component", "addComponent");

		digester.addObjectCreate( "*/attribute", AttributeInfo.class );
		digester.addSetProperties( "*/attribute" );
		digester.addSetNext("*/attribute", "addAttribute");
		
		return digester;
	}	
	
	@Override
	public String toString() {
	     return new ToStringBuilder(this).append("namespace", namespace).toString();
	}
	
}
