package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Country;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.entity.master.ProjectReservationGuestDB;

@Entity
@Table(name="project_reservation_guest")
public class ProjectReservationGuest extends ProjectReservationGuestDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public ProjectReservationGuest() {
		setDocumentType(DocumentType.NIF);
		setDocumentCountry(Country.ES);
	}

	@Transient
	public String getFullName() {
    	String fullName = StringUtils.isEmpty(getTreatment()) ? "" : getTreatment() + " ";
    	fullName += StringUtils.isEmpty(getName()) ? "" : getName() + " ";
    	fullName += StringUtils.isEmpty(getSurname()) ? "" : getSurname() + " ";
    	fullName += StringUtils.isEmpty(getSurname2()) ? "" : getSurname2() + " ";
    	return fullName;
	}

    @Transient
	public String getFullAddress() {
    	String fullAddress = StringUtils.isEmpty(getAddress()) ? "" : getAddress() + " ";
    	fullAddress += StringUtils.isEmpty(getNumber()) ? "" : getNumber() + " ";
    	fullAddress += StringUtils.isEmpty(getAddress2()) ? "" : getAddress2() + " ";
    	fullAddress += StringUtils.isEmpty(getZip()) ? "" : getZip() + " - ";
    	fullAddress += StringUtils.isEmpty(getCity()) ? "" : getCity() + " ";
    	fullAddress += StringUtils.isEmpty(getProvince()) ? "" : "(" + getProvince() + ") ";
    	fullAddress += StringUtils.isEmpty(getCountry()) ? "" : getCountry();
    	return fullAddress;
	}
	
	@Transient
	public boolean isValidDocument() {
		RegistryDocument registryDocument = new RegistryDocument();
		registryDocument.setDocument(getDocument());
		registryDocument.setType(getDocumentType());
		registryDocument.setCountry(getDocumentCountry());
		return registryDocument.isValid();
	}

	@Transient
	public boolean isDocumentValidable() {
		RegistryDocument registryDocument = new RegistryDocument();
		registryDocument.setDocument(getDocument());
		registryDocument.setType(getDocumentType());
		registryDocument.setCountry(getDocumentCountry());
		return registryDocument.isValidable();
	}

}