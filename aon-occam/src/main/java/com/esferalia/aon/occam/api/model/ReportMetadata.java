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

	private boolean showCover;

	private int pageOffset;
	private String pageOffsetText;
	
	private boolean hideFilter;
	private boolean hideDateTimeOnFooter;
	private String footerText;

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

	public boolean isShowCover() {
		return showCover;
	}

	public ReportMetadata setShowCover(boolean showCover) {
		this.showCover = showCover;
		return this;
	}

	public int getPageOffset() {
		return pageOffset;
	}
	
	public ReportMetadata setPageOffset(int pageOffset) {
		this.pageOffset = pageOffset;
		return this;
	}

	public String getPageOffsetText() {
		return pageOffsetText;
	}

	public ReportMetadata setPageOffsetText(String pageOffsetText) {
		this.pageOffsetText = pageOffsetText;
		return this;
	}

	public boolean isHideFilter() {
		return hideFilter;
	}

	public ReportMetadata setHideFilter(boolean hideFilter) {
		this.hideFilter = hideFilter;
		return this;
	}

	public boolean isHideDateTimeOnFooter() {
		return hideDateTimeOnFooter;
	}

	public ReportMetadata setHideDateTimeOnFooter(boolean hideDateTimeOnFooter) {
		this.hideDateTimeOnFooter = hideDateTimeOnFooter;
		return this;
	}

	public String getFooterText() {
		return footerText;
	}

	public ReportMetadata setFooterText(String footerText) {
		this.footerText = footerText;
		return this;
	}
	
	
}
