package com.esferalia.aon.in.payroll.excel;

import java.util.Date;
import java.util.Map;

import com.code.aon.person.enumeration.Gender;
import com.esferalia.aon.payroll.enumeration.FamilySituation;

public interface IRemunerationRecordEntry {
	public String getName();
	public String getSocialSecurityNumber();
	public Gender getGender();
	public Date getBirthDate();
	public String getStudies();
	public FamilySituation getFamilySituation();
	public Integer getChildren();
	public Date getHireDate();
	public Date getContractEndDate();
	public Date getSeniorityDate();
	public Date getContractSituationStartDate();
	public Date getContractSituationEndDate();
	public Double getWorkdayPercent();
	public Double getReducedWorkdayPercent();
	public String getWorkdayReductionReason();
	public String getContractKey();
	public String getEnterpriseArea();
	public String getEnterpriseDepartment();
	public String getCategory();
	public Schedule getSchedule();
	public Boolean isByTurns();
	public String getEnterpriseScale();
	public String getProfessionalClass();
	public String getScale();
	public String getAgreement();
	public String getProfessionalCategory();
	public String getProfessionalGroup();
	public String getLevel();
	public Integer getQuoteGroup();
	
	public Map<String, Double> getPayments();
	
	
	
	public static enum Schedule {
		CONTINUO ("continuo"),
		PARTIDO ("partido");
		
		private String description;
		
		private Schedule (String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
		
		
	}
	
}
