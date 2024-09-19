package net.aonsolutions.aon.tedi.test.pdf;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;

import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;
import net.aonsolutions.aon.tedi.test.AbstractTediTest;
import solutions.aon.in.invoice.UnknownInvoiceException;

public class PDF2AONTestCase extends AbstractTediTest {

	@Test
	public void test01AUSARTA() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_01_AUSARTA);
	}
	@Test
	public void test02BNP() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_02_BNP);
	}
	@Test
	public void test03BIP_DRIVE() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_03_BIP_DRIVE);
	}
	@Test
	public void test04BIP_DRIVE() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_04_BIP_DRIVE);
	}
	@Test
	public void test05TRANSLOGIA() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_05_TRANSLOGIA);
	}
	@Test
	public void test06TRANSLOGIA() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_06_TRANSLOGIA);
	}
	@Test
	public void test07TOLEDO() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_07_TOLEDO);
	}
	@Test
	public void test08VODAFONE() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_08_VODAFONE);
	}
	@Test
	public void test09VUELING() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_09_VUELING);
	}
	@Test
	public void test10UDAPA() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_2019_01_02_UDAPA);
	}

	private void testTemplate(TestTemplates template) throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		StringBuilder out = new StringBuilder();
		out.append("-----------------");
		out.append("\n");
		out.append("TIEMPOS DE PARSEO");
		out.append("\n");
		out.append("-----------------");
		out.append("\n");
		
		TediContext tctx = new TediContext()
				.setDomain(DOMAIN_ID)
				.setDomainName(DOMAIN_NAME)
				.setAONContext ( ctx )
				;
		tctx.setAonConfiguration(ConfigurationDAO.getConfiguration(tctx.getAONContext()));
		tctx.getAonConfiguration().getCompany().setDocument("B01487271");
		
		Date start = new Date();
		try (InputStream is = PDF2AONTestCase.class.getResourceAsStream(template.getFile())) {
			TediResult result = TEDI.parse(tctx, is, MimeType.PDF);
			String file = "["+ template.getFile() +"]. ";
			
			assertNotNull(result,file + "Null result!");
			assertNotNull(result.getInvoice(),file + "Null Invoice!");
			Invoice inv = result.getInvoice();
			
			// Domain
			assertEquals(DOMAIN_ID , inv.getDomain(),file + " Invoice domain does not match: ");
			
			
			// Activity
			assertNull(inv.getActivity().map(a -> a.getId()).orElse(null),file + "Not Null Activity ID!");
			// Epigraph
			assertNull(inv.getEpigraph(),file + "Not Null Epigraph!");
			// InvestAsset
			assertNull(inv.getInvestAsset(),file + "Not Null InvestAsset!");
			// Project
			assertNull(inv.getProject(),file + "Not Null Project!");
			
			// Invoice type
			assertNotNull(inv.getType(),file + "Null Invoice Type!");
			assertEquals(template.getInvoiceType() , inv.getType(),file + " Invoice type does not match: ");
			
			// Dates
			assertNotNull(inv.getIssueDate(),file + "Null issue date!");
			assertEquals(template.getDate() , inv.getIssueDate(),file + " Invoice issue date does not match: ");
			assertNotNull(inv.getTaxDate(),file + "Null tax date!");
			assertEquals(template.getDate() , inv.getTaxDate(),file + " Invoice tax date does not match: ");

			// Serie/numero/referenceCode
			assertEquals(template.getSeries() , inv.getSeries(),file + " Invoice Series does not match: ");
			assertEquals(template.getNumber() , Integer.valueOf( inv.getNumber()),file + " Invoice Number does not match: ");
			//assertEquals(file + " Invoice Reference Code does not match: ",template.getReference() , inv.getReferenceCode());
			
			// Registry
			assertNotNull(inv.getRegistry(),file + "Null REGISTRY!");
			if (inv.isSales()) {
				assertEquals(template.getReceiverDocument() , inv.getRegistryDocument(),file + " Invoice Registry Document does not match: ");
			} else {
				assertEquals(template.getSenderDocument() , inv.getRegistryDocument(),file + " Invoice Registry Document does not match: ");
			}
			assertEquals(Country.ES  , inv.getRegistryDocumentCountry(),file + " Invoice Registry Document Country does not match: ");
			
			Registry registry = RegistryDAO.get(ctx, inv.getRegistry());
			assertNotNull(registry,file + "Null REGISTRY Row!");
			assertEquals(registry.getDocument()  , inv.getRegistryDocument(),file + " Invoice Registry Document does not match: ");
			assertEquals(registry.getDocumentCountry()  , inv.getRegistryDocumentCountry(),file + " Invoice Registry Document Country does not match: ");
			assertEquals(registry.getDocumentType()  , inv.getRegistryDocumentType(),file + " Invoice Registry Document Type does not match: ");
			assertEquals(registry.getName()  , inv.getRegistryName(),file + " Invoice Registry Name does not match: ");
			
			
			// Transaction
			assertEquals(InvoiceTransactionType.NATIONAL , inv.getTransaction(),file + " Invoice Transaction does not match: ");
			
			
			
			
		} finally {
			Date end = new Date();
			out.append("*  ");
			out.append(end.getTime() - start.getTime());
			out.append(" ms.\t");
			out.append(template.getFile());
			out.append("\n");
		}
		
		out.append("-----------------");
		out.append("\n");
		System.out.println( out.toString() );
	}
	
}
