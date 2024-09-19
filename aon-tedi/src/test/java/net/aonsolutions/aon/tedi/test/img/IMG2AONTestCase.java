package net.aonsolutions.aon.tedi.test.img;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;

import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;
import net.aonsolutions.aon.tedi.test.AbstractTediTest;
import solutions.aon.in.invoice.UnknownInvoiceException;

public class IMG2AONTestCase extends AbstractTediTest {

	private static final String AUTO_REFERENCE_CODE = "<auto>";

	@Test
	public void testAON_03_ERKIAGA() throws IOException, UnknownInvoiceException, ClassNotFoundException, TediException {
		testTemplate(TestTemplates.AON_03_ERKIAGA);
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
		try (InputStream is = IMG2AONTestCase.class.getResourceAsStream(template.getFile())) {
			String file = "["+ template.getFile() +"]. ";
			TediResult result = TEDI.parse(tctx, is, MimeType.JPEG);
			
			assertNotNull(result, file + "Null result!");
			assertNotNull(result.getInvoice(),file + "Null Invoice!");
			Invoice inv = result.getInvoice();
			
			// Domain
//			assertEquals(file + " Invoice domain does not match: ", DOMAIN_ID.intValue() , inv.getDomain());
			
			// Activity
			assertNotNull(inv.getActivity().orElse(null),file + "Null Activity!");
			assertNull(inv.getActivity().map(a -> a.getId()).orElse(null),file + "Not Null Activity ID!");
			
			// Epigraph
			assertNull(inv.getEpigraph(),file + "Not Null Epigraph!");
			// InvestAsset
			assertNull(inv.getInvestAsset(),file + "Not Null InvestAsset!");
			// Project
			assertNull(inv.getProject(),file + "Not Null Project!");
			
			// Invoice type
			assertNotNull(inv.getType(),file + "Null Invoice Type!");
			assertEquals(InvoiceType.UNDEDUCTIBLE , inv.getType(),file + " Invoice type does not match: ");
			
			// Dates
			assertNotNull(inv.getIssueDate(),file + "Null issue date!");
			assertEquals(template.getDate() , inv.getIssueDate(),file + " Invoice issue date does not match: ");
			assertNotNull(inv.getTaxDate(),file + "Null tax date!");
			assertEquals(template.getDate() , inv.getTaxDate(),file + " Invoice tax date does not match: ");

			assertEquals(AUTO_REFERENCE_CODE , inv.getReferenceCode(),file + " Invoice Reference Code does not match: ");
			
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
			assertEquals(template.getSenderDocument()  , inv.getRegistryDocument(),file + " Invoice Registry Document does not match: ");
			assertEquals(registry.getDocumentCountry()  , inv.getRegistryDocumentCountry(),file + " Invoice Registry Document Country does not match: ");
			assertEquals(registry.getDocumentType()  , inv.getRegistryDocumentType(),file + " Invoice Registry Document Type does not match: ");
			
			
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
