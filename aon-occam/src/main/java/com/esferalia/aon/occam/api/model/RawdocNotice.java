package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class RawdocNotice implements Serializable {

	private static final long serialVersionUID = 320474275458042903L;
		
	private Integer count;
	private LinkedList<Integer> domains;
	private HashMap<String, Integer> domainCount;
	
	public RawdocNotice() {
	
	}
	
	public Integer getCount() {
		return count;
	}
	
	public RawdocNotice setCount(Integer count) {
		this.count = count;
		return this;
	}
	
	public LinkedList<Integer> getDomains() {
		return domains;
	}
	
	public RawdocNotice setDomains(LinkedList<Integer> domains) {
		this.domains = domains;
		return this;
	}
	
	public HashMap<String, Integer> getDomainCount() {
		return domainCount;
	}
	
	public RawdocNotice setDomainCount(HashMap<String, Integer> domainCount) {
		this.domainCount = domainCount;
		return this;
	}
	
}
