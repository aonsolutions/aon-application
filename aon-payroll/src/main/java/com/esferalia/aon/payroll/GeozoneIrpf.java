package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.geozone.GeoZone;

@Entity
@Table(name = "geozone_irpf")
public class GeozoneIrpf implements ITransferObject {
	
	private static final long serialVersionUID = 6644808573614667399L;

	private Integer id;
	private GeoZone geozone;
	private Date startDate;
	private Date endDate;
	private Double amount;

	@Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn( name="geozone", nullable=false )
    @ForeignKey(name = "FK_GEOZONE_IRPF_GEOZONE")
    @Index(name = "IDX_GEOZONE_IRPF_GEOZONE")
    public GeoZone getGeozone() {
        return geozone;
    }
    public void setGeozone(GeoZone geozone) {
        this.geozone = geozone;
    }
    
	@Temporal(TemporalType.DATE)
	@Column( name = "start_date", nullable=false )
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Temporal(TemporalType.DATE)
	@Column( name = "end_date" )
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(precision = 15, scale = 3)
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final GeozoneIrpf o =  (GeozoneIrpf) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.startDate, o.startDate)			
				.append(this.endDate, o.endDate)			
				.append(this.geozone, o.geozone)			
				.append(this.amount, o.amount)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.geozone)		
			.append(this.startDate)		
			.append(this.endDate)		
			.append(this.amount)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
