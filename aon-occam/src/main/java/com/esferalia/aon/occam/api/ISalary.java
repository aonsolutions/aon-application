package com.esferalia.aon.occam.api;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;

public interface ISalary {

	public void deleteSalaries(AONContext ctx, 
			SalaryFilter filter);
	
	public Stream<Salary> getSalaries(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier);

	public Stream<Salary> getSalaryData(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier);

	public Stream<Salary> getContractData(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier);

	public Collection<Salary> saveSalaries(AONContext ctx, 
			Integer domainId, Collection<Salary> salaries);
	
	public Collection<FiscalModel> getFiscalModels(AONContext ctx, 
			Salary salary);
}
