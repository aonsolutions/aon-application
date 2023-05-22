package net.aonsolutions.watson.client.util;

import java.util.Collection;
import java.util.Map;

public class AonCollectionUtils {
	
	private AonCollectionUtils() {
	}

	public static boolean isEmpty(Collection<?> collection){
		return collection == null || collection.isEmpty();
	}
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
