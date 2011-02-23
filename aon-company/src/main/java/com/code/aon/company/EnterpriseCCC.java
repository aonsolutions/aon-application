package com.code.aon.company;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.enumeration.CCCType;
import com.code.aon.geozone.GeoZone;

@Entity
@Table(name="enterprise_ccc")
public class EnterpriseCCC implements ITransferObject {
	
	private static final long serialVersionUID = -8232319254583226675L;

	private Integer id;
    private String ccc;
	private CCCType type;
	private GeoZone geozone;
    private EnterpriseActivity activity; 

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="ccc", length = 11)
	public String getCcc() {
		return ccc;
	}
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	@Column( nullable = false )
	public CCCType getType() {
		return type;
	}
	public void setType(CCCType type) {
		this.type = type;
	}

	@ManyToOne
	@JoinColumn( name="geozone")
	@ForeignKey(name = "FK_ENTERPRICE_CCC_GEOZONE")
	@Index(name = "IDX_ENTERPRICE_CCC_GEOZONE")	
	public GeoZone getGeozone() {
		return geozone;
	}
	public void setGeozone(GeoZone geozone) {
		this.geozone = geozone;
	}

	@OneToOne
	@JoinColumn(name="enterprise_activity", nullable = false)
	@ForeignKey(name = "FK_ENTERPRICE_CCC_ENTERPRISEACTIVITY")
	@Index(name = "IDX_ENTERPRICE_CCC_ENTERPRISEACTIVITY")    
	public EnterpriseActivity getActivity() {
		return activity;
	}
	public void setActivity(EnterpriseActivity activity) {
		this.activity = activity;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EnterpriseCCC o = (EnterpriseCCC) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.activity, o.activity)
				.append(this.ccc, o.ccc)
				.append(this.geozone, o.geozone)
				.append(this.type, o.type)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(activity)
			.append(ccc)
			.append(geozone)
			.append(id)
			.append(type)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
