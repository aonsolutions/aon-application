package solutions.aon.seg.social;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import org.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.OutOfServiceException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.DataDoesNotExist;
import solutions.aon.seg.social.exception.invalid.InvalidDateException;
import solutions.aon.seg.social.exception.invalid.LiquidationDoesNotExist;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.exception.invalid.InvalidCccException;
import solutions.aon.seg.social.object.Calc;
import solutions.aon.seg.social.object.Liquidation;
import solutions.aon.seg.social.object.Period;
import solutions.aon.seg.social.object.WorkerLiquidation;

public class TestCalculationQuery {

	@Test
	public void testCalculationQueryOk() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			for (Liquidation liquidation : liq) {
				System.out.println(liquidation);
			}
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testCalculationQueryGrantsAndBonuses() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105577910", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			for (Liquidation liquidation : liq) {
				System.out.println(liq);
			}
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testCalculationQueryDateNotFound() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Calendar c=Calendar.getInstance();
			c.add(Calendar.MONTH, 1);
			Date d=c.getTime();
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			System.out.println(liq);
			fail("Shouldn't throw results");
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (DataDoesNotExist e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail("Should have returned the object");
		}
	}
	
//	@Test
//	public void testCalculationQueryDateNotFoundPast() {
//		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
//			Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-01-1980");
//			Collection<Liquidation> liq=SistemaRED_I.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
//			System.out.println(liq);
//			fail("Shouldn't throw results");
//		} catch (DataDoesNotExist e) {
//			
//		} catch (IOException e) {
//			fail("Wrong certificate on test");
//		} catch (SegSocialException e) {
//			fail("Should have returned the object");
//		} catch (ParseException e) {
//			fail();
//		}
//	}
	
	@Test
	public void testCalculationOriginNoData() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.GENERADAS_POR_LA_TGSS);
			System.out.println(liq);
			fail("Shouldn't throw results");
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (LiquidationDoesNotExist e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail("Should have returned the object");
		} catch (ParseException e) {
			fail("wrong test date");
		}
	}
	
	@Test
	public void testCalculationQueryRegimeCCCNotFound() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL_ARTISTAS, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			System.out.println(liq);
			fail("Shouldn't throw results");
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (WrongRegimeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail("Should have returned the object");
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testCalculationQueryNullCCC() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			System.out.println(liq);
			fail("Shouldn't throw results");
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (UnfilledMandatory e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail("Should have returned the object");
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testCalculationQueryInvalidCCC() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105369062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			System.out.println(liq);
			fail("Shouldn't throw results");
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (InvalidCccException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail("Should have returned the object");
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	@Test
	public void testCalculationQueryWrongCertificate() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMTp12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			fail("Shouldn't end");
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch(CertificateNotFoundException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	@Test
	public void testCalculationQueryWrongCertificateKey() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			fail("Shouldn't end");
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch(InvalidCertificateException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	@Test
	public void testCalculationQueryWrongCertificateType() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkc12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			fail("Shouldn't end");
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch(InvalidCertificateException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	
	
	
	
	
	@Test
	public void testWorkersCalculationQueryOk() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Map<String,Map<String,WorkerLiquidation>> liq=SistemaREDI.workersCalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			for (Map<String, WorkerLiquidation> map : liq.values()) {
				Iterator<String> it=map.keySet().iterator();
				while(it.hasNext()) {
					System.out.println(map.get(it.next()));
				}
			}
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationQueryOkWithBonuses() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-10-2020");
			Map<String,Map<String,WorkerLiquidation>> liq=SistemaREDI.workersCalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105577910", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			for (Map<String, WorkerLiquidation> map : liq.values()) {
				Iterator<String> it=map.keySet().iterator();
				while(it.hasNext()) {
					System.out.println(map.get(it.next()));
				}
			}
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	
	
	@Test
	public void testWorkerCalculationQueryByNAFOk() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			WorkerLiquidation liq=SistemaREDI.WorkerCalculationQueryByNAF(certificateInputStream, "jg@FNMT", "pkcs12","011005185924" ,"01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			System.out.println(liq);
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkerCalculationQueryByNAFNotFound() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			WorkerLiquidation liq=SistemaREDI.WorkerCalculationQueryByNAF(certificateInputStream, "jg@FNMT", "pkcs12","naf_falso" ,"01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			fail("Shouldn't find anything");
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (DataDoesNotExist e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkerCalculationQueriesByNAFOk() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Collection<WorkerLiquidation> liq=SistemaREDI.WorkerCalculationQueriesByNAF(certificateInputStream, "jg@FNMT", "pkcs12","011005185924" ,"01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			for (WorkerLiquidation workerLiquidation : liq) {
				System.out.println(liq);
			}
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkerCalculationQueriesByNAFNotFound() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Collection<WorkerLiquidation> liq=SistemaREDI.WorkerCalculationQueriesByNAF(certificateInputStream, "jg@FNMT", "pkcs12","naf_falso" ,"01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
			fail("Shouldn't find anything");
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (DataDoesNotExist e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
//	@Test
//	public void testCalculationQueryByLiqNumberOk() {
//		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
//			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
//			Collection<Liquidation> liq=SistemaRED_I.CalculationQueryByLiqNumber(certificateInputStream, "jg@FNMT", "pkcs12", "01202000213486894");
//			for (Liquidation liquidation : liq) {
//				System.out.println(liq);
//			}
//		} catch (IOException e) {
//			fail("Wrong certificate on test");
//		} catch (SegSocialException e) {
//			fail(""+e.getClass());
//		} catch (ParseException e) {
//			fail("Test date fails");
//		}
//	}
	
	
	
	@Test
	public void testWorkersCalculationQueryByCCCandNAFsOk() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	
	@Test
	public void testWorkersCalculationQueryByCCCandNAFsOkOriginalCert() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "010019805355", "011001022503", "011005185924"));
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	
	@Test
	public void testWorkersCalculationQueryByCCCandNAFsEmpty() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS));
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationQueryByCCCandNAFsUnavailableLiqType() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L03_COMP_ABONO_SALARIOS_CARACTER_RETROACTIV, SistemaRED.LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (DataDoesNotExist e) {
			
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationQueryByCCCandNAFsWrongDate() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2021");
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (DataDoesNotExist e) {
			
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationQueryByCCCandNAFsWrongLiqOrigin() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.GENERADAS_POR_LA_TGSS, "111016467058", "111008520536", "gwt354"));
		} catch (DataDoesNotExist e) {
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationQueryByCCCandNAFsWrongRegime() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.ESPECIAL_MAR_GRUPO_1, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (WrongRegimeException e) {
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationQueryByCCCandNAFsWrongCCC() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534311", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (InvalidCccException e) {
			
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationQueryByCCCandNAFsGrantsAndBonuses() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "jg@FNMT", "pkcs12", "01105577910", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "011017250195"));
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	
	@Test
	public void testWorkersCalculationOk() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Map<String, Map<String,Map<Period, Map<String, Calc>>>> liq=Calculations.workersCalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
//			for (Map<String, WorkerLiquidation> map : liq.values()) {
//				Iterator<String> it=map.keySet().iterator();
//				while(it.hasNext()) {
//					System.out.println(map.get(it.next()));
//				}
//			}
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	@Test
	public void testWorkersCalculationWrongRegime() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Map<String, Map<String,Map<Period, Map<String, Calc>>>> liq=Calculations.workersCalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.ESPECIAL_MAR_GRUPO_1, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
//			for (Map<String, WorkerLiquidation> map : liq.values()) {
//				Iterator<String> it=map.keySet().iterator();
//				while(it.hasNext()) {
//					System.out.println(map.get(it.next()));
//				}
//			}
		} catch (WrongRegimeException e) {
			
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationWrongCCC() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Map<String, Map<String,Map<Period, Map<String, Calc>>>> liq=Calculations.workersCalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105340062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
//			for (Map<String, WorkerLiquidation> map : liq.values()) {
//				Iterator<String> it=map.keySet().iterator();
//				while(it.hasNext()) {
//					System.out.println(map.get(it.next()));
//				}
//			}
		} catch (InvalidCccException e) {
			
		}  catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationWrongDate() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) +1);
			d = cal.getTime();
			Map<String, Map<String,Map<Period, Map<String, Calc>>>> liq=Calculations.workersCalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.L00_NORMAL, SistemaRED.LiquidationOrigin.TODAS);
//			for (Map<String, WorkerLiquidation> map : liq.values()) {
//				Iterator<String> it=map.keySet().iterator();
//				while(it.hasNext()) {
//					System.out.println(map.get(it.next()));
//				}
//			}
		} catch (InvalidDateException e) {
			
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		}
	}

	
	@Test
	public void testWorkersCalculationByCCCandNAFsOk() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(Calculations.workersCalculationByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "111008520536"));
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	@Test
	public void testWorkersCalculationByCCCandNAFsMultpleNafsOk() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(Calculations.workersCalculationByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354").toString());
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	@Test
	public void testWorkersCalculationByCCCandNAFsMultpleNafsWrongDate() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-"+(Calendar.getInstance().get(Calendar.YEAR)+2));
			System.out.println(Calculations.workersCalculationByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (InvalidDateException e) {
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationByCCCandNAFsMultpleNafsDateWithNoData() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2006");
			System.out.println(Calculations.workersCalculationByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (DataDoesNotExist e) {
			System.out.println("entra");
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationByCCCandNAFsMultpleNafsWrongRegime() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(Calculations.workersCalculationByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", SistemaRED.Regime.ESPECIAL_MAR_ASIMILADOS_GRUPO_1, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (WrongRegimeException e) {
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testWorkersCalculationByCCCandNAFsMultpleNafsWrongCCC() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("AyudaTFNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(Calculations.workersCalculationByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11177534302", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (InvalidCccException e) {
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	
	@Test
	public void testWorkersCalculationByCCCandNAFsOriginalCert() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			System.out.println(Calculations.workersCalculationByCCCandNAFS(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", SistemaRED.Regime.GENERAL, d, d, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, "010019805355", "011001022503", "011005185924"));
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (StatusCodeException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}

}
