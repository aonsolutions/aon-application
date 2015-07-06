package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ISalary;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.SalaryProperties;
import com.esferalia.aon.occam.impl.jooq.dao.SalaryDAO;

public class SalaryImpl implements ISalary {
	
	@Override
	public SalaryAccountEntry getSalaryAccountEntry(AONContext ctx,
			Integer enterprise, Date from, Date to, String concept,
			Integer registryBank) {
		return SalaryDAO.getSalaryEntry(ctx, enterprise, from, to, concept, registryBank);
	}
	
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
}
