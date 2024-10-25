package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class InvoiceBreakdownTest {
	
	@Test
	void testInvoiceBreakdown() {
		InvoiceBreakdown expected = AonMocker.mock(InvoiceBreakdown.class);
		InvoiceBreakdown actual = new InvoiceBreakdown()
			.setTaxType(expected.getTaxType())
			.setBase(expected.getBase())
			.setPercentage(expected.getPercentage())
			.setQuota(expected.getQuota())
			.setSurcharge(expected.getSurcharge())
			.setSurchargeQuota(expected.getSurchargeQuota())
			.setDeductibleQuota(expected.getDeductibleQuota())
			.setVatDeductionType(expected.getVatDeductionType())
			.setWithholdingType(expected.getWithholdingType())
			.setWithholdingAccount(expected.getWithholdingAccount().orElse(null))
			.setQuotaEdited(expected.isQuotaEdited())
			.setSurchargeQuotaEdited(expected.isSurchargeQuotaEdited())
			.setDeductibleQuotaEdited(expected.isDeductibleQuotaEdited())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
}
