package com.esferalia.aon.payroll.calculator.sql;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;

public class SQLSalaryProxy implements ISalaryProxy {

	private Integer domainId;
	private Integer contractId;
	
	public SQLSalaryProxy( Integer contractId, Integer domainId  ) {
		this.domainId = domainId;
		this.contractId = contractId;
	}
	
	public Integer getDomainId() {
		return domainId;
	}
	
	public Integer getContractId() {
		return contractId;
	}
	
	@Override
	public ISalary getSalary() throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}


}
