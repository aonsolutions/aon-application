package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.SegSocialException;

public class SistemaRED {

	public SistemaRED() {}
	
	public static Collection<Employee> getEmployees(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException 
	{
		return SistemaRedEmployee.getEmployees(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
	}

	
	
	private static void evalSwitch(FailingHttpStatusCodeException e) throws ForbiddenException, SegSocialException {
		switch (e.getStatusCode()) {
		case 403:
			throw new ForbiddenException();
		default:
			throw new SegSocialException(e);
		}
	}

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

	}

}
