package solutions.aon.in.invoice.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import com.github.javafaker.Faker;

import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.templates.DateParser;
import solutions.aon.in.invoice.templates.ParserContext;

public class DateParserTestCase {
	
	private static Faker FAKER = Faker.instance();
	
	private static final Date JANUARY_10_2019 = Date.from(LocalDateTime.of(2019, 1, 10, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
	private static final Date JUNE_15_2017    = Date.from(LocalDateTime.of(2017, 6, 15, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
	private static final Date FEBRUARY_03_02_2021 = Date.from(LocalDateTime.of(2021, 2, 3, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
	
	public DateParserTestCase() {
		super();
	}
	
	private String getLorem( String data ) {
		String randomText = null;
		if (FAKER.random().nextInt(0, 6) > 3) {
			randomText = 
				((FAKER.random().nextInt(0, 100) < 80)?FAKER.lorem().sentence(FAKER.random().nextInt(0, 5)):"") +
				" " +  data  + " "
				+ ((FAKER.random().nextInt(0, 100) < 80)?FAKER.lorem().sentence(FAKER.random().nextInt(0, 5)):"")
				;
 	
		} else {
			randomText = 
				((FAKER.random().nextInt(0, 100) < 80)?FAKER.lorem().characters(FAKER.random().nextInt(0, 50),true):"") +
				" " +  data  + " "
				+ ((FAKER.random().nextInt(0, 100) < 80)?FAKER.lorem().characters(FAKER.random().nextInt(0, 50),true):"") 
				;
		}
		return randomText;
	}

	@Test
	public void testDate_000() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10/01/2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_001() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10/01/19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_002() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10.01.2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_003() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10.01.19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_004() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10-01-2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_005() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10-01-19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_006() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10-01-2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_007() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10-01-19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_008() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10 de enero de 2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_009() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10 de ene de 2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_010() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10 enero 2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}
	
	@Test
	public void testDate_011() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10 ene 2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}
	
	@Test
	public void testDate_012() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10 enero 19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_013() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem("10 de enero de 19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_014() throws IOException, UnknownInvoiceException {
		String text = " 10 enero 2019 "; 
		Collection<Date> dates = DateParser.getDates(ParserContext.SPANISH, getLorem(text) );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_015() throws IOException, UnknownInvoiceException {
		String txt = "9sj2e49j5l78wnnQ1cJlZecZINbR598F 10 de enero de 2019 DbC111IzSQvwZQb47T25ikeXK7wswb081ON";
		Collection<Date> dates = DateParser.getDates( ParserContext.SPANISH,txt );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	
	@Test
	public void testDate_016() throws IOException, UnknownInvoiceException {
		String txt = "Desde 15-06-17 el precio del trafico movil en UE/EEE";
		Collection<Date> dates = DateParser.getDates( ParserContext.SPANISH,txt );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JUNE_15_2017,dates.stream().findFirst().get());
	}
	
	@Test
	public void testDate_017() throws IOException, UnknownInvoiceException {
		String txt = "3 Ago 14:25:59 B 606390017 M.Orange Love Negocio Total 27s 0,0000";
		Collection<Date> dates = DateParser.getDates( ParserContext.SPANISH,txt );
		assertNotNull(dates);
		assertEquals(0,dates.size());
	}

	@Test
	public void testDate_018() throws IOException, UnknownInvoiceException {
		String txt = "Peri­odo de 09.08.2018 a 31.08.2018 167 kWh 0,106218 \u20ac/kWh 17,74 \u20ac";
		Collection<Date> dates = DateParser.getDates( ParserContext.SPANISH,txt );
		assertNotNull(dates);
		assertEquals(2,dates.size());
	}
	
	@Test
	public void testDate_019() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.ENGLISH_US, getLorem("February 3, 2021") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(FEBRUARY_03_02_2021,dates.stream().findFirst().get());
	}
	
	@Test
	public void testDate_020() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.ENGLISH_US, getLorem("Feb 3, 2021") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(FEBRUARY_03_02_2021,dates.stream().findFirst().get());
	}
	
	@Test
	public void testDate_021() throws IOException, UnknownInvoiceException {
		Collection<Date> dates = DateParser.getDates(ParserContext.ENGLISH_US, getLorem("Feb 3 2021") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(FEBRUARY_03_02_2021,dates.stream().findFirst().get());
	}
	
}


