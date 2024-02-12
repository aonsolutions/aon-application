package com.esferalia.aon.watson.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
	
	public static <T> Stream<T> stream( Collection<T> list) {
		if (list == null) return Stream.empty();
		return list.stream();
	}

	public static <T> Stream<T> stream( Set<T> list) {
		if (list == null) return Stream.empty();
		return list.stream();
	}

	public static <T> Stream<T> stream( T[] array) {
		if (array == null) return Stream.empty();
		return Arrays.stream(array);
	}
	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0; 
	}
	public static <T> boolean isNotEmpty(T[] array) {
		return !isEmpty(array);
	}
	
	public static IntStream range( int to) {
		if ( to <= 0) return IntStream.empty();
		return IntStream.range(0, to); 
	}
	
	
}
