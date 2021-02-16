package com.esferalia.aon.occam.impl.jooq;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ISalary;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.impl.jooq.dao.SalaryDAO;

public class SalaryImpl implements ISalary {
	
	@Override
	public Stream<Salary> getSalaries(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier) {
		return SalaryDAO.getSalaries(ctx, filter, supplier);
	}
	
	@Override
	public Stream<Salary> getSalaryData(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier) {
		return SalaryDAO.getSalaryData(ctx, filter, supplier);
	}

	@Override
	public Stream<Salary> getContractData(AONContext ctx,
			SalaryFilter filter, Supplier<Salary> supplier) {
		return SalaryDAO.getContractData(ctx, filter, supplier);
	}
}
