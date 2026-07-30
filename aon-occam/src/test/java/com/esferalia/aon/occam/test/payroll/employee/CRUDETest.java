package com.esferalia.aon.occam.test.payroll.employee;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static com.esferalia.aon.occam.test.OccamAssertions.*;
import org.junit.Test;

import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
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
		
		for ( int i = 0; i < getEmployees.length; i++ ) {
			Employee addEmployee = addEmployees[i];
			Employee getEmployee = getEmployees[i];
			Asserts.assertEmployee(addEmployee, getEmployee);
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
		
		assertEquals(addEmployees.length, getEmployeesMap.size());
		
		for ( int i = 0; i < addEmployees.length; i++ ) {
			Employee addEmployee = addEmployees[i];
			Employee getEmployee = getEmployeesMap.get(addEmployee.getNaf());
			Asserts.assertEmployee(addEmployee, getEmployee);
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
		
		
		assertEquals(cccEmployeesLength, getEmployeesMap.size());
		for (int i = 0; i < cccEmployeesLength; i++) {
			Employee addEmployee = addEmployees[i];
			Employee getEmployee = getEmployeesMap.get(addEmployee.getNaf());
			Asserts.assertEmployee(addEmployee, getEmployee);
		}
		
	}
	
}
