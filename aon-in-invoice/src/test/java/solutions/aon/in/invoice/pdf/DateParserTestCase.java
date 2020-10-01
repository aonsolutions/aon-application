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

public class DateParserTestCase {
	
	private static Faker FAKER = Faker.instance();
	
	private static final Date JANUARY_10_2019 = Date.from(LocalDateTime.of(2019, 1, 10, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
	
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
		System.out.println("[" + randomText + "]");
		return randomText;
	}

	@Test
	public void testDate_000() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_000 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10/01/2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_001() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_001 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10/01/19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_002() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_002 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10.01.2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_003() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_003 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10.01.19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_004() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_004 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10-01-2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_005() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_005 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10-01-19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_006() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_006 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10-01-2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_007() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_007 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10-01-19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_008() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_008 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10 de enero de 2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_009() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_009 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10 de ene de 2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_010() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_010 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10 enero 2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}
	
	@Test
	public void testDate_011() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_011 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10 ene 2019") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}
	
	@Test
	public void testDate_012() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_012 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10 enero 19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_013() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_013 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10 de enero de 19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_014() throws IOException, UnknownInvoiceException {
		System.out.print("- testDate_014 ----");
		Collection<Date> dates = DateParser.getDates( getLorem("10 de enero del 19") );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}

	@Test
	public void testDate_XXX() throws IOException, UnknownInvoiceException {
		System.out.print("- test XXX ----");
		String txt = "9sj2e49j5l78wnnQ1cJlZecZINbR598F 10 de enero de 2019 DbC111IzSQvwZQb47T25ikeXK7wswb081ON";
		System.out.println("- test XXX ----");
		Collection<Date> dates = DateParser.getDates( txt );
		assertNotNull(dates);
		assertEquals(1,dates.size());
		assertEquals(JANUARY_10_2019,dates.stream().findFirst().get());
	}
	

}


