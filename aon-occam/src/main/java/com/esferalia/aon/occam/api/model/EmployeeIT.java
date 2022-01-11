package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.ContractLeaveDischargeCause;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;


public class EmployeeIT implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Integer id;
	Integer domain;
	ContractLeaveType type;
	Integer contract;
	String description;
	Date startDate;
	Date endDate;
	Double dailyCgcBase;	
	Double dailyCgpBase;	
	Integer parent;
	Double dailyRegBase;	
	ContractLeaveDischargeCause dischargeCause;
	
	List<EmployeeITPart> itParts;
	
	public EmployeeIT() {
		this.itParts = new ArrayList<>();
	}
	
	
	public Integer getId() {
		return id;
	}

	public EmployeeIT setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}

	public EmployeeIT setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public ContractLeaveType getType() {
		return type;
	}

	public EmployeeIT setType(ContractLeaveType type) {
		this.type = type;
		return this;
	}

	public Integer getContract() {
		return contract;
	}

	public EmployeeIT setContract(Integer contract) {
		this.contract = contract;
		return this;
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}

	public EmployeeIT setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public EmployeeIT setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Optional<Date> getEndDate() {
		return Optional.ofNullable(endDate);
	}

	public EmployeeIT setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Optional<Double> getDailyCgcBase() {
		return Optional.ofNullable(dailyCgcBase);
	}

	public EmployeeIT setDailyCgcBase(Double dailyCgcBase) {
		this.dailyCgcBase = dailyCgcBase;
		return this;
	}

	public Optional<Double> getDailyCgpBase() {
		return Optional.ofNullable(dailyCgpBase);
	}
	
	public EmployeeIT setDailyRegBase(Double dailyRegBase) {
		this.dailyRegBase = dailyRegBase;
		return this;
	}

	public Optional<Double> getDailyRegBase() {
		return Optional.ofNullable(dailyRegBase);
	}

	public EmployeeIT setDailyCgpBase(Double dailyCgpBase) {
		this.dailyCgpBase = dailyCgpBase;
		return this;
	}

	public Optional<Integer> getParent() {
		return Optional.ofNullable(parent);
	}

	public EmployeeIT setParent(Integer parent) {
		this.parent = parent;
		return this;
	}

	public ContractLeaveDischargeCause getDischargeCause() {
		return dischargeCause;
	}

	public EmployeeIT setDischargeCause(ContractLeaveDischargeCause dischargeCause) {
		this.dischargeCause = dischargeCause;
		return this;
	}

	public List<EmployeeITPart> getITsParts() {
		return itParts;
	}

	public EmployeeIT setITParts(List<EmployeeITPart> itParts) {
		this.itParts = itParts;
		return this;
	}
	
	public void addITPart(EmployeeITPart itPart) {
		this.itParts.add(itPart);
	}
	
	  @Override
    public String toString() {
        return "EmployeeIT{"
        		+ "domain=" + domain +","
        		+ "type=" + type +","
        		+ "contract=" + contract +","
        		+ "description=" + description +","
        		+ "startDate=" + startDate +","
        		+ "endDate=" + endDate +","
        		+ "dailyCgcBase=" + dailyCgcBase +","
        		+ "dailyCgpBase=" + dailyCgpBase +","
        		+ "parent=" + parent +","
        		+ "dailyRegBase=" + dailyRegBase +","
        		+ "dischargeCause=" + dischargeCause +","
        		+ "itParts=[" + itParts.toString() +"]"
        +  "}";
    }

	
	
//	@Override
//	public int hashCode() {
//		return Objects.hashCode(id);
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		if (!(obj instanceof EmployeeIT ) )
//			return false;
//		
//		EmployeeIT employeeIt = (EmployeeIT) obj;
//		
//		return Objects.equals(id, employeeIt.id);
//	}
	

}
