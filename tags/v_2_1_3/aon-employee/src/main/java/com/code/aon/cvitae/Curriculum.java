package com.code.aon.cvitae;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ITransferObject;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.registry.Registry;

/**
 * Transfer Object que representa una entidad de tipo CV
 * 
 * @author Consulting & Development. Joseba Urkiri - 15-nov-2006
 *  
 * @since 1.0
 */
@Entity
@Table(name="curriculum")
public class Curriculum implements ITransferObject {
	
	/** Unique autoincrment identifier. */
	private Integer id;
	private Registry registry;
	
	/** Date of Entry */
	private Date entryDate;

	/** Place of Birth. */
	private String birthPlace;

	/** Birth date. */
	private Date birthDate;

	/** Gender. */
	private Gender gender;

	/** Place of Residence */
	private String residencePlace;
	
	/** Province or County. */
	private GeoZone geoZone;
	
	/** City. */
	private String city;
	
	/** Postal code. */
	private String zip;
	
	/** Address. */
	private String address;
	
	/** Phone number. */
	private String phone;
	
	/** Driver License. */
	private String driverLicenses;

	/** Driver license date. */
	private Date driverLiceseDate;
	
	/** Indicates the rol of the curriculum owner inside the company. Employee, Applicant, ... */
	private boolean postcategory;

	private Set<Studies> studies;
	
	private Set<Knowledge> knowledges;
	
	private Set<WorkExperience> workExperiences;
	
	private Set<Language> languages;

	@Id
	@Column(name="registry", nullable=false)
	@GeneratedValue(generator="registry_id")
	@GenericGenerator(name="registry_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="registry")})
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return Returns the registry.
	 */
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * @param registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	/**
	 * @return the birthPlace
	 */
	@Column(length=3)
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * @param birthPlace the birthPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * Method getGender
	 * 
	 * @return Gender
	 * 
     */
	@Column(nullable=false)
	public Gender getGender() {
		return gender;
	}

	/**
	 * Method setGender
	 * @param gender Gender
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@ManyToOne
    @JoinColumn(name="geozone", nullable = false)
	public GeoZone getGeoZone() {
		return geoZone;
	}

	public void setGeoZone(GeoZone geoZone) {
		this.geoZone = geoZone;
	}

	/**
	 * @return the residencePlace
	 */
	@Column(length=3)
	public String getResidencePlace() {
		return residencePlace;
	}

	/**
	 * @param residencePlace the residencePlace to set
	 */
	public void setResidencePlace(String residencePlace) {
		this.residencePlace = residencePlace;
	}

	@Column(length=64)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(length=16)
	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Column(length=128)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(length=16)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name="driver_licenses")
	public String getDriverLicenses() {
		return driverLicenses;
	}

	public void setDriverLicenses(String driverLicenses) {
		this.driverLicenses = driverLicenses;
	}

	@Column(name="driver_license_date")
	public Date getDriverLiceseDate() {
		return driverLiceseDate;
	}

	/**
	 * @return the postcategory
	 */
	public boolean isPostcategory() {
		return postcategory;
	}

	/**
	 * @param postcategory the postcategory to set
	 */
	public void setPostcategory(boolean postcategory) {
		this.postcategory = postcategory;
	}

	public void setDriverLiceseDate(Date driverLiceseDate) {
		this.driverLiceseDate = driverLiceseDate;
	}

	@OneToMany(mappedBy = "curriculum", cascade={CascadeType.REMOVE})
	public Set<Studies> getStudies() {
		return studies;
	}

	public void setStudies(Set<Studies> studies) {
		this.studies = studies;
	}

	@OneToMany(mappedBy = "curriculum", cascade={CascadeType.REMOVE})
	public Set<Knowledge> getKnowledges() {
		return knowledges;
	}

	public void setKnowledges(Set<Knowledge> knowledges) {
		this.knowledges = knowledges;
	}

	@OneToMany(mappedBy = "curriculum", cascade={CascadeType.REMOVE})
	public Set<WorkExperience> getWorkExperiences() {
		return workExperiences;
	}

	public void setWorkExperiences(Set<WorkExperience> workExperiences) {
		this.workExperiences = workExperiences;
	}

	@OneToMany(mappedBy = "curriculum", cascade={CascadeType.REMOVE})
	public Set<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(Set<Language> languages) {
		this.languages = languages;
	}
}