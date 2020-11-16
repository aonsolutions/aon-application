package solutions.aon;


import java.io.InputStream;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.SistemaREDITParts;
import solutions.aon.seg.social.SistemaREDITParts.Contingencies;
import solutions.aon.seg.social.SistemaREDITParts.ContractType;

public class TestItRegister {
 
	@Test

	public void registerItCertificateTest() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			SistemaREDITParts.addItStart(certificateInputStream,"jg@FNMT","pkcs12", "0111", "01105360062", "291136796369", Contingencies.ENFERMEDAD_COMUN, "1342341245", "0123456789", new Date(), ContractType.RESTO_Y_AUTONOMOS,2.3f,23);
		} catch (Exception e) {System.err.println(e);}
	}
	
	@Test
	@Ignore
	public void registerItTestRegime() {
		
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
	public void addItConfirmationCertificateTest(){

	}
}
