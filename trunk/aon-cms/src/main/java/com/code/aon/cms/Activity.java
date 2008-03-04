package com.code.aon.cms;

import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="activity")
public class Activity implements ITransferObject {

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


}