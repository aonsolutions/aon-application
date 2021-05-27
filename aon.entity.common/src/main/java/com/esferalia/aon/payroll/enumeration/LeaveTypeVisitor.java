package com.esferalia.aon.payroll.enumeration;

public interface  LeaveTypeVisitor<T> {

	T visitCommonDisease(LeaveType leaveType);

	T visitOcupationalDisease(LeaveType leaveType);

	T visitMaternity(LeaveType leaveType);

	T visitPaternity(LeaveType leaveType);

	T visitPregnacyRisk(LeaveType leaveType);

	T visitBreastFeedingRisk(LeaveType leaveType);

	T visitNonOcupationalDisease(LeaveType leaveType);

	T visitCommonDiseaseAtLack(LeaveType leaveType);
	
	T visitCommonProfessionalDisease(LeaveType leaveType);
}
