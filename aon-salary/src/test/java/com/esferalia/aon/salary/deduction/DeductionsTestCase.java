package com.esferalia.aon.salary.deduction;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;

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

		assertEquals(deductions.getCommonContingency().getAmount(), (double)DeductionType.COMMON_CONTINGENCY.ordinal());
		assertEquals(deductions.getCommonContingency().getName(), DeductionType.COMMON_CONTINGENCY.name());
		assertEquals(deductions.getJobTraining().getAmount(), (double)DeductionType.JOB_TRAINING.ordinal());
		assertEquals(deductions.getJobTraining().getName(), DeductionType.JOB_TRAINING.name());
		assertEquals(deductions.getIrpf().getAmount(), (double)DeductionType.IRPF.ordinal());
		assertEquals(deductions.getIrpf().getName(), DeductionType.IRPF.name());
		assertEquals(deductions.getAdvancePayment().getAmount(), (double)DeductionType.ADVANCE_PAYMENT.ordinal());
		assertEquals(deductions.getAdvancePayment().getName(), DeductionType.ADVANCE_PAYMENT.name());
		assertEquals(deductions.getOther().getAmount(), (double)DeductionType.OTHER.ordinal());
		assertEquals(deductions.getOther().getName(), DeductionType.OTHER.name());
		assertEquals(deductions.getUnemployment().getAmount(), (double)DeductionType.UNEMPLOYMENT.ordinal());

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

		assertEquals(deductions.getCommonContingency().getAmount(), (double)DeductionType.COMMON_CONTINGENCY.ordinal()*3);
		assertEquals(deductions.getCommonContingency().getName(), DeductionType.COMMON_CONTINGENCY.name());
		assertEquals(deductions.getCommonContingency().getExpression(), null);
		assertEquals(deductions.getJobTraining().getAmount(), (double)DeductionType.JOB_TRAINING.ordinal()*3);
		assertEquals(deductions.getJobTraining().getName(), DeductionType.JOB_TRAINING.name());
		assertEquals(deductions.getJobTraining().getExpression(), null);
		assertEquals(deductions.getIrpf().getAmount(), (double)DeductionType.IRPF.ordinal()*3);
		assertEquals(deductions.getIrpf().getName(), DeductionType.IRPF.name());
		assertEquals(deductions.getIrpf().getExpression(), null);
		assertEquals(deductions.getAdvancePayment().getAmount(), (double)DeductionType.ADVANCE_PAYMENT.ordinal()*3);
		assertEquals(deductions.getAdvancePayment().getName(), DeductionType.ADVANCE_PAYMENT.name());
		assertEquals(deductions.getAdvancePayment().getExpression(), null);
		assertEquals(deductions.getOther().getAmount(), (double)DeductionType.OTHER.ordinal()*3);
		assertEquals(deductions.getOther().getName(), DeductionType.OTHER.name());
		assertEquals(deductions.getOther().getExpression(), null);
		assertEquals(deductions.getUnemployment().getAmount(), (double)DeductionType.UNEMPLOYMENT.ordinal()*3);
		assertEquals(deductions.getUnemployment().getExpression(), null);

	}
}
