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
import java.util.Optional;
	import java.util.function.Function;

	import com.gargoylesoftware.htmlunit.BrowserVersion;
	import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
	import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
	import com.gargoylesoftware.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.Employee.EmployeeBuilder;
import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.SegSocialException;
	
public class SistemaRedEmployee {
		public static <HtmlPage, R> Optional<R> wait4(HtmlPage htmlPage, Function<HtmlPage, R> function) throws InterruptedException {
			// try 20 times to wait .5 second each for filling the page.
			for (int i = 0; i < 20; i++) {
				R r = function.apply(htmlPage);
				if (r != null) {return Optional.of(r);}
				synchronized (htmlPage) {htmlPage.wait(500);}
			}
			return Optional.empty();
		}

		
		public static Collection<Employee> getEmployees(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc) throws SegSocialException 
		{
			try {
				return getEmployeesImpl(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
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
		public static Collection<Employee> getEmployeesImpl(final InputStream certificateInputStream, final String certificatePassword,
				final String certificateType, String regimen, String ccc) 
				throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, FailingHttpStatusCodeException {
			
			try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
				
				ArrayList<Employee> employees = new ArrayList<Employee>();
				HtmlPage Origen = webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");

				HtmlPage htmlPage = wait4(Origen, p -> p.getAnchorByHref("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR62&E=I&AP=AFIR")).orElseThrow().click();
				HtmlForm buscaPartesForm = wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
							
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
					employees.add(employeeInfo(empData,webClient));
								
				return employees;
			}
		}
		
		//CREATES AN EMPLOYEE WITH A LIST OF INFORMATION & WEB QUERIES
		private static Employee employeeInfo(ArrayList<String> empData ,WebClient webClient) throws IOException, InterruptedException {
			
			HtmlPage _htmlPage = fillEmployeeForm(empData,webClient);
			
			String nss = _htmlPage.getElementById("SDFTESORNAF").getTextContent() +  _htmlPage.getElementById("SDFNUMNAF").getTextContent();
			String ipf = _htmlPage.getElementById("SDFTIPIPF").getTextContent() +  _htmlPage.getElementById("SDFNUMIPF").getTextContent();
			String birthDateStr = _htmlPage.getElementById("SDFDIANAC").getTextContent() + "-" 
					+ _htmlPage.getElementById("SDFMESNAC").getTextContent() + "-" 
					+ _htmlPage.getElementById("SDFAONAC").getTextContent();
						
			String sex = _htmlPage.getElementById("SDFSEXO").getTextContent();
			String name = _htmlPage.getElementById("SDFAPELNOM").getTextContent();
			String ctaCot = _htmlPage.getElementById("SDFTESORCCC").getTextContent() + _htmlPage.getElementById("SDFNUMCCC").getTextContent();
			String regime = _htmlPage.getElementById("SDFREGIM").getTextContent();
			String companyId = _htmlPage.getElementById("SDFEMPRESARIO3").getTextContent();
			String companyName = _htmlPage.getElementById("SDFNOMBRE3").getTextContent();
			String situation = _htmlPage.getElementById("SDFTSITUACAFI").getTextContent();
			String gc = _htmlPage.getElementById("SDFTGRUPOAFI").getTextContent();
			String agricultPromo = _htmlPage.getElementById("SDFPFEA").getTextContent();
			String workTimeReduct = _htmlPage.getElementById("SDFLITRJ").getTextContent();
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
			
			builder.setNss(empData.get(0).replace(" ",""))
			.setName(empData.get(1))
			.setStartDate(Toolkit.parseDate(empData.get(2), "dd-MM-yyyy"))
			.setSituation(empData.get(3))
			.setIpf(empData.get(4).replace(" ",""))
			.setNss(nss)
			.setIpf(ipf)
			.setBirthDate(birthDate)
			.setSex(sex)
			.setCtaCti(ctaCot)
			.setRegime(regime)
			.setCompanyId(companyId)
			.setCompanyName(companyName)
			.setSituation(situation)
			.setGc(gc)
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
		
		//FILLS EMPLOYEE FORM AND RETURNS THE NEW PAGE
		private static HtmlPage fillEmployeeForm(ArrayList<String> empData, WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
			HtmlPage _htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");
			_htmlPage = wait4(_htmlPage, p -> p.getAnchorByHref("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR61&E=I&AP=AFIR")).orElseThrow().click();
			HtmlForm buscaPartesForm = wait4(_htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			
			buscaPartesForm.getInputByName("txt_SDFTESORNAF").setValueAttribute(empData.get(0).substring(0,3));
			buscaPartesForm.getInputByName("txt_SDFNUMNAF").setValueAttribute(empData.get(0).substring(3));
			
			buscaPartesForm.getInputByName("btn_Sub2207601004").focus();
			return buscaPartesForm.getInputByName("btn_Sub2207601004").click();
		}


		public static void main(String[] args)
				throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SegSocialException {
			try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
				ArrayList<Employee> employees = (ArrayList<Employee>) getEmployees(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062");
				File file = new File("Log.txt");
				FileWriter fw = new FileWriter(file);
				for(Employee e : employees) fw.write(e.toString()+"\n");
				fw.close();
			}
		}
	}


