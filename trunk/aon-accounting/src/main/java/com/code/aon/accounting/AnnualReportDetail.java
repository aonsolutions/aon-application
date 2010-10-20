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

import com.code.aon.accounting.enumeration.AnnualReportStyle;
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
	private AnnualReportStyle  style;
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
	
	@Column(name="style")
	public AnnualReportStyle getStyle() {
		return style;
	}

	public void setStyle(AnnualReportStyle style) {
		this.style = style;
	}
	
	@Transient
	public String getContentResolved() {
		return contentResolved!=null?contentResolved:content;
	}

	public void setContentResolved(String contentResolved) {
		this.contentResolved = contentResolved;
	}

	@Transient
	public String getCssStyle() {
		if (getStyle() == AnnualReportStyle.MAIN_TITLE) {
			return "font-size: 1.3em; font-weight: bold; text-align: center; margin-bottom: 15px; margin-top: 15px;";
		}
		if (getStyle() == AnnualReportStyle.TITLE) {
			return "font-size: 1.2em; font-weight: bold; margin-bottom: 10px; margin-top: 10px;";
		}
		if (getStyle() == AnnualReportStyle.SUBTITLE) {
			return "margin-left: 10px; font-size: 1.1em; font-weight: bold; margin-bottom: 5px; margin-top: 5px;";
		}
		if (getStyle() == AnnualReportStyle.NORMAL) {
			return "margin-left: 20px; text-align: justify; margin-bottom: 3px; margin-top: 3px;";
		}
		if (getStyle() == AnnualReportStyle.COLUMN_HEADER) {
			return "text-align: center; font-weight: bold;";
		}
		return null;
	}
	@Transient
	public String getBeforeContent() {
		if (getStyle() == AnnualReportStyle.TABLE) {
			return "<table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" style=\"margin-top: 10px; margin-bottom: 10px;\">";
		}
		if (getStyle() == AnnualReportStyle.ROW) {
			return "<tr>";
		}
		if (getStyle() == AnnualReportStyle.COLUMN_TEXT) {
			return "<td style=\"border: solid #DDDDDD 1px; padding: 3px; text-align: left;\">";
		}
		if (getStyle() == AnnualReportStyle.COLUMN_NUMBER) {
			return "<td style=\"border: solid #DDDDDD 1px; padding: 3px; text-align: right;\">";
		}
		if (getStyle() == AnnualReportStyle.COLUMN_HEADER) {
			return "<td style=\"border: solid #DDDDDD 1px; padding: 3px; background-color: #EEEEEE;\">";
		}
		return null;
	}
	@Transient
	public String getAfterContent() {
		if (getStyle() == AnnualReportStyle.TABLE_END) {
			return "</table>";
		}
		if (getStyle() == AnnualReportStyle.ROW_END) {
			return "</tr>";
		}
		if (getStyle() == AnnualReportStyle.COLUMN_TEXT) {
			return "</td>";
		}
		if (getStyle() == AnnualReportStyle.COLUMN_NUMBER) {
			return "</td>";
		}
		if (getStyle() == AnnualReportStyle.COLUMN_HEADER) {
			return "</td>";
		}
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
			.append(this.style, o.style)
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
			.append(this.style)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}