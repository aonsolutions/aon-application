package net.aonsolutions.aon.tedi.test.img;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Test;

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
			
			assertNotNull(file + "Null result!", result);
			assertNotNull(file + "Null Invoice!", result.getInvoice());
			Invoice inv = result.getInvoice();
			
			// Domain
//			assertEquals(file + " Invoice domain does not match: ", DOMAIN_ID.intValue() , inv.getDomain());
			
			// Activity
			assertNull(file + "Not Null Activity!", inv.getActivity());
			// Epigraph
			assertNull(file + "Not Null Epigraph!", inv.getEpigraph());
			// InvestAsset
			assertNull(file + "Not Null InvestAsset!", inv.getInvestAsset());
			// Project
			assertNull(file + "Not Null Project!", inv.getProject());
			
			// Invoice type
			assertNotNull(file + "Null Invoice Type!", inv.getType());
			assertEquals(file + " Invoice type does not match: ", InvoiceType.UNDEDUCTIBLE , inv.getType());
			
			// Dates
			assertNotNull(file + "Null issue date!", inv.getIssueDate());
			assertEquals(file + " Invoice issue date does not match: ",template.getDate() , inv.getIssueDate());
			assertNotNull(file + "Null tax date!", inv.getTaxDate());
			assertEquals(file + " Invoice tax date does not match: ",template.getDate() , inv.getTaxDate());

			assertEquals(file + " Invoice Reference Code does not match: ", AUTO_REFERENCE_CODE , inv.getReferenceCode());
			
			// Registry
			assertNotNull(file + "Null REGISTRY!", inv.getRegistry());
			if (inv.isSales()) {
				assertEquals(file + " Invoice Registry Document does not match: ",template.getReceiverDocument() , inv.getRegistryDocument());
			} else {
				assertEquals(file + " Invoice Registry Document does not match: ",template.getSenderDocument() , inv.getRegistryDocument());
			}
			assertEquals(file + " Invoice Registry Document Country does not match: ",Country.ES  , inv.getRegistryDocumentCountry());
			
			Registry registry = RegistryDAO.get(ctx, inv.getRegistry());
			assertNotNull(file + "Null REGISTRY Row!", registry);
			assertEquals(file + " Invoice Registry Document does not match: ", template.getSenderDocument()  , inv.getRegistryDocument());
			assertEquals(file + " Invoice Registry Document Country does not match: ",registry.getDocumentCountry()  , inv.getRegistryDocumentCountry());
			assertEquals(file + " Invoice Registry Document Type does not match: ",registry.getDocumentType()  , inv.getRegistryDocumentType());
			
			
			// Transaction
			assertEquals(file + " Invoice Transaction does not match: ", InvoiceTransactionType.NATIONAL , inv.getTransaction());
			
			
			
			
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
