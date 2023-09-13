package com.code.aon.ui.common.session;

import java.util.Enumeration;
import java.util.Hashtable;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;

/**
 * Mock implementation of the {@link jakarta.servlet.http.HttpSession} interface.
 * Supports the Servlet 2.4 API level.
 *
 * <p>Used for testing the web framework; also useful for testing
 * application controllers.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Mark Fisher
 * @since 1.0.2
 */
public class MockHttpSession implements HttpSession {

	public static final String SESSION_COOKIE_NAME = "JSESSION";

	private final long creationTime = System.currentTimeMillis();

	private int maxInactiveInterval;

	private long lastAccessedTime = System.currentTimeMillis();

	private final ServletContext servletContext;

	private final Hashtable attributes = new Hashtable();

	private boolean invalid = false;

	private boolean isNew = true;

	/**
	 * Create a new MockHttpSession.
	 * @param servletContext the ServletContext that the session runs in
	 * @param id a unique identifier for this session
	 */
	public MockHttpSession(ServletContext servletContext) {
		this.servletContext = servletContext;
	}


	public long getCreationTime() {
		return this.creationTime;
	}

	public String getId() {
		return "1";
	}

	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

	public ServletContext getServletContext() {
		return this.servletContext;
	}

	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}

	public int getMaxInactiveInterval() {
		return this.maxInactiveInterval;
	}


	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	public Object getValue(String name) {
		return getAttribute(name);
	}

	public Enumeration getAttributeNames() {
		return this.attributes.keys();
	}

	public String[] getValueNames() {
		return (String[]) this.attributes.keySet().toArray(new String[this.attributes.size()]);
	}

	public void setAttribute(String name, Object value) {
		if (value != null) {
			this.attributes.put(name, value);
			if (value instanceof HttpSessionBindingListener) {
				((HttpSessionBindingListener) value).valueBound(new HttpSessionBindingEvent(this, name, value));
			}
		}
		else {
			removeAttribute(name);
		}
	}

	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	public void removeAttribute(String name) {
		Object value = this.attributes.remove(name);
		if (value instanceof HttpSessionBindingListener) {
			((HttpSessionBindingListener) value).valueUnbound(new HttpSessionBindingEvent(this, name, value));
		}
	}

	public void removeValue(String name) {
		removeAttribute(name);
	}

	public void invalidate() {
		this.invalid = true;
	}

	public boolean isNew() {
		return this.isNew;
	}

}
