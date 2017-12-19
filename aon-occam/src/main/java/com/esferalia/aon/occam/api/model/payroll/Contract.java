package com.esferalia.aon.occam.api.model.payroll;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.ContractModel;
import com.esferalia.aon.occam.api.model.type.ContractStatus;
import com.esferalia.aon.occam.api.model.type.SSRegimeType;

@SuppressWarnings("serial")
public class Contract implements Serializable{

	Integer id;
	Integer domain;
	Integer person;
	Integer workplace;
	Integer enterpriseCCC;
	Date startDate;
	Date endDate;
	Integer calendar;
	String description;
	ContractStatus sepeStatus;
	Integer registration;
	Date seniorityDate;
	Integer enterpriseActivity;
	SSRegimeType ssRegime;
	Integer agreementLevel;
	ContractModel model;
	String categoryDescription;
	ContractStatus ssStatus;
		
	public Contract() {
	
	}

	public Integer getId() {
		return id;
	}

	public Contract setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Contract setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getPerson() {
		return person;
	}

	public Contract setPerson(Integer person) {
		this.person = person;
		return this;
	}

	public Integer getWorkplace() {
		return workplace;
	}

	public Contract setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}

	public Integer getEnterpriseCCC() {
		return enterpriseCCC;
	}

	public Contract setEnterpriseCCC(Integer workplaceCCC) {
		this.enterpriseCCC = workplaceCCC;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Contract setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Contract setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Integer getCalendar() {
		return calendar;
	}

	public Contract setCalendar(Integer calendar) {
		this.calendar = calendar;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Contract setDescription(String description) {
		this.description = description;
		return this;
	}

	public ContractStatus getSepeStatus() {
		return sepeStatus;
	}

	public Contract setSepeStatus(ContractStatus sepeStatus) {
		this.sepeStatus = sepeStatus;
		return this;
	}

	public Integer getRegistration() {
		return registration;
	}

	public Contract setRegistration(Integer registration) {
		this.registration = registration;
		return this;
	}

	public Date getSeniorityDate() {
		return seniorityDate;
	}

	public Contract setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
		return this;
	}

	public Integer getEnterpriseActivity() {
		return enterpriseActivity;
	}

	public Contract setEnterpriseActivity(Integer enterpriseActivity) {
		this.enterpriseActivity = enterpriseActivity;
		return this;
	}

	public SSRegimeType getSsRegime() {
		return ssRegime;
	}

	public Contract setSsRegime(SSRegimeType ssRegime) {
		this.ssRegime = ssRegime;
		return this;
	}

	public Integer getAgreementLevel() {
		return agreementLevel;
	}

	public Contract setAgreementLevel(Integer agreementLevel) {
		this.agreementLevel = agreementLevel;
		return this;
	}

	public ContractModel getModel() {
		return model;
	}

	public Contract setModel(ContractModel model) {
		this.model = model;
		return this;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public Contract setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
		return this;
	}

	public ContractStatus getSsStatus() {
		return ssStatus;
	}

	public Contract setSsStatus(ContractStatus ssStatus) {
		this.ssStatus = ssStatus;
		return this;
	}

}
