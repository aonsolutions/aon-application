package com.code.aon.ui.config;

import static com.code.aon.ui.config.DomainData.replace;
import static com.esferalia.aon.watson.util.AonStringUtils.INDEX_NOT_FOUND;
import static com.esferalia.aon.watson.util.AonStringUtils.indexOfIgnoreCase;
import static com.esferalia.aon.watson.util.AonStringUtils.isBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;

import com.code.aon.AonVersion;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
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
	
	private DomainType type;
	
	private int[] rawdocCount;
	private int[] invoiceCount;
	
	public DomainData(Integer id, String name, String description, Date expirationDate, boolean active, boolean enableHeredity) {
		this(id, name, description, expirationDate, active, enableHeredity, null);
	}

	public DomainData(Integer id, String name, String description, Date expirationDate, boolean active, boolean enableHeredity, DomainType type) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.expirationDate = expirationDate;
		this.active = active;
		this.enableHeredity = enableHeredity;
		this.cccs = new ArrayList<String>();
		this.type = type;

		// A single-dimensional array is created of the specified length, 
		// and each component of the array is initialized to its default value.
		// For type int, the default value is zero, that is, 0.
		this.rawdocCount = new int[RawdocStatus.values().length];
		this.invoiceCount = new int[InvoiceStatus.values().length];
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

	public boolean isAdmin() {
		return type == DomainType.ADMIN;
	}

	public boolean isOffice() {
		return type == DomainType.OFFICE;
	}

	public boolean isEnterprise() {
		return type == DomainType.ENTERPRISE;
	}

	public boolean isGeneric() {
		return type == DomainType.GENERIC;
	}

	public boolean isConsultancy() {
		return type == DomainType.CONSULTANCY;
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
		
		UnaryOperator<String> repl = s -> "<b>"+s+"</b>";
		StringBuilder display = new StringBuilder();
		
		// Replace names
		String aux = description;
		List<String> matches = AonStringUtils.getMatching(description, filter);
		
		for (String match : matches) {
			try {
				aux = replace(aux, match, repl); 
			} catch (Exception e) {}
		}
				
		display.append(aux);
		

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
	
	public int getRawdocInboxCount() {
		return getRawdocCount(RawdocStatus.INBOX);
	}

	public boolean hasRawdocInbox() {
		return getRawdocInboxCount() > 0 ;
	}

	public int getRawdocRejectedCount() {
		return getRawdocCount(RawdocStatus.REJECTED);
	}

	public boolean hasRawdocRejected() {
		return getRawdocRejectedCount() > 0 ;
	}

	public int getRawdocCount(RawdocStatus status) {
		return rawdocCount[status.ordinal()];
	}
	
	public void setRawdocCount(RawdocStatus status, int count) {
		rawdocCount[status.ordinal()] = count;
	}

	public int getInvoicePendingCount() {
		return getInvoiceCount(InvoiceStatus.PENDING);
	}

	public boolean hasInvoicePending() {
		return getInvoicePendingCount() > 0;
	}

	public int getInvoiceCount(InvoiceStatus status) {
		return invoiceCount[status.ordinal()];
	}

	public void setInvoiceCount(InvoiceStatus status, int count) {
		invoiceCount[status.ordinal()] = count;
	}
	
	
	
	public static String replace(final String text, final String searchString,
			UnaryOperator<String> replace) {
		
		if ( isBlank(text) || isBlank(searchString))
			return text;
		
		int start = 0;
		int end = indexOfIgnoreCase(text, searchString, start);
		if (end == INDEX_NOT_FOUND) {
			throw new IndexOutOfBoundsException();
		}
		final int replLength = searchString.length();
		final StringBuilder buf = new StringBuilder();
		while (end != INDEX_NOT_FOUND) {			
			String replacement  = replace.apply(text.substring(end,end+replLength));
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			end = indexOfIgnoreCase(text,searchString, start);
			
			// Array overflow control
			if(buf.toString().length() > Integer.MAX_VALUE / 2) {
				break;
			}
		}
		buf.append(text.substring(start));
		return buf.toString();
	}
	
}
