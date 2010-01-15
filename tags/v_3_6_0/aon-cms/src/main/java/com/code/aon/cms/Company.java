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
@Table(name="company")
public class Company implements ITransferObject {

	private static final long serialVersionUID = 1954910182440296235L;

	private Integer id;

	private String name;
	
	private String telephone;
	
	private String fax;
	
	private String email;
	
	private String address;
	
	private String locality;
	
	private String province;
	
	private Integer postal_code;
	
	private String web;
	
	private String logo; 	
	
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

	@Column(nullable=false,length=64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length=20)
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Column(length=20)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(length=64)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(length=128)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(length=64)
	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	@Column(length=64)
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(Integer postal_code) {
		this.postal_code = postal_code;
	}

	@Column(length=128)
	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	@Column(length=255)
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@OneToMany(mappedBy = "company", cascade={CascadeType.REMOVE})
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
		final Company o = (Company) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.address, o.address)
				.append(this.email, o.email)				
				.append(this.fax, o.fax)
				.append(this.locality, o.locality)				
				.append(this.logo, o.logo)
				.append(this.name, o.name)
				.append(this.postal_code, o.postal_code)				
				.append(this.province, o.province)				
				.append(this.telephone, o.telephone)
				.append(this.web, o.web)
			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(address)
			.append(email)
			.append(fax)
			.append(id)	
			.append(locality)
			.append(logo)			
			.append(name)
			.append(postal_code)
			.append(province)
			.append(telephone)
			.append(web)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}