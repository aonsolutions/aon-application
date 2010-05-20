package com.code.aon.ui.resources.bean;

import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.resources.enumeration.ResourceLocation;
import com.code.aon.ui.resources.servlet.ResourceServlet;
import com.code.aon.ui.resources.servlet.ResourceURI;

/**
 * The Class ResourceResolver.
 */
public class ResourceResolver {

	private static final String VERSION = "version";
	
	private static final String GROUP_ID = "com.code.aon";
	
	private static final String ARTIFACT_ID = "aon.ui.resources";
	
	private static final String SNAPSHOT = "SNAPSHOT";
	
	/** The Constant POM_PROPERTIES. */
	private static final String POM_PROPERTIES = "/META-INF/maven/" + GROUP_ID + "/" + ARTIFACT_ID + "/pom.properties";
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ResourceResolver.class);
	
	/** The resource context path. */
	private String resourceContextPath;
	
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
		this.resolve = new FakeMap(ResourceLocation.EXTERNAL_CONTEXT);
		this.resolveLocal = new FakeMap(ResourceLocation.WEB_APPLICATION);
		this.resolveCommon = new FakeMap(ResourceLocation.COMMON_RESOURCES);
		this.resourceURIPreffix = ResourceServlet.DEFAULT_PATTERN;
		String version = getVersion();
		if (! StringUtils.isBlank(version) ) {
			LOGGER.info( "Resources version: " + version );
			if (! StringUtils.endsWith(version, SNAPSHOT) ) {
				this.resourceURIPreffix += "/" + version;
			}
		}
	}
	
	/**
	 * Gets the resource context path.
	 * 
	 * @return the resource context path
	 */
	public String getResourceContextPath() {
		return resourceContextPath;
	}

	/**
	 * Sets the resource context path.
	 * 
	 * @param resourceContextPath the new resource context path
	 */
	public void setResourceContextPath(String resourceContextPath) {
		this.resourceContextPath = "../" + resourceContextPath;
	}

	/**
	 * Gets the resource uri preffix.
	 * 
	 * @return the resource uri preffix
	 */
	public String getResourceURIPreffix() {
		return resourceURIPreffix;
	}

	/**
	 * Sets the resource uri preffix.
	 * 
	 * @param resourceURIPreffix the new resource uri preffix
	 */
	public void setResourceURIPreffix(String resourceURIPreffix) {
		this.resourceURIPreffix = resourceURIPreffix;
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
	
	private String getVersion() {
		String version = null;
		try {
			Properties properties = new Properties();
			InputStream in = ResourceResolver.class.getResourceAsStream(POM_PROPERTIES);
			properties.load(in);
			in.close();
			version = properties.getProperty(VERSION);
		} catch ( Throwable th ) {
			LOGGER.warn("Imposible determinar la versión");
		}
		return version;
	}
	
	/**
	 * The Class FakeMap.
	 */
	@SuppressWarnings("unchecked")
	public class FakeMap extends AbstractMap<String,String> {
		
		/** The location. */
		private ResourceLocation location;
		
		/**
		 * Instantiates a new fake map.
		 * 
		 * @param location the location
		 */
		public FakeMap(ResourceLocation location) {
			this.location = location;
		}

		@Override
		public Set entrySet() {
			return null;
		}
		
		@Override
		public String get(Object key) {
			String result = null;
			switch ( location ) {
				case WEB_APPLICATION:
					result = StringUtils.join( new Object[] {resourceURIPreffix, key} );
					break;
				case EXTERNAL_CONTEXT:
					result = StringUtils.join( new Object[] {resourceContextPath, resourceURIPreffix, key} );
					break;
				case COMMON_RESOURCES:
					result = StringUtils.join( new Object[] {resourceURIPreffix, "/", ResourceURI.COMMON_RESOURCE_PREFFIX, key} );
					break;
			}
			return StringUtils.removeStart( result, "/");
		}
		
	}

}
