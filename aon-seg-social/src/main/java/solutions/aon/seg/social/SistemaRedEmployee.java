	package solutions.aon.seg.social;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.Employee;
import solutions.aon.seg.social.objects.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;
	
public class SistemaRedEmployee {
	
		//HANDLE THE EXCEPTIONS OF GETFULLEMPLOYEESIMPL METHOD
		public static Collection<Employee> getFullEmployees(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc) throws SegSocialException 
		{
			InvalidCertificateException.checkCertificate(certificateInputStream);
			try {return getEmployeesFullImpl(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
} 
			catch (FailingHttpStatusCodeException e) {
				switch (e.getStatusCode()) {
				case 403:
					throw new ForbiddenException();
				default:
					throw new SegSocialException(e);
				}
			} 
			catch (MalformedURLException e) {throw new SegSocialException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SegSocialException(e);}
		}
		
		//RETURNS ALL THE EMPLOYEES OF A COMPANY 
		private static Collection<Employee> getEmployeesFullImpl(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc) 
				throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, FailingHttpStatusCodeException, SegSocialException {
			
			try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
				webClient.getOptions().setJavaScriptEnabled(false);
				
				ArrayList<Employee> employees = new ArrayList<Employee>();
				HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
				
				HtmlUnitToolkit.manageStatusCode(htmlPage);
				
				HtmlForm buscaPartesForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();

				buscaPartesForm.getInputByName("txt_SDFREG62_ayuda").setValueAttribute(regimen);
				buscaPartesForm.getInputByName("txt_SDFTESO62").setValueAttribute(ccc.substring(0, 2));
				buscaPartesForm.getInputByName("txt_SDFNUM62").setValueAttribute(ccc.substring(2));
				buscaPartesForm.getInputByName("chk_chkgrupo1_1").setChecked(true);
				htmlPage = buscaPartesForm.getInputByName("btn_Sub2207601004").click();
				
				HtmlUnitToolkit.manageStatusCode(htmlPage);
				
				Iterable<DomElement> tableContent = htmlPage.getElementById("Sub1000112079").getLastElementChild().getChildElements();
				ArrayList<ArrayList<String>> totalData = new ArrayList<ArrayList<String>>();
				ArrayList<String> employeeData = new ArrayList<String>();
				
				int i = 1;
				for(DomElement tr: tableContent) {
					Iterable<DomElement> rowContent = tr.getChildElements();
					for(DomElement td : rowContent) {
						if(i == 5) {
								employeeData.add(td.getVisibleText());	
								totalData.add(employeeData);
								employeeData = new ArrayList<String>();
								i=0;
						}else 
							if(i == 1 && td.getVisibleText().equals("")) break;
							else employeeData.add(td.getVisibleText());				
						i++;
					}
				}
				
				for (ArrayList<String> empData : totalData) 
					employees.add(employeeFullInfo(empData.get(0),webClient));
								
				return employees;
			}
		}
		
		//CREATES AN EMPLOYEE WITH A LIST OF INFORMATION & WEB QUERIES
		private static Employee employeeFullInfo(String nss ,WebClient webClient) throws IOException, InterruptedException, SegSocialException {
			
			HtmlPage _htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR61&E=I&AP=AFIR");
			HtmlForm buscaPartesForm = HtmlUnitToolkit.wait4(_htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			HtmlUnitToolkit.manageStatusCode(_htmlPage);

			buscaPartesForm.getInputByName("txt_SDFTESORNAF").setValueAttribute(nss.substring(0,2));
			buscaPartesForm.getInputByName("txt_SDFNUMNAF").setValueAttribute(nss.substring(2));
			buscaPartesForm.getInputByName("btn_Sub2207601004").focus();
			_htmlPage = buscaPartesForm.getInputByName("btn_Sub2207601004").click();
			HtmlUnitToolkit.manageStatusCode(_htmlPage);
			
			String ipf = _htmlPage.getElementById("SDFTIPIPF").getTextContent() +  _htmlPage.getElementById("SDFNUMIPF").getTextContent();
			String birthDateStr = _htmlPage.getElementById("SDFDIANAC").getTextContent() + "-" 
					+ _htmlPage.getElementById("SDFMESNAC").getTextContent() + "-" 
					+ _htmlPage.getElementById("SDFAONAC").getTextContent();
						
			String sex = _htmlPage.getElementById("SDFSEXO").getTextContent();
			String name = _htmlPage.getElementById("SDFAPELNOM").getTextContent();
			String tlf = _htmlPage.getElementById("SDFMOVIL").getTextContent();
			String ctaCot = _htmlPage.getElementById("SDFTESORCCC").getTextContent() + _htmlPage.getElementById("SDFNUMCCC").getTextContent();
			String regime = _htmlPage.getElementById("SDFREGIM").getTextContent();
			String companyId = _htmlPage.getElementById("SDFEMPRESARIO3").getTextContent();
			String companyName = _htmlPage.getElementById("SDFNOMBRE3").getTextContent();
			String situation = _htmlPage.getElementById("SDFTSITUACAFI").getTextContent();
			String gc = _htmlPage.getElementById("SDFCGRUPOAFI").getTextContent();
			String gcDesc = _htmlPage.getElementById("SDFTGRUPOAFI").getTextContent();
			Boolean agricultPromo = Toolkit.toBoolean(_htmlPage.getElementById("SDFPFEA").getTextContent());
			Boolean workTimeReduct = Toolkit.toBoolean(_htmlPage.getElementById("SDFLITRJ").getTextContent());
			String fraStr = _htmlPage.getElementById("SDFFRAAFI").getTextContent();
			String feaStr = _htmlPage.getElementById("SDFFEAAFI").getTextContent();
			String frbStr = _htmlPage.getElementById("SDFFRBAFI").getTextContent();
			String febStr = _htmlPage.getElementById("SDFFEBAFI").getTextContent();
			String contract= _htmlPage.getElementById("SDFTIPOAFI").getTextContent();
			String coef= _htmlPage.getElementById("SDFCOEFAFI").getTextContent();
			String colec= _htmlPage.getElementById("SDFCOLECTIVO").getTextContent();
			String epig= _htmlPage.getElementById("SDFEPIGAFI").getTextContent();
			String ocup= _htmlPage.getElementById("SDFOCUPACION").getTextContent();
			String vinFam= _htmlPage.getElementById("SDFVINCULO").getTextContent();
			String profesCat= _htmlPage.getElementById("SDFCATEGORIA").getTextContent();
			String reducingCoef= _htmlPage.getElementById("SDFCOEFRED").getTextContent();
						
			ipf = Toolkit.removeExtraZeros(ipf);
			companyId = Toolkit.removeExtraZeros(companyId);
			
			if(birthDateStr.equals(" / / ")) birthDateStr = "";
			
			Date birthDate = Toolkit.parseDate(birthDateStr, "dd-MM-yyyy");
			Date fra = Toolkit.parseDate(fraStr, "dd/MM/yyyy");
			Date fea = Toolkit.parseDate(feaStr, "dd/MM/yyyy");
			Date frb = Toolkit.parseDate(frbStr, "dd/MM/yyyy");
			Date feb = Toolkit.parseDate(febStr, "dd/MM/yyyy");
			
		
			//BUILD
			EmployeeBuilder builder = new EmployeeBuilder();
			
			builder.setNss(nss.replace(" ",""))
			.setName(name)
			.setSituation(situation)
			.setIpf(ipf.replace(" ",""))
			.setNss(nss)
			.setIpf(ipf)
			.setBirthDate(birthDate)
			.setSex(sex)
			.setTlf(tlf)
			.setCtaCti(ctaCot)
			.setRegime(regime)
			.setCompanyId(companyId)
			.setCompanyName(companyName)
			.setSituation(situation)
			.setGc(gc)
			.setGcDesc(gcDesc)
			.setAgricultPromo(agricultPromo)
			.setWorkTimeReduct(workTimeReduct)
			.setFra(fra)
			.setFea(fea)
			.setFrb(frb)
			.setFeb(feb)
			.setContract(contract)
			.setCoef(coef)
			.setColec(colec)
			.setEpig(epig)
			.setOcup(ocup)
			.setVinFam(vinFam)
			.setProfesCat(profesCat)
			.setReducingCoefic(reducingCoef);
			
			return builder.build();
		}
		
		
		
		
		//HANDLE THE EXCEPTIONS OF GETEMPLOYEE METHOD
		public static Collection<Employee> getEmployees(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc) throws SegSocialException{
			
			Toolkit.verifyData(new Object[]{regimen,ccc});
			InvalidCertificateException.checkCertificate(certificateInputStream);
			
			try {return getEmployeesImpl(certificateInputStream, certificatePassword, certificateType, regimen, ccc);} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (MalformedURLException e) {throw new SegSocialException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SegSocialException(e);}
			return null;
		}
		
		//RETURN ALL THE EMPLOYEES
		private static Collection<Employee> getEmployeesImpl(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc) throws ElementNotFoundException, IOException, InterruptedException, SegSocialException
		{
				try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
					
				webClient.getOptions().setJavaScriptEnabled(false);
				ArrayList<Employee> employees = new ArrayList<Employee>();
				
				HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
				HtmlUnitToolkit.manageStatusCode(htmlPage);
				
				HtmlForm buscaPartesForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
				buscaPartesForm.getInputByName("txt_SDFREG62_ayuda").setValueAttribute(regimen);
				buscaPartesForm.getInputByName("txt_SDFTESO62").setValueAttribute(ccc.substring(0, 2));
				buscaPartesForm.getInputByName("txt_SDFNUM62").setValueAttribute(ccc.substring(2));
				buscaPartesForm.getInputByName("chk_chkgrupo1_1").setChecked(true);
				htmlPage = buscaPartesForm.getInputByName("btn_Sub2207601004").click();
				HtmlUnitToolkit.manageStatusCode(htmlPage);
				
				Iterable<DomElement> tableContent = htmlPage.getElementById("Sub1000112079").getLastElementChild().getChildElements();
				ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();	
				ArrayList<String> empData = new ArrayList<String>();
				int i = 1;
				boolean end = false;
				
				while(!end) {
					for(DomElement tr: tableContent) {
						Iterable<DomElement> rowContent = tr.getChildElements();
						for(DomElement td : rowContent) {				
							if(i == 5) {
								empData.add(td.getVisibleText());	
								data.add(empData);
								empData = new ArrayList<String>();
								i=0;
							}else 
								if(i == 1 && td.getVisibleText().equals("")) break;
								else empData.add(td.getVisibleText());				
							i++;
						}
					}
					
					EmployeeBuilder builder = new EmployeeBuilder();
					
					
					for(ArrayList<String> empdata : data) {
						String nss = empdata.get(0);
						if(nss != null) nss = nss.replace(" ", "");
						String name = empdata.get(1);
						Date fra = Toolkit.parseDate(empdata.get(2), "dd-MM-yyyy");
						String situation = empdata.get(3);
						String ipf = empdata.get(4);
						
						if(ipf != null) ipf = Toolkit.removeExtraZeros(ipf.replace(" ", ""));
						
						Employee employee = builder.setNss(nss)
						.setName(name)
						.setFra(fra)
						.setSituation(situation)
						.setIpf(ipf)
						.build();
						
						employees.add(employee);
					}
					HtmlInput btn = htmlPage.querySelector("input[name=btn_Sub2207801001]");
					htmlPage = btn.click();
					if(HtmlUnitToolkit.getSSCode(htmlPage) == 3145) end = true;
				}
				return employees;
			}
			
		}
		
		
		//HANDLE GETEMPLOYEE EXCEPTIONS
		public static Employee getEmployee(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
			
			Toolkit.verifyData(new Object[]{regimen,ccc,nss});
			InvalidCertificateException.checkCertificate(certificateInputStream);
			
			try {return getEmployeeImpl(certificateInputStream, certificatePassword, certificateType, nss);} 
			catch (FailingHttpStatusCodeException e) {
				switch (e.getStatusCode()) {
				case 403:
					throw new ForbiddenException();
				default:
					throw new StatusCodeException();
				}
			} 
			catch (MalformedURLException e) {throw new SegSocialException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SegSocialException(e);}
		}
		
		
		
		//RETURNS AN EMPLOYEE
		private static Employee getEmployeeImpl(InputStream certificateInputStream, String certificatePassword,
				String certificateType, String nss) throws IOException, InterruptedException, SegSocialException {
			try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
				webClient.getOptions().setJavaScriptEnabled(false);
				return employeeFullInfo(nss, webClient);
			}
		}
		
		
		//GETS A PDF CCC LIQUIDATION
		public static byte[] getCccLiquidation(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc,Date liquidationPeriod) throws SegSocialException {
			InvalidCertificateException.checkCertificate(certificateInputStream);
			try {return getCccLiquidationImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, liquidationPeriod);} 
			catch (FailingHttpStatusCodeException e) {throw new StatusCodeException();} 
			catch (MalformedURLException e) {throw new SegSocialException();} 
			catch (IOException e) {throw new SegSocialException();} 			
		}
		
		
		//GET PDF INFO 
		public static byte[] getCccLiquidationImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, String regime, String ccc, Date liquidationPeriod) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SegSocialException {
			
			try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
				
				HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR38&E=I&AP=AFIR");
				
				HtmlForm form = htmlPage.getFormByName("jacadaform");
				String[] cccSplit = Toolkit.SplitString(ccc, 2);
				
				form.getInputByName("txt_SDFREGCTA").setAttribute("value", regime);
				form.getInputByName("txt_SDFTESCTA").setAttribute("value", cccSplit[0]);
				form.getInputByName("txt_SDFCUENTA").setAttribute("value", cccSplit[1]);
				
				HtmlOption option = (HtmlOption) form.querySelectorAll("select[name=cbo_ListaTipoImpresion]>option").get(1);				
				option.click();
				HtmlUnitToolkit.manageStatusCode(htmlPage);
								
				String[] splitDate = Toolkit.formatDate(liquidationPeriod, "MM-yyyy").get().split("-");
				form.getInputByName("txt_SDFMES").setAttribute("value", splitDate[0]);
				form.getInputByName("txt_SDFAO").setAttribute("value", splitDate[1]);
									
				HtmlInput submit = (HtmlInput)(form.querySelector("input[name=btn_Sub2207601004]"));
				webClient.getOptions().setRedirectEnabled(true);
				InputStream stream = submit.click().getWebResponse().getContentAsStream();

				byte[] ret = stream.readAllBytes();
				stream.close();  
				
				HtmlUnitToolkit.manageStatusCode(htmlPage);
				return ret;
			}			
		}
	}


