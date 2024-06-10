package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;

final class EmployeeITListener implements IdcParserListener {
	
	
	private String nss;
	private String name;
	private String ccc;
	private String regime;
	private String document;
	
	
	private List<EmployeeIT> employeeITs = new ArrayList<>();
	
	public Collection<EmployeeIT> getEmployeeITs() {
//		return Collections.unmodifiableCollection(employeeITs);
		return employeeITs;

	}

	
	@Override
	public void onEmployee(String nss, String name) {
		this.nss = nss;
		this.name = name;
	}
	
	@Override
	public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
			String economicActivityDescription, String regime, String fullCCC) {
		this.ccc = ccc;
		this.regime = fullCCC.substring(0, 4);
	}
	
	@Override
	public void onEmployeeOtherInfo(String documentType, String document, String gender, Date birthDate) {
		this.document = document.trim();
	}
	
	
	@Override
	public void onEmployeeIT(String suspensionType, Date from, Date to) {
		EmployeeIT employeeIT  = new EmployeeIT();
		employeeIT.setNss(this.nss);
		employeeIT.setName(this.name);
		
		employeeIT.setCcc(this.ccc);
		employeeIT.setRegime(this.regime);
		
		employeeIT.setDni(this.document);
		
		if (isMaternityLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.MATERNIDAD);
		}else if (isWorkAccidentLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.ACCIDENTE_LABORAL);
		}else if (isPaternityLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.PATERNIDAD);
		}else if (isPregnancyRiskLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.RIESGO_EMBARAZO);
		}else if (isLactationRiskLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.RIESGO_LACTANCIA);
		}else if (isNotWorkAccidentLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.ACCIDENTE_NO_LABORAL);
		}else if (isCommunIllnessWaitingLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.ENFERMEDAD_COMUN_CARENCIA);
		}else if (isCommunIllnessProfessionalLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.ENFERMEDAD_COMUN_PRESTACION);
		}else if (isObservationPeriodLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.PERIODO_OBSERVACION_EP);
		}else if (isMenstruationLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.MENSTRUACION);
		}else if (isPregnancyInterruptionLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.INTERRUPCION_EMBARAZO);
		}else if (isWeek39PregnancyLeave(suspensionType)) {
			employeeIT.setType(ContractLeaveType.SEMANA_39_EMBARAZO);
		}else if (isITLeave(suspensionType)) {
			employeeIT.setType(null);
		}else {
			employeeIT.setType(ContractLeaveType.ENFERMEDAD_COMUN);
		}
		
		employeeIT.setStartDate(from);
		employeeIT.setEndDate(to);
		
		employeeITs.add(employeeIT);
	}
	
	private boolean isWorkAccidentLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^ACCIDENTE\\s*DE\\s*TRABAJO$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isMaternityLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^MATERNIDAD$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isPaternityLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^PATERNIDAD$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isPregnancyRiskLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^RIESGO\\s*DURANTE\\s*EMB$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isLactationRiskLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^RIESGO\\s*DURANTE\\s*LA\\s*LACTANCIA$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isNotWorkAccidentLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^ACCIDENTE\\s*NO\\s*LABORAL\\s*$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isCommunIllnessWaitingLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^ENFERMEDAD\\s*COM.N\\s*PERIODO\\s*DE\\s*CARENCIA$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isCommunIllnessProfessionalLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^ENFERMEDAD\\s*COM.N\\,\\s*PRESTACI.N\\s*PROFESIONAL\\s*\\(\\s*COVID\\-19\\)$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isObservationPeriodLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^PERIODO\\s*DE\\s*OBSERVACI.N\\s*POR\\s*ENFERMEDAD\\s*PROFESIONAL$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isMenstruationLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^MENSTRUACI.N\\s*INCAPACITANTE\\s*SECUNDARIA$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isPregnancyInterruptionLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^INTERRUPCI.N\\s*DEL\\s*EMBARAZO$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isWeek39PregnancyLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^SEMANA\\s*TRI.SIMA\\s*NOVENA\\s*DE\\s*GESTACI.N$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
	
	private boolean isITLeave(String suspensionType) {
		Pattern pattern = Pattern.compile("^INCAPACIDAD\\s*TEMPORAL$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(suspensionType);
	    return matcher.find();
	}
}