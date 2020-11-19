package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDateException;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.ITPart;
import solutions.aon.seg.social.objects.It;
import solutions.aon.seg.social.objects.ItPartId;
import solutions.aon.seg.social.objects.ITPart.ITPartBuilder;
import solutions.aon.seg.social.objects.It.ItBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaREDITParts {
		
	
	//GET ITs
	public static Collection<It> getIts(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Date from, Date to)throws SegSocialException{
		
		Toolkit.verifyData(new Object[] {regime,ccc,from,to});
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		ArrayList<It> its = new ArrayList<>();
		ArrayList<ITPart>  itParts = (ArrayList<ITPart>) getFullItParts(certificateInputStream, certificatePassword, certificateType, regime, ccc, from, to);
		HashMap<ItPartId, Collection<ITPart>> orderedItParts = new HashMap<>();
		
		for (ITPart itp : itParts) {
			ItPartId id = new ItPartId(itp.getWorkLeaveDate(), itp.getNaf());
			if(orderedItParts.containsKey(id)) orderedItParts.get(id).add(itp);
			else {
				ArrayList<ITPart> list = new ArrayList<>();
				list.add(itp);
				orderedItParts.put(id, list);
			}
		}
		
		ItBuilder builder = new ItBuilder();
		Set<ItPartId> partIds = orderedItParts.keySet(); 
		for(ItPartId id :partIds) {
			itParts  = (ArrayList<ITPart>) orderedItParts.get(id);
			
			ITPart end = null;
			ITPart start = null;
			ArrayList<ITPart> confirmations = new ArrayList<>();
			
			for(ITPart itp : itParts) {
				if(itp.getPartType().toLowerCase().equals("alta")) end = itp;
				if(itp.getPartType().toLowerCase().equals("baja")) start = itp;
				if(itp.getPartType().toLowerCase().equals("confirmaci�n") && !confirmations.contains(itp)) confirmations.add(itp);
			}
			
			its.add(builder.setStart(start)
			.setConfirmations(confirmations)
			.setEnd(end)
			.build());
		}
		
		return its;
	}
	

	//HANDLE GETFULLITPARTS EXCEPTIONS
	public static Collection<ITPart> getFullItParts(final InputStream certificateInputStream, final String certificatePassword,
	final String certificateType, String regime, String ccc, Date from, Date to) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try { return getFullItPartsImpl(certificateInputStream, certificatePassword,	certificateType, regime, ccc, from, to); } 
		catch (FailingHttpStatusCodeException e) { 
			switch (e.getStatusCode()) {
				case 403: throw new ForbiddenException();
				default: throw new StatusCodeException(); 
			}
		}
		catch (MalformedURLException e) { throw new SegSocialException(e); }
		catch (IOException e) { throw new SegSocialException(e); }
		catch (InterruptedException e) {throw new SegSocialException(e);}
		
	}

	//GET ALL THE ITPARTS
	private static Collection<ITPart> getFullItPartsImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date from, Date to) throws FailingHttpStatusCodeException, IOException, InterruptedException, InvalidCertificateException, InvalidDataException {
		
		Toolkit.verifyData(new Object[] {regime,ccc,from,to});
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			
			if(Toolkit.isFuture(to)) throw new InvalidDateException();
			
			ArrayList<ITPart> itParts = new ArrayList<>();
			boolean last = false;
			
			while(!last) {
				HtmlPage origen = webClient.getPage("https://w2.seg-social.es/GetAccess/ResourceList");
				HtmlPage document = HtmlUnitToolkit.wait4(origen, p -> p.getAnchorByHref("https://w2.seg-social.es/isincaA/inicio.do")).orElseThrow().click();
				document = document.getAnchorByHref("/isincaA/menu.do?opcion=C").click();
				
				HtmlForm formularioPartes = document.getFormByName("BuscaPartesForm");
				formularioPartes.getInputByName("regimen").setValueAttribute(regime);
				document.getElementById("ccc1").setAttribute("value", ccc.substring(0,2));
				formularioPartes.getInputByName("ccc2").setValueAttribute(ccc.substring(2));
				
				Integer[] fromArray = Toolkit.getDateArray(from);
				Integer[] toArray = Toolkit.getDateArray(to);
				
				formularioPartes.getInputByName("fechaDesde_dd").setValueAttribute(fromArray[0].toString());
				formularioPartes.getInputByName("fechaDesde_mm").setValueAttribute(fromArray[1].toString());
				formularioPartes.getInputByName("fechaDesde_aa").setValueAttribute(fromArray[2].toString());
				
				formularioPartes.getInputByName("fechaHasta_dd").setValueAttribute(toArray[0].toString());
				formularioPartes.getInputByName("fechaHasta_mm").setValueAttribute(toArray[1].toString());
				formularioPartes.getInputByName("fechaHasta_aa").setValueAttribute(toArray[2].toString());
				
				HtmlInput show = (HtmlInput) formularioPartes.querySelectorAll("input[type=submit]").get(0);
				document = show.click();
				handleItPartErrors(document);
	
				ITPartBuilder builder = new ITPartBuilder();
				String format = "dd/MM/yyyy";
					
				DomNodeList<DomNode> rows = document.querySelectorAll(".resultados>tbody>tr");
				for (DomNode row : rows) {
					ArrayList<String> data = new ArrayList<>();
					Iterable<DomNode> cells = row.getChildren();	
						
					for(DomNode cell : cells) 				
						data.add(cell.getVisibleText().trim());
					
					String recDateStr = data.get(3);
					String naf = data.get(5);
					String workLeaveStr = data.get(7);
					String workRestartStr = data.get(9);
					String partDate = data.get(11);
					Integer partNum = null;
					
					try {partNum = Integer.parseInt(data.get(13).trim());}
					catch (NumberFormatException e) {}
					String partType = data.get(15);
					Boolean cancelled = Toolkit.toBoolean(data.get(17));
					Boolean wrong = Toolkit.toBoolean(data.get(19));
					
					ITPart part = builder.setReceptionDate(Toolkit.parseDate(recDateStr, format))
					.setNaf(naf)
					.setWorkLeaveDate(Toolkit.parseDate(workLeaveStr, format))
					.setWorkRestartDate(Toolkit.parseDate(workRestartStr, format))
					.setPartDate(Toolkit.parseDate(partDate, format))
					.setPartNum(partNum)
					.setPartType(partType)
					.setCanceled(cancelled)
					.setWrong(wrong)
					.build();
					
					if(itParts.contains(part)) last = true;
					else itParts.add(part);
				}
				to = itParts.get(itParts.size()-1).getReceptionDate();
			}
			return itParts;
			
		}		
	}

	//HANDLE IT PART ERRORS
	private static void handleItPartErrors(HtmlPage htmlPage) throws InvalidDataException {
		DomNode errors = htmlPage.querySelector("#errores");
		if(errors != null) throw new InvalidDataException();
		
	}	

	//CONTINGENCIES 
	public enum Contingencies{
		ENFERMEDAD_COMUN,
		ACCIDENTE_NO_LABORAL,
		ACCIDENT_LABORAL,
		ENFERMEDAD_PROFESIONAL,
		PERIODOS_OBSERVACION
	}
	
	//PART TYPE 
	public enum PartType{
		ALTA,
		CONFIRMACION,
		BAJA
	}
	
	//CONTRACTS
	public enum ContractType{
		FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL,
		RESTO_Y_AUTONOMOS
	}
	
	//REGISTER IT START HANDLE EXCEPTIONS
	public static void addItStart(InputStream certificateInputStream, String certificatePassword,String certificateType,
			String regime, String ccc, String naf, Contingencies contingency, String licenseNumber, String cias, Date startdate, 
			ContractType contractType,  float baseCot , int cotDays) throws StatusCodeException, InvalidCertificateException, IOException, InvalidDataException {
		
		Toolkit.verifyData(new Object[]{regime, ccc, naf, contingency, licenseNumber, cias, startdate, contractType,baseCot,cotDays});
		
		try{ addItStartImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency,licenseNumber, cias, startdate, contractType, baseCot, cotDays);}
		catch(FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);}
	}
	
	//REGISTER IT START
	private static void addItStartImpl(InputStream certificateInputStream, String certificatePassword,String certificateType, String regime, String ccc,
			String naf, Contingencies contingency, String licenseNumber, String cias, Date startdate, ContractType contractType, float base_cot , int cotDays) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException, InvalidDataException {
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			
			HtmlPage document = webClient.getPage("https://w2.seg-social.es/isincaA/inicio.do");
			HtmlOption type = document.querySelector("#tipoParte option:nth-child(2)");
			document = fillCommonData(type.click(), regime, ccc, naf, contingency, PartType.BAJA);
			
			Integer[] arr_startDate = Toolkit.getDateArray(startdate);		
			ArrayList<String> n_coleg_arr_ls = Toolkit.splitString_m(licenseNumber, new int[]{2,4});
			String[] arr_base_cot = Toolkit.splitDecimal(base_cot,2);
			
			HtmlInput n_coleg_1_in = document.querySelector("#ncol_0");
			HtmlInput n_coleg_2_in = document.querySelector("#ncol_1");
			HtmlInput n_coleg_3_in = document.querySelector("#ncol_2");
			HtmlInput cias_in = document.querySelector("#cias");
			HtmlInput startDate_dd_in = document.querySelector("#fechaBaja_dd");			
			HtmlInput startDate_mm_in = document.querySelector("#fechaBaja_mm");			
			HtmlInput startDate_aa_in = document.querySelector("#fechaBaja_aa");	

						
			HtmlOption contract_type_opt = null;
			HtmlInput cot_base_in_1 = null;
			HtmlInput cot_base_in_2 = null;
			HtmlInput cot_days_in = null;
			
			switch (contractType) {
				case FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL: 	
					contract_type_opt = document.querySelector("#tipoContrato option:nth-child(2)");	
					cot_base_in_1 = document.querySelector("#sumaBC1");
					cot_base_in_2 = document.querySelector("#sumaBC2");
					cot_days_in = document.querySelector("#sumaDias");
					break;
				case RESTO_Y_AUTONOMOS:						
					contract_type_opt = document.querySelector("#tipoContrato option:nth-child(3)");	
					cot_base_in_1 = document.querySelector("#baseCotizacion1");
					cot_base_in_2 = document.querySelector("#baseCotizacion2");
					cot_days_in = document.querySelector("#diasCot");
					break;
			}
						
			n_coleg_1_in.setValueAttribute(n_coleg_arr_ls.get(0));
			n_coleg_2_in.setValueAttribute(n_coleg_arr_ls.get(1));
			n_coleg_3_in.setValueAttribute(n_coleg_arr_ls.get(2));
			cias_in.setValueAttribute(cias);
			startDate_dd_in.setValueAttribute(arr_startDate[0] + "");
			startDate_mm_in.setValueAttribute(arr_startDate[1] + "");
			startDate_aa_in.setValueAttribute(arr_startDate[2] + "");
			cot_base_in_1.setValueAttribute(arr_base_cot[0]);
			cot_base_in_2.setValueAttribute(arr_base_cot[1]);
			cot_days_in.setValueAttribute(cotDays + "");
			
			document = contract_type_opt.click();
			
			HtmlSubmitInput validate = document.querySelector("#Validar");
			document = validate.click();
			
			HtmlUnitToolkit.showAsXML(new HtmlElement[]{cot_base_in_1,cot_base_in_2,cot_days_in});
			Toolkit.buildFile(document.getWebResponse().getContentAsStream().readAllBytes(),"coso.txt");
			
		} 
	}
	//REGISTER IT CONFIRMATION HANDLE EXCEPTIONS
	public static void addItConfirmation(InputStream certificateInputStream, String certificatePassword,String certificateType,
								  String regime, String ccc, String naf, Contingencies contingency) throws StatusCodeException, InvalidCertificateException, IOException, InvalidDataException {

		Toolkit.verifyData(new Object[]{regime, ccc, naf, contingency});
		try{ addItConfirmationImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency);}
		catch(FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);}
	}

	//REGISTER IT CONFIRMATION
	private static void addItConfirmationImpl(InputStream certificateInputStream, String certificatePassword,String certificateType, String regime, String ccc, String naf, Contingencies contingency) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException, InvalidDataException {
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage document = webClient.getPage("https://w2.seg-social.es/isincaA/inicio.do");
			HtmlOption type = document.querySelector("#tipoParte option:nth-child(3)");
			document = fillCommonData(type.click(), regime, ccc, naf, contingency, PartType.CONFIRMACION);
		}
	}

	//REGISTER IT END HANDLE EXCEPTIONS
	public static void addItEnd(InputStream certificateInputStream, String certificatePassword,String certificateType, String regime, String ccc, String naf, Contingencies contingency) throws StatusCodeException, InvalidCertificateException, IOException, InvalidDataException {

		Toolkit.verifyData(new Object[]{regime, ccc, naf, contingency});
		try{ addItEndImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency);}
		catch(FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);}
	}

	//REGISTER IT
	private static void addItEndImpl(InputStream certificateInputStream, String certificatePassword,String certificateType, String regime, String ccc, String naf, Contingencies contingency) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException, InvalidDataException {
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage document = webClient.getPage("https://w2.seg-social.es/isincaA/inicio.do");
			HtmlOption type = document.querySelector("#tipoParte option:nth-child(3)");
			document = fillCommonData(type.click(), regime, ccc, naf, contingency, PartType.ALTA);
		}
	}

	//COMMON DATA FILLING
	private static HtmlPage fillCommonData(HtmlPage document,String regime, String ccc, String naf, Contingencies contingency,PartType type) throws IOException, InvalidDataException {
		HtmlInput regime_in = document.querySelector("#regimen");
		HtmlInput ccc_1_in = document.querySelector("#ccc1");
		HtmlInput ccc_2_in = document.querySelector("#ccc2");
		HtmlInput naf_1_in = document.querySelector("#naf1");
		HtmlInput naf_2_in = document.querySelector("#naf2");
		HtmlOption contingency_opt = null;
		HtmlSubmitInput accept = (HtmlSubmitInput) document.querySelector("#situacionTrabajador").getNextSibling().getNextSibling();
		
		switch(contingency) {
			case ENFERMEDAD_COMUN: 			contingency_opt = document.querySelector("#contingencia option:nth-child(2)");	break;
			case ACCIDENTE_NO_LABORAL: 		contingency_opt = document.querySelector("#contingencia option:nth-child(3)");	break;
			case ACCIDENT_LABORAL: 			contingency_opt = document.querySelector("#contingencia option:nth-child(4)"); 	break;
			case ENFERMEDAD_PROFESIONAL: 	contingency_opt = document.querySelector("#contingencia option:nth-child(5)"); 	break;
			case PERIODOS_OBSERVACION: 		contingency_opt = document.querySelector("#contingencia option:nth-child(6)"); 	break;
		}
		
		contingency_opt.click();
		
		String[] ccc_arr =  Toolkit.SplitString(ccc, 2);
		String[] naf_arr =  Toolkit.SplitString(naf, 2);
		
		regime_in.setValueAttribute(regime);
		ccc_1_in.setValueAttribute(ccc_arr[0]);
		ccc_2_in.setValueAttribute(ccc_arr[1]);
		
		naf_1_in.setValueAttribute(naf_arr[0]);
		naf_2_in.setValueAttribute(naf_arr[1]);
		
		document = accept.click();		
		return document;
	}
	
	//Cancel IT
	public static void removeItStart(InputStream certificateInputStream, String certificatePassword,String certificateType,String regime, String ccc, String naf, Date startdate)
		throws StatusCodeException, InvalidCertificateException, IOException, InvalidDataException {
		Toolkit.verifyData(new Object[]{regime, ccc, naf, startdate});
		try{ removeItStartImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, startdate);}
		catch(FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);}
	}



	//remove it
	private static void removeItStartImpl(InputStream certificateInputStream, String certificatePassword,String certificateType, String regime, String ccc, String naf, Date startdate)
			throws InvalidCertificateException, FailingHttpStatusCodeException, IOException {
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/isincaA/menu.do?opcion=A");

			HtmlInput regime_in = htmlPage.querySelector("#regimen");
			HtmlInput ccc_in_1 = htmlPage.querySelector("#cc1");
			HtmlInput ccc_in_2 = htmlPage.querySelector("#cc2");
			HtmlInput naf_in_1 = htmlPage.querySelector("#naf2");
			HtmlInput naf_in_2 = htmlPage.querySelector("#naf2");
			HtmlInput date_in_dd = htmlPage.querySelector("#fechaBaja_dd");
			HtmlInput date_in_mm = htmlPage.querySelector("#fechaBaja_mm");
			HtmlInput date_in_aa = htmlPage.querySelector("#fechaBaja_aa");
			HtmlInput continue_in = htmlPage.querySelector("#botonesANULAR input[name=boton]");



		}
	}
}
