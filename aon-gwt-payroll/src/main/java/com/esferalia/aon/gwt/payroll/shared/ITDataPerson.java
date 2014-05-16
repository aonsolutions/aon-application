package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ITDataPerson implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1519604556575978934L;
	
	public static enum Type implements HasDescription{
		COMMON_DISEASE("Enfermedad Com\u00FAn"),
		OCCUPATIONAL_DISEASE("Enfermedad Profesional"),
		MATERNITY("Maternidad"),
		PATERNITY("Paternidad"),
		PREGNANCY_RISK("Riesgo Durante Embarazo"),
		BREASTFEEDING_RISK("Lactancia Materna"),
		NON_OCCUPATIONAL_DISEASE("Enfermedad No Profesional");
		
		private String description;
		
		private Type(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return this.description;
		};
	}
	
	public static enum DischargeCause implements HasDescription {
		
		CURATION("Curaci\u00F3n"),
		DEATH("Fallecimiento"),
		MEDICAL_INSPECTION("Inspecci\u00F3n m\u00E9dica"),
		DISABILITY("Propuesta incapacidad"),
		TIME_EXHAUSTION("Agotamiento de plazo"),
		IMPROVEMENT("Mejor\u00EDa para el trabajo habitual"),
		ENTERING("Incomparecencia"),
		CONTROL_INSS("Control INSS duraci\u00F3n 12 meses"),
		RECOVERY("Recuperaci\u00F3n capacidad profesional"),
		ENTERING_EDUCATION("Incomp. CTTOs formaci\u00F3n");
		
		private String discharge_cause;
		
		private DischargeCause(String cause) {
			this.discharge_cause = cause;
		}

		@Override
		public String getDescription() {
			// TODO Apéndice de método generado automáticamente
			return discharge_cause;
		};
		
	}

	private int contractId;
	private int contractLeaveId;
	private Type type;	
	private DischargeCause discharge;
	private Date leaveStartDate;
	private Date leaveEndDate;
	private int discharge_cause;
	
	public ITDataPerson() {		
		
	}	

	public int getContractId() {
		return contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	
	public int getContractLeaveId() {
		return contractLeaveId;
	}
	
	public void setContractLeaveId(int pContractLeaveId) {
		contractLeaveId = pContractLeaveId;
	}	

	public Date getLeaveStartDate() {
		return leaveStartDate;
	}

	public void setLeaveStartDate(Date start_date) {
		this.leaveStartDate = start_date;
	}

	public Date getLeaveEndDate() {
		return leaveEndDate;
	}

	public void setLeaveEndDate(Date end_date) {
		this.leaveEndDate = end_date;
	}	

	public int getDischarge_cause() {
		return discharge_cause;
	}

	public void setDischarge_cause(int discharge_cause) {
		this.discharge_cause = discharge_cause;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public DischargeCause getDischargeCause() {
		return discharge;
	}
	
}
