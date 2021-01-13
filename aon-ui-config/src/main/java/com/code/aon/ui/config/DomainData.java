package com.code.aon.ui.config;

import static com.esferalia.aon.watson.util.AonStringUtils.INDEX_NOT_FOUND;
import static com.esferalia.aon.watson.util.AonStringUtils.indexOfIgnoreCase;
import static com.esferalia.aon.watson.util.AonStringUtils.isBlank;
import static com.esferalia.aon.watson.util.AonStringUtils.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DomainData implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer id;
	
	private String logo;

	private String name;
	
	private String description;
	
	private boolean active;
	
	private boolean enableHeredity;
	
	private Date expirationDate;
	
	private String document;
	
	private List<String> cccs ;
	
	public DomainData(Integer id, String name, String description, Date expirationDate, boolean active, boolean enableHeredity) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.expirationDate = expirationDate;
		this.active = active;
		this.enableHeredity = enableHeredity;
		this.cccs = new ArrayList<String>();
	}

	public Integer getId() {
		return id;
	}
	
	public String getLogo() {
		return logo;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isActive() {
		return active;
	}	

	public boolean isEnableHeredity() {
		return enableHeredity;
	}
	
	public String getDocument() {
		return document;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	public void setDocument(String document) {
		this.document = document;
	}
	
	public boolean isExpired() {
		if ( this.expirationDate != null ) {
			return this.expirationDate.compareTo(new Date()) < 0;
		}
		return false;
	}	
	
	public void addCCC(String ccc ) {
		cccs.add(ccc);
	}
	
	public String getCccs() {
		return cccs.stream().collect(Collectors.joining(","));
	}
	
	public String getDisplay(String filter) {
		
		String htmlDescription = StringEscapeUtils.escapeHtml(description);
		
		if ( isBlank(filter) ) 
			return htmlDescription;
		
		Function<String, String> repl = s -> "<b>"+s+"</b>";
		
		StringBuffer display = new StringBuffer();
		try {
			display.append(replace(htmlDescription, filter, repl));
		} catch ( Exception e ) {
			display.append(htmlDescription);
		}
		
		try {
			display.append(" " + replace(document, filter, repl));
		} catch ( Exception e ) {
		}

		for ( String ccc : cccs ) {
			try {
					display.append( " " + replace(ccc, filter, repl));
			} catch ( Exception e ) {			
			}
		}
		
		if ( display.indexOf("<b>") == INDEX_NOT_FOUND) {
			String htmlName = StringEscapeUtils.escapeHtml(name);
			try {
				display.append(" " + replace(htmlName, filter, repl));
			} catch ( Exception e ) {
			}
		}
		

		return display.toString();
	}
	
	public static String replace(final String text, final String searchString,
			Function<String, String> replace) {

		int start = 0;
		int end = indexOfIgnoreCase(text, searchString, start);
		if (end == INDEX_NOT_FOUND) {
			throw new RuntimeException();
		}
		final int replLength = searchString.length();
		final StringBuilder buf = new StringBuilder();
		while (end != INDEX_NOT_FOUND) {			
			String replacement  = replace.apply(text.substring(end,end+replLength));
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			end = indexOfIgnoreCase(text,searchString, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}


}
