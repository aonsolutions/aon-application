package com.code.aon.ui.resources.bean;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.ui.resources.enumeration.ResourceLocation;
import com.code.aon.ui.resources.servlet.ResourceServlet;
import com.code.aon.ui.resources.servlet.ResourceURI;

/**
 * The Class ResourceResolver.
 */
public class ResourceResolver implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(ResourceResolver.class);
	
	/** The resource uri preffix. */
	private String resourceURIPreffix;
	
	/** The resolve. */
	private FakeMap resolve;
	
	/** The resolve local. */
	private FakeMap resolveLocal;
	
	/** The resolve common. */
	private FakeMap resolveCommon;
	
	/**
	 * Instantiates a new resource resolver.
	 */
	public ResourceResolver() {
		this.resolve = new FakeMap(this, ResourceLocation.EXTERNAL_CONTEXT);
		this.resolveLocal = new FakeMap(this, ResourceLocation.WEB_APPLICATION);
		this.resolveCommon = new FakeMap(this, ResourceLocation.COMMON_RESOURCES);
	}
	
	/**
	 * Gets the resource uri preffix.
	 * 
	 * @return the resource uri preffix
	 */
	private String getResourceURIPreffix() {
		if ( resourceURIPreffix == null ) {
			this.resourceURIPreffix = ResourceServlet.DEFAULT_PATTERN;
			String version = AonVersion.VERSION;
			if (! StringUtils.isBlank(version) ) {
				this.resourceURIPreffix += "/" + version;
			}			
			LOGGER.debug( "Resources URI preffix: {}", this.resourceURIPreffix );
		}
		return resourceURIPreffix;
	}

	/**
	 * Gets the resolve.
	 * 
	 * @return the resolve
	 */
	public FakeMap getResolve() {
		return resolve;
	}
	
	/**
	 * Gets the resolve local.
	 * 
	 * @return the resolve local
	 */
	public FakeMap getResolveLocal() {
		return resolveLocal;
	}

	/**
	 * Gets the resolve common.
	 * 
	 * @return the resolve common
	 */
	public FakeMap getResolveCommon() {
		return resolveCommon;
	}
	
	/**
	 * The Class FakeMap.
	 */
	public static class FakeMap extends AbstractMap<String,String> implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		/** The location. */
		private ResourceLocation location;
		
		private ResourceResolver resolver;
		
		/**
		 * Instantiates a new fake map.
		 * 
		 * @param location the location
		 */
		public FakeMap(ResourceResolver resolver, ResourceLocation location) {
			this.resolver = resolver;
			this.location = location;
		}

		@Override
		public Set<Entry<String, String>> entrySet() {
			return null;
		}

		@Override
		public String get(Object key) {
			String result = null;
			switch ( location ) {
				case WEB_APPLICATION:
					result = StringUtils.join( new Object[] {resolver.getResourceURIPreffix(), key} );
					break;
				case EXTERNAL_CONTEXT:
					result = StringUtils.join( new Object[] {resolver.getResourceURIPreffix(), key} );
					break;
				case COMMON_RESOURCES:
					result = StringUtils.join( new Object[] {resolver.getResourceURIPreffix(), "/", ResourceURI.COMMON_RESOURCE_PREFFIX, key} );
					break;
			}
			return StringUtils.removeStart( result, "/");
		}
		
	}

}
