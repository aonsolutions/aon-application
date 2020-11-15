package com.esferalia.aon.occam.api.model.payroll;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

public class Employee implements Serializable{
	
	private String naf;
	private String dni;
	private String name;
	private Date birthDate;
	private String phone;
	private String sex; 
	
	private String ccc;
	
	private String group;
	private String category;
	private String regime;
	private Date startDate;
	private Date endDate;
	private Double factor;
//	private String status;
//	private String statusDescription;
	private String type = "000";
	
	private Date insertDate;
	private Date deleteDate;
	
	
	private Integer employeeId;
	private Integer workplaceId;
	
	
	public String getNaf() {
		return naf;
	}
	
	public Employee setNaf(String naf) {
		this.naf = naf;
		return this;
	}
	
	public String getDni() {
		return dni;
	}
	
	public Employee setDni(String dni) {
		this.dni = dni;
		return this;
	}
	
	public String getCcc() {
		return ccc;
	}
	
	public Employee setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}
	
	public String getRegime() {
		return regime;
	}
	
	public Employee setRegime(String regime) {
		this.regime = regime;
		return this;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Employee setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public String getContractType() {
		return type;
	}
	
	public Employee setContractType(String contractType) {
		this.type = contractType;
		return this;
	}

	public String getQuoteGroup() {
		return group;
	}
	
	public Employee setQuoteGroup(String group) {
		this.group = group;
		return this;
	}
	
	public Integer getEmployeeId() {
		return employeeId;
	}
	
	public Employee setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
		return this;
	}
	
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	
	public Employee setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
		return this;
	}

	// ------------------------------------------------------------------------
	
	public Optional<String> getSex() {
		return Optional.ofNullable(sex);
	}
	
	public Employee setSex(String sex) {
		this.sex = sex;
		return this;
	}
	
	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}
	
	public Employee setName(String name) {
		this.name = name;
		return this;
	}
	
	public Optional<String> getPhone() {
		return Optional.ofNullable(phone);
	}
	
	public Employee setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public Optional<Date> getBirthDate() {
		return Optional.ofNullable(birthDate);
	}
	
	public Employee setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
		return this;
	}
	
	public Optional<Double> getFactor() {
		return Optional.ofNullable(factor);
	}
	
	public Employee setFactor(double factor) {
		this.factor = factor;
		return this;
	}

	public Optional<Date> getEndDate() {
		return Optional.ofNullable(endDate);
	}
	
	public Employee setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Optional<Date> getInsertDate() {
		return Optional.ofNullable(insertDate);
	}
	
	public Employee setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
		return this;
	}

	public Optional<Date> getDeleteDate() {
		return Optional.ofNullable(deleteDate);
	}
	
	public Employee setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
		return this;
	}

	//	public Optional<String> getStatus() {
//		return Optional.ofNullable(status);
//	}
	
	public Optional<String> getCategory() {
		return Optional.ofNullable(category);
	}
	
	public Employee setCategory(String category) {
		this.category = category;
		return this;
	}

//	public Optional<String> getStatusDescription() {
//		return Optional.ofNullable(statusDescription);
//	}
	
	
	
	public Employee set(Date deleteDate) {
		this.deleteDate = deleteDate;
		return this;
	}
	
	
}
