package com.esferalia.aon.occam.api;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryFilter;

public interface ISalary {

	public Stream<Salary> getSalaries(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier);

	public Stream<Salary> getSalaryData(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier);

	public Stream<Salary> getContractData(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier);
}
