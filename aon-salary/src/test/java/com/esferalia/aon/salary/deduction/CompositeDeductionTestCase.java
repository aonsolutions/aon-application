package com.esferalia.aon.salary.deduction;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import com.esferalia.aon.salary.enumeration.DeductionType;

public class CompositeDeductionTestCase {

	@Test
	public void testI() {
		
		IDeduction d = new DeductionImpl()
		.setName("NAME")
		.setAmount(100.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		CompositeDeduction composite = new CompositeDeduction(d);
		
		Assert.assertEquals(composite.getName(), "NAME");
		Assert.assertEquals(composite.getAmount(), 100.00);
		Assert.assertEquals(composite.getExpression(), "EXPRESSION");
		Assert.assertEquals(composite.getDescription(), "DESCRIPTION");
		Assert.assertEquals(composite.getType(), DeductionType.OTHER);
		
	}
	
	
	@Test
	public void testII() {
		
		IDeduction d1 = new DeductionImpl()
		.setName("NAME")
		.setAmount(100.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		IDeduction d2 = new DeductionImpl()
		.setName("NAME")
		.setAmount(100.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		CompositeDeduction composite = new CompositeDeduction(d1, d2);
		
		Assert.assertEquals(composite.getName(), "NAME");
		Assert.assertEquals(composite.getAmount(), 200.00);
		Assert.assertEquals(composite.getExpression(), "EXPRESSION");
		Assert.assertEquals(composite.getDescription(), "DESCRIPTION");
		Assert.assertEquals(composite.getType(), DeductionType.OTHER);
		
	}

	@Test
	public void testIII() {
		
		IDeduction d1 = new DeductionImpl()
		.setName("NAME")
		.setAmount(100.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		IDeduction d2 = new DeductionImpl()
		.setName("NAME")
		.setAmount(200.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		IDeduction d3 = new DeductionImpl()
		.setName("NAME")
		.setAmount(300.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		CompositeDeduction composite = new CompositeDeduction(d1, d2, d3);
		
		Assert.assertEquals(composite.getName(), "NAME");
		Assert.assertEquals(composite.getAmount(), 600.00);
		Assert.assertEquals(composite.getExpression(), "EXPRESSION");
		Assert.assertEquals(composite.getDescription(), "DESCRIPTION");
		Assert.assertEquals(composite.getType(), DeductionType.OTHER);
		
	}

	@Test
	public void testIV() {
		
		IDeduction d1 = new DeductionImpl()
		.setName("NAME")
		.setAmount(100.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		IDeduction d2 = new DeductionImpl()
		.setName("NAME")
		.setAmount(200.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		IDeduction d3 = new DeductionImpl()
		.setName("D3")
		.setAmount(300.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		CompositeDeduction composite = new CompositeDeduction(d1, d2, d3);
		
		Assert.assertEquals(composite.getName(), null);
		Assert.assertEquals(composite.getAmount(), 600.00);
		Assert.assertEquals(composite.getExpression(), "EXPRESSION");
		Assert.assertEquals(composite.getDescription(), "DESCRIPTION");
		Assert.assertEquals(composite.getType(), DeductionType.OTHER);
		
	}

	@Test
	public void testV() {
		
		IDeduction d1 = new DeductionImpl()
		.setName("NAME")
		.setAmount(100.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		IDeduction d2 = new DeductionImpl()
		.setName("NAME")
		.setAmount(200.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		IDeduction d3 = new DeductionImpl()
		.setName("D3")
		.setAmount(300.00)
		.setExpression("E3")
		.setDescription("D3")
		.setType(DeductionType.OTHER)
		;
		
		CompositeDeduction composite = new CompositeDeduction(d1, d2, d3);
		
		Assert.assertEquals(composite.getName(), null);
		Assert.assertEquals(composite.getAmount(), 600.00);
		Assert.assertEquals(composite.getExpression(), null);
		Assert.assertEquals(composite.getDescription(), null);
		Assert.assertEquals(composite.getType(), DeductionType.OTHER);
		
	}

	@Test
	public void testVI() {
		
		IDeduction d1 = new DeductionImpl()
		.setName("NAME")
		.setAmount(100.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		IDeduction d2 = new DeductionImpl()
		.setName("NAME")
		.setAmount(200.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		IDeduction d3 = new DeductionImpl()
		.setName("D3")
		.setAmount(300.00)
		.setExpression("E3")
		.setDescription("D3")
		.setType(DeductionType.COMMON_CONTINGENCY)
		;
		
		CompositeDeduction composite = new CompositeDeduction(d1, d2, d3);
		
		Assert.assertEquals(composite.getName(), null);
		Assert.assertEquals(composite.getAmount(), 600.00);
		Assert.assertEquals(composite.getExpression(), null);
		Assert.assertEquals(composite.getDescription(), null);
		Assert.assertEquals(composite.getType(), null);
		
	}
	
	@Test
	public void testNullsI() {
		
		IDeduction d1 = new DeductionImpl()
		.setName("NAME")
		.setAmount(100.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		;
		
		IDeduction d2 = new DeductionImpl()
		.setAmount(200.00)
		.setExpression("EXPRESSION")
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;

		IDeduction d3 = new DeductionImpl()
		.setName("NAME")
		.setAmount(300.00)
		.setDescription("DESCRIPTION")
		.setType(DeductionType.OTHER)
		;
		
		
		CompositeDeduction composite = new CompositeDeduction(d1, d2, d3);
		
		Assert.assertEquals(composite.getName(), null);
		Assert.assertEquals(composite.getAmount(), 600.00);
		Assert.assertEquals(composite.getExpression(), null);
		Assert.assertEquals(composite.getDescription(), "DESCRIPTION");
		Assert.assertEquals(composite.getType(), null);
		
	}
	
	
}
