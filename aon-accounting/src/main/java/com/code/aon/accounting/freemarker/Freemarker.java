package com.code.aon.accounting.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import com.code.aon.common.AonException;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.MapModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class Freemarker {

	private static final String ENCODING = "ISO-8859-1";
	private static final String PACKAGE = "/com/code/aon/accounting/freemarker/";

	private Locale locale;
	private String encoding;
	private Map<String, Object> context;

	public Freemarker(Map<String,Object> context) {
		this.context = context;
	}

	public Locale getLocale() {
		if (this.locale == null) {
			this.locale = new Locale("es");
		}
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getEncoding() {
		if (this.encoding == null) {
			this.encoding = ENCODING;
		}
		return this.encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public void addToContext(String key, Object value) {
		getContext().put(key, value);
	}

	public void addToContext(Map<String, Object> map) {
		for (String key : map.keySet()) {
			getContext().put(key, map.get(key));
		}
	}

	public void process(String template, Writer out) throws AonException {
		try {
			Configuration cfg = new Configuration();
			cfg.setClassForTemplateLoading(Freemarker.class, PACKAGE);
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
			Template t = cfg.getTemplate(template, getLocale(), getEncoding());
			BeansWrapper wrapper = new DefaultObjectWrapper();
			MapModel model = new MapModel(getContext(), wrapper);
			t.process(model, out);
			out.flush();
		} catch (IOException e) {
			throw new AonException(e.getMessage(), e);
		} catch (TemplateException e) {
			throw new AonException(e.getMessage(), e);
		}

	}
}
