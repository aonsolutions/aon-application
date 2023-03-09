package com.esferalia.aon.occam.api.model.payroll;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.HasEndDate;
import com.esferalia.aon.occam.api.model.HasStartDate;

@SuppressWarnings("serial")
public class ContractData implements Serializable, HasStartDate, HasEndDate{

	Integer id;
	Integer domain;
	String name;
	Integer contract;
	String expression;
	Date startDate;
	Date endDate;
	
	Boolean modify = false;
		
	public ContractData() {
	
	}

	public Integer getId() {
		return id;
	}

	public ContractData setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public ContractData setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getName() {
		return name;
	}

	public ContractData setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getContract() {
		return contract;
	}

	public ContractData setContract(Integer contract) {
		this.contract = contract;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public ContractData setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public ContractData setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public ContractData setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
   public Boolean getModify() {
		return modify;
	}

	public ContractData setModify(Boolean modify) {
		this.modify = modify;
		return this;
	}

@Override
    public String toString() {
        return "ContractData{"
        		+ "id=" + id +","
        		+ "domain=" + domain +","
        		+ "name=" + name +","
        		+ "contract=" + contract +","
        		+ "expression=" + expression +","
        		+ "startDate=" + startDate +","
        		+ "endDate=" + endDate+ "}";
    }
}
