package net.aonsolutions.aon.verifactu;

import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFiscal;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.TaxBreakdown;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;

public class Invoice2VerifactuTest {
	
	private Invoice2VerifactuTest() {
	}
	
	@Test
	void nullVerifactuConfigurationTest() {
		RegFactuSistemaFacturacion rfsf = Invoice2Verifactu.build(config(), company(), invoices(), null);
		assertNotNull( rfsf );
	}
	
	private  VerifactuConfiguration config() {
		return new VerifactuConfiguration()
			.setActive(true)
			.setTest(true)
			.setDefaultCertificate(null)
			.setCertificate(null)
			.setIncludeDate(null)
			.setRegistryDate(null)
		;
	}
	private Company company() {
		Company company = new Company();
		company.setDocument("11111111H");
		company.setName("Verifactu Test Company S.L.");
		company.setDomain(new Domain().setId(1));
		return company;
	}
	private List<Invoice> invoices() {
		return Stream.of(invoice1()).toList();
	}
	private Invoice invoice1() {
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
			.addDetail(new InvoiceDetail()
				.setQuantity(1)
				.setPrice(100.0)
				.setTaxableBase(100.0)
				.addTax(new InvoiceTax()
					.setTaxType(TaxType.VAT)
					.setBase(100.0)
					.setPercentage(21.0)
					.setQuota(21.0)
					.setDeductibleQuota(21.0))
				)
			.setVatQuota(21.0)
			.setTotal(121.0)
		;
	}
	private Invoice invoice2() {
		return new Invoice();
	}
	private Invoice invoice3() {
		return new Invoice();
	}
}
