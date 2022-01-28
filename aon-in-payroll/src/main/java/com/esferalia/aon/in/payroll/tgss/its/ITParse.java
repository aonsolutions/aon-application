package com.esferalia.aon.in.payroll.tgss.its;

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

public class ITParse {

	private ITParse() {
		throw new IllegalStateException("Utility class");
	}

	public static EmployeeIT parseTGSSToAon(It it) {
		ITPart startIT = it.getStart();
		ITPart endIT = null != it.getEnd() ? it.getEnd() : new ITPart();
		
		String nss = startIT.getNaf().get();

		Date startDateIT = startIT.getWorkLeaveDate().get();
		Optional<Date> endDateIT = endIT.getWorkRestartDate();

		EmployeeIT employeeIT = new EmployeeIT()
				
				.setCcc(startIT.getCcc())
				.setType(ContractLeaveType.valueOfTGSS(startIT.getCauseNumber()))
				.setStartDate(startDateIT);
		
		startIT.getIpf().ifPresent(d-> employeeIT.setDni(d));
		startIT.getNameEmployee().ifPresent(d-> employeeIT.setName(d));
		startIT.getDailyBaseCgc().ifPresent(d-> employeeIT.setDailyCgcBase(d.doubleValue()));

		ContractLeaveDetailStatus status = ContractLeaveDetailStatus.PROCESSED;

		endDateIT.ifPresent(employeeIT::setEndDate);

		{ // -------------ADD ALTA, BAJA
			// ---------ADD BAJA
			employeeIT.addITPart(buildPartIt(ContractLeaveDetailType.BAJA, status,
					employeeIT.getStartDate(), startIT.getCollegiateNumber(), startIT.getCias()));
			// --------ADD ALTA
			if (employeeIT.getEndDate().isPresent())
				employeeIT.addITPart(buildPartIt(ContractLeaveDetailType.ALTA, status,
						employeeIT.getEndDate().get(), endIT.getCollegiateNumber(), endIT.getCias()));
		}

		if (endIT.getCauseRestart().isPresent())
			employeeIT.setDischargeCause(
					ContractLeaveDischargeCause.safeValueOf(endIT.getCauseNumber() - 1));

		it.getConfirmations().forEach(c -> {
			EmployeeITPart itPart = new EmployeeITPart().setType(ContractLeaveDetailType.CONFIRMACION)
			.setStatus(status);

			c.getConfirmationDate().ifPresent(itPart::setDate);

			c.getCollegiateNumber().ifPresent(itPart::setCollegeNumber);

			c.getCias().ifPresent(itPart::setCias);

			c.getPartNum().ifPresent(part -> itPart.setConfirmOrder(part.byteValue()));

			employeeIT.addITPart(itPart);
		});

		return employeeIT;

	}

	private static EmployeeITPart buildPartIt(ContractLeaveDetailType type, ContractLeaveDetailStatus status, Date date,
			Optional<String> collegiateNumber, Optional<String> cias) {
		EmployeeITPart itPart = new EmployeeITPart().setType(type).setStatus(status).setDate(date);
		collegiateNumber.ifPresent(itPart::setCollegeNumber);

		cias.ifPresent(itPart::setCias);

		return itPart;
	}
	
}
