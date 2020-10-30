package solutions.aon.seg.social;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.gargoylesoftware.css.parser.javacc.ParseException;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;

import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.PaternityException;
import solutions.aon.seg.social.exceptions.PaternityNotFoundException;
import solutions.aon.seg.social.exceptions.PaternityWrongDataException;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class Paternidad {
	final static String[] ID_TYPE={"NIF", "NIE"};
	final static String[] APPLICANT_TYPE= {"M", "P", "A", "B"};
	final static String[] MOTHER_REASON= {"Nacimiento de hijo","Fallecimiento de la madre","Cesión/Opción en favor del otro progenitor",
			"Parto múltiple","Inicio del descanso antes del parto (solo para madre biológica ET)"};
	final static String[] FATHER_REASON= {"Nacimiento de hijo","Parto múltiple"};
	final static String ADOPTERS= "Adopción/Tutela/Acogimiento";
	
	public static byte[] grabarCertificado(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount,final String docType, final String docNum,
			final String applicantType, final String reason, final Date dateFrom, final Date dateTo, final float baseCC, final float baseCP, final int days) throws SegSocialException{
		
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			webClient.getOptions().setJavaScriptEnabled(false);
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/GetAccess/ResourceList");
			htmlPage=htmlPage.getAnchorByHref("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ"
					+ ".SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H100").click();
			HtmlForm formDatos=(HtmlForm) htmlPage.getElementById("formDatos");
			
			//REGIME
			formDatos.getInputByName("regimen").setValueAttribute(regime);
			//CCC
			formDatos.getInputByName("ccc2").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
			formDatos.getInputByName("ccc9").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
			//NAF
			formDatos.getInputByName("naf2").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
			formDatos.getInputByName("naf10").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			//ID TYPE
			HtmlSelect idTypeSelect=formDatos.getSelectByName("tipoIpf");
			idTypeSelect.getOptionByText(docType).setSelected(true);
			//ID NUM
			formDatos.getInputByName("codIpf").setValueAttribute(docNum);
			//APPLICANT TYPE
			HtmlSelect applicantTypeSelect=formDatos.getSelectByName("tipoPrestacion");
			applicantTypeSelect.getOptionByValue(applicantType).setSelected(true);
			//REASON
			HtmlSelect reasonSelect=formDatos.getSelectByName("motivoMadreBiologica");
			reasonSelect.getOptionByText(reason).setSelected(true);
			//START DATE
			formDatos.getInputByName("fechaInicio").setValueAttribute(Toolkit.formatDate(dateFrom, "dd/MM/yyyy").get());
			
			//SUBMIT
			//webClient.getOptions().setJavaScriptEnabled(true);
			htmlPage=formDatos.getInputByValue("Validar").click();
			
			//GOES TO THE CONFIRM PAGE
			HtmlForm formDatos2=(HtmlForm)htmlPage.getElementById("formDatos");
			//END DATE
			formDatos2.getInputByName("fechaFinPeriodo1").setValueAttribute(Toolkit.formatDate(dateTo, "dd/MM/yyyy").get());
			//BASE CC
			formDatos2.getInputByName("baseCC1").setValueAttribute(""+Float.toString(baseCC).replace(".", ","));
			formDatos2.getInputByName("baseCP1").setValueAttribute(""+Float.toString(baseCP).replace(".", ","));
			formDatos2.getInputByName("prestacion1").setValueAttribute(""+days);
			//CONFIRM
			htmlPage=formDatos2.getInputByValue("Confirmar").click();
			
			InputStream docIS=htmlPage.getWebResponse().getContentAsStream();
			byte[] ret=docIS.readAllBytes();
			docIS.close();
			return ret;
			
			
		} catch (FailingHttpStatusCodeException e) {
			switch (e.getStatusCode()) {
			case 403:
				throw new ForbiddenException();
			default:
				throw new SegSocialException();
			}
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	
	}

	public static void voidPaternity(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount, final Date dateFrom, final Date dateTo, final Optional<Date> startDate) throws SegSocialException{
			try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
				HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H100");
				//MOVING TO 'MODIFICAR/ANULAR CERTIFICADOS' SECTION
				HtmlForm formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
				htmlPage=formDatos.getInputByValue("Modificar/Anular certificado").click();
				formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
				//REGIME
				formDatos.getInputByName("regimen").setValueAttribute(regime);
				//CCC
				formDatos.getInputByName("ccc2").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
				formDatos.getInputByName("ccc9").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
				//DATE FROM
				formDatos.getInputByName("fechaDesde").setValueAttribute(Toolkit.formatDate(dateFrom, "dd/MM/yyyy").get());
				//END DATE
				formDatos.getInputByName("fechaHasta").setValueAttribute(Toolkit.formatDate(dateTo, "dd/MM/yyyy").get());
				//NAF
				formDatos.getInputByName("naf2").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
				formDatos.getInputByName("naf10").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
				//START DATE (OPTIONAL)
				if(!startDate.isEmpty()) {
					formDatos.getInputByName("fechaInicio").setValueAttribute(Toolkit.formatDate(startDate.get(), "dd/MM/yyyy").get());
				}
				
				//SUBMIT
				htmlPage=formDatos.getInputByValue("Buscar").click();
				//CHECKING IF THE PAGE THREW RESULTS
				try {
					HtmlTable resultTable=(HtmlTable)htmlPage.querySelector("#ARQcapaPrincipalPest fieldset>div>table");
					int rows=resultTable.getRowCount()-1;
					for(int i=1;i<=rows;i++) {
						HtmlTableCell resultCell=resultTable.getCellAt(i, 0);
						formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
						try {
							HtmlInput resultInput=(HtmlInput) resultCell.getFirstElementChild();
							htmlPage=resultInput.click();
							//VOIDING
							
							HtmlPage htmlAux=formDatos.getInputByValue("Anular").click();
							htmlAux=htmlAux.getElementById("SPM.ACC.AC_GE_ANULAR").click();
							//htmlPage=HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("SPM.ACC.AC_GE_ANULAR")).orElseThrow().click();
						}catch (NullPointerException |ElementNotFoundException e) {
							//It's already voided
						}
					}
					
//					//VOIDING
//					formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
//					htmlPage=formDatos.getInputByValue("Anular").click();
//					htmlPage=htmlPage.getElementById("SPM.ACC.AC_GE_ANULAR").click();
//					//htmlPage=HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("SPM.ACC.AC_GE_ANULAR")).orElseThrow().click();
					
					
				}catch (NullPointerException | ElementNotFoundException e) {
					try {
						HtmlListItem errorLi=(HtmlListItem)htmlPage.querySelector("#ARQContenMensajePest>ul>.mensajeError[title='Error']");
						if(errorLi.getVisibleText().trim().equalsIgnoreCase("Régimen/Cuenta de Cotización NO HAY DATOS PARA ESTOS CRITERIOS DE CONSULTA")) {
							throw new PaternityNotFoundException();
						}
						else {
							throw new PaternityWrongDataException();
						}
					}catch (NullPointerException e1) {
					throw new PaternityException();
					}
				}
				
			} catch (FailingHttpStatusCodeException e) {
				switch (e.getStatusCode()) {
				case 403:
					throw new ForbiddenException();
				default:
					throw new SegSocialException();
				}
			} catch (MalformedURLException e) {
				throw new SegSocialException(e);
			} catch (IOException e) {
				throw new SegSocialException(e);
			}	catch (NoSuchElementException e) {
				throw new PaternityException();
			}
	}
	
	
	

	
	
}	
	
	
	
	
//	public static void consultCertificate (final InputStream certificateInputStream,
//			final String certificatePassword, final String certificateType, final String affiliationNumber,
//			final String regime, final String contributionAccount, final Date dateFrom, final Date dateTo, final Optional<Date> startDate) throws SegSocialException{
//			try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
//				HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H100");
//				//MOVING TO 'MODIFICAR/ANULAR CERTIFICADOS' SECTION
//				HtmlForm formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
//				htmlPage=formDatos.getInputByValue("Consultar certificado").click();
//				formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
//				//REGIME
//				formDatos.getInputByName("regimen").setValueAttribute(regime);
//				//CCC
//				formDatos.getInputByName("ccc2").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
//				formDatos.getInputByName("ccc9").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
//				//DATE FROM
//				formDatos.getInputByName("fechaDesde").setValueAttribute(Toolkit.formatDate(dateFrom, "dd/MM/yyyy").get());
//				//END DATE
//				formDatos.getInputByName("fechaHasta").setValueAttribute(Toolkit.formatDate(dateTo, "dd/MM/yyyy").get());
//				//NAF
//				formDatos.getInputByName("naf2").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
//				formDatos.getInputByName("naf10").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
//				//START DATE (OPTIONAL)
//				if(!startDate.isEmpty()) {
//					formDatos.getInputByName("fechaInicio").setValueAttribute(Toolkit.formatDate(startDate.get(), "dd/MM/yyyy").get());
//				}
//				
//				//SUBMIT
//				htmlPage=formDatos.getInputByValue("Buscar").click();
//				//CHECKING IF THE PAGE THREW RESULTS
//				try {
//					HtmlTable resultTable=(HtmlTable)htmlPage.querySelector("#ARQcapaPrincipalPest fieldset>div>table");
//					int rows=resultTable.getRowCount()-1;
//					for(int i=1;i<=rows;i++) {
//						HtmlTableCell resultCell=resultTable.getCellAt(i, 0);
//						try {
//							HtmlInput resultInput=(HtmlInput) resultCell.getFirstElementChild();
//							htmlPage=resultInput.click();
//						}catch (NullPointerException |ElementNotFoundException e) {
//							//It's already voided
//						}
//					}
//					
//				}catch (NullPointerException | ElementNotFoundException e) {
//					try {
//						HtmlListItem errorLi=(HtmlListItem)htmlPage.querySelector("#ARQContenMensajePest>ul>.mensajeError[title='Error']");
//						if(errorLi.getVisibleText().trim().equalsIgnoreCase("Régimen/Cuenta de Cotización NO HAY DATOS PARA ESTOS CRITERIOS DE CONSULTA")) {
//							throw new PaternityNotFoundException();
//						}
//						else {
//							throw new PaternityWrongDataException();
//						}
//					}catch (NullPointerException e1) {
//					throw new PaternityException();
//					}
//				
//			} catch (FailingHttpStatusCodeException e) {
//				switch (e.getStatusCode()) {
//				case 403:
//					throw new ForbiddenException();
//				default:
//					throw new SegSocialException();
//				}
//			} catch (MalformedURLException e) {
//				throw new SegSocialException(e);
//			} catch (IOException e) {
//				throw new SegSocialException(e);
//			}	catch (NoSuchElementException e) {
//				throw new PaternityException();
//			}
//	} catch (FailingHttpStatusCodeException e2) {
//		// TODO Auto-generated catch block
//		e2.printStackTrace();
//	} catch (MalformedURLException e2) {
//		// TODO Auto-generated catch block
//		e2.printStackTrace();
//	} catch (IOException e2) {
//		// TODO Auto-generated catch block
//		e2.printStackTrace();
//	}
//			}
	