package com.esferalia.aon.in.payroll.excel;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

public class RemunerationRecordData {
	private String socialReason;
	private String enterpriseDocument;
	private Date startDate;
	private Date endDate;
	private Optional<byte[]> logo;
	
	private Collection<IRetributiveConcept> concepts;
	private LinkedHashMap<String, LinkedList<IRemunerationRecordEntry>> entries;
	
	public RemunerationRecordData () {
		super();
		logo = Optional.empty();
	}
	
	public String getSocialReason() {
		return socialReason;
	}
	public void setSocialReason(String socialReason) {
		this.socialReason = socialReason;
	}
	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}
	public void setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Collection<IRetributiveConcept> getConcepts() {
		return concepts;
	}
	public void setConcepts(Collection<IRetributiveConcept> concepts) {
		this.concepts = concepts;
	}
	public LinkedHashMap<String, LinkedList<IRemunerationRecordEntry>> getEntries() {
		return entries;
	}
	public void setEntries(LinkedHashMap<String, LinkedList<IRemunerationRecordEntry>> entries) {
		this.entries = entries;
	}
	public Optional<byte[]> getLogo() {
		return logo;
	}
	public void setLogo(Optional<byte[]> logo) {
		this.logo = logo;
	}
	
	
}
