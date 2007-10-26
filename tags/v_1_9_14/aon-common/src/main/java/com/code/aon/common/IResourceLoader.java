package com.code.aon.common;

import java.io.InputStream;

/**
 * Base interface that loads any Aon-Framework resource.
 * 
 * @author Consulting & Development. Aimar Tellitu - 22-sep-2005
 * @since 1.0
 *
 */

public interface IResourceLoader {

	/**
	 * Return the stream bound to the resource.
	 * 
	 * @param resource
	 * @return The stream bound to the resource.
	 */
    InputStream getResourceAsStream( String resource );

}
