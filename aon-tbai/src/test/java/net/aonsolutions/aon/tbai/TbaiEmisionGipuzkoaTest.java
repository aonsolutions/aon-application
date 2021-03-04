package net.aonsolutions.aon.tbai;

import static net.aonsolutions.aon.tbai._enums.Territory.GIPUZKOA;
import static net.aonsolutions.aon.tbai.toolkit.ConsoleToolkit.log;
import static net.aonsolutions.aon.tbai.toolkit.ConsoleToolkit.start_console;
import static net.aonsolutions.aon.tbai.toolkit.ConsoleToolkit.test_title;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Date;

import org.junit.Test;

import net.aonsolutions.aon.tbai.emision.EmisionInvoice;
import net.aonsolutions.aon.tbai.exceptions.json.JsonNotFoundException;
import net.aonsolutions.aon.tbai.exceptions.json.JsonParseException;
import net.aonsolutions.aon.tbai.exceptions.validation.ValidationException;
import net.aonsolutions.aon.tbai.toolkit.ConsoleToolkit;

public class TbaiEmisionGipuzkoaTest {

	@Test
	public void SuccessTest() {
		Date d = new Date();
		start_console(1);
		test_title("Success");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("factura.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		System.out.println();
		log("", "Test passed successfully.", d);
	}

	@Test
	public void jsonNotFoundTest() {
		Date d = new Date();
		start_console(1);
		test_title("json not found");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("factura.txt");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (JsonNotFoundException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("", "Test failed.", d);
	}

	@Test
	public void NullInputStreamTest() {
		Date d = new Date();
		start_console(1);
		test_title("Null input stream");

		try {
			final InputStream is = null;
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (JsonParseException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("", "Test failed.", d);
	}

	@Test
	public void NullInvoiceTest() {
		Date d = new Date();
		start_console(1);
		test_title("Null invoice");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("factura.json");
			final EmisionInvoice invoice = null;
			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("", "Test failed.", d);
	}

	@Test
	public void NoSenderTest() {
		Date d = new Date();
		start_console(1);
		test_title("No sender");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoSender.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}

		fail("Unexpected path");
		log("", "Test failed.", d);
	}

	@Test
	public void NoSenderNIFTest() {
		Date d = new Date();
		start_console(1);
		test_title("No sender NIF");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoSenderNIF.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void WrongSenderNifTest() {
		Date d = new Date();
		start_console(1);
		test_title("Wrong sender NIF format");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaWrongSenderNIF.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}

	@Test
	public void NoSenderNameTest() {
		Date d = new Date();
		start_console(1);
		test_title("No sender name");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoSenderName.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}

	@Test
	public void NoDateTest() {
		Date d = new Date();
		start_console(1);
		test_title("No date");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoDate.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}

	@Test
	public void WrongDateFormatTest() {
		Date d = new Date();
		start_console(1);
		test_title("Wrong date format");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaWrongDateFormat.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}

	@Test
	public void NoInvoiceNumberTest() {
		Date d = new Date();
		start_console(1);
		test_title("No invoice number");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceNumber.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoInvoiceTotalTest() {
		Date d = new Date();
		start_console(1);
		test_title("No invoice total");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceTotal.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoInvoiceDetails() {
		Date d = new Date();
		start_console(1);
		test_title("No invoice details");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceDetails.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}	
	
	@Test
	public void NoInvoiceDetailAmount() {
		Date d = new Date();
		start_console(1);
		test_title("No invoice detail amount");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceDetailAmount.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoInvoiceDetailQuantity() {
		Date d = new Date();
		start_console(1);
		test_title("No invoice detail quantity");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceDetailQuantity.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoInvoiceDetailPrice() {
		Date d = new Date();
		start_console(1);
		test_title("No invoice detail price");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceDetailPrice.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoInvoiceDetailDescription() {
		Date d = new Date();
		start_console(1);
		test_title("No invoice detail description");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceDetailDescription.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoInvoiceCategory() {
		Date d = new Date();
		start_console(1);
		test_title("No invoice category");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceCategory.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoRecievers() {
		Date d = new Date();
		start_console(1);
		test_title("No recievers");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoRecievers.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoRecieverAddress() {
		Date d = new Date();
		start_console(1);
		test_title("No reciever address");
		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoRecieverAddress.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoRecieverNIF() {
		Date d = new Date();
		start_console(1);
		test_title("No reciever NIF");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoRecieverNIF.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void WrongRecieverNIF() {
		Date d = new Date();
		start_console(1);
		test_title("Wrong reciever NIF format");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaWrongRecieverNIF.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);

			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoRecieverName() {
		Date d = new Date();
		start_console(1);
		test_title("No reciever name");

		try {
			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoRecieverName.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
		} catch (ValidationException e) {
			log("Test passed successfully", e.getMessage(), d);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			log("", "Test failed.", d);
			fail("Unexpected exception " + e);
		}
		fail("Unexpected path");
		log("","Test failed.",d);
	}
	
	@Test
	public void NoSignature() {
		Date d = new Date();
		start_console(1);
		test_title("No signature");
		
		ConsoleToolkit.log_warning("Not implemented yet.");
		//fail("Not implemented yet");
	}
	
	@Test
	public void WrongChainingProcess() {
		Date d = new Date();
		start_console(1);
		test_title("No signature");
		
		ConsoleToolkit.log_warning("Not implemented yet.");
		//fail("Not implemented yet");
	}
}
