package com.code.aon.ui.form;

import java.util.logging.Logger;

import com.code.aon.ui.util.AonUtil;

/**
 * AonUtil includes some common methods.
 */
public class FormUtil {

	/** Obtains a suitable Logger. */
	private static final Logger LOGGER = Logger.getLogger(FormUtil.class.getName());

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
		LOGGER.severe(o + " is not a instance of 'com.code.aon.ui.form.IController'");
		return null;
	}

}
