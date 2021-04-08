package solutions.aon;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import solutions.aon.seg.social.SistemaREDI;
import solutions.aon.seg.social.SistemaREDI.LiquidationOrigin;
import solutions.aon.seg.social.SistemaREDI.LiquidationType;
import solutions.aon.seg.social.SistemaREDI.Regime;
import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.OutOfServiceException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.DataDoesNotExist;
import solutions.aon.seg.social.exception.invalid.LiquidationDoesNotExist;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.exception.invalid.invalidCccException;
import solutions.aon.seg.social.object.Liquidation;
import solutions.aon.seg.social.object.WorkerLiquidation;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class TestCalculationQuery {

	@Test
	public void testCalculationQueryOk() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-08-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			for (Liquidation liquidation : liq) {
				System.out.println(liq);
			}
		}catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	@Test
	public void testCalculationQueryGrantsAndBonuses() {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("FNMT.p12")){
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-12-2020");
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105577910", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			for (Liquidation liquidation : liq) {
				System.out.println(liq);
			}
		}catch (FailingHttpStatusCodeException e) {
			
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
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			System.out.println(liq);
			fail("Shouldn't throw results");
		}catch (FailingHttpStatusCodeException e) {
			
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
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.GENERADAS_POR_LA_TGSS);
			System.out.println(liq);
			fail("Shouldn't throw results");
		}catch (FailingHttpStatusCodeException e) {
			
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
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", Regime.GENERAL_ARTISTAS, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			System.out.println(liq);
			fail("Shouldn't throw results");
		}catch (FailingHttpStatusCodeException e) {
			
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
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			System.out.println(liq);
			fail("Shouldn't throw results");
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
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105369062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			System.out.println(liq);
			fail("Shouldn't throw results");
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (invalidCccException e) {
			
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
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			fail("Shouldn't end");
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
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FMT", "pkcs12", "01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			fail("Shouldn't end");
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
			Collection<Liquidation> liq=SistemaREDI.CalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkc12", "01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			fail("Shouldn't end");
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
			Map<String,Map<String,WorkerLiquidation>> liq=SistemaREDI.workersCalculationQueryByCCC(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			for (Map<String, WorkerLiquidation> map : liq.values()) {
				Iterator<String> it=map.keySet().iterator();
				while(it.hasNext()) {
					System.out.println(map.get(it.next()));
				}
			}
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
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
			WorkerLiquidation liq=SistemaREDI.WorkerCalculationQueryByNAF(certificateInputStream, "jg@FNMT", "pkcs12","011005185924" ,"01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			System.out.println(liq);
		}catch (FailingHttpStatusCodeException e) {
			
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
			WorkerLiquidation liq=SistemaREDI.WorkerCalculationQueryByNAF(certificateInputStream, "jg@FNMT", "pkcs12","naf_falso" ,"01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			fail("Shouldn't find anything");
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
			Collection<WorkerLiquidation> liq=SistemaREDI.WorkerCalculationQueriesByNAF(certificateInputStream, "jg@FNMT", "pkcs12","011005185924" ,"01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			for (WorkerLiquidation workerLiquidation : liq) {
				System.out.println(liq);
			}
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
			Collection<WorkerLiquidation> liq=SistemaREDI.WorkerCalculationQueriesByNAF(certificateInputStream, "jg@FNMT", "pkcs12","naf_falso" ,"01105360062", Regime.GENERAL, d, d, LiquidationType.L00_NORMAL, LiquidationOrigin.TODAS);
			fail("Shouldn't find anything");
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
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", Regime.GENERAL, d, d, LiquidationType.TODAS, LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
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
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "jg@FNMT", "pkcs12", "01105360062", Regime.GENERAL, d, d, LiquidationType.TODAS, LiquidationOrigin.TODAS, "010019805355", "011001022503", "011005185924"));
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
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
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", Regime.GENERAL, d, d, LiquidationType.TODAS, LiquidationOrigin.TODAS));
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
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
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", Regime.GENERAL, d, d, LiquidationType.L03_COMP_ABONO_SALARIOS_CARACTER_RETROACTIV, LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (DataDoesNotExist e) {
			
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
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
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", Regime.GENERAL, d, d, LiquidationType.TODAS, LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (DataDoesNotExist e) {
			
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
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
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", Regime.GENERAL, d, d, LiquidationType.TODAS, LiquidationOrigin.GENERADAS_POR_LA_TGSS, "111016467058", "111008520536", "gwt354"));
		} catch (DataDoesNotExist e) {
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
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
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534302", Regime.ESPECIAL_MAR_GRUPO_1, d, d, LiquidationType.TODAS, LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (WrongRegimeException e) {
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
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
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "123456", "pkcs12", "11122534311", Regime.GENERAL, d, d, LiquidationType.TODAS, LiquidationOrigin.TODAS, "111016467058", "111008520536", "gwt354"));
		} catch (invalidCccException e) {
			
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
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
			System.out.println(SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, "jg@FNMT", "pkcs12", "01105577910", Regime.GENERAL, d, d, LiquidationType.TODAS, LiquidationOrigin.TODAS, "011017250195"));
		} catch (OutOfServiceException e) {
			System.err.println(e.getMessage()+"\n\t"+e.getCause().getMessage());
		} catch (FailingHttpStatusCodeException e) {
			
		} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			fail(""+e.getClass());
		} catch (ParseException e) {
			fail("Test date fails");
		}
	}
	
	
	
//	public static void main(String[] args) {
//		try {
//		throw new SegSocialException("Aplicación Cerrada temporalmente.", new SegSocialException("La aplicación SLD Cotización se encuentra en estado cerrado. Motivo : La aplicación estará fuera de servicio hasta el día 03/02/2021 a las 12 horas. La fecha prevista para la próxima apertura es: 03/02/2021 12:00."));
//		} catch ( Exception  e ) {
//			
//			System.out.println(e.getMessage() + e.getCause().getMessage());
//			
//		}
//	}

}
