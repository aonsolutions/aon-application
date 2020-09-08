package solutions.aon.in.invoice.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Locale.IsoCountryCode;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.watson.util.AonDateUtils;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.UnknownInvoiceException;

public class InvoicePDFParserTestCase {
	
	static class InvoiceAssertBuilder {
		
		private InvoiceAssert invoiceAssert = new InvoiceAssert();
		
		public InvoiceAssertBuilder setDate(Date date) {
			invoiceAssert.date = date;
			return this;
		}

		public InvoiceAssertBuilder setDate(int day, int month, int year) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			invoiceAssert.date = calendar.getTime();
			return this;
		}

		public InvoiceAssertBuilder setSenderName(String name) {
			invoiceAssert.senderName = name;
			return this;
		}

		public InvoiceAssertBuilder setSenderCity(String city) {
			invoiceAssert.senderCity = city;
			return this;
		}

		public InvoiceAssertBuilder setSenderAddress(String address) {
			invoiceAssert.senderAddress = address;
			return this;
		}

		public InvoiceAssertBuilder setSenderProvince(String province) {
			invoiceAssert.senderProvince = province;
			return this;
		}

		public InvoiceAssertBuilder setSenderPostalCode(String postalCode) {
			invoiceAssert.senderPostalCode = postalCode;
			return this;
		}

		public InvoiceAssertBuilder setSenderCountry(String isoCountryCode) {
			invoiceAssert.senderCountry = isoCountryCode;
			return this;
		}

		public InvoiceAssertBuilder setSenderDocument(String document) {
			invoiceAssert.senderDocument = document;
			return this;
		}

		public InvoiceAssertBuilder setSenderDocumentCountry(String isoCountryCode) {
			invoiceAssert.senderDocumentCountry = isoCountryCode;
			return this;
		}
 
		
		public InvoiceAssert getInvoiceAssert() {
			return invoiceAssert;
		}

	}
	
	static class InvoiceAssert implements InvoiceBuilder<Object> {
		
		private Date date;
		private String senderName;
		private String senderCity;
		private String senderAddress;
		private String senderProvince;
		private String senderPostalCode;
		private String senderCountry;
		private String senderDocument;
		private String senderDocumentCountry;		

		@Override
		public void setDate(Date date) {
			if ( this.date != null )
				Assert.assertEquals(this.date, date);
		}

		@Override
		public void setSenderName(String name) {
			if ( this.senderName != null )
				Assert.assertEquals(this.senderName, name);
		}

		@Override
		public void setSenderCity(String city) {
			if ( this.senderCity != null )
			Assert.assertEquals(this.senderCity, city);
		}

		@Override
		public void setSenderAddress(String address) {
			if ( this.senderAddress != null )
				Assert.assertEquals(this.senderAddress, address);	
		}

		@Override
		public void setSenderProvince(String province) {
			if ( this.senderProvince != null )
				Assert.assertEquals(this.senderProvince, province);
		}

		@Override
		public void setSenderPostalCode(String postalCode) {
			if ( this.senderPostalCode != null )
				Assert.assertEquals(this.senderPostalCode, postalCode);
		}

		@Override
		public void setSenderCountry(String isoCountryCode) {
			if ( this.senderCountry != null )
				Assert.assertEquals(this.senderCountry, isoCountryCode);			
		}

		@Override
		public void setSenderDocument(String document) {
			if ( this.senderDocument != null )
				Assert.assertEquals(this.senderDocument, document);						
		}

		@Override
		public void setSenderDocumentCountry(String isoCountryCode) {
			if ( this.senderDocumentCountry != null )
				Assert.assertEquals(this.senderDocumentCountry, isoCountryCode);						
		}
		
	}

	@Test
	public void testAMAZON_1() throws IOException, UnknownInvoiceException {
		try (InputStream is = InvoicePDFParserTestCase.class.getResourceAsStream("AMAZON_1.pdf")) {
			InvoicePDFParser.parse(is, 
			new InvoiceAssertBuilder()
			.setSenderCountry("ES")
			.setSenderCity("MADRID")
			.setSenderProvince("MADRID")
			.setSenderPostalCode("28045")
			.setSenderAddress("CALLE DE RAMÍREZ DE PRADO 5")
			.setSenderDocument("W0184081H")
			.setSenderDocumentCountry("ES")
			.setDate(10, Calendar.JANUARY, 2019)
			.getInvoiceAssert()
			);
		}
	}

}
