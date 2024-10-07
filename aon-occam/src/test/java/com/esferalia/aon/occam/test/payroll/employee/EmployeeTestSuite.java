package com.esferalia.aon.occam.test.payroll.employee;


import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.payroll.Employee.ExpressionData;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class EmployeeTestSuite extends AbstractOccamTest  {

    	@Test
	public void testSetData() {

		Date dates [] = {
			AonRandom.getPastDate(0),						
			AonRandom.getPastDate(0),						
			AonRandom.getPastDate(0)						
		};
		Arrays.sort(dates);
		String ccc = AonFaker.getCCC();
		
		Date startDate = AonRandom.getPastDate(0);
		Date endDate = AonRandom.getFutureDate(50) ;
		
		Employee addEmployee = 
		AonFaker.getEmployee(
			ctx, 
			ccc, 
			startDate,
			endDate);
		
		PAYROLL.addEmployee(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), addEmployee);
		
		Employee getEmployee =  PAYROLL.getEmployee(
			ctx.getDomainName(), 
			ctx.getDomainId(), 
			ctx.getUser(), 
			f -> f.getCCCProperty().eq(addEmployee.getCcc())
			.and(f.getNafProperty().eq(addEmployee.getNaf()))
			).orElseThrow();

		Asserts.assertEmployee(addEmployee, getEmployee);

		
		
		Asserts.assertEmployee(addEmployee, getEmployee);
		
		
		Map<String, Collection<ExpressionData>> addDatas = addEmployee.getDatas();
		Map<String, Collection<ExpressionData>> getDatas = getEmployee.getDatas();
		
		addDatas.forEach(( name, datas ) -> {
		    datas.forEach( data -> {
			    getDatas.get(name).forEach( getData-> {
				Assert.assertEquals(data.getEndDate(), getData.getEndDate());
				Assert.assertEquals(data.getStartDate(), getData.getStartDate());
				Assert.assertEquals(data.getExpression(), getData.getExpression());
			    });
		    });
		});
		
		ContractData tc2 = new ContractData()
			.setName("TC2")
			.setExpression("\"100\"")
			.setStartDate(startDate)
			.setEndDate(endDate)
			; 
		
		ContractData group = new ContractData()
			.setName("GRUPO_COTIZACION")
			.setExpression("\"01\"")
			.setStartDate(startDate)
			.setEndDate(endDate)
			; 
		
		PAYROLL.setData(
			ctx.getDomainName(), 
			ctx.getDomainId(), 
			ctx.getUser(), 
			addEmployee.getCcc(), 
			addEmployee.getNaf(),
			startDate, 
			endDate, 
			tc2,
			group);
		
		getEmployee =  PAYROLL.getEmployee(
			ctx.getDomainName(), 
			ctx.getDomainId(), 
			ctx.getUser(), 
			f -> f.getCCCProperty().eq(addEmployee.getCcc())
			.and(f.getNafProperty().eq(addEmployee.getNaf()))
			).orElseThrow();
		
		Map<String, Collection<ExpressionData>> newDatas = getEmployee.getDatas();

		addDatas.forEach(( name, datas ) -> {
		    datas.forEach( data -> {
			newDatas.get(name).forEach( newData-> {
				Assert.assertEquals(data.getEndDate(), newData.getEndDate());
				Assert.assertEquals(data.getStartDate(), newData.getStartDate());
				if ( name.equals("TC2")) {
				    Assert.assertEquals(tc2.getExpression(), newData.getExpression());
				} else if (name.equals("GRUPO_COTIZACION")) {
				    Assert.assertEquals(group.getExpression(), newData.getExpression());
				} else {
				    Assert.assertEquals(data.getExpression(), newData.getExpression());
				}
			    });
		    });
		});

	}
	
}
