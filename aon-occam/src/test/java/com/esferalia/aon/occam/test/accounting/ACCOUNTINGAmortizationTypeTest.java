package com.esferalia.aon.occam.test.accounting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ACCOUNTINGAmortizationTypeTest extends AbstractOccamTest {

	/*
	 * Tests for ACCOUNTING class functions of AmortizationType
	 * */
	
	/*
	 * ACCOUNTING.getAmortizationTypeList
	 * */
	
	@Test
	public void accountingGetNullParamsAmortizationTypeList() {
		AmortizationTypeParams amp = null;
		String domainName = ctx.getDomainName();
		int domain = ctx.getDomainId();
		String user = ctx.getUser();
		AonCoreException e = assertThrows(AonCoreException.class, () -> ACCOUNTING.getAmortizationTypeList(domainName,domain,user, amp));
		assertEquals(AonError.AMORTIZATION_TYPE_PARAMS_NULL.getMessage(), e.getMessage());
	}
	
	/*
	 * ACCOUNTING.deleteAmortizationTypes
	 * */
	
	@Test
	public void accountingDeleteAmortizationTypesListNull() {
		String domainName = ctx.getDomainName();
		int domain = ctx.getDomainId();
		String user = ctx.getUser();	
			AonCoreException e = assertThrows(AonCoreException.class,
					() ->ACCOUNTING.deleteAmortizationTypes(domainName, domain, user, null));
			assertEquals(AonError.AMORTIZATION_TYPE_NULL.getMessage(), e.getMessage());
	}
	
	/*
	 * ACCOUNTING.saveAmortizationType
	 * */	

	@Test
	public void accountingSaveAmortizationTypeNull() {
		AmortizationType amortizationType = null;
		String domainName = ctx.getDomainName();
		int domain = ctx.getDomainId();
		String user = ctx.getUser();	
		AonCoreException e = assertThrows(AonCoreException.class, () -> ACCOUNTING.saveAmortizationType(domainName, domain, user, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL.getMessage(), e.getMessage());
	}
}
