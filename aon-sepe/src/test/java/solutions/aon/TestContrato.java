package solutions.aon;

import java.io.InputStream;
import java.util.Base64;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import aon.sepe.objects.Contract;
import aon.sepe.objects.Contract.ContractBuilder;
import solutions.aon.sepe.Contrato;

public class TestContrato {
	
	@Test
	@Ignore
	public void testContrato() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";

			ContractBuilder bd = new ContractBuilder();
			bd.setCifEnterprise("B01487271")
			.setRegimen("0111")
			.setCtaCti("01105360062")
			.setIpf("Y7514970X")
			.setName("RAY")
			.setSurname("VASQUEZ")
			.setLastSurname("BEAUPERTHUY")
			.setSex(1)
			.setCodNationality(862)
			.setCodPaisDom(724)
			.setCodMunDom("01059") //review
			.setNss("291136796369")
//			.setTypeJnd("M")
			.setCodContract("401")
			.setDateIniContract(new Date("2021/01/01"))
			.setCodFormativo(59)//review
			.setCodOccupation(1311)//review
			.setCodPaisWork(724)
			.setCodMunWork("01059")
			.setOffer(false) //review
			;
			Contract cto = bd.build();
			Contrato.contrato(certificateInputStream, certificatePassword, certificateType,  cto);
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
			Date fini =  new Date("2020/09/09");
			@SuppressWarnings("deprecation")
			Date ffin =  new Date("2021/01/01");
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

}
