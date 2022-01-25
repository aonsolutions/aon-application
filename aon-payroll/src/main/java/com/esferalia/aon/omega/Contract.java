package com.esferalia.aon.omega;

import java.util.Date;
import java.util.List;

public class Contract {

	Integer contractId;		// Contract Id (se rellena en el proceso)
	
	Person person;			// Persona asociada al contrato
	String workplaceName;	// Nombre del centro del trabajo al que está asociado
	String ccc;				// Cuenta de cotizacion al que está asociado
	Date startDate;			// Fecha inicio contrato
	Date endDate;			// Fecha fin contrato
	Date seniorityDate;		// Fecha antiguedad contrato
	Byte ssRegime;			// Regimen Seguridad Social (Comun = 0, Reta = 3, Socios Corp = 1, Jubilacion activa = 2, Garantia Juvenil = 4)
	
	List<ContractData> contractDatas;
	List<ContractInfo> contractInfos;
	
	protected Contract() {
		super();
	}

	public Integer getContractId() {
		return contractId;
	}

	public Contract setContractId(Integer contractId) {
		this.contractId = contractId;
		return this;
	}

	public Person getPerson() {
		return person;
	}

	public Contract setPerson(Person person) {
		this.person = person;
		return this;
	}

	public String getWorkplaceName() {
		return workplaceName;
	}

	public Contract setWorkplaceName(String workplaceName) {
		this.workplaceName = workplaceName;
		return this;
	}

	public String getCcc() {
		return ccc;
	}

	public Contract setCcc(String ccc) {
		this.ccc = ccc;
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

	public Date getSeniorityDate() {
		return seniorityDate;
	}

	public Contract setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
		return this;
	}

	public Byte getSsRegime() {
		return ssRegime;
	}

	public Contract setSsRegime(Byte ssRegime) {
		this.ssRegime = ssRegime;
		return this;
	}

	public List<ContractData> getContractDatas() {
		return contractDatas;
	}

	public Contract setContractDatas(List<ContractData> contractDatas) {
		this.contractDatas = contractDatas;
		return this;
	}

	public List<ContractInfo> getContractInfos() {
		return contractInfos;
	}

	public Contract setContractInfos(List<ContractInfo> contractInfos) {
		this.contractInfos = contractInfos;
		return this;
	}
	
}
