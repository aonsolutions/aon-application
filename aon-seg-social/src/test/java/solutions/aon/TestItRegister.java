package solutions.aon;


import java.io.InputStream;
import java.util.Date;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.SistemaRED_ITParts;
import solutions.aon.seg.social.SistemaRED_ITParts.Contingencies;
import solutions.aon.seg.social.SistemaRED_ITParts.ContractType;

public class TestItRegister {
	@Ignore
	@Test
	public void registerItBaja() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaRED_ITParts.registerItBaja(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "291136796369", 
					Contingencies.ENFERMEDAD_COMUN, Optional.of("1342341245"), Optional.of("00000001"), 
					new Date(), ContractType.RESTO_Y_AUTONOMOS, (float) 2.3, 23);
		} catch (Exception e) {System.err.println(e.getMessage());}
	}
	
	@Test
//	@Ignore
	public void removeIt() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaRED_ITParts.removeIt(certificateInputStream,"jg@FNMT","pkcs12", 
					"0111", "01105360062", "291136796369", new Date());
		} catch (Exception e) {System.err.println(e.getMessage());}
	}
	
	@Test
	@Ignore
	public void registerItTestRegimeEmpty() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestNaf() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestNafEmpty() {
		
	}
	
	@Test
	@Ignore
	public void registerItTestContigency() {
		
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
