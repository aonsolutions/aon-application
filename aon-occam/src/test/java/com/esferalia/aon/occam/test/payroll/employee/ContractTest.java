package com.esferalia.aon.occam.test.payroll.employee;

import java.util.Date;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.ContractExtendedData;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ContractTest extends AbstractOccamTest{

	@Test
	public void test(){
		Stream<ContractExtendedData> contractsNull = PAYROLL.getContractExtendedDataStream(
				ctx.getDomainName(), 
				ctx.getDomainId(), 
				ctx.getUser(), 
				null, 1, 20);
		Assert.assertNotEquals(contractsNull, null);
		Stream<ContractExtendedData> contracts = PAYROLL.getContractExtendedDataStream(
				ctx.getDomainName(), 
				ctx.getDomainId(), 
				ctx.getUser(), 
				null, 1, 0);
		Assert.assertEquals(0, contracts.count());
		Stream<ContractExtendedData> contractsUsingFilters = PAYROLL.getContractExtendedDataStream(
				ctx.getDomainName(), 
				ctx.getDomainId(), 
				ctx.getUser(), 
				f -> f.getPersonFullNameProperty().like("%name%")
				.and(f.getEndDateProperty().le(AonDateUtils.toSql(new Date())))
				.and(f.getStartDateProperty().le(AonDateUtils.toSql(new Date())))
				.and(f.getWorkplaceProperty().eq(0)), 
				1, 
				20);
		Assert.assertNotEquals(contractsUsingFilters, null);
	}
	
}
