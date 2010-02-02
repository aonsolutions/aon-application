package com.code.aon.common;

import java.util.Map;

/**
 * Interface that provides extra information about a Transfer Object.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 29-jun-2005
 * @since 1.0
 *
 */

@Deprecated
public interface ILookupObject {

	/**
	 * Return a <code>Map</code> of extra information about the Transfer Object.
	 * 
	 * @return the Map.
	 */
    Map<String,Object> getLookups();
}
