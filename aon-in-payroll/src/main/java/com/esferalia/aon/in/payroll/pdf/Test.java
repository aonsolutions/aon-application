package com.esferalia.aon.in.payroll.pdf;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Salary;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {
	public static void main(String[] args) {
		AONContext context = AONContext.getAONContext("sherpa.aonsolutions.net", "admin");
		Stream<Salary> salaries = AON.getSalaries(context, f -> f.getIdProperty().gt(0));
		System.out.println(salaries.map(s -> s.getEmployeeDocument()).collect(Collectors.joining("\n")));
	}

}
