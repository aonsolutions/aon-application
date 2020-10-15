package solutions.aon.seg.social;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


import solutions.aon.seg.social.SituacionEmpresa.SituacionEmpresaBuilder;

public class SistemaRED_I {
	
	private static SituacionEmpresa getSituacionEmpresa(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc) 
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, FailingHttpStatusCodeException {
						
		
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");
			
			htmlPage = HtmlUnitToolkit.wait4(htmlPage, p -> p.getAnchorByHref("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR")).orElseThrow().click();
			HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementsById("SDFREGCTA_ayuda"));
			
			//System.out.println(htmlPage.asXml());
			HtmlForm jacadaform = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			
			//Separando el ccc
			String ccc1 = ccc.substring(0, 2);
			String ccc2 = ccc.substring(2);
			
			jacadaform.getInputByName("txt_SDFREGCTA_ayuda").setValueAttribute(regime);
			jacadaform.getInputByName("txt_SDFTESCTA").setValueAttribute(ccc1);
			jacadaform.getInputByName("txt_SDFNUMCTA").setValueAttribute(ccc2);
			
			//click en 'Continuar'
			htmlPage = jacadaform.getInputByValue("Continuar").click();
			
			//control de campos
			String nif = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFEMPRESARIO3");
			String cadFecha = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECHASIT");
			String fechaAlta = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECHAALTA");
			String fechaAlta13 = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECHAALTA13");
			String fechaBaja93 = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECHABAJA93");
			String esctaller = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCESTAL");
			Boolean booltaller = Toolkit.toBoolean(esctaller);
			String plazoRed = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPLAZORED");
			String fechaAutRed = HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFFECHAAUTRED");
			Integer trabajadorAlta = null;
			Integer trabajador2Alta = null;
			Integer ta2baja = null;
			Float tiposATyEpit = null;
			Float ims = null;
			Float total = null;
			
			try { trabajadorAlta = Integer.parseInt(HtmlUnitToolkit.getTrimmedById(htmlPage,"SDFNROTRA3"));}
			catch(NumberFormatException e) {}
			try { trabajador2Alta = Integer.parseInt(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCCONDIAS3"));}
			catch (NumberFormatException e) {}
			try { ta2baja = Integer.parseInt(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCCONDPPB3"));}
			catch (NumberFormatException e) {}			
			try { tiposATyEpit = Float.parseFloat(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTITN12").replace(",", "."));}
			catch (NumberFormatException e) {}
			try { ims = Float.parseFloat(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTIMSN12").replace(",", "."));}
			catch (NumberFormatException e) {}
			try { total = Float.parseFloat(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTTOTN12").replace(",", "."));}
			catch (NumberFormatException e) {}
			
			if(nif.length()>9) 
				nif = Toolkit.removeExtraZeros(nif);
		
			//Pasando los valores al objeto
			SituacionEmpresaBuilder seb1 = new SituacionEmpresaBuilder();
			
			//DATOS IDENTIFICATIVOS
			seb1.setCcc(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROVINCIA3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNISS3"))
			.setIdEmpresario(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTIPO3"))
			.setNif_empresa(nif)
			.setRegimen(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFREGIMEN3"))
			.setNss(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPRONAF3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMNAF30"))
			.setCccp(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROVINCIACP3"))
			.setUgtgss(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTESORERIA3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFADMON3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFURE3"))
			.setUgtgsscccp(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTESPPAL") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFADMPPAL") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFUREPPAL"))
			.setUgcentral(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFUNIDAD"))
			.setOgism(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROUGISM") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCUGISM"))
			.setCccAnt(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROVCANT3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMCANT3"))
			.setCccSuc(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPROSUC3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMSUC3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFREGSUC3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCCOASUC3"))
			.setSit(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFSITUACION3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTSITUACION3"))
			.setfSit(Toolkit.parseDate(cadFecha, "dd/MM/yyyy"))
			.setfAltaInicial(Toolkit.parseDate(fechaAlta, "dd/MM/yyyy"))
			.setTrabajadorAlta(trabajadorAlta)
			.setAltaPrTrab(Toolkit.parseDate(fechaAlta13, "dd/MM/yyyy"))
			.setUltBajaEfCot(Toolkit.parseDate(fechaBaja93, "dd/MM/yyyy"))
			.setTrl(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTIPCON") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDESTIPCON"))
			.setcEsp_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCOLECTIVO3"))
			.setcEsp_cad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDESCOL3"))
			.setCnae09_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFACTIV093"))
			.setCnae93_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFACTIV933"))
			.setCnae09_cad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTACTIV093"))
			.setCnae93_cad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTACTIV933"))
			.setTa2Alta(trabajador2Alta)
			.setTa2Baja(ta2baja)
			.setTiposATyEPIT(tiposATyEpit)
			.setIms(ims)
			.setTotal(total)
			.setCoeJub_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFCOREJU"))
			.setCoeJub_cad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDSCOREJU"))
			.setAconExtra(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFACTMEXTR") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDSACONT"))
			.setEscTaller(booltaller)
			.setAutorizacionRed(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFAUTORIDRED"))
			.setPlazoIncorpRed(Toolkit.parseDate(plazoRed, "dd/MM/yyyy"))
			.setFechaAutCan(Toolkit.parseDate(fechaAutRed, "dd/MM/yyyy"));
			
			//Datos identificativos
			jacadaform = htmlPage.getFormByName("jacadaform");
			
			htmlPage=jacadaform.getInputByValue("Datos Iden.").click();
			HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("SDFLOCALIDAD4"));
			
			seb1.setAnagrama(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFANAGR3"))
			.setEmbarcacion(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTIPEMB") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFEMB") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOMEMBAR"))
			.setTlfMovil(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTELMOVIL3"))
			.setTlfFijo(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTELFIJO3"))
			.setEmail(HtmlUnitToolkit.getTrimmedById(htmlPage, "txtconcat1_1"))
			.setNotif_dom_empresa(Toolkit.toBoolean(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOTIF3")))
			.setTipo_via_dir_empresa(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFVIA3"))
			.setDir_emp_calle(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDOMICILIO3"))
			.setDir_emp_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMERO3"))
			.setDir_emp_bis(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBIS3"))
			.setDir_emp_bloq(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBLOQUE3"))
			.setDir_emp_es(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFESCALERA3"))
			.setDir_emp_piso(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPISO3"))
			.setDir_emp_p(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPUERTA3"))
			.setDir_emp_CP(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPOSTAL3"))
			.setDir_emp_num_muni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCALIDAD13"))
			.setDir_emp_nom_muni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCALIDAD23"))
			.setDir_emp_tlf(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUM9TELEFONO3"))
			.setNotif_dom_actividad(Toolkit.toBoolean(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNOTIF4")))
			.setAct_ugtgss(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFTESORERIA3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFADMON3") + HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFURE3"))
			.setTipo_via_dir_actividad(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFVIA4"))
			.setDir_act_calle(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFDOMICILIO4"))
			.setDir_act_num(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUMERO3"))
			.setDir_act_bis(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBIS4"))
			.setDir_act_bloq(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFBLOQUE4"))
			.setDir_act_es(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFESCALERA4"))
			.setDir_act_piso(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPISO4"))
			.setDir_act_p(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPUERTA4"))
			.setDir_act_CP(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFPOSTAL4"))
			.setDir_act_num_muni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFMUNICIPIO4"))
			.setDir_act_nom_muni(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFLOCALIDAD4"))
			.setDir_act_tlf(HtmlUnitToolkit.getTrimmedById(htmlPage, "SDFNUM9TELEFONO4"));
			
			return seb1.build();
		}
		
	}
	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, ParseException {
		try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			System.out.println(getSituacionEmpresa(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062"));
		}
		
		
		
	}
	
}
