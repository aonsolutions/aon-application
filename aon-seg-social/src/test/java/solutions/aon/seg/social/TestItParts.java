package solutions.aon.seg.social;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.InvalidDateException;
import solutions.aon.seg.social.object.ITPart;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.object.Period;
import solutions.aon.seg.social.toolkit.Toolkit;

public class TestItParts {
	
	Date unreachableDate = Toolkit.getUnreachableDate();

	@Test
	@Ignore
	public void testGetItsCertificateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			Collection<It> its = SistemaREDITPart.getIts(certificateInputStream, 
					"jg@FNMT", "pkcs12", "0111","01105360062", 
					Toolkit.parseDate("01-01-2015", "dd-MM-yyyy"), Toolkit.parseDate("01-01-2020", "dd-MM-yyyy"),
					Optional.empty()
			);
			for (It it : its) {
				System.out.println("BAJA >> "+it.getStart());
				System.out.println("ALTA >> "+it.getEnd());
				System.out.println("CONFIRMACION >> "+it.getConfirmations().toString());
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	@Test
	@Ignore
	public void testGetCalc() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {	
			Date dateFrom = Toolkit.addMonth(new Date(), -1);
			Date dateTo =  dateFrom;
			List<Period> periods = new ArrayList<>();
			
			ServicioRED.workersCalculationByCCCandNAFsPOST(
				certificateInputStream, "jg@FNMT", "pkcs12", 
				"01105360062", SistemaRED.Regime.GENERAL, dateFrom, dateTo, SistemaRED.LiquidationType.L00_NORMAL, 
				SistemaRED.LiquidationOrigin.TODAS, "291136796369" 
				)
			.values().stream().filter(x->null!=x)
			.forEach(v->v.values().stream().filter(x->null!=x)
			.forEach( p->{
				List<Period> list = p.keySet().stream().filter(x->null!=x).collect(Collectors.toList());
				if(list.size()>0)
					periods.addAll(list);
			}));
	
			int periodsSize = periods.size();
			Double quoteDays = 0.00;
			Double baseCc = 0.00;
			Double baseAt = 0.00;
			
//			Period [startDate=Wed Dec 01 00:00:00 CET 2021, endDate=Fri Dec 31 00:00:00 CET 2021, hours=null, baseCC=2245.0, baseAT=2245.0, quoteDays=30.0]
//
			for (Period period : periods) {
				if(null!=period.getQuoteDays())
					quoteDays += period.getQuoteDays();
				if(null!=period.getBaseCC())
					baseCc += period.getBaseCC();
				if(null!=period.getBaseAT())
					baseAt += period.getBaseAT();
			}
			
			quoteDays = quoteDays / periodsSize;
			baseCc = baseCc / periodsSize;
			baseAt = baseAt / periodsSize;
			
			System.out.println("quoteDays: "+quoteDays+" baseCc: "+baseCc+" baseAt: "+ baseAt);
		
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	@Test
	@Ignore
	public void registerItBaja() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaREDITPart.registerItBaja(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "011017250195", 
					SistemaRED.Contingencies.ACCIDENT_LABORAL, SistemaRED.SituationEmployee.ACTIVO,
					new Date(), SistemaRED.ContractType.RESTO_Y_AUTONOMOS, (float) 844.38, 30, Optional.of(new Date()), Optional.empty(),
					Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	@Ignore
	public void registerItConfirmation() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaREDITParts.registerItConfirmation(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "011011187190", 
					SistemaRED.Contingencies.ENFERMEDAD_COMUN, SistemaRED.SituationEmployee.ACTIVO, Optional.empty(), Optional.empty(),
					 Toolkit.addDays(new Date(), -1), new Date(), Optional.empty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void registerItAlta() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			System.out.println("baja:"+new Date("2021/12/27")+" alta:"+new Date("2022/01/03"));
			SistemaREDITParts.registerItAlta(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "011011187190", 
					SistemaRED.Contingencies.ENFERMEDAD_COMUN, SistemaRED.SituationEmployee.ACTIVO,
					new Date("2021/12/27"), new Date("2022/01/03"), Optional.empty(), Optional.empty(),  SistemaRED.CauseType.CURACION, Optional.empty(), Optional.empty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	@Ignore
	public void removeIt() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaREDITPart.removeIt(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "011011187190", SistemaRED.PartType.ALTA, new Date(), new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void pdfIt() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			@SuppressWarnings("deprecation")
			byte[] pdf = SistemaREDITParts.pdfIt(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "011011187190", SistemaRED.PartType.BAJA, new Date("2021/01/12"), new Date("2021/01/12"));
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void getDataIt() {
//		 new Date("2016/04/23")
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			 @SuppressWarnings("deprecation")
			Date fecha_baja = new Date("2016/04/20");
			 @SuppressWarnings("deprecation")
			Date fecha_proceso = new Date("2016/04/20");
			 ITPart itPart = SistemaREDITParts.getDataIt(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "011011187190", SistemaRED.PartType.BAJA, fecha_baja, fecha_proceso);
			 System.out.println(itPart);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	@Ignore
	public void testGetItsUnreachableDateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITPart.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), unreachableDate, Optional.empty());
		}
		catch (InvalidDateException | StatusCodeException e) {} catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}
	
	@Test
	@Ignore
	public void testGetItsEmptyDateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITPart.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), null,  Optional.empty());
		}
		catch (InvalidDataException | StatusCodeException e) {} 
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}

	@Test
	@Ignore
	public void testGetItsRegimeTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITPart.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0211","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), new Date(), Optional.empty());
		}
		catch (StatusCodeException | InvalidDataException e) {} catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}
	
	@Test
	@Ignore
	public void testGetItsRegimeEmptyTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITPart.getIts(certificateInputStream, "jg@FNMT", "pkcs12", null,"01105360062", Toolkit.parseDate("1-12-2015", "dd-MM-yyyy"), new Date(), Optional.empty());
		}
		catch (StatusCodeException | InvalidDataException e) {} catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}
	
	@Test
	@Ignore
	public void testGetItsCccTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITPart.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","0X105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), new Date(), Optional.empty());
		}
		catch (StatusCodeException | InvalidDataException e) {} catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}	
	
}
