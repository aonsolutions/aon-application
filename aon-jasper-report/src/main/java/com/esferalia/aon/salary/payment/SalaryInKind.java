package com.esferalia.aon.salary.payment;

import java.util.List;

import net.aonsolutions.payroll.report.Payment;
import net.aonsolutions.payroll.report.Values;

public class SalaryInKind extends Values<Payment>{

	public SalaryInKind(List<Payment> values) {
		super(values);
	}

}
