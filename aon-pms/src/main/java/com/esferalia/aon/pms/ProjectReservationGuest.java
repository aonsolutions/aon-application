package com.esferalia.aon.pms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="project_reservation_guest")
public class ProjectReservationGuest implements ITransferObject {

	private static final long serialVersionUID = -2595051575335189544L;

	private Integer id;
	private ProjectReservation projectReservation;
	private int guestIndex;
    private String name;
    private String surname;
    private String treatment;
    private String email;
    private String phone;
    private String address;
    private String zip;
    private String city;
    private String province;
    private String country;

    @Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="project_reservation", nullable=false)
	public ProjectReservation getProjectReservation() {
		return projectReservation;
	}

	public void setProjectReservation(ProjectReservation projectReservation) {
		this.projectReservation = projectReservation;
	}

    @Column(name="guest_index", nullable=false)
    public int getGuestIndex() {
        return guestIndex;
    }
    public void setGuestIndex(int guestIndex) {
        this.guestIndex = guestIndex;
    }

    @Column(length=64, nullable=false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(length=64, nullable=false)
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Column(length=4)
    public String getTreatment() {
        return treatment;
    }
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    @Column(length=128)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(length=32)
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(length=256)
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Column(length=16)
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column(length=64)
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    @Column(length=64)
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }

    @Column(length=2)
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @Transient
	public String getFullName() throws ManagerBeanException {
    	String fullName = StringUtils.isEmpty(getTreatment()) ? "" : getTreatment() + " ";
    	fullName += StringUtils.isEmpty(getName()) ? "" : getName() + " ";
    	fullName += StringUtils.isEmpty(getSurname()) ? "" : getSurname() + " ";
    	return fullName;
	}

    @Transient
	public String getFullAddress() throws ManagerBeanException {
    	String fullAddress = StringUtils.isEmpty(getAddress()) ? "" : getAddress() + " ";
    	fullAddress += StringUtils.isEmpty(getZip()) ? "" : getZip() + " - ";
    	fullAddress += StringUtils.isEmpty(getCity()) ? "" : getCity() + " ";
    	fullAddress += StringUtils.isEmpty(getProvince()) ? "" : "(" + getProvince() + ") ";
    	fullAddress += StringUtils.isEmpty(getCountry()) ? "" : getCountry();
    	return fullAddress;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProjectReservationGuest o = (ProjectReservationGuest) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.address, o.address)
				.append(this.city, o.city)
				.append(this.country, o.country)
				.append(this.email, o.email)
				.append(this.guestIndex, o.guestIndex)
				.append(this.name, o.name)
				.append(this.phone, o.phone)
				.append(this.projectReservation, o.projectReservation)
				.append(this.province, o.province)
				.append(this.surname, o.surname)
				.append(this.treatment, o.treatment)
				.append(this.zip, o.zip)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(address)
			.append(city)
			.append(country)
			.append(email)
			.append(guestIndex)
			.append(id)
			.append(name)
			.append(phone)
			.append(projectReservation)
			.append(province)
			.append(surname)
			.append(treatment)
			.append(zip)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}