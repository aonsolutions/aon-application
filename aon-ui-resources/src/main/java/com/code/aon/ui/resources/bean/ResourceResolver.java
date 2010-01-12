package com.code.aon.ui.resources.bean;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class ResourceResolver {
	
	private String resourceContextPath;
	
	private String resourceURIPreffix;
	
	private FakeMap resolve;
	
	private FakeMap resolveLocal;
	
	public ResourceResolver() {
		this.resolve = new FakeMap(false);
		this.resolveLocal = new FakeMap(true);
	}
	
	public String getResourceContextPath() {
		return resourceContextPath;
	}

	public void setResourceContextPath(String resourceContextPath) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> parameters = context.getExternalContext().getRequestParameterMap();
		if ( parameters.containsKey("aonDesktop") ) {
			this.resourceContextPath = "../../" + resourceContextPath;
		} else {
			this.resourceContextPath = "../" + resourceContextPath;
		}
	}

	public String getResourceURIPreffix() {
		return resourceURIPreffix;
	}

	public void setResourceURIPreffix(String resourceURIPreffix) {
		this.resourceURIPreffix = resourceURIPreffix;
	}

	public FakeMap getResolve() {
		return resolve;
	}
	
	public FakeMap getResolveLocal() {
		return resolveLocal;
	}
	
	@SuppressWarnings("unchecked")
	private class FakeMap extends AbstractMap {
		
		private boolean local;
		
		public FakeMap(boolean local) {
			this.local = local;
		}

		@Override
		public Set entrySet() {
			return null;
		}
		
		@Override
		public Object get(Object key) {
			if ( local ) {
				return resourceURIPreffix + key;
			}
			return resourceContextPath + resourceURIPreffix + key;
		}
		
	}

}
