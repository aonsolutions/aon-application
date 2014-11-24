package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ISalary;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.impl.jooq.dao.SalaryDAO;

public class SalaryImpl implements ISalary {

	@Override
	public SalaryAccountEntry getSalaryAccountEntry(AONContext ctx,
			Integer enterprise, Date from, Date to, String concept,
			Integer registryBank) {
		return SalaryDAO.getSalaryEntry(ctx, enterprise, from, to, concept, registryBank);
	}
	

}
