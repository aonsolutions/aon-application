package com.code.aon.common.velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.code.aon.common.AonException;

/**
 * TODO
 * 
 * @author Consulting & Development. ecastellano - 22/11/2006
 * 
 */
public class TemplateHelper {

	private static Logger LOGGER = Logger.getLogger(TemplateHelper.class.getName());

	private VelocityHelper velocityHelper;

	private VelocityContext context;

	/**
	 * TODO
	 * 
	 * @param context
	 * @param velocityHelper
	 */
	protected TemplateHelper(Map context, VelocityHelper velocityHelper) {
		this.velocityHelper = velocityHelper;
		this.context = new VelocityContext(context, velocityHelper.getBaseContext());
		this.context.put("templates", new Templates());
	}

	/**
	 * TODO
	 * 
	 * @param velocityHelper
	 */
	protected TemplateHelper(VelocityHelper velocityHelper) {
		this(null, velocityHelper);
	}

	/**
	 * TODO
	 * 
	 * @param templateName
	 * @param file
	 * @throws AonException
	 */
	public void processTemplate(String templateName, File file) throws AonException {
		try {
			Writer out = new BufferedWriter(new FileWriter(file));
			this.processTemplate(templateName, out);
			out.close();
		} catch (IOException e) {
			throw new AonException("Error while processing template " + templateName, e);
		}
	}

	/**
	 * TODO
	 * 
	 * @param templateName
	 * @return String
	 * @throws AonException
	 */
	public String processTemplate(String templateName) throws AonException {
		StringWriter out = new StringWriter();
		this.processTemplate(templateName, out);
		return out.toString();
	}

	/**
	 * TODO
	 * 
	 * @param name
	 * @param output
	 * @throws AonException
	 */
	public void processTemplate(String name, Writer output) throws AonException {
		try {
			Template template = this.velocityHelper.getTemplate(name);
			template.merge(this.context, output);
		} catch (ResourceNotFoundException e) {
			throw new AonException("Resource not found while processing template " + name + ". " + e.getMessage(), e);
		} catch (ParseErrorException e) {
			throw new AonException("Parse error while processing template " + name + ". " + e.getMessage(), e);
		} catch (MethodInvocationException e) {
			throw new AonException("MethodInvocationException while processing template " + name + ". "
					+ e.getMessage(), (e.getWrappedThrowable() != null ? e.getWrappedThrowable() : ((Throwable) (e))));
		} catch (Exception e) {
			throw new AonException("Error while processing template " + name, e);
		}
	}

	/**
	 * TODO
	 * 
	 * @param template
	 * @param logTag
	 * @return String
	 * @throws AonException
	 */
	public String processTemplate(String template, String logTag) throws AonException {
		StringWriter out = new StringWriter();
		this.processTemplate(template, logTag, out);
		return out.toString();
	}

	/**
	 * TODO
	 * 
	 * @param template
	 * @param logTag
	 * @param out
	 * @throws AonException
	 */
	public void processTemplate(String template, String logTag, Writer out) throws AonException {
		try {
			this.velocityHelper.getEngine().evaluate(this.context, out, logTag, template);
		} catch (ResourceNotFoundException e) {
			throw new AonException("Resource not found while processing template " + logTag + ". " + e.getMessage(), e);
		} catch (ParseErrorException e) {
			throw new AonException("Parse error while processing template " + logTag + ". " + e.getMessage(), e);
		} catch (MethodInvocationException e) {
			throw new AonException("MethodInvocationException while processing template " + logTag + ". "
					+ e.getMessage(), (e.getWrappedThrowable() != null ? e.getWrappedThrowable() : ((Throwable) (e))));
		} catch (Exception e) {
			throw new AonException("Error while processing template " + logTag, e);
		}
	}

	/**
	 * TODO
	 * 
	 * @param key
	 * @param value
	 */
	public void putInContext(String key, Object value) {
		LOGGER.finest("putInContext " + key + "=" + value);
		if (value == null) {
			throw new IllegalStateException("value must not be null for " + key);
		}
		Object replaced = this.context.put(key, value);
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
	public void removeFromContext(String key, Object expected) {
		LOGGER.finest("removeFromContext " + key + "=" + expected);
		Object replaced = this.context.remove(key);
		if (replaced == null) {
			throw new IllegalStateException(key + " did not exist in template context.");
		}
		if (replaced != expected) {
			throw new IllegalStateException("expected " + key + " to be bound to " + expected + " but was to "
					+ replaced);
		}
	}

	/**
	 * TODO
	 * 
	 * @author Consulting & Development. ecastellano - 22/11/2006
	 * 
	 */
	public class Templates {

		/**
		 * TODO
		 * 
		 * @param name
		 * @return String
		 * @throws AonException
		 */
		public String get(String name) throws AonException {
			return processTemplate(name);
		}

	}

}
