package com.code.aon.fiscal;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.Enterprise;
import com.code.aon.fiscal.enumeration.WithholdingDetailKey;
import com.code.aon.fiscal.enumeration.WithholdingDetailSubkey;
import com.code.aon.registry.enumeration.DocumentType;

@Entity
@Table(name = "fs_prof_retention")
public class ProfessionalRetention implements ITransferObject{

	private static final long serialVersionUID = 3888215072552064718L;
	
	private Integer id;
	private Enterprise enterprise;
	private Date paymentDate;
	private String document;
	private DocumentType documentType;
	private Country documentCountry;
	private String name;
	private String concept;
	private WithholdingDetailKey key;
	private WithholdingDetailSubkey subkey;
	private Double taxableBase;
	private Double percent;
	private Double quota;
	private boolean inKind;
	
	@Id
    @GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="enterprise", nullable = false)
    @ForeignKey(name="FK_FS_PROF_RET_ENTERPRISE")
    @Index(name="IDX_FS_PROF_RET_ENTERPRISE")  
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
    @Column(name="payment_date")
    @Temporal(TemporalType.DATE)
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	@Column(name="document", length=16, nullable=false)
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	
	@Column(name="document_type",nullable=false)
	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	
	@Column(name="document_country",nullable=false)
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.common.enumeration.Country") })
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public void setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
	}
	
	@Column(name="name", length=64, nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="withholding_key", nullable=false)
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.fiscal.enumeration.WithholdingDetailKey") })
	public WithholdingDetailKey getKey() {
		return key;
	}
	public void setKey(WithholdingDetailKey key) {
		this.key = key;
	}

	@Column(name="withholding_subkey")
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.fiscal.enumeration.WithholdingDetailSubkey") })
	public WithholdingDetailSubkey getSubkey() {
		return subkey;
	}
	public void setSubkey(WithholdingDetailSubkey subkey) {
		this.subkey = subkey;
	}

	@Column(name="concept", length=64)
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	
    @Column(name="taxable_base")
	public Double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(Double taxableBase) {
		this.taxableBase = taxableBase;
	}
	
    @Column(name="percent")
	public Double getPercent() {
		return percent;
	}
	public void setPercent(Double percent) {
		this.percent = percent;
	}
	
    @Column(name="quota")
	public Double getQuota() {
		return quota;
	}
	public void setQuota(Double quota) {
		this.quota = quota;
	}
	
    @Column(name="in_kind")
	public boolean isInKind() {
		return inKind;
	}
	public void setInKind(boolean inKind) {
		this.inKind = inKind;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProfessionalRetention o = (ProfessionalRetention) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.enterprise,o.enterprise)
			.append(this.paymentDate,o.paymentDate)
			.append(this.document,o.document)
			.append(this.documentType,o.documentType)
			.append(this.documentCountry,o.documentCountry)
			.append(this.name,o.name)
			.append(this.concept,o.concept)
			.append(this.taxableBase,o.taxableBase)
			.append(this.percent,o.percent)
			.append(this.quota,o.quota)
			.append(this.inKind,o.inKind)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)		
			.append(this.enterprise)
			.append(this.paymentDate)
			.append(this.document)
			.append(this.documentType)
			.append(this.documentCountry)
			.append(this.name)
			.append(this.concept)
			.append(this.taxableBase)
			.append(this.percent)
			.append(this.quota)
			.append(this.inKind)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}
