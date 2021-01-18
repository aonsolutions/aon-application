package solutions.aon;

import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import org.junit.Ignore;
import org.junit.Test;
import aon.sepe.objects.Contract;
import aon.sepe.objects.Contract.ContractBuilder;
import solutions.aon.sepe.Contrato;
import solutions.aon.sepe.Contrato.TypeFirm;

public class TestContrato {
	
	@Test
	@Ignore
	public void testContrato() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			@SuppressWarnings("deprecation")
			Date fnac = new Date("1994/07/18");
			@SuppressWarnings("deprecation")
			Date fini = new Date("2021/01/28");
			ContractBuilder bd = new ContractBuilder();
			bd.setRegimen("0111")
			.setCtaCti("01105360062")
			.setCifEnterprise("B01487271")
			.setIpf("Y7514970X")
			.setName("RAY")
			.setSurname("VASQUEZ")
			.setLastSurname("BEAUPERTHUY")
			.setDateBirth(fnac)
			.setSex(1)
			.setCodNationality(862)
			.setCodPaisDom(724)
			.setCodMunDom("01059") //review
			.setNss("291136796369")
//			.setTypeJnd("M")
			.setCodContract("401")
			.setDateIniContract(fini)
			.setCodFormativo(59)//review
			.setCodOccupation(1311)//review
			.setCodPaisWork(724)
			.setCodMunWork("01059")
			.setOffer(false) //review
			;
			Contract cto = bd.build();
			String ide = Contrato.contrato(certificateInputStream, certificatePassword, certificateType,  cto);
			System.out.println("ide: "+ide);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testCopyBasic() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			String ipf = "Y7514970X";
			@SuppressWarnings("deprecation")
			Date fini = new Date("2021/01/28");
			Date ffin = fini;
			String workAddress = "CALLE WELLINGM, ALAVA";
			String restContract = "segun convenio";
			Contrato.contratoCopyBasic(certificateInputStream, certificatePassword, certificateType, ipf, fini, ffin, TypeFirm.NO_FACILITADO_COPIA, workAddress, restContract);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testContratoPdf() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			@SuppressWarnings("deprecation")
			Date fini =  new Date("2021/01/28");
			@SuppressWarnings("deprecation")
			Date ffin =  new Date("2021/01/28");
			String ipf = "Y7514970X";
			byte[] pdf = Contrato.contratoPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, ffin);
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testGetCopyBasicPdf() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			@SuppressWarnings("deprecation")
			Date fini =  new Date("2020/09/09");
			@SuppressWarnings("deprecation")
			Date ffin =  new Date("2021/01/01");
			String ipf = "Y7514970X";
			byte[] pdf = Contrato.getCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, ffin);
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testTransformacionsPdf() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			@SuppressWarnings("deprecation")
			Date fecha =  new Date("2016/05/06");
			String ipf = "Y7514970X";
			byte[] pdf = Contrato.transformacionsPdf(certificateInputStream, certificatePassword, certificateType, ipf, fecha);
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void anulacionContrato() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			String ide = "0120210005805";
			Contrato.removeContrato(certificateInputStream, certificatePassword, certificateType, ide);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
