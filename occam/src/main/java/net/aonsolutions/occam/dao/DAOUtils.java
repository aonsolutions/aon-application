package net.aonsolutions.occam.dao;

public class DAOUtils {
	
	public static final String NULL_FILTER_MSG = "Filter can not be null.";
	public static final String NULL_FACTORY_MSG = "Factory can not be null.";
	
	private DAOUtils() {
	}
	
	static void checkNullFilter( Object obj) {
		checkNull(obj, NULL_FILTER_MSG);
	}
	static void checkNullFactory( Object obj) {
		checkNull(obj, NULL_FACTORY_MSG);
	}
	
	private static void checkNull( Object obj, String msg) {
		if (obj == null) {
			throw new IllegalArgumentException(msg);
		}
	}
	
}
