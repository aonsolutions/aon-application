package com.esferalia.aon.salary.deduction;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import com.esferalia.aon.salary.enumeration.DeductionType;

public class DeductionsTestCase {

	@Test
	public void testI() {
		Deductions deductions = new Deductions();
		
		DeductionType types [] = DeductionType.values();
		
		for ( int i = 1; i <= types.length; i++) {
			
			DeductionType type = types[i % types.length];
			IDeduction d = new DeductionImpl()
			.setName(type.name())
			.setExpression(String.valueOf(i) )
			.setDescription(type.getName(new Locale("ES")))
			.setType(DeductionType.OTHER)
			.setAmount((double)type.ordinal())
			;
			System.out.println(i + "-. " + type.getName(new Locale("ES"))+ " = " + (double)type.ordinal() );
			deductions.put(type, d);
		}
		
		Assert.assertEquals(deductions.getCommonContingency().getAmount(), (double)DeductionType.COMMON_CONTINGENCY.ordinal());
		Assert.assertEquals(deductions.getCommonContingency().getName(), DeductionType.COMMON_CONTINGENCY.name());
		Assert.assertEquals(deductions.getJobTraining().getAmount(), (double)DeductionType.JOB_TRAINING.ordinal());
		Assert.assertEquals(deductions.getJobTraining().getName(), DeductionType.JOB_TRAINING.name());
		Assert.assertEquals(deductions.getIrpf().getAmount(), (double)DeductionType.IRPF.ordinal());
		Assert.assertEquals(deductions.getIrpf().getName(), DeductionType.IRPF.name());
		Assert.assertEquals(deductions.getAdvancePayment().getAmount(), (double)DeductionType.ADVANCE_PAYMENT.ordinal());
		Assert.assertEquals(deductions.getAdvancePayment().getName(), DeductionType.ADVANCE_PAYMENT.name());
		Assert.assertEquals(deductions.getOther().getAmount(), (double)DeductionType.OTHER.ordinal());
		Assert.assertEquals(deductions.getOther().getName(), DeductionType.OTHER.name());
		Assert.assertEquals(deductions.getUnemployment().getAmount(), (double)DeductionType.UNEMPLOYMENT.ordinal());
		
	}

	@Test
	public void testIII() {
		Deductions deductions = new Deductions();
		
		DeductionType types [] = DeductionType.values();
		
		for ( int i = 1; i <= types.length*3; i++) {
			
			DeductionType type = types[i % types.length];
			IDeduction d = new DeductionImpl()
			.setName(type.name())
			.setExpression(String.valueOf(i) )
			.setDescription(type.getName(new Locale("ES")))
			.setType(DeductionType.OTHER)
			.setAmount((double)type.ordinal())
			;
			System.out.println(i + "-. " + type.getName(new Locale("ES"))+ " = " + (double)type.ordinal() );
			deductions.put(type, d);
		}
		
		Assert.assertEquals(deductions.getCommonContingency().getAmount(), (double)DeductionType.COMMON_CONTINGENCY.ordinal()*3);
		Assert.assertEquals(deductions.getCommonContingency().getName(), DeductionType.COMMON_CONTINGENCY.name());
		Assert.assertEquals(deductions.getCommonContingency().getExpression(), null);
		Assert.assertEquals(deductions.getJobTraining().getAmount(), (double)DeductionType.JOB_TRAINING.ordinal()*3);
		Assert.assertEquals(deductions.getJobTraining().getName(), DeductionType.JOB_TRAINING.name());
		Assert.assertEquals(deductions.getJobTraining().getExpression(), null);
		Assert.assertEquals(deductions.getIrpf().getAmount(), (double)DeductionType.IRPF.ordinal()*3);
		Assert.assertEquals(deductions.getIrpf().getName(), DeductionType.IRPF.name());
		Assert.assertEquals(deductions.getIrpf().getExpression(), null);
		Assert.assertEquals(deductions.getAdvancePayment().getAmount(), (double)DeductionType.ADVANCE_PAYMENT.ordinal()*3);
		Assert.assertEquals(deductions.getAdvancePayment().getName(), DeductionType.ADVANCE_PAYMENT.name());
		Assert.assertEquals(deductions.getAdvancePayment().getExpression(), null);
		Assert.assertEquals(deductions.getOther().getAmount(), (double)DeductionType.OTHER.ordinal()*3);
		Assert.assertEquals(deductions.getOther().getName(), DeductionType.OTHER.name());
		Assert.assertEquals(deductions.getOther().getExpression(), null);
		Assert.assertEquals(deductions.getUnemployment().getAmount(), (double)DeductionType.UNEMPLOYMENT.ordinal()*3);
		Assert.assertEquals(deductions.getUnemployment().getExpression(), null);
		
	}
}
