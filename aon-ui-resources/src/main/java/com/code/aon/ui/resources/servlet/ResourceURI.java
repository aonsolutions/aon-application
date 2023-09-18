package com.code.aon.ui.resources.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ResourceURI.
 */
public class ResourceURI {

	/**
	 * Common Resoureces preffix
	 */
	public static final String COMMON_RESOURCE_PREFFIX = "cr:";
	
	private static final String COMMON_RESOURCES_PATH = "/home/COMMON-RESOURCES";
	
	private static final String COMMON_RESOURCES_PATH_2 = "/usr/share/java";
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ResourceURI.class);
	
	private String path;
	
	private boolean cacheable;
	
	private boolean commonResource;
	
	/**
	 * Instantiates a new resource uri.
	 * 
	 * @param uri the uri
	 * @param context the context
	 * @param pattern the pattern
	 */
	public ResourceURI( String uri, String context, String pattern ) {
		String _path = uri;
		if (_path.startsWith(context)) {
			_path = _path.substring(context.length());
		}
		if (_path.startsWith(pattern)) {
			_path = _path.substring(pattern.length());
		}
		String[] parts = StringUtils.split( _path, '/');
		if (! ArrayUtils.isEmpty(parts)  ) {
			int index = 0;
			String version = parts[index];
			if ( isVersionString(version) ) {
				this.cacheable = true;
				index++;
			}
			if ( StringUtils.equals(parts[index], COMMON_RESOURCE_PREFFIX) ) {
				this.commonResource = true;
				index++;
			}
			if ( index != 0 ) {
				_path = "/" + StringUtils.join(parts, '/', index, parts.length);	
			}
		}
		this.path = _path;
	}
	
	private boolean isVersionString( String value ) {
		String _value = StringUtils.chomp(value, "-SNAPSHOT");
		String[] numbers = StringUtils.split( _value, '.');
		if (! ArrayUtils.isEmpty(numbers) ) {
			for( String number : numbers ) {
				if (! NumberUtils.isNumber(number) ) {
					return false;
				}
			}
		}
		return true;
	}	
	
	/**
	 * Gets the input stream.
	 * 
	 * @param basePath the base path
	 * @return the input stream
	 * @throws IOException 
	 */
	public InputStream getInputStream( ServletContext ctx, String basePath ) throws IOException {
		InputStream in = null;
		if ( this.commonResource ) {
			File file = new File( COMMON_RESOURCES_PATH, this.path );
			if (! file.exists() ) {
				file = new File( COMMON_RESOURCES_PATH_2, this.path );
			}
			LOGGER.debug("Request for resource: {}", file);
			in = new BufferedInputStream( new FileInputStream(file) );
		} else {
			String path = this.path;
			if (! StringUtils.isEmpty(basePath) ) {
				path = basePath + path;
			}
			LOGGER.debug("Request for resource (in jar): {}", path);
			in = getResourceAsStream(ctx, path);
			if (in == null) {
				LOGGER.debug("Request for resource (in war): {}", this.path);
				in = getResourceAsStream(ctx, this.path);
			}			
		}
		return in;
	}

	private InputStream getResourceAsStream( ServletContext ctx, String path ) {
		InputStream in = getClass().getResourceAsStream(path);
		if ( in == null ) {
			in = ctx.getResourceAsStream(path);
		}
		return in;
	}
	
	/**
	 * Checks if is cacheable.
	 * 
	 * @return true, if is cacheable
	 */
	public boolean isCacheable() {
		return cacheable;
	}

	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
}
