package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.common.shared.HasDescription;

public class ITDataPerson implements Serializable, Comparable {


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
		NON_OCCUPATIONAL_DISEASE("Enfermedad No Profesional"),
		COMMON_DISEASE_AT_LACK("Enfermedad Com\u00FAn Periodo de Carencia"),
		COMMON_OCCUPATIONAL_DISEASE("Enfermedad Com\u00FAn, Prestaci\u00F3n Profesional (COVID-19)"),
		OCCUPATIONAL_DISEASE_OBSERVATION("Periodo de Observaci\u00f3n por Enfermedad Profesional"),
		MENSTRUATION("Menstruaci\u00F3n Incapacitante Secundaria"),
		PREGNANCY_STOP("Interrupci\u00F3n del Embarazo"),
		PREGNANCY_39_WEEK("Semana Trig\u00E9sima Novena de Gestaci\u00F3n"),
		;
		
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
			return discharge_cause;
		};
		
	}

	private int contractId;
	private int contractLeaveId;
	private Type type;
	private int numType;
	private DischargeCause discharge;
	private String leaveStartDate;
	private String leaveEndDate;
	private int discharge_cause;
	
	private String regBase;
	
	public ITDataPerson() {
		setRegBase(null);
	}	

	public int getContractId() {
		return this.contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	
	public int getContractLeaveId() {
		return this.contractLeaveId;
	}
	
	public void setContractLeaveId(int pContractLeaveId) {
		this.contractLeaveId = pContractLeaveId;
	}	

	public Date getLeaveStartDate() {
		return parse(this.leaveStartDate);
	}

	public void setLeaveStartDate(Date start_date) {
		this.leaveStartDate = format(start_date);
	}

	public Date getLeaveEndDate() {
		return parse(this.leaveEndDate);
	}

	public void setLeaveEndDate(Date end_date) {
		this.leaveEndDate = format(end_date);
	}	

	public int getDischarge_cause() {
		return this.discharge_cause;
	}

	public void setDischarge_cause(int discharge_cause) {
		this.discharge_cause = discharge_cause;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public void setNumType(int numType) {
		this.numType = numType;
	}
	
	public int getNumType() {
		return this.numType;
	}
	
	public DischargeCause getDischargeCause() {
		return this.discharge;
	}
	
	public void setRegBase(String regBase) {
		this.regBase = regBase;
	}
	
	public String getRegBase() {
		return this.regBase;
	}

	public int compareTo(Object o1) {		
		ITDataPerson data = (ITDataPerson) o1;
		return this.getLeaveStartDate().compareTo(data.getLeaveStartDate());
	}	
	
	@Override
	public boolean equals(Object obj) {
		return (obj != null) && (obj instanceof ITDataPerson)
				&& (contractLeaveId == ((ITDataPerson) obj).contractLeaveId);
	}
	
	
}
