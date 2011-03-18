/*
 * Created on 10-mar-2005
 *
 */
package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.CONFIG;
import static com.code.aon.ldap.IAonObjectClasses.TOP;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 17-mar-2011
 * @since 1.0
 *
 */
@EntryObject(mainObjectClass=CONFIG, objectClasses={TOP})
public class Config implements ILdapTransferObject {

	private static final long serialVersionUID = -3635587673683977225L;
	
	public static final String COMMON_NAME = "Config";

	private Name id;
	
	/** The version of the schema. */
	private String commonName;
	
	/** Maximum number of users defined for the domain. */
	private Integer maxDocumentSize;

	/** Maximun number of users allowed to access application in each domain. */
	private Integer maxTotalDocumentSize;
	
	public Config() {
		this.maxDocumentSize = 1024;
		this.maxTotalDocumentSize = 10240;
		this.commonName = COMMON_NAME;
	}

	@Id
	public Name getId() {
		return id;
	}
	
	/**
	 * @param id The id to set.
	 */
	public void setId(Name id) {
		this.id = id;
	}

	@RDN
	@Attribute(name=COMMON_NAME_ATTRIBUTE,nullable=false)
	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	@Attribute(name=MAX_DOCUMENT_SIZE_ATTRIBUTE, nullable=false)
	public Integer getMaxDocumentSize() {
		return maxDocumentSize;
	}

	public void setMaxDocumentSize(Integer maxDocumentSize) {
		this.maxDocumentSize = maxDocumentSize;
	}

	@Attribute(name=MAX_TOTAL_DOCUMENT_SIZE_ATTRIBUTE, nullable=false)
	public Integer getMaxTotalDocumentSize() {
		return maxTotalDocumentSize;
	}

	public void setMaxTotalDocumentSize(Integer maxTotalDocumentSize) {
		this.maxTotalDocumentSize = maxTotalDocumentSize;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Config o = (Config) obj;
		if (o.getCommonName() == null && getCommonName() == null) {
			return new EqualsBuilder()
				.append(this.maxDocumentSize, o.maxDocumentSize)
				.append(this.maxTotalDocumentSize, o.maxTotalDocumentSize)				
				.isEquals();
		}
		return ObjectUtils.equals(getCommonName(), o.getCommonName());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(commonName)
			.append(maxDocumentSize)
			.append(maxTotalDocumentSize)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}