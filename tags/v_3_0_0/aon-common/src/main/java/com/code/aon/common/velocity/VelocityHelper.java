package com.code.aon.common.velocity;

import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.log.CommonsLogLogSystem;

import com.code.aon.common.AonException;

/**
 * TODO
 * 
 * @author Consulting & Development. ecastellano - 22/11/2006
 * 
 */
public class VelocityHelper {

	private static Logger LOGGER = Logger.getLogger(VelocityHelper.class.getName());

	private VelocityEngine engine;

	private VelocityContext baseContext;

	private String[] paths;

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		this.engine = new VelocityEngine();
		Properties p = new Properties();
		p.setProperty(VelocityEngine.VM_LIBRARY, "");
		p.setProperty(VelocityEngine.COUNTER_INITIAL_VALUE, "0");
		p.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, CommonsLogLogSystem.class.getName());
		p.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM + ".commons.logging.name", VelocityHelper.class.getName());
		p.setProperty(VelocityEngine.RESOURCE_LOADER, "class");
		p.setProperty("class." + VelocityEngine.RESOURCE_LOADER + ".cache", "true");
		p.setProperty("class." + VelocityEngine.RESOURCE_LOADER + ".class", (ClasspathResourceLoader.class).getName());
		engine.init(p);
		this.baseContext = new VelocityContext();
	}

	/**
	 * TODO
	 * 
	 * @param templatePaths
	 * @throws Exception
	 */
	public void init(String templatePaths) throws Exception {
		init();
		setPaths(templatePaths);
	}

	/**
	 * TODO
	 * 
	 * @return VelocityContext
	 */
	protected VelocityContext getBaseContext() {
		return this.baseContext;
	}

	/**
	 * TODO
	 * 
	 * @param templatePaths
	 */
	public void setPaths(String templatePaths) {
		this.paths = calculatePaths(templatePaths);
	}

	/**
	 * TODO
	 * 
	 * @return VelocityEngine
	 */
	protected VelocityEngine getEngine() {
		return engine;
	}

	/**
	 * @param templatePaths
	 * @return String[]
	 */
	private String[] calculatePaths(String templatePaths) {
		String[] result = null;
		if ((templatePaths != null) && (templatePaths.length() > 0)) {
			StringTokenizer st = new StringTokenizer(templatePaths, ", ");
			result = new String[st.countTokens()];
			for (int i = 0; st.hasMoreTokens(); i++) {
				result[i] = st.nextToken();
			}
		}
		return result;
	}

	/**
	 * TODO
	 * 
	 * @param context
	 * @return TemplateHelper
	 */
	public TemplateHelper getTemplateHelper(Map context) {
		return new TemplateHelper(context, this);
	}

	/**
	 * TODO
	 * 
	 * @return TemplateHelper
	 */
	public TemplateHelper getTemplateHelper() {
		return new TemplateHelper(this);
	}

	/**
	 * TODO
	 * 
	 * @param name
	 * @return Template
	 * @throws AonException
	 */
	public Template getTemplate(String name) throws AonException {
		String templateName = name;
		if (!templateName.endsWith(".vm")) {
			templateName = templateName + ".vm";
		}
		if (this.paths != null) {
			for (String path : paths) {
				if (this.engine.templateExists(path + templateName)) {
					templateName = path + templateName;
					break;
				}
			}
		}
		try {
			return this.engine.getTemplate(templateName);
		} catch (ResourceNotFoundException e) {
			throw new AonException("Could not find template: " + name, e);
		} catch (ParseErrorException e) {
			throw new AonException("Error parsing template: " + name, e);
		} catch (Exception e) {
			throw new AonException("Error getting template: " + name, e);
		}
	}

	/**
	 * TODO
	 * 
	 * @param key
	 * @param value
	 */
	public void putInBaseContext(String key, Object value) {
		LOGGER.finest("putInContext " + key + "=" + value);
		if (value == null) {
			throw new IllegalStateException("value must not be null for " + key);
		}
		Object replaced = getBaseContext().put(key, value);
		if (replaced != null) {
			throw new IllegalStateException(replaced + " found when setting " + key);
		}
	}

	/**
	 * TODO
	 * 
	 * @param key
	 * @param expected
	 */
	public void removeFromBaseContext(String key, Object expected) {
		LOGGER.finest("removeFromContext " + key + "=" + expected);
		Object replaced = getBaseContext().remove(key);
		if (replaced == null) {
			throw new IllegalStateException(key + " did not exist in template context.");
		}
		if (replaced != expected) {
			throw new IllegalStateException("expected " + key + " to be bound to " + expected + " but was to "
					+ replaced);
		}
	}

}
