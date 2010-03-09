package com.esferalia.aon.payroll.core;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.enumeration.ContractType;
import com.esferalia.aon.payroll.core.enumeration.ContributionAccount;

public interface IEmployee extends Serializable{

	ICompany getCompany();
	void setCompany(ICompany company);
	
	IPerson getPerson();
	void setPerson(IPerson person);
	
	Date getStartDate();
	void setStartDate();

	Date getStopDate();
	void setStopDate();
	
	ContributionAccount getContributionAccount();
	void setContributionAccount(ContributionAccount contributionAccount);
	
	boolean isOlder65();
	void setOlder65(boolean older65);
	
	// Indicador Tiempo de Contrato
	ContractType getContractType();
	void setContractType(ContractType contractType);
	
}
