package com.esferalia.aon.occam.test.payroll.employee;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;


public class CRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		String cccs [] = new String [5];
		Employee addEmployees [] = new Employee[255];
		int cccEmployeesLength = addEmployees.length / cccs.length;
		for ( int i = 0; i < cccs.length; i++ ) {
			cccs[i] = AonFaker.getCCC();
			for ( int j = 0; j < cccEmployeesLength; j++ ) {

				Date dates [] = {
					AonRandom.getPastDate(0),						
					AonRandom.getPastDate(0),						
					AonRandom.getPastDate(0)						
				};
				Arrays.sort(dates);
				
				addEmployees [i * cccEmployeesLength +j] = 
				AonFaker.getEmployee(
						ctx, 
						cccs[i], 
						dates[0],
						dates[1],
						dates[2],
						AonRandom.getFutureDate(50) );
			}
		}

		for ( int i = 0; i < addEmployees.length; i++ ) {
			printf(addEmployees[i]);
		}

		for ( int i = 0; i < addEmployees.length; i++ ) {
			PAYROLL.addEmployee(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), addEmployees[i]);
		}
		
		Employee getEmployees [] = new Employee[addEmployees.length];
		for ( int i = 0; i < getEmployees.length; i++ ) {
			Employee addEmployee = addEmployees[i];
			getEmployees [i] = PAYROLL.getEmployee(
								ctx.getDomainName(), 
								ctx.getDomainId(), 
								ctx.getUser(), 
								f -> f.getCCCProperty().eq(addEmployee.getCcc())
								.and(f.getNafProperty().eq(addEmployee.getNaf()))
								).orElseThrow();
		}
		
		//for ( int i = 0; i < getEmployees.length; i++ ) {
		//printf(getEmployees[i]);
		//}
		
		for ( int i = 0; i < getEmployees.length; i++ ) {
			Employee addEmployee = addEmployees[i];
			Employee getEmployee = getEmployees[i];
			assertEquals(addEmployee, getEmployee);
		}
		
		String nafs [] = Arrays.stream(addEmployees).map( e -> e.getNaf()).toArray(String[]::new);
		
		Map<String, Employee> getEmployeesMap = 
		PAYROLL.getEmployees(
				ctx.getDomainName(), 
				ctx.getDomainId(), 
				ctx.getUser(), 
				f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getNafProperty().in(nafs))
				)
		.collect(Collectors.toMap(e -> e.getNaf(), e -> e ));
		
		Assert.assertEquals(addEmployees.length, getEmployeesMap.size());
		
		for ( int i = 0; i < addEmployees.length; i++ ) {
			Employee addEmployee = addEmployees[i];
			Employee getEmployee = getEmployeesMap.get(addEmployee.getNaf());
			assertEquals(addEmployee, getEmployee);
		}
		
		
		getEmployeesMap = 
		PAYROLL.getEmployees(
				ctx.getDomainName(), 
				ctx.getDomainId(), 
				ctx.getUser(), 
				f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getCCCProperty().eq(cccs[0]))
				)
		.collect(Collectors.toMap(e -> e.getNaf(), e -> e ));
		
		
		Assert.assertEquals(cccEmployeesLength, getEmployeesMap.size());
		for (int i = 0; i < cccEmployeesLength; i++) {
			Employee addEmployee = addEmployees[i];
			Employee getEmployee = getEmployeesMap.get(addEmployee.getNaf());
			assertEquals(addEmployee, getEmployee);
		}
		
	}
	
	private void assertEquals(Employee addEmployee, Employee getEmployee) {
		Assert.assertEquals(addEmployee.getCcc(), getEmployee.getCcc());
		Assert.assertEquals(addEmployee.getNaf(), getEmployee.getNaf());
		Assert.assertEquals(addEmployee.getDni(), getEmployee.getDni());
		//Assert.assertEquals(addEmployee.getCif(), getEmployee.getCif());
		Assert.assertEquals(addEmployee.getStartDate(), getEmployee.getStartDate());
		Assert.assertEquals(addEmployee.getEndDate(), getEmployee.getEndDate());
		Assert.assertEquals(addEmployee.getName(), getEmployee.getName());
		
		Assert.assertEquals(addEmployee.getRegime(), getEmployee.getRegime());
		
		Assert.assertEquals(addEmployee.getFactor(), getEmployee.getFactor());
		Assert.assertEquals(addEmployee.getOccupation(), getEmployee.getOccupation());
		Assert.assertEquals(addEmployee.getContractType(), getEmployee.getContractType());
		Assert.assertEquals(addEmployee.getRlce(), getEmployee.getRlce());
		Assert.assertEquals(addEmployee.getQuoteGroup(), getEmployee.getQuoteGroup());

		Assert.assertEquals(addEmployee.getCategory(), getEmployee.getCategory());

		Assert.assertEquals(addEmployee.getSex(), getEmployee.getSex());
		//Assert.assertEquals(addEmployee.getPhone(), getEmployee.getPhone());
		Assert.assertEquals(addEmployee.getBirthDate(), getEmployee.getBirthDate());
	}

	private void printf(Employee employee) {
		
		System.out.printf("%s [%2$td/%2$tm/%2$ty-%3$td/%3$tm/%3$ty]: CCC: %4$s, NAF: %5$s , GC=%6$s, TC2=%7$s, PARCIALIDAD=%8$f \r\n",  
				employee.getName().orElse("-"), 
				employee.getStartDate(),
				employee.getEndDate().orElse(null),
				employee.getCcc(),
				employee.getNaf(),
				employee.getQuoteGroup().orElse(null),
				employee.getContractType().orElse(null),
				employee.getFactor().orElse(null)
		);
	}
}
