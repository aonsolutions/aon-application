package solutions.aon;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.seg.social.SistemaRED_I;
import solutions.aon.seg.social.SistemaRED_Remesas;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.invalidData.DataDoesNotExist;
import solutions.aon.seg.social.exceptions.invalidData.LiquidationDoesNotExist;
import solutions.aon.seg.social.exceptions.invalidData.NotExistingYetException;
import solutions.aon.seg.social.exceptions.invalidData.PendingProcessesException;
import solutions.aon.seg.social.exceptions.invalidData.UnfilledMandatory;
import solutions.aon.seg.social.exceptions.invalidData.invalidCccException;
import solutions.aon.seg.social.exceptions.invalidData.outOfTimeException;

public class TestSistemaRED_Remesas {

	
	@Test
	public void testDraftRequestFuture() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Calendar c=Calendar.getInstance();
			c.add(Calendar.MONTH, 1);
			Date d=c.getTime();
			SistemaRED_Remesas.draftRequest(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED_I.Regime.GENERAL, d, d, SistemaRED_I.LiquidationType.L00_NORMAL, false, true);
			fail("Shouldn't succeed");
		} catch (NotExistingYetException e) {
			
		} catch (LiquidationDoesNotExist e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testDraftRequestOutOfTime() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-09-2020");
			SistemaRED_Remesas.draftRequest(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED_I.Regime.GENERAL, d, d, SistemaRED_I.LiquidationType.L00_NORMAL, false, true);
			fail("Shouldn't succeed");
		} catch (outOfTimeException e) {
			
		} catch (LiquidationDoesNotExist e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail();
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testDraftRequestInvalidCCC() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-10-2020");
			SistemaRED_Remesas.draftRequest(certificateInputStream, "jg@FNMT", "pkcs12", "01115320062", SistemaRED_I.Regime.GENERAL, d, d, SistemaRED_I.LiquidationType.L00_NORMAL, false, true);
			fail("Shouldn't succeed");
		} catch (invalidCccException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail();
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testDraftRequestUnfilledCCC() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-10-2020");
			SistemaRED_Remesas.draftRequest(certificateInputStream, "jg@FNMT", "pkcs12", "", SistemaRED_I.Regime.GENERAL, d, d, SistemaRED_I.LiquidationType.L00_NORMAL, false, true);
			fail("Shouldn't succeed");
		} catch (UnfilledMandatory e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail();
		} catch (ParseException e) {
			fail();
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}
	
	
	
	
	
	
	@Test
	public void testConfirmationRequestFuture() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Calendar c=Calendar.getInstance();
			c.add(Calendar.MONTH, 1);
			Date d=c.getTime();
			SistemaRED_Remesas.confirmationRequest(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED_I.Regime.GENERAL, d, d, SistemaRED_I.LiquidationType.L00_NORMAL, true);
			fail("Shouldn't succeed");
		} catch (LiquidationDoesNotExist e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testConfirmationRequestInvalidCCC() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-10-2020");
			SistemaRED_Remesas.confirmationRequest(certificateInputStream, "jg@FNMT", "pkcs12", "01115320062", SistemaRED_I.Regime.GENERAL, d, d, SistemaRED_I.LiquidationType.L00_NORMAL, true);
			fail("Shouldn't succeed");
		} catch (invalidCccException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail();
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testConfirmationRequestUnfilledCCC() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-10-2020");
			SistemaRED_Remesas.confirmationRequest(certificateInputStream, "jg@FNMT", "pkcs12", "", SistemaRED_I.Regime.GENERAL, d, d, SistemaRED_I.LiquidationType.L00_NORMAL, true);
			fail("Shouldn't succeed");
		} catch (UnfilledMandatory e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail();
		} catch (ParseException e) {
			fail();
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}
	
	
//	@Test
//	public void testBankDataByCCC() {
//		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
//			SistemaRED_Remesas.getBankDataByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED_I.Regime.GENERAL);
//		} catch (PendingProcessesException e) {
//			
//		}
//		catch (IOException e) {
//			fail("Wrong certificate on test");
//		} catch (SegSocialException e) {
//			System.out.println(e.getClass());
//			fail();
//		} catch (FailingHttpStatusCodeException e) {
//			assertTrue(true);
//		}
//	}



}
