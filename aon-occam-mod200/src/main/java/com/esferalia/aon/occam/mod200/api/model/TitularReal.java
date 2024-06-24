package com.esferalia.aon.occam.mod200.api.model;

import java.io.Serializable;
import java.util.Date;

// Identificación del titular real de la entidad
public class TitularReal implements Serializable  {
	
	private static final long serialVersionUID = 2566371506127574634L;
	
	private int documentType;          // Tipo documento identificativo (0-No es aplicable, 1-DNI, NIF o NIE, 2-TIN, 3-Pasaporte, 4-Otro). Se graba en el campo representative de la tabla                        
	private String document;           // NIF/código de identificación extranjero            
	private String name;               // Apellidos y nombre                                 
	private String documentCountry;    // País de expedición del documento de identificación. Se graba en el campo country de la tabla   
	private Date birthDate;            // Fecha de nacimiento. Se graba en el campo notary_date de la tabla                                
	private String residenceCountry;   // País de residencia. Se graba en el campo residence de la tabla                                  
	private String nationality;        // Nacionalidad. Se graba en el campo notary de la tabla

	public int getDocumentType() {
		return documentType;
	}
	public TitularReal setDocumentType(int documentType) {
		this.documentType = documentType;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public TitularReal setDocument(String document) {
		this.document = document;
		return this;
	}
	public String getName() {
		return name;
	}
	public TitularReal setName(String name) {
		this.name = name;
		return this;
	}
	public String getDocumentCountry() {
		return documentCountry;
	}
	public TitularReal setDocumentCountry(String documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public TitularReal setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
		return this;
	}
	public String getResidenceCountry() {
		return residenceCountry;
	}
	public TitularReal setResidenceCountry(String residenceCountry) {
		this.residenceCountry = residenceCountry;
		return this;
	}
	public String getNationality() {
		return nationality;
	}
	public TitularReal setNationality(String nationality) {
		this.nationality = nationality;
		return this;
	}
	
}
