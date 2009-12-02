package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="activity")
public class Activity implements ITransferObject {

	private static final long serialVersionUID = -1306482179289895727L;

	private Integer id;
	
	private String alias;
	
	private Set<ActivityDetail> details;
	
	private Set<CompanyActivity> companyActivities;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false,length=32)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@OneToMany(mappedBy = "activity", cascade={CascadeType.REMOVE})
	public Set<ActivityDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<ActivityDetail> details) {
		this.details = details;
	}
	
	@OneToMany(mappedBy = "activity", cascade={CascadeType.REMOVE})
    public Set<CompanyActivity> getCompanyActivities() {
        return companyActivities;
    }

	public void setCompanyActivities(Set<CompanyActivity> companyActivities) {
		this.companyActivities = companyActivities;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Activity o = (Activity) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.alias, o.alias)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(alias)
			.append(id)	
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	

}