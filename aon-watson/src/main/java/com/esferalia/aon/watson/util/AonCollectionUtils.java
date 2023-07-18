package com.esferalia.aon.watson.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class AonCollectionUtils {
	
	private AonCollectionUtils() {
		
	}

    /**
     * Null-safe check if the specified collection is empty.
     * <p>
     * Null returns true.
     *
     * @param coll  the collection to check, may be null
     * @return true if empty or null
     */
	public static boolean isEmpty(Collection<?> collection){
		return collection == null || collection.isEmpty();
	}
	
	public static int size(final Collection<?> coll){
		return  isEmpty(coll) ? 0 : coll.size();
	}

	/**
     * Null-safe check if the specified collection is not empty.
     * <p>
     * Null returns false.
     *
     * @param coll  the collection to check, may be null
     * @return true if non-null and non-empty
     */
    public static boolean isNotEmpty(final Collection<?> coll) {
        return !isEmpty(coll);
    }
    
	public static boolean isEmpty(Map<?,?> map){
		return map == null || map.isEmpty();
	}
	public static boolean isNotEmpty(Map<?,?> map){
		return !isEmpty(map);
	}
	

}
