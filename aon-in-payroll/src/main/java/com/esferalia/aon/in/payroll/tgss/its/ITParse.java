package com.esferalia.aon.in.payroll.tgss.its;

import static com.esferalia.aon.occam.api.model.type.ContractLeaveDetailStatus.PROCESSED;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType.ALTA;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType.BAJA;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType.CONFIRMACION;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveType.MATERNIDAD;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveType.PATERNIDAD;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailStatus;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDischargeCause;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;

import solutions.aon.seg.social.object.ITPart;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.object.PaternityCertificate;

public class ITParse {

	private ITParse() {
		throw new IllegalStateException("Utility class");
	}

	public static EmployeeIT parseTGSSToAon(It it) {
		ITPart startIT = it.getStart();
		ITPart endIT = null != it.getEnd() ? it.getEnd() : new ITPart();
		
		Date startDateIT = startIT.getWorkLeaveDate().get();
		Optional<Date> endDateIT = endIT.getWorkRestartDate();

		EmployeeIT employeeIT = new EmployeeIT()
				.setCcc(startIT.getCcc())
				.setType(ContractLeaveType.valueOfTGSS(startIT.getCauseNumber()))
				.setStartDate(startDateIT);
		
		startIT.getIpf().ifPresent(employeeIT::setDni);
		startIT.getNaf().ifPresent(employeeIT::setNss);
		startIT.getNameEmployee().ifPresent(employeeIT::setName);
		startIT.getDailyBaseCgc().ifPresent(d-> employeeIT.setDailyCgcBase(d.doubleValue()));

		ContractLeaveDetailStatus status = PROCESSED;

		endDateIT.ifPresent(employeeIT::setEndDate);

		{ // -------------ADD ALTA, BAJA
			// ---------ADD BAJA
			employeeIT.addITPart(
				buildPartIt(BAJA, status, employeeIT.getStartDate(), startIT.getCollegiateNumber(), startIT.getCias())
			);
			// --------ADD ALTA
			Optional<Date> endD = employeeIT.getEndDate();
			if (!endD.isEmpty())
				employeeIT.addITPart(
					buildPartIt(ALTA, status, endD.get(), endIT.getCollegiateNumber(), endIT.getCias())
				);
		}

		if (!endIT.getCauseRestart().isEmpty())
			employeeIT.setDischargeCause(
					ContractLeaveDischargeCause.safeValueOf(endIT.getCauseNumber() - 1));

		it.getConfirmations().forEach(c -> {
			EmployeeITPart itPart = new EmployeeITPart()
			.setType(CONFIRMACION)
			.setStatus(status);

			c.getConfirmationDate().ifPresent(itPart::setDate);

			c.getCollegiateNumber().ifPresent(itPart::setCollegeNumber);

			c.getCias().ifPresent(itPart::setCias);

			c.getPartNum().ifPresent(part -> itPart.setConfirmOrder(part.byteValue()));

			employeeIT.addITPart(itPart);
		});

		return employeeIT;

	}
	
	public static EmployeeIT parsePaternityTGSSToAon(PaternityCertificate paternity) {
		
		ContractLeaveDetailStatus status = ContractLeaveDetailStatus.PROCESSED;
		
		EmployeeIT employeeIT = new EmployeeIT()
		.setCcc(paternity.getCcc())
		.setType(paternity.getIsFather() ? PATERNIDAD : MATERNIDAD)
		.setStartDate(paternity.getStartDate());

		paternity.getWorkerNif().ifPresent(employeeIT::setDni);
		paternity.getWorkerNaf().ifPresent(employeeIT::setNss);
		paternity.getWorkerName().ifPresent(employeeIT::setName);
		paternity.getEndDate().ifPresent(employeeIT::setEndDate);
		
		{ // ADD PART
			// ---------ADD BAJA
			employeeIT.addITPart(
					buildPartIt(BAJA, status, employeeIT.getStartDate(), Optional.empty(), Optional.empty())
			);
			// --------ADD ALTA
			Optional<Date> endDate = employeeIT.getEndDate();
			if (!endDate.isEmpty()) {
				employeeIT.addITPart(
						buildPartIt(ALTA, status, endDate.get(), Optional.empty(), Optional.empty())
				);
				employeeIT.setDischargeCause(ContractLeaveDischargeCause.AGOTAMIENTO_PLAZO);
			}
		}

		//-------------CONTRACT DATA-----------
		paternity.getWorkerApplicantType().ifPresent(type->
			employeeIT.setPaternityType(type.value()+"")
		);
		
		paternity.getReason().ifPresent(reason->
			employeeIT.setPaternityReason(reason.value()+"")
		);
		
		paternity.getWorkerPartialTimeCoef().ifPresent(coef->
			employeeIT.setPaternityParciality(coef.doubleValue())
		);		
		
//		employeeIT.setDirectPay(getExpressionDirectPay(paternity.getStartDate()));
//		
		if(!paternity.getPaternityDetail().isEmpty()) 
			employeeIT.setRegulationBase(paternity.getPaternityDetail().get(0).getBaseCC().doubleValue());

		//-------------END CONTRACT DATA-----------

		return employeeIT;
	}
	
	private static EmployeeITPart buildPartIt(ContractLeaveDetailType type, ContractLeaveDetailStatus status, Date date,
			Optional<String> collegiateNumber, Optional<String> cias) {
		EmployeeITPart itPart = new EmployeeITPart().setType(type).setStatus(status).setDate(date);
		collegiateNumber.ifPresent(itPart::setCollegeNumber);

		cias.ifPresent(itPart::setCias);

		return itPart;
	}
	
	private static String getExpressionDirectPay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return "FECHA(" + cal.get(Calendar.YEAR) + "," + (cal.get(Calendar.MONTH) + 1) + "," + cal.get(Calendar.DAY_OF_MONTH) + ")";
	}
}
