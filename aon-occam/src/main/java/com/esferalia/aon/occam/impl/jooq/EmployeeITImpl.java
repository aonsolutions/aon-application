package com.esferalia.aon.occam.impl.jooq;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IEmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.Filter.ContractLeaveFilter;
import com.esferalia.aon.occam.impl.jooq.dao.EmployeeITDAO;

public class EmployeeITImpl implements IEmployeeIT {

	@Override
	public Optional<EmployeeIT> getEmployeeIT(AONContext ctx, ContractLeaveFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> EmployeeITDAO.get(ctx, filter));
	}

	@Override
	public Stream<EmployeeIT> getEmployeesIT(AONContext ctx, ContractLeaveFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> EmployeeITDAO.getStream(ctx, filter));
	}

	@Override
	public EmployeeIT[] setEmployeeIT(AONContext ctx, EmployeeIT... employeeITs) {
		return ctx.getDslContext().transactionResult(configuration -> EmployeeITDAO.setEmployeeIT(ctx, employeeITs));
	}
	
	@Override
	public void removeEmployeeIT(AONContext ctx, Integer contractLeaveId, Integer ...partIds) {
		ctx.getDslContext().transaction(configuration -> EmployeeITDAO.removeEmployeeIT(ctx, contractLeaveId, partIds));
	}

}
