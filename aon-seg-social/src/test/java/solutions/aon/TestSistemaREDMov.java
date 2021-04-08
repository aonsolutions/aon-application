package solutions.aon;

import static org.junit.Assert.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import org.junit.Ignore;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import solutions.aon.seg.social.SistemaREDMov;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.NotExistingYetException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;

public class TestSistemaREDMov {

	@Test
	@Ignore
	public void testSendAlta() {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			Calendar c=Calendar.getInstance();
			c.add(Calendar.DATE, 8);
			Date fecha=c.getTime();
			EmployeeBuilder builder = new EmployeeBuilder();
			Employee employee = builder
			.setRegime("0111")
			.setCtaCti("01105360062")
			.setNss("010022757387")
			.setIpf("16262835H")
			.setFra(fecha)
			.setOcup("e")
			.setColec("60888888888888")
			.setGc("03")
			.setContract("401")
			.build();
			SistemaREDMov.sendAlta(certificateInputStream, "jg@FNMT", "pkcs12", employee);
		} catch (NotExistingYetException e) {} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}

}
