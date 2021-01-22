package solutions.aon;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import aon.sepe.objects.Certificates;
import aon.sepe.objects.Certificates.CertificatesBuilder;
import aon.sepe.objects.Certificates.TypeDuration;
import solutions.aon.sepe.Certificado;
import solutions.aon.sepe.exceptions.SepeException;

public class TestCertificado {
	
	@Test
	@Ignore
	public void testCertEnterprisePdf() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			String nif = "72740703Y";
			@SuppressWarnings("deprecation")
			Date fecha =  new Date("2016/05/06");
			byte[] pdf = Certificado.certEnterprisePdf(certificateInputStream, certificatePassword, certificateType, nif, fecha);
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	@Ignore
	public void testCertEnterprise() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			CertificatesBuilder bd  = new CertificatesBuilder();
			bd.setRegimen("0111")
			.setCtaCti("01105360062")
			.setIpf("16262835H")
			.setIpfManager("16262835H")
			.setName("TEST")
			.setSurname("APELLIDO 1")
			.setLastSurname("APELLIDO 2")
			.setTypeContract("501")
			.setGz("06")
			.setDurationContract(30)
			.setTypeDuration(TypeDuration.DIAS)
			.setCatProfessional("2722")
			.setCauseSuspension("33")
			.setfAEd(new Date("2020/01/01"))
			.setfSTd(new Date())
			.setDaysCtzVc(30)
			.setBcccVc("000001200")
			.setBcdVc("000001200");
			
			List<Map<String, String>> dataCtz = new ArrayList<Map<String, String>>();
			Map<String, String> values = new HashMap<String, String>();
			values.put("anioCtz","2021");
			values.put("monthCtz","01");
			values.put("daysCtz","20");
			values.put("bccc","000001200");
			values.put("bcd","000001200");
			dataCtz.add(values);
			Map<String, String> values2 = new HashMap<String, String>();
			values2.put("anioCtz","2020");
			values2.put("monthCtz","11");
			values2.put("daysCtz","10");
			values2.put("bccc","000000800");
			values2.put("bcd","000000800");
			dataCtz.add(values2);
			
			bd.setDataCtz(dataCtz);
			
			Certificates certificates = bd.build();
			
			Certificado.certEnterprise(certificateInputStream, certificatePassword, certificateType, certificates);
		} catch (SepeException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	@Ignore
	public void testValues() {
		CertificatesBuilder bd  = new CertificatesBuilder();
		bd.setRegimen("0111")
		.setCtaCti("01105360062")
		.setIpf("16262835H")
		.setIpfManager("16262835H")
		.setName("TEST")
		.setSurname("APELLIDO 1")
		.setLastSurname("APELLIDO 2")
		.setTypeContract("501")
		.setGz("06")
		.setDurationContract(30)
		.setTypeDuration(TypeDuration.DIAS)
		.setCatProfessional("2722")
		.setCauseSuspension("33")
		.setfAEd(new Date("2020/01/01"))
		.setfSTd(new Date())
		.setDaysCtzVc(30)
		.setBcccVc("000001200")
		.setBcdVc("000001200");
		List<Map<String, String>> dataCtz = new ArrayList<Map<String, String>>();
		Map<String, String> values = new HashMap<String, String>();
		
		values.put("anioCtz","2021");
		values.put("monthCtz","01");
		values.put("daysCtz","20");
		values.put("bccc","000001200");
		values.put("bcd","000001200");
		dataCtz.add(values);
		Map<String, String> values2 = new HashMap<String, String>();
		values2.put("anioCtz","2020");
		values2.put("monthCtz","11");
		values2.put("daysCtz","20");
		values2.put("bccc","000001200");
		values2.put("bcd","000001200");
		dataCtz.add(values2);
		
		bd.setDataCtz(dataCtz);
		System.out.println(bd.build());
		for(Map<String, String> ctz: dataCtz) {
			System.out.println(ctz);
		}

	}
	
	
}
