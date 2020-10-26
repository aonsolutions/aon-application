package solutions.aon.seg.social;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Test;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.invalidCccException;
/*@RunWith(JUnit4.class)
public class TestSRI {
	@Test(expected=invalidCccException.class)
	public void testGetSituacionEmpresaOk() {
		InputStream certificateInputStream = new FileInputStream(args[0]));
		SegSocialException thrown=assertThrows(invalidCccException.class, () -> SistemaRED_I.I.getSituacionEmpresa(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105370062"));
		SistemaRED_I.getSituacionEmpresa(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105370062");
				
	}*/
	
	
	
	
	
	
	
	
	
	
	
	@RunWith(Parameterized.class)
	public class TestSRI {
		private SegSocialException excepcion;
		private String certKey;
		private String certType;
		private String regime;
		private String ccc;
		
	}
			@Parameters
			public static Collection<Object[]> data() {
				return Arrays.asList(new Object[][] {
					//{"jg@FNMT", "pkcs12", "0111", "01105360062"},
					{"jg@FNMT", "pkcs12", "0111", "01105362042", new invalidCccException()}
					//{"jg@FNMT", "pkcs12", "0511", "01105360062"},
					//{"jg@FNMT", "pDcs12", "0511", "01105360062"}
				});
			}
			
			
			public TestSistemaRED_I(String certKey, String certType, String regime, String ccc, exception) {
				super();
				this.certKey=certKey;
				this.certType=certType;
				this.regime=regime;
				this.ccc=ccc;
				this.exception=exception;
			}
			@Test
			public void testingWrongParameters() {
				SistemaRED_I sri=new TestSistemaRED_I();
					assertThrows(exception, sri.getSituacionEmpresa(new FileInputStream(args[0]), certificateInputStream, certKey, certType, regime, ccc), exception);
			}

}
