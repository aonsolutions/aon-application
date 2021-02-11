package com.code.aon.ui.config;

import static com.code.aon.ui.config.DomainData.replace;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonStringUtils.isBlank;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import org.apache.commons.lang.StringEscapeUtils;

public class ContractData implements Serializable {
	
	private Integer id;
	private String document;
	private String fullName;
	private String ssNumber;
	
	private Date endDate;
	private Date startDate;
	
	private Integer domainId;
	private String domainDescription;
	

	public ContractData(Integer id, String document, String fullName, String ssNumber, Date endDate, Date startDate, Integer domainId,  String domainDescription) {
		super();
		this.id = id;
		this.document = document;
		this.fullName = fullName;
		this.ssNumber = ssNumber;
		this.endDate = endDate;
		this.startDate = startDate;
		this.domainId = domainId;
		this.domainDescription = domainDescription;
	}

	public Integer getId() {
		return id;
	}
	
	public String getDocument() {
		return document;
	}


	public String getFullName() {
		return fullName;
	}


	public String getSsNumber() {
		return ssNumber;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public boolean isActive() {
		return endDate == null 
				|| endDate.compareTo(getFirstDayOfMonth(new Date())) >= 0;
	}
	
	public Integer getDomainId() {
		return domainId;
	}
	
	public String getDomainDescription() {
		return domainDescription;
	}
	
	public String getDisplay(String filter) {
		
		String htmlFullName = StringEscapeUtils.escapeHtml(fullName);
		
		if ( isBlank(filter) ) 
			return htmlFullName;
		

		Function<String, String> repl = s -> "<b>"+s+"</b>";

		for ( String word : filter.split("\\s+") ) {
			try {
				htmlFullName = replace(htmlFullName, word, repl);
			} catch ( Exception e ) {
			}
		}
		
		StringBuffer display = new StringBuffer();

		display.append(htmlFullName);

		try {
			display.append(" " + replace(document, filter, repl));
		} catch ( Exception e ) {
		}

		try {
			display.append( " " + replace(ssNumber, filter, repl));
		} catch ( Exception e ) {			
		}

		
		

		return display.toString();
	}
	
}
