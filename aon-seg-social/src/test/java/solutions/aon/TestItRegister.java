package solutions.aon;


import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.SistemaRED_ITParts;
import solutions.aon.seg.social.SistemaRED_ITParts.Contingencies;
import solutions.aon.seg.social.SistemaRED_ITParts.ContractType;
import solutions.aon.seg.social.SistemaRED_ITParts.PartType;
import solutions.aon.seg.social.SistemaRED_ITParts.SituationEmployee;
//import solutions.aon.seg.social.SistemaRED_ITParts.TypeAccident;
import solutions.aon.seg.social.objects.ITPart;

public class TestItRegister {
	@Test
	@Ignore
	public void registerItBaja() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaRED_ITParts.registerItBaja(certificateInputStream,"jg@FNMT","pkcs12", 
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
			SistemaRED_ITParts.removeIt(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "291136796369", PartType.ALTA, new Date(), new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void registerItConfirmation() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaRED_ITParts.registerItConfirmation(certificateInputStream,"jg@FNMT","pkcs12", 
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
//		CAUSA POSIBLES VALORES
//		value: 01 Text: 1 Curación
//		value: 02 Text: 2 Fallecimiento
//		value: 03 Text: 3 Inspección médica
//		value: 04 Text: 4 Propuesta invalidez
//		value: 05 Text: 5 Agotamiento plazo
//		value: 06 Text: 6 Mejoría permite trabajar
//		value: 07 Text: 7 Incomparecencia
//		value: 10 Text: 10 Control INSS - 12 Meses
//		value: 17 Text: 17 Recup. Capacidad prof.
//		value: 18 Text: 18 Incomp. (Ctos. form.)
//		value: 20 Text: 20 Inicio de Maternidad
//		value: 53 Text: 53 Alta médica inspección INSS
//		value: 55 Text: 55 Propuesta de IP en INSS
//		value: 56 Text: 56 Fallecimiento comunicado desde el INSS
		String causa = "01";
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaRED_ITParts.registerItAlta(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "291136796369", 
					Contingencies.ENFERMEDAD_COMUN, SituationEmployee.ACTIVO, Optional.empty(), Optional.empty(),
					new Date(), new Date(), Optional.empty(), Optional.empty(),  causa);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void pdfIt() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			byte[] pdf = SistemaRED_ITParts.pdfIt(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "291136796369", PartType.BAJA, new Date("2021/01/12"), new Date("2021/01/12"));
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
//	@Ignore
	public void getDataIt() {
//		 new Date("2016/04/23")
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			 Date fecha_baja = new Date("2016/04/20");
			 Date fecha_proceso = new Date("2016/04/20");
			 ITPart itPart = SistemaRED_ITParts.getDataIt(certificateInputStream,"jg@FNMT","pkcs12", 
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
