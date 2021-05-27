package solutions.aon;

import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import org.junit.Ignore;
import org.junit.Test;
import aon.sepe.objects.Contract;
import aon.sepe.objects.Contract.ContractBuilder;
import aon.sepe.objects.Contract.OfferType;
import aon.sepe.objects.Contract.SexType;
import solutions.aon.sepe.Contrato;
import solutions.aon.sepe.Contrato.FirmType;

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
			.setIpf("16262835H")
			.setName("JULIO")
			.setSurname("GARCIA")
			.setLastSurname("PEREZ")
			.setDateBirth(fnac)
			.setSex(SexType.HOMBRE)
			.setCodNationality(862)
			.setCodPaisDom(724)
			.setCodMunDom("01059") 
			.setNss("010022757387")
//			.setTypeJnd()
			.setCodContract("401")
			.setDateIniContract(fini)
			.setCodFormativo(59)//review
			.setCodOccupation(1311)//review
			.setCodPaisWork(724)
			.setCodMunWork("01059")
			.setOffer(OfferType.NO) //review
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
			String ipf = "16262835H";
			@SuppressWarnings("deprecation")
			Date fini = new Date("2021/01/28");
			Date ffin = fini;
			String workAddress = "CALLE WELLINGM, ALAVA";
			String restContract = "segun convenio";
			Contrato.contratoCopyBasic(certificateInputStream, certificatePassword, certificateType, ipf, fini, ffin, FirmType.NO_FACILITADO_COPIA, workAddress, restContract);
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
			String ipf = "16262835H";
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
			String ipf = "16262835H";
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
			String ipf = "16262835H";
			byte[] pdf = Contrato.transformacionsPdf(certificateInputStream, certificatePassword, certificateType, ipf, fecha);
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void removeContrato() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			String ide = "0120210005805";
			Contrato.removeContrato(certificateInputStream, certificatePassword, certificateType, ide);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testGetContratoData() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			@SuppressWarnings("deprecation")
			Date fini =  new Date("2020/01/09");
			@SuppressWarnings("deprecation")
			Date fend =  new Date("2020/09/09");
			String ipf = "Y7514970X";
			Contract contract = Contrato.getContratoData(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
			System.out.println(contract.getSepeId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
