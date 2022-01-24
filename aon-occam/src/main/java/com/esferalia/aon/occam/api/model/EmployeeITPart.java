package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailStatus;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType;

public class EmployeeITPart implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Integer id;

	ContractLeaveDetailType type;
	Integer contractLeave;
	String collegeNumber;
	Byte confirmOrder;
	String cias;
	Date date;
	ContractLeaveDetailStatus status;
	
	private Integer domain;

	public EmployeeITPart() {}

	public Integer getId() {
		return id;
	}

	public EmployeeITPart setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	
	public EmployeeITPart setCias(String cias) {
		this.cias = cias;
		return this;
	}

	public ContractLeaveDetailType getType() {
		return type;
	}

	public EmployeeITPart setType(ContractLeaveDetailType type) {
		this.type = type;
		return this;
	}

	public Integer getContractLeave() {
		return contractLeave;
	}

	public EmployeeITPart setContractLeave(Integer contractLeave) {
		this.contractLeave = contractLeave;
		return this;
	}

	public Optional<String> getCollegeNumber() {
		return Optional.ofNullable(collegeNumber);
	}
	
	public Optional<String> getCias() {
		return Optional.ofNullable(cias);
	}

	public EmployeeITPart setCollegeNumber(String collegeNumber) {
		
		if(collegeNumber!=null && collegeNumber.length()>8)
			collegeNumber = collegeNumber.substring(collegeNumber.length() - 8, collegeNumber.length());
		else if(collegeNumber!=null && collegeNumber.length()<=0) 
			collegeNumber = null;
		
		this.collegeNumber = collegeNumber;

		return this;
	}

	public Date getDate() {
		return date;
	}

	public EmployeeITPart setDate(Date date) {
		this.date = date;
		return this;
	}

	public ContractLeaveDetailStatus getStatus() {
		return status;
	}

	public EmployeeITPart setStatus(ContractLeaveDetailStatus status) {
		this.status = status;
		return this;
	}
	
	public Optional<Byte> getConfirmOrder() {
		return Optional.ofNullable(confirmOrder);
	}

	public EmployeeITPart setConfirmOrder(Byte confirmOrder) {
		this.confirmOrder = confirmOrder;
		return this;
	}
	
	@Override
    public String toString() {
        return "EmployeeITPart{"
        		+ "id=" + id +","
        		+ "type=" + type +","
        		+ "contractLeave=" + contractLeave +","
        		+ "collegeNumber=" + collegeNumber +","
        		+ "confirmOrder=" + confirmOrder +","
        		+ "cias=" + cias +","
        		+ "date=" + date +","
        		+ "status=" + status 
        		+  "}";
    }

}
