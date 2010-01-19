package com.code.aon.ui.resources.bean;

import java.util.AbstractMap;
import java.util.Set;

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
	public class FakeMap extends AbstractMap<String,String> {
		
		private boolean local;
		
		public FakeMap(boolean local) {
			this.local = local;
		}

		@Override
		public Set entrySet() {
			return null;
		}
		
		@Override
		public String get(Object key) {
			String result;
			if ( local ) {
				result = StringUtils.join( new Object[] {resourceURIPreffix, key} );
			} else {
				result = StringUtils.join( new Object[] {resourceContextPath, resourceURIPreffix, key} );	
			}
			return StringUtils.removeStart( result, "/");
		}
		
	}

}
