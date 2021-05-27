package com.esferalia.aon.salary.payment;

import java.util.List;

import net.aonsolutions.payroll.report.Payment;
import net.aonsolutions.payroll.report.Values;

public class OvertimeHours extends Values<Payment>{

	public OvertimeHours(List<Payment> values) {
		super(values);
	}

}
