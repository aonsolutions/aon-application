package solutions.aon.seg.social;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLOptionElement;

import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.objects.SecondaryUser;
import solutions.aon.seg.social.objects.SecondaryUser.SecondaryUserBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaRedSecondaryUser {

	//HANDLE SECONDARYUSERS EXCEPTIONS
	public static SecondaryUser getSecondaryUsers(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType,final String ipf) throws SegSocialException {
		
		try {
			return getSecondaryUsersImpl(certificateInputStream, certificatePassword, certificateType,ipf);			
		} catch (FailingHttpStatusCodeException e) {
			switch (e.getStatusCode()) {
			case 403:
				throw new ForbiddenException();
			default:
				throw new SegSocialException(e);
			}
		}
		catch (MalformedURLException e) {}
		catch (IOException e) {e.printStackTrace();}
		return null; 		
	}
	
	//GET THE SECONDARY USERS
	public static SecondaryUser getSecondaryUsersImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ipf) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=NRW67&E=I&AP=AUT");
						
			
			HtmlCheckBoxInput ch = htmlPage.querySelector("#chkgrupo1_2");
			htmlPage = ch.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage); 
			
			HtmlInput ipf_in = htmlPage.querySelector("#inputgrupo1_2_1");
			ipf_in.setAttribute("value",ipf);
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlSubmitInput continue_btn = htmlPage.querySelector("#Sub2207101004_46");
			htmlPage = continue_btn.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			
			//OBJECT CREATE
			String authoritation =  HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFAUTORIZ");
			String authoritation_entity = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFRAZSOCIALAUT");
			String main_user_name = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOMBREAUT");
			String main_user_ipf = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFIPFAUT");
			String main_user_naf = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNAFAUT");
			
			String name = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOMBRESEC");
			String province = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROVSEC");;
			String naf = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNAFSEC");;
			String situation = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFSITSEC");;
			Date situation_date = Toolkit.parseDate(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECSITSEC"),"dd/MM/yyyy");
			String telephone = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTELEFONOSEC");;
			String fax = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFAXSEC");;
			String mobile = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFMOVILSEC");;
			String mail = HtmlUnitToolkit.getTrimmedById(htmlPage, "txtconcat1_2");;
			
			SecondaryUserBuilder builder = new SecondaryUserBuilder();
			
			builder.setAuthoritation(authoritation)
			.setAuthoritation_entity(authoritation_entity)
			.setMain_user_name(main_user_name)
			.setMain_user_ipf(main_user_ipf)
			.setMain_user_naf(main_user_naf)
			.setName(name)
			.setProvince(province)
			.setNaf(naf)
			.setSituation(situation)
			.setSituation_date(situation_date)
			.setTelephone(telephone)
			.setFax(fax)
			.setMobile(mobile)
			.setMail(mail);
			
			SecondaryUser user = builder.build();
			return user;
		}
	}
	
	//MAIN
	public static void main(String[] args) {
		try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			try { System.out.println(getSecondaryUsers(certificateInputStream,"jg@FNMT", "pkcs12","044679529M"));}
			catch (SegSocialException e) {e.printStackTrace();}
		} 
		catch (FileNotFoundException e1) {} 
		catch (IOException e1) {}
	}
}
