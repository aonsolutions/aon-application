package es.code.ecm.repository.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for classes.
 */
public final class ClassUtil {

	/** ClassUtil class Logger */
	protected static final Logger LOGGER = LoggerFactory.getLogger( ClassUtil.class.getName() );

	/**
	 * Don't instantiate.
	 */
	private ClassUtil() {
		// unused
	}

	/**
	 * Load a class trying both with the standard that with the thread classloader.
	 * 
	 * @param className class name
	 * @return loaded class
	 * @throws ClassNotFoundException if the given class can't be loaded by both classloaders.
	 */
	@SuppressWarnings("unchecked")
	public static Class classForName(String className) throws ClassNotFoundException {
		Class loadedClass;
		try {
			loadedClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			try {
				loadedClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
			} catch (ClassNotFoundException e1) {
				LOGGER.error("Unable to load class \"{"+className+"}\" due to a ClassNotFoundException");
				throw e1;
			}
		}
		return loadedClass;
	}

	/**
	 * Shortcut for <code>ClassUtil.classForName(className).newInstance()</code>
	 * 
	 * @param className class name
	 * @return instance of the given class
	 * @throws InstantiationException exception thrown by newInstance()
	 * @throws IllegalAccessException exception thrown by newInstance()
	 * @throws ClassNotFoundException if the given class can't be loaded by both classloaders
	 */
	public static Object newInstance(String className) 
				throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return classForName(className).newInstance();
	}
}
