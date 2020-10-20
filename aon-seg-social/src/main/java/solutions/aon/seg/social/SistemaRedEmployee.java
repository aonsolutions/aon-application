	package solutions.aon.seg.social;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
	import com.gargoylesoftware.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.Employee.EmployeeBuilder;
import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.SegSocialException;
	
public class SistemaRedEmployee {
	
		//HANDLE THE EXCEPTIONS OF GETFULLEMPLOYEESIMPL METHOD
		public static Collection<Employee> getFullEmployees(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc) throws SegSocialException 
		{
			try {
				return getEmployeesFullImpl(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
			} catch (FailingHttpStatusCodeException e) {
				switch (e.getStatusCode()) {
				case 403:
					throw new ForbiddenException();
				default:
					throw new SegSocialException(e);
				}
			} 
			catch (MalformedURLException e) {throw new SegSocialException(e);} 
			catch (IOException e) {throw new SegSocialException(e);} 
			catch (InterruptedException e) {throw new SegSocialException(e);}
		}
		
		//RETURNS ALL THE EMPLOYEES OF A COMPANY 
		private static Collection<Employee> getEmployeesFullImpl(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc) 
				throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, FailingHttpStatusCodeException {
			
			try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
				webClient.getOptions().setJavaScriptEnabled(false);
				
				ArrayList<Employee> employees = new ArrayList<Employee>();
				HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
				HtmlForm buscaPartesForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();

				buscaPartesForm.getInputByName("txt_SDFREG62_ayuda").setValueAttribute(regimen);
				buscaPartesForm.getInputByName("txt_SDFTESO62").setValueAttribute(ccc.substring(0, 2));
				buscaPartesForm.getInputByName("txt_SDFNUM62").setValueAttribute(ccc.substring(2));
				buscaPartesForm.getInputByName("chk_chkgrupo1_1").setChecked(true);
				htmlPage = buscaPartesForm.getInputByName("btn_Sub2207601004").click();
				
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
		private static Employee employeeFullInfo(String nss ,WebClient webClient) throws IOException, InterruptedException {
			
			HtmlPage _htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR61&E=I&AP=AFIR");
			HtmlForm buscaPartesForm = HtmlUnitToolkit.wait4(_htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();

			buscaPartesForm.getInputByName("txt_SDFTESORNAF").setValueAttribute(nss.substring(0,2));
			buscaPartesForm.getInputByName("txt_SDFNUMNAF").setValueAttribute(nss.substring(3));
			buscaPartesForm.getInputByName("btn_Sub2207601004").focus();
			_htmlPage = buscaPartesForm.getInputByName("btn_Sub2207601004").click();
			
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
			
			try {return getEmployeesImpl(certificateInputStream, certificatePassword, certificateType, regimen, ccc);} 
			catch (FailingHttpStatusCodeException e) {
				switch (e.getStatusCode()) {
				case 403:
					throw new ForbiddenException();
				default:
					throw new SegSocialException(e);
				}
			} 
			catch (MalformedURLException e) {throw new SegSocialException(e);} 
			catch (IOException e) {throw new SegSocialException(e);} 
			catch (InterruptedException e) {throw new SegSocialException(e);}
		}
		
		//RETURN ALL THE EMPLOYEES
		private static Collection<Employee> getEmployeesImpl(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc) throws ElementNotFoundException, IOException, InterruptedException
		{
				try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
				webClient.getOptions().setJavaScriptEnabled(false);
				HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR");
				HtmlForm buscaPartesForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
							
				buscaPartesForm.getInputByName("txt_SDFREG62_ayuda").setValueAttribute(regimen);
				buscaPartesForm.getInputByName("txt_SDFTESO62").setValueAttribute(ccc.substring(0, 2));
				buscaPartesForm.getInputByName("txt_SDFNUM62").setValueAttribute(ccc.substring(2));
				buscaPartesForm.getInputByName("chk_chkgrupo1_1").setChecked(true);
				htmlPage = buscaPartesForm.getInputByName("btn_Sub2207601004").click();
				
				Iterable<DomElement> tableContent = htmlPage.getElementById("Sub1000112079").getLastElementChild().getChildElements();
				ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();	
				ArrayList<String> empData = new ArrayList<String>();
				int i = 1;

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
				ArrayList<Employee> employees = new ArrayList<Employee>();
				
				for(ArrayList<String> empdata : data) {
					String nss = empdata.get(0);
					if(nss != null) nss = Toolkit.removeExtraZeros(nss.replace(" ", ""));
					String name = empdata.get(1);
					Date birthDate = Toolkit.parseDate(empdata.get(2), "dd-MM-yyyy");
					String situation = empdata.get(3);
					String ipf = empdata.get(4);
					if(ipf != null) ipf = Toolkit.removeExtraZeros(ipf.replace(" ", ""));
					
					Employee employee = builder.setNss(nss)
					.setName(name)
					.setBirthDate(birthDate)
					.setSituation(situation)
					.setIpf(ipf)
					.build();
					
					employees.add(employee);
				}

				return employees;
			}
			
		}
		
		
		//HANDLE GETEMPLOYEE EXCEPTIONS
		public static Employee getEmployee(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
			
			try {return getEmployeeImpl(certificateInputStream, certificatePassword, certificateType, nss);} 
			catch (FailingHttpStatusCodeException e) {
				switch (e.getStatusCode()) {
				case 403:
					throw new ForbiddenException();
				default:
					throw new SegSocialException(e);
				}
			} 
			catch (MalformedURLException e) {throw new SegSocialException(e);} 
			catch (IOException e) {throw new SegSocialException(e);} 
			catch (InterruptedException e) {throw new SegSocialException(e);}
		}
		
		
		
		//RETURNS AN EMPLOYEE
		private static Employee getEmployeeImpl(InputStream certificateInputStream, String certificatePassword,
				String certificateType, String nss) throws IOException, InterruptedException {
			try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
				webClient.getOptions().setJavaScriptEnabled(false);
				return employeeFullInfo(nss, webClient);
			}
		}

		public static void main(String[] args)
				throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SegSocialException {
			try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
				File f = new File("logEmployee.txt");
				FileWriter fw = new FileWriter(f);
//				Employee e = getEmployee(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062", "010019805355");
				
				ArrayList<Employee> employees = (ArrayList<Employee>) getFullEmployees(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062");
				for(Employee e : employees) fw.append(e.toString());
				fw.close();
;				
			}
		}
	}


