package com.code.aon.accounting;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "annual_report_detail")
public class AnnualReportDetail implements ITransferObject {
	
	private static final long serialVersionUID = -7813781883946689430L;
	
	private Integer id;	
	private AnnualReport annualReport;
	private Integer sortKey;
	private String  content;
	private String  contentResolved;

	@Id
	@GeneratedValue
	@Column(nullable = false, length=11)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="annual_report")	
	@ForeignKey(name = "FK_ANNUAL_REPORT_DETAIL_ANNUAL_REPORT")
	@Index(name = "IDX_ANNUAL_REPORT_DETAIL_ANNUAL_REPORT")
	public AnnualReport getAnnualReport() {
		return annualReport;
	}

	public void setAnnualReport(AnnualReport annualReport) {
		this.annualReport = annualReport;
	}
		
	@Column(name="sortKey")
	public Integer getSortKey() {
		return sortKey;
	}

	public void setSortKey(Integer sortKey) {
		this.sortKey = sortKey;
	}

	@Lob
	@Type(type="stringClob")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Transient
	public String getContentResolved() {
		return contentResolved!=null?contentResolved:content;
	}

	public void setContentResolved(String contentResolved) {
		this.contentResolved = contentResolved;
	}

	@Transient
	public String getBeforeContent() {
		return null;
	}
	@Transient
	public String getAfterContent() {
		return null;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final AnnualReportDetail o = (AnnualReportDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.annualReport, o.annualReport)
			.append(this.sortKey, o.sortKey)
			.append(this.content, o.content)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.annualReport)
			.append(this.sortKey)
			.append(this.content)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}