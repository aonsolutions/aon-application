package solutions.aon;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import aon.sepe.objects.Certificates;
import aon.sepe.objects.QuoteData;
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
			.setIpf("xxxx72740703Y")
			.setIpfManager("72740703Y")
			.setName("TEST")
			.setSurname("TEST")
			.setLastSurname("TEST")
			.setTypeContract("502")
			.setGz("09")
			.setDurationContract(2)
			.setTypeDuration(TypeDuration.DIAS)
			.setCatProfessional("9210")
			.setCauseSuspension("22")
			.setfAEd(new Date("2021/01/01"))
			.setfSTd(new Date("2021/09/05"))
			.setDaysCtzVc(1)
			.setBcccVc(0.00)
			.setBcdVc(0.00);
			
			List<Map<String, String>> dataCtz = new ArrayList<Map<String, String>>();

			dataCtz.add(parseMap("2021", "10", "1", "11.1", "11.1"));
		
			dataCtz.add(parseMap("2021", "09", "1", "11.17", "11.17"));

			dataCtz.add(parseMap("2021", "05", "1", "10.81", "10.81"));
			
			dataCtz.add(parseMap("2021", "04", "1", "11.17", "11.17"));
			
			dataCtz.add(parseMap("2021", "03", "1", "11.10", "11.10"));
			
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
		
		List<QuoteData> quoteDatas = new LinkedList<>();
		
		quoteDatas.add(parseQuoteData(2021, 01, 20, 25.20, 25.10));
		quoteDatas.add(parseQuoteData(2020, 11, 20, 10.55, 15.44));
		
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
		.setBcccVc(1200.00)
		.setQuoteData(quoteDatas)
		.setBcdVc(1200.00);
		
		Certificates certificate = bd.build();

		for(QuoteData data: certificate.getQuoteData()) {
			System.out.println(data.toString());
		}
	}
	
	
	private static QuoteData parseQuoteData(Integer anio, Integer month, Integer days, Double bccc, Double bcd) {
		return new QuoteData()
		.setAnio(anio)
		.setMonth(month)
		.setDays(days)
		.setBccc(bccc)
		.setBcd(bcd);
	}
	
	private static Map<String, String> parseMap(String anio, String month, String days, String bccc, String bcd) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("anioCtz", anio);
		map.put("monthCtz", month);
		map.put("daysCtz", days);
		map.put("bccc", bccc);
		map.put("bcd", bcd);
		return map;
	}
	
}
