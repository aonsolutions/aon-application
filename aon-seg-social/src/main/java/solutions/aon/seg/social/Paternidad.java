package solutions.aon.seg.social;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

import solutions.aon.seg.social.exceptions.ForbiddenException;
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
	
	
	//01-1017250195
	public static byte[] grabarCertificado(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount,final String docType, final String docNum,
			final String applicantType, final String reason, final Date startDate, final Date endDate, final float baseCC, final float baseCP, final int days) throws SegSocialException{
		
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
			formDatos.getInputByName("fechaInicio").setValueAttribute(Toolkit.formatDate(startDate, "dd/MM/yyyy").get());
			
			//SUBMIT
			//webClient.getOptions().setJavaScriptEnabled(true);
			htmlPage=formDatos.getInputByValue("Validar").click();
			
			//GOES TO THE CONFIRM PAGE
			HtmlForm formDatos2=(HtmlForm)htmlPage.getElementById("formDatos");
			//END DATE
			formDatos2.getInputByName("fechaFinPeriodo1").setValueAttribute(Toolkit.formatDate(endDate, "dd/MM/yyyy").get());
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
	
	
	
	
}
