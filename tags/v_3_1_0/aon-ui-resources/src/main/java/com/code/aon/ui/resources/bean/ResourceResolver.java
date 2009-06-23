package com.code.aon.ui.resources.bean;

import java.util.AbstractMap;
import java.util.Set;

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
		this.resourceContextPath = "../" + resourceContextPath;
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
