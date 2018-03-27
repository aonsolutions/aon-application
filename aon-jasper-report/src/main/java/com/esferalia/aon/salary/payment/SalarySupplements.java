package com.esferalia.aon.salary.payment;

import java.util.List;

import net.aonsolutions.payroll.report.Payment;
import net.aonsolutions.payroll.report.Values;

public class SalarySupplements extends Values<Payment>{

	public SalarySupplements(List<Payment> values) {
		super(values);
	}

}
