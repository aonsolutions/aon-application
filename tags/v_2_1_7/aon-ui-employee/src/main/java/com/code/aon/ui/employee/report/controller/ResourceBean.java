/**
 * 
 */
package com.code.aon.ui.employee.report.controller;

import com.code.aon.company.resources.Resource;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 13/09/2007
 *
 */
public class ResourceBean {

	/** Resource. */
	private Resource resource;
	/** Employee position. */
	private String position;
	/** Employee Working place - Working activity origin. */
	private String origin;

	/**
	 * @param resource
	 */
	public ResourceBean(Resource resource) {
		super();
		this.resource = resource;
	}
	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}
	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}
	/**
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}
	/**
	 * @param resource the resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	
}