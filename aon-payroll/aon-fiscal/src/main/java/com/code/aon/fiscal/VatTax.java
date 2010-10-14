package com.code.aon.fiscal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.fiscal.enumeration.VatPeriod;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.fiscal.enumeration.VatTaxType;

@Entity
@Table(name = "fs_vat")
public class VatTax implements ITransferObject {
	
	private static final long serialVersionUID = 5692053383866684819L;

    private Integer id;
	private Integer year;
	private VatPeriod period;
	private VatTaxType type;
	private String comments;
	private VatTaxStatus status;
	
    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false)
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }

    @Column(name = "period")
    public VatPeriod getPeriod() {
        return period;
    }
    public void setPeriod(VatPeriod period) {
        this.period = period;
    }

    @Column(name = "type")
    public VatTaxType getType() {
        return type;
    }
    public void setType(VatTaxType type) {
        this.type = type;
    }

    @Column(name="comments")
	@Lob
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(name="status")
    public VatTaxStatus getStatus() {
        return status;
    }
    public void setStatus(VatTaxStatus status) {
        this.status = status;
    }
    
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final VatTax o = (VatTax) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.year, o.year)			
				.append(this.period, o.period)
				.append(this.type, o.type)				
				.append(this.comments, o.comments)			
				.append(this.status, o.status)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.year)			
			.append(this.period)
			.append(this.type)				
			.append(this.comments)			
			.append(this.status)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}