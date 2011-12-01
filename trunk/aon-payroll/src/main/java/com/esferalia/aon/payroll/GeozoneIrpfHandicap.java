package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "geozone_irpf_handicap")
public class GeozoneIrpfHandicap implements ITransferObject {
	
	private static final long serialVersionUID = -5510834054771909961L;
	
	private Integer id;
	private GeozoneIrpf geozoneIrpf;
	private Integer handicap;
	private Double percent;

	@Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn( name="geozone_irpf", nullable=false )
    @ForeignKey(name = "FK_GEOZONE_IRPF_PERCENT_GEOZONE_IRPF")
    @Index(name = "IDX_GEOZONE_IRPF_PERCENT_GEOZONE_IRPF")
    public GeozoneIrpf getGeozoneIrpf() {
        return geozoneIrpf;
    }
    public void setGeozoneIrpf(GeozoneIrpf geozoneIrpf) {
        this.geozoneIrpf = geozoneIrpf;
    }
	
	@Column( length=2 )
	public Integer getHandicap() {
		return handicap;
	}
	public void setHandicap(Integer handicap) {
		this.handicap = handicap;
	}
	
	@Column(precision = 15, scale = 2)
	public Double getPercent() {
		return percent;
	}
	public void setPercent(Double percent) {
		this.percent = percent;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final GeozoneIrpfHandicap o =  (GeozoneIrpfHandicap) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.geozoneIrpf, o.geozoneIrpf)			
				.append(this.handicap, o.handicap)			
				.append(this.percent, o.percent)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.geozoneIrpf)		
			.append(this.handicap)		
			.append(this.percent)		
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
