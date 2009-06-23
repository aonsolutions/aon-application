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

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ITransferObject;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.Registry;

/**
 * Transfer Object that represents a person.
 * 
 * @author Consulting & Development. Aimar Tellitu - 6-jun-2005
 * @since 1.0
 */
@Entity
@Table(name="person")
public class Person implements ITransferObject {

	private static final long serialVersionUID = -8638619556227571794L;

	/** The id. */
	private Integer id;
	
	/** The registry. */
	private Registry registry;
	
	/** The birth date. */
	private Date birthDate;

	/** The gender. */
	private Gender gender;

	/** The marital status. */
	private MaritalStatus maritalStatus;


	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
    @Id
	@Column(name="registry")
	@GeneratedValue(generator="registry_id")
	@GenericGenerator(name="registry_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="registry")})
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the registry.
	 * 
	 * @return the registry
	 */
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry the registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	/**
	 * Gets the birth date.
	 * 
	 * @return the birth date
	 */
	@Column(name="birth_date")
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * Sets the birth date.
	 * 
	 * @param birthDate the birth date
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * Gets the gender.
	 * 
	 * @return the gender
	 */
	@Column(nullable=false)
	public Gender getGender() {
		return gender;
	}

	/**
	 * Sets the gender.
	 * 
	 * @param gender the gender
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * Gets the marital status.
	 * 
	 * @return the marital status
	 */
	@Column(name="marital_status", nullable=false)
	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	/**
	 * Sets the marital status.
	 * 
	 * @param maritalStatus the marital status
	 */
	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Person) {
			Person o = (Person) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }

}