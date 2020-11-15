package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.EnumMap;

import com.esferalia.aon.occam.api.model.type.RawdocNature;

public class RawdocDomainData implements Serializable {

	private static final long serialVersionUID = -1902490494107861971L;
	
	private Integer domain;
	private String domainName;
	private String domainDescription;
	
	private EnumMap<RawdocNature,RawdocBreakdown> map = new EnumMap<RawdocNature,RawdocBreakdown>(RawdocNature.class);
	
	public Integer getDomain() {
		return domain;
	}
	public RawdocDomainData setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public RawdocDomainData setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	public String getDomainDescription() {
		return domainDescription;
	}
	public RawdocDomainData setDomainDescription(String domainDescription) {
		this.domainDescription = domainDescription;
		return this;
	}
	
	public RawdocBreakdown getInvoiceBreakdown() {
		if ( map.get(RawdocNature.INVOICE) == null) {
			map.put(RawdocNature.INVOICE, new RawdocBreakdown());
		}
		return map.get(RawdocNature.INVOICE);
	}
	
}
