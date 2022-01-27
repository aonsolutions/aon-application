package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType;
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
	Integer parent;
	ContractLeaveDischargeCause dischargeCause;
	List<EmployeeITPart> itParts;
	
	Double dailyCgpBase;	
	Double dailyRegBase;	
	
	Double dailyCgcBase;	//base TGSS
	Integer quoteDays;	// day TGSS
	
	String regime;
	String ccc;
	String nss;
	Byte contractType; // 0 parcial, 1 tiempo completo
	
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
	
	public EmployeeIT setQuoteDays(Integer quoteDays) {
		this.quoteDays = quoteDays;
		return this;
	}

	public Integer getQuoteDays() {
		return quoteDays;
	}
	
	public EmployeeIT setRegime(String regime) {
		this.regime = regime;
		return this;
	}

	public String getRegime() {
		return regime;
	}

	public EmployeeIT setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}

	public String getCcc() {
		return ccc;
	}
	
	public EmployeeIT setNss(String nss) {
		this.nss = nss;
		return this;
	}

	public String getNss() {
		return nss;
	}
	
	public EmployeeIT setContractType(Byte contractType) {
		this.contractType = contractType;
		return this;
	}

	public Byte getContractType() {
		return contractType;
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
	
	
	public Optional<EmployeeITPart> getItBaja(){
		return itParts.stream().filter(x->x.getType().equals(ContractLeaveDetailType.BAJA)).findFirst();
	}
	
	public Optional<EmployeeITPart> getItAlta(){
		return itParts.stream().filter(x->x.getType().equals(ContractLeaveDetailType.ALTA)).findFirst();
	}

	public List<EmployeeITPart> getItConfirmations(){
		return itParts.stream().filter(x->x.getType().equals(ContractLeaveDetailType.CONFIRMACION)).collect(Collectors.toList());
	}
	
	 @Override
    public String toString() {
        return "EmployeeIT{"
        		+ "id=" + id +","
        		+ "domain=" + domain +","
        		+ "type=" + type +","
        		+ "contract=" + contract +","
        		+ "nss=" + nss +","
        		+ "regime=" + regime +","
        		+ "ccc=" + ccc +","
        		+ "description=" + description +","
        		+ "startDate=" + startDate +","
        		+ "endDate=" + endDate +","
        		+ "dailyCgcBase=" + dailyCgcBase +","
        		+ "quoteDays=" + quoteDays +","
        		+ "dailyCgpBase=" + dailyCgpBase +","
        		+ "parent=" + parent +","
        		+ "dailyRegBase=" + dailyRegBase +","
        		+ "dischargeCause=" + dischargeCause +","
        		+ "contractType=" + contractType +","
        		+ "itParts=[" + itParts.toString() +"]"
        +  "}";
    }

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EmployeeIT ) )
			return false;
		
		EmployeeIT employeeIt = (EmployeeIT) obj;
		
		return Objects.equals(id, employeeIt.id);
	}
	

}
