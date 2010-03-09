package com.esferalia.aon.payroll.core.it;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.IEmployee;
import com.esferalia.aon.payroll.core.enumeration.ContingencyType;
import com.esferalia.aon.payroll.core.enumeration.Period;

public interface ITemporalDisability extends Serializable{

	IEmployee getEmployee();
	void getEmployee(IEmployee employee);
	
	Date getStartDate();
	void getStartDate(Date startDate);
	
	String getStartMedicalLicenseNumber();
	void setStartMedicalLicenseNumber(String startMedicalLicenseNumber);
	
	String getStartMedicalAreaId();
	void setStartMedicalAreaId(String startMedicalAreaId);
	
	boolean isStartProcessed();
	void setStartProcessed(boolean startProcessed);

	Date getStopDate();
	void setStopDate(Date stopDate);

	String getStopMedicalLicenseNumber();
	void setStopMedicalLicenseNumber(String stopMedicalLicenseNumber);
	
	String getStopMedicalAreaId();
	void setStopMedicalAreaId(String stopMedicalAreaId);

	boolean isStopProcessed();
	void setStopProcessed(boolean stopProcessed);

	boolean isProcessed();
	void setProcessed(boolean processed);
	
	ContingencyType getContingencyType();
	void setContingencyType(ContingencyType contingencyType);
	
	boolean isRelapase();
	void setRelapse(boolean relapse);
	
	ITemporalDisability getFirstTemporalDisability();
	void setFirstTemporalDisability(ITemporalDisability firstTemporalDisability);

	Period getProratePeriod();
	void setProratePeriod(Period period);
	
	Double getPrevPeriodBaseSalary();
	void setPrevPeriodBaseSalary(Double prevPeriodBaseSalary);
	
	Integer getPrevDaysCount();
	void setPrevDaysCount(Integer prevDaysCount);
	
	Double getDailyRegulatoryBase();
	void setDailyRegulatoryBase(Double dailyRegulatoryBase);
	
	Double getDailyCommonContingencyBase();
	void setDailyCommonContingencyBase(Double dailyCommonContingencyBase);
	
	Double getDailyAccidentBase();
	void setDailyAccidentBase(Double dailyAccidentBase);
	
	Double getDailyAssistance60();
	void setDailyAssistance60(Double dailyAssistance60);
	
	Double getDailyAssistance75();
	void setDailyAssistance75(Double dailyAssistance75);
	
}
