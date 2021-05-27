package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.occam.api.model.type.MaritalStatus;

@SuppressWarnings("serial")
public class Person extends Registry implements Serializable {
	
    private Date birthDate;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private String socialSecurityNum;
    private String firstName;
    private String firstSurname;
    private String secondSurname;

	public Date getBirthDate() {
		return birthDate;
	}

	public Person setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public Gender getGender() {
		return gender;
	}

	public Person setGender(Gender gender) {
		this.gender = gender;
		return this;
	}

	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	public Person setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
		return this;
	}

	public String getSocialSecurityNum() {
		return socialSecurityNum;
	}

	public Person setSocialSecurityNum(String socialSecurityNum) {
		this.socialSecurityNum = socialSecurityNum;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public Person setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getFirstSurname() {
		return firstSurname;
	}

	public Person setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
		return this;
	}

	public String getSecondSurname() {
		return secondSurname;
	}

	public Person setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
		return this;
	}
	
}
