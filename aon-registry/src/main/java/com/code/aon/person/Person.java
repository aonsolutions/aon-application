package com.code.aon.person;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;

@Entity
@Table(name="person")
public class Person implements ITransferObject, IRegistry {

	private static final long serialVersionUID = -8638619556227571794L;

	private Integer id;
	private Registry registry;
	private Date birthDate;
	private Gender gender;
	private MaritalStatus maritalStatus;
	private String socialSecurityNumber;
	private String name;
	private String firstSurname;
	private String secondSurname;

    @Id
	@Column(name="registry")
	@GeneratedValue(generator="registry_id")
	@GenericGenerator(name="registry_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="registry")})
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Column(name="birth_date")
	@Temporal(TemporalType.DATE)
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Column(nullable=false)
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Column(name="marital_status", nullable=false)
	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	@Column(name="social_security_num", length = 32)
	@Index(name = "IDX_EMPLOYEE_SOCIAL_SECURITY_NUM")
	public String getSocialSecurityNumber() {
		return socialSecurityNumber;
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
	}
	
	@Column(length=64)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="first_surname", length = 64)
	public String getFirstSurname() {
		return firstSurname;
	}

	public void setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
	}

	@Column(name="second_surname", length = 64)
	public String getSecondSurname() {
		return secondSurname;
	}

	public void setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
	}
	
	@Transient
    public String getFullName() {
		return getFirstSurname() + ((StringUtils.isEmpty(getSecondSurname())) ? "" : " " + getSecondSurname()) + ", " + getName();
    }
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Person o = (Person) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.birthDate, o.birthDate)
				.append(this.gender, o.gender)
				.append(this.maritalStatus, o.maritalStatus)
				.append(this.registry, o.registry)
				.append(this.socialSecurityNumber, o.socialSecurityNumber)
				.append(this.name, o.name)
				.append(this.firstSurname, o.firstSurname)
				.append(this.secondSurname, o.secondSurname)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(birthDate)
			.append(gender)
			.append(id)
			.append(maritalStatus)
			.append(registry)
			.append(socialSecurityNumber)
			.append(name)
			.append(firstSurname)
			.append(secondSurname)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}