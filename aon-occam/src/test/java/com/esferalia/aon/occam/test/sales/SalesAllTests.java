package com.esferalia.aon.occam.test.sales;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.management.ShipmentPeriod;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalesAllTests extends AbstractOccamTest {

	@Test
	public void test() {
		crudeSalesDAO();
		hasIdTest();
		getProjectTest();
		getCustomerTest();
		getReferenceCodeTest();
		getShippingAddressTest();
		getSellerTest();
		getPayMethodTest();
		isConfidentialTest();
		setConfidentialTest();
		getWorkplaceTest();
		getScopeTest();
		getCarrierTest();
		getShippingPeriodValueTest();
		getDetailsTest();
		addDetailTest();
		isEmptyTest();
		equalsTest();
	}
	
	private void crudeSalesDAO() {
		Sales sales = AonFaker.getSales(ctx); 
		sales = SalesDAO.insert(ctx, sales);
		Integer salesId = sales.getId();
		Sales inserted = SalesDAO.get(ctx, f -> f.getIdProperty().eq(salesId));
		Asserts.assertEqualsSales(sales, inserted);
		
		sales = SalesDAO.update(ctx, sales);
//		Sales updated = SalesDAO.get(ctx, f -> f.getIdProperty().eq(salesId));
//		Asserts.assertEqualsSales(sales, updated);
		
//		SalesDAO.delete(ctx, f -> f.getIdProperty().eq(salesId));
//		Sales deleted = SalesDAO.get(ctx, f -> f.getIdProperty().eq(salesId));
//		assertNull(deleted.getId());
	}
	
	private void hasIdTest() {
		Sales sales = AonFaker.getSales(ctx);
		SalesDAO.save(ctx, sales);
		assertTrue(sales.hasId());
		
		sales.setId(null);
		assertFalse(sales.hasId());
	}
	
	private void getProjectTest() {
		Sales sales = AonFaker.getSales(ctx);
		
		Project project = AonFaker.getProject(ctx);
		sales.setProject(project);
		
		Asserts.assertEqualsProject(project, sales.getProject());
		
		sales.setProject(null);
		assertTrue(sales.getProject().isEmpty());
	}
	
	private void getCustomerTest() {
		Sales sales = AonFaker.getSales(ctx);
		
		Registry registry = AonFaker.getRegistry(ctx);
		Customer customer = AonFaker.getCustomer(ctx, registry);
		sales.setCustomer(customer);
		
		Asserts.assertEqualsCustomer(customer, sales.getCustomer());
		
		sales.setCustomer(null);
		assertTrue(sales.getCustomer().isEmpty());
	}
	
	private void getReferenceCodeTest() {
		Sales sales = AonFaker.getSales(ctx);
		String reference = "" + sales.getSeries() + "/";
		reference = reference + AonStringUtils.leftPad(Integer.toString(sales.getNumber()), 6, "0");
		
		assertEquals(reference, sales.getReferenceCode());
		
		String series = " ";
		sales.setSeries(series);
		reference = "" + AonStringUtils.leftPad(Integer.toString(sales.getNumber()), 6, "0");
		
		assertEquals(reference, sales.getReferenceCode());
	}
	
	private void getShippingAddressTest() {
		Sales sales = AonFaker.getSales(ctx);
		
		RegistryAddress address = AonFaker.getRegistryAddress(ctx);
		sales.setShippingAddress(address);
		
		Asserts.assertEqualsRegistryAddress(address, sales.getShippingAddress());
		
		sales.setShippingAddress(null);
		assertTrue(sales.getShippingAddress().isEmpty());
	}
	
	private void getSellerTest() {
		Sales sales = AonFaker.getSales(ctx);
		
		Seller seller = AonFaker.getSeller(ctx);
		sales.setSeller(seller);
		
		Asserts.assertEqualsSeller(seller, sales.getSeller());
		
		sales.setSeller(null);
		assertTrue(sales.getSeller().isEmpty());
	}
	
	private void getPayMethodTest() {
	    Sales sales = AonFaker.getSales(ctx);
	    
	    PayMethod payMethod = AonFaker.getPayMethod(ctx);
	    sales.setPayMethod(payMethod);
	    
	    Asserts.assertEqualsPayMethod(payMethod, sales.getPayMethod());

	    sales.setPayMethod(null);
	   	assertTrue(sales.getPayMethod().isEmpty());
	}
	
	private void isConfidentialTest() {
		Sales sales = AonFaker.getSales(ctx);
		
		sales.setSecurityLevel(SecurityLevel.CONFIDENTIAL);
		assertTrue(sales.isConfidential());
		
		sales.setSecurityLevel(SecurityLevel.OFFICIAL);
		assertFalse(sales.isConfidential());
	}
	
	private void setConfidentialTest() {
		Sales sales = AonFaker.getSales(ctx);
		
		sales.setConfidential(true);
		assertTrue(sales.isConfidential());
		
		sales.setConfidential(false);
		assertFalse(sales.isConfidential());
	}
	
	private void getWorkplaceTest() {
	    Sales sales = AonFaker.getSales(ctx);
	    
	    Workplace workplace = AonFaker.getWorkplace(ctx);
	    sales.setWorkplace(workplace);
	    
	    Asserts.assertEqualsWorkplace(workplace, sales.getWorkplace());

	    sales.setWorkplace(null);
	    Asserts.assertEqualsWorkplace(new Workplace(), sales.getWorkplace());
	}

	private void getScopeTest() {
	    Sales sales = AonFaker.getSales(ctx);
	    
	    Scope scope = AonFaker.getScope();
	    sales.setScope(scope);
	    
	    Asserts.assertEqualsScope(scope, sales.getScope());

	    sales.setScope(null);
	    assertTrue(sales.getScope().isEmpty());
	}
	
	private void getCarrierTest() {
	    Sales sales = AonFaker.getSales(ctx);
	    Carrier carrier = AonFaker.getCarrier(ctx);

	    sales.setCarrier(carrier);

	    Asserts.assertEqualsCarrier(carrier, sales.getCarrier());

	    sales.setCarrier(null);
	    assertTrue(sales.getCarrier().isEmpty());
	}

	private void getShippingPeriodValueTest() {
	    Sales sales = AonFaker.getSales(ctx);
	    
	    Random random = new Random();
	    ShipmentPeriod[] periods = ShipmentPeriod.values();
	    
	    ShipmentPeriod shipmentPeriod = periods[random.nextInt(periods.length)];
	    
	    sales.setShippingPeriod(shipmentPeriod);

	    assertEquals(shipmentPeriod.value(), sales.getShippingPeriodValue());

	    sales.setShippingPeriod(null);
	    assertNull(sales.getShippingPeriod());
	}
	
	private void getDetailsTest() {
	    Sales sales = AonFaker.getSales(ctx);
	    sales.setDetails(null);

	    assertEquals(new LinkedList<>(), sales.getDetails());
	}
	
	private void addDetailTest() {
	    Sales sales = AonFaker.getSales(ctx);
	    SalesDetail detail = AonFaker.getSalesDetail(ctx);
	    
	    sales.addDetail(detail);
	    List<SalesDetail> detailList = sales.getDetails();
	    assertTrue(detailList.contains(detail));
	}
	
	private void isEmptyTest() {
		Sales sales = AonFaker.getSales(ctx);
		assertFalse(sales.isEmpty());
		
		sales.setId(null);
		sales.setSeries(null);
		assertTrue(sales.isEmpty());
	}

	private void equalsTest() {
		Sales sales1 = AonFaker.getSales(ctx);
		Sales sales2 = AonFaker.getSales(ctx);
		
		sales1.setId(1);
		sales2.setId(2);
		
		assertNotEquals(sales1,sales2);
		
		sales1.setId(2);
		
		assertEquals(sales1,sales2);
	}

	
}
