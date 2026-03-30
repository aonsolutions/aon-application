package net.aonsolutions.aon.tedi.test;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.ewok.TediAddress;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceDetail;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.ewok.TediRegistry;
import es.translogia.tedi.json.TediInvoiceJSON;
import net.aonsolutions.aon.tedi.TediParser;

class TediValidationTest {
	private LinkedList<InvoiceError> getMessages(TediResult result) {
		return result.getAccountingInvoice().messageStream().collect(Collectors.toCollection(LinkedList::new));
	}
	
	@AfterEach
	public void afterEach() {
		System.out.println("");
	}

	@Test
	public void test_001_NullInvoiceType() throws Exception {
		System.out.print("\t-test Tedi null invoice type validation");
		JSONObject tediInvoice = TediEwokFaker.getTediInvoice();
		TediInvoice tedi = TediInvoiceJSON.fromJSON(tediInvoice);
		tedi.setType(null);
		TediResult result = TediParser.toFullInvoice(null,null,tedi);
		assertNotNull(getMessages(result));
		printMessages(getMessages(result));
		MatcherAssert.assertThat(getMessages(result), hasItem(hasProperty("code", equalTo(InvoiceErrorMessages.C001.toString()))));
	}

//	@Test
//	public void test_002_OverflowSeries() throws Exception {
//		System.out.print("\t-test Tedi overflow series validation");
//		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
//		tedi.setType(TediInvoiceType.EMITIDA);
//		tedi.setSeries(AonStringUtils.repeat("X", (INVOICE.SERIES.getDataType().length() + 1)));
//		TediResult result = TediParser.toFullInvoice(null,null,tedi);
//		assertNotNull(getMessages(result));
//		printMessages(getMessages(result));
//		MatcherAssert.assertThat(getMessages(result), hasItem(allOf(
//				hasProperty("code", equalTo(InvoiceErrorMessages.C003.toString()))
//				,hasProperty("context", hasProperty("key",is(InvoiceErrorKey.SERIES)))
//				)));
//	}

//	@Test
//	public void test_003_NullNumber() throws Exception {
//		System.out.print("\t-test Tedi null invoice number validation");
//		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
//		tedi.setType(TediInvoiceType.EMITIDA);
//		tedi.setNumber(null);
//		TediResult result = TediParser.toFullInvoice(null,null,tedi);
//		assertNotNull(getMessages(result));
//		printMessages(getMessages(result));
//		MatcherAssert.assertThat(getMessages(result), hasItem(allOf(
//				hasProperty("code", equalTo(InvoiceErrorMessages.C003.toString()))
//				,hasProperty("context", hasProperty("key",is(InvoiceErrorKey.NUMBER)))
//				)));
//	}

	@Test
	public void test_004_OverflowReference() throws Exception {
		System.out.print("\t-test Tedi overflow reference code validation");
		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
		tedi.setReference(AonStringUtils.repeat("X", (INVOICE.REFERENCE_CODE.getDataType().length() + 1)));
		TediResult result = TediParser.toFullInvoice(null,null,tedi);
		assertNotNull(getMessages(result));
		printMessages(getMessages(result));
		MatcherAssert.assertThat(getMessages(result), hasItem(allOf(
					 hasProperty("code", equalTo(InvoiceErrorMessages.C002.toString()))		
					,hasProperty("context", hasProperty("key",is(InvoiceErrorKey.REFERENCE_CODE)))
						)));
	}

	@Test
	public void test_005_NullInvoiceDate() throws Exception {
		System.out.print("\t-test Tedi null invoice date validation");
		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
		tedi.setDate( (Date) null);
		TediResult result = TediParser.toFullInvoice(null,null,tedi);
		assertNotNull(getMessages(result));
		printMessages(getMessages(result));
		MatcherAssert.assertThat(getMessages(result), hasItem(allOf(
				hasProperty("code", equalTo(InvoiceErrorMessages.C001.toString()))
				,hasProperty("context", hasProperty("key",is(InvoiceErrorKey.ISSUE_DATE)))
				)));
	}

	@Test
	public void test_006_NullRegistryDocument() throws Exception {
		System.out.print("\t-test Tedi null registry document validation");
		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
		if (tedi.getType() == null) {
			tedi.setType(TediInvoiceType.RECIBIDA);
		}
		if (tedi.getRegistry() == null) {
			tedi.setRegistry( new TediRegistry());
		}
		tedi.getRegistry().setDocument(null);
		TediResult result = TediParser.toFullInvoice(null,null,tedi);
		assertNotNull(getMessages(result));
		printMessages(getMessages(result));
		MatcherAssert.assertThat(getMessages(result),hasItem(allOf(
				hasProperty("code", equalTo(InvoiceErrorMessages.C001.toString())),
				hasProperty("context", hasProperty("key",is(InvoiceErrorKey.REGISTRY)))
				)));
	}
	
	@Test
	public void test_007_OverflowRegistryDocument() throws Exception {
		System.out.print("\t-test Tedi overflow registry document validation");
		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
		if (tedi.getType() == null) {
			tedi.setType(TediInvoiceType.RECIBIDA);
		}
		if (tedi.getRegistry() == null) {
			tedi.setRegistry( new TediRegistry( ));
		}
		tedi.getRegistry().setDocument(AonStringUtils.repeat("X", (INVOICE.RDOCUMENT.getDataType().length() + 1)));
		TediResult result = TediParser.toFullInvoice(null,null,tedi);
		assertNotNull(getMessages(result));
		printMessages(getMessages(result));
		MatcherAssert.assertThat(getMessages(result), hasItem(allOf(
				hasProperty("code", equalTo(InvoiceErrorMessages.C002.toString()))
				,hasProperty("context", hasProperty("key",is(InvoiceErrorKey.RDOCUMENT)))
				)));
	}

	
	@Test
	public void test_008_InvalidRegistryDocument() throws Exception {
		System.out.print("\t-test Tedi invalid registry document validation");
		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
		if (tedi.getType() == null) {
			tedi.setType(TediInvoiceType.RECIBIDA);
		}
		if (tedi.getRegistry() == null) {
			tedi.setRegistry( new TediRegistry( ));
		}
		tedi.getRegistry().setDocument("11111111Q");
		TediResult result = TediParser.toFullInvoice(null,null,tedi);
		assertNotNull(getMessages(result));
		printMessages(getMessages(result));
		MatcherAssert.assertThat(getMessages(result),hasItem(allOf(
				hasProperty("code", equalTo(InvoiceErrorMessages.C004.toString())),
				hasProperty("context", hasProperty("key",is(InvoiceErrorKey.RDOCUMENT)))
				)));
	}

//	@Test
//	public void test_009_NullRegistryDocumentCountry() throws Exception {
//		System.out.print("\t-test Tedi null registry document country validation");
//		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
//		if (tedi.getType() == null) {
//			tedi.setType(TediInvoiceType.RECIBIDA);
//		}
//		if (tedi.getRegistry() == null) {
//			tedi.setRegistry( new TediRegistry( ));
//		}
//		tedi.getRegistry().setDocumentCountry(null);
//		TediResult result = TediParser.toFullInvoice(null,null,tedi);
//		assertNotNull(getMessages(result));
//		printMessages(getMessages(result));
//		MatcherAssert.assertThat(getMessages(result),hasItem(allOf(
//				hasProperty("code", equalTo(InvoiceErrorMessages.C003.toString())),
//				hasProperty("context", hasProperty("key",is(InvoiceErrorKey.RDOCUMENT_COUNTRY))))))
//				;
//	}

//	@Test
//	public void test_010_NullRegistyName() throws Exception {
//		System.out.print("\t-test Tedi null registry name validation");
//		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
//		if (tedi.getType() == null) {
//			tedi.setType(TediInvoiceType.RECIBIDA);
//		}
//		if (tedi.getRegistry() == null) {
//			tedi.setRegistry( new TediRegistry( ));
//		}
//		tedi.getRegistry().setName(null);
//		TediResult result = TediParser.toFullInvoice(null,null,tedi);
//		Assert.assertNotNull(getMessages(result));
//		printMessages(getMessages(result));
//		MatcherAssert.assertThat(getMessages(result),hasItem(allOf(
//				hasProperty("code", equalTo(InvoiceErrorMessages.C001.toString())),
//				hasProperty("context", hasProperty("key",is(InvoiceErrorKey.RNAME))))));
//	}

	@Test
	public void test_011_OverflowRegistryName() throws Exception {
		System.out.print("\t-test Tedi overflow registry name validation");
		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
		if (tedi.getType() == null) {
			tedi.setType(TediInvoiceType.RECIBIDA);
		}
		if (tedi.getRegistry() == null) {
			tedi.setRegistry( new TediRegistry( ));
		}
		tedi.getRegistry().setName(AonStringUtils.repeat("X", (INVOICE.RNAME.getDataType().length() + 1)));
		TediResult result = TediParser.toFullInvoice(null,null,tedi);
		assertNotNull(getMessages(result));
		printMessages(getMessages(result));
		MatcherAssert.assertThat(getMessages(result), hasItem(allOf(
				hasProperty("code", equalTo(InvoiceErrorMessages.C002.toString()))
				,hasProperty("context", hasProperty("key",is(InvoiceErrorKey.RNAME))))));
	}

	@Test
	public void test_012_OverflowAddress() throws Exception {
		System.out.print("\t-test Tedi overflow address validation");
		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
		if (tedi.getType() == null) {
			tedi.setType(TediInvoiceType.RECIBIDA);
		}
		if (tedi.getRegistry() == null) {
			tedi.setRegistry( new TediRegistry( ));
		}
		if (tedi.getRegistry().getAddress() == null) {
			tedi.getRegistry().setAddress(new TediAddress( ));	
		}
		tedi.getRegistry().getAddress().setAddress(AonStringUtils.repeat("X", (RADDRESS.ADDRESS.getDataType().length() + 1)));
		TediResult result = TediParser.toFullInvoice(null,null,tedi);
		assertNotNull(getMessages(result));
		printMessages(getMessages(result));
		MatcherAssert.assertThat(getMessages(result), hasItem(allOf(
				hasProperty("code", equalTo(InvoiceErrorMessages.C002.toString()))
				,hasProperty("context", hasProperty("key",is(InvoiceErrorKey.ADDRESS))))));
	}

	@Test
	public void test_013_OverflowDetailDescription() throws Exception {
		System.out.print("\t-test Tedi overflow first detail description");
		TediInvoice tedi = TediInvoiceJSON.fromJSON(TediEwokFaker.getTediInvoice());
		if (tedi.getDetails() == null) {
			tedi.setDetails( new LinkedList<TediInvoiceDetail>());
		}
		if (tedi.getDetails().size() == 0) {
			tedi.getDetails().add( new TediInvoiceDetail());
		}
		TediInvoiceDetail det = tedi.getDetails().get(0);
		det.setDescription(AonStringUtils.repeat("X", (INVOICE_DETAIL.DESCRIPTION.getDataType().length() + 1)));
		TediResult result = TediParser.toFullInvoice(null,null,tedi);
		assertNotNull(getMessages(result));
		printMessages(getMessages(result));
		MatcherAssert.assertThat(getMessages(result), 
			hasItem(
				allOf(
					 hasProperty("code", equalTo(InvoiceErrorMessages.C002.toString()))		
					,hasProperty("context", 
						allOf( 
							hasProperty("key",is(InvoiceErrorKey.DETAIL_DESCRIPTION))
							,hasProperty("line",is(1))
		 )))));
	}

	private void printMessages(LinkedList<InvoiceError> messages) {
		if (messages != null && messages.size()>0) {
			System.out.println();
			System.out.println("\t\t"
					+" " + AonStringUtils.repeat("-", 4)
					+" " + AonStringUtils.repeat("-", 5)
					+" " + AonStringUtils.repeat("-", 25)
					+" " + AonStringUtils.repeat("-", 80)
					+" "
					);
			System.out.println("\t\t"
					+"|" + AonStringUtils.rightPad("TYP", 4)
					+"|" + AonStringUtils.rightPad("CODE", 5)
					+"|" + AonStringUtils.rightPad("FIELD", 25)
					+"|" + AonStringUtils.rightPad("MESSAGE", 80)
					+"|"
					);
			System.out.println("\t\t"
					+"|" + AonStringUtils.repeat("-", 4)
					+"|" + AonStringUtils.repeat("-", 5)
					+"|" + AonStringUtils.repeat("-", 25)
					+"|" + AonStringUtils.repeat("-", 80)
					+"|"
					);
			for (InvoiceError e : messages) {
				System.out.println("\t\t"
						+"|" + AonStringUtils.rightPad(e.getLevel() == null ? "" : e.getLevel().toString(), 4)
						+"|" + AonStringUtils.rightPad(AonStringUtils.defaultString(e.getCode()), 5)
						+"|" + AonStringUtils.rightPad(e.getContext() == null ? "" : e.getContext().toString(), 25)
						+"|" + AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(e.getMessage()),79),80)
						+"|");
			}
			System.out.println("\t\t"
					+" " + AonStringUtils.repeat("-", 4)
					+" " + AonStringUtils.repeat("-", 5)
					+" " + AonStringUtils.repeat("-", 25)
					+" " + AonStringUtils.repeat("-", 80)
					+" "
					);
		}
	}
}
