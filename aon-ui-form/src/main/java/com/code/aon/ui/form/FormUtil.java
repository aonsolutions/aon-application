package com.code.aon.ui.form;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.code.aon.ui.util.AonUtil;

/**
 * AonUtil includes some common methods.
 */
public class FormUtil {

	/** Obtains a suitable Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FormUtil.class);

	/**
	 * Gets the controller registered in <code>faces-bean-config.xml</code>
	 * with that name.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return the controller
	 */
	public static IController getController(String name) {
		Object o = AonUtil.getRegisteredBean(name);
		if (o instanceof IController) {
			return (IController) o;
		}
		LOGGER.error("{} is not a instance of 'com.code.aon.ui.form.IController'",o);
		return null;
	}

}
