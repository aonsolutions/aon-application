package com.esferalia.aon.occam.api.model;

import static com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType.ALTA;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType.BAJA;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType.CONFIRMACION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDischargeCause;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;


public class EmployeeIT implements Serializable {
	
	private static final String INICIO_PAGO_DIRECTO = "INICIO_PAGO_DIRECTO";
	private static final String TIPO_SOLICITANTE_MAT_PAT = "TIPO_SOLICITANTE_MAT_PAT";
	private static final String MOTIVO_MAT_PAT = "MOTIVO_MAT_PAT";
	private static final String COEFICIENTE_MATERNIDAD = "COEFICIENTE_MATERNIDAD";
	private static final String COEFICIENTE_PATERNIDAD = "COEFICIENTE_PATERNIDAD";
//	private static final String BASE_REGULADORA = "BASE_REGULADORA";
	
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
	ContractType contractType;

	Double dailyCgcBase;	//base CC TGSS
	Double dailyCgpBase;	//base CP TGSS
	Integer quoteDays;	// day TGSS
	
	String regime;
	String ccc;
	String name;
	String nss;
	String dni;
	
	String job;
	String jobDescription;

	
	private List<EmployeeITPart> itParts;
	
	private Map<String, ContractData> contractDatas;
	
	
	public EmployeeIT() {
		this.itParts = new ArrayList<>();
		this.contractDatas = new HashMap<>();
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

	public EmployeeIT setDailyCgpBase(Double dailyCgpBase) {
		this.dailyCgpBase = dailyCgpBase;
		return this;
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
	
	public EmployeeIT setDni(String dni) {
		this.dni = dni;
		return this;
	}

	public Optional<String> getJob() {
		return Optional.ofNullable(job);
	}
	
	public EmployeeIT setJob(String job) {
		this.job = job;
		return this;
	}
	
	public Optional<String> getJobDescription() {
		return Optional.ofNullable(jobDescription);
	}
	
	public EmployeeIT setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
		return this;
	}

	public Optional<String> getDni() {
		return Optional.ofNullable(dni);
	}
	
	
	public EmployeeIT setName(String name) {
		this.name = name;
		return this;
	}

	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}

	public String getNss() {
		return nss;
	}
	
	public EmployeeIT setContractType(ContractType contractType) {
		this.contractType = contractType;
		return this;
	}

	public ContractType getContractType() {
		return contractType;
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
		return itParts.stream().filter(x->x.getType().equals(BAJA)).findFirst();
	}
	
	public Optional<EmployeeITPart> getItAlta(){
		return itParts.stream().filter(x->x.getType().equals(ALTA)).findFirst();
	}

	public List<EmployeeITPart> getItConfirmations(){
		return itParts.stream().filter(x->x.getType().equals(CONFIRMACION)).collect(Collectors.toList());
	}
	
	//  ---------------------ADD CONTRACT DATA
	
	public EmployeeIT addContractData(String name, String expression) {
		addContractData(name, expression, this.startDate, this.endDate);
		return this;
	}
	
	public Optional<String> getPaternityType() {
		return getContractDataValue(TIPO_SOLICITANTE_MAT_PAT);
	}
	public EmployeeIT setPaternityType(String expression) {
		addContractData(TIPO_SOLICITANTE_MAT_PAT, expression, this.startDate, this.endDate);
		return this;
	}
	
	public Optional<String> getPaternityReason() {
		return getContractDataValue(MOTIVO_MAT_PAT);
	}
	public EmployeeIT setPaternityReason(String expression) {
		addContractData(MOTIVO_MAT_PAT, expression, this.startDate, this.endDate);
		return this;
	}
	
	public Optional<String> getDirectPay() {
		return getContractDataValue(INICIO_PAGO_DIRECTO);
	}
	
	public EmployeeIT setDirectPay(String expression) {
		addContractData(INICIO_PAGO_DIRECTO, expression, this.startDate, this.endDate);
		return this;
	}
	
//	public Optional<String> getRegulationBase() {
//		return getContractDataValue(BASE_REGULADORA);
//	}
//	
//	public EmployeeIT setRegulationBase(Double baseReg) {
//		addContractData(BASE_REGULADORA, baseReg.toString(), this.startDate, this.endDate);
//		return this;
//	}
	
	public EmployeeIT setPaternityParciality(Double parciality) {
		String tmp = getType()!=null && getType().equals(ContractLeaveType.PATERNIDAD) ? COEFICIENTE_PATERNIDAD : COEFICIENTE_MATERNIDAD;
		addContractData(tmp, parciality.toString(), this.startDate, this.endDate);
		return this;
	}

	public List<ContractData> getContractDatas() {
		return new ArrayList<>(contractDatas.values());
	}
	
	private Optional<String> getContractDataValue(String name) {
		return contractDatas
				.entrySet()
				.stream()
				.filter( e-> e.getKey().equals(name))
				.map(Map.Entry::getValue)
				.map(ContractData::getExpression)
				.findFirst();
	}
	
	private EmployeeIT addContractData(String name, String expression, Date startDate, Date endDate) {
		contractDatas.put(name, 
			new ContractData()
			.setName(name)
			.setEndDate(endDate)
			.setStartDate(startDate)
			.setExpression(expression)
		);
		
		return this;
	}
	//  ---------------------ADD CONTRACT DATA

	public boolean isPaternity() {
		return type!=null && (type.equals(ContractLeaveType.MATERNIDAD) ||  type.equals(ContractLeaveType.PATERNIDAD));
	}
	
    @Override
    public String toString() {
        return "EmployeeIT{"
        		+ "id=" + id +","
        		+ "domain=" + domain +","
        		+ "type=" + type +","
        		+ "contract=" + contract +","
        		+ "nss=" + nss +","
        		+ "dni=" + dni +","
        		+ "regime=" + regime +","
        		+ "ccc=" + ccc +","
        		+ "description=" + description +","
        		+ "startDate=" + startDate +","
        		+ "endDate=" + endDate +","
        		+ "dailyCgcBase=" + dailyCgcBase +","
        		+ "dailyCgpBase=" + dailyCgpBase +","
        		+ "quoteDays=" + quoteDays +","
        		+ "parent=" + parent +","
        		+ "dischargeCause=" + dischargeCause +","
        		+ "contractType=" + contractType +","
        		+ "job=" + job +","
        		+ "jobDescription=" + jobDescription +","
        		+ "itParts=[" + itParts.toString() +"],"
        		+ "contractDatas=[" + getContractDatas()+"]"
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
	
	// CONTRACTS
	public enum ContractType {
		FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL, RESTO_Y_AUTONOMOS;
		
		public byte value() {
			return (byte) this.ordinal();
		}
		
		public static ContractType safeValueOf( Byte i ) {
			if (i == null) return null;
			return safeValueOf( i.intValue() ); 
		}
		public static ContractType safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= ContractType.values().length) return null;
			return ContractType.values()[i];
		}
	}
}
