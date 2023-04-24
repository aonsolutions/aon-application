package com.esferalia.aon.in.payroll.pdf.maker.invoice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

public class InvoiceListTest {
	
	@Test
	public void testEntries() throws FileNotFoundException {
		int repeats = Faker.instance().number().numberBetween(5, 50);
		for (int i=0; i<repeats; i++) {
			singleTest();
		}
	}

	@Test
	public void testInvoices() throws FileNotFoundException {
		int repeats = Faker.instance().number().numberBetween(5, 50);
		for (int i=0; i<repeats; i++) {
			singleTest2();
		}
	}
	
	@Test
	public void testNoEntries() throws FileNotFoundException {
		
		try (OutputStream os = OutputStream.nullOutputStream()){
			InvoiceListTemplate template = new InvoiceListTemplate((String) null, null);
			template.print(os);
		} catch (CanNotCreatePdfException e) {
			fail();
		} catch (IOException e1) {
			fail();
		}
	}
	
	@Test
	public void testNoInvoices() throws FileNotFoundException {
		
		try (OutputStream os = OutputStream.nullOutputStream()){
			InvoiceListTemplate template = new InvoiceListTemplate((Company) null, null);
			template.print(os);
		} catch (CanNotCreatePdfException e) {
			fail();
		} catch (IOException e1) {
			fail();
		}
	}
	
	@Test
	public void testOneEntry() throws FileNotFoundException {
		List<InvoiceListEntry> entries = new LinkedList<>();
		Faker faker = Faker.instance(new Locale("ES"));
		int min = -99999;
		int max = 99999;
		InvoiceListEntry entry = new InvoiceListEntry()
				.setBase(nullProbability(faker.number().randomDouble(4, min, max), 20))
				.setDate(nullProbability(faker.date().past(50 * 365, TimeUnit.DAYS), 20))
				.setIrpf(nullProbability(faker.number().randomDouble(4, min, max), 20))
				.setIva(nullProbability(faker.number().randomDouble(4, min, max), 20))
				.setName(nullProbability(faker.company().name(), 20))
				.setNif(nullProbability(AonStringUtils.substring(faker.idNumber().valid(), 0, 10), 20))
				.setNumber(nullProbability(faker.code().imei(), 20))
				.setTotal(nullProbability(faker.number().randomDouble(4, min, max), 20));
		
		entries.add(nullProbability(entry, 20));
		
		try (OutputStream os = OutputStream.nullOutputStream()){
			InvoiceListTemplate template = new InvoiceListTemplate(nullProbability(faker.company().name(), 20), entries);
			template.print(os);
		} catch (CanNotCreatePdfException e) {
			fail();
		} catch (IOException e1) {
			fail();
		}
	}
	
	@Test
	public void testPageLimitEntries() throws FileNotFoundException {
		Faker faker = Faker.instance(new Locale("ES"));
		List<InvoiceListEntry> entries = new LinkedList<>();
		
		int numberOfPages = (int) faker.number().randomDouble(0, 1, 10);
		
		int numberOfEntries = InvoiceListTemplate.MAX_ENTRIES_PER_PAGE * numberOfPages - 2;
		
		for (int i=0; i<numberOfEntries; i++) {
			int min = -99999;
			int max = 99999;
			InvoiceListEntry entry = new InvoiceListEntry()
					.setBase(faker.number().randomDouble(4, min, max))
					.setDate(faker.date().past(50 * 365, TimeUnit.DAYS))
					.setIrpf(faker.number().randomDouble(4, min, max))
					.setIva(faker.number().randomDouble(4, min, max))
					.setName(faker.company().name())
					.setNif(AonStringUtils.substring(faker.idNumber().valid(), 0, 10))
					.setNumber(faker.code().imei())
					.setTotal(faker.number().randomDouble(4, min, max));
			entries.add(entry);
		}

		try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
			InvoiceListTemplate template = new InvoiceListTemplate(nullProbability(faker.company().name(), 20), entries);
			template.print(os);
			PDDocument document = Loader.loadPDF(os.toByteArray());
			int realNumberOfPages = document.getNumberOfPages();
			document.close();
			assertEquals(realNumberOfPages, numberOfPages);
		} catch (CanNotCreatePdfException e) {
			fail();
		} catch (IOException e1) {
			fail();
		}
	}
	
	@Test
	public void testPageLimitEntriesPlusOne() throws FileNotFoundException {
		Faker faker = Faker.instance(new Locale("ES"));
		List<InvoiceListEntry> entries = new LinkedList<>();
		
		int numberOfPages = (int) faker.number().randomDouble(0, 1, 10);
		
		int numberOfEntries = InvoiceListTemplate.MAX_ENTRIES_PER_PAGE * numberOfPages;
		
		for (int i=0; i<numberOfEntries; i++) {
			int min = -99999;
			int max = 99999;
			InvoiceListEntry entry = new InvoiceListEntry()
					.setBase(faker.number().randomDouble(4, min, max))
					.setDate(faker.date().past(50 * 365, TimeUnit.DAYS))
					.setIrpf(faker.number().randomDouble(4, min, max))
					.setIva(faker.number().randomDouble(4, min, max))
					.setName(faker.company().name())
					.setNif(AonStringUtils.substring(faker.idNumber().valid(), 0, 10))
					.setNumber(faker.code().imei())
					.setTotal(faker.number().randomDouble(4, min, max));
			entries.add(entry);
		}
		
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()/*OutputStream os = new FileOutputStream("InvoiceListTemplateTest.pdf")*/){
			InvoiceListTemplate template = new InvoiceListTemplate(nullProbability(faker.company().name(), 20), entries);
			template.print(os);
			PDDocument document = Loader.loadPDF(os.toByteArray());
			int realNumberOfPages = document.getNumberOfPages();
			document.close();
			assertEquals(realNumberOfPages, numberOfPages + 1);
		} catch (CanNotCreatePdfException e) {
			fail();
		} catch (IOException e1) {
			fail();
		}
	}

	private void singleTest() throws FileNotFoundException {
		Faker faker = Faker.instance(new Locale("ES"));
		List<InvoiceListEntry> entries = new LinkedList<>();
		for (int i=0; i<106; i++) {
			int min = -99999;
			int max = 99999;
			InvoiceListEntry entry = new InvoiceListEntry()
					.setBase(nullProbability(faker.number().randomDouble(4, min, max), 20))
					.setDate(nullProbability(faker.date().past(50 * 365, TimeUnit.DAYS), 20))
					.setIrpf(nullProbability(faker.number().randomDouble(4, min, max), 20))
					.setIva(nullProbability(faker.number().randomDouble(4, min, max), 20))
					.setName(nullProbability(faker.company().name(), 20))
					.setNif(nullProbability(AonStringUtils.substring(faker.idNumber().valid(), 0, 10), 20))
					.setNumber(nullProbability(faker.code().imei(), 20))
					.setTotal(nullProbability(faker.number().randomDouble(4, min, max), 20));
			entries.add(nullProbability(entry, 20));
		}
		
		try (OutputStream os = OutputStream.nullOutputStream()/*new FileOutputStream("InvoiceListTemplateTest.pdf")*/){
			InvoiceListTemplate template = new InvoiceListTemplate(nullProbability(faker.company().name(), 20), entries);
			template.print(os);
		} catch (CanNotCreatePdfException e) {
			fail();
		} catch (IOException e1) {
			fail();
		}
	}
	
	private void singleTest2() throws FileNotFoundException {
		Faker faker = Faker.instance(new Locale("ES"));
		List<Invoice> invoices = new LinkedList<>();
		for (int i=0; i<106; i++) {
			int min = -99999;
			int max = 99999;
			Invoice invoice = new Invoice()
					.setTaxableBase(AonNumberUtils.zeroIfNull(nullProbability(faker.number().randomDouble(4, min, max), 20)))
					.setIssueDate(nullProbability(faker.date().past(50 * 365, TimeUnit.DAYS), 20))
					.setRetentionQuota(AonNumberUtils.zeroIfNull(nullProbability(faker.number().randomDouble(4, min, max), 20)))
					.setVatQuota(AonNumberUtils.zeroIfNull(nullProbability(faker.number().randomDouble(4, min, max), 20)))
					.setRegistryName(nullProbability(faker.company().name(), 20))
					.setRegistryDocument(nullProbability(AonStringUtils.substring(faker.idNumber().valid(), 0, 10), 20))
					.setReferenceCode(nullProbability(faker.code().imei(), 20))
					.setTotal(AonNumberUtils.zeroIfNull(nullProbability(faker.number().randomDouble(4, min, max), 20)));
			
			invoices.add(nullProbability(invoice, 20));
		}
		
		Company company = new Company();
		company.setName(nullProbability(faker.company().name(), 20));

		try (OutputStream os = OutputStream.nullOutputStream()/*new FileOutputStream("InvoiceListTemplateTest2.pdf")*/){
			InvoiceListTemplate template = new InvoiceListTemplate(company, invoices);
			template.print(os);
		} catch (CanNotCreatePdfException e) {
			fail();
		} catch (IOException e1) {
			fail();
		}
	}
	
	private static <E> E nullProbability (E item, int percentage) {
		int rand = (int)(Math.random() * 100) + 1;
		if (rand < percentage)
			return null;
		else
			return item;
	}
	
}
