package com.code.aon.webmail.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.webmail.ISignature;
import com.code.aon.webmail.enumeration.MailSource;

@Entity
@Table(name="signature")
public class Signature implements ITransferObject, ISignature {

	private static final long serialVersionUID = 6711062069044244781L;
	
	public static final String SIGNATURE_SOURCE_ID = "Signature.sourceId";

	public static final String SIGNATURE_SOURCE = "Signature.source";
	
	public static final String SIGNATURE_NAME = "Signature.name";

	// ident
	private Integer id;

	// signature
    private String signature;

	// name
    private String name;
    
	private MailSource source;

	private Integer sourceId;

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the signature
	 */
	@Lob
	@Type(type="stringClob")
	@Column(nullable=false)
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * @return the name
	 */
	@Column(nullable=false,length=64)
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(nullable=false)
	public MailSource getSource() {
		return source;
	}
	public void setSource(MailSource source) {
		this.source = source;
	}
	
	@Column(name="source_id")
	public Integer getSourceId() {
		return sourceId;
	}
	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}	

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Signature o = (Signature) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name, o.name)
				.append(this.signature, o.signature)				
				.append(this.source, o.source)
				.append(this.sourceId, o.sourceId)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(name)
			.append(signature)
			.append(source)
			.append(sourceId)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("id", id).
			append("name", name).
			append("signature", StringUtils.abbreviate(signature, 64)).
			append("source", source).
			append("sourceId", sourceId).
			toString();
	}
	
}