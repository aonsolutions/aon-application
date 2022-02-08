package com.esferalia.aon.omega;

import java.util.Date;

public class Person {

	Integer personId;		// Person Id (se rellena en el proceso)
	
	String document;		// Documento identificacion
	Byte documentType;		// Tipo documento (DNI = 0, CIF = 1, Pasaport = 3)
	String nationality;		// Nacionalidad en iso2 ej.: España -> ES, Argentina -> AR ...
	String name;			// Nombre
	String surname; 		// Primer apellido
	String secondSurname;	// Segundo apellido
	Byte gender;			// Genero (Hombre = 0, Mujer = 1, Desconocido = 2)
	Byte civilStatus;		// Estado civil  (Soltero = 0, Casado = 1, Divorciado = 3, Viudo = 4, Desconocido = 5)
	Date birthDate;			// Fecha nacimiento
	String ssNum;			// Numero Seguridad Social
	
	String phone;			// Telefono
	String mobile;			// Movil
	String email;			// Email
	
	Byte payMethod;			// Forma pago (0 = EFECTIVO, 1 == GIRO,	4 == CHEQUE, 5 = TRANSFERENCIA)
	String account;			// Cuenta banco
	String bic;				// BIC cuenta bancarea
	
	Address address;		// Domicilio
	
	protected Person() {
		super();
	}

	public Integer getPersonId() {
		return personId;
	}

	public Person setPersonId(Integer personId) {
		this.personId = personId;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Person setDocument(String document) {
		this.document = document;
		return this;
	}

	public Byte getDocumentType() {
		return documentType;
	}

	public Person setDocumentType(Byte documentType) {
		this.documentType = documentType;
		return this;
	}

	public String getNationality() {
		return nationality;
	}

	public Person setNationality(String nationality) {
		this.nationality = nationality;
		return this;
	}

	public String getName() {
		return name;
	}

	public Person setName(String name) {
		this.name = name;
		return this;
	}

	public String getSurname() {
		return surname;
	}

	public Person setSurname(String surname) {
		this.surname = surname;
		return this;
	}

	public String getSecondSurname() {
		return secondSurname;
	}

	public Person setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
		return this;
	}

	public Byte getGender() {
		return gender;
	}

	public Person setGender(Byte gender) {
		this.gender = gender;
		return this;
	}
	
	public Byte getCivilStatus() {
		return civilStatus;
	}

	public Person setCivilStatus(Byte civilStatus) {
		this.civilStatus = civilStatus;
		return this;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public Person setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public String getSsNum() {
		return ssNum;
	}

	public Person setSsNum(String ssNum) {
		this.ssNum = ssNum;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public Person setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public Person setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public Person setEmail(String email) {
		this.email = email;
		return this;
	}

	public Byte getPayMethod() {
		return payMethod;
	}

	public Person setPayMethod(Byte payMethod) {
		this.payMethod = payMethod;
		return this;
	}

	public String getAccount() {
		return account;
	}

	public Person setAccount(String account) {
		this.account = account;
		return this;
	}

	public String getBic() {
		return bic;
	}

	public Person setBic(String bic) {
		this.bic = bic;
		return this;
	}

	public Address getAddress() {
		return address;
	}

	public Person setAddress(Address address) {
		this.address = address;
		return this;
	}

	public String getFullName() {
		return surname + " " + secondSurname + ", " + name;
	}
	
}
