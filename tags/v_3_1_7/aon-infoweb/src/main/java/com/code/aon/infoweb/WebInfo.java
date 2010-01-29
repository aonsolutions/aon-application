package com.code.aon.infoweb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.Company;

@Entity
@Table(name="web_info")
public class WebInfo implements ITransferObject {
	
	private static final long serialVersionUID = -5089020866799812305L;

	private Integer id;
	
	private Company company;
	
	private String commercialDescription;
		
	private String schedule;
	
	private String slogan;

	@Id
	@GeneratedValue
	@Column(name="id",nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn( name="company",nullable=false )
	@ForeignKey(name="FK_WEB_INFO_COMPANY")
	@Index(name="IDX_WEB_INFO_COMPANY")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Lob
	@Type(type="stringClob")  
	@Column(name="commercial_description")
	public String getCommercialDescription() {
		return commercialDescription;
	}

	public void setCommercialDescription(String commercialDescription) {
		this.commercialDescription = commercialDescription;
	}

	@Lob
	@Type(type="stringClob")  
	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	@Column(length=64)
	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final WebInfo o = (WebInfo) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.commercialDescription, o.commercialDescription)
				.append(this.company, o.company)
				.append(this.schedule, o.schedule)
				.append(this.slogan, o.slogan)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(commercialDescription)
			.append(company)
			.append(id)
			.append(schedule)
			.append(slogan)
			.toHashCode();
	}

	@Override
	public String toString() {
	     return new ToStringBuilder(this).
	       append("id", id).
	       append("company", company.getId()).
	       append("commercialDescription", StringUtils.abbreviate(schedule, 32)).
	       append("schedule", StringUtils.abbreviate(schedule, 32)).
	       append("slogan", slogan).	       
	       toString();
	}
	
}