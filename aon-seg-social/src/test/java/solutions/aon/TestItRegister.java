package solutions.aon;


import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;
import solutions.aon.seg.social.SistemaREDITParts;
import solutions.aon.seg.social.SistemaREDITParts.CauseType;
import solutions.aon.seg.social.SistemaREDITParts.Contingencies;
import solutions.aon.seg.social.SistemaREDITParts.ContractType;
import solutions.aon.seg.social.SistemaREDITParts.PartType;
import solutions.aon.seg.social.SistemaREDITParts.SituationEmployee;
import solutions.aon.seg.social.object.ITPart;

public class TestItRegister {
	@Test
	@Ignore
	public void registerItBaja() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaREDITParts.registerItBaja(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "291136796369", 
					Contingencies.ENFERMEDAD_COMUN, SituationEmployee.ACTIVO, Optional.empty(), Optional.empty(), Optional.of("9490"),
					new Date(), ContractType.RESTO_Y_AUTONOMOS, (float) 2.3, 23, Optional.of(new Date()), Optional.empty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void removeIt() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaREDITParts.removeIt(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "291136796369", PartType.ALTA, new Date(), new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void registerItConfirmation() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaREDITParts.registerItConfirmation(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "291136796369", 
					Contingencies.ENFERMEDAD_COMUN, SituationEmployee.ACTIVO, Optional.empty(), Optional.empty(),
					new Date(), new Date(), Optional.empty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void registerItAlta() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaREDITParts.registerItAlta(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "291136796369", 
					Contingencies.ENFERMEDAD_COMUN, SituationEmployee.ACTIVO, Optional.empty(), Optional.empty(),
					new Date(), new Date(), Optional.empty(), Optional.empty(),  CauseType.CURACION);
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
					"0111", "01105360062", "291136796369", PartType.BAJA, new Date("2021/01/12"), new Date("2021/01/12"));
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
					"0111", "01105360062", "011011187190", PartType.BAJA, fecha_baja, fecha_proceso);
			 System.out.println(itPart);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	@Ignore
	public void registerItTestContigency() {
		System.out.println("test");
	}
	
	@Test
	@Ignore
	public void registerItTestContigencyEmpty() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestPartType() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestPartTypeEmpty() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestStartDate() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestStartDateEmpty() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestEndDate() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestEndDateEmpty() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestEndCause() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestEndCauseEmpty() {
		
	}
	
	@Test 
	@Ignore
	public void addItConfirmationCertificateTest(){

	}
}
