package com.esferalia.aon.occam.api;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.Filter.ContractLeaveFilter;

public interface IEmployeeIT {
	
	public Optional<EmployeeIT> getEmployeeIT(AONContext ctx, ContractLeaveFilter filter);
	
	public Stream<EmployeeIT> getEmployeesIT(AONContext ctx, ContractLeaveFilter filter);
	
	public EmployeeIT[] setEmployeeIT(AONContext aonContext, EmployeeIT... employeeITs);

}
