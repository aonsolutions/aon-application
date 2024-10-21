package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class FinanceTest {
	
	@Test
	void testFinance() {
		Finance expected = AonMocker.mock(Finance.class);
		Finance actual = new Finance()
			.setSelected(expected.isSelected())
			.setDeleted(expected.isDeleted())
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setScope(expected.getScope())
			.setFinanceType(expected.getFinanceType())
			.setRegistry(expected.getRegistry())
			.setRegistryDocument(expected.getRegistryDocument())
			.setRegistryDocumentType(expected.getRegistryDocumentType())
			.setRegistryDocumentCountry(expected.getRegistryDocumentCountry())
			.setRegistryName(expected.getRegistryName())
			.setRegistryAccount(expected.getRegistryAccount().orElse(null))
			.setAmount(expected.getAmount())
			.setExpenses(expected.getExpenses())
			.setConcept(expected.getConcept())
			.setInvoice(expected.getInvoice().orElse(null))
			.setDueDate(expected.getDueDate())
			.setPayMethod(expected.getPayMethod().orElse(null))
			.setBankAccount(expected.getBankAccount().orElse(null))
			.setBankAlias(expected.getBankAlias())
			.setBic(expected.getBic())
			.setChequeNumber(expected.getChequeNumber())
			.setFinanceStatus(expected.getFinanceStatus())
			.setConfidential(expected.isConfidential())
			.setRemarks(expected.getRemarks())
			.setManual(expected.isManual())
			.setAdvance(expected.isAdvance())
			.setPayroll(expected.isPayroll())
			.setPrepayment(expected.isPrepayment())
			.setSourceId(expected.getSourceId())
			.setFinanceGroup(expected.getFinanceGroup())
			.setPaidDate(expected.getPaidDate().orElse(null))
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())			
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	

	
}
