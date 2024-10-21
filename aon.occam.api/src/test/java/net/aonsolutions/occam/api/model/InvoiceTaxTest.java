package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class InvoiceTaxTest {
	
	@Test
	void testInvoiceTax() {
		InvoiceTax expected = AonMocker.mock(InvoiceTax.class);
		InvoiceTax actual = new InvoiceTax()
			.setDeleted(expected.isDeleted())
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setInvoiceDetail(expected.getInvoiceDetail())
			.setTaxType(expected.getTaxType())
			.setBase(expected.getBase())
			.setPercentage(expected.getPercentage())
			.setQuota(expected.getQuota())
			.setSurcharge(expected.getSurcharge())
			.setSurchargeQuota(expected.getSurchargeQuota())
			.setVatDeductionType(expected.getVatDeductionType())
			.setDeductiblePercent(expected.getDeductiblePercent())
			.setDeductibleQuota(expected.getDeductibleQuota())
			.setDirectTaxPercent(expected.getDirectTaxPercent())
			.setWithholdingType(expected.getWithholdingType())
			.setWithholdingAccount(expected.getWithholdingAccount().orElse(null))
			.setOutputAccount(expected.getOutputAccount().orElse(null))
			.setInputAccount(expected.getInputAccount().orElse(null))
			.setAdjAccount(expected.getAdjAccount().orElse(null))
			.setAdjDirectTaxAccount(expected.getAdjDirectTaxAccount().orElse(null))
			.setQuotaEdited(expected.isQuotaEdited())
			.setSurchargeQuotaEdited(expected.isSurchargeQuotaEdited())
			.setDeductibleQuotaEdited(expected.isDeductibleQuotaEdited())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
}
