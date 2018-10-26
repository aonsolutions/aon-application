package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class ReportMetadata implements Serializable {

	private static final long serialVersionUID = 9019820879058601660L;

	private String title;
	private String subject;
	private String author;
	private String companyName;
	private String keyWords;
	private String filterDescription;
	private int pageOffset;

	public String getTitle() {
		return title;
	}

	public ReportMetadata setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public ReportMetadata setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public ReportMetadata setAuthor(String author) {
		this.author = author;
		return this;
	}

	public String getCompanyName() {
		return companyName;
	}

	public ReportMetadata setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public ReportMetadata setKeyWords(String keyWords) {
		this.keyWords = keyWords;
		return this;
	}

	public String getFilterDescription() {
		return filterDescription;
	}

	public ReportMetadata setFilterDescription(String filterDescription) {
		this.filterDescription = filterDescription;
		return this;
	}

	public int getPageOffset() {
		return pageOffset;
	}
	
	public ReportMetadata setPageOffset(int pageOffset) {
		this.pageOffset = pageOffset;
		return this;
	}
}
