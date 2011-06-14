package com.code.aon.warehouse;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.IHeaderObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.SecurityLevel;

@Entity
@Table(name="warehouse_transfer")
public class WarehouseTransfer implements ITransferObject, IHeaderObject {
	
	private static final long serialVersionUID = -4396726885282376514L;
	
	public WarehouseTransfer() {
		this.issueTime = new Date();
	}

	private Integer id;
    private String series;
    private int number;
	private Date issueTime;
	private Warehouse sourceWarehouse;
	private Warehouse targetWarehouse;
	private String comments;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(length=5)
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	
	@Column(nullable = false)
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

    @Transient
    public String getReferenceCode() {
    	String referenceCode = StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		if (!StringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }

	@Column(name="issue_time",nullable = false)
	public Date getIssueTime() {
		return issueTime;
	}
	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}
	
	@ManyToOne
	@JoinColumn(name="source_warehouse")
	public Warehouse getSourceWarehouse() {
		return sourceWarehouse;
	}
	public void setSourceWarehouse(Warehouse sourceWarehouse) {
		this.sourceWarehouse = sourceWarehouse;
	}

	@ManyToOne
	@JoinColumn(name="target_warehouse")
	public Warehouse getTargetWarehouse() {
		return targetWarehouse;
	}
	public void setTargetWarehouse(Warehouse targetWarehouse) {
		this.targetWarehouse = targetWarehouse;
	}

	@Column(name="comments")
	@Lob
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final WarehouseTransfer o = (WarehouseTransfer) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.series, o.series)
			.append(this.number, o.number)
			.append(this.issueTime, o.issueTime)
			.append(this.sourceWarehouse, o.sourceWarehouse)
			.append(this.targetWarehouse, o.targetWarehouse)
			.append(this.comments, o.comments)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(series)
			.append(number)
			.append(issueTime)
			.append(sourceWarehouse)
			.append(targetWarehouse)
			.append(comments)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
	@Override
	@Transient
	public SecurityLevel getSecurityLevel() {
		// Método necesario por implementar IHeaderObject. 
		// Al no dar soporte de confidencialidad, se devuelve siempre el mismo.
		return SecurityLevel.OFFICIAL;
	}
}