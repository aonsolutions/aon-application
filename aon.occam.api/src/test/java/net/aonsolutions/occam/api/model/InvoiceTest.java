package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.RepeatedTest;

class InvoiceTest {
	
	@RepeatedTest(10)
	void testInvoice() {
		Invoice expected = AonMocker.mock(Invoice.class);
		Invoice actual = new Invoice()
			.setSelected(expected.isSelected())
			.setRectificationInvoice( expected.getRectificationInvoice().orElse(null))
			.setInvoiceAddress( expected.getInvoiceAddress().orElse(null) )
			.setFinances(expected.getFinances())
			.setFiscal(expected.getFiscal().orElse(null)) 
			.setAttach(expected.getAttach().orElse(null))
			.setRawdocId(expected.getRawdocId());
		actual	
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setActivity(expected.getActivity().orElse(null))
			.setProject(expected.getProject())
			.setType(expected.getType())
			.setSeries(expected.getSeries())
			.setNumber(expected.getNumber())
			.setReferenceCode(expected.getReferenceCode())
			.setTransaction(expected.getTransaction())
			.setIssueDate(expected.getIssueDate())
			.setTaxDate(expected.getTaxDate())
			.setRegistry(expected.getRegistry())
			.setRegistryDocument(expected.getRegistryDocument())
			.setRegistryDocumentType(expected.getRegistryDocumentType())
			.setRegistryDocumentCountry(expected.getRegistryDocumentCountry())
			.setRegistryName(expected.getRegistryName())
			.setRegistryAccount(expected.getRegistryAccount().orElse(null))
			.setRectificationType(expected.getRectificationType())
			.setRectificationInvoiceId(expected.getRectificationInvoiceId())
			.setSeller( expected.getSeller().orElse(null) )
			.setScope( expected.getScope() )
			.setConfidential(expected.isConfidential())
			.setRecorded(expected.isRecorded())
			.setSurcharge(expected.isSurcharge())
			.setWithholding(expected.isWithholding())
			.setWithholdingFarmer(expected.isWithholdingFarmer())
			.setVatAccrualPayment(expected.isVatAccrualPayment())
			.setInvestment(expected.isInvestment())
			.setService(expected.isService())
			.setSigned(expected.isSigned())
			.setAnnulled(expected.isAnnulled())
			.setTaxableBase(expected.getTaxableBase())
			.setVatQuota(expected.getVatQuota())
			.setRetentionQuota(expected.getRetentionQuota())
			.setTotal( expected.getTotal() )
			.setComments( expected.getComments() )
			.setRemarks( expected.getRemarks() )
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
		;
		expected.messageStream().forEach( m -> actual.addMessage(m));
		expected.detailStream().forEach( d -> actual.addDetail(d));
		AonAsserts.assertClassEquals(expected, actual);
	}
}
