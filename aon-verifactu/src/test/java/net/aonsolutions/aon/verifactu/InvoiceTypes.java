package net.aonsolutions.aon.verifactu;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;

class InvoiceTypes {
	static Invoice VENTA_NACIONAL_SIMPLE() {
		Date today = Date.from( LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant() );
		return new Invoice()
			.setId(1)
			.setType(InvoiceType.SALES)
			.setSeries("A" + AonDateUtils.getYear(today))
			.setNumber(1)
			.setReferenceCode("A" + AonDateUtils.getYear(today) + "/000001")
			.setIssueDate(today)
			.setTaxDate(today)
			.setTransaction(InvoiceTransactionType.NATIONAL)
			.setRectificationType(RectificationType.NONE)
			.setConfidential(false)
			.setRegistryDocument("88888888Y")
			.setRegistryDocumentType(DocumentType.NIF)
			.setRegistryDocumentCountry(Country.ES)
			.setRegistryName("Verfictu Cliente Test")
			.setSurcharge(false)
			.setWithholding(false)
			.setWithholdingFarmer(false)
			.setVatAccrualPayment(false)
			.setInvestment(false)
			.setService(false)
			.setAddress( new RegistryAddress()
				.setStreetType(StreetType.CALLE)
				.setAddress("Calle Verifactu")
				.setNumber("6")
				.setAddress2(" portal Verifactu")
				.setZip("01000")
				.setCity("Abetxukooo")
				.setProvince("ALAVA")
				.setCountry(Country.ES)
			)
			.addDetail(new InvoiceDetail()
				.setQuantity(1)
				.setPrice(100.0)
				.setTaxableBase(100.0)
				.addTax(new InvoiceTax()
					.setTaxType(TaxType.VAT)
					.setBase(100.0)
					.setPercentage(21.0)
					.setQuota(21.0)
					.setDeductibleQuota(21.0)
				)
			)
			.setVatQuota(21.0)
			.setTotal(121.0)
			.refreshTaxBreakdown()
		;
	}

	static Invoice VENTA_NACIONAL_RE() {
		Date today = Date.from( LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant() );
		return new Invoice()
			.setId(1)
			.setType(InvoiceType.SALES)
			.setSeries("A" + AonDateUtils.getYear(today))
			.setNumber(1)
			.setReferenceCode("A" + AonDateUtils.getYear(today) + "/000001")
			.setIssueDate(today)
			.setTaxDate(today)
			.setTransaction(InvoiceTransactionType.NATIONAL)
			.setRectificationType(RectificationType.NONE)
			.setConfidential(false)
			.setRegistryDocument("88888888Y")
			.setRegistryDocumentType(DocumentType.NIF)
			.setRegistryDocumentCountry(Country.ES)
			.setRegistryName("Verfictu Cliente Test")
			.setSurcharge(true)
			.setWithholding(false)
			.setWithholdingFarmer(false)
			.setVatAccrualPayment(false)
			.setInvestment(false)
			.setService(false)
			.setAddress( new RegistryAddress()
				.setStreetType(StreetType.CALLE)
				.setAddress("Calle Verifactu")
				.setNumber("6")
				.setAddress2(" portal Verifactu")
				.setZip("01000")
				.setCity("Abetxukooo")
				.setProvince("ALAVA")
				.setCountry(Country.ES)
			)
			.addDetail(new InvoiceDetail()
				.setQuantity(1)
				.setPrice(100.0)
				.setTaxableBase(100.0)
				.addTax(new InvoiceTax()
					.setTaxType(TaxType.VAT)
					.setBase(100.0)
					.setPercentage(21.0)
					.setQuota(21.0)
					.setSurcharge(5.2)
					.setSurchargeQuota(5.2)
					.setDeductibleQuota(26.2)
				)
			)
			.setVatQuota(21.0)
			.setTotal(121.0)
			.refreshTaxBreakdown()
		;
	}
}
