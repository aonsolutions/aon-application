package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.objects.SecondaryUser;
import solutions.aon.seg.social.objects.SecondaryUser.SecondaryUserBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaRedSecondaryUser {

	//HANDLE SECONDARYUSERS EXCEPTIONS
	public static SecondaryUser getSecondaryUserByIpf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType,final String ipf) throws SegSocialException {
		Toolkit.verifyData(new Object[]{ipf});
		try {
			return getSecondaryUserByIpfImpl(certificateInputStream, certificatePassword, certificateType,ipf);			
		} catch (FailingHttpStatusCodeException e) {
			switch (e.getStatusCode()) {
				case 403:	throw new ForbiddenException();
				default:	throw new SegSocialException(e);
			}
		}
		catch (MalformedURLException e) {throw new SegSocialException(e);}
		catch (IOException e) {throw new SegSocialException(e);}		
	}
	
	//GET THE SECONDARY USER BY IPF
	public static SecondaryUser getSecondaryUserByIpfImpl(final InputStream certificateInputStream, final String certificatePassword,
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
			
			return getSecondaryUserInfo(htmlPage);

		}
	}
	
	public static SecondaryUser getSecondaryUserInfo(HtmlPage htmlPage) {
		
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
	
	//HANDLE EXCEPTIONS OF 
	public static Collection<SecondaryUser> getSecondaryUsers(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws SegSocialException{
		try {return getSecondaryUsersImpl(certificateInputStream, certificatePassword, certificateType);}
		catch (FailingHttpStatusCodeException e) {throw new SegSocialException(e);}
		catch (MalformedURLException e) {throw new SegSocialException(e);}
		catch (IOException e) {throw new SegSocialException(e);}	
	}
	
	//GET SECONDARY USERS 
	public static Collection<SecondaryUser> getSecondaryUsersImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SegSocialException{
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=NRW67&E=I&AP=AUT");
		
			HtmlCheckBoxInput ch = htmlPage.querySelector("#chkgrupo1_1");
			htmlPage = ch.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage); 
			
			HtmlSubmitInput btn = htmlPage.querySelector("#Sub2207101004_46");
			htmlPage = btn.click();			
			
			DomNodeList<DomNode> checkboxes = htmlPage.querySelectorAll("#Sub0800010080>tbody>tr input[type=checkbox]");
			for (DomNode checkbox : checkboxes) {
				HtmlCheckBoxInput check = (HtmlCheckBoxInput) checkbox;
				htmlPage = check.click();
			}
			
			HtmlSubmitInput query_btn = htmlPage.querySelector("#Sub2205301004_72");
			htmlPage = query_btn.click();
			HtmlSubmitInput continue_btn = null;
			
			ArrayList<SecondaryUser> users = new ArrayList<SecondaryUser>(); 
			
			do{
				continue_btn = htmlPage.querySelector("#Sub2207101004_92");
				if(continue_btn == null) break;
				
				SecondaryUser user = getSecondaryUserInfo(htmlPage);
				users.add(user);
				htmlPage = continue_btn.click();
			}while(true);
			
			return users;
		}		
	}
	
	//HANDLE EXCEPTIONS OF registerSecondaryUserImpl()
	public static boolean registerSecondaryUserByNie(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String nie, String naf) throws SegSocialException {
			try {
				registerSecondaryUserImpl(certificateInputStream,certificatePassword,certificateType,nie,naf);
				return true;
			}
			catch (FailingHttpStatusCodeException e) {throw new SegSocialException(e);}
			catch (MalformedURLException e) {throw new SegSocialException(e);}
			catch (IOException e) {throw new SegSocialException(e);}
	}
	
	//REGISTER SECONDARY USER 
	public static void registerSecondaryUserImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ipf, String naf) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SegSocialException {
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
		
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=NRW68&E=I&AP=AUT");
			
			HtmlCheckBoxInput ch1 = htmlPage.querySelector("#chkgrupo1_1");
			htmlPage = ch1.click();
			
			HtmlOption opt1 = (HtmlOption) htmlPage.querySelector("#inputgrupo1_1_1 option:nth-child(2)");
			htmlPage = opt1.click();
			
			HtmlInput ipf_txt = htmlPage.querySelector("#inputgrupo1_1_2");
			ipf_txt.setAttribute("value", ipf);
			
			HtmlInput naf_txt = htmlPage.querySelector("#inputgrupo1_1_3");
			naf_txt.setAttribute("value", naf);
			
			HtmlSubmitInput submit_btn = htmlPage.querySelector("#Sub2207101004_52");
			htmlPage = submit_btn.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlSubmitInput next_submit_btn = htmlPage.querySelector("#Sub2207101004_99");
			htmlPage = next_submit_btn.click();
			
			HtmlSubmitInput final_submit_btn = htmlPage.querySelector("#Sub2207101004_99");
			htmlPage = final_submit_btn.click();
			
		}		
	}
		
	//HANDLE EXCEPTIONS OF deleteSecondaryUser
	public static boolean deleteSecondaryUser(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String nie) throws SegSocialException {
		try {
			deleteSecondaryUserImpl(certificateInputStream,certificatePassword,certificateType,nie);
			return true;
		}catch (Exception  e) {throw new SegSocialException(e);}
		
	}
	
	//DELETE SECONDARY USER
	public static void deleteSecondaryUserImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ipf) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SegSocialException, InterruptedException {
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			
			webClient.getOptions().setTimeout(15000);
			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=NRW68&E=I&AP=AUT");
			
			HtmlCheckBoxInput ch1 = htmlPage.querySelector("#chkgrupo1_2");
			htmlPage = ch1.click();
			
			HtmlOption opt1 = (HtmlOption) htmlPage.querySelector("#inputgrupo1_2_1 option:nth-child(2)");
			htmlPage = opt1.click();
			
			HtmlInput ipf_txt = htmlPage.querySelector("#inputgrupo1_2_2");
			ipf_txt.setAttribute("value", ipf);
			
			HtmlSubmitInput submit_btn = htmlPage.querySelector("#Sub2207101004_52");
			htmlPage = submit_btn.click();
			
			HtmlUnitToolkit.wait4(htmlPage, p->p.querySelector("Sub0600301012")).orElseThrow();
			
			HtmlSubmitInput final_submit_btn = htmlPage.querySelector("#Sub2207101004_99");
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			htmlPage = final_submit_btn.click();			
		}				
	}
	
	//HANDLE EXCEPTIONS OF
	public void modifySecondaryUser(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ipf) {
		modifySecondaryUserImpl(certificateInputStream,certificatePassword,certificateType,ipf);
	}
	
	//MODIFY SECONDARY USERS 
	public void modifySecondaryUserImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ipf) {
		
	}

	//ipf	
	//naf	291136796369

}
